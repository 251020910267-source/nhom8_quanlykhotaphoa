package com.mycompany.quanlykhohang.controller; // gói chứa lớp điều khiển

import com.mycompany.quanlykhohang.action.ManagerHangHoa; // lớp nghiệp vụ
import com.mycompany.quanlykhohang.entity.HangHoa; // lớp hàng hoá
import com.mycompany.quanlykhohang.view.HangHoaView; // lớp giao diện
import java.awt.event.ActionEvent; // sự kiện bấm nút
import java.awt.event.ActionListener; // giao diện lắng nghe sự kiện bấm nút
import javax.swing.event.ListSelectionEvent; // sự kiện chọn dòng trong bảng
import javax.swing.event.ListSelectionListener; // giao diện lắng nghe việc chọn dòng

public class HangHoaController { // nối giao diện (view) với nghiệp vụ (manager)

    private final HangHoaView view; // tham chiếu tới giao diện
    private final ManagerHangHoa manager; // tham chiếu tới lớp nghiệp vụ

    public HangHoaController(HangHoaView view) { // constructor nhận giao diện
        this.view = view; // lưu giao diện
        this.manager = new ManagerHangHoa(); // tạo lớp nghiệp vụ (tự đọc file XML)
        view.addAddListener(new AddListener()); // gắn xử lý cho nút Thêm
        view.addEditListener(new EditListener()); // gắn xử lý cho nút Sửa
        view.addDeleteListener(new DeleteListener()); // gắn xử lý cho nút Xoá
        view.addClearListener(new ClearListener()); // gắn xử lý cho nút Làm mới
        view.addTableSelectionListener(new SelectionListener()); // gắn xử lý khi chọn dòng trong bảng
        capNhatBang(); // hiển thị danh sách ban đầu lên bảng
    } // kết thúc constructor

    public void showView() { // hiện cửa sổ
        view.setVisible(true); // bật hiển thị
    } // kết thúc hàm

    /** Nạp lại bảng và danh sách gợi ý nhà cung cấp rồi xoá form. */
    private void capNhatBang() { // làm mới giao diện theo dữ liệu
        view.showListHangHoa(manager.getListHangHoa()); // nạp lại bảng
        view.napNhaCungCap(manager.getDanhSachNhaCungCap()); // nạp lại gợi ý nhà cung cấp
        view.clearForm(); // xoá trắng form
    } // kết thúc hàm

    class AddListener implements ActionListener { // xử lý nút Thêm
        public void actionPerformed(ActionEvent e) { // chạy khi bấm nút
            HangHoa h = view.getHangHoaInfo(); // lấy dữ liệu từ form
            if (h == null) return; // đơn giá nhập sai (view đã báo) thì dừng
            String error = manager.add(h); // thêm; nhận về thông báo lỗi hoặc null
            if (error != null) { // có lỗi
                view.showMessage(error); // hiện lỗi cho người dùng
                return; // dừng
            } // kết thúc if
            capNhatBang(); // nạp lại bảng và xoá form
            view.showMessage("Thêm hàng hoá thành công."); // báo thành công
        } // kết thúc hàm
    } // kết thúc lớp AddListener

    class EditListener implements ActionListener { // xử lý nút Sửa
        public void actionPerformed(ActionEvent e) { // chạy khi bấm nút
            if (view.getSelectedId() < 0) { // chưa chọn dòng nào
                view.showMessage("Hãy chọn hàng hoá cần sửa trong bảng."); // nhắc người dùng
                return; // dừng
            } // kết thúc if
            HangHoa h = view.getHangHoaInfo(); // lấy dữ liệu từ form
            if (h == null) return; // dữ liệu sai thì dừng
            String error = manager.edit(h); // sửa; nhận lỗi hoặc null
            if (error != null) { // có lỗi
                view.showMessage(error); // hiện lỗi
                return; // dừng
            } // kết thúc if
            capNhatBang(); // nạp lại bảng và xoá form
            view.showMessage("Cập nhật hàng hoá thành công."); // báo thành công
        } // kết thúc hàm
    } // kết thúc lớp EditListener

    class DeleteListener implements ActionListener { // xử lý nút Xoá
        public void actionPerformed(ActionEvent e) { // chạy khi bấm nút
            if (view.getSelectedId() < 0) { // chưa chọn dòng
                view.showMessage("Hãy chọn hàng hoá cần xoá trong bảng."); // nhắc người dùng
                return; // dừng
            } // kết thúc if
            if (!view.confirm("Bạn có chắc muốn xoá hàng hoá này?")) return; // hỏi xác nhận, chọn "No" thì dừng
            if (manager.delete(view.getSelectedId())) { // xoá theo id, true nếu thành công
                capNhatBang(); // nạp lại bảng và xoá form
            } else { // không xoá được
                view.showMessage("Không tìm thấy hàng hoá cần xoá."); // báo lỗi
            } // kết thúc if-else
        } // kết thúc hàm
    } // kết thúc lớp DeleteListener

    class ClearListener implements ActionListener { // xử lý nút Làm mới
        public void actionPerformed(ActionEvent e) { // chạy khi bấm nút
            view.clearForm(); // xoá trắng form, bỏ chọn dòng
        } // kết thúc hàm
    } // kết thúc lớp ClearListener

    class SelectionListener implements ListSelectionListener { // xử lý khi chọn dòng trong bảng
        public void valueChanged(ListSelectionEvent e) { // chạy khi lựa chọn thay đổi
            if (e.getValueIsAdjusting()) return; // bỏ qua sự kiện trung gian khi đang kéo chọn
            int row = view.getSelectedRow(); // lấy chỉ số dòng đang chọn (-1 nếu không có)
            if (row >= 0 && row < manager.getListHangHoa().size()) { // dòng hợp lệ
                view.fillForm(manager.getListHangHoa().get(row)); // đổ dữ liệu dòng đó lên form (thứ tự bảng = thứ tự danh sách)
            } // kết thúc if
        } // kết thúc hàm
    } // kết thúc lớp SelectionListener
} // kết thúc lớp HangHoaController
