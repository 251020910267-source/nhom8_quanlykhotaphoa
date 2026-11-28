package com.mycompany.quanlydoituongdacbiet.entity;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/** Phiếu nhập kho: mỗi dòng nhập sinh ra đúng 1 lô hàng mới (HSD, giá nhập, NCC riêng). */
@XmlRootElement(name = "PhieuNhap")
@XmlAccessorType(XmlAccessType.FIELD)
public class PhieuNhap extends PhieuGiaoDich {
    private int idNhaCungCap;
    private String tenNhaCungCap;
    private Date hanSuDung;

    public PhieuNhap() {
    }

    public PhieuNhap(int id, Date ngayGiaoDich, int maHangHoa, int soLuong, double donGia, String tenNhaCungCap) {
        super(id, ngayGiaoDich, maHangHoa, soLuong, donGia);
        this.tenNhaCungCap = tenNhaCungCap;
    }

    public PhieuNhap(String maDonHang, Date ngayGiaoDich, int maHangHoa, int soLuong, double donGia,
            int idNhaCungCap, String tenNhaCungCap, Date hanSuDung, String ghiChu) {
        super(0, ngayGiaoDich, maHangHoa, soLuong, donGia);
        setMaDonHang(maDonHang);
        setGhiChu(ghiChu);
        this.idNhaCungCap = idNhaCungCap;
        this.tenNhaCungCap = tenNhaCungCap;
        this.hanSuDung = hanSuDung;
    }

    @Override
    public double tinhThanhTien() {
        return tinhTienHang();
    }

    @Override
    protected String getTienToMa() { return "PN"; }

    @Override
    public String getTienToDonHang() { return "DHN"; }

    @Override
    public String getLoaiPhieu() { return "Phiếu Nhập"; }

    @Override
    public String getDoiTac() { return tenNhaCungCap; }

    @Override
    public String getTieuDeChungTu() { return "PHIẾU NHẬP KHO HÀNG HOÁ"; }

    @Override
    public int getBienDongTonKho() { return getSoLuong(); }

    public int getIdNhaCungCap() { return idNhaCungCap; }
    public void setIdNhaCungCap(int idNhaCungCap) { this.idNhaCungCap = idNhaCungCap; }

    public String getTenNhaCungCap() { return tenNhaCungCap; }
    public void setTenNhaCungCap(String tenNhaCungCap) { this.tenNhaCungCap = tenNhaCungCap; }

    public Date getHanSuDung() { return hanSuDung; }
    public void setHanSuDung(Date hanSuDung) { this.hanSuDung = hanSuDung; }
}
