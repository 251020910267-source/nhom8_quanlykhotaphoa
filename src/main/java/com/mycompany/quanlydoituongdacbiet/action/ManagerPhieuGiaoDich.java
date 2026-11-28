package com.mycompany.quanlydoituongdacbiet.action;

import com.mycompany.quanlydoituongdacbiet.entity.LoHang;
import com.mycompany.quanlydoituongdacbiet.entity.NhaCungCap;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuGiaoDich;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuGiaoDichXML;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuHuy;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuNhap;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuXuat;
import com.mycompany.quanlydoituongdacbiet.utils.FileUtils;
import com.mycompany.quanlydoituongdacbiet.utils.FormatUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Nghiệp vụ kho thực tế: NHẬP – XUẤT – HUỶ theo LÔ HÀNG và MÃ ĐƠN HÀNG / SỐ BILL.
 *
 * Quy tắc nghiệp vụ cốt lõi:
 * 1. Mỗi lần nhập kho tạo ra 1 Lô hàng riêng (LoHang) lưu HSD riêng, giá nhập riêng, NCC riêng.
 * 2. Cùng 1 mặt hàng nhập từ 2 Nhà cung cấp khác nhau:
 *    - Dùng chung Mã mặt hàng (maHang), nhưng BẮT BUỘC khác Mã đơn hàng (maDonHang) và khác Mã lô (LOxxxxx).
 *    - Nếu cố gắn chung 1 Mã đơn nhập cho 2 Nhà cung cấp khác nhau, hệ thống sẽ từ chối.
 * 3. Xuất bán: chỉ cho phép xuất từ lô còn hạn (ưu tiên FEFO - hết hạn trước xuất trước).
 * 4. Hư hỏng (chuột cắn, thời tiết ẩm mốc, vỡ móp, quá hạn...): lập Phiếu Huỷ trừ trực tiếp vào lô đó
 *    và tự động cập nhật trạng thái + chi tiết hư hỏng của lô.
 */
public class ManagerPhieuGiaoDich implements IQuanLy<PhieuGiaoDich> {
    private static final String PHIEU_FILE_NAME = "phieugiaodich.xml";
    private List<PhieuGiaoDich> listPhieu;
    private final ManagerLoHang managerLo;

    public ManagerPhieuGiaoDich() {
        this.managerLo = new ManagerLoHang();
        this.listPhieu = readListPhieu();
        dongBoDuLieuCu();
    }

    public ManagerLoHang getManagerLoHang() {
        return managerLo;
    }

    public void taiLai() {
        managerLo.taiLai();
        listPhieu = readListPhieu();
    }

    public void writeListPhieu(List<PhieuGiaoDich> list) {
        PhieuGiaoDichXML xml = new PhieuGiaoDichXML();
        xml.setPhieuGiaoDich(list);
        FileUtils.writeXMLtoFile(PHIEU_FILE_NAME, xml);
    }

    public final List<PhieuGiaoDich> readListPhieu() {
        List<PhieuGiaoDich> list = new ArrayList<>();
        File file = new File(PHIEU_FILE_NAME);
        if (!file.exists()) {
            writeListPhieu(list);
            return list;
        }
        PhieuGiaoDichXML xml = (PhieuGiaoDichXML) FileUtils.readXMLFile(PHIEU_FILE_NAME, PhieuGiaoDichXML.class);
        if (xml != null && xml.getPhieuGiaoDich() != null) {
            list = xml.getPhieuGiaoDich();
        }
        return list;
    }

    private void luu() {
        writeListPhieu(listPhieu);
        managerLo.luu();
    }

    private int getNextId() {
        int maxId = 0;
        for (PhieuGiaoDich p : listPhieu) {
            maxId = Math.max(maxId, p.getId());
        }
        return maxId + 1;
    }

    /** Sinh mã đơn / số bill tiếp theo theo tiền tố: DHN0001, HDX0001, BBH0001. */
    public String sinhMaDonTiepTheo(String tienTo) {
        int maxSo = 0;
        String prefix = tienTo.toUpperCase();
        for (PhieuGiaoDich p : listPhieu) {
            String ma = p.getMaDonHangHienThi().toUpperCase();
            if (ma.startsWith(prefix)) {
                String phanSo = ma.substring(prefix.length()).replaceAll("[^0-9]", "");
                if (!phanSo.isEmpty()) {
                    try {
                        maxSo = Math.max(maxSo, Integer.parseInt(phanSo));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return String.format("%s%04d", prefix, maxSo + 1);
    }

    /** Lấy toàn bộ các dòng hàng thuộc cùng một Mã đơn / Số Bill để in chung 1 chứng từ. */
    public List<PhieuGiaoDich> layDanhSachTheoMaDon(String maDonHang) {
        List<PhieuGiaoDich> kq = new ArrayList<>();
        if (maDonHang == null || maDonHang.trim().isEmpty()) {
            return kq;
        }
        String key = maDonHang.trim();
        for (PhieuGiaoDich p : listPhieu) {
            if (p.getMaDonHangHienThi().equalsIgnoreCase(key)) {
                kq.add(p);
            }
        }
        return kq;
    }

    /** Kiểm tra tính hợp lệ của Mã đơn hàng khi gộp nhiều dòng vào chung 1 đơn / bill. */
    private String kiemTraMaDonHang(PhieuGiaoDich phieuMoi) {
        String maDon = phieuMoi.getMaDonHang();
        if (maDon == null || maDon.trim().isEmpty()) {
            maDon = sinhMaDonTiepTheo(phieuMoi.getTienToDonHang());
            phieuMoi.setMaDonHang(maDon);
            return null;
        }
        maDon = maDon.trim().toUpperCase();
        phieuMoi.setMaDonHang(maDon);

        for (PhieuGiaoDich pCu : listPhieu) {
            if (!pCu.getMaDonHangHienThi().equalsIgnoreCase(maDon)) {
                continue;
            }
            if (!pCu.getClass().equals(phieuMoi.getClass())) {
                return "Mã chứng từ '" + maDon + "' đã được dùng cho " + pCu.getLoaiPhieu()
                        + ", không thể dùng chung cho " + phieuMoi.getLoaiPhieu() + "!";
            }
            if (phieuMoi instanceof PhieuNhap) {
                PhieuNhap pnMoi = (PhieuNhap) phieuMoi;
                PhieuNhap pnCu = (PhieuNhap) pCu;
                boolean khacNCC = (pnCu.getIdNhaCungCap() > 0 && pnMoi.getIdNhaCungCap() > 0
                        && pnCu.getIdNhaCungCap() != pnMoi.getIdNhaCungCap())
                        || (pnCu.getTenNhaCungCap() != null
                        && !pnCu.getTenNhaCungCap().equalsIgnoreCase(pnMoi.getTenNhaCungCap()));
                if (khacNCC) {
                    return "Mã đơn nhập '" + maDon + "' thuộc về Nhà cung cấp '" + pnCu.getTenNhaCungCap() + "'.\n"
                            + "Cùng 1 mặt hàng hoặc đơn hàng từ Nhà cung cấp khác bắt buộc phải có Mã đơn hàng khác!";
                }
            } else if (phieuMoi instanceof PhieuXuat) {
                PhieuXuat pxMoi = (PhieuXuat) phieuMoi;
                PhieuXuat pxCu = (PhieuXuat) pCu;
                if (pxCu.getTenKhachHang() != null
                        && !pxCu.getTenKhachHang().trim().equalsIgnoreCase(pxMoi.getTenKhachHang().trim())) {
                    return "Số hoá đơn '" + maDon + "' đang lập cho khách hàng '" + pxCu.getTenKhachHang() + "'.\n"
                            + "Khách hàng khác vui lòng bấm 'Đơn mới' để tách hoá đơn riêng!";
                }
            }
        }
        return null;
    }

    // =====================================================================
    //                              NHẬP KHO
    // =====================================================================
    public String nhapKho(PhieuNhap p) {
        String loi = kiemTraChung(p);
        if (loi != null) {
            return loi;
        }
        if (p.getDonGia() <= 0) {
            return "Giá nhập phải lớn hơn 0.";
        }
        if (p.getTenNhaCungCap() == null || p.getTenNhaCungCap().trim().isEmpty()) {
            return "Vui lòng chọn nhà cung cấp.";
        }
        Date hsd = p.getHanSuDung();
        if (hsd != null) {
            if (!FormatUtils.isNamHopLe(hsd)) {
                return "Hạn sử dụng không hợp lệ (năm phải nằm trong khoảng 2000 - 2100).";
            }
            if (!hsd.after(batDauNgay(p.getNgayGiaoDich()))) {
                return "Hạn sử dụng phải SAU ngày nhập kho. Không nhận nhập lô hàng đã hết hạn!";
            }
        }
        loi = kiemTraMaDonHang(p);
        if (loi != null) {
            return loi;
        }

        p.setId(getNextId());
        LoHang lo = new LoHang(p.getMaHangHoa(), p.getIdNhaCungCap(), p.getTenNhaCungCap().trim(),
                p.getId(), p.getMaDonHangHienThi(), p.getNgayGiaoDich(), hsd,
                p.getSoLuong(), Math.round(p.getDonGia()));
        managerLo.add(lo);
        p.setIdLoHang(lo.getId());
        listPhieu.add(p);
        luu();
        return null;
    }

    // =====================================================================
    //                              XUẤT KHO
    // =====================================================================
    public String xuatKho(PhieuXuat p) {
        String loi = kiemTraChung(p);
        if (loi != null) {
            return loi;
        }
        if (p.getDonGia() <= 0) {
            return "Đơn giá xuất bán phải lớn hơn 0.";
        }
        if (p.getTenKhachHang() == null || p.getTenKhachHang().trim().isEmpty()) {
            return "Vui lòng nhập tên khách hàng.";
        }
        LoHang lo = managerLo.findById(p.getIdLoHang());
        loi = kiemTraLo(lo, p);
        if (loi != null) {
            return loi;
        }
        if (lo.isHetHan()) {
            return "Lô " + lo.getMaLo() + " đã HẾT HẠN (" + FormatUtils.formatDate(lo.getHanSuDung())
                    + "), tuyệt đối không được xuất bán!\nVui lòng lập Phiếu Huỷ cho lô này.";
        }
        loi = kiemTraMaDonHang(p);
        if (loi != null) {
            return loi;
        }

        p.setTenKhachHang(p.getTenKhachHang().trim());
        lo.setSoLuongDaXuat(lo.getSoLuongDaXuat() + p.getSoLuong());
        p.setId(getNextId());
        listPhieu.add(p);
        luu();
        return null;
    }

    // =====================================================================
    //                       HUỶ HÀNG HƯ HỎNG / HẾT HẠN
    // =====================================================================
    public String baoHuHong(PhieuHuy p) {
        LoHang lo = managerLo.findById(p.getIdLoHang());
        if (lo == null) {
            return "Vui lòng chọn lô hàng cần báo hư hỏng / huỷ.";
        }
        p.setMaHangHoa(lo.getIdHangHoa());
        p.setDonGia(lo.getGiaNhap());

        String loi = kiemTraChung(p);
        if (loi != null) {
            return loi;
        }
        if (p.getLyDo() == null) {
            return "Vui lòng chọn nguyên nhân hư hỏng / xuất huỷ.";
        }
        loi = kiemTraLo(lo, p);
        if (loi != null) {
            return loi;
        }
        loi = kiemTraMaDonHang(p);
        if (loi != null) {
            return loi;
        }

        lo.setSoLuongHuHong(lo.getSoLuongHuHong() + p.getSoLuong());
        p.setId(getNextId());
        listPhieu.add(p);
        capNhatChiTietHuHongCuaLo(lo);
        luu();
        return null;
    }

    /** Tổng hợp lại chuỗi nguyên nhân hư hỏng của lô từ tất cả các Phiếu Huỷ hiện có. */
    private void capNhatChiTietHuHongCuaLo(LoHang lo) {
        if (lo == null) {
            return;
        }
        Map<String, Integer> thongKeLyDo = new LinkedHashMap<>();
        for (PhieuGiaoDich p : listPhieu) {
            if (p instanceof PhieuHuy && p.getIdLoHang() == lo.getId()) {
                PhieuHuy ph = (PhieuHuy) p;
                String tenLyDo = ph.getLyDo() != null ? ph.getLyDo().getTenHienThi() : "Hư hỏng";
                thongKeLyDo.merge(tenLyDo, ph.getSoLuong(), Integer::sum);
            }
        }
        if (thongKeLyDo.isEmpty()) {
            lo.setChiTietHuHong("");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Integer> e : thongKeLyDo.entrySet()) {
                if (sb.length() > 0) {
                    sb.append("; ");
                }
                sb.append(e.getKey()).append(" (").append(e.getValue()).append(")");
            }
            lo.setChiTietHuHong(sb.toString());
        }
    }

    private String kiemTraChung(PhieuGiaoDich p) {
        if (p.getMaHangHoa() <= 0) {
            return "Vui lòng chọn mặt hàng.";
        }
        if (p.getNgayGiaoDich() == null || !FormatUtils.isNamHopLe(p.getNgayGiaoDich())) {
            return "Ngày giao dịch không hợp lệ (năm phải từ 2000 đến 2100).";
        }
        if (p.getNgayGiaoDich().after(cuoiNgayHomNay())) {
            return "Ngày giao dịch không được vượt quá ngày hiện tại.";
        }
        if (p.getSoLuong() <= 0) {
            return "Số lượng phải là số nguyên dương (> 0).";
        }
        return null;
    }

    private String kiemTraLo(LoHang lo, PhieuGiaoDich p) {
        if (lo == null) {
            return "Không tồn tại lô hàng được chọn.";
        }
        if (lo.getIdHangHoa() != p.getMaHangHoa()) {
            return "Lô " + lo.getMaLo() + " không thuộc mặt hàng đã chọn.";
        }
        if (p.getSoLuong() > lo.getSoLuongCon()) {
            return "Số lượng yêu cầu (" + p.getSoLuong() + ") vượt quá số lượng còn tồn của lô "
                    + lo.getMaLo() + " (" + lo.getSoLuongCon() + ").";
        }
        if (lo.getNgayNhap() != null && p.getNgayGiaoDich().before(batDauNgay(lo.getNgayNhap()))) {
            return "Ngày lập phiếu không được trước ngày nhập lô (" + FormatUtils.formatDate(lo.getNgayNhap()) + ").";
        }
        return null;
    }

    // =====================================================================
    //                              XOÁ PHIẾU
    // =====================================================================
    public String xoaPhieu(int id) {
        PhieuGiaoDich p = findById(id);
        if (p == null) {
            return "Không tìm thấy phiếu cần xoá.";
        }
        LoHang lo = managerLo.findById(p.getIdLoHang());
        if (p instanceof PhieuNhap) {
            if (lo != null && (lo.getSoLuongDaXuat() > 0 || lo.getSoLuongHuHong() > 0)) {
                return "Không thể xoá " + p.getMaPhieu() + " vì lô " + lo.getMaLo()
                        + " đã phát sinh giao dịch xuất kho hoặc báo hư hỏng!\n"
                        + "Hãy xoá các phiếu xuất/huỷ của lô này trước.";
            }
            if (lo != null) {
                managerLo.getList().remove(lo);
            }
            listPhieu.remove(p);
        } else if (p instanceof PhieuXuat) {
            if (lo != null) {
                lo.setSoLuongDaXuat(Math.max(0, lo.getSoLuongDaXuat() - p.getSoLuong()));
            }
            listPhieu.remove(p);
        } else if (p instanceof PhieuHuy) {
            listPhieu.remove(p);
            if (lo != null) {
                lo.setSoLuongHuHong(Math.max(0, lo.getSoLuongHuHong() - p.getSoLuong()));
                capNhatChiTietHuHongCuaLo(lo);
            }
        }
        luu();
        return null;
    }

    // =====================================================================
    //                              IQuanLy
    // =====================================================================
    @Override
    public void add(PhieuGiaoDich obj) {
        String loi;
        if (obj instanceof PhieuNhap) {
            loi = nhapKho((PhieuNhap) obj);
        } else if (obj instanceof PhieuXuat) {
            loi = xuatKho((PhieuXuat) obj);
        } else {
            loi = baoHuHong((PhieuHuy) obj);
        }
        if (loi != null) {
            throw new IllegalArgumentException(loi);
        }
    }

    @Override
    public void edit(PhieuGiaoDich obj) {
        PhieuGiaoDich cu = findById(obj.getId());
        if (cu != null) {
            cu.setGhiChu(obj.getGhiChu());
            luu();
        }
    }

    @Override
    public boolean delete(PhieuGiaoDich obj) {
        return obj != null && xoaPhieu(obj.getId()) == null;
    }

    @Override
    public List<PhieuGiaoDich> getList() {
        return listPhieu;
    }

    public PhieuGiaoDich findById(int id) {
        for (PhieuGiaoDich p : listPhieu) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public int tinhTonKho(int maHangHoa) {
        return managerLo.tinhTonKho(maHangHoa);
    }

    // =====================================================================
    //  Tự động đồng bộ dữ liệu XML cũ (nếu mở từ bản cũ chưa có lô/mã đơn)
    // =====================================================================
    private void dongBoDuLieuCu() {
        boolean thayDoi = false;
        List<PhieuGiaoDich> theoThuTu = new ArrayList<>(listPhieu);
        theoThuTu.sort(Comparator.comparingInt(PhieuGiaoDich::getId));
        List<NhaCungCap> dsNCC = null;

        for (PhieuGiaoDich p : theoThuTu) {
            if (p.getMaDonHang() == null || p.getMaDonHang().trim().isEmpty()) {
                p.setMaDonHang(String.format("%s%04d", p.getTienToDonHang(), p.getId()));
                thayDoi = true;
            }
            if (p instanceof PhieuNhap && managerLo.findById(p.getIdLoHang()) == null) {
                PhieuNhap pn = (PhieuNhap) p;
                if (pn.getIdNhaCungCap() == 0 && pn.getTenNhaCungCap() != null) {
                    if (dsNCC == null) {
                        dsNCC = new ManagerNhaCungCap().getListNCC();
                    }
                    for (NhaCungCap ncc : dsNCC) {
                        if (pn.getTenNhaCungCap().equalsIgnoreCase(ncc.getTenNCC())) {
                            pn.setIdNhaCungCap(ncc.getId());
                        }
                    }
                }
                LoHang lo = new LoHang(pn.getMaHangHoa(), pn.getIdNhaCungCap(), pn.getTenNhaCungCap(),
                        pn.getId(), pn.getMaDonHangHienThi(), pn.getNgayGiaoDich(),
                        pn.getHanSuDung(), pn.getSoLuong(), Math.round(pn.getDonGia()));
                lo.setId(managerLo.getList().stream().mapToInt(LoHang::getId).max().orElse(0) + 1);
                managerLo.getList().add(lo);
                pn.setIdLoHang(lo.getId());
                thayDoi = true;
            }
        }
        for (PhieuGiaoDich p : theoThuTu) {
            if (!(p instanceof PhieuNhap) && managerLo.findById(p.getIdLoHang()) == null) {
                for (LoHang lo : managerLo.getLoConHang(p.getMaHangHoa())) {
                    if (lo.getSoLuongCon() >= p.getSoLuong()) {
                        if (p instanceof PhieuXuat) {
                            lo.setSoLuongDaXuat(lo.getSoLuongDaXuat() + p.getSoLuong());
                        } else if (p instanceof PhieuHuy) {
                            lo.setSoLuongHuHong(lo.getSoLuongHuHong() + p.getSoLuong());
                        }
                        p.setIdLoHang(lo.getId());
                        capNhatChiTietHuHongCuaLo(lo);
                        thayDoi = true;
                        break;
                    }
                }
            }
        }
        if (thayDoi) {
            luu();
        }
    }

    private static Date cuoiNgayHomNay() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return c.getTime();
    }

    private static Date batDauNgay(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
}
