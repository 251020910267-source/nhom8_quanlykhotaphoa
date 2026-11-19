package com.mycompany.quanlydoituongdacbiet.action; // gói chứa lớp xử lý nghiệp vụ

import com.mycompany.quanlydoituongdacbiet.entity.HangHoa; // lớp hàng hoá
import com.mycompany.quanlydoituongdacbiet.entity.HangHoaXML; // lớp bao danh sách để đọc/ghi XML
import com.mycompany.quanlydoituongdacbiet.utils.FileUtils; // hàm đọc/ghi file XML
import java.util.ArrayList; // danh sách động
import java.util.List; // giao diện danh sách

/**
 * Xử lý nghiệp vụ Thêm / Sửa / Xoá / Hiển thị hàng hoá và đọc/ghi XML.
 * Các hàm add/edit trả về null nếu thành công, ngược lại trả về nội dung lỗi.
 */
public class ManagerHangHoa { // lớp quản lý danh sách hàng hoá

    private static final String HANGHOA_FILE_NAME = "HangHoa.xml"; // tên file lưu dữ liệu (nằm ở thư mục chạy chương trình)
    private List<HangHoa> listHangHoa; // danh sách hàng hoá đang giữ trong bộ nhớ

    public ManagerHangHoa() { // constructor
        this.listHangHoa = readListHangHoa(); // khi tạo đối tượng thì nạp dữ liệu từ file XML
    } // kết thúc constructor

    // ---------- XML ----------
    public final List<HangHoa> readListHangHoa() { // đọc danh sách từ file XML (final vì được gọi trong constructor)
        List<HangHoa> list = new ArrayList<HangHoa>(); // mặc định là danh sách rỗng
        HangHoaXML xml = (HangHoaXML) FileUtils.readXMLFile(HANGHOA_FILE_NAME, HangHoaXML.class); // đọc file và ép kiểu về HangHoaXML
        if (xml != null && xml.getHangHoa() != null) { // nếu đọc được và có danh sách
            list = xml.getHangHoa(); // lấy danh sách đọc được
        } // kết thúc if
        return list; // trả về danh sách
    } // kết thúc hàm

    public void writeListHangHoa(List<HangHoa> list) { // ghi danh sách xuống file XML
        HangHoaXML xml = new HangHoaXML(); // tạo đối tượng bao
        xml.setHangHoa(list); // đưa danh sách vào đối tượng bao
        FileUtils.writeXMLtoFile(HANGHOA_FILE_NAME, xml); // ghi ra file
    } // kết thúc hàm

    // ---------- Kiểm tra dữ liệu ----------
    private String validate(HangHoa h, int ignoreId) { // kiểm tra dữ liệu; ignoreId là id bỏ qua khi so trùng mã (dùng khi sửa)
        if (h.getMaHang() == null || h.getMaHang().trim().isEmpty()) { // mã hàng null hoặc chỉ toàn khoảng trắng
            return "Mã hàng không được để trống."; // trả thông báo lỗi
        } // kết thúc if
        if (h.getTenHang() == null || h.getTenHang().trim().isEmpty()) { // tên hàng rỗng
            return "Tên hàng không được để trống."; // trả thông báo lỗi
        } // kết thúc if
        if (h.getNhomHang() == null || h.getNhomHang().trim().isEmpty()) { // nhóm hàng rỗng
            return "Nhóm hàng không được để trống."; // trả thông báo lỗi
        } // kết thúc if
        if (h.getDonViTinh() == null || h.getDonViTinh().trim().isEmpty()) { // đơn vị tính rỗng
            return "Đơn vị tính không được để trống."; // trả thông báo lỗi
        } // kết thúc if
        if (h.getDonGia() <= 0) { // đơn giá không dương
            return "Đơn giá phải lớn hơn 0."; // trả thông báo lỗi
        } // kết thúc if
        if (h.getHanSuDung() == null) { // chưa chọn ngày
            return "Vui lòng chọn hạn sử dụng."; // trả thông báo lỗi
        } // kết thúc if
        for (HangHoa x : listHangHoa) { // duyệt từng hàng đã có
            if (x.getId() != ignoreId // bỏ qua chính hàng đang sửa
                    && x.getMaHang().trim().equalsIgnoreCase(h.getMaHang().trim())) { // và so sánh mã không phân biệt hoa thường
                return "Mã hàng \"" + h.getMaHang().trim() + "\" đã tồn tại."; // trùng mã thì báo lỗi
            } // kết thúc if
        } // kết thúc for
        return null; // không có lỗi nào
    } // kết thúc hàm

    // ---------- Thêm ----------
    public String add(HangHoa h) { // thêm hàng hoá mới
        String error = validate(h, -1); // kiểm tra; -1 nghĩa là không bỏ qua hàng nào
        if (error != null) { // nếu có lỗi
            return error; // trả lỗi, không thêm
        } // kết thúc if
        int max = 0; // biến lưu id lớn nhất hiện có
        for (HangHoa x : listHangHoa) { // duyệt danh sách
            if (x.getId() > max) { // nếu id lớn hơn max
                max = x.getId(); // cập nhật max
            } // kết thúc if
        } // kết thúc for
        h.setId(max + 1); // id mới = id lớn nhất + 1 (tự tăng)
        h.setMaHang(h.getMaHang().trim().toUpperCase()); // chuẩn hoá mã hàng: bỏ khoảng trắng thừa, viết hoa
        h.setTenHang(h.getTenHang().trim()); // bỏ khoảng trắng thừa ở tên
        h.setNhomHang(h.getNhomHang().trim()); // bỏ khoảng trắng thừa ở nhóm
        h.setDonViTinh(h.getDonViTinh().trim()); // bỏ khoảng trắng thừa ở đơn vị tính
        listHangHoa.add(h); // thêm vào danh sách
        writeListHangHoa(listHangHoa); // ghi ngay xuống file XML
        return null; // thành công
    } // kết thúc hàm

    // ---------- Sửa ----------
    public String edit(HangHoa h) { // sửa hàng hoá đã có (tìm theo id)
        String error = validate(h, h.getId()); // kiểm tra, bỏ qua chính hàng này khi so trùng mã
        if (error != null) { // có lỗi
            return error; // trả lỗi
        } // kết thúc if
        for (HangHoa x : listHangHoa) { // duyệt tìm hàng cần sửa
            if (x.getId() == h.getId()) { // tìm thấy theo id
                x.setMaHang(h.getMaHang().trim().toUpperCase()); // cập nhật mã hàng
                x.setTenHang(h.getTenHang().trim()); // cập nhật tên
                x.setNhomHang(h.getNhomHang().trim()); // cập nhật nhóm
                x.setDonViTinh(h.getDonViTinh().trim()); // cập nhật đơn vị tính
                x.setDonGia(h.getDonGia()); // cập nhật đơn giá
                x.setHanSuDung(h.getHanSuDung()); // cập nhật hạn sử dụng
                writeListHangHoa(listHangHoa); // ghi xuống file XML
                return null; // thành công
            } // kết thúc if
        } // kết thúc for
        return "Không tìm thấy hàng hoá cần sửa."; // duyệt hết mà không thấy id
    } // kết thúc hàm

    // ---------- Xoá ----------
    /** Không đánh lại id các phần tử sau, vì phiếu nhập/xuất sau này sẽ tham chiếu tới mã hàng. */
    public boolean delete(int id) { // xoá theo id, trả true nếu xoá được
        for (int i = 0; i < listHangHoa.size(); i++) { // duyệt theo chỉ số
            if (listHangHoa.get(i).getId() == id) { // tìm thấy phần tử có id cần xoá
                listHangHoa.remove(i); // xoá khỏi danh sách
                writeListHangHoa(listHangHoa); // ghi lại file XML
                return true; // báo xoá thành công
            } // kết thúc if
        } // kết thúc for
        return false; // không tìm thấy
    } // kết thúc hàm

    // ---------- Hiển thị ----------
    public List<HangHoa> getListHangHoa() { // lấy danh sách để hiển thị
        return listHangHoa; // trả về danh sách hiện tại
    } // kết thúc hàm
} // kết thúc lớp
