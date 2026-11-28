# PHẦN MỀM QUẢN LÝ KHO HÀNG CỬA HÀNG TẠP HOÁ — BẢN THỰC TẾ (VERSION 2)

- **Môn học:** Lập trình hướng đối tượng (Java OOP) — Trường Đại học Phenikaa
- **Giảng viên hướng dẫn:** Phùng Anh Tú, Hà Thị Kim Dung
- **Đề tài 13 — Nhóm thực hiện:** Nhóm 8

---

## 1. Tài khoản đăng nhập chương trình

> **THÔNG TIN ĐĂNG NHẬP BẮT BUỘC (`LoginView`):**
> - **Tên đăng nhập (Username):** `admin`
> - **Mật khẩu (Password):** `admin`

---

## 2. Các cải tiến nghiệp vụ thực tế ở Version 2 (theo góp ý kiểm tra lần 1)

1. **Tách Danh mục Hàng hoá (`HangHoa`) và Lô hàng (`LoHang`) — Giải quyết bài toán Hạn sử dụng theo Nhà cung cấp:**
   - Cùng một sản phẩm (ví dụ: *Thùng sữa đậu nành Fami*, mã vạch `83VNHS`) khi nhập từ **2 Nhà cung cấp khác nhau** sẽ giữ chung **1 Mã hàng (`83VNHS`)** trong danh mục, nhưng sinh ra **2 Lô hàng độc lập (`LO00001`, `LO00002`)** lưu trong `lohang.xml` với **Hạn sử dụng (HSD) khác nhau** và **Giá vốn nhập khác nhau**.
   - Khi xuất bán (`PhieuXuat`), hệ thống tự động sắp xếp các lô còn hạn theo nguyên tắc **FEFO (*First Expired – First Out*: hết hạn trước xuất trước)** và **khoá chặn tuyệt đối** việc xuất bán lô đã hết hạn.

2. **Quản lý Mã đơn hàng / Số Bill (`maDonHang`) chuẩn thực tế:**
   - Phân biệt rõ 3 cấp mã:
     - **Mã mặt hàng (`maHang`)**: định danh sản phẩm (ví dụ `83VNHS`).
     - **Mã lô kho (`LO00001`)**: định danh từng đợt hàng nhập nằm trong kho.
     - **Mã đơn hàng / Số chứng từ (`DHN0001`, `HDX0001`, `BBH0001`)**: định danh đơn giao dịch/hoá đơn.
   - **Quy tắc kiểm soát:** Nhập hàng từ **2 Nhà cung cấp khác nhau** bắt buộc phải lập **2 Mã đơn nhập khác nhau** (hệ thống tự động chặn nếu gộp 2 nhà cung cấp vào chung 1 mã đơn). Ngược lại, nếu nhập nhiều mặt hàng từ **cùng 1 Nhà cung cấp** hoặc bán nhiều món cho **cùng 1 Khách hàng**, người dùng có thể tick *"Gộp nhiều món chung Mã đơn / Bill này"* để in tất cả mặt hàng trên **1 tờ Bill duy nhất**.

3. **Trạng thái hàng trong kho & Nghiệp vụ Báo hư hỏng (`TrangThaiLoHang`, `PhieuHuy`, `LyDoHuHong`):**
   - Mỗi lô hàng tự động phản ánh chính xác 6 trạng thái vòng đời:
     - `Còn trong kho`
     - `Còn hàng (có hư hỏng)`
     - `Sắp hết hạn (<= 30 ngày)`
     - `Đã hết hạn - cấm xuất`
     - `Đã xuất kho hết`
     - `Bị hư hỏng / Đã huỷ`
   - Hỗ trợ lập **Phiếu Huỷ / Biên bản hư hỏng (`PhieuHuy`)** ngay tại màn hình Phiếu giao dịch hoặc màn hình Quản lý Lô & Tồn kho với đầy đủ nguyên nhân thực tế ở tạp hoá:
     - *Chuột / côn trùng cắn phá*
     - *Thời tiết: ẩm mốc, ngấm nước, nóng chảy*
     - *Hết hạn sử dụng*
     - *Vỡ / móp méo / rách bao bì*
     - *Mất mát / thất lạc*

4. **Xuất Bill / In Chứng từ (`HoaDonDialog`) & Xuất Báo cáo Thống kê:**
   - Xem trước và xuất Bill cho cả 3 loại chứng từ (**Phiếu Nhập Kho**, **Hoá Đơn Xuất Bán Hàng** có tính thuế VAT 10%, **Biên Bản Xử Lý Hàng Hư Hỏng**), tự động đọc thành tiền bằng chữ tiếng Việt.
   - Hỗ trợ **In trực tiếp ra máy in / Microsoft Print to PDF**, lưu file **`.html`** trình bày chuẩn, hoặc lưu file **`.txt`** khổ giấy in nhiệt K80.
   - Toàn bộ cơ sở dữ liệu gốc của chương trình vẫn lưu trữ **100% bằng XML (JAXB)**: `hanghoa.xml`, `nhacungcap.xml`, `lohang.xml`, `phieugiaodich.xml`.

---

## 3. Phân công thành viên & phạm vi mã nguồn phụ trách

| STT | Họ và tên | Mã SV | Vai trò | Các lớp phụ trách chính (vấn đáp) |
| :---: | :--- | :---: | :--- | :--- |
| 1 | Đặng Quang Duy | 251020910267 | Nhóm trưởng – Kiến trúc | `QuanLyDoiTuongDacBiet`, `IQuanLy`, `CheckLogin`, `LoginView`/`LoginController`, `MainView`/`MainController` (cảnh báo tự động), `FileUtils`, `FormatUtils`, đóng gói `.jar` |
| 2 | Lương Xuân Thành | 251020910271 | Đối tượng 1 – Hàng hoá | `HangHoa`, `HangHoaXML`, `ManagerHangHoa`, `HangHoaView`, `HangHoaController` (quản lý danh mục, định mức tồn tối thiểu, liên kết xem lô) |
| 3 | Chu Minh Thuấn | 251020909884 | Đối tượng 2 – Nhà cung cấp | `NhaCungCap`, `NhaCungCapXML`, `ManagerNhaCungCap`, `NhaCungCapView`, `NhaCungCapController` (CRUD, tìm kiếm, ràng buộc khoá ngoại với lô hàng) |
| 4 | Lê Minh Hùng | 251020908122 | Nghiệp vụ Kho, Lô & Bill | `LoHang`, `LoHangXML`, `TrangThaiLoHang`, `LyDoHuHong`, `PhieuGiaoDich` (abstract), `PhieuNhap`, `PhieuXuat`, `PhieuHuy`, `ManagerLoHang`, `ManagerPhieuGiaoDich`, `PhieuGiaoDichView`/`Controller`, `TonKhoLoView`, `HoaDonDialog` |
| 5 | Nguyễn Tiến Dũng | 251020901280 | Tìm kiếm & Thống kê | `ThongKeAction`, `ThongKeView`, `ThongKeController` (tìm kiếm chuỗi/NCC + khoảng giá, thống kê giá trị tồn/nhập/xuất/thiệt hại hư hỏng, lọc HSD & tồn thấp, xuất báo cáo `.html`) |

---

## 4. Hướng dẫn mở trong Apache NetBeans 23 & Chạy chương trình

### Cách 1: Mở và chạy mã nguồn trong Apache NetBeans 23
1. Mở **Apache NetBeans 23** -> chọn **File -> Open Project...**
2. Trỏ tới thư mục dự án `nhom8_quanlykhotaphoa_v2` -> bấm **Open Project**.
3. Nhấn phím **`F6`** (Run Project) để chạy chương trình từ lớp chính `QuanLyDoiTuongDacBiet`.

### Cách 2: Chạy trực tiếp bản đóng gói độc lập (`.jar`)
1. Mở thư mục `BoChuongTrinh_DongGoi/` (đã gồm file `QuanLyKhoHangCuaHangTapHoa-1.0-SNAPSHOT.jar` và bộ 4 file dữ liệu `.xml` mẫu).
2. Nhấp đúp chuột vào `QuanLyKhoHangCuaHangTapHoa-1.0-SNAPSHOT.jar` hoặc chạy lệnh:
   ```bash
   java -jar QuanLyKhoHangCuaHangTapHoa-1.0-SNAPSHOT.jar
   ```
