package com.mycompany.quanlydoituongdacbiet.controller;

import com.mycompany.quanlydoituongdacbiet.action.ManagerNhaCungCap;
import com.mycompany.quanlydoituongdacbiet.entity.NhaCungCap;
import com.mycompany.quanlydoituongdacbiet.view.NhaCungCapView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class NhaCungCapController {
    private ManagerNhaCungCap managerNCC;
    private NhaCungCapView nccView;

    public NhaCungCapController(NhaCungCapView view) {
        this.nccView = view;
        this.managerNCC = new ManagerNhaCungCap();

        view.addAddNCCListener(new AddNCCListener());
        view.addEditNCCListener(new EditNCCListener());
        view.addDeleteNCCListener(new DeleteNCCListener());
        view.addClearListener(new ClearNCCListener());
        view.addSearchListener(new SearchNCCListener());
        view.addListNCCSelectionListener(new ListNCCSelectionListener());
    }

    public void showNhaCungCapView() {
        nccView.setVisible(true);
        nccView.showListNCC(managerNCC.getListNCC());
    }

    class AddNCCListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            NhaCungCap ncc = nccView.getNCCInfo();
            if (ncc != null) {
                if (!managerNCC.isNCCUnique(ncc)) {
                    nccView.showMessage("Tên nhà cung cấp hoặc Số điện thoại đã tồn tại!");
                    return;
                }
                managerNCC.add(ncc);
                nccView.showNCC(ncc);
                nccView.showListNCC(managerNCC.getListNCC());
                nccView.showMessage("Thêm nhà cung cấp thành công!");
            }
        }
    }

    class EditNCCListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            NhaCungCap ncc = nccView.getNCCInfo();
            if (ncc != null) {
                if (!managerNCC.isNCCUnique(ncc)) {
                    nccView.showMessage("Tên nhà cung cấp hoặc Số điện thoại bị trùng!");
                    return;
                }
                managerNCC.edit(ncc);
                nccView.showNCC(ncc);
                nccView.showListNCC(managerNCC.getListNCC());
                nccView.showMessage("Cập nhật thành công!");
            }
        }
    }

    class DeleteNCCListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            NhaCungCap ncc = nccView.getNCCInfo();
            if (ncc != null) {
                if (nccView.showConfirm("Bạn có chắc chắn muốn xoá nhà cung cấp này?")) {
                    managerNCC.delete(ncc);
                    nccView.clearNCCInfo();
                    nccView.showListNCC(managerNCC.getListNCC());
                    nccView.showMessage("Xoá thành công!");
                }
            }
        }
    }

    class ClearNCCListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            nccView.clearNCCInfo();
            nccView.showListNCC(managerNCC.getListNCC());
        }
    }

    class SearchNCCListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String key = nccView.getSearchKeyword();
            nccView.showListNCC(managerNCC.searchNCC(key));
        }
    }

    class ListNCCSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            nccView.fillNCCFromSelectedRow();
        }
    }
}