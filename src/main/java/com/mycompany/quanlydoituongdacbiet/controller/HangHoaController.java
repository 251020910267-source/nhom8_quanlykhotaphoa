package com.mycompany.quanlydoituongdacbiet.controller;

import com.mycompany.quanlydoituongdacbiet.action.ManagerHangHoa;
import com.mycompany.quanlydoituongdacbiet.action.ManagerLoHang;
import com.mycompany.quanlydoituongdacbiet.entity.HangHoa;
import com.mycompany.quanlydoituongdacbiet.view.HangHoaView;
import com.mycompany.quanlydoituongdacbiet.view.TonKhoLoView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class HangHoaController {

    private final HangHoaView view;
    private final ManagerHangHoa manager;
    private final ManagerLoHang managerLo;

    public HangHoaController(HangHoaView view) {
        this.view = view;
        this.manager = new ManagerHangHoa();
        this.managerLo = new ManagerLoHang();

        view.addAddListener(new AddListener());
        view.addEditListener(new EditListener());
        view.addDeleteListener(new DeleteListener());
        view.addClearListener(new ClearListener());
        view.addXemLoListener(new XemLoListener());
        view.addTableSelectionListener(new SelectionListener());

        view.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                lamMoiBang();
            }
        });
        lamMoiBang();
    }

    private void lamMoiBang() {
        manager.taiLai();
        managerLo.taiLai();
        view.showListHangHoa(manager.getListHangHoa(), managerLo);
    }

    public void showView() {
        view.setVisible(true);
    }

    class AddListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            HangHoa h = view.getHangHoaInfo();
            if (h == null) {
                return;
            }
            String error = manager.themHangHoa(h);
            if (error != null) {
                view.showMessage(error);
                return;
            }
            lamMoiBang();
            view.clearForm();
            view.showMessage("Thêm mặt hàng thành công! Hãy sang mục Lập Phiếu Nhập để nhập các lô hàng vào kho.");
        }
    }

    class EditListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (view.getSelectedId() < 0) {
                view.showMessage("Hãy chọn mặt hàng cần sửa trong bảng.");
                return;
            }
            HangHoa h = view.getHangHoaInfo();
            if (h == null) {
                return;
            }
            String error = manager.suaHangHoa(h);
            if (error != null) {
                view.showMessage(error);
                return;
            }
            lamMoiBang();
            view.clearForm();
            view.showMessage("Cập nhật mặt hàng thành công.");
        }
    }

    class DeleteListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (view.getSelectedId() < 0) {
                view.showMessage("Hãy chọn mặt hàng cần xoá trong bảng.");
                return;
            }
            if (!view.confirm("Bạn có chắc muốn xoá mặt hàng này khỏi danh mục?")) {
                return;
            }
            String loi = manager.xoaHangHoa(view.getSelectedId());
            if (loi != null) {
                view.showMessage(loi);
                return;
            }
            lamMoiBang();
            view.clearForm();
            view.showMessage("Đã xoá mặt hàng khỏi danh mục.");
        }
    }

    class ClearListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.clearForm();
        }
    }

    class XemLoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int id = view.getSelectedId();
            if (id < 0) {
                view.showMessage("Hãy chọn 1 mặt hàng trong bảng để xem các lô của mặt hàng đó.");
                return;
            }
            TonKhoLoView loView = new TonKhoLoView(id);
            loView.setVisible(true);
        }
    }

    class SelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int id = view.getSelectedModelId();
            if (id >= 0) {
                HangHoa h = manager.findById(id);
                if (h != null) {
                    view.fillForm(h);
                }
            }
        }
    }
}
