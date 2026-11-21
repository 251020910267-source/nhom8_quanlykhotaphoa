package com.mycompany.quanlydoituongdacbiet.entity;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PhieuGiaoDichs")
@XmlAccessorType(XmlAccessType.FIELD)
public class PhieuGiaoDichXML {

    @XmlElementRefs({
            @XmlElementRef(type = PhieuNhap.class),
            @XmlElementRef(type = PhieuXuat.class)
    })
    private List<PhieuGiaoDich> phieuGiaoDich;

    public List<PhieuGiaoDich> getPhieuGiaoDich() {
        return phieuGiaoDich;
    }

    public void setPhieuGiaoDich(List<PhieuGiaoDich> phieuGiaoDich) {
        this.phieuGiaoDich = phieuGiaoDich;
    }
}