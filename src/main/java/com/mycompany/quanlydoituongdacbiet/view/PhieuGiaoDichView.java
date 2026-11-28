package com.mycompany.quanlydoituongdacbiet.view;

import com.mycompany.quanlydoituongdacbiet.controller.PhieuGiaoDichController;
import com.mycompany.quanlydoituongdacbiet.entity.HangHoa;
import com.mycompany.quanlydoituongdacbiet.entity.LoHang;
import com.mycompany.quanlydoituongdacbiet.entity.LyDoHuHong;
import com.mycompany.quanlydoituongdacbiet.utils.FormatUtils;
import com.toedter.calendar.JDateChooser;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Giao diện Lập Phiếu Nhập kho / Xuất bán / Báo hư hỏng (Huỷ) và Xuất Bill (Thành viên 4).
 */
public class PhieuGiaoDichView extends JFrame {

    private static final String[] COLUMN_NAMES = {
        "ID", "Mã phiếu", "Mã đơn / Bill", "Loại phiếu", "Mặt hàng",
        "Lô hàng (HSD)", "Đối tác / Lý do", "Ngày GD", "SL", "Đơn giá", "Thành tiền"
    };

    private final PhieuGiaoDichController controller;

    private final JComboBox<String> cboLoaiPhieu = new JComboBox<>(
            new String[]{"Phiếu Nhập", "Phiếu Xuất", "Phiếu Huỷ (Hư hỏng)"});
    private final JTextField txtMaDon = new JTextField(12);
    private final JButton btnDonMoi = new JButton("Đơn mới");
    private final JCheckBox chkGopDon = new JCheckBox("Gộp nhiều món chung Mã đơn / Bill này");

    private final JComboBox<String> cboHangHoa = new JComboBox<>();
    private final JLabel lblLoHang = new JLabel("Chọn lô (*):");
    private final JComboBox<LoHang> cboLoHang = new JComboBox<>();

    private final JLabel lblDoiTac = new JLabel("Nhà cung cấp (*):");
    private final JComboBox<String> cboNhaCungCap = new JComboBox<>();
    private final JTextField txtKhachHang = new JTextField("Khách lẻ", 16);
    private final JComboBox<LyDoHuHong> cboLyDoHuHong = new JComboBox<>(LyDoHuHong.values());

    private final JLabel lblHSD = new JLabel("HSD lô nhập (*):");
    private final JDateChooser dateHSD = new JDateChooser();
    private final JCheckBox chkKhongHSD = new JCheckBox("Không HSD");

    private final JDateChooser dateNgayGD = new JDateChooser();
    private final JTextField txtSoLuong = new JTextField(10);
    private final JLabel lblGoiYTon = new JLabel(" ");
    private final JLabel lblDonGia = new JLabel("Giá nhập (đ) (*):");
    private final JTextField txtDonGia = new JTextField(14);
    private final JTextField txtGhiChu = new JTextField(16);

    private final JButton btnAdd = new JButton("Thêm phiếu");
    private final JButton btnXuatBill = new JButton("Xuất Bill / In");
    private final JButton btnDelete = new JButton("Xoá phiếu");
    private final JButton btnClear = new JButton("Làm mới");

    private final JTextField txtSearch = new JTextField(18);
    private final JComboBox<String> cboLocLoai = new JComboBox<>(
            new String[]{"Tất cả loại phiếu", "Phiếu Nhập", "Phiếu Xuất", "Phiếu Huỷ"});
    private final JButton btnSearch = new JButton("Lọc / Tìm");

    private final DefaultTableModel tableModel;
    private final JTable phieuTable;
    private String maDonVuaChon = null;

    public PhieuGiaoDichView() {
        controller = new PhieuGiaoDichController();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản Lý Nhập / Xuất / Báo Hư Hỏng Kho & Xuất Bill");

        // -------- Cột trái: Form nhập --------
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createTitledBorder("Lập chứng từ kho & Hoá đơn"));
        pnlForm.setPreferredSize(new Dimension(405, 520));

        dateNgayGD.setDateFormatString(FormatUtils.DATE_PATTERN);
        dateNgayGD.setDate(new Date());
        dateHSD.setDateFormatString(FormatUtils.DATE_PATTERN);
        Calendar calMacDinh = Calendar.getInstance();
        calMacDinh.add(Calendar.MONTH, 6);
        dateHSD.setDate(calMacDinh.getTime());

        txtDonGia.setHorizontalAlignment(SwingConstants.RIGHT);
        txtSoLuong.setHorizontalAlignment(SwingConstants.RIGHT);
        lblGoiYTon.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblGoiYTon.setForeground(new Color(0, 95, 160));

        JPanel pnlMaDon = new JPanel(new BorderLayout(4, 0));
        pnlMaDon.add(txtMaDon, BorderLayout.CENTER);
        pnlMaDon.add(btnDonMoi, BorderLayout.EAST);

        JPanel pnlHSD = new JPanel(new BorderLayout(4, 0));
        pnlHSD.add(dateHSD, BorderLayout.CENTER);
        pnlHSD.add(chkKhongHSD, BorderLayout.EAST);

        JPanel pnlDoiTac = new JPanel(new BorderLayout());
        pnlDoiTac.add(cboNhaCungCap, BorderLayout.NORTH);
        pnlDoiTac.add(txtKhachHang, BorderLayout.CENTER);
        pnlDoiTac.add(cboLyDoHuHong, BorderLayout.SOUTH);

        int r = 0;
        themDongForm(pnlForm, r++, new JLabel("Loại phiếu (*):"), cboLoaiPhieu);
        themDongForm(pnlForm, r++, new JLabel("Mã đơn / Bill (*):"), pnlMaDon);
        themDongFull(pnlForm, r++, chkGopDon);
        themDongForm(pnlForm, r++, new JLabel("Mặt hàng (*):"), cboHangHoa);
        themDongForm(pnlForm, r++, lblLoHang, cboLoHang);
        themDongForm(pnlForm, r++, lblDoiTac, pnlDoiTac);
        themDongForm(pnlForm, r++, lblHSD, pnlHSD);
        themDongForm(pnlForm, r++, new JLabel("Ngày GD (*):"), dateNgayGD);
        themDongForm(pnlForm, r++, new JLabel("Số lượng (*):"), txtSoLuong);
        themDongFull(pnlForm, r++, lblGoiYTon);
        themDongForm(pnlForm, r++, lblDonGia, txtDonGia);
        themDongForm(pnlForm, r++, new JLabel("Ghi chú:"), txtGhiChu);

        JPanel pnlLeftButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
        pnlLeftButtons.add(btnAdd);
        pnlLeftButtons.add(btnXuatBill);
        pnlLeftButtons.add(btnDelete);
        pnlLeftButtons.add(btnClear);

        JPanel pnlLeft = new JPanel(new BorderLayout());
        pnlLeft.add(pnlForm, BorderLayout.CENTER);
        pnlLeft.add(pnlLeftButtons, BorderLayout.SOUTH);

        // -------- Cột phải: Thanh tìm kiếm + Bảng --------
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        pnlSearch.add(new JLabel("Tìm kiếm:"));
        pnlSearch.add(txtSearch);
        pnlSearch.add(cboLocLoai);
        pnlSearch.add(btnSearch);

        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int col) {
                return (col == 0 || col == 8) ? Integer.class : String.class;
            }
        };
        phieuTable = new JTable(tableModel);
        phieuTable.setRowHeight(24);
        phieuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        phieuTable.setAutoCreateRowSorter(true);
        phieuTable.getTableHeader().setFont(phieuTable.getTableHeader().getFont().deriveFont(Font.BOLD));

        int[] widths = {35, 68, 78, 82, 165, 130, 145, 76, 45, 85, 95};
        PhieuRenderer renderer = new PhieuRenderer();
        for (int i = 0; i < COLUMN_NAMES.length; i++) {
            phieuTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            phieuTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        JPanel pnlRight = new JPanel(new BorderLayout(0, 4));
        pnlRight.setBorder(BorderFactory.createTitledBorder("Danh sách phiếu giao dịch & chứng từ kho"));
        pnlRight.add(pnlSearch, BorderLayout.NORTH);
        pnlRight.add(new JScrollPane(phieuTable), BorderLayout.CENTER);

        setLayout(new BorderLayout(8, 8));
        add(pnlLeft, BorderLayout.WEST);
        add(pnlRight, BorderLayout.CENTER);

        btnDelete.setEnabled(false);
        btnXuatBill.setEnabled(false);

        ganSuKien();
        loadData();
        capNhatCheDoLoaiPhieu(true);

        setSize(1240, 580);
        setLocationRelativeTo(null);
    }

    private void themDongForm(JPanel p, int row, Component lbl, Component comp) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 8, 4, 8);
        g.gridy = row;
        g.gridx = 0;
        g.anchor = GridBagConstraints.WEST;
        p.add(lbl, g);

        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        p.add(comp, g);
    }

    private void themDongFull(JPanel p, int row, Component comp) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(1, 8, 3, 8);
        g.gridy = row;
        g.gridx = 0;
        g.gridwidth = 2;
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        p.add(comp, g);
    }

    private String getLoaiDangChon() {
        Object sel = cboLoaiPhieu.getSelectedItem();
        if (sel == null) {
            return "Phiếu Nhập";
        }
        String s = sel.toString();
        return s.startsWith("Phiếu Huỷ") ? "Phiếu Huỷ" : s;
    }

    private int getIdHangDangChon() {
        Object sel = cboHangHoa.getSelectedItem();
        if (sel == null || sel.toString().isEmpty()) {
            return -1;
        }
        return Integer.parseInt(sel.toString().split(" - ")[0].trim());
    }

    private void capNhatCheDoLoaiPhieu(boolean doiMaDonMoi) {
        String loai = getLoaiDangChon();
        boolean laNhap = "Phiếu Nhập".equals(loai);
        boolean laXuat = "Phiếu Xuất".equals(loai);
        boolean laHuy = "Phiếu Huỷ".equals(loai);

        lblLoHang.setVisible(!laNhap);
        cboLoHang.setVisible(!laNhap);

        cboNhaCungCap.setVisible(laNhap);
        txtKhachHang.setVisible(laXuat);
        cboLyDoHuHong.setVisible(laHuy);
        lblDoiTac.setText(laNhap ? "Nhà cung cấp (*):" : (laXuat ? "Khách hàng (*):" : "Nguyên nhân (*):"));

        lblHSD.setVisible(laNhap);
        dateHSD.setVisible(laNhap);
        chkKhongHSD.setVisible(laNhap);

        lblDonGia.setText(laNhap ? "Giá nhập (đ) (*):" : (laXuat ? "Giá bán (đ) (*):" : "Giá vốn lô (đ):"));
        txtDonGia.setEditable(!laHuy);

        if (doiMaDonMoi) {
            chkGopDon.setSelected(false);
            txtMaDon.setText(controller.sinhMaDonMoi(loai));
        }
        capNhatTheoMatHangChon();
    }

    private void capNhatTheoMatHangChon() {
        int idHang = getIdHangDangChon();
        String loai = getLoaiDangChon();
        if (idHang <= 0) {
            cboLoHang.removeAllItems();
            lblGoiYTon.setText(" ");
            return;
        }
        HangHoa hh = controller.timHangHoa(idHang);
        int tonKhaDung = controller.getTonKhaDung(idHang);
        int tonThucTe = controller.getTonThucTe(idHang);

        if ("Phiếu Nhập".equals(loai)) {
            lblGoiYTon.setText("Tồn khả dụng hiện tại: " + tonKhaDung + " (Tổng kho: " + tonThucTe + ")");
        } else if ("Phiếu Xuất".equals(loai)) {
            controller.loadLoTheoHangHoa(idHang, true, cboLoHang);
            if (hh != null) {
                txtDonGia.setText(FormatUtils.formatNumber(hh.getDonGia()));
            }
            lblGoiYTon.setText("Tồn có thể xuất (còn hạn): " + tonKhaDung + " — tự động xếp FEFO (HSD gần trước)");
        } else {
            controller.loadLoTheoHangHoa(idHang, false, cboLoHang);
            capNhatGiaVonKhiChonLo();
            lblGoiYTon.setText("Tổng tồn thực tế trong kho (kể cả hết hạn): " + tonThucTe);
        }
    }

    private void capNhatGiaVonKhiChonLo() {
        LoHang lo = (LoHang) cboLoHang.getSelectedItem();
        if ("Phiếu Huỷ".equals(getLoaiDangChon())) {
            if (lo != null) {
                txtDonGia.setText(FormatUtils.formatNumber(lo.getGiaNhap()));
                if (lo.isHetHan()) {
                    cboLyDoHuHong.setSelectedItem(LyDoHuHong.HET_HAN);
                }
            } else {
                txtDonGia.setText("");
            }
        }
    }

    private void ganSuKien() {
        cboLoaiPhieu.addActionListener(e -> capNhatCheDoLoaiPhieu(true));
        btnDonMoi.addActionListener(e -> {
            chkGopDon.setSelected(false);
            txtMaDon.setText(controller.sinhMaDonMoi(getLoaiDangChon()));
        });
        chkKhongHSD.addActionListener(e -> dateHSD.setEnabled(!chkKhongHSD.isSelected()));
        cboHangHoa.addActionListener(e -> capNhatTheoMatHangChon());
        cboLoHang.addActionListener(e -> capNhatGiaVonKhiChonLo());

        phieuTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int viewRow = phieuTable.getSelectedRow();
                if (viewRow >= 0) {
                    int modelRow = phieuTable.convertRowIndexToModel(viewRow);
                    maDonVuaChon = tableModel.getValueAt(modelRow, 2).toString();
                    btnDelete.setEnabled(true);
                    btnXuatBill.setEnabled(true);
                }
            }
        });

        btnAdd.addActionListener(e -> {
            String loai = getLoaiDangChon();
            String maDon = txtMaDon.getText().trim();
            String hangHoa = cboHangHoa.getSelectedItem() != null ? cboHangHoa.getSelectedItem().toString() : "";
            LoHang loChon = (LoHang) cboLoHang.getSelectedItem();
            String ncc = cboNhaCungCap.getSelectedItem() != null ? cboNhaCungCap.getSelectedItem().toString() : "";
            String khach = txtKhachHang.getText();
            LyDoHuHong lyDo = (LyDoHuHong) cboLyDoHuHong.getSelectedItem();

            String maDonThanhCong = controller.lapPhieu(
                    this, loai, maDon, hangHoa, loChon, ncc, khach, lyDo,
                    dateNgayGD.getDate(), dateHSD.getDate(), chkKhongHSD.isSelected(),
                    txtSoLuong.getText(), txtDonGia.getText(), txtGhiChu.getText());

            if (maDonThanhCong != null) {
                maDonVuaChon = maDonThanhCong;
                btnXuatBill.setEnabled(true);
                controller.showListPhieu(phieuTable);
                txtSoLuong.setText("");
                txtGhiChu.setText("");
                if (chkGopDon.isSelected()) {
                    txtMaDon.setText(maDonThanhCong);
                } else {
                    txtMaDon.setText(controller.sinhMaDonMoi(loai));
                }
                capNhatTheoMatHangChon();
            }
        });

        btnXuatBill.addActionListener(e -> controller.moCuaSoXuatBill(this, maDonVuaChon));

        btnDelete.addActionListener(e -> {
            int viewRow = phieuTable.getSelectedRow();
            if (viewRow < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng phiếu cần xoá trên bảng!");
                return;
            }
            int modelRow = phieuTable.convertRowIndexToModel(viewRow);
            int id = (Integer) tableModel.getValueAt(modelRow, 0);
            if (controller.xoaPhieu(this, id)) {
                controller.showListPhieu(phieuTable);
                clearForm();
            }
        });

        btnClear.addActionListener(e -> clearForm());

        ActionListener locAction = e -> apDungBoLocBang();
        btnSearch.addActionListener(locAction);
        txtSearch.addActionListener(locAction);
        cboLocLoai.addActionListener(locAction);
    }

    private void apDungBoLocBang() {
        String keyword = txtSearch.getText().trim();
        String loaiLoc = cboLocLoai.getSelectedIndex() > 0 ? cboLocLoai.getSelectedItem().toString() : null;

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        phieuTable.setRowSorter(sorter);
        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        if (!keyword.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(keyword)));
        }
        if (loaiLoc != null) {
            filters.add(RowFilter.regexFilter("^" + Pattern.quote(loaiLoc) + "$", 3));
        }
        sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
    }

    private void loadData() {
        controller.taiLaiTatCa();
        controller.loadComboBox(cboHangHoa, cboNhaCungCap);
        controller.showListPhieu(phieuTable);
    }

    private void clearForm() {
        controller.taiLaiTatCa();
        controller.loadComboBox(cboHangHoa, cboNhaCungCap);
        controller.showListPhieu(phieuTable);
        txtSoLuong.setText("");
        txtGhiChu.setText("");
        txtSearch.setText("");
        cboLocLoai.setSelectedIndex(0);
        phieuTable.setRowSorter(null);
        phieuTable.setAutoCreateRowSorter(true);
        phieuTable.clearSelection();
        maDonVuaChon = null;
        btnDelete.setEnabled(false);
        btnXuatBill.setEnabled(false);
        chkGopDon.setSelected(false);
        txtMaDon.setText(controller.sinhMaDonMoi(getLoaiDangChon()));
        capNhatTheoMatHangChon();
    }

    private class PhieuRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
            if (col == 9 || col == 10) {
                setHorizontalAlignment(SwingConstants.RIGHT);
            } else if (col <= 3 || col == 7 || col == 8) {
                setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }
            int modelRow = t.convertRowIndexToModel(row);
            String loai = String.valueOf(t.getModel().getValueAt(modelRow, 3));
            if (!isSelected) {
                if ("Phiếu Huỷ".equals(loai)) {
                    setForeground(new Color(180, 0, 0));
                } else if ("Phiếu Xuất".equals(loai)) {
                    setForeground(new Color(0, 105, 45));
                } else {
                    setForeground(new Color(0, 60, 140));
                }
            }
            return this;
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new PhieuGiaoDichView().setVisible(true));
    }
}
