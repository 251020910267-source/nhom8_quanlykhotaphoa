package com.mycompany.quanlydoituongdacbiet.entity;

import com.mycompany.quanlydoituongdacbiet.utils.FormatUtils;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * LÔ HÀNG = một đợt nhập một mặt hàng cụ thể từ một nhà cung cấp cụ thể.
 *
 * Giải quyết bài toán thực tế:
 * - Cùng 1 mặt hàng (ví dụ: Thùng sữa Fami, mã 83VNHS) nhập từ 2 Nhà cung cấp khác nhau:
 *   + Mã mặt hàng (maHang) GIỐNG NHAU vì là cùng 1 sản phẩm.
 *   + Nhưng sinh ra 2 Lô hàng khác nhau (LO00001, LO00002), thuộc 2 Mã đơn nhập khác nhau,
 *     có HẠN SỬ DỤNG khác nhau và GIÁ NHẬP có thể khác nhau.
 * - Mỗi lô theo dõi đầy đủ vòng đời: SL nhập, SL đã xuất kho, SL bị hư hỏng (chuột, thời tiết,
 *   vỡ móp, hết hạn...), SL còn tồn thực tế và Trạng thái lô.
 */
@XmlRootElement(name = "LoHang")
@XmlAccessorType(XmlAccessType.FIELD)
public class LoHang implements Serializable {

    /** Số ngày trước HSD bắt đầu cảnh báo sắp hết hạn. */
    public static final int SO_NGAY_CANH_BAO = 30;

    private int id;                 // ID lô tự tăng
    private int idHangHoa;          // ID mặt hàng trong danh mục HangHoa
    private int idNhaCungCap;       // ID nhà cung cấp
    private String tenNhaCungCap;   // Tên nhà cung cấp (hiển thị nhanh)
    private int idPhieuNhap;        // ID phiếu nhập sinh ra lô này
    private String maDonNhap;       // Mã đơn nhập hàng (ví dụ DHN0001)
    private Date ngayNhap;          // Ngày nhập kho
    private Date hanSuDung;         // Hạn sử dụng riêng của lô (null nếu hàng không có HSD)
    private int soLuongNhap;        // Tổng số lượng ban đầu nhập vào
    private int soLuongDaXuat;      // Số lượng đã xuất bán
    private int soLuongHuHong;      // Số lượng bị hư hỏng / đã huỷ
    private long giaNhap;           // Giá nhập 1 đơn vị của riêng lô này
    private String chiTietHuHong;   // Tóm tắt nguyên nhân hư hỏng (nếu có), ví dụ: "Chuột cắn (3)"

    public LoHang() {
    }

    public LoHang(int idHangHoa, int idNhaCungCap, String tenNhaCungCap, int idPhieuNhap,
            String maDonNhap, Date ngayNhap, Date hanSuDung, int soLuongNhap, long giaNhap) {
        this.idHangHoa = idHangHoa;
        this.idNhaCungCap = idNhaCungCap;
        this.tenNhaCungCap = tenNhaCungCap;
        this.idPhieuNhap = idPhieuNhap;
        this.maDonNhap = maDonNhap;
        this.ngayNhap = ngayNhap;
        this.hanSuDung = hanSuDung;
        this.soLuongNhap = soLuongNhap;
        this.giaNhap = giaNhap;
    }

    // ================== Nghiệp vụ của lô ==================

    /** Mã lô hiển thị chuẩn, ví dụ LO00001. */
    public String getMaLo() {
        return String.format("LO%05d", id);
    }

    /** Số lượng thực tế còn nằm trong kho. */
    public int getSoLuongCon() {
        return Math.max(0, soLuongNhap - soLuongDaXuat - soLuongHuHong);
    }

    /** Kiểm tra lô đã quá hạn sử dụng tính đến đầu ngày hôm nay chưa. */
    public boolean isHetHan() {
        return hanSuDung != null && hanSuDung.before(dauNgayHomNay());
    }

    /** Kiểm tra lô còn hạn nhưng HSD rơi vào N ngày tới. */
    public boolean isSapHetHan(int soNgay) {
        if (hanSuDung == null || isHetHan()) {
            return false;
        }
        Calendar moc = Calendar.getInstance();
        moc.setTime(dauNgayHomNay());
        moc.add(Calendar.DAY_OF_YEAR, soNgay);
        return !hanSuDung.after(moc.getTime());
    }

    /** Số ngày còn lại đến HSD (âm nếu đã quá hạn), Integer.MAX_VALUE nếu không có HSD. */
    public long getSoNgayConHan() {
        if (hanSuDung == null) {
            return Integer.MAX_VALUE;
        }
        long ms = hanSuDung.getTime() - dauNgayHomNay().getTime();
        return Math.floorDiv(ms, 24L * 60 * 60 * 1000);
    }

    /** Lô có được phép xuất bán không: còn tồn > 0 và chưa hết hạn. */
    public boolean coTheXuat() {
        return getSoLuongCon() > 0 && !isHetHan();
    }

    /** Giá trị tiền hàng còn tồn của lô (tính theo giá nhập). */
    public long getGiaTriTon() {
        return getSoLuongCon() * giaNhap;
    }

    /** Trạng thái thực tế của lô hàng trong kho. */
    public TrangThaiLoHang getTrangThai() {
        if (getSoLuongCon() <= 0) {
            if (soLuongHuHong > 0 && soLuongDaXuat == 0) {
                return TrangThaiLoHang.DA_HUY;
            }
            if (soLuongHuHong > 0 && soLuongHuHong >= soLuongDaXuat) {
                return TrangThaiLoHang.DA_HUY;
            }
            return TrangThaiLoHang.DA_XUAT_HET;
        }
        if (isHetHan()) {
            return TrangThaiLoHang.HET_HAN;
        }
        if (isSapHetHan(SO_NGAY_CANH_BAO)) {
            return TrangThaiLoHang.SAP_HET_HAN;
        }
        if (soLuongHuHong > 0) {
            return TrangThaiLoHang.CON_HANG_CO_HAO_HUT;
        }
        return TrangThaiLoHang.CON_HANG;
    }

    /** Hiển thị gọn rõ trong JComboBox khi chọn lô xuất / huỷ. */
    @Override
    public String toString() {
        return getMaLo() + " | NCC: " + tenNhaCungCap
                + " | HSD: " + (hanSuDung == null ? "Không HSD" : FormatUtils.formatDate(hanSuDung))
                + " | Còn: " + getSoLuongCon()
                + (isHetHan() ? " [HẾT HẠN]" : (isSapHetHan(SO_NGAY_CANH_BAO) ? " [SẮP HẾT HẠN]" : ""));
    }

    private static Date dauNgayHomNay() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    // ================== Getter / Setter ==================
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdHangHoa() { return idHangHoa; }
    public void setIdHangHoa(int idHangHoa) { this.idHangHoa = idHangHoa; }

    public int getIdNhaCungCap() { return idNhaCungCap; }
    public void setIdNhaCungCap(int idNhaCungCap) { this.idNhaCungCap = idNhaCungCap; }

    public String getTenNhaCungCap() { return tenNhaCungCap; }
    public void setTenNhaCungCap(String tenNhaCungCap) { this.tenNhaCungCap = tenNhaCungCap; }

    public int getIdPhieuNhap() { return idPhieuNhap; }
    public void setIdPhieuNhap(int idPhieuNhap) { this.idPhieuNhap = idPhieuNhap; }

    public String getMaDonNhap() { return maDonNhap; }
    public void setMaDonNhap(String maDonNhap) { this.maDonNhap = maDonNhap; }

    public Date getNgayNhap() { return ngayNhap; }
    public void setNgayNhap(Date ngayNhap) { this.ngayNhap = ngayNhap; }

    public Date getHanSuDung() { return hanSuDung; }
    public void setHanSuDung(Date hanSuDung) { this.hanSuDung = hanSuDung; }

    public int getSoLuongNhap() { return soLuongNhap; }
    public void setSoLuongNhap(int soLuongNhap) { this.soLuongNhap = soLuongNhap; }

    public int getSoLuongDaXuat() { return soLuongDaXuat; }
    public void setSoLuongDaXuat(int soLuongDaXuat) { this.soLuongDaXuat = soLuongDaXuat; }

    public int getSoLuongHuHong() { return soLuongHuHong; }
    public void setSoLuongHuHong(int soLuongHuHong) { this.soLuongHuHong = soLuongHuHong; }

    public long getGiaNhap() { return giaNhap; }
    public void setGiaNhap(long giaNhap) { this.giaNhap = giaNhap; }

    public String getChiTietHuHong() { return chiTietHuHong == null ? "" : chiTietHuHong; }
    public void setChiTietHuHong(String chiTietHuHong) { this.chiTietHuHong = chiTietHuHong; }
}
