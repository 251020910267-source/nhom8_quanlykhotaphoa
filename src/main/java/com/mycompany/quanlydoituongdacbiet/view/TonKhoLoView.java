package com.mycompany.quanlydoituongdacbiet.view;

import com.mycompany.quanlydoituongdacbiet.action.ManagerHangHoa;
import com.mycompany.quanlydoituongdacbiet.action.ManagerLoHang;
import com.mycompany.quanlydoituongdacbiet.action.ManagerNhaCungCap;
import com.mycompany.quanlydoituongdacbiet.action.ManagerPhieuGiaoDich;
import com.mycompany.quanlydoituongdacbiet.entity.HangHoa;
import com.mycompany.quanlydoituongdacbiet.entity.LoHang;
import com.mycompany.quanlydoituongdacbiet.entity.LyDoHuHong;
import com.mycompany.quanlydoituongdacbiet.entity.NhaCungCap;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuHuy;
import com.mycompany.quanlydoituongdacbiet.entity.TrangThaiLoHang;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Màn hình Quản lý Tồn kho & Trạng thái từng Lô hàng thực tế.
 * Cho phép đối chiếu rõ:
 * - Cùng 1 mặt hàng nhập từ nhiều Nhà cung cấp khác nhau -> Mã lô khác nhau, Mã đơn nhập khác nhau, HSD khác nhau.
 * - Vòng đời từng lô: Đang tồn kho, Sắp hết hạn, Đã hết hạn, Đã xuất kho hết, Bị hư hỏng (chuột, thời tiết, vỡ...).
 * - Báo hư hỏng / Xuất huỷ trực tiếp từ lô đang chọn và in Biên bản huỷ.
 */
public class TonKhoLoView extends JFrame {

    private static final String[] COLUMNS = {
        "ID", "Mã lô", "Mã đơn nhập", "Mặt hàng", "Nhà cung cấp",
        "Ngày nhập", "Hạn sử dụng", "Giá nhập",
        "SL Nhập", "Đã xuất", "Hư hỏng", "Còn tồn", "Trạng thái lô", "Chi tiết hư hỏng"
    };

    private final ManagerPhieuGiaoDich qlPhieu = new ManagerPhieuGiaoDich();
    private final ManagerHangHoa qlHang = new ManagerHangHoa();
    private final ManagerNhaCungCap qlNCC = new ManagerNhaCungCap();

    private final JComboBox<String> cboLocHang = new JComboBox<>();
    private final JComboBox<String> cboLocNCC = new JComboBox<>();
    private final JComboBox<String> cboLocTrangThai = new JComboBox<>();

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel lblTomTat = new JLabel(" ");

    private final JButton btnBaoHuHong = new JButton("Báo hư hỏng / Huỷ hàng lô đang chọn");
    private final JButton btnXemBillNhap = new JButton("Xem / Xuất Bill đơn nhập của lô");
    private final JButton btnLamMoi = new JButton("Làm mới dữ liệu");

    public TonKhoLoView() {
        this(-1);
    }

    public TonKhoLoView(int idHangHoaMacDinh) {
        super("Quản lý Tồn kho & Trạng thái Lô hàng thực tế");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel title = new JLabel("QUẢN LÝ LÔ HÀNG, HẠN SỬ DỤNG & TRẠNG THÁI KHO", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(8, 0, 6, 0));

        // Bộ lọc
        JPanel pnlFilter = new JPanel(new GridBagLayout());
        pnlFilter.setBorder(BorderFactory.createTitledBorder("Bộ lọc lô hàng trong kho"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0; pnlFilter.add(new JLabel("Mặt hàng:"), g);
        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1; pnlFilter.add(cboLocHang, g);
        g.gridx = 2; g.fill = GridBagConstraints.NONE; g.weightx = 0; pnlFilter.add(new JLabel("Nhà cung cấp:"), g);
        g.gridx = 3; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1; pnlFilter.add(cboLocNCC, g);
        g.gridx = 4; g.fill = GridBagConstraints.NONE; g.weightx = 0; pnlFilter.add(new JLabel("Trạng thái:"), g);
        g.gridx = 5; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 0.8; pnlFilter.add(cboLocTrangThai, g);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.add(title, BorderLayout.NORTH);
        pnlTop.add(pnlFilter, BorderLayout.CENTER);

        // Bảng lô hàng
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int col) {
                if (col == 0 || (col >= 8 && col <= 11)) {
                    return Integer.class;
                }
                if (col == 7) {
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

        int[] widths = {36, 72, 85, 190, 155, 82, 82, 92, 60, 60, 62, 62, 150, 180};
        LoRenderer renderer = new LoRenderer();
        for (int i = 0; i < COLUMNS.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        // Thanh tác vụ dưới cùng
        lblTomTat.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTomTat.setForeground(new Color(0, 80, 150));
        lblTomTat.setBorder(BorderFactory.createEmptyBorder(4, 12, 2, 12));

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        pnlButtons.add(btnBaoHuHong);
        pnlButtons.add(btnXemBillNhap);
        pnlButtons.add(btnLamMoi);

        JPanel pnlSouth = new JPanel(new BorderLayout());
        pnlSouth.add(lblTomTat, BorderLayout.NORTH);
        pnlSouth.add(pnlButtons, BorderLayout.SOUTH);

        add(pnlTop, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(pnlSouth, BorderLayout.SOUTH);

        btnBaoHuHong.setEnabled(false);
        btnXemBillNhap.setEnabled(false);

        // Sự kiện
        cboLocHang.addActionListener(e -> hienThiBang());
        cboLocNCC.addActionListener(e -> hienThiBang());
        cboLocTrangThai.addActionListener(e -> hienThiBang());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                LoHang lo = getLoDangChon();
                btnBaoHuHong.setEnabled(lo != null && lo.getSoLuongCon() > 0);
                btnXemBillNhap.setEnabled(lo != null);
            }
        });

        btnBaoHuHong.addActionListener(e -> xuLyBaoHuHong());
        btnXemBillNhap.addActionListener(e -> xuLyXemBillNhap());
        btnLamMoi.addActionListener(e -> napBoLocVaBang(-1));

        napBoLocVaBang(idHangHoaMacDinh);
        setSize(1180, 620);
        setLocationRelativeTo(null);
    }

    private void napBoLocVaBang(int idHangChonSan) {
        qlPhieu.taiLai();
        qlHang.taiLai();
        qlNCC.taiLai();

        cboLocHang.removeAllItems();
        cboLocHang.addItem("-- Tất cả mặt hàng --");
        int idxChon = 0;
        List<HangHoa> dsHang = qlHang.getList();
        for (int i = 0; i < dsHang.size(); i++) {
            HangHoa h = dsHang.get(i);
            cboLocHang.addItem(h.getId() + " - " + h.getMaHang() + " - " + h.getTenHang());
            if (h.getId() == idHangChonSan) {
                idxChon = i + 1;
            }
        }

        cboLocNCC.removeAllItems();
        cboLocNCC.addItem("-- Tất cả nhà cung cấp --");
        for (NhaCungCap ncc : qlNCC.getListNCC()) {
            cboLocNCC.addItem(ncc.getTenNCC());
        }

        cboLocTrangThai.removeAllItems();
        cboLocTrangThai.addItem("-- Tất cả trạng thái --");
        for (TrangThaiLoHang tt : TrangThaiLoHang.values()) {
            cboLocTrangThai.addItem(tt.getTenHienThi());
        }

        cboLocHang.setSelectedIndex(idxChon);
        hienThiBang();
    }

    private void hienThiBang() {
        if (cboLocHang.getItemCount() == 0 || cboLocTrangThai.getItemCount() == 0) {
            return;
        }
        tableModel.setRowCount(0);
        ManagerLoHang qlLo = qlPhieu.getManagerLoHang();

        int locIdHang = -1;
        if (cboLocHang.getSelectedIndex() > 0 && cboLocHang.getSelectedItem() != null) {
            locIdHang = Integer.parseInt(cboLocHang.getSelectedItem().toString().split(" - ")[0].trim());
        }
        String locTenNCC = cboLocNCC.getSelectedIndex() > 0 && cboLocNCC.getSelectedItem() != null
                ? cboLocNCC.getSelectedItem().toString() : null;
        String locTT = cboLocTrangThai.getSelectedIndex() > 0 && cboLocTrangThai.getSelectedItem() != null
                ? cboLocTrangThai.getSelectedItem().toString() : null;

        int soLoHienThi = 0;
        int tongCon = 0;
        int tongHuHong = 0;
        long tongGiaTriTon = 0;

        for (LoHang lo : qlLo.getList()) {
            if (locIdHang > 0 && lo.getIdHangHoa() != locIdHang) {
                continue;
            }
            if (locTenNCC != null && !locTenNCC.equalsIgnoreCase(lo.getTenNhaCungCap())) {
                continue;
            }
            String tenTrangThai = lo.getTrangThai().getTenHienThi();
            if (locTT != null && !locTT.equals(tenTrangThai)) {
                continue;
            }
            HangHoa hh = qlHang.findById(lo.getIdHangHoa());
            String tenMH = hh != null ? (hh.getMaHang() + " - " + hh.getTenHang()) : ("#" + lo.getIdHangHoa());
            String maDon = (lo.getMaDonNhap() == null || lo.getMaDonNhap().isEmpty())
                    ? String.format("DHN%04d", lo.getIdPhieuNhap()) : lo.getMaDonNhap();

            tableModel.addRow(new Object[]{
                lo.getId(),
                lo.getMaLo(),
                maDon,
                tenMH,
                lo.getTenNhaCungCap(),
                lo.getNgayNhap(),
                lo.getHanSuDung(),
                lo.getGiaNhap(),
                lo.getSoLuongNhap(),
                lo.getSoLuongDaXuat(),
                lo.getSoLuongHuHong(),
                lo.getSoLuongCon(),
                tenTrangThai,
                lo.getChiTietHuHong()
            });
            soLoHienThi++;
            tongCon += lo.getSoLuongCon();
            tongHuHong += lo.getSoLuongHuHong();
            tongGiaTriTon += lo.getGiaTriTon();
        }

        lblTomTat.setText(String.format(
                "Đang hiển thị: %d lô  |  Tổng SL còn tồn: %d  |  Tổng SL hư hỏng/huỷ: %d  |  Giá trị tồn kho (theo giá vốn): %s",
                soLoHienThi, tongCon, tongHuHong, FormatUtils.formatMoney(tongGiaTriTon)));
        btnBaoHuHong.setEnabled(false);
        btnXemBillNhap.setEnabled(false);
    }

    private LoHang getLoDangChon() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        int idLo = (Integer) tableModel.getValueAt(modelRow, 0);
        return qlPhieu.getManagerLoHang().findById(idLo);
    }

    private void xuLyBaoHuHong() {
        LoHang lo = getLoDangChon();
        if (lo == null || lo.getSoLuongCon() <= 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lô đang còn hàng tồn để ghi nhận hư hỏng / huỷ!");
            return;
        }
        HangHoa hh = qlHang.findById(lo.getIdHangHoa());
        String tenMH = hh != null ? hh.getTenHang() : ("Mặt hàng #" + lo.getIdHangHoa());

        JComboBox<LyDoHuHong> cboLyDo = new JComboBox<>(LyDoHuHong.values());
        if (lo.isHetHan()) {
            cboLyDo.setSelectedItem(LyDoHuHong.HET_HAN);
        }
        JTextField txtSoLuong = new JTextField(lo.isHetHan() ? String.valueOf(lo.getSoLuongCon()) : "1", 10);
        JTextField txtGhiChu = new JTextField(20);

        JPanel pnl = new JPanel(new GridLayout(0, 1, 4, 4));
        pnl.add(new JLabel("Lô hàng: " + lo.getMaLo() + " - " + tenMH));
        pnl.add(new JLabel("Nhà cung cấp: " + lo.getTenNhaCungCap() + " | Số lượng còn: " + lo.getSoLuongCon()));
        pnl.add(new JLabel("Nguyên nhân hư hỏng / loại bỏ (*):"));
        pnl.add(cboLyDo);
        pnl.add(new JLabel("Số lượng bị hư hỏng / cần huỷ (*):"));
        pnl.add(txtSoLuong);
        pnl.add(new JLabel("Ghi chú chi tiết (vị trí kho, tình trạng...):"));
        pnl.add(txtGhiChu);

        int ok = JOptionPane.showConfirmDialog(this, pnl,
                "Ghi nhận hàng hư hỏng / Lập Biên bản huỷ", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) {
            return;
        }

        int sl;
        try {
            sl = Integer.parseInt(txtSoLuong.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng hư hỏng phải là số nguyên dương!");
            return;
        }

        String maBienBan = qlPhieu.sinhMaDonTiepTheo("BBH");
        PhieuHuy ph = new PhieuHuy(maBienBan, new Date(), lo.getId(), sl,
                (LyDoHuHong) cboLyDo.getSelectedItem(), txtGhiChu.getText().trim());
        String loi = qlPhieu.baoHuHong(ph);
        if (loi != null) {
            JOptionPane.showMessageDialog(this, loi, "Không thể lập biên bản", JOptionPane.WARNING_MESSAGE);
            return;
        }
        hienThiBang();
        int xemBill = JOptionPane.showConfirmDialog(this,
                "Đã cập nhật trạng thái lô " + lo.getMaLo() + " và lập Biên bản huỷ " + maBienBan + " thành công!\n"
                + "Bạn có muốn xem / xuất Bill Biên bản huỷ ngay không?",
                "Thành công", JOptionPane.YES_NO_OPTION);
        if (xemBill == JOptionPane.YES_OPTION) {
            new HoaDonDialog(this, maBienBan, qlPhieu, qlHang, qlNCC).setVisible(true);
        }
    }

    private void xuLyXemBillNhap() {
        LoHang lo = getLoDangChon();
        if (lo == null) {
            return;
        }
        String maDon = (lo.getMaDonNhap() == null || lo.getMaDonNhap().isEmpty())
                ? String.format("DHN%04d", lo.getIdPhieuNhap()) : lo.getMaDonNhap();
        new HoaDonDialog(this, maDon, qlPhieu, qlHang, qlNCC).setVisible(true);
    }

    private class LoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            Object shown = value;
            if ((col == 5 || col == 6) && value instanceof Date) {
                shown = FormatUtils.formatDate((Date) value);
            } else if (col == 6 && value == null) {
                shown = "Không HSD";
            } else if (col == 7 && value instanceof Number) {
                shown = FormatUtils.formatMoney(((Number) value).longValue());
            }
            super.getTableCellRendererComponent(t, shown, isSelected, hasFocus, row, col);

            if (col == 7) {
                setHorizontalAlignment(SwingConstants.RIGHT);
            } else if (col <= 2 || col == 5 || col == 6 || (col >= 8 && col <= 11)) {
                setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }

            int modelRow = t.convertRowIndexToModel(row);
            String tt = String.valueOf(t.getModel().getValueAt(modelRow, 12));
            if (!isSelected) {
                if (tt.contains("Đã hết hạn")) {
                    setForeground(new Color(190, 0, 0));
                } else if (tt.contains("Sắp hết hạn") || tt.contains("có hư hỏng")) {
                    setForeground(new Color(190, 95, 0));
                } else if (tt.contains("Đã huỷ")) {
                    setForeground(new Color(135, 30, 110));
                } else if (tt.contains("Đã xuất kho hết")) {
                    setForeground(new Color(110, 110, 110));
                } else {
                    setForeground(Color.BLACK);
                }
            }
            return this;
        }
    }
}
