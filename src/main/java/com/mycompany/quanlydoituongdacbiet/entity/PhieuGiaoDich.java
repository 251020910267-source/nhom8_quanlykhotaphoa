package com.mycompany.quanlydoituongdacbiet.entity;

import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class PhieuGiaoDich implements Serializable {
    private int id;
    private Date ngayGiaoDich;
    private int maHangHoa;
    private int soLuong;
    private double donGia;

    public PhieuGiaoDich() {
    }

    public PhieuGiaoDich(int id, Date ngayGiaoDich, int maHangHoa, int soLuong, double donGia) {
        this.id = id;
        this.ngayGiaoDich = ngayGiaoDich;
        this.maHangHoa = maHangHoa;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    public abstract double tinhThanhTien();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getNgayGiaoDich() { return ngayGiaoDich; }
    public void setNgayGiaoDich(Date ngayGiaoDich) { this.ngayGiaoDich = ngayGiaoDich; }

    public int getMaHangHoa() { return maHangHoa; }
    public void setMaHangHoa(int maHangHoa) { this.maHangHoa = maHangHoa; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }
}