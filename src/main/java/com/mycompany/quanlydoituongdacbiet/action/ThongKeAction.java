package com.mycompany.quanlydoituongdacbiet.action; // khai báo gói chứa lớp xử lý nghiệp vụ

import com.mycompany.quanlydoituongdacbiet.entity.HangHoa; // nạp lớp đối tượng Hàng hoá
import java.util.ArrayList; // nạp lớp danh sách động ArrayList
import java.util.Calendar; // nạp lớp Calendar để tính toán ngày tháng
import java.util.Comparator; // nạp giao diện Comparator để so sánh tìm Max/Min
import java.util.Date; // nạp lớp Date biểu diễn ngày tháng
import java.util.LinkedHashMap; // nạp Map giữ nguyên thứ tự thêm vào để thống kê nhóm
import java.util.List; // nạp giao diện List
import java.util.Map; // nạp giao diện Map (cặp Khoá - Giá trị)

/** Lớp xử lý nghiệp vụ Tìm kiếm & Thống kê báo cáo cho Thành viên 5. */
public class ThongKeAction { // bắt đầu định nghĩa lớp ThongKeAction

    // 1. Tìm kiếm gần đúng theo chuỗi (Mã hàng, Tên hàng, Nhóm hàng)
    public List<HangHoa> timKiemHangHoaTheoChuoi(List<HangHoa> dsGoc, String tuKhoa) { // nhận danh sách gốc và từ khoá
        List<HangHoa> ketQua = new ArrayList<>(); // tạo danh sách rỗng chứa kết quả khớp điều kiện
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) { // nếu người dùng để trống từ khoá
            return new ArrayList<>(dsGoc); // trả về toàn bộ danh sách gốc ban đầu
        } // kết thúc kiểm tra rỗng
        String key = tuKhoa.trim().toLowerCase(); // cắt khoảng trắng 2 đầu và chuyển từ khoá về chữ thường
        for (HangHoa h : dsGoc) { // duyệt qua từng mặt hàng h trong danh sách gốc
            boolean matchMa = h.getMaHang() != null && h.getMaHang().toLowerCase().contains(key); // kiểm tra mã hàng có chứa từ khoá không
            boolean matchTen = h.getTenHang() != null && h.getTenHang().toLowerCase().contains(key); // kiểm tra tên hàng có chứa từ khoá không
            boolean matchNhom = h.getNhomHang() != null && h.getNhomHang().toLowerCase().contains(key); // kiểm tra nhóm hàng có chứa từ khoá không
            if (matchMa || matchTen || matchNhom) { // nếu khớp 1 trong 3 tiêu chí
                ketQua.add(h); // thêm mặt hàng h vào danh sách kết quả
            } // kết thúc if
        } // kết thúc vòng lặp
        return ketQua; // trả về danh sách các mặt hàng tìm được
    } // kết thúc hàm tìm kiếm chuỗi

    // 2. Tìm kiếm theo khoảng đơn giá [giaTu, giaDen]
    public List<HangHoa> timKiemTheoKhoangGia(List<HangHoa> dsGoc, long giaTu, long giaDen) { // lọc hàng trong khoảng giá
        List<HangHoa> ketQua = new ArrayList<>(); // tạo danh sách rỗng chứa kết quả
        for (HangHoa h : dsGoc) { // duyệt qua từng mặt hàng h
            if (h.getDonGia() >= giaTu && h.getDonGia() <= giaDen) { // nếu đơn giá nằm từ giaTu đến giaDen
                ketQua.add(h); // thêm mặt hàng thoả mãn vào danh sách kết quả
            } // kết thúc if
        } // kết thúc vòng lặp
        return ketQua; // trả về danh sách sau khi lọc giá
    } // kết thúc hàm tìm theo khoảng giá

    // Kết hợp tìm kiếm cả chuỗi VÀ khoảng giá cùng lúc
    public List<HangHoa> timKiemKetHop(List<HangHoa> dsGoc, String tuKhoa, long giaTu, long giaDen) { // tìm kiếm đa điều kiện
        List<HangHoa> locChuoi = timKiemHangHoaTheoChuoi(dsGoc, tuKhoa); // bước 1: lọc theo từ khoá chuỗi trước
        return timKiemTheoKhoangGia(locChuoi, giaTu, giaDen); // bước 2: lọc tiếp theo khoảng giá
    } // kết thúc hàm tìm kiếm kết hợp

    // 3. Thống kê Tổng / Lớn nhất / Nhỏ nhất
    public int tinhTongSoMatHang(List<HangHoa> dsGoc) { // đếm tổng số mặt hàng hiện có
        return dsGoc == null ? 0 : dsGoc.size(); // nếu null trả về 0, ngược lại trả về số lượng phần tử
    } // kết thúc hàm đếm tổng mặt hàng

    public long tinhTongDonGiaDanhMuc(List<HangHoa> dsGoc) { // tính tổng đơn giá của tất cả mặt hàng
        long tong = 0; // khởi tạo biến cộng dồn bằng 0
        if (dsGoc != null) { // kiểm tra danh sách khác null
            for (HangHoa h : dsGoc) { // duyệt từng mặt hàng
                tong += h.getDonGia(); // cộng dồn đơn giá vào biến tong
            } // kết thúc vòng lặp
        } // kết thúc if
        return tong; // trả về tổng giá trị
    } // kết thúc hàm tính tổng

    public HangHoa timHangGiaCaoNhat(List<HangHoa> dsGoc) { // tìm mặt hàng có đơn giá cao nhất (Max)
        if (dsGoc == null || dsGoc.isEmpty()) return null; // nếu danh sách rỗng thì trả về null
        return dsGoc.stream() // chuyển danh sách thành luồng dữ liệu Stream
                .max(Comparator.comparingLong(HangHoa::getDonGia)) // tìm phần tử có getDonGia() lớn nhất
                .orElse(null); // trả về mặt hàng tìm được
    } // kết thúc hàm tìm Max

    public HangHoa timHangGiaThapNhat(List<HangHoa> dsGoc) { // tìm mặt hàng có đơn giá thấp nhất (Min)
        if (dsGoc == null || dsGoc.isEmpty()) return null; // nếu danh sách rỗng thì trả về null
        return dsGoc.stream() // chuyển danh sách thành luồng dữ liệu Stream
                .min(Comparator.comparingLong(HangHoa::getDonGia)) // tìm phần tử có getDonGia() nhỏ nhất
                .orElse(null); // trả về mặt hàng tìm được
    } // kết thúc hàm tìm Min

    // 4. Thống kê số lượng mặt hàng theo từng nhóm hàng
    public Map<String, Integer> thongKeSoLuongTheoNhom(List<HangHoa> dsGoc) { // gom nhóm và đếm số lượng
        Map<String, Integer> map = new LinkedHashMap<>(); // tạo Map lưu cặp (Tên nhóm -> Số lượng)
        if (dsGoc != null) { // kiểm tra danh sách khác null
            for (HangHoa h : dsGoc) { // duyệt từng mặt hàng
                String nhom = (h.getNhomHang() == null || h.getNhomHang().trim().isEmpty()) // nếu nhóm hàng bị trống
                        ? "Khác" : h.getNhomHang().trim(); // trống thì gán là "Khác", ngược lại lấy tên nhóm
                map.put(nhom, map.getOrDefault(nhom, 0) + 1); // lấy số đếm cũ cộng thêm 1 rồi lưu lại
            } // kết thúc vòng lặp
        } // kết thúc if
        return map; // trả về bảng thống kê theo nhóm
    } // kết thúc hàm thống kê nhóm

    // 5. Cảnh báo tự động hàng đã hết hạn hoặc sắp hết hạn trong N ngày tới
    public List<HangHoa> layDanhSachCanhBaoHetHan(List<HangHoa> dsGoc, int soNgayCanhBao) { // lọc hàng sắp/đã hết hạn
        List<HangHoa> ketQua = new ArrayList<>(); // tạo danh sách chứa các mặt hàng cần cảnh báo
        if (dsGoc == null) return ketQua; // nếu danh sách gốc null thì trả về list rỗng

        Calendar cal = Calendar.getInstance(); // lấy lịch tại thời điểm hiện tại
        cal.add(Calendar.DAY_OF_YEAR, soNgayCanhBao); // cộng thêm số ngày cảnh báo (ví dụ +30 ngày)
        Date mocThoiGian = cal.getTime(); // chuyển mốc thời gian tương lai sang kiểu Date

        for (HangHoa h : dsGoc) { // duyệt từng mặt hàng trong kho
            if (h.getHanSuDung() != null && h.getHanSuDung().before(mocThoiGian)) { // nếu hạn sử dụng trước mốc thời gian
                ketQua.add(h); // thêm vào danh sách cảnh báo
            } // kết thúc if
        } // kết thúc vòng lặp
        return ketQua; // trả về danh sách cảnh báo
    } // kết thúc hàm cảnh báo hết hạn
} // kết thúc lớp ThongKeAction