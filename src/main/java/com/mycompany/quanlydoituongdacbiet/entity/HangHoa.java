package com.mycompany.quanlydoituongdacbiet.entity; // khai báo gói (thư mục logic) chứa lớp này

import javax.xml.bind.annotation.XmlAccessType; // kiểu truy cập dữ liệu khi JAXB đọc/ghi XML
import javax.xml.bind.annotation.XmlAccessorType; // annotation chọn cách JAXB truy cập thuộc tính
import javax.xml.bind.annotation.XmlRootElement; // annotation đánh dấu lớp là phần tử gốc XML

/**
 * Lớp thực thể Hàng hoá = THÔNG TIN SẢN PHẨM (danh mục), không phải từng lần nhập.
 *
 * Thay đổi ở bản v2: HẠN SỬ DỤNG đã được chuyển sang lớp LoHang, vì cùng một mặt hàng
 * nhập từ 2 nhà cung cấp/2 thời điểm khác nhau sẽ có HSD khác nhau.
 * Tồn kho và "HSD gần nhất" của mặt hàng được tính tổng hợp từ các lô.
 */
@XmlRootElement(name = "HangHoa") // khi ghi XML, đối tượng này có thẻ <HangHoa>
@XmlAccessorType(XmlAccessType.FIELD) // JAXB lấy dữ liệu trực tiếp từ các thuộc tính (field)
public class HangHoa { // lớp thực thể Hàng hoá

    private int id;              // khoá nội bộ, tự tăng, dùng để phân biệt các dòng
    private String maHang;       // mã hàng / mã vạch (không được trùng). Cùng sản phẩm -> cùng mã dù khác NCC
    private String tenHang;      // tên hàng
    private String nhomHang;     // nhóm hàng: Đồ uống, Bánh kẹo, Gia vị...
    private String donViTinh;    // đơn vị tính: chai, gói, thùng...
    private long donGia;         // GIÁ BÁN lẻ (đồng). Giá nhập nằm ở từng lô hàng
    private int tonToiThieu;     // mức tồn tối thiểu: tồn kho <= mức này thì cảnh báo cần nhập thêm

    public HangHoa() { // constructor rỗng, JAXB bắt buộc phải có
    }

    public HangHoa(int id, String maHang, String tenHang, String nhomHang, // constructor đầy đủ tham số
            String donViTinh, long donGia, int tonToiThieu) {
        this.id = id;
        this.maHang = maHang;
        this.tenHang = tenHang;
        this.nhomHang = nhomHang;
        this.donViTinh = donViTinh;
        this.donGia = donGia;
        this.tonToiThieu = tonToiThieu;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMaHang() { return maHang; }
    public void setMaHang(String maHang) { this.maHang = maHang; }

    public String getTenHang() { return tenHang; }
    public void setTenHang(String tenHang) { this.tenHang = tenHang; }

    public String getNhomHang() { return nhomHang; }
    public void setNhomHang(String nhomHang) { this.nhomHang = nhomHang; }

    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }

    public long getDonGia() { return donGia; }
    public void setDonGia(long donGia) { this.donGia = donGia; }

    public int getTonToiThieu() { return tonToiThieu; }
    public void setTonToiThieu(int tonToiThieu) { this.tonToiThieu = tonToiThieu; }

    @Override
    public String toString() { // hiển thị trong JComboBox
        return maHang + " - " + tenHang;
    }
} // kết thúc lớp HangHoa
