package com.mycompany.quanlykhohang; // gói chứa lớp chạy chương trình

import com.mycompany.quanlykhohang.controller.HangHoaController; // lớp điều khiển
import com.mycompany.quanlykhohang.view.HangHoaView; // lớp giao diện
import java.awt.EventQueue; // hàng đợi sự kiện của Swing

public class QuanLyKhoHang { // lớp chứa hàm main
    public static void main(String[] args) { // điểm bắt đầu của chương trình
        EventQueue.invokeLater(new Runnable() { // chạy đoạn code bên trong trên luồng giao diện (Swing yêu cầu)
            public void run() { // nội dung sẽ chạy
                HangHoaView view = new HangHoaView(); // tạo cửa sổ giao diện
                new HangHoaController(view).showView(); // tạo controller nối với view rồi hiển thị cửa sổ
            } // kết thúc run
        }); // kết thúc invokeLater
    } // kết thúc main
} // kết thúc lớp
