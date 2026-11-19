
## 1. Công nghệ sử dụng
- **Ngôn ngữ:** Java (JDK 23)
- **Công cụ phát triển (IDE):** Apache NetBeans 23 (Maven Project)
- **Giao diện:** Java Swing
- **Lưu trữ dữ liệu:** File XML (JAXB)

## 2. Tài khoản đăng nhập hệ thống
- **Tài khoản (Username):** `admin`
- **Mật khẩu (Password):** `admin`

## 3. Phân công thành viên trong nhóm
| STT | Họ và tên | Mã SV | Vai trò | Công việc phụ trách |
| :--- | :--- | :--- | :--- | :--- |
| 1 | Đặng Quang Duy | 251020910267 | Nhóm trưởng - Kiến trúc | Thiết kế sơ đồ lớp, cấu trúc package, đăng nhập, tích hợp code, đóng gói phần mềm, viết Readme |
| 2 | Lương Xuân Thành | 251020910271 |Thành viên - Đối tượng 1 | Quản lý Hàng hoá (Thêm/Sửa/Xoá/Hiển thị, đọc/ghi XML) |
| 3 | Chu Minh Thuấn | 251020909884 | Thành viên - Đối tượng 2 | Quản lý Nhà cung cấp (Thêm/Sửa/Xoá/Hiển thị, đọc/ghi XML) |
| 4 | Lê Minh Hùng | 251020908122 | Thành viên - Nghiệp vụ | Xử lý phiếu Nhập/Xuất kho, tính thành tiền, kiểm tra lỗi nhập liệu |
| 5 | Nguyễn Tiến Dũng | 251020901280 | Thành viên - Tìm kiếm & Thống kê | Tìm kiếm gần đúng/theo khoảng, thống kê tồn kho, tổng hợp báo cáo |

## 4. Cấu trúc Package
- `com.mycompany.quanlydoituongdacbiet.QuanLyDoiTuong`: Chứa lớp chạy chính (`Main`).
- `com.mycompany.quanlydoituongdacbiet.entity`: Chứa các lớp thực thể, lớp trừu tượng `PhieuGiaoDich` và các lớp ánh xạ XML.
- `com.mycompany.quanlydoituongdacbiet.action`: Chứa Interface `IQuanLy`, xử lý đăng nhập `CheckLogin` và các lớp quản lý nghiệp vụ.
- `com.mycompany.quanlydoituongdacbiet.view`: Chứa các màn hình giao diện (`LoginView`, `MainView`,...).
- `com.mycompany.quanlydoituongdacbiet.controller`: Chứa các lớp điều khiển sự kiện giữa View và Action.
- `com.mycompany.quanlydoituongdacbiet.utils`: Chứa lớp tiện ích đọc/ghi file XML (`FileUtils`).

## 5. Hướng dẫn cài đặt và chạy chương trình
1. Mở dự án trên **Apache NetBeans 23** (sử dụng **JDK 23**).
2. Chuột phải vào dự án chọn **Clean and Build** (hoặc nhấn `Shift + F11`) để đóng gói.
3. Nhấn `F6` để chạy trực tiếp hoặc chạy file đóng gói tại thư mục `target/QuanLyDoiTuongDacBiet-1.0-SNAPSHOT.jar`.