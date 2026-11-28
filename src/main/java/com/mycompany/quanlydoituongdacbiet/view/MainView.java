package com.mycompany.quanlydoituongdacbiet.view;

import com.mycompany.quanlydoituongdacbiet.utils.FileUtils;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * Màn hình điều khiển chính của hệ thống (Nhóm trưởng).
 * Bổ sung phân hệ Quản lý Lô & Tồn kho thực tế và thanh cảnh báo tự động theo ngày.
 */
public class MainView extends JFrame {

    private JButton btnChooseResidents;
    private JButton btnNhaCungCap;
    private JButton btnPhieuGiaoDich;
    private JButton btnTonKhoLo;
    private JButton btnThongKe;
    private JLabel jLabel4;
    private JLabel lblCanhBaoNhanh;
    private JLabel jLabel9;

    public MainView() {
        initComponents();
        setTitle("Hệ thống Quản lý Kho hàng Cửa hàng Tạp hoá - Nhóm 8");
        setLocationRelativeTo(null);
    }

    private JButton taoNutChucNang(String nhan) {
        JButton btn = new JButton(nhan);
        btn.setBackground(new Color(0, 0, 102));
        btn.setFont(new Font("Times New Roman", Font.PLAIN, 30));
        btn.setForeground(Color.WHITE);
        btn.setBorder(null);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        return btn;
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(980, 680));

        jLabel4 = new JLabel("Quản lý kho hàng cửa hàng tạp hoá", SwingConstants.CENTER);
        jLabel4.setFont(new Font("Times New Roman", Font.BOLD, 36));
        jLabel4.setForeground(new Color(35, 35, 35));

        lblCanhBaoNhanh = new JLabel(" ", SwingConstants.CENTER);
        lblCanhBaoNhanh.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblCanhBaoNhanh.setForeground(new Color(185, 35, 0));

        btnChooseResidents = taoNutChucNang("Quản Lý Hàng hoá");
        btnNhaCungCap = taoNutChucNang("Quản Lý Nhà cung cấp");
        btnPhieuGiaoDich = taoNutChucNang("Nhập / Xuất / Huỷ & Bill");
        btnTonKhoLo = taoNutChucNang("Quản Lý Lô & Tồn Kho");
        btnThongKe = taoNutChucNang("Tìm kiếm & Thống kê");

        jLabel9 = new JLabel();
        jLabel9.setIcon(FileUtils.loadViewImage("Lovepik_com-500330964-blue-blazed-background.jpg"));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 810, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCanhBaoNhanh, javax.swing.GroupLayout.PREFERRED_SIZE, 810, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnChooseResidents, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                            .addComponent(btnPhieuGiaoDich, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE))
                        .addGap(40, 40, 40)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnNhaCungCap, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                            .addComponent(btnTonKhoLo, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)))
                    .addComponent(btnThongKe, javax.swing.GroupLayout.PREFERRED_SIZE, 810, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(lblCanhBaoNhanh, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnChooseResidents, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnNhaCungCap, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnPhieuGiaoDich, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnTonKhoLo, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addComponent(btnThongKe, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(45, Short.MAX_VALUE))
        );

        pack();
    }

    public void setCanhBaoNhanh(String text, boolean nguyHiem) {
        lblCanhBaoNhanh.setText(text);
        lblCanhBaoNhanh.setForeground(nguyHiem ? new Color(190, 20, 0) : new Color(0, 125, 45));
    }

    public void addChooseSpecialPersonListener(ActionListener listener) {
        btnNhaCungCap.addActionListener(listener);
    }

    public void addChooseResidentsListener(ActionListener listener) {
        btnChooseResidents.addActionListener(listener);
    }

    public void addChooseThongKeListener(ActionListener listener) {
        btnThongKe.addActionListener(listener);
    }

    public void addChoosePhieuGiaoDichListener(ActionListener listener) {
        btnPhieuGiaoDich.addActionListener(listener);
    }

    public void addChooseTonKhoLoListener(ActionListener listener) {
        btnTonKhoLo.addActionListener(listener);
    }
}
