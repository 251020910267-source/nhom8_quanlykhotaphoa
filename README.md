# PHẦN MỀM QUẢN LÝ KHO HÀNG CỬA HÀNG TẠP HOÁ (ĐỀ TÀI 13)

- **Môn học:** Lập trình hướng đối tượng (OOP)
- **Giảng viên hướng dẫn:** Phùng Anh Tú, Hà Thị Kim Dung
- **Nhóm thực hiện:** Nhóm 8
- **Kho lưu trữ mã nguồn (GitHub):** https://github.com/251020910267-source/nhom8_quanlykhotaphoa

---

## 1. Giới thiệu phần mềm

Phần mềm **Quản lý kho hàng cửa hàng tạp hoá** được xây dựng bằng ngôn ngữ **Java (Java Swing)** trên nền tảng **Apache NetBeans (Maven)** theo mô hình lập trình hướng đối tượng và kiến trúc tách lớp (`Entity – Action – Controller – View – Utils`).

Toàn bộ dữ liệu của chương trình được lưu trữ và truy xuất hoàn toàn bằng định dạng **XML** (thông qua thư viện **JAXB**), không sử dụng cơ sở dữ liệu SQL hay file văn bản thuần (`.txt`). Chương trình hỗ trợ chủ cửa hàng quản lý tập trung danh mục hàng hoá, nhà cung cấp, nghiệp vụ nhập/xuất kho, kiểm soát tồn kho, tìm kiếm, thống kê và cảnh báo hàng hoá sắp hết hạn sử dụng.

---

## 2. Tài khoản đăng nhập chương trình

> **LƯU Ý:** Để đăng nhập và sử dụng phần mềm tại màn hình Đăng nhập (`LoginView`), vui lòng nhập thông tin tài khoản sau:
> - **Tên đăng nhập (Username):** `admin`
> - **Mật khẩu (Password):** `admin`

---

## 3. Phân công thành viên trong nhóm

| STT | Họ và tên | Mã SV | Vai trò | Công việc phụ trách |
| :---: | :--- | :---: | :--- | :--- |
| 1 | Đặng Quang Duy | 251020910267 | Nhóm trưởng – Kiến trúc | Thiết kế sơ đồ lớp, cấu trúc package, đăng nhập, tích hợp code, đóng gói phần mềm, viết Readme |
| 2 | Lương Xuân Thành | 251020910271 | Thành viên – Đối tượng 1 | Quản lý Hàng hoá (Thêm/Sửa/Xoá/Hiển thị, đọc/ghi XML) |
| 3 | Chu Minh Thuấn | 251020909884 | Thành viên – Đối tượng 2 | Quản lý Nhà cung cấp (Thêm/Sửa/Xoá/Hiển thị, đọc/ghi XML) |
| 4 | Lê Minh Hùng | 251020908122 | Thành viên – Nghiệp vụ | Xử lý phiếu Nhập/Xuất kho, tính thành tiền, kiểm tra lỗi nhập liệu |
| 5 | Nguyễn Tiến Dũng | 251020901280 | Thành viên – Tìm kiếm & Thống kê | Tìm kiếm gần đúng/theo khoảng, thống kê tồn kho, cảnh báo hết hạn, tổng hợp báo cáo |

---

## 4. Các chức năng chính

1. **Đăng nhập hệ thống (`LoginView`):**
   - Xác thực tài khoản qua lớp `CheckLogin` trước khi truy cập màn hình điều khiển chính (`MainView`).
2. **Quản lý Hàng hoá (`HangHoaView`):**
   - Thêm, sửa, xoá (có hộp thoại xác nhận) và hiển thị danh sách hàng hoá trên `JTable`.
   - Chọn nhóm hàng bằng Dropdown (`JComboBox`), chọn hạn sử dụng bằng `JCalendar`, ID tự động tăng, hiển thị đơn giá có dấu phân cách hàng nghìn.
   - Kiểm tra lỗi nhập liệu (bỏ trống trường bắt buộc, sai kiểu số, trùng dữ liệu).
3. **Quản lý Nhà cung cấp (`NhaCungCapView`):**
   - Thêm, sửa, xoá (có hộp thoại xác nhận) và hiển thị danh sách đối tác cung cấp.
   - Chọn một dòng trên bảng để nạp dữ liệu lên các ô nhập phục vụ sửa/xoá.
4. **Quản lý Phiếu Nhập / Xuất kho (`PhieuGiaoDichView`):**
   - Lập **Phiếu Nhập** (liên kết Hàng hoá & Nhà cung cấp) và **Phiếu Xuất** (liên kết Hàng hoá & Khách hàng).
   - Chọn ngày giao dịch bằng `JCalendar`, tự động tính thành tiền theo tính đa hình (`tinhThanhTien()`), kiểm tra và cảnh báo nếu số lượng xuất vượt quá tồn kho hiện tại.
   - Tìm kiếm và xoá phiếu (có hộp thoại xác nhận).
5. **Tìm kiếm, Thống kê & Cảnh báo (`ThongKeView`):**
   - Tìm kiếm gần đúng theo chuỗi (không phân biệt hoa/thường) và tìm kiếm theo khoảng số.
   - Thống kê tổng số mặt hàng, tổng giá trị danh mục, giá trị lớn nhất / nhỏ nhất.
   - **Tính năng nâng cao:** Cảnh báo tự động các mặt hàng sắp hết hạn sử dụng theo ngày.

---

## 5. Thiết kế hướng đối tượng & Cấu trúc Package

- **Tính đóng gói:** Các lớp thực thể (`HangHoa`, `NhaCungCap`, `PhieuGiaoDich`, `PhieuNhap`, `PhieuXuat`) khai báo thuộc tính `private` kèm `getter`/`setter`.
- **Tính kế thừa & Đa hình:** Lớp trừu tượng `PhieuGiaoDich` được kế thừa bởi `PhieuNhap` và `PhieuXuat`, ghi đè (`@Override`) phương thức `tinhThanhTien()`.
- **Interface:** Giao diện `IQuanLy` chuẩn hoá các thao tác quản lý dữ liệu chung.

### Cấu trúc Package trong dự án:
- `com.mycompany.quanlydoituongdacbiet.QuanLyDoiTuong`: Chứa lớp chạy chính (`QuanLyDoiTuongDacBiet.java`).
- `com.mycompany.quanlydoituongdacbiet.entity`: Chứa các lớp thực thể, lớp trừu tượng `PhieuGiaoDich` và các lớp ánh xạ XML.
- `com.mycompany.quanlydoituongdacbiet.action`: Chứa Interface `IQuanLy`, kiểm tra đăng nhập `CheckLogin` và các lớp xử lý nghiệp vụ, đọc/ghi XML.
- `com.mycompany.quanlydoituongdacbiet.view`: Chứa các màn hình giao diện Java Swing (`LoginView`, `MainView`, `HangHoaView`, `NhaCungCapView`, `PhieuGiaoDichView`, `ThongKeView`).
- `com.mycompany.quanlydoituongdacbiet.controller`: Chứa các lớp điều khiển sự kiện kết nối giữa `View` và `Action`.
- `com.mycompany.quanlydoituongdacbiet.utils`: Chứa các lớp tiện ích đọc/ghi XML (`FileUtils`) và định dạng ngày/tiền tệ (`FormatUtils`).

---

## 6. Yêu cầu môi trường & Hướng dẫn chạy chương trình

### Yêu cầu hệ thống
- **Java Development Kit (JDK):** JDK 17 trở lên (khuyến nghị **JDK 23**).
- **IDE (nếu chạy từ mã nguồn):** **Apache NetBeans 23** (dự án quản lý thư viện bằng Maven).

### Cách 1: Chạy trực tiếp phần mềm đã đóng gói (`.jar`)
1. Mở thư mục chứa file đóng gói `QuanLyKhoHangCuaHangTapHoa-1.0-SNAPSHOT.jar` (nằm trong thư mục `target/`).
2. Click đúp chuột vào file `QuanLyKhoHangCuaHangTapHoa-1.0-SNAPSHOT.jar` để mở chương trình.
3. Nếu máy tính chưa tự liên kết file `.jar` với Java, mở **Command Prompt (CMD)** tại thư mục chứa file và chạy lệnh:
   ```bash
   java -jar QuanLyKhoHangCuaHangTapHoa-1.0-SNAPSHOT.jar
