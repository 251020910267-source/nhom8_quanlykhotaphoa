package com.mycompany.quanlydoituongdacbiet.view; // khai báo gói chứa các lớp giao diện

import com.mycompany.quanlydoituongdacbiet.controller.ThongKeController; // nạp Controller để chạy thử hàm main
import com.mycompany.quanlydoituongdacbiet.entity.HangHoa; // nạp lớp đối tượng Hàng hoá
import com.mycompany.quanlydoituongdacbiet.utils.FormatUtils; // nạp lớp tiện ích định dạng tiền và ngày
import java.awt.BorderLayout; // nạp bố cục chia 5 vùng Bắc-Nam-Đông-Tây-Giữa
import java.awt.Color; // nạp lớp màu sắc
import java.awt.Component; // nạp lớp thành phần giao diện cơ sở
import java.awt.FlowLayout; // nạp bố cục xếp ngang tự động xuống dòng
import java.awt.Font; // nạp lớp phông chữ
import java.awt.GridBagConstraints; // nạp lớp ràng buộc vị trí cho GridBagLayout
import java.awt.GridBagLayout; // nạp bố cục dạng lưới linh hoạt
import java.awt.GridLayout; // nạp bố cục chia đều các ô bằng nhau
import java.awt.Insets; // nạp lớp căn lề trên-trái-dưới-phải
import java.awt.event.ActionListener; // nạp giao diện lắng nghe sự kiện bấm nút
import java.util.Date; // nạp lớp Date
import java.util.List; // nạp giao diện List
import java.util.Map; // nạp giao diện Map
import javax.swing.BorderFactory; // nạp lớp tạo đường viền
import javax.swing.JButton; // nạp thành phần nút bấm
import javax.swing.JFrame; // nạp thành phần cửa sổ chính
import javax.swing.JLabel; // nạp thành phần nhãn chữ
import javax.swing.JOptionPane; // nạp hộp thoại thông báo bật lên
import javax.swing.JPanel; // nạp khung chứa các thành phần
import javax.swing.JScrollPane; // nạp khung có thanh cuộn
import javax.swing.JTable; // nạp thành phần bảng dữ liệu
import javax.swing.JTextArea; // nạp ô văn bản nhiều dòng
import javax.swing.JTextField; // nạp ô nhập văn bản một dòng
import javax.swing.ListSelectionModel; // nạp chế độ chọn dòng trên bảng
import javax.swing.SwingConstants; // nạp các hằng số căn lề trái/phải/giữa
import javax.swing.table.DefaultTableCellRenderer; // nạp lớp tuỳ biến cách vẽ từng ô của bảng
import javax.swing.table.DefaultTableModel; // nạp mô hình quản lý dữ liệu của bảng

/** Giao diện Tìm kiếm & Thống kê báo cáo kho hàng (Thành viên 5). */
public class ThongKeView extends JFrame { // lớp giao diện kế thừa từ cửa sổ JFrame

    private static final String[] COLUMNS = {"STT", "Mã hàng", "Tên hàng", "Nhóm hàng", "ĐVT", "Đơn giá", "Hạn sử dụng"}; // tiêu đề 7 cột

    private final DefaultTableModel tableModel; // mô hình lưu trữ dữ liệu các dòng của bảng
    private final JTable table; // đối tượng bảng hiển thị lên màn hình

    private final JTextField txtTuKhoa = new JTextField(18); // ô nhập từ khoá tìm kiếm
    private final JTextField txtGiaTu = new JTextField(10); // ô nhập mức giá bắt đầu
    private final JTextField txtGiaDen = new JTextField(10); // ô nhập mức giá kết thúc

    private final JButton btnTimKiem = new JButton("Tìm kiếm"); // nút thực hiện tìm kiếm
    private final JButton btnCanhBaoHSD = new JButton("Lọc hàng sắp/đã hết hạn"); // nút lọc hàng sắp hết hạn
    private final JButton btnLamMoi = new JButton("Hiển thị tất cả & Cập nhật"); // nút làm mới dữ liệu

    private final JLabel lblTongMatHang = new JLabel("0"); // nhãn hiển thị tổng số mặt hàng
    private final JLabel lblTongGiaTri = new JLabel("0 đ"); // nhãn hiển thị tổng đơn giá danh mục
    private final JLabel lblGiaCaoNhat = new JLabel("Chưa có dữ liệu"); // nhãn hiển thị hàng giá cao nhất
    private final JLabel lblGiaThapNhat = new JLabel("Chưa có dữ liệu"); // nhãn hiển thị hàng giá thấp nhất
    private final JLabel lblSoHangCanhBao = new JLabel("0"); // nhãn hiển thị số lượng hàng sắp hết hạn
    private final JTextArea txtBaoCaoNhom = new JTextArea(5, 30); // vùng văn bản hiển thị thống kê theo từng nhóm

    public ThongKeView() { // hàm khởi tạo giao diện
        super("Quản lý kho hàng tạp hoá - Tìm kiếm & Thống kê"); // đặt tiêu đề cho thanh cửa sổ
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // đóng cửa sổ này chỉ tắt riêng nó

        // 1. Tiêu đề chính phía trên
        JLabel title = new JLabel("TÌM KIẾM & THỐNG KÊ BÁO CÁO KHO HÀNG", SwingConstants.CENTER); // tạo nhãn tiêu đề căn giữa
        title.setFont(new Font("Times New Roman", Font.BOLD, 22)); // đặt phông chữ Times New Roman, in đậm, cỡ 22
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // tạo khoảng trống lề trên và dưới 10px

        // 2. Khu vực Bộ lọc Tìm kiếm
        JPanel pnlSearch = new JPanel(new GridBagLayout()); // tạo khung chứa các ô tìm kiếm
        pnlSearch.setBorder(BorderFactory.createTitledBorder("Bộ lọc tìm kiếm hàng hoá")); // viền có tiêu đề
        GridBagConstraints g = new GridBagConstraints(); // đối tượng cấu hình toạ độ ô trong lưới
        g.insets = new Insets(5, 8, 5, 8); // khoảng cách lề giữa các thành phần
        g.anchor = GridBagConstraints.WEST; // căn các thành phần về bên trái

        txtGiaTu.setHorizontalAlignment(SwingConstants.RIGHT); // căn phải số tiền ô Giá từ
        txtGiaDen.setHorizontalAlignment(SwingConstants.RIGHT); // căn phải số tiền ô Giá đến

        g.gridx = 0; g.gridy = 0; // vị trí cột 0, hàng 0
        pnlSearch.add(new JLabel("Từ khoá (Mã / Tên / Nhóm):"), g); // nhãn hướng dẫn nhập từ khoá
        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1; // cột 1, giãn ngang tự do
        pnlSearch.add(txtTuKhoa, g); // ô nhập từ khoá

        g.gridx = 2; g.fill = GridBagConstraints.NONE; g.weightx = 0; // cột 2
        pnlSearch.add(new JLabel("Đơn giá từ (đ):"), g); // nhãn Đơn giá từ
        g.gridx = 3; // cột 3
        pnlSearch.add(txtGiaTu, g); // ô nhập Giá từ

        g.gridx = 4; // cột 4
        pnlSearch.add(new JLabel("Đến (đ):"), g); // nhãn Đến
        g.gridx = 5; // cột 5
        pnlSearch.add(txtGiaDen, g); // ô nhập Giá đến

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4)); // thanh chứa nút bấm căn giữa
        pnlButtons.add(btnTimKiem); // gắn nút Tìm kiếm
        pnlButtons.add(btnCanhBaoHSD); // gắn nút Lọc hết hạn
        pnlButtons.add(btnLamMoi); // gắn nút Làm mới

        JPanel pnlTop = new JPanel(new BorderLayout()); // khung gom tiêu đề, bộ lọc và nút bấm
        pnlTop.add(title, BorderLayout.NORTH); // tiêu đề ở trên cùng
        pnlTop.add(pnlSearch, BorderLayout.CENTER); // khung tìm kiếm ở giữa
        pnlTop.add(pnlButtons, BorderLayout.SOUTH); // hàng nút bấm ở dưới cùng

        // 3. Bảng hiển thị danh sách kết quả
        tableModel = new DefaultTableModel(COLUMNS, 0) { // khởi tạo mô hình bảng 7 cột, 0 dòng ban đầu
            @Override
            public boolean isCellEditable(int row, int column) { // quyết định ô có được sửa trực tiếp không
                return false; // khoá không cho sửa trực tiếp trên bảng
            }
        };
        table = new JTable(tableModel); // tạo bảng JTable gắn với tableModel
        table.setRowHeight(24); // chiều cao mỗi dòng 24px
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // chỉ cho chọn 1 dòng một lúc
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD)); // in đậm tiêu đề cột
        table.getColumnModel().getColumn(0).setPreferredWidth(40); // cột STT rộng 40px
        table.getColumnModel().getColumn(2).setPreferredWidth(230); // cột Tên hàng rộng 230px
        table.setDefaultRenderer(Object.class, new RowRenderer()); // áp dụng bộ vẽ tuỳ chỉnh RowRenderer

        // 4. Khu vực Báo cáo & Thống kê phía dưới
        JPanel pnlBottom = new JPanel(new GridLayout(1, 2, 10, 0)); // khung chia 2 cột bằng nhau
        pnlBottom.setBorder(BorderFactory.createTitledBorder("Tổng hợp báo cáo & Thống kê")); // viền tiêu đề thống kê

        JPanel pnlStats = new JPanel(new GridLayout(5, 2, 6, 6)); // lưới 5 hàng 2 cột chứa các chỉ số
        pnlStats.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10)); // lề trong khung chỉ số
        Font boldFont = new Font("SansSerif", Font.BOLD, 12); // phông chữ in đậm cỡ 12

        lblTongMatHang.setFont(boldFont); // in đậm nhãn tổng mặt hàng
        lblTongMatHang.setForeground(new Color(0, 102, 204)); // màu xanh dương
        lblTongGiaTri.setFont(boldFont); // in đậm nhãn tổng giá trị
        lblGiaCaoNhat.setFont(boldFont); // in đậm nhãn giá cao nhất
        lblGiaCaoNhat.setForeground(new Color(0, 128, 0)); // màu xanh lá
        lblGiaThapNhat.setFont(boldFont); // in đậm nhãn giá thấp nhất
        lblSoHangCanhBao.setFont(boldFont); // in đậm nhãn số hàng cảnh báo
        lblSoHangCanhBao.setForeground(Color.RED); // màu đỏ cảnh báo

        pnlStats.add(new JLabel("Tổng số mặt hàng trong kho:")); // nhãn mô tả hàng 1
        pnlStats.add(lblTongMatHang); // giá trị hàng 1
        pnlStats.add(new JLabel("Tổng đơn giá danh mục:")); // nhãn mô tả hàng 2
        pnlStats.add(lblTongGiaTri); // giá trị hàng 2
        pnlStats.add(new JLabel("Mặt hàng đơn giá CAO NHẤT:")); // nhãn mô tả hàng 3
        pnlStats.add(lblGiaCaoNhat); // giá trị hàng 3
        pnlStats.add(new JLabel("Mặt hàng đơn giá THẤP NHẤT:")); // nhãn mô tả hàng 4
        pnlStats.add(lblGiaThapNhat); // giá trị hàng 4
        pnlStats.add(new JLabel("Hàng đã/sắp hết hạn (30 ngày):")); // nhãn mô tả hàng 5
        pnlStats.add(lblSoHangCanhBao); // giá trị hàng 5

        txtBaoCaoNhom.setEditable(false); // khoá không cho sửa khung báo cáo nhóm
        txtBaoCaoNhom.setFont(new Font("Monospaced", Font.PLAIN, 12)); // font chữ đơn cách căn thẳng hàng
        JScrollPane scrollReport = new JScrollPane(txtBaoCaoNhom); // bọc khung báo cáo vào thanh cuộn
        scrollReport.setBorder(BorderFactory.createTitledBorder("Phân bổ mặt hàng theo nhóm")); // tiêu đề khung nhóm

        pnlBottom.add(pnlStats); // đưa cột chỉ số vào bên trái
        pnlBottom.add(scrollReport); // đưa cột báo cáo nhóm vào bên phải

        // Ráp 3 phần vào cửa sổ chính
        add(pnlTop, BorderLayout.NORTH); // khu vực tìm kiếm ở phía Bắc (trên)
        add(new JScrollPane(table), BorderLayout.CENTER); // bảng dữ liệu ở vùng Trung tâm (giữa)
        add(pnlBottom, BorderLayout.SOUTH); // khu vực thống kê ở phía Nam (dưới)

        setSize(960, 660); // kích thước cửa sổ rộng 960px, cao 660px
        setLocationRelativeTo(null); // căn cửa sổ nằm chính giữa màn hình
    } // kết thúc constructor ThongKeView

    /** Lớp nội bộ tuỳ chỉnh cách hiển thị từng ô: định dạng tiền, ngày và tô đỏ hàng hết hạn */
    private class RowRenderer extends DefaultTableCellRenderer { // kế thừa lớp vẽ ô mặc định
        @Override
        public Component getTableCellRendererComponent(JTable t, Object value, // chạy cho từng ô của bảng
                                                       boolean isSelected, boolean hasFocus, int row, int col) {
            Object shown = value; // biến lưu giá trị sẽ hiển thị
            if (value instanceof Long) { // nếu dữ liệu gốc là số Long (cột Đơn giá)
                shown = FormatUtils.formatMoney((Long) value); // định dạng thành chuỗi tiền Việt (10.000 đ)
            } else if (value instanceof Date) { // nếu dữ liệu gốc là ngày Date (cột Hạn sử dụng)
                shown = FormatUtils.formatDate((Date) value); // định dạng thành chuỗi dd/MM/yyyy
            }
            super.getTableCellRendererComponent(t, shown, isSelected, hasFocus, row, col); // vẽ ô với chuỗi đã định dạng
            setHorizontalAlignment(col == 5 ? SwingConstants.RIGHT // cột 5 (Đơn giá) căn phải
                    : (col == 0 || col == 4 || col == 6) ? SwingConstants.CENTER : SwingConstants.LEFT); // STT/ĐVT/HSD căn giữa
            Object hsd = t.getModel().getValueAt(t.convertRowIndexToModel(row), 6); // lấy ngày ở cột 6 (Hạn sử dụng)
            boolean expired = hsd instanceof Date && ((Date) hsd).before(new Date()); // kiểm tra xem đã hết hạn chưa
            if (!isSelected) { // nếu dòng không đang được chọn
                setForeground(expired ? Color.RED : Color.BLACK); // hết hạn tô chữ ĐỎ, còn hạn tô ĐEN
            }
            return this; // trả về ô đã vẽ
        }
    } // kết thúc lớp RowRenderer

    // ---------- Các hàm cập nhật dữ liệu lên giao diện ----------
    public void showListHangHoa(List<HangHoa> list) { // đổ danh sách hàng hoá lên bảng
        tableModel.setRowCount(0); // xoá sạch các dòng cũ
        int stt = 1; // số thứ tự bắt đầu từ 1
        for (HangHoa h : list) { // duyệt từng hàng hoá h
            tableModel.addRow(new Object[]{ // thêm một dòng mới gồm 7 cột
                    stt++, h.getMaHang(), h.getTenHang(), h.getNhomHang(), // STT, Mã, Tên, Nhóm
                    h.getDonViTinh(), Long.valueOf(h.getDonGia()), h.getHanSuDung() // ĐVT, Đơn giá, Hạn sử dụng
            });
        }
    } // kết thúc hàm showListHangHoa

    public void updateThongKe(int tongMatHang, long tongGiaTri, HangHoa maxGia, HangHoa minGia, // cập nhật số liệu thống kê
                              int soCanhBao, Map<String, Integer> thongKeNhom) {
        lblTongMatHang.setText(tongMatHang + " mặt hàng"); // hiển thị tổng số mặt hàng
        lblTongGiaTri.setText(FormatUtils.formatMoney(tongGiaTri)); // hiển thị tổng đơn giá danh mục

        if (maxGia != null) { // nếu có mặt hàng giá cao nhất
            lblGiaCaoNhat.setText(maxGia.getTenHang() + " (" + FormatUtils.formatMoney(maxGia.getDonGia()) + ")");
        } else {
            lblGiaCaoNhat.setText("Không có dữ liệu");
        }

        if (minGia != null) { // nếu có mặt hàng giá thấp nhất
            lblGiaThapNhat.setText(minGia.getTenHang() + " (" + FormatUtils.formatMoney(minGia.getDonGia()) + ")");
        } else {
            lblGiaThapNhat.setText("Không có dữ liệu");
        }

        lblSoHangCanhBao.setText(soCanhBao + " mặt hàng"); // hiển thị số lượng hàng cảnh báo

        StringBuilder sb = new StringBuilder(); // ghép chuỗi nội dung báo cáo nhóm
        if (thongKeNhom.isEmpty()) {
            sb.append("Chưa có dữ liệu nhóm hàng.");
        } else {
            for (Map.Entry<String, Integer> entry : thongKeNhom.entrySet()) { // duyệt từng cặp (Nhóm - Số lượng)
                sb.append(String.format("• %-22s : %d mặt hàng\n", entry.getKey(), entry.getValue()));
            }
        }
        txtBaoCaoNhom.setText(sb.toString()); // đổ chuỗi báo cáo vào khung văn bản
    } // kết thúc hàm updateThongKe

    // ---------- Các hàm lấy dữ liệu nhập & đăng ký sự kiện ----------
    public String getTuKhoa() { return txtTuKhoa.getText(); } // lấy từ khoá tìm kiếm
    public String getGiaTuText() { return txtGiaTu.getText(); } // lấy giá bắt đầu
    public String getGiaDenText() { return txtGiaDen.getText(); } // lấy giá kết thúc

    public void clearSearchInputs() { // xoá trắng các ô nhập tìm kiếm
        txtTuKhoa.setText("");
        txtGiaTu.setText("");
        txtGiaDen.setText("");
    }

    public void showMessage(String msg) { // hiển thị hộp thoại thông báo
        JOptionPane.showMessageDialog(this, msg);
    }

    public void addTimKiemListener(ActionListener l) { btnTimKiem.addActionListener(l); } // gắn sự kiện nút Tìm kiếm
    public void addCanhBaoListener(ActionListener l) { btnCanhBaoHSD.addActionListener(l); } // gắn sự kiện nút Lọc hết hạn
    public void addLamMoiListener(ActionListener l) { btnLamMoi.addActionListener(l); } // gắn sự kiện nút Làm mới

    // Hàm main để chạy thử độc lập màn hình Tìm kiếm & Thống kê
    public static void main(String[] args) { // điểm bắt đầu chạy thử
        java.awt.EventQueue.invokeLater(() -> { // tạo giao diện trên luồng sự kiện chuẩn
            ThongKeView view = new ThongKeView(); // khởi tạo giao diện
            ThongKeController controller = new ThongKeController(view); // khởi tạo Controller gắn với giao diện
            controller.showThongKeView(); // hiển thị cửa sổ lên màn hình
        });
    } // kết thúc hàm main
} // kết thúc lớp ThongKeView