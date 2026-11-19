package com.mycompany.quanlydoituongdacbiet.entity; // khai báo gói (thư mục logic) chứa lớp này

import java.util.Date; // dùng kiểu Date cho hạn sử dụng
import javax.xml.bind.annotation.XmlAccessType; // kiểu truy cập dữ liệu khi JAXB đọc/ghi XML
import javax.xml.bind.annotation.XmlAccessorType; // annotation chọn cách JAXB truy cập thuộc tính
import javax.xml.bind.annotation.XmlRootElement; // annotation đánh dấu lớp là phần tử gốc XML

@XmlRootElement(name = "HangHoa") // khi ghi XML, đối tượng này có thẻ <HangHoa>
@XmlAccessorType(XmlAccessType.FIELD) // JAXB lấy dữ liệu trực tiếp từ các thuộc tính (field), không cần getter/setter
public class HangHoa { // lớp thực thể Hàng hoá

    private int id;              // khoá nội bộ, tự tăng, dùng để phân biệt các dòng
    private String maHang;       // mã hàng (không được trùng), ví dụ HH001
    private String tenHang;      // tên hàng
    private String nhomHang;     // nhóm hàng: Đồ uống, Bánh kẹo, Gia vị...
    private String donViTinh;    // đơn vị tính: chai, gói, thùng...
    private long donGia;         // đơn giá tính bằng đồng; dùng long để không bị sai số như double
    private Date hanSuDung;      // hạn sử dụng

    public HangHoa() { // constructor rỗng, JAXB bắt buộc phải có để tạo đối tượng khi đọc XML
    } // kết thúc constructor rỗng

    public HangHoa(int id, String maHang, String tenHang, String nhomHang, // constructor đầy đủ tham số
            String donViTinh, long donGia, Date hanSuDung) { // phần còn lại của danh sách tham số
        this.id = id; // gán tham số id vào thuộc tính id của đối tượng
        this.maHang = maHang; // gán mã hàng
        this.tenHang = tenHang; // gán tên hàng
        this.nhomHang = nhomHang; // gán nhóm hàng
        this.donViTinh = donViTinh; // gán đơn vị tính
        this.donGia = donGia; // gán đơn giá
        this.hanSuDung = hanSuDung; // gán hạn sử dụng
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
} // kết thúc lớp HangHoa
