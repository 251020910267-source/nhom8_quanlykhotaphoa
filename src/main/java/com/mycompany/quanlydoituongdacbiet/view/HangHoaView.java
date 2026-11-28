package com.mycompany.quanlydoituongdacbiet.view;

import com.mycompany.quanlydoituongdacbiet.action.ManagerLoHang;
import com.mycompany.quanlydoituongdacbiet.entity.HangHoa;
import com.mycompany.quanlydoituongdacbiet.entity.LoHang;
import com.mycompany.quanlydoituongdacbiet.utils.FormatUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Giao diện Quản lý Danh mục Hàng hoá (Thành viên 2).
 * Tách rõ thông tin chung của sản phẩm với thông tin từng Lô hàng (HSD, NCC, giá nhập),
 * đồng thời hiển thị tổng hợp Tồn khả dụng và HSD gần nhất ngay trên bảng.
 */
public class HangHoaView extends JFrame {

    private static final String[] COLUMNS = {
        "ID", "Mã hàng", "Tên hàng", "Nhóm hàng", "ĐVT",
        "Giá bán lẻ", "Tồn khả dụng", "HSD gần nhất", "Tình trạng kho"
    };
    private static final String[] NHOM_HANG = {
        "Đồ uống", "Sữa", "Bánh kẹo", "Mì - Miến - Cháo",
        "Gia vị", "Đồ khô", "Đồ dùng gia đình", "Khác"
    };
    private static final String[] DON_VI_TINH = {
        "Thùng", "Lốc", "Hộp", "Gói", "Chai", "Lon", "Túi", "Kg", "Cái"
    };

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField txtMa = new JTextField(15);
    private final JTextField txtTen = new JTextField(15);
    private final JComboBox<String> cboNhom = new JComboBox<>(NHOM_HANG);
    private final JComboBox<String> cboDVT = new JComboBox<>(DON_VI_TINH);
    private final JTextField txtDonGia = new JTextField(15);
    private final JTextField txtTonToiThieu = new JTextField("5", 15);

    private final JButton btnAdd = new JButton("Thêm");
    private final JButton btnEdit = new JButton("Sửa");
    private final JButton btnDelete = new JButton("Xoá");
    private final JButton btnClear = new JButton("Làm mới");
    private final JButton btnXemLo = new JButton("Xem các lô của mặt hàng");

    private int selectedId = -1;

    public HangHoaView() {
        super("Quản lý kho hàng cửa hàng tạp hoá - Danh mục Hàng hoá");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 6) {
                    return Integer.class;
                }
                if (columnIndex == 5) {
                    return Long.class;
                }
                return Object.class;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));
        table.getColumnModel().getColumn(0).setPreferredWidth(42);
        table.getColumnModel().getColumn(1).setPreferredWidth(85);
        table.getColumnModel().getColumn(2).setPreferredWidth(210);
        table.getColumnModel().getColumn(5).setPreferredWidth(105);
        table.getColumnModel().getColumn(6).setPreferredWidth(85);
        table.getColumnModel().getColumn(7).setPreferredWidth(95);
        table.getColumnModel().getColumn(8).setPreferredWidth(155);

        RowRenderer renderer = new RowRenderer();
        for (int i = 0; i < COLUMNS.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        // ----- Form nhập -----
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Thông tin danh mục mặt hàng"));
        cboNhom.setEditable(true);
        cboDVT.setEditable(true);
        txtDonGia.setHorizontalAlignment(SwingConstants.RIGHT);
        txtTonToiThieu.setHorizontalAlignment(SwingConstants.RIGHT);

        addRow(form, 0, "Mã hàng / Mã vạch (*):", txtMa, "Tên hàng hoá (*):", txtTen);
        addRow(form, 1, "Nhóm hàng (*):", cboNhom, "Đơn vị tính (*):", cboDVT);
        addRow(form, 2, "Giá bán lẻ (đ) (*):", txtDonGia, "Mức tồn tối thiểu:", txtTonToiThieu);

        JLabel lblGhiChu = new JLabel(
                "(*) Hạn sử dụng, Nhà cung cấp và Giá vốn nhập được quản lý riêng theo từng Lô hàng khi lập Phiếu Nhập kho.");
        lblGhiChu.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblGhiChu.setForeground(new Color(0, 90, 160));
        lblGhiChu.setBorder(BorderFactory.createEmptyBorder(2, 12, 4, 12));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        buttons.add(btnAdd);
        buttons.add(btnEdit);
        buttons.add(btnDelete);
        buttons.add(btnClear);
        buttons.add(btnXemLo);

        JPanel centerTop = new JPanel(new BorderLayout());
        centerTop.add(form, BorderLayout.CENTER);
        centerTop.add(lblGhiChu, BorderLayout.SOUTH);

        JPanel top = new JPanel(new BorderLayout());
        top.add(centerTop, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        JLabel title = new JLabel("QUẢN LÝ DANH MỤC HÀNG HOÁ", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JPanel north = new JPanel(new BorderLayout());
        north.add(title, BorderLayout.NORTH);
        north.add(top, BorderLayout.CENTER);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        setSize(980, 620);
        setLocationRelativeTo(null);
        clearForm();
    }

    private void addRow(JPanel p, int row, String l1, Component c1, String l2, Component c2) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 8, 4, 8);
        g.anchor = GridBagConstraints.WEST;
        g.gridy = row;
        g.gridx = 0; p.add(new JLabel(l1), g);
        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1; p.add(c1, g);
        g.gridx = 2; g.fill = GridBagConstraints.NONE; g.weightx = 0; p.add(new JLabel(l2), g);
        g.gridx = 3; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1; p.add(c2, g);
    }

    private class RowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            Object shown = value;
            if (col == 5 && value instanceof Number) {
                shown = FormatUtils.formatMoney(((Number) value).longValue());
            } else if (col == 7 && value instanceof Date) {
                shown = FormatUtils.formatDate((Date) value);
            } else if (col == 7 && value == null) {
                shown = "—";
            }
            super.getTableCellRendererComponent(t, shown, isSelected, hasFocus, row, col);
            setHorizontalAlignment(col == 5 ? SwingConstants.RIGHT
                    : (col == 0 || col == 4 || col == 6 || col == 7) ? SwingConstants.CENTER : SwingConstants.LEFT);

            int modelRow = t.convertRowIndexToModel(row);
            String tinhTrang = String.valueOf(t.getModel().getValueAt(modelRow, 8));
            if (!isSelected) {
                if (tinhTrang.contains("Có lô hết hạn") || tinhTrang.equals("Hết hàng")) {
                    setForeground(new Color(190, 0, 0));
                } else if (tinhTrang.contains("Sắp hết")) {
                    setForeground(new Color(185, 95, 0));
                } else {
                    setForeground(Color.BLACK);
                }
            }
            return this;
        }
    }

    // ---------- Hiển thị danh sách mặt hàng kèm tổng hợp từ các lô ----------
    public void showListHangHoa(List<HangHoa> list, ManagerLoHang qlLo) {
        tableModel.setRowCount(0);
        for (HangHoa h : list) {
            int tonKhaDung = qlLo.tinhTonKhaDung(h.getId());
            int tonThucTe = qlLo.tinhTonKho(h.getId());
            Date hsdGanNhat = qlLo.getHanSuDungGanNhat(h.getId());
            List<LoHang> cacLo = qlLo.getLoCuaHang(h.getId());
            int soLoDangCon = 0;
            boolean coLoHetHan = false;
            boolean coLoSapHetHan = false;
            for (LoHang lo : cacLo) {
                if (lo.getSoLuongCon() > 0) {
                    soLoDangCon++;
                    if (lo.isHetHan()) {
                        coLoHetHan = true;
                    } else if (lo.isSapHetHan(LoHang.SO_NGAY_CANH_BAO)) {
                        coLoSapHetHan = true;
                    }
                }
            }
            String tinhTrang;
            if (coLoHetHan) {
                tinhTrang = "Có lô hết hạn (" + tonThucTe + ")";
            } else if (tonKhaDung == 0) {
                tinhTrang = cacLo.isEmpty() ? "Chưa nhập lô nào" : "Hết hàng";
            } else if (coLoSapHetHan) {
                tinhTrang = "Sắp hết hạn (" + soLoDangCon + " lô)";
            } else if (tonKhaDung <= h.getTonToiThieu()) {
                tinhTrang = "Sắp hết tồn (<= " + h.getTonToiThieu() + ")";
            } else {
                tinhTrang = "Ổn định (" + soLoDangCon + " lô)";
            }

            tableModel.addRow(new Object[]{
                h.getId(), h.getMaHang(), h.getTenHang(), h.getNhomHang(),
                h.getDonViTinh(), h.getDonGia(), tonKhaDung, hsdGanNhat, tinhTrang
            });
        }
    }

    public void fillForm(HangHoa h) {
        selectedId = h.getId();
        txtMa.setText(h.getMaHang());
        txtTen.setText(h.getTenHang());
        cboNhom.setSelectedItem(h.getNhomHang());
        cboDVT.setSelectedItem(h.getDonViTinh());
        txtDonGia.setText(FormatUtils.formatNumber(h.getDonGia()));
        txtTonToiThieu.setText(String.valueOf(h.getTonToiThieu()));
        btnAdd.setEnabled(false);
        btnEdit.setEnabled(true);
        btnDelete.setEnabled(true);
        btnXemLo.setEnabled(true);
    }

    public void clearForm() {
        selectedId = -1;
        txtMa.setText("");
        txtTen.setText("");
        cboNhom.setSelectedIndex(0);
        cboDVT.setSelectedIndex(0);
        txtDonGia.setText("");
        txtTonToiThieu.setText("5");
        table.clearSelection();
        btnAdd.setEnabled(true);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        btnXemLo.setEnabled(false);
    }

    public HangHoa getHangHoaInfo() {
        long donGia;
        try {
            donGia = FormatUtils.parseMoney(txtDonGia.getText());
        } catch (NumberFormatException ex) {
            showMessage("Giá bán lẻ phải là số nguyên dương (ví dụ: 35000 hoặc 35.000).");
            return null;
        }
        int tonToiThieu;
        try {
            String strTon = txtTonToiThieu.getText().trim();
            tonToiThieu = strTon.isEmpty() ? 0 : Integer.parseInt(strTon);
            if (tonToiThieu < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            showMessage("Mức tồn tối thiểu phải là số nguyên >= 0.");
            return null;
        }
        Object nhom = cboNhom.getEditor().getItem();
        Object dvt = cboDVT.getEditor().getItem();
        return new HangHoa(
                selectedId,
                txtMa.getText(),
                txtTen.getText(),
                nhom == null ? "" : nhom.toString().trim(),
                dvt == null ? "" : dvt.toString().trim(),
                donGia,
                tonToiThieu
        );
    }

    public int getSelectedId() { return selectedId; }

    public int getSelectedModelId() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return -1;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        return (Integer) tableModel.getValueAt(modelRow, 0);
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public boolean confirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public void addAddListener(ActionListener l) { btnAdd.addActionListener(l); }
    public void addEditListener(ActionListener l) { btnEdit.addActionListener(l); }
    public void addDeleteListener(ActionListener l) { btnDelete.addActionListener(l); }
    public void addClearListener(ActionListener l) { btnClear.addActionListener(l); }
    public void addXemLoListener(ActionListener l) { btnXemLo.addActionListener(l); }
    public void addTableSelectionListener(ListSelectionListener l) {
        table.getSelectionModel().addListSelectionListener(l);
    }
}
