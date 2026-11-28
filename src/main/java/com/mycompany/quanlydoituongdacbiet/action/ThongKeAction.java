package com.mycompany.quanlydoituongdacbiet.action;

import com.mycompany.quanlydoituongdacbiet.entity.HangHoa;
import com.mycompany.quanlydoituongdacbiet.entity.LoHang;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuGiaoDich;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuHuy;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuNhap;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuXuat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lớp xử lý nghiệp vụ Tìm kiếm & Thống kê báo cáo kho hàng (Thành viên 5).
 * Kết hợp dữ liệu Danh mục Hàng hoá + Lô hàng + Phiếu Nhập/Xuất/Huỷ thực tế.
 */
public class ThongKeAction {

    // 1. Tìm kiếm gần đúng theo chuỗi (Mã hàng, Tên hàng, Nhóm hàng, hoặc Tên Nhà cung cấp của lô)
    public List<HangHoa> timKiemHangHoaTheoChuoi(List<HangHoa> dsGoc, List<LoHang> dsLo, String tuKhoa) {
        List<HangHoa> ketQua = new ArrayList<>();
        if (dsGoc == null) {
            return ketQua;
        }
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) {
            return new ArrayList<>(dsGoc);
        }
        String key = tuKhoa.trim().toLowerCase();
        for (HangHoa h : dsGoc) {
            boolean matchMa = h.getMaHang() != null && h.getMaHang().toLowerCase().contains(key);
            boolean matchTen = h.getTenHang() != null && h.getTenHang().toLowerCase().contains(key);
            boolean matchNhom = h.getNhomHang() != null && h.getNhomHang().toLowerCase().contains(key);
            boolean matchNCC = false;
            if (dsLo != null) {
                for (LoHang lo : dsLo) {
                    if (lo.getIdHangHoa() == h.getId() && lo.getTenNhaCungCap() != null
                            && lo.getTenNhaCungCap().toLowerCase().contains(key)) {
                        matchNCC = true;
                        break;
                    }
                }
            }
            if (matchMa || matchTen || matchNhom || matchNCC) {
                ketQua.add(h);
            }
        }
        return ketQua;
    }

    // 2. Tìm kiếm theo khoảng đơn giá bán [giaTu, giaDen]
    public List<HangHoa> timKiemTheoKhoangGia(List<HangHoa> dsGoc, long giaTu, long giaDen) {
        List<HangHoa> ketQua = new ArrayList<>();
        if (dsGoc == null) {
            return ketQua;
        }
        for (HangHoa h : dsGoc) {
            if (h.getDonGia() >= giaTu && h.getDonGia() <= giaDen) {
                ketQua.add(h);
            }
        }
        return ketQua;
    }

    // Kết hợp tìm kiếm chuỗi VÀ khoảng giá cùng lúc
    public List<HangHoa> timKiemKetHop(List<HangHoa> dsGoc, List<LoHang> dsLo, String tuKhoa, long giaTu, long giaDen) {
        List<HangHoa> locChuoi = timKiemHangHoaTheoChuoi(dsGoc, dsLo, tuKhoa);
        return timKiemTheoKhoangGia(locChuoi, giaTu, giaDen);
    }

    // 3. Thống kê Tổng / Lớn nhất / Nhỏ nhất
    public int tinhTongSoMatHang(List<HangHoa> dsGoc) {
        return dsGoc == null ? 0 : dsGoc.size();
    }

    public HangHoa timHangGiaCaoNhat(List<HangHoa> dsGoc) {
        if (dsGoc == null || dsGoc.isEmpty()) {
            return null;
        }
        return dsGoc.stream()
                .max(Comparator.comparingLong(HangHoa::getDonGia))
                .orElse(null);
    }

    public HangHoa timHangGiaThapNhat(List<HangHoa> dsGoc) {
        if (dsGoc == null || dsGoc.isEmpty()) {
            return null;
        }
        return dsGoc.stream()
                .min(Comparator.comparingLong(HangHoa::getDonGia))
                .orElse(null);
    }

    /** Tổng giá trị hàng đang tồn trong kho (tính theo giá vốn nhập của từng lô còn tồn). */
    public long tinhTongGiaTriTonKho(List<LoHang> dsLo) {
        long tong = 0;
        if (dsLo != null) {
            for (LoHang lo : dsLo) {
                tong += lo.getGiaTriTon();
            }
        }
        return tong;
    }

    /** Tổng tiền đã chi nhập hàng từ các nhà cung cấp. */
    public long tinhTongTienNhap(List<PhieuGiaoDich> dsPhieu) {
        long tong = 0;
        if (dsPhieu != null) {
            for (PhieuGiaoDich p : dsPhieu) {
                if (p instanceof PhieuNhap) {
                    tong += Math.round(p.tinhThanhTien());
                }
            }
        }
        return tong;
    }

    /** Tổng doanh thu xuất bán hàng (đã gồm VAT). */
    public long tinhTongDoanhThuXuat(List<PhieuGiaoDich> dsPhieu) {
        long tong = 0;
        if (dsPhieu != null) {
            for (PhieuGiaoDich p : dsPhieu) {
                if (p instanceof PhieuXuat) {
                    tong += Math.round(p.tinhThanhTien());
                }
            }
        }
        return tong;
    }

    /** Tổng giá trị thiệt hại do hàng hư hỏng / hết hạn phải huỷ. */
    public long tinhTongThietHaiHuy(List<PhieuGiaoDich> dsPhieu) {
        long tong = 0;
        if (dsPhieu != null) {
            for (PhieuGiaoDich p : dsPhieu) {
                if (p instanceof PhieuHuy) {
                    tong += Math.round(p.tinhThanhTien());
                }
            }
        }
        return tong;
    }

    // 4. Lọc các lô còn tồn đã hết hạn hoặc sắp hết hạn trong N ngày tới
    public List<LoHang> layDanhSachLoCanhBaoHetHan(List<LoHang> dsLo, int soNgayCanhBao) {
        List<LoHang> ketQua = new ArrayList<>();
        if (dsLo == null) {
            return ketQua;
        }
        for (LoHang lo : dsLo) {
            if (lo.getSoLuongCon() > 0 && (lo.isHetHan() || lo.isSapHetHan(soNgayCanhBao))) {
                ketQua.add(lo);
            }
        }
        ketQua.sort(ManagerLoHang.THEO_HSD);
        return ketQua;
    }

    /** Lấy danh sách mặt hàng có lô đang hết hạn hoặc sắp hết hạn trong N ngày. */
    public List<HangHoa> layMatHangCoLoSapHetHan(List<HangHoa> dsHang, List<LoHang> dsLo, int soNgay) {
        List<LoHang> loCanhBao = layDanhSachLoCanhBaoHetHan(dsLo, soNgay);
        List<HangHoa> ketQua = new ArrayList<>();
        if (dsHang == null) {
            return ketQua;
        }
        for (HangHoa h : dsHang) {
            for (LoHang lo : loCanhBao) {
                if (lo.getIdHangHoa() == h.getId()) {
                    ketQua.add(h);
                    break;
                }
            }
        }
        return ketQua;
    }

    /** Lấy danh sách mặt hàng có tồn khả dụng <= định mức tồn tối thiểu (cần nhập thêm). */
    public List<HangHoa> layMatHangSapHetTon(List<HangHoa> dsHang, ManagerLoHang qlLo) {
        List<HangHoa> ketQua = new ArrayList<>();
        if (dsHang == null) {
            return ketQua;
        }
        for (HangHoa h : dsHang) {
            if (qlLo.tinhTonKhaDung(h.getId()) <= h.getTonToiThieu()) {
                ketQua.add(h);
            }
        }
        return ketQua;
    }

    // 5. Thống kê tồn kho theo nhóm hàng & thiệt hại theo nguyên nhân hư hỏng
    public Map<String, String> thongKeChiTietTheoNhom(List<HangHoa> dsHang, ManagerLoHang qlLo) {
        Map<String, int[]> tam = new LinkedHashMap<>(); // [số mặt hàng, tổng SL tồn]
        if (dsHang != null) {
            for (HangHoa h : dsHang) {
                String nhom = (h.getNhomHang() == null || h.getNhomHang().trim().isEmpty())
                        ? "Khác" : h.getNhomHang().trim();
                int[] v = tam.computeIfAbsent(nhom, k -> new int[]{0, 0});
                v[0] += 1;
                v[1] += qlLo.tinhTonKho(h.getId());
            }
        }
        Map<String, String> kq = new LinkedHashMap<>();
        for (Map.Entry<String, int[]> e : tam.entrySet()) {
            kq.put(e.getKey(), e.getValue()[0] + " mặt hàng | Tồn: " + e.getValue()[1]);
        }
        return kq;
    }

    public Map<String, long[]> thongKeThietHaiTheoLyDo(List<PhieuGiaoDich> dsPhieu) {
        Map<String, long[]> map = new LinkedHashMap<>(); // [tổng SL hỏng, tổng tiền thiệt hại]
        if (dsPhieu != null) {
            for (PhieuGiaoDich p : dsPhieu) {
                if (p instanceof PhieuHuy) {
                    PhieuHuy ph = (PhieuHuy) p;
                    String lyDo = ph.getDoiTac().isEmpty() ? "Khác" : ph.getDoiTac();
                    long[] v = map.computeIfAbsent(lyDo, k -> new long[]{0, 0});
                    v[0] += ph.getSoLuong();
                    v[1] += Math.round(ph.tinhThanhTien());
                }
            }
        }
        return map;
    }
}
