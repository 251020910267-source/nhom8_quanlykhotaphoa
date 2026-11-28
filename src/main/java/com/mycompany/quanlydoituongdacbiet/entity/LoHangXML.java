package com.mycompany.quanlydoituongdacbiet.entity;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/** Lớp bao danh sách lô hàng để JAXB đọc/ghi file lohang.xml. */
@XmlRootElement(name = "DanhSachLoHang")
@XmlAccessorType(XmlAccessType.FIELD)
public class LoHangXML {

    @XmlElement(name = "LoHang")
    private List<LoHang> loHang = new ArrayList<>();

    public List<LoHang> getLoHang() { return loHang; }
    public void setLoHang(List<LoHang> loHang) { this.loHang = loHang; }
}
