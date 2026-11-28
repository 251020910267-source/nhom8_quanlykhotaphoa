package com.mycompany.quanlydoituongdacbiet.entity;

import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * Lớp trừu tượng cho mọi chứng từ kho: Phiếu nhập, Phiếu xuất, Phiếu huỷ.
 * - id: khoá chính số nguyên tự tăng.
 * - maDonHang: Mã đơn hàng / Số Bill (ví dụ DHN0001, HDX0001, BBH0001).
 *   + Một đơn hàng có thể gồm nhiều dòng hàng CÙNG 1 đối tác (để in chung 1 Bill).
 *   + Cùng 1 mặt hàng nhập từ 2 Nhà cung cấp khác nhau bắt buộc mang 2 maDonHang khác nhau.
 * - idLoHang: lô hàng cụ thể mà dòng phiếu này tác động.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({PhieuNhap.class, PhieuXuat.class, PhieuHuy.class})
public abstract class PhieuGiaoDich implements Serializable {
    private int id;             // ID số nguyên tự tăng (theo yêu cầu bắt buộc của đề)
    private String maDonHang;   // Mã đơn hàng / Số hoá đơn (DHN0001, HDX0001, BBH0001)
    private Date ngayGiaoDich;
    private int maHangHoa;      // ID của HangHoa
    private int idLoHang;       // ID của LoHang
    private int soLuong;
    private double donGia;
    private String ghiChu;

    public PhieuGiaoDich() {
    }

    public PhieuGiaoDich(int id, Date ngayGiaoDich, int maHangHoa, int soLuong, double donGia) {
        this.id = id;
        this.ngayGiaoDich = ngayGiaoDich;
        this.maHangHoa = maHangHoa;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    // ============ Các phương thức trừu tượng -> mỗi lớp con ghi đè (đa hình) ============

    /** Thành tiền của phiếu (nhập: tiền hàng; xuất: gồm 10% VAT; huỷ: giá trị thiệt hại). */
    public abstract double tinhThanhTien();

    /** Tiền tố mã dòng phiếu: PN / PX / PH. */
    protected abstract String getTienToMa();

    /** Tiền tố mã đơn / chứng từ: DHN / HDX / BBH. */
    public abstract String getTienToDonHang();

    /** Tên loại phiếu hiển thị trên bảng. */
    public abstract String getLoaiPhieu();

    /** Đối tác của phiếu: NCC (nhập), khách hàng (xuất), lý do hư hỏng (huỷ). */
    public abstract String getDoiTac();

    /** Tiêu đề khi in chứng từ (bill). */
    public abstract String getTieuDeChungTu();

    /** Ảnh hưởng tới tồn kho: nhập +, xuất -, huỷ -. */
    public abstract int getBienDongTonKho();

    // ============ Phương thức dùng chung ============

    /** Mã dòng phiếu kho, ví dụ PN00001, PX00002, PH00003. */
    public String getMaPhieu() {
        return String.format("%s%05d", getTienToMa(), id);
    }

    /** Trả về mã đơn hàng (nếu dữ liệu cũ chưa có thì sinh từ tiền tố + ID). */
    public String getMaDonHangHienThi() {
        if (maDonHang != null && !maDonHang.trim().isEmpty()) {
            return maDonHang.trim();
        }
        return String.format("%s%04d", getTienToDonHang(), id);
    }

    /** Tiền hàng chưa thuế = số lượng x đơn giá. */
    public double tinhTienHang() {
        return soLuong * donGia;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMaDonHang() { return maDonHang; }
    public void setMaDonHang(String maDonHang) { this.maDonHang = maDonHang; }

    public Date getNgayGiaoDich() { return ngayGiaoDich; }
    public void setNgayGiaoDich(Date ngayGiaoDich) { this.ngayGiaoDich = ngayGiaoDich; }

    public int getMaHangHoa() { return maHangHoa; }
    public void setMaHangHoa(int maHangHoa) { this.maHangHoa = maHangHoa; }

    public int getIdLoHang() { return idLoHang; }
    public void setIdLoHang(int idLoHang) { this.idLoHang = idLoHang; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    public String getGhiChu() { return ghiChu == null ? "" : ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
