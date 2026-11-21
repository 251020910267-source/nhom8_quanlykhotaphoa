package com.mycompany.quanlydoituongdacbiet.view;

import com.mycompany.quanlydoituongdacbiet.controller.PhieuGiaoDichController;
import com.toedter.calendar.JDateChooser;
import java.awt.Dimension;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class PhieuGiaoDichView extends JFrame {
    private JButton addBtn, deleteBtn, clearBtn, searchBtn;
    private JScrollPane jScrollPaneTable;
    private JTable phieuTable;
    private JLabel idLabel, loaiPhieuLabel, hangHoaLabel, doiTacLabel, ngayGDLabel, soLuongLabel, donGiaLabel, searchLabel;
    private JTextField idField, khachHangField, soLuongField, donGiaField, searchField;
    private JComboBox<String> cboLoaiPhieu, cboHangHoa, cboNhaCungCap;
    private JDateChooser dateChooserNgayGD;

    private final PhieuGiaoDichController controller;
    private final String[] columnNames = new String[]{
        "ID", "Loại phiếu", "Hàng hóa", "Đối tác", "Ngày GD", "Số lượng", "Đơn giá", "Thành tiền"
    };

    public PhieuGiaoDichView() {
        controller = new PhieuGiaoDichController();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản Lý Phiếu Nhập / Xuất Kho");

        idLabel = new JLabel("ID:");
        loaiPhieuLabel = new JLabel("Loại phiếu (*):");
        hangHoaLabel = new JLabel("Hàng hóa (*):");
        doiTacLabel = new JLabel("Nhà cung cấp (*):");
        ngayGDLabel = new JLabel("Ngày GD (*):");
        soLuongLabel = new JLabel("Số lượng (*):");
        donGiaLabel = new JLabel("Đơn giá (*):");
        searchLabel = new JLabel("Tìm kiếm:");

        idField = new JTextField(6);
        idField.setEditable(false);

        cboLoaiPhieu = new JComboBox<>(new String[]{"Phiếu Nhập", "Phiếu Xuất"});
        cboLoaiPhieu.setPreferredSize(new Dimension(200, 25));

        cboHangHoa = new JComboBox<>();
        cboHangHoa.setPreferredSize(new Dimension(200, 25));

        cboNhaCungCap = new JComboBox<>();
        cboNhaCungCap.setPreferredSize(new Dimension(200, 25));

        khachHangField = new JTextField(18);
        khachHangField.setVisible(false);

        dateChooserNgayGD = new JDateChooser();
        dateChooserNgayGD.setDateFormatString("dd/MM/yyyy");
        dateChooserNgayGD.setDate(new Date());
        dateChooserNgayGD.setPreferredSize(new Dimension(200, 25));

        soLuongField = new JTextField(18);
        donGiaField = new JTextField(18);
        searchField = new JTextField(20);

        addBtn = new JButton("Thêm phiếu");
        deleteBtn = new JButton("Xoá");
        clearBtn = new JButton("Làm mới");
        searchBtn = new JButton("Tìm");

        jScrollPaneTable = new JScrollPane();
        phieuTable = new JTable();
        phieuTable.setModel(new DefaultTableModel(new Object[][]{}, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        jScrollPaneTable.setViewportView(phieuTable);
        jScrollPaneTable.setPreferredSize(new Dimension(650, 340));

        SpringLayout layout = new SpringLayout();
        JPanel panel = new JPanel();
        panel.setLayout(layout);

        panel.add(jScrollPaneTable);
        panel.add(idLabel); panel.add(idField);
        panel.add(loaiPhieuLabel); panel.add(cboLoaiPhieu);
        panel.add(hangHoaLabel); panel.add(cboHangHoa);
        panel.add(doiTacLabel); panel.add(cboNhaCungCap); panel.add(khachHangField);
        panel.add(ngayGDLabel); panel.add(dateChooserNgayGD);
        panel.add(soLuongLabel); panel.add(soLuongField);
        panel.add(donGiaLabel); panel.add(donGiaField);
        panel.add(addBtn); panel.add(deleteBtn); panel.add(clearBtn);
        panel.add(searchLabel); panel.add(searchField); panel.add(searchBtn);

        // Cột bên trái (Form nhập liệu)
        layout.putConstraint(SpringLayout.WEST, idLabel, 20, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, idLabel, 20, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, idField, 130, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, idField, 18, SpringLayout.NORTH, panel);

        layout.putConstraint(SpringLayout.WEST, loaiPhieuLabel, 20, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, loaiPhieuLabel, 55, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, cboLoaiPhieu, 130, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, cboLoaiPhieu, 52, SpringLayout.NORTH, panel);

        layout.putConstraint(SpringLayout.WEST, hangHoaLabel, 20, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, hangHoaLabel, 90, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, cboHangHoa, 130, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, cboHangHoa, 87, SpringLayout.NORTH, panel);

        layout.putConstraint(SpringLayout.WEST, doiTacLabel, 20, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, doiTacLabel, 125, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, cboNhaCungCap, 130, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, cboNhaCungCap, 122, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, khachHangField, 130, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, khachHangField, 122, SpringLayout.NORTH, panel);

        layout.putConstraint(SpringLayout.WEST, ngayGDLabel, 20, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, ngayGDLabel, 160, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, dateChooserNgayGD, 130, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, dateChooserNgayGD, 157, SpringLayout.NORTH, panel);

        layout.putConstraint(SpringLayout.WEST, soLuongLabel, 20, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, soLuongLabel, 195, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, soLuongField, 130, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, soLuongField, 192, SpringLayout.NORTH, panel);

        layout.putConstraint(SpringLayout.WEST, donGiaLabel, 20, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, donGiaLabel, 230, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, donGiaField, 130, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, donGiaField, 227, SpringLayout.NORTH, panel);

        // Các nút bấm
        layout.putConstraint(SpringLayout.WEST, addBtn, 20, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, addBtn, 280, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, deleteBtn, 135, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, deleteBtn, 280, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, clearBtn, 210, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, clearBtn, 280, SpringLayout.NORTH, panel);

        // Cột bên phải (Tìm kiếm & Bảng)
        layout.putConstraint(SpringLayout.WEST, searchLabel, 360, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, searchLabel, 23, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, searchField, 430, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, searchField, 20, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, searchBtn, 665, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, searchBtn, 19, SpringLayout.NORTH, panel);

        layout.putConstraint(SpringLayout.WEST, jScrollPaneTable, 360, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, jScrollPaneTable, 55, SpringLayout.NORTH, panel);

        deleteBtn.setEnabled(false);

        // Sự kiện đổi loại phiếu (Nhập -> hiện Nhà cung cấp, Xuất -> hiện Khách hàng)
        cboLoaiPhieu.addActionListener(e -> {
            boolean isNhap = "Phiếu Nhập".equals(cboLoaiPhieu.getSelectedItem());
            doiTacLabel.setText(isNhap ? "Nhà cung cấp (*):" : "Khách hàng (*):");
            cboNhaCungCap.setVisible(isNhap);
            khachHangField.setVisible(!isNhap);
        });

        // Sự kiện chọn dòng trên bảng
        phieuTable.getSelectionModel().addListSelectionListener(e -> {
            int row = phieuTable.getSelectedRow();
            if (row >= 0) {
                int modelRow = phieuTable.convertRowIndexToModel(row);
                idField.setText(phieuTable.getModel().getValueAt(modelRow, 0).toString());
                deleteBtn.setEnabled(true);
            }
        });

        // Nút Thêm phiếu
        addBtn.addActionListener(e -> {
            String loaiPhieu = cboLoaiPhieu.getSelectedItem().toString();
            String hangHoa = cboHangHoa.getSelectedItem() != null ? cboHangHoa.getSelectedItem().toString() : "";
            String ncc = cboNhaCungCap.getSelectedItem() != null ? cboNhaCungCap.getSelectedItem().toString() : "";
            String khachHang = khachHangField.getText();
            Date ngayGD = dateChooserNgayGD.getDate();
            String soLuong = soLuongField.getText();
            String donGia = donGiaField.getText();

            if (controller.themPhieu(loaiPhieu, hangHoa, ncc, khachHang, ngayGD, soLuong, donGia)) {
                controller.showListPhieu(phieuTable);
                clearForm();
            }
        });

        // Nút Xóa phiếu
        deleteBtn.addActionListener(e -> {
            int row = phieuTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu cần xóa trên bảng!");
                return;
            }
            int modelRow = phieuTable.convertRowIndexToModel(row);
            int id = Integer.parseInt(phieuTable.getModel().getValueAt(modelRow, 0).toString());
            if (controller.xoaPhieu(id)) {
                controller.showListPhieu(phieuTable);
                clearForm();
            }
        });

        // Nút Làm mới
        clearBtn.addActionListener(e -> clearForm());

        // Nút Tìm kiếm nhanh trên bảng
        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            DefaultTableModel model = (DefaultTableModel) phieuTable.getModel();
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
            phieuTable.setRowSorter(sorter);
            if (keyword.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + keyword));
            }
        });

        this.add(panel);
        this.setSize(1040, 450);
        this.setLocationRelativeTo(null);
    }

    private void loadData() {
        controller.loadComboBox(cboHangHoa, cboNhaCungCap);
        controller.showListPhieu(phieuTable);
    }

    private void clearForm() {
        idField.setText("");
        khachHangField.setText("");
        soLuongField.setText("");
        donGiaField.setText("");
        searchField.setText("");
        dateChooserNgayGD.setDate(new Date());
        phieuTable.setRowSorter(null);
        phieuTable.clearSelection();
        deleteBtn.setEnabled(false);
        controller.loadComboBox(cboHangHoa, cboNhaCungCap);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new PhieuGiaoDichView().setVisible(true);
        });
    }
}