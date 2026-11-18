package com.mycompany.quanlydoituongdacbiet.entity;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "NhaCungCap")
@XmlAccessorType(XmlAccessType.FIELD)
public class NhaCungCap implements Serializable {
    private int id;
    private String tenNCC;
    private String soDienThoai;
    private String diaChi;
    private String email;

    public NhaCungCap() {
    }

    public NhaCungCap(int id, String tenNCC, String soDienThoai, String diaChi, String email) {
        this.id = id;
        this.tenNCC = tenNCC;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.email = email;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTenNCC() { return tenNCC; }
    public void setTenNCC(String tenNCC) { this.tenNCC = tenNCC; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return id + " - " + tenNCC;
    }
}