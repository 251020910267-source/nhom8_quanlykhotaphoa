package com.mycompany.quanlydoituongdacbiet.view;

import com.mycompany.quanlydoituongdacbiet.action.ManagerLoHang;
import com.mycompany.quanlydoituongdacbiet.controller.ThongKeController;
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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Giao diện Tìm kiếm & Thống kê báo cáo kho hàng thực tế (Thành viên 5).
 */
public class ThongKeView extends JFrame {

    private static final String[] COLUMNS = {
        "STT", "Mã hàng", "Tên hàng", "Nhóm hàng", "ĐVT",
        "Giá bán lẻ", "Số lô", "Tồn khả dụng", "Giá trị tồn (vốn)", "HSD gần nhất"
    };

    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JTextField txtTuKhoa = new JTextField(16);
    private final JTextField txtGiaTu = new JTextField(9);
    private final JTextField txtGiaDen = new JTextField(9);

    private final JButton btnTimKiem = new JButton("Tìm kiếm");
    private final JButton btnCanhBaoHSD = new JButton("Lọc hàng sắp/đã hết hạn");
    private final JButton btnSapHetTon = new JButton("Lọc hàng sắp hết tồn");
    private final JButton btnLamMoi = new JButton("Hiển thị tất cả & Cập nhật");
    private final JButton btnXuatBaoCao = new JButton("Xuất báo cáo (.html)");

    private final JLabel lblTongMatHang = new JLabel("0");
    private final JLabel lblTongGiaTriTon = new JLabel("0 đ");
    private final JLabel lblGiaCaoNhat = new JLabel("Chưa có dữ liệu");
    private final JLabel lblGiaThapNhat = new JLabel("Chưa có dữ liệu");
    private final JLabel lblNhapXuat = new JLabel("0 đ / 0 đ");
    private final JLabel lblThietHaiHuy = new JLabel("0 đ");
    private final JLabel lblCanhBao = new JLabel("0");
    private final JTextArea txtBaoCaoNhom = new JTextArea(7, 32);

    public ThongKeView() {
        super("Quản lý kho hàng tạp hoá - Tìm kiếm & Thống kê báo cáo");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel title = new JLabel("TÌM KIẾM & THỐNG KÊ BÁO CÁO KHO HÀNG", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JPanel pnlSearch = new JPanel(new GridBagLayout());
        pnlSearch.setBorder(BorderFactory.createTitledBorder("Bộ lọc tìm kiếm đa tiêu chí"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.anchor = GridBagConstraints.WEST;

        txtGiaTu.setHorizontalAlignment(SwingConstants.RIGHT);
        txtGiaDen.setHorizontalAlignment(SwingConstants.RIGHT);

        g.gridx = 0; g.gridy = 0;
        pnlSearch.add(new JLabel("Từ khoá (Mã / Tên / Nhóm / NCC):"), g);
        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1;
        pnlSearch.add(txtTuKhoa, g);

        g.gridx = 2; g.fill = GridBagConstraints.NONE; g.weightx = 0;
        pnlSearch.add(new JLabel("Giá bán từ (đ):"), g);
        g.gridx = 3;
        pnlSearch.add(txtGiaTu, g);

        g.gridx = 4;
        pnlSearch.add(new JLabel("Đến (đ):"), g);
        g.gridx = 5;
        pnlSearch.add(txtGiaDen, g);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        pnlButtons.add(btnTimKiem);
        pnlButtons.add(btnCanhBaoHSD);
        pnlButtons.add(btnSapHetTon);
        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnXuatBaoCao);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.add(title, BorderLayout.NORTH);
        pnlTop.add(pnlSearch, BorderLayout.CENTER);
        pnlTop.add(pnlButtons, BorderLayout.SOUTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int col) {
                if (col == 0 || col == 6 || col == 7) {
                    return Integer.class;
                }
                if (col == 5 || col == 8) {
                    return Long.class;
                }
                return Object.class;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(2).setPreferredWidth(210);

        RowRenderer renderer = new RowRenderer();
        for (int i = 0; i < COLUMNS.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        // Khu vực Báo cáo & Thống kê phía dưới
        JPanel pnlBottom = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlBottom.setBorder(BorderFactory.createTitledBorder("Tổng hợp chỉ số kho hàng thực tế"));

        JPanel pnlStats = new JPanel(new GridLayout(7, 2, 6, 4));
        pnlStats.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        Font boldFont = new Font("SansSerif", Font.BOLD, 12);

        lblTongMatHang.setFont(boldFont);
        lblTongMatHang.setForeground(new Color(0, 102, 204));
        lblTongGiaTriTon.setFont(boldFont);
        lblGiaCaoNhat.setFont(boldFont);
        lblGiaCaoNhat.setForeground(new Color(0, 128, 0));
        lblGiaThapNhat.setFont(boldFont);
        lblNhapXuat.setFont(boldFont);
        lblThietHaiHuy.setFont(boldFont);
        lblThietHaiHuy.setForeground(new Color(175, 0, 0));
        lblCanhBao.setFont(boldFont);
        lblCanhBao.setForeground(Color.RED);

        pnlStats.add(new JLabel("Tổng danh mục & số lô kho:"));
        pnlStats.add(lblTongMatHang);
        pnlStats.add(new JLabel("Tổng giá trị tồn kho (theo vốn):"));
        pnlStats.add(lblTongGiaTriTon);
        pnlStats.add(new JLabel("Mặt hàng giá bán CAO NHẤT:"));
        pnlStats.add(lblGiaCaoNhat);
        pnlStats.add(new JLabel("Mặt hàng giá bán THẤP NHẤT:"));
        pnlStats.add(lblGiaThapNhat);
        pnlStats.add(new JLabel("Tổng tiền Nhập / Doanh thu Xuất:"));
        pnlStats.add(lblNhapXuat);
        pnlStats.add(new JLabel("Tổng thiệt hại hư hỏng / xuất huỷ:"));
        pnlStats.add(lblThietHaiHuy);
        pnlStats.add(new JLabel("Cảnh báo HSD (30 ngày) & Tồn thấp:"));
        pnlStats.add(lblCanhBao);

        txtBaoCaoNhom.setEditable(false);
        txtBaoCaoNhom.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollReport = new JScrollPane(txtBaoCaoNhom);
        scrollReport.setBorder(BorderFactory.createTitledBorder("Phân bổ theo nhóm & Thống kê nguyên nhân hư hỏng"));

        pnlBottom.add(pnlStats);
        pnlBottom.add(scrollReport);

        add(pnlTop, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);

        setSize(1080, 690);
        setLocationRelativeTo(null);
    }

    private class RowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            Object shown = value;
            if ((col == 5 || col == 8) && value instanceof Number) {
                shown = FormatUtils.formatMoney(((Number) value).longValue());
            } else if (col == 9 && value instanceof Date) {
                shown = FormatUtils.formatDate((Date) value);
            } else if (col == 9 && value == null) {
                shown = "—";
            }
            super.getTableCellRendererComponent(t, shown, isSelected, hasFocus, row, col);
            setHorizontalAlignment((col == 5 || col == 8) ? SwingConstants.RIGHT
                    : (col == 0 || col == 4 || col == 6 || col == 7 || col == 9)
                    ? SwingConstants.CENTER : SwingConstants.LEFT);

            int modelRow = t.convertRowIndexToModel(row);
            Object hsd = t.getModel().getValueAt(modelRow, 9);
            int ton = (Integer) t.getModel().getValueAt(modelRow, 7);
            boolean expired = hsd instanceof Date && ((Date) hsd).before(new Date());
            if (!isSelected) {
                setForeground((expired || ton == 0) ? new Color(185, 0, 0) : Color.BLACK);
            }
            return this;
        }
    }

    public void showListHangHoa(List<HangHoa> list, ManagerLoHang qlLo) {
        tableModel.setRowCount(0);
        int stt = 1;
        for (HangHoa h : list) {
            List<LoHang> cacLo = qlLo.getLoCuaHang(h.getId());
            long giaTriTon = 0;
            for (LoHang lo : cacLo) {
                giaTriTon += lo.getGiaTriTon();
            }
            tableModel.addRow(new Object[]{
                stt++,
                h.getMaHang(),
                h.getTenHang(),
                h.getNhomHang(),
                h.getDonViTinh(),
                h.getDonGia(),
                cacLo.size(),
                qlLo.tinhTonKhaDung(h.getId()),
                giaTriTon,
                qlLo.getHanSuDungGanNhat(h.getId())
            });
        }
    }

    public void updateThongKe(int tongMatHang, int tongSoLo, long tongGiaTriTon,
            HangHoa maxGia, HangHoa minGia, long tongNhap, long tongXuat, long tongThietHai,
            int soLoCanhBaoHSD, int soHangSapHetTon,
            Map<String, String> theoNhom, Map<String, long[]> theoLyDoHuy) {

        lblTongMatHang.setText(tongMatHang + " mặt hàng (" + tongSoLo + " lô)");
        lblTongGiaTriTon.setText(FormatUtils.formatMoney(tongGiaTriTon));
        lblGiaCaoNhat.setText(maxGia != null
                ? maxGia.getTenHang() + " (" + FormatUtils.formatMoney(maxGia.getDonGia()) + ")"
                : "Không có dữ liệu");
        lblGiaThapNhat.setText(minGia != null
                ? minGia.getTenHang() + " (" + FormatUtils.formatMoney(minGia.getDonGia()) + ")"
                : "Không có dữ liệu");
        lblNhapXuat.setText(FormatUtils.formatMoney(tongNhap) + "  /  " + FormatUtils.formatMoney(tongXuat));
        lblThietHaiHuy.setText(FormatUtils.formatMoney(tongThietHai));
        lblCanhBao.setText(soLoCanhBaoHSD + " lô sắp/quá HSD  |  " + soHangSapHetTon + " mặt hàng sắp hết tồn");

        StringBuilder sb = new StringBuilder();
        sb.append("=== PHÂN BỔ THEO NHÓM HÀNG ===\n");
        if (theoNhom.isEmpty()) {
            sb.append("Chưa có dữ liệu nhóm hàng.\n");
        } else {
            for (Map.Entry<String, String> entry : theoNhom.entrySet()) {
                sb.append(String.format("• %-18s : %s\n", entry.getKey(), entry.getValue()));
            }
        }
        sb.append("\n=== NGUYÊN NHÂN HƯ HỎNG / HUỶ KHO ===\n");
        if (theoLyDoHuy.isEmpty()) {
            sb.append("Chưa phát sinh hàng hư hỏng.\n");
        } else {
            for (Map.Entry<String, long[]> e : theoLyDoHuy.entrySet()) {
                sb.append(String.format("• %s: SL %d (-%s)\n",
                        e.getKey(), e.getValue()[0], FormatUtils.formatMoney(e.getValue()[1])));
            }
        }
        txtBaoCaoNhom.setText(sb.toString());
        txtBaoCaoNhom.setCaretPosition(0);
    }

    public String getTuKhoa() { return txtTuKhoa.getText(); }
    public String getGiaTuText() { return txtGiaTu.getText(); }
    public String getGiaDenText() { return txtGiaDen.getText(); }

    public void clearSearchInputs() {
        txtTuKhoa.setText("");
        txtGiaTu.setText("");
        txtGiaDen.setText("");
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public void addTimKiemListener(ActionListener l) {
        btnTimKiem.addActionListener(l);
        txtTuKhoa.addActionListener(l);
    }
    public void addCanhBaoListener(ActionListener l) { btnCanhBaoHSD.addActionListener(l); }
    public void addSapHetTonListener(ActionListener l) { btnSapHetTon.addActionListener(l); }
    public void addLamMoiListener(ActionListener l) { btnLamMoi.addActionListener(l); }
    public void addXuatBaoCaoListener(ActionListener l) { btnXuatBaoCao.addActionListener(l); }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            ThongKeView view = new ThongKeView();
            ThongKeController controller = new ThongKeController(view);
            controller.showThongKeView();
        });
    }
}
