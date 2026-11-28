package com.mycompany.quanlydoituongdacbiet.controller;

import com.mycompany.quanlydoituongdacbiet.action.ManagerHangHoa;
import com.mycompany.quanlydoituongdacbiet.action.ManagerLoHang;
import com.mycompany.quanlydoituongdacbiet.action.ThongKeAction;
import com.mycompany.quanlydoituongdacbiet.entity.HangHoa;
import com.mycompany.quanlydoituongdacbiet.entity.LoHang;
import com.mycompany.quanlydoituongdacbiet.view.HangHoaView;
import com.mycompany.quanlydoituongdacbiet.view.MainView;
import com.mycompany.quanlydoituongdacbiet.view.NhaCungCapView;
import com.mycompany.quanlydoituongdacbiet.view.PhieuGiaoDichView;
import com.mycompany.quanlydoituongdacbiet.view.ThongKeView;
import com.mycompany.quanlydoituongdacbiet.view.TonKhoLoView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.JFrame;

public class MainController {
    private final MainView mainView;

    public MainController(MainView view) {
        this.mainView = view;
        view.addChooseResidentsListener(new ChooseHangHoaListener());
        view.addChooseSpecialPersonListener(new ChooseNhaCungCapListener());
        view.addChooseThongKeListener(new ChooseThongKeListener());
        view.addChoosePhieuGiaoDichListener(new ChoosePhieuGiaoDichListener());
        view.addChooseTonKhoLoListener(new ChooseTonKhoLoListener());

        view.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                capNhatCanhBaoTrenManHinhChinh();
            }
        });
        capNhatCanhBaoTrenManHinhChinh();
    }

    public void showMainView() {
        capNhatCanhBaoTrenManHinhChinh();
        mainView.setVisible(true);
    }

    /** Tính năng nâng cao: Tự động kiểm tra và cảnh báo lô hết hạn / sắp hết hạn & sắp hết tồn kho. */
    private void capNhatCanhBaoTrenManHinhChinh() {
        ManagerLoHang qlLo = new ManagerLoHang();
        ManagerHangHoa qlHang = new ManagerHangHoa();
        ThongKeAction tk = new ThongKeAction();

        int soLoHetHan = 0;
        int soLoSapHetHan = 0;
        for (LoHang lo : qlLo.getList()) {
            if (lo.getSoLuongCon() > 0) {
                if (lo.isHetHan()) {
                    soLoHetHan++;
                } else if (lo.isSapHetHan(LoHang.SO_NGAY_CANH_BAO)) {
                    soLoSapHetHan++;
                }
            }
        }
        List<HangHoa> sapHetTon = tk.layMatHangSapHetTon(qlHang.getList(), qlLo);

        if (soLoHetHan > 0 || soLoSapHetHan > 0 || !sapHetTon.isEmpty()) {
            mainView.setCanhBaoNhanh(String.format(
                    "CẢNH BÁO TỰ ĐỘNG: Có %d lô đã hết hạn (cấm xuất)  •  %d lô sắp hết hạn (<= 30 ngày)  •  %d mặt hàng chạm mức tồn tối thiểu",
                    soLoHetHan, soLoSapHetHan, sapHetTon.size()), true);
        } else {
            mainView.setCanhBaoNhanh("Kho hàng ổn định: Không có lô hàng quá hạn hoặc thiếu hụt tồn kho.", false);
        }
    }

    class ChooseHangHoaListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            HangHoaView hangHoaView = new HangHoaView();
            hangHoaView.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            HangHoaController hangHoaController = new HangHoaController(hangHoaView);
            hangHoaController.showView();
        }
    }

    class ChooseNhaCungCapListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            NhaCungCapView nhaCungCapView = new NhaCungCapView();
            nhaCungCapView.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            NhaCungCapController nccController = new NhaCungCapController(nhaCungCapView);
            nccController.showNhaCungCapView();
        }
    }

    class ChooseThongKeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ThongKeView thongKeView = new ThongKeView();
            thongKeView.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            ThongKeController thongKeController = new ThongKeController(thongKeView);
            thongKeController.showThongKeView();
        }
    }

    class ChoosePhieuGiaoDichListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            PhieuGiaoDichView phieuView = new PhieuGiaoDichView();
            phieuView.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            phieuView.setLocationRelativeTo(null);
            phieuView.setVisible(true);
        }
    }

    class ChooseTonKhoLoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            TonKhoLoView loView = new TonKhoLoView();
            loView.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            loView.setVisible(true);
        }
    }
}
