package com.mycompany.quanlykhohang.entity; // gói chứa lớp

import java.util.ArrayList; // danh sách động kiểu mảng
import java.util.List; // giao diện danh sách
import javax.xml.bind.annotation.XmlAccessType; // kiểu truy cập của JAXB
import javax.xml.bind.annotation.XmlAccessorType; // annotation chọn kiểu truy cập
import javax.xml.bind.annotation.XmlElement; // annotation đặt tên thẻ cho từng phần tử
import javax.xml.bind.annotation.XmlRootElement; // annotation đánh dấu thẻ gốc

/** Lớp bao danh sách hàng hoá để JAXB đọc/ghi file HangHoa.xml. */
@XmlRootElement(name = "DanhSachHangHoa") // thẻ gốc của file XML là <DanhSachHangHoa>
@XmlAccessorType(XmlAccessType.FIELD) // JAXB truy cập trực tiếp thuộc tính
public class HangHoaXML { // lớp chứa toàn bộ danh sách để ghi cùng lúc

    @XmlElement(name = "HangHoa") // mỗi phần tử trong list sẽ là một thẻ <HangHoa>
    private List<HangHoa> hangHoa = new ArrayList<HangHoa>(); // danh sách hàng hoá, khởi tạo rỗng để không bị null

    public List<HangHoa> getHangHoa() { return hangHoa; } // trả về danh sách
    public void setHangHoa(List<HangHoa> hangHoa) { this.hangHoa = hangHoa; } // gán danh sách
} // kết thúc lớp
