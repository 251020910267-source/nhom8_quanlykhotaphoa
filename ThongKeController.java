package com.mycompany.quanlykhohang.controller; // khai báo gói chứa lớp điều khiển

import com.mycompany.quanlykhohang.action.ManagerHangHoa; // nạp lớp đọc/ghi XML hàng hoá
import com.mycompany.quanlykhohang.action.ThongKeAction; // nạp lớp xử lý logic tìm kiếm & thống kê
import com.mycompany.quanlykhohang.entity.HangHoa; // nạp lớp đối tượng Hàng hoá
import com.mycompany.quanlykhohang.utils.FormatUtils; // nạp lớp tiện ích chuyển đổi chuỗi tiền sang số
import com.mycompany.quanlykhohang.view.ThongKeView; // nạp lớp giao diện Tìm kiếm & Thống kê
import java.awt.event.ActionEvent; // nạp lớp sự kiện bấm nút
import java.awt.event.ActionListener; // nạp giao diện lắng nghe sự kiện
import java.util.List; // nạp giao diện List
import java.util.Map; // nạp giao diện Map

/** Controller điều phối giữa ThongKeView (giao diện) và ThongKeAction (xử lý logic). */
public class ThongKeController { // bắt đầu định nghĩa lớp ThongKeController

    private final ThongKeView view; // tham chiếu tới giao diện ThongKeView
    private final ThongKeAction thongKeAction; // tham chiếu tới lớp xử lý nghiệp vụ ThongKeAction
    private final ManagerHangHoa managerHangHoa; // tham chiếu tới lớp đọc dữ liệu XML hàng hoá

    public ThongKeController(ThongKeView view) { // hàm khởi tạo nhận vào giao diện ThongKeView
        this.view = view; // lưu tham chiếu giao diện
        this.thongKeAction = new ThongKeAction(); // khởi tạo đối tượng xử lý logic thống kê
        this.managerHangHoa = new ManagerHangHoa(); // khởi tạo đối tượng đọc file HangHoa.xml

        this.view.addTimKiemListener(new TimKiemListener()); // gắn xử lý nút Tìm kiếm
        this.view.addCanhBaoListener(new CanhBaoListener()); // gắn xử lý nút Lọc hết hạn
        this.view.addLamMoiListener(new LamMoiListener()); // gắn xử lý nút Làm mới

        taiLaiDuLieuVaThongKe(); // tự động nạp dữ liệu từ XML và tính thống kê ngay khi mở cửa sổ
    } // kết thúc constructor

    public void showThongKeView() { // hiển thị cửa sổ giao diện
        view.setVisible(true); // bật trạng thái hiển thị JFrame
    } // kết thúc hàm

    /** Đọc dữ liệu mới nhất từ file HangHoa.xml, đổ lên bảng và cập nhật các chỉ số thống kê */
    public final void taiLaiDuLieuVaThongKe() { // tải lại toàn bộ dữ liệu
        List<HangHoa> dsHang = managerHangHoa.readListHangHoa(); // đọc danh sách hàng hoá từ file HangHoa.xml

        view.showListHangHoa(dsHang); // đổ toàn bộ danh sách lên bảng

        int tongMatHang = thongKeAction.tinhTongSoMatHang(dsHang); // đếm tổng số mặt hàng
        long tongGiaTri = thongKeAction.tinhTongDonGiaDanhMuc(dsHang); // tính tổng đơn giá danh mục
        HangHoa maxGia = thongKeAction.timHangGiaCaoNhat(dsHang); // tìm mặt hàng giá cao nhất
        HangHoa minGia = thongKeAction.timHangGiaThapNhat(dsHang); // tìm mặt hàng giá thấp nhất
        int soCanhBao = thongKeAction.layDanhSachCanhBaoHetHan(dsHang, 30).size(); // đếm số hàng hết hạn trong 30 ngày
        Map<String, Integer> thongKeNhom = thongKeAction.thongKeSoLuongTheoNhom(dsHang); // thống kê số lượng theo từng nhóm

        view.updateThongKe(tongMatHang, tongGiaTri, maxGia, minGia, soCanhBao, thongKeNhom); // đẩy kết quả lên giao diện
    } // kết thúc hàm taiLaiDuLieuVaThongKe

    // Xử lý sự kiện khi bấm nút "Tìm kiếm"
    private class TimKiemListener implements ActionListener { // cài đặt ActionListener
        @Override
        public void actionPerformed(ActionEvent e) { // chạy khi bấm nút Tìm kiếm
            String tuKhoa = view.getTuKhoa(); // lấy từ khoá người dùng nhập
            String strTu = view.getGiaTuText().trim(); // lấy chuỗi giá bắt đầu
            String strDen = view.getGiaDenText().trim(); // lấy chuỗi giá kết thúc

            long giaTu = 0; // mặc định giá thấp nhất là 0 nếu bỏ trống
            long giaDen = Long.MAX_VALUE; // mặc định giá cao nhất là vô cùng lớn nếu bỏ trống

            try { // bắt lỗi nếu người dùng nhập sai định dạng số
                if (!strTu.isEmpty()) { // nếu ô Giá từ có nhập dữ liệu
                    giaTu = FormatUtils.parseMoney(strTu); // chuyển chuỗi tiền (ví dụ "10.000") thành số 10000
                    if (giaTu < 0) { // kiểm tra số âm
                        view.showMessage("Giá bắt đầu không được âm!");
                        return;
                    }
                }
                if (!strDen.isEmpty()) { // nếu ô Giá đến có nhập dữ liệu
                    giaDen = FormatUtils.parseMoney(strDen); // chuyển chuỗi tiền thành số
                    if (giaDen < 0) { // kiểm tra số âm
                        view.showMessage("Giá kết thúc không được âm!");
                        return;
                    }
                }
                if (!strTu.isEmpty() && !strDen.isEmpty() && giaTu > giaDen) { // kiểm tra khoảng hợp lệ
                    view.showMessage("Khoảng giá không hợp lệ: 'Đơn giá từ' phải nhỏ hơn hoặc bằng 'Đến'!");
                    return;
                }
            } catch (NumberFormatException ex) { // bắt ngoại lệ nhập chữ thay vì số
                view.showMessage("Vui lòng nhập khoảng đơn giá là số hợp lệ (ví dụ: 5000 hoặc 5.000)!");
                return;
            }

            List<HangHoa> dsHang = managerHangHoa.readListHangHoa(); // đọc danh sách hàng hoá mới nhất từ XML
            List<HangHoa> ketQua = thongKeAction.timKiemKetHop(dsHang, tuKhoa, giaTu, giaDen); // lọc kết hợp từ khoá và khoảng giá
            view.showListHangHoa(ketQua); // cập nhật bảng hiển thị kết quả tìm thấy

            if (ketQua.isEmpty()) { // nếu không tìm thấy mặt hàng nào
                view.showMessage("Không tìm thấy mặt hàng nào phù hợp với điều kiện tìm kiếm!");
            }
        }
    } // kết thúc lớp TimKiemListener

    // Xử lý sự kiện khi bấm nút "Lọc hàng sắp/đã hết hạn"
    private class CanhBaoListener implements ActionListener { // cài đặt ActionListener
        @Override
        public void actionPerformed(ActionEvent e) { // chạy khi bấm nút Lọc hết hạn
            List<HangHoa> dsHang = managerHangHoa.readListHangHoa(); // đọc danh sách hàng hoá từ XML
            List<HangHoa> dsCanhBao = thongKeAction.layDanhSachCanhBaoHetHan(dsHang, 30); // lọc hàng hết hạn trong 30 ngày
            view.showListHangHoa(dsCanhBao); // hiển thị danh sách lên bảng
            if (dsCanhBao.isEmpty()) {
                view.showMessage("Tuyệt vời! Không có mặt hàng nào hết hạn hoặc sắp hết hạn trong 30 ngày tới.");
            } else {
                view.showMessage("Có " + dsCanhBao.size() + " mặt hàng đã hết hạn (màu đỏ) hoặc sắp hết hạn trong 30 ngày tới!");
            }
        }
    } // kết thúc lớp CanhBaoListener

    // Xử lý sự kiện khi bấm nút "Hiển thị tất cả & Cập nhật"
    private class LamMoiListener implements ActionListener { // cài đặt ActionListener
        @Override
        public void actionPerformed(ActionEvent e) { // chạy khi bấm nút Làm mới
            view.clearSearchInputs(); // xoá trắng các ô nhập tìm kiếm
            taiLaiDuLieuVaThongKe(); // nạp lại dữ liệu gốc và tính lại thống kê
        }
    } // kết thúc lớp LamMoiListener
} // kết thúc lớp ThongKeController