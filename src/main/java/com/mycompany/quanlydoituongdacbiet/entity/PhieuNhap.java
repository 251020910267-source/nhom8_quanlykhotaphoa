package com.mycompany.quanlydoituongdacbiet.entity;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PhieuNhap")
@XmlAccessorType(XmlAccessType.FIELD)
public class PhieuNhap extends PhieuGiaoDich {
    private String tenNhaCungCap;

    public PhieuNhap() {
    }

    public PhieuNhap(int id, Date ngayGiaoDich, int maHangHoa, int soLuong, double donGia, String tenNhaCungCap) {
        super(id, ngayGiaoDich, maHangHoa, soLuong, donGia);
        this.tenNhaCungCap = tenNhaCungCap;
    }

    @Override
    public double tinhThanhTien() {
        return getSoLuong() * getDonGia();
    }

    public String getTenNhaCungCap() { return tenNhaCungCap; }
    public void setTenNhaCungCap(String tenNhaCungCap) { this.tenNhaCungCap = tenNhaCungCap; }
}