package com.mycompany.quanlydoituongdacbiet.view;

import com.mycompany.quanlydoituongdacbiet.controller.NhaCungCapController;
import com.mycompany.quanlydoituongdacbiet.entity.NhaCungCap;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class NhaCungCapView extends JFrame {
    private JButton addBtn, editBtn, deleteBtn, clearBtn, searchBtn;
    private JScrollPane jScrollPaneTable;
    private JTable nccTable;
    private JLabel idLabel, tenLabel, sdtLabel, diaChiLabel, emailLabel, searchLabel;
    private JTextField idField, tenField, sdtField, diaChiField, emailField, searchField;

    private final String[] columnNames = new String[]{"ID", "Tên Nhà Cung Cấp", "Số Điện Thoại", "Địa Chỉ", "Email"};

    public NhaCungCapView() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản Lý Nhà Cung Cấp - Đề 13");

        idLabel = new JLabel("ID:");
        tenLabel = new JLabel("Tên NCC (*):");
        sdtLabel = new JLabel("Số ĐT (*):");
        diaChiLabel = new JLabel("Địa chỉ (*):");
        emailLabel = new JLabel("Email:");
        searchLabel = new JLabel("Tìm kiếm:");

        idField = new JTextField(6);
        idField.setEditable(false);
        tenField = new JTextField(18);
        sdtField = new JTextField(18);
        diaChiField = new JTextField(18);
        emailField = new JTextField(18);
        searchField = new JTextField(20);

        addBtn = new JButton("Thêm");
        editBtn = new JButton("Sửa");
        deleteBtn = new JButton("Xoá");
        clearBtn = new JButton("Làm mới");
        searchBtn = new JButton("Tìm");

        jScrollPaneTable = new JScrollPane();
        nccTable = new JTable();
        nccTable.setModel(new DefaultTableModel(new Object[][]{}, columnNames));
        jScrollPaneTable.setViewportView(nccTable);
        jScrollPaneTable.setPreferredSize(new Dimension(540, 310));

        SpringLayout layout = new SpringLayout();
        JPanel panel = new JPanel();
        panel.setLayout(layout);

        panel.add(jScrollPaneTable);
        panel.add(idLabel); panel.add(idField);
        panel.add(tenLabel); panel.add(tenField);
        panel.add(sdtLabel); panel.add(sdtField);
        panel.add(diaChiLabel); panel.add(diaChiField);
        panel.add(emailLabel); panel.add(emailField);
        panel.add(addBtn); panel.add(editBtn); panel.add(deleteBtn); panel.add(clearBtn);
        panel.add(searchLabel); panel.add(searchField); panel.add(searchBtn);

        layout.putConstraint(SpringLayout.WEST, idLabel, 20, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, idLabel, 20, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, idField, 110, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, idField, 20, SpringLayout.NORTH, panel);

        layout.putConstraint(SpringLayout.WEST, tenLabel, 20, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, tenLabel, 55, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, tenField, 110, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, tenField, 55, SpringLayout.NORTH, panel);

        layout.putConstraint(SpringLayout.WEST, sdtLabel, 20, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, sdtLabel, 90, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, sdtField, 110, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, sdtField, 90, SpringLayout.NORTH, panel);

        layout.putConstraint(SpringLayout.WEST, diaChiLabel, 20, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, diaChiLabel, 125, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, diaChiField, 110, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, diaChiField, 125, SpringLayout.NORTH, panel);

        layout.putConstraint(SpringLayout.WEST, emailLabel, 20, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, emailLabel, 160, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, emailField, 110, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, emailField, 160, SpringLayout.NORTH, panel);

        layout.putConstraint(SpringLayout.WEST, addBtn, 20, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, addBtn, 215, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, editBtn, 95, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, editBtn, 215, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, deleteBtn, 165, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, deleteBtn, 215, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, clearBtn, 235, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, clearBtn, 215, SpringLayout.NORTH, panel);

        layout.putConstraint(SpringLayout.WEST, searchLabel, 340, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, searchLabel, 23, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, searchField, 410, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, searchField, 20, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, searchBtn, 645, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, searchBtn, 19, SpringLayout.NORTH, panel);

        layout.putConstraint(SpringLayout.WEST, jScrollPaneTable, 340, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, jScrollPaneTable, 55, SpringLayout.NORTH, panel);

        editBtn.setEnabled(false);
        deleteBtn.setEnabled(false);

        this.add(panel);
        this.setSize(910, 420);
        this.setLocationRelativeTo(null);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public boolean showConfirm(String message) {
        return JOptionPane.showConfirmDialog(this, message, "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public void showListNCC(List<NhaCungCap> list) {
        int size = list.size();
        Object[][] data = new Object[size][5];
        for (int i = 0; i < size; i++) {
            data[i][0] = list.get(i).getId();
            data[i][1] = list.get(i).getTenNCC();
            data[i][2] = list.get(i).getSoDienThoai();
            data[i][3] = list.get(i).getDiaChi();
            data[i][4] = list.get(i).getEmail();
        }
        nccTable.setModel(new DefaultTableModel(data, columnNames));
    }

    public void fillNCCFromSelectedRow() {
        int row = nccTable.getSelectedRow();
        if (row >= 0) {
            idField.setText(nccTable.getModel().getValueAt(row, 0).toString());
            tenField.setText(nccTable.getModel().getValueAt(row, 1).toString());
            sdtField.setText(nccTable.getModel().getValueAt(row, 2).toString());
            diaChiField.setText(nccTable.getModel().getValueAt(row, 3).toString());
            Object emailObj = nccTable.getModel().getValueAt(row, 4);
            emailField.setText(emailObj != null ? emailObj.toString() : "");

            editBtn.setEnabled(true);
            deleteBtn.setEnabled(true);
            addBtn.setEnabled(false);
        }
    }

    public void clearNCCInfo() {
        idField.setText("");
        tenField.setText("");
        sdtField.setText("");
        diaChiField.setText("");
        emailField.setText("");
        searchField.setText("");
        editBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        addBtn.setEnabled(true);
        nccTable.clearSelection();
    }

    public void showNCC(NhaCungCap ncc) {
        idField.setText(String.valueOf(ncc.getId()));
        tenField.setText(ncc.getTenNCC());
        sdtField.setText(ncc.getSoDienThoai());
        diaChiField.setText(ncc.getDiaChi());
        emailField.setText(ncc.getEmail());
        editBtn.setEnabled(true);
        deleteBtn.setEnabled(true);
        addBtn.setEnabled(false);
    }

    public NhaCungCap getNCCInfo() {
        String ten = tenField.getText().trim();
        String sdt = sdtField.getText().trim();
        String diaChi = diaChiField.getText().trim();
        String email = emailField.getText().trim();

        if (ten.isEmpty() || sdt.isEmpty() || diaChi.isEmpty()) {
            showMessage("Vui lòng nhập đầy đủ Tên NCC, Số điện thoại và Địa chỉ!");
            return null;
        }
        if (!sdt.matches("^0\\d{9}$")) {
            showMessage("Số điện thoại phải gồm 10 chữ số và bắt đầu bằng số 0!");
            return null;
        }
        if (!email.isEmpty() && !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showMessage("Định dạng Email không hợp lệ!");
            return null;
        }

        NhaCungCap ncc = new NhaCungCap();
        if (!idField.getText().trim().isEmpty()) {
            ncc.setId(Integer.parseInt(idField.getText().trim()));
        }
        ncc.setTenNCC(ten);
        ncc.setSoDienThoai(sdt);
        ncc.setDiaChi(diaChi);
        ncc.setEmail(email);
        return ncc;
    }

    public String getSearchKeyword() {
        return searchField.getText();
    }

    public void addAddNCCListener(ActionListener listener) { addBtn.addActionListener(listener); }
    public void addEditNCCListener(ActionListener listener) { editBtn.addActionListener(listener); }
    public void addDeleteNCCListener(ActionListener listener) { deleteBtn.addActionListener(listener); }
    public void addClearListener(ActionListener listener) { clearBtn.addActionListener(listener); }
    public void addSearchListener(ActionListener listener) { searchBtn.addActionListener(listener); }
    public void addListNCCSelectionListener(ListSelectionListener listener) {
        nccTable.getSelectionModel().addListSelectionListener(listener);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            NhaCungCapView view = new NhaCungCapView();
            NhaCungCapController controller = new NhaCungCapController(view);
            controller.showNhaCungCapView();
        });
    }
}