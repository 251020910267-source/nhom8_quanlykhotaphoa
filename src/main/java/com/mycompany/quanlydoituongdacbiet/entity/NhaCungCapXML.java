package com.mycompany.quanlydoituongdacbiet.entity;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "NhaCungCaps")
@XmlAccessorType(XmlAccessType.FIELD)
public class NhaCungCapXML {
    @XmlElement(name = "NhaCungCap")
    private List<NhaCungCap> nhaCungCap;

    public List<NhaCungCap> getNhaCungCap() {
        return nhaCungCap;
    }

    public void setNhaCungCap(List<NhaCungCap> nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
    }
}