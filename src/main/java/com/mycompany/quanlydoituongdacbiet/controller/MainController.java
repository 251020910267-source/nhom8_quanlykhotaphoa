package com.mycompany.quanlydoituongdacbiet.controller;

import com.mycompany.quanlydoituongdacbiet.view.HangHoaView;
import com.mycompany.quanlydoituongdacbiet.view.MainView;
import com.mycompany.quanlydoituongdacbiet.view.NhaCungCapView;
import com.mycompany.quanlydoituongdacbiet.view.ThongKeView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

public class MainController {
    private MainView mainView;
    private HangHoaView hangHoaView;
    private NhaCungCapView nhaCungCapView;
    private ThongKeView thongKeView;

    public MainController(MainView view) {
        this.mainView = view;
        view.addChooseResidentsListener(new ChooseHangHoaListener());
        view.addChooseSpecialPersonListener(new ChooseNhaCungCapListener());
        view.addChooseThongKeListener(new ChooseThongKeListener());
    }

    public void showMainView() {
        mainView.setVisible(true);
    }

    class ChooseHangHoaListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            hangHoaView = new HangHoaView();
            hangHoaView.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            HangHoaController hangHoaController = new HangHoaController(hangHoaView);
            hangHoaController.showView();
        }
    }

    class ChooseNhaCungCapListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            nhaCungCapView = new NhaCungCapView();
            nhaCungCapView.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            NhaCungCapController nccController = new NhaCungCapController(nhaCungCapView);
            nccController.showNhaCungCapView();
        }
    }

    class ChooseThongKeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            thongKeView = new ThongKeView();
            thongKeView.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            ThongKeController thongKeController = new ThongKeController(thongKeView);
            thongKeController.showThongKeView();
        }
    }
}