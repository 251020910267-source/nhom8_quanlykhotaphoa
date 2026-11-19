# Quản lý kho hàng cửa hàng tạp hoá - Vai trò 2: Đối tượng chính thứ nhất (Hàng hoá)

Phạm vi: Lớp, giao diện Thêm/Sửa/Xoá/Hiển thị, đọc/ghi XML cho đối tượng Hàng hoá.

## Chạy thử
Yêu cầu JDK 23+, Maven 3.9+ (lần đầu cần mạng để tải thư viện).

    mvn compile exec:java

Dữ liệu lưu ở HangHoa.xml trong thư mục chạy lệnh.

## Các file thuộc phần này
    entity/HangHoa.java, entity/HangHoaXML.java   lớp đối tượng + lớp bao danh sách XML
    action/ManagerHangHoa.java                    Thêm/Sửa/Xoá/Hiển thị + đọc/ghi XML
    view/HangHoaView.java                         giao diện Swing
    controller/HangHoaController.java             nối giao diện với xử lý
    utils/FileUtils.java, utils/FormatUtils.java  đọc/ghi XML, định dạng tiền và ngày
