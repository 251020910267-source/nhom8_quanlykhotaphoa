package com.mycompany.quanlydoituongdacbiet.entity;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Phiếu huỷ hàng (biên bản hư hỏng): ghi nhận hàng trong kho bị chuột cắn, ẩm mốc do thời tiết,
 * vỡ/móp, hết hạn... Hàng huỷ bị trừ khỏi tồn kho của lô và ghi nhận thiệt hại theo giá vốn nhập.
 */
@XmlRootElement(name = "PhieuHuy")
@XmlAccessorType(XmlAccessType.FIELD)
public class PhieuHuy extends PhieuGiaoDich {
    private LyDoHuHong lyDo;

    public PhieuHuy() {
    }

    public PhieuHuy(String maDonHang, Date ngayGiaoDich, int idLoHang, int soLuong, LyDoHuHong lyDo, String ghiChu) {
        super(0, ngayGiaoDich, 0, soLuong, 0);
        setMaDonHang(maDonHang);
        setIdLoHang(idLoHang);
        setGhiChu(ghiChu);
        this.lyDo = lyDo;
    }

    @Override
    public double tinhThanhTien() {
        return tinhTienHang();
    }

    @Override
    protected String getTienToMa() { return "PH"; }

    @Override
    public String getTienToDonHang() { return "BBH"; }

    @Override
    public String getLoaiPhieu() { return "Phiếu Huỷ"; }

    @Override
    public String getDoiTac() { return lyDo == null ? "" : lyDo.getTenHienThi(); }

    @Override
    public String getTieuDeChungTu() { return "BIÊN BẢN XỬ LÝ HÀNG HƯ HỎNG / HẾT HẠN"; }

    @Override
    public int getBienDongTonKho() { return -getSoLuong(); }

    public LyDoHuHong getLyDo() { return lyDo; }
    public void setLyDo(LyDoHuHong lyDo) { this.lyDo = lyDo; }
}
