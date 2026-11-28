package com.mycompany.quanlykhohang.view; // gói chứa các lớp giao diện

import com.mycompany.quanlykhohang.entity.HangHoa; // lớp hàng hoá
import com.mycompany.quanlykhohang.utils.FormatUtils; // hàm định dạng tiền/ngày
import com.toedter.calendar.JDateChooser; // ô chọn ngày kiểu lịch (Calendar)
import java.awt.BorderLayout; // bố cục chia 5 vùng
import java.awt.Color; // màu sắc
import java.awt.Component; // thành phần giao diện nói chung
import java.awt.Dimension; // kích thước
import java.awt.FlowLayout; // bố cục xếp ngang liên tiếp
import java.awt.Font; // phông chữ
import java.awt.GridBagConstraints; // ràng buộc vị trí cho GridBagLayout
import java.awt.GridBagLayout; // bố cục dạng lưới linh hoạt
import java.awt.Insets; // khoảng cách lề
import java.awt.event.ActionListener; // lắng nghe bấm nút
import java.util.Date; // kiểu ngày
import java.util.List; // danh sách
import javax.swing.BorderFactory; // tạo viền
import javax.swing.JButton; // nút bấm
import javax.swing.JComboBox; // ô chọn thả xuống (Dropdown list box)
import javax.swing.JFrame; // cửa sổ chính
import javax.swing.JLabel; // nhãn chữ
import javax.swing.JOptionPane; // hộp thoại thông báo/xác nhận
import javax.swing.JPanel; // khung chứa
import javax.swing.JScrollPane; // khung có thanh cuộn
import javax.swing.JTable; // bảng
import javax.swing.JTextField; // ô nhập chữ một dòng
import javax.swing.ListSelectionModel; // chế độ chọn dòng của bảng
import javax.swing.SwingConstants; // hằng số căn lề
import javax.swing.event.ListSelectionListener; // lắng nghe chọn dòng
import javax.swing.table.DefaultTableCellRenderer; // lớp vẽ ô của bảng
import javax.swing.table.DefaultTableModel; // mô hình dữ liệu của bảng

/** Giao diện quản lý hàng hoá: bảng danh sách + form nhập + nút Thêm/Sửa/Xoá/Làm mới. */
public class HangHoaView extends JFrame { // giao diện là một cửa sổ JFrame

    private static final String[] COLUMNS = {"STT", "Mã hàng", "Tên hàng", "Nhóm hàng", "ĐVT", "Đơn giá", "Hạn sử dụng", "Mã lô", "Nhà cung cấp", "Tình trạng"}; // tiêu đề cột (Mã lô và Nhà cung cấp đứng cạnh nhau)
    private static final int COT_TINH_TRANG = 9; // vị trí cột Tình trạng (dùng để tô đỏ)
    private static final String[] NHOM_HANG = {"Đồ uống", "Bánh kẹo", "Mì - Miến - Cháo", "Gia vị", "Sữa", "Đồ dùng gia đình", "Khác"}; // các nhóm hàng gợi ý
    private static final String[] TINH_TRANG = {HangHoa.BINH_THUONG, "Chuột cắn hỏng", "Ẩm mốc", "Móp méo / rách bao bì", "Côn trùng / sâu mọt", "Hư hỏng khác"}; // các tình trạng để chọn (hết hạn được tự động nhận biết theo hạn sử dụng)

    private final DefaultTableModel tableModel; // mô hình dữ liệu của bảng
    private final JTable table; // bảng hiển thị danh sách
    private final JTextField txtMa = new JTextField(15); // ô mã hàng
    private final JTextField txtTen = new JTextField(15); // ô tên hàng
    private final JComboBox<String> cboNhom = new JComboBox<String>(NHOM_HANG); // ô chọn nhóm hàng
    private final JTextField txtDVT = new JTextField(15); // ô đơn vị tính
    private final JTextField txtDonGia = new JTextField(15); // ô đơn giá
    private final JDateChooser dateHSD = new JDateChooser(); // ô hạn sử dụng (Calendar)
    private final JTextField txtMaLo = new JTextField(15); // ô mã lô
    private final JComboBox<String> cboNCC = new JComboBox<String>(); // ô nhà cung cấp (chọn từ danh sách đã có hoặc gõ mới)
    private final JComboBox<String> cboTinhTrang = new JComboBox<String>(TINH_TRANG); // ô chọn tình trạng hàng
    private final JButton btnAdd = new JButton("Thêm"); // nút Thêm
    private final JButton btnEdit = new JButton("Sửa"); // nút Sửa
    private final JButton btnDelete = new JButton("Xoá"); // nút Xoá
    private final JButton btnClear = new JButton("Làm mới"); // nút Làm mới form

    private int selectedId = -1; // id của dòng đang chọn, -1 nếu chưa chọn

    public HangHoaView() { // constructor dựng giao diện
        super("Quản lý kho hàng cửa hàng tạp hoá - Hàng hoá"); // tiêu đề cửa sổ
        setDefaultCloseOperation(EXIT_ON_CLOSE); // đóng cửa sổ thì thoát chương trình

        // ----- Bảng -----
        tableModel = new DefaultTableModel(COLUMNS, 0) { // mô hình bảng với các cột, 0 dòng ban đầu
            @Override // ghi đè phương thức của lớp cha
            public boolean isCellEditable(int row, int column) { // quyết định ô có được sửa trực tiếp không
                return false; // không cho sửa trực tiếp trên bảng (sửa qua form)
            } // kết thúc hàm
        }; // kết thúc lớp ẩn danh
        table = new JTable(tableModel); // tạo bảng dùng mô hình trên
        table.setRowHeight(24); // chiều cao mỗi dòng 24 pixel
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // chỉ chọn được một dòng một lúc
        table.getTableHeader().setReorderingAllowed(false); // không cho kéo đổi chỗ cột
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD)); // in đậm tiêu đề cột
        table.getColumnModel().getColumn(0).setPreferredWidth(40); // cột STT hẹp
        table.getColumnModel().getColumn(2).setPreferredWidth(200); // cột Tên hàng rộng
        table.getColumnModel().getColumn(8).setPreferredWidth(150); // cột Nhà cung cấp rộng
        table.getColumnModel().getColumn(9).setPreferredWidth(150); // cột Tình trạng rộng
        table.setDefaultRenderer(Object.class, new RowRenderer()); // dùng bộ vẽ ô tự viết cho mọi ô

        // ----- Form nhập -----
        JPanel form = new JPanel(new GridBagLayout()); // khung form dùng bố cục lưới
        form.setBorder(BorderFactory.createTitledBorder("Thông tin hàng hoá")); // viền có tiêu đề
        cboNhom.setEditable(true); // cho phép gõ thêm nhóm hàng mới
        cboNCC.setEditable(true); // cho phép gõ tên nhà cung cấp mới
        dateHSD.setDateFormatString(FormatUtils.DATE_PATTERN); // hiển thị ngày dạng dd/MM/yyyy
        dateHSD.setPreferredSize(new Dimension(170, 24)); // kích thước ô chọn ngày
        txtDonGia.setHorizontalAlignment(SwingConstants.RIGHT); // số tiền căn phải

        addRow(form, 0, "Mã hàng:", txtMa, "Tên hàng:", txtTen); // hàng 0: mã hàng và tên hàng
        addRow(form, 1, "Nhóm hàng:", cboNhom, "Đơn vị tính:", txtDVT); // hàng 1: nhóm hàng và đơn vị tính
        addRow(form, 2, "Đơn giá (đ):", txtDonGia, "Hạn sử dụng:", dateHSD); // hàng 2: đơn giá và hạn sử dụng
        addRow(form, 3, "Mã lô:", txtMaLo, "Nhà cung cấp:", cboNCC); // hàng 3: mã lô và nhà cung cấp (đứng cạnh nhau)
        addRow(form, 4, "Tình trạng:", cboTinhTrang, null, null); // hàng 4: tình trạng hàng

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6)); // khung chứa các nút
        buttons.add(btnAdd); // thêm nút Thêm
        buttons.add(btnEdit); // thêm nút Sửa
        buttons.add(btnDelete); // thêm nút Xoá
        buttons.add(btnClear); // thêm nút Làm mới

        JPanel top = new JPanel(new BorderLayout()); // khung chứa form và nút
        top.add(form, BorderLayout.CENTER); // form ở giữa
        top.add(buttons, BorderLayout.SOUTH); // nút ở phía dưới form

        JLabel title = new JLabel("QUẢN LÝ HÀNG HOÁ", SwingConstants.CENTER); // tiêu đề trang
        title.setFont(new Font("Times New Roman", Font.BOLD, 24)); // chữ đậm cỡ 24
        title.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0)); // chừa lề trên dưới 8px

        JPanel north = new JPanel(new BorderLayout()); // khung phía trên cửa sổ
        north.add(title, BorderLayout.NORTH); // tiêu đề ở trên cùng
        north.add(top, BorderLayout.CENTER); // form + nút bên dưới tiêu đề

        add(north, BorderLayout.NORTH); // đặt khung trên vào phía Bắc cửa sổ
        add(new JScrollPane(table), BorderLayout.CENTER); // bảng (có thanh cuộn) chiếm phần còn lại

        setSize(1150, 680); // kích thước cửa sổ (rộng hơn vì có thêm cột)
        setLocationRelativeTo(null); // hiện giữa màn hình
        clearForm(); // đặt form về trạng thái ban đầu
    } // kết thúc constructor

    private void addRow(JPanel p, int row, String l1, Component c1, String l2, Component c2) { // thêm một hàng gồm 1-2 cặp (nhãn + ô nhập) vào form
        GridBagConstraints g = new GridBagConstraints(); // đối tượng mô tả vị trí trong lưới
        g.insets = new Insets(4, 8, 4, 8); // khoảng cách lề quanh mỗi thành phần
        g.anchor = GridBagConstraints.WEST; // căn về bên trái
        g.gridy = row; // số thứ tự hàng
        g.gridx = 0; p.add(new JLabel(l1), g); // cột 0: nhãn thứ nhất
        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1; p.add(c1, g); // cột 1: ô nhập thứ nhất, giãn ngang
        if (l2 != null && c2 != null) { // có cặp thứ hai
            g.gridx = 2; g.fill = GridBagConstraints.NONE; g.weightx = 0; p.add(new JLabel(l2), g); // cột 2: nhãn thứ hai
            g.gridx = 3; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1; p.add(c2, g); // cột 3: ô nhập thứ hai
        } // kết thúc if
    } // kết thúc hàm

    /** Tô đỏ dòng cần cảnh báo (hết hạn, chuột cắn hỏng...), căn phải cột tiền, định dạng tiền/ngày. */
    private class RowRenderer extends DefaultTableCellRenderer { // lớp quyết định cách vẽ từng ô
        @Override // ghi đè hàm của lớp cha
        public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) { // hàm vẽ mỗi ô
            Object shown = value; // giá trị sẽ hiển thị, mặc định là giá trị gốc
            if (value instanceof Long) { // số kiểu Long là tiền
                shown = FormatUtils.formatMoney((Long) value); // đổi thành dạng 1.250.000 đ
            } else if (value instanceof Date) { // kiểu ngày
                shown = FormatUtils.formatDate((Date) value); // đổi thành dd/MM/yyyy
            } // kết thúc if-else
            super.getTableCellRendererComponent(t, shown, isSelected, hasFocus, row, col); // để lớp cha vẽ ô với giá trị đã định dạng
            setHorizontalAlignment(col == 5 ? SwingConstants.RIGHT : (col == 0 || col == 4 || col == 6 || col == 7) ? SwingConstants.CENTER : SwingConstants.LEFT); // đơn giá căn phải; STT/ĐVT/HSD/Mã lô căn giữa; còn lại căn trái
            Object tt = t.getModel().getValueAt(t.convertRowIndexToModel(row), COT_TINH_TRANG); // tình trạng hiển thị của dòng này
            boolean canhBao = tt != null && !HangHoa.BINH_THUONG.equals(tt.toString()); // khác "Bình thường" nghĩa là hết hạn hoặc hỏng
            if (!isSelected) { // dòng không được chọn thì mới đổi màu chữ
                setForeground(canhBao ? Color.RED : Color.BLACK); // cảnh báo màu đỏ, bình thường màu đen
            } // kết thúc if
            return this; // trả về thành phần đã vẽ
        } // kết thúc hàm
    } // kết thúc lớp RowRenderer

    // ---------- Hiển thị ----------
    public void showListHangHoa(List<HangHoa> list) { // đổ danh sách lên bảng
        tableModel.setRowCount(0); // xoá hết dòng cũ
        int stt = 1; // số thứ tự bắt đầu từ 1
        for (HangHoa h : list) { // duyệt từng hàng hoá
            tableModel.addRow(new Object[]{stt++, h.getMaHang(), h.getTenHang(), h.getNhomHang(), h.getDonViTinh(), Long.valueOf(h.getDonGia()), h.getHanSuDung(), h.getMaLo(), h.getNhaCungCap(), h.getTinhTrangHienThi()}); // thêm dòng (tình trạng đã tự nhận biết hết hạn)
        } // kết thúc for
    } // kết thúc hàm

    /** Nạp các nhà cung cấp đã có vào ô chọn để người dùng chọn lại cho nhanh. */
    public void napNhaCungCap(List<String> ds) { // gợi ý nhà cung cấp
        Object dangGo = cboNCC.getEditor().getItem(); // nội dung đang hiển thị trong ô
        cboNCC.removeAllItems(); // xoá danh sách cũ
        for (String s : ds) { // duyệt từng nhà cung cấp
            cboNCC.addItem(s); // thêm vào ô chọn
        } // kết thúc for
        cboNCC.setSelectedItem(dangGo == null ? "" : dangGo.toString()); // giữ lại nội dung đang hiển thị
    } // kết thúc hàm

    public void fillForm(HangHoa h) { // đổ một dòng lên form
        selectedId = h.getId(); // nhớ id đang chọn
        txtMa.setText(h.getMaHang()); // điền mã hàng
        txtTen.setText(h.getTenHang()); // điền tên hàng
        cboNhom.setSelectedItem(h.getNhomHang()); // chọn nhóm hàng
        txtDVT.setText(h.getDonViTinh()); // điền đơn vị tính
        txtDonGia.setText(String.valueOf(h.getDonGia())); // điền đơn giá (số thuần)
        dateHSD.setDate(h.getHanSuDung()); // chọn hạn sử dụng
        txtMaLo.setText(h.getMaLo() == null ? "" : h.getMaLo()); // điền mã lô (file cũ có thể chưa có)
        cboNCC.setSelectedItem(h.getNhaCungCap() == null ? "" : h.getNhaCungCap()); // điền nhà cung cấp
        String tt = h.getTinhTrang() == null || h.getTinhTrang().trim().isEmpty() ? HangHoa.BINH_THUONG : h.getTinhTrang(); // tình trạng đã lưu
        cboTinhTrang.setSelectedItem(tt); // chọn tình trạng trong danh sách
        btnAdd.setEnabled(false); // đang xem dòng có sẵn nên tắt nút Thêm
        btnEdit.setEnabled(true); // bật nút Sửa
        btnDelete.setEnabled(true); // bật nút Xoá
    } // kết thúc hàm

    public void clearForm() { // đưa form về trạng thái trống
        selectedId = -1; // bỏ chọn
        txtMa.setText(""); // xoá mã hàng
        txtTen.setText(""); // xoá tên hàng
        cboNhom.setSelectedIndex(0); // chọn lại nhóm đầu tiên
        txtDVT.setText(""); // xoá đơn vị tính
        txtDonGia.setText(""); // xoá đơn giá
        dateHSD.setDate(null); // xoá ngày
        txtMaLo.setText(""); // xoá mã lô
        cboNCC.setSelectedItem(""); // xoá nhà cung cấp
        cboTinhTrang.setSelectedIndex(0); // về "Bình thường"
        table.clearSelection(); // bỏ chọn dòng trong bảng
        btnAdd.setEnabled(true); // bật nút Thêm
        btnEdit.setEnabled(false); // tắt nút Sửa
        btnDelete.setEnabled(false); // tắt nút Xoá
    } // kết thúc hàm

    /** Đọc dữ liệu từ form; trả null (và báo lỗi) nếu đơn giá nhập sai. */
    public HangHoa getHangHoaInfo() { // lấy dữ liệu người dùng nhập
        long donGia; // biến chứa đơn giá
        try { // thử đổi chuỗi sang số
            donGia = FormatUtils.parseMoney(txtDonGia.getText()); // đổi "15.000" hoặc "15000" thành 15000
        } catch (NumberFormatException ex) { // nếu nhập sai
            showMessage("Đơn giá phải là số nguyên dương (ví dụ 15000 hoặc 15.000)."); // báo lỗi
            return null; // trả null để controller dừng
        } // kết thúc catch
        Object nhom = cboNhom.getEditor().getItem(); // nhóm hàng đang hiển thị (kể cả gõ tay)
        Object ncc = cboNCC.getEditor().getItem(); // nhà cung cấp đang hiển thị (kể cả gõ tay)
        Object tt = cboTinhTrang.getSelectedItem(); // tình trạng đã chọn
        return new HangHoa(selectedId, txtMa.getText(), txtTen.getText(), nhom == null ? "" : nhom.toString(), txtDVT.getText(), donGia, dateHSD.getDate(), txtMaLo.getText(), ncc == null ? "" : ncc.toString(), tt == null ? HangHoa.BINH_THUONG : tt.toString()); // tạo đối tượng
    } // kết thúc hàm

    public int getSelectedId() { return selectedId; } // trả về id đang chọn
    public int getSelectedRow() { return table.getSelectedRow(); } // trả về chỉ số dòng đang chọn trong bảng

    public void showMessage(String msg) { // hiện hộp thoại thông báo
        JOptionPane.showMessageDialog(this, msg); // hiển thị nội dung msg
    } // kết thúc hàm

    public boolean confirm(String msg) { // hiện hộp thoại hỏi Có/Không
        return JOptionPane.showConfirmDialog(this, msg, "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION; // true nếu người dùng chọn Yes
    } // kết thúc hàm

    // ---------- Gắn listener (controller gọi) ----------
    public void addAddListener(ActionListener l) { btnAdd.addActionListener(l); } // gắn xử lý nút Thêm
    public void addEditListener(ActionListener l) { btnEdit.addActionListener(l); } // gắn xử lý nút Sửa
    public void addDeleteListener(ActionListener l) { btnDelete.addActionListener(l); } // gắn xử lý nút Xoá
    public void addClearListener(ActionListener l) { btnClear.addActionListener(l); } // gắn xử lý nút Làm mới
    public void addTableSelectionListener(ListSelectionListener l) { table.getSelectionModel().addListSelectionListener(l); } // gắn xử lý chọn dòng
} // kết thúc lớp HangHoaView
