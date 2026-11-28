package com.mycompany.quanlydoituongdacbiet.entity;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/** Phiếu xuất kho (bán hàng): xuất từ 1 lô còn hạn cụ thể. */
@XmlRootElement(name = "PhieuXuat")
@XmlAccessorType(XmlAccessType.FIELD)
public class PhieuXuat extends PhieuGiaoDich {
    public static final double THUE_VAT = 0.1; // 10% VAT

    private String tenKhachHang;

    public PhieuXuat() {
    }

    public PhieuXuat(int id, Date ngayGiaoDich, int maHangHoa, int soLuong, double donGia, String tenKhachHang) {
        super(id, ngayGiaoDich, maHangHoa, soLuong, donGia);
        this.tenKhachHang = tenKhachHang;
    }

    public PhieuXuat(String maDonHang, Date ngayGiaoDich, int maHangHoa, int idLoHang,
            int soLuong, double donGia, String tenKhachHang, String ghiChu) {
        super(0, ngayGiaoDich, maHangHoa, soLuong, donGia);
        setMaDonHang(maDonHang);
        setIdLoHang(idLoHang);
        setGhiChu(ghiChu);
        this.tenKhachHang = tenKhachHang;
    }

    public double tinhTienThue() {
        return tinhTienHang() * THUE_VAT;
    }

    @Override
    public double tinhThanhTien() {
        return tinhTienHang() + tinhTienThue();
    }

    @Override
    protected String getTienToMa() { return "PX"; }

    @Override
    public String getTienToDonHang() { return "HDX"; }

    @Override
    public String getLoaiPhieu() { return "Phiếu Xuất"; }

    @Override
    public String getDoiTac() { return tenKhachHang; }

    @Override
    public String getTieuDeChungTu() { return "HOÁ ĐƠN XUẤT BÁN HÀNG"; }

    @Override
    public int getBienDongTonKho() { return -getSoLuong(); }

    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }
}
