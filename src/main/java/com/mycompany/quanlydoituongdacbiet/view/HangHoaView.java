package com.mycompany.quanlydoituongdacbiet.view; // gói chứa các lớp giao diện

import com.mycompany.quanlydoituongdacbiet.entity.HangHoa; // lớp hàng hoá
import com.mycompany.quanlydoituongdacbiet.utils.FormatUtils; // hàm định dạng tiền/ngày
import com.toedter.calendar.JDateChooser; // ô chọn ngày kiểu lịch (thư viện jcalendar)
import java.awt.BorderLayout; // bố cục chia 5 vùng: Bắc/Nam/Đông/Tây/Giữa
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
import javax.swing.BorderFactory; // tạo viền cho thành phần
import javax.swing.JButton; // nút bấm
import javax.swing.JComboBox; // ô chọn thả xuống
import javax.swing.JFrame; // cửa sổ chính
import javax.swing.JLabel; // nhãn chữ
import javax.swing.JOptionPane; // hộp thoại thông báo/xác nhận
import javax.swing.JPanel; // khung chứa các thành phần
import javax.swing.JScrollPane; // khung có thanh cuộn
import javax.swing.JTable; // bảng
import javax.swing.JTextField; // ô nhập văn bản một dòng
import javax.swing.ListSelectionModel; // chế độ chọn dòng của bảng
import javax.swing.SwingConstants; // hằng số căn lề
import javax.swing.event.ListSelectionListener; // lắng nghe chọn dòng
import javax.swing.table.DefaultTableCellRenderer; // lớp vẽ ô của bảng
import javax.swing.table.DefaultTableModel; // mô hình dữ liệu của bảng

/** Giao diện quản lý hàng hoá: bảng danh sách + form nhập + nút Thêm/Sửa/Xoá/Làm mới. */
public class HangHoaView extends JFrame { // giao diện là một cửa sổ JFrame

    private static final String[] COLUMNS = {"STT", "Mã hàng", "Tên hàng", "Nhóm hàng", // tiêu đề các cột của bảng
        "ĐVT", "Đơn giá", "Hạn sử dụng"}; // tiêu đề các cột còn lại
    private static final String[] NHOM_HANG = {"Đồ uống", "Bánh kẹo", "Mì - Miến - Cháo", // các nhóm hàng gợi ý trong ô chọn
        "Gia vị", "Sữa", "Đồ dùng gia đình", "Khác"}; // các nhóm hàng còn lại

    private final DefaultTableModel tableModel; // mô hình dữ liệu của bảng
    private final JTable table; // bảng hiển thị danh sách
    private final JTextField txtMa = new JTextField(15); // ô nhập mã hàng (rộng 15 ký tự)
    private final JTextField txtTen = new JTextField(15); // ô nhập tên hàng
    private final JComboBox<String> cboNhom = new JComboBox<String>(NHOM_HANG); // ô chọn nhóm hàng
    private final JTextField txtDVT = new JTextField(15); // ô nhập đơn vị tính
    private final JTextField txtDonGia = new JTextField(15); // ô nhập đơn giá
    private final JDateChooser dateHSD = new JDateChooser(); // ô chọn hạn sử dụng bằng lịch
    private final JButton btnAdd = new JButton("Thêm"); // nút Thêm
    private final JButton btnEdit = new JButton("Sửa"); // nút Sửa
    private final JButton btnDelete = new JButton("Xoá"); // nút Xoá
    private final JButton btnClear = new JButton("Làm mới"); // nút Làm mới

    private int selectedId = -1; // id hàng đang chọn, -1 nghĩa là chưa chọn

    public HangHoaView() { // constructor dựng giao diện
        super("Quản lý kho hàng cửa hàng tạp hoá - Hàng hoá"); // đặt tiêu đề cửa sổ
        setDefaultCloseOperation(EXIT_ON_CLOSE); // đóng cửa sổ thì thoát chương trình

        // ----- Bảng -----
        tableModel = new DefaultTableModel(COLUMNS, 0) { // tạo mô hình bảng với các cột, 0 dòng ban đầu
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
        table.getColumnModel().getColumn(2).setPreferredWidth(220); // cột Tên hàng rộng
        table.setDefaultRenderer(Object.class, new RowRenderer()); // dùng bộ vẽ ô tự viết cho mọi ô

        // ----- Form nhập -----
        JPanel form = new JPanel(new GridBagLayout()); // khung form dùng bố cục lưới
        form.setBorder(BorderFactory.createTitledBorder("Thông tin hàng hoá")); // viền có tiêu đề
        cboNhom.setEditable(true); // cho phép gõ thêm nhóm hàng mới
        dateHSD.setDateFormatString(FormatUtils.DATE_PATTERN); // hiển thị ngày dạng dd/MM/yyyy
        dateHSD.setPreferredSize(new Dimension(170, 24)); // kích thước ô chọn ngày
        txtDonGia.setHorizontalAlignment(SwingConstants.RIGHT); // số tiền căn phải

        addRow(form, 0, "Mã hàng:", txtMa, "Tên hàng:", txtTen); // hàng 0 của form: mã hàng và tên hàng
        addRow(form, 1, "Nhóm hàng:", cboNhom, "Đơn vị tính:", txtDVT); // hàng 1: nhóm hàng và đơn vị tính
        addRow(form, 2, "Đơn giá (đ):", txtDonGia, "Hạn sử dụng:", dateHSD); // hàng 2: đơn giá và hạn sử dụng

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6)); // khung nút, căn giữa, cách nhau 12px
        buttons.add(btnAdd); // thêm nút Thêm
        buttons.add(btnEdit); // thêm nút Sửa
        buttons.add(btnDelete); // thêm nút Xoá
        buttons.add(btnClear); // thêm nút Làm mới

        JPanel top = new JPanel(new BorderLayout()); // khung chứa form và nút
        top.add(form, BorderLayout.CENTER); // form ở giữa
        top.add(buttons, BorderLayout.SOUTH); // nút ở phía dưới form

        JLabel title = new JLabel("QUẢN LÝ HÀNG HOÁ", SwingConstants.CENTER); // tiêu đề căn giữa
        title.setFont(new Font("Times New Roman", Font.BOLD, 24)); // chữ đậm cỡ 24
        title.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0)); // chừa lề trên dưới 8px

        JPanel north = new JPanel(new BorderLayout()); // khung phần phía trên cửa sổ
        north.add(title, BorderLayout.NORTH); // tiêu đề ở trên cùng
        north.add(top, BorderLayout.CENTER); // form + nút bên dưới tiêu đề

        add(north, BorderLayout.NORTH); // đặt khung trên vào phía Bắc cửa sổ
        add(new JScrollPane(table), BorderLayout.CENTER); // bảng (có thanh cuộn) chiếm phần còn lại

        setSize(900, 620); // kích thước cửa sổ
        setLocationRelativeTo(null); // hiện giữa màn hình
        clearForm(); // đặt form về trạng thái ban đầu
    } // kết thúc constructor

    private void addRow(JPanel p, int row, String l1, Component c1, String l2, Component c2) { // thêm một hàng gồm 2 cặp (nhãn + ô nhập) vào form
        GridBagConstraints g = new GridBagConstraints(); // đối tượng mô tả vị trí trong lưới
        g.insets = new Insets(4, 8, 4, 8); // lề quanh mỗi thành phần
        g.anchor = GridBagConstraints.WEST; // căn về bên trái
        g.gridy = row; // số thứ tự hàng
        g.gridx = 0; p.add(new JLabel(l1), g); // cột 0: nhãn thứ nhất
        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1; p.add(c1, g); // cột 1: ô nhập thứ nhất, giãn ngang
        g.gridx = 2; g.fill = GridBagConstraints.NONE; g.weightx = 0; p.add(new JLabel(l2), g); // cột 2: nhãn thứ hai, không giãn
        g.gridx = 3; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1; p.add(c2, g); // cột 3: ô nhập thứ hai, giãn ngang
    } // kết thúc hàm

    /** Tô đỏ dòng hết hạn, căn phải cột tiền, định dạng tiền/ngày. */
    private class RowRenderer extends DefaultTableCellRenderer { // lớp quyết định cách vẽ từng ô
        @Override // ghi đè hàm của lớp cha
        public Component getTableCellRendererComponent(JTable t, Object value, // hàm được gọi cho mỗi ô cần vẽ
                boolean isSelected, boolean hasFocus, int row, int col) { // các tham số: ô có đang chọn, vị trí dòng/cột
            Object shown = value; // giá trị sẽ hiển thị, mặc định là giá trị gốc
            if (value instanceof Long) { // nếu là số (cột đơn giá)
                shown = FormatUtils.formatMoney((Long) value); // đổi thành dạng 1.250.000 đ
            } else if (value instanceof Date) { // nếu là ngày (cột hạn sử dụng)
                shown = FormatUtils.formatDate((Date) value); // đổi thành dd/MM/yyyy
            } // kết thúc if-else
            super.getTableCellRendererComponent(t, shown, isSelected, hasFocus, row, col); // để lớp cha vẽ ô với giá trị đã định dạng
            setHorizontalAlignment(col == 5 ? SwingConstants.RIGHT // cột 5 (đơn giá) căn phải
                    : (col == 0 || col == 4 || col == 6) ? SwingConstants.CENTER : SwingConstants.LEFT); // cột STT/ĐVT/HSD căn giữa, còn lại căn trái
            Object hsd = t.getModel().getValueAt(t.convertRowIndexToModel(row), 6); // lấy hạn sử dụng của dòng này (cột 6)
            boolean expired = hsd instanceof Date && ((Date) hsd).before(new Date()); // hết hạn nếu hạn sử dụng trước thời điểm hiện tại
            if (!isSelected) { // dòng không được chọn thì mới đổi màu chữ
                setForeground(expired ? Color.RED : Color.BLACK); // hết hạn: đỏ, còn hạn: đen
            } // kết thúc if
            return this; // trả về thành phần đã vẽ
        } // kết thúc hàm
    } // kết thúc lớp RowRenderer

    // ---------- Hiển thị ----------
    public void showListHangHoa(List<HangHoa> list) { // đổ danh sách lên bảng
        tableModel.setRowCount(0); // xoá hết dòng cũ
        int stt = 1; // số thứ tự bắt đầu từ 1
        for (HangHoa h : list) { // duyệt từng hàng hoá
            tableModel.addRow(new Object[]{stt++, h.getMaHang(), h.getTenHang(), h.getNhomHang(), // thêm dòng: STT, mã, tên, nhóm
                h.getDonViTinh(), Long.valueOf(h.getDonGia()), h.getHanSuDung()}); // ĐVT, đơn giá (dạng Long), hạn sử dụng
        } // kết thúc for
    } // kết thúc hàm

    public void fillForm(HangHoa h) { // đổ dữ liệu một hàng hoá lên form
        selectedId = h.getId(); // ghi nhớ id đang chọn
        txtMa.setText(h.getMaHang()); // điền mã hàng
        txtTen.setText(h.getTenHang()); // điền tên hàng
        cboNhom.setSelectedItem(h.getNhomHang()); // chọn nhóm hàng
        txtDVT.setText(h.getDonViTinh()); // điền đơn vị tính
        txtDonGia.setText(String.valueOf(h.getDonGia())); // điền đơn giá (số thuần)
        dateHSD.setDate(h.getHanSuDung()); // chọn hạn sử dụng
        btnAdd.setEnabled(false); // đang xem hàng có sẵn nên tắt nút Thêm
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
        table.clearSelection(); // bỏ chọn dòng trong bảng
        btnAdd.setEnabled(true); // bật nút Thêm
        btnEdit.setEnabled(false); // tắt nút Sửa
        btnDelete.setEnabled(false); // tắt nút Xoá
    } // kết thúc hàm

    /** Đọc dữ liệu từ form; trả null (và báo lỗi) nếu đơn giá nhập sai. */
    public HangHoa getHangHoaInfo() { // lấy dữ liệu người dùng nhập thành đối tượng HangHoa
        long donGia; // biến chứa đơn giá
        try { // thử đổi chuỗi sang số
            donGia = FormatUtils.parseMoney(txtDonGia.getText()); // đổi "15.000" hoặc "15000" thành 15000
        } catch (NumberFormatException ex) { // nếu nhập sai
            showMessage("Đơn giá phải là số nguyên dương (ví dụ 15000 hoặc 15.000)."); // báo lỗi
            return null; // trả null để controller dừng
        } // kết thúc catch
        Object nhom = cboNhom.getEditor().getItem(); // lấy nhóm hàng đang hiển thị (kể cả khi gõ tay)
        return new HangHoa(selectedId, txtMa.getText(), txtTen.getText(), // tạo hàng hoá với id đang chọn, mã, tên
                nhom == null ? "" : nhom.toString(), txtDVT.getText(), donGia, dateHSD.getDate()); // nhóm, ĐVT, đơn giá, ngày
    } // kết thúc hàm

    public int getSelectedId() { return selectedId; } // trả về id đang chọn
    public int getSelectedRow() { return table.getSelectedRow(); } // trả về chỉ số dòng đang chọn trong bảng

    public void showMessage(String msg) { // hiện hộp thoại thông báo
        JOptionPane.showMessageDialog(this, msg); // hiển thị nội dung msg
    } // kết thúc hàm

    public boolean confirm(String msg) { // hiện hộp thoại hỏi Có/Không
        return JOptionPane.showConfirmDialog(this, msg, "Xác nhận", // hiện hộp thoại với tiêu đề "Xác nhận"
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION; // true nếu người dùng chọn Yes
    } // kết thúc hàm

    // ---------- Gắn listener (controller gọi) ----------
    public void addAddListener(ActionListener l) { btnAdd.addActionListener(l); } // gắn xử lý cho nút Thêm
    public void addEditListener(ActionListener l) { btnEdit.addActionListener(l); } // gắn xử lý cho nút Sửa
    public void addDeleteListener(ActionListener l) { btnDelete.addActionListener(l); } // gắn xử lý cho nút Xoá
    public void addClearListener(ActionListener l) { btnClear.addActionListener(l); } // gắn xử lý cho nút Làm mới
    public void addTableSelectionListener(ListSelectionListener l) { // gắn xử lý khi chọn dòng trong bảng
        table.getSelectionModel().addListSelectionListener(l); // đăng ký listener vào mô hình chọn dòng
    } // kết thúc hàm
} // kết thúc lớp HangHoaView
