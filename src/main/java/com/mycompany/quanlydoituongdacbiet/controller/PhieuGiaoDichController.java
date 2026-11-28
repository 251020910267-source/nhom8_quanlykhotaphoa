package com.mycompany.quanlydoituongdacbiet.controller;

import com.mycompany.quanlydoituongdacbiet.action.ManagerHangHoa;
import com.mycompany.quanlydoituongdacbiet.action.ManagerLoHang;
import com.mycompany.quanlydoituongdacbiet.action.ManagerNhaCungCap;
import com.mycompany.quanlydoituongdacbiet.action.ManagerPhieuGiaoDich;
import com.mycompany.quanlydoituongdacbiet.entity.HangHoa;
import com.mycompany.quanlydoituongdacbiet.entity.LoHang;
import com.mycompany.quanlydoituongdacbiet.entity.LyDoHuHong;
import com.mycompany.quanlydoituongdacbiet.entity.NhaCungCap;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuGiaoDich;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuHuy;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuNhap;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuXuat;
import com.mycompany.quanlydoituongdacbiet.utils.FormatUtils;
import com.mycompany.quanlydoituongdacbiet.view.HoaDonDialog;
import java.awt.Window;
import java.util.Date;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Controller điều phối nghiệp vụ Nhập kho / Xuất bán / Báo hư hỏng (Huỷ) và Xuất Bill.
 */
public class PhieuGiaoDichController {
    private final ManagerPhieuGiaoDich managerPhieu;
    private final ManagerHangHoa managerHangHoa;
    private final ManagerNhaCungCap managerNCC;

    public PhieuGiaoDichController() {
        this.managerPhieu = new ManagerPhieuGiaoDich();
        this.managerHangHoa = new ManagerHangHoa();
        this.managerNCC = new ManagerNhaCungCap();
    }

    public void taiLaiTatCa() {
        managerPhieu.taiLai();
        managerHangHoa.taiLai();
        managerNCC.taiLai();
    }

    public String sinhMaDonMoi(String loaiPhieu) {
        if ("Phiếu Nhập".equals(loaiPhieu)) {
            return managerPhieu.sinhMaDonTiepTheo("DHN");
        }
        if ("Phiếu Xuất".equals(loaiPhieu)) {
            return managerPhieu.sinhMaDonTiepTheo("HDX");
        }
        return managerPhieu.sinhMaDonTiepTheo("BBH");
    }

    public void loadComboBox(JComboBox<String> cboHangHoa, JComboBox<String> cboNhaCungCap) {
        cboHangHoa.removeAllItems();
        for (HangHoa hh : managerHangHoa.getList()) {
            cboHangHoa.addItem(hh.getId() + " - " + hh.getMaHang() + " - " + hh.getTenHang());
        }

        cboNhaCungCap.removeAllItems();
        for (NhaCungCap ncc : managerNCC.getListNCC()) {
            cboNhaCungCap.addItem(ncc.getId() + " - " + ncc.getTenNCC());
        }
    }

    /** Nạp các lô phù hợp vào ComboBox khi chọn mặt hàng ở chế độ Xuất (FEFO) hoặc Huỷ. */
    public void loadLoTheoHangHoa(int idHangHoa, boolean chiLoCoTheXuat, JComboBox<LoHang> cboLo) {
        cboLo.removeAllItems();
        if (idHangHoa <= 0) {
            return;
        }
        ManagerLoHang qlLo = managerPhieu.getManagerLoHang();
        List<LoHang> dsLo = chiLoCoTheXuat
                ? qlLo.getLoCoTheXuat(idHangHoa)
                : qlLo.getLoConHang(idHangHoa);
        for (LoHang lo : dsLo) {
            cboLo.addItem(lo);
        }
    }

    public HangHoa timHangHoa(int id) {
        return managerHangHoa.findById(id);
    }

    public int getTonKhaDung(int idHangHoa) {
        return managerPhieu.getManagerLoHang().tinhTonKhaDung(idHangHoa);
    }

    public int getTonThucTe(int idHangHoa) {
        return managerPhieu.tinhTonKho(idHangHoa);
    }

    public void showListPhieu(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        ManagerLoHang qlLo = managerPhieu.getManagerLoHang();

        for (PhieuGiaoDich p : managerPhieu.getList()) {
            HangHoa hh = managerHangHoa.findById(p.getMaHangHoa());
            String tenHang = hh != null ? (hh.getMaHang() + " - " + hh.getTenHang()) : ("HH#" + p.getMaHangHoa());
            LoHang lo = qlLo.findById(p.getIdLoHang());
            String thongTinLo;
            if (lo != null) {
                thongTinLo = lo.getMaLo() + (lo.getHanSuDung() != null
                        ? " (" + FormatUtils.formatDate(lo.getHanSuDung()) + ")" : "");
            } else {
                thongTinLo = p.getIdLoHang() > 0 ? String.format("LO%05d", p.getIdLoHang()) : "—";
            }

            model.addRow(new Object[]{
                p.getId(),
                p.getMaPhieu(),
                p.getMaDonHangHienThi(),
                p.getLoaiPhieu(),
                tenHang,
                thongTinLo,
                p.getDoiTac(),
                FormatUtils.formatDate(p.getNgayGiaoDich()),
                p.getSoLuong(),
                FormatUtils.formatMoney(Math.round(p.getDonGia())),
                FormatUtils.formatMoney(Math.round(p.tinhThanhTien()))
            });
        }
    }

    /** Lập phiếu Nhập / Xuất / Huỷ; trả về Mã đơn hàng nếu thành công, ngược lại trả null. */
    public String lapPhieu(Window parent, String loaiPhieu, String maDonHang,
            String itemHangHoa, LoHang loChon, String itemNCC, String khachHang,
            LyDoHuHong lyDo, Date ngayGD, Date hsdNhap, boolean khongHSD,
            String strSoLuong, String strDonGia, String ghiChu) {

        if (itemHangHoa == null || itemHangHoa.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Vui lòng chọn mặt hàng!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int idHangHoa = Integer.parseInt(itemHangHoa.split(" - ")[0].trim());

        int soLuong;
        try {
            soLuong = Integer.parseInt(strSoLuong.trim());
            if (soLuong <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(parent, "Số lượng phải là số nguyên dương (> 0)!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        if ("Phiếu Nhập".equals(loaiPhieu)) {
            if (itemNCC == null || itemNCC.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "Vui lòng chọn nhà cung cấp!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            if (!khongHSD && hsdNhap == null) {
                JOptionPane.showMessageDialog(parent,
                        "Vui lòng chọn Hạn sử dụng của lô hàng nhập (hoặc tick 'Hàng không có HSD')!",
                        "Thiếu hạn sử dụng lô", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            long giaNhap;
            try {
                giaNhap = FormatUtils.parseMoney(strDonGia);
                if (giaNhap <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parent, "Giá nhập của lô phải là số nguyên dương!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            String[] partsNCC = itemNCC.split(" - ", 2);
            int idNCC = Integer.parseInt(partsNCC[0].trim());
            String tenNCC = partsNCC.length > 1 ? partsNCC[1].trim() : itemNCC;

            PhieuNhap pn = new PhieuNhap(maDonHang, ngayGD, idHangHoa, soLuong, giaNhap,
                    idNCC, tenNCC, khongHSD ? null : hsdNhap, ghiChu);
            String loi = managerPhieu.nhapKho(pn);
            if (loi != null) {
                JOptionPane.showMessageDialog(parent, loi, "Không thể lập Phiếu Nhập", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            LoHang loMoi = managerPhieu.getManagerLoHang().findById(pn.getIdLoHang());
            JOptionPane.showMessageDialog(parent,
                    "Lập Phiếu Nhập thành công!\n"
                    + "• Mã đơn nhập: " + pn.getMaDonHangHienThi() + " (" + pn.getMaPhieu() + ")\n"
                    + "• Đã sinh lô mới: " + (loMoi != null ? loMoi.getMaLo() : "") + " - NCC: " + tenNCC);
            return pn.getMaDonHangHienThi();

        } else if ("Phiếu Xuất".equals(loaiPhieu)) {
            if (loChon == null) {
                JOptionPane.showMessageDialog(parent,
                        "Mặt hàng này hiện không có lô nào CÒN HẠN trong kho để xuất bán!",
                        "Không có lô khả dụng", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            long giaBan;
            try {
                giaBan = FormatUtils.parseMoney(strDonGia);
                if (giaBan <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parent, "Đơn giá bán phải là số nguyên dương!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            PhieuXuat px = new PhieuXuat(maDonHang, ngayGD, idHangHoa, loChon.getId(),
                    soLuong, giaBan, khachHang, ghiChu);
            String loi = managerPhieu.xuatKho(px);
            if (loi != null) {
                JOptionPane.showMessageDialog(parent, loi, "Cảnh báo xuất kho", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            JOptionPane.showMessageDialog(parent,
                    "Lập Phiếu Xuất bán thành công từ lô " + loChon.getMaLo() + "!\n"
                    + "• Số hoá đơn (Bill): " + px.getMaDonHangHienThi());
            return px.getMaDonHangHienThi();

        } else {
            if (loChon == null) {
                JOptionPane.showMessageDialog(parent,
                        "Vui lòng chọn lô hàng đang còn tồn trong kho để báo hư hỏng / huỷ!",
                        "Chưa chọn lô", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            PhieuHuy ph = new PhieuHuy(maDonHang, ngayGD, loChon.getId(), soLuong, lyDo, ghiChu);
            String loi = managerPhieu.baoHuHong(ph);
            if (loi != null) {
                JOptionPane.showMessageDialog(parent, loi, "Không thể lập Phiếu Huỷ", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            JOptionPane.showMessageDialog(parent,
                    "Đã ghi nhận hư hỏng & trừ tồn kho lô " + loChon.getMaLo() + "!\n"
                    + "• Mã biên bản: " + ph.getMaDonHangHienThi() + "\n"
                    + "• Nguyên nhân: " + ph.getDoiTac());
            return ph.getMaDonHangHienThi();
        }
    }

    public boolean xoaPhieu(Window parent, int id) {
        PhieuGiaoDich target = managerPhieu.findById(id);
        if (target == null) {
            return false;
        }
        int confirm = JOptionPane.showConfirmDialog(parent,
                "Bạn có chắc muốn xoá chứng từ " + target.getMaPhieu()
                + " (Mã đơn: " + target.getMaDonHangHienThi() + ")?",
                "Xác nhận xoá", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return false;
        }
        String loi = managerPhieu.xoaPhieu(id);
        if (loi != null) {
            JOptionPane.showMessageDialog(parent, loi, "Không thể xoá phiếu", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public void moCuaSoXuatBill(Window parent, String maDonHang) {
        if (maDonHang == null || maDonHang.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Vui lòng chọn 1 dòng phiếu trên bảng để xuất Bill!");
            return;
        }
        new HoaDonDialog(parent, maDonHang.trim(), managerPhieu, managerHangHoa, managerNCC).setVisible(true);
    }
}
