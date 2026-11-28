package com.mycompany.quanlykhohang.entity; // khai báo gói (thư mục logic) chứa lớp này

import java.util.Date; // dùng kiểu Date cho hạn sử dụng
import javax.xml.bind.annotation.XmlAccessType; // kiểu truy cập dữ liệu khi JAXB đọc/ghi XML
import javax.xml.bind.annotation.XmlAccessorType; // annotation chọn cách JAXB truy cập thuộc tính
import javax.xml.bind.annotation.XmlRootElement; // annotation đánh dấu lớp là phần tử gốc XML

/** Đối tượng chính thứ nhất: Hàng hoá. Mỗi dòng là một LÔ của một mã hàng, kèm nhà cung cấp và tình trạng. */
@XmlRootElement(name = "HangHoa") // khi ghi XML, đối tượng này có thẻ <HangHoa>
@XmlAccessorType(XmlAccessType.FIELD) // JAXB lấy dữ liệu trực tiếp từ các thuộc tính (field), không cần getter/setter
public class HangHoa { // lớp thực thể Hàng hoá

    public static final String BINH_THUONG = "Bình thường"; // tình trạng mặc định
    public static final String HET_HAN = "Hết hạn"; // tình trạng tự động khi quá hạn sử dụng

    private int id;              // khoá nội bộ, tự tăng, dùng để phân biệt các dòng
    private String maHang;       // mã hàng, ví dụ HH001 (một mã hàng có thể có nhiều lô)
    private String tenHang;      // tên hàng
    private String nhomHang;     // nhóm hàng: Đồ uống, Bánh kẹo, Gia vị...
    private String donViTinh;    // đơn vị tính: chai, gói, thùng...
    private long donGia;         // đơn giá tính bằng đồng; dùng long để không bị sai số như double
    private Date hanSuDung;      // hạn sử dụng của lô này
    private String maLo;         // mã lô hàng, ví dụ L001 (cùng mã hàng + mã lô thì không được trùng)
    private String nhaCungCap;   // nhà cung cấp của lô này (để biết lô do ai cấp)
    private String tinhTrang;    // tình trạng do người dùng chọn: Bình thường, Chuột cắn hỏng, Ẩm mốc...

    public HangHoa() { // constructor rỗng, JAXB bắt buộc phải có để tạo đối tượng khi đọc XML
    } // kết thúc constructor rỗng

    public HangHoa(int id, String maHang, String tenHang, String nhomHang, // constructor đầy đủ tham số
            String donViTinh, long donGia, Date hanSuDung, // phần tiếp theo của danh sách tham số
            String maLo, String nhaCungCap, String tinhTrang) { // ba thông tin mới: lô, nhà cung cấp, tình trạng
        this.id = id; // gán tham số id vào thuộc tính id của đối tượng
        this.maHang = maHang; // gán mã hàng
        this.tenHang = tenHang; // gán tên hàng
        this.nhomHang = nhomHang; // gán nhóm hàng
        this.donViTinh = donViTinh; // gán đơn vị tính
        this.donGia = donGia; // gán đơn giá
        this.hanSuDung = hanSuDung; // gán hạn sử dụng
        this.maLo = maLo; // gán mã lô
        this.nhaCungCap = nhaCungCap; // gán nhà cung cấp
        this.tinhTrang = tinhTrang; // gán tình trạng
    } // kết thúc constructor đầy đủ

    public int getId() { return id; } // getter: trả về id
    public void setId(int id) { this.id = id; } // setter: gán id

    public String getMaHang() { return maHang; } // trả về mã hàng
    public void setMaHang(String maHang) { this.maHang = maHang; } // gán mã hàng

    public String getTenHang() { return tenHang; } // trả về tên hàng
    public void setTenHang(String tenHang) { this.tenHang = tenHang; } // gán tên hàng

    public String getNhomHang() { return nhomHang; } // trả về nhóm hàng
    public void setNhomHang(String nhomHang) { this.nhomHang = nhomHang; } // gán nhóm hàng

    public String getDonViTinh() { return donViTinh; } // trả về đơn vị tính
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; } // gán đơn vị tính

    public long getDonGia() { return donGia; } // trả về đơn giá
    public void setDonGia(long donGia) { this.donGia = donGia; } // gán đơn giá

    public Date getHanSuDung() { return hanSuDung; } // trả về hạn sử dụng
    public void setHanSuDung(Date hanSuDung) { this.hanSuDung = hanSuDung; } // gán hạn sử dụng

    public String getMaLo() { return maLo; } // trả về mã lô
    public void setMaLo(String maLo) { this.maLo = maLo; } // gán mã lô

    public String getNhaCungCap() { return nhaCungCap; } // trả về nhà cung cấp
    public void setNhaCungCap(String nhaCungCap) { this.nhaCungCap = nhaCungCap; } // gán nhà cung cấp

    public String getTinhTrang() { return tinhTrang; } // trả về tình trạng do người dùng chọn
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; } // gán tình trạng

    /** Lô đã quá hạn sử dụng so với thời điểm hiện tại? */
    public boolean isHetHan() { // kiểm tra hết hạn
        return hanSuDung != null && hanSuDung.before(new Date()); // có hạn và hạn đó đã qua
    } // kết thúc hàm

    /** Tình trạng do người dùng chọn có phải "Bình thường" (hoặc chưa chọn) không? */
    private boolean daChonBinhThuong() { // dùng nội bộ
        return tinhTrang == null || tinhTrang.trim().isEmpty() || BINH_THUONG.equalsIgnoreCase(tinhTrang.trim()); // rỗng hoặc đúng chữ Bình thường
    } // kết thúc hàm

    /** Tình trạng hiển thị: hết hạn được tự động ghi thêm, không cần người dùng chọn. */
    public String getTinhTrangHienThi() { // chuỗi hiển thị trên bảng
        if (isHetHan()) { // quá hạn sử dụng
            return daChonBinhThuong() ? HET_HAN : HET_HAN + "; " + tinhTrang.trim(); // hết hạn (kèm tình trạng hỏng nếu có)
        } // kết thúc if
        return daChonBinhThuong() ? BINH_THUONG : tinhTrang.trim(); // còn hạn: bình thường hoặc tình trạng đã chọn
    } // kết thúc hàm

    /** Cần cảnh báo (tô đỏ) khi hết hạn hoặc bị hỏng (chuột cắn, ẩm mốc...). */
    public boolean isCanhBao() { // dùng để tô đỏ dòng
        return isHetHan() || !daChonBinhThuong(); // hết hạn hoặc tình trạng khác bình thường
    } // kết thúc hàm
} // kết thúc lớp HangHoa
