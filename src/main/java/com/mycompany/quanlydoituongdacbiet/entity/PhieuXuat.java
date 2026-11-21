package com.mycompany.quanlydoituongdacbiet.entity;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PhieuXuat")
@XmlAccessorType(XmlAccessType.FIELD)
public class PhieuXuat extends PhieuGiaoDich {
    private String tenKhachHang;

    public PhieuXuat() {
    }

    public PhieuXuat(int id, Date ngayGiaoDich, int maHangHoa, int soLuong, double donGia, String tenKhachHang) {
        super(id, ngayGiaoDich, maHangHoa, soLuong, donGia);
        this.tenKhachHang = tenKhachHang;
    }

    @Override
    public double tinhThanhTien() {
        // Thành tiền phiếu xuất bao gồm 10% thuế VAT (hoặc sửa thành getSoLuong() * getDonGia() nếu nhóm không tính VAT)
        return getSoLuong() * getDonGia() * 1.1;
    }

    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }
}