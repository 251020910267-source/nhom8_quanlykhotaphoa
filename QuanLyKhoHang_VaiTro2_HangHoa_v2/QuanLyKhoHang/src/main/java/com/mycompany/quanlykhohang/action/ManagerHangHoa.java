package com.mycompany.quanlykhohang.action; // gói chứa lớp xử lý nghiệp vụ

import com.mycompany.quanlykhohang.entity.HangHoa; // lớp hàng hoá
import com.mycompany.quanlykhohang.entity.HangHoaXML; // lớp bao danh sách để đọc/ghi XML
import com.mycompany.quanlykhohang.utils.FileUtils; // hàm đọc/ghi file XML
import java.util.ArrayList; // danh sách động
import java.util.LinkedHashSet; // tập hợp không trùng, giữ thứ tự thêm vào
import java.util.List; // giao diện danh sách
import java.util.Set; // giao diện tập hợp

/**
 * Xử lý nghiệp vụ Thêm / Sửa / Xoá / Hiển thị hàng hoá và đọc/ghi XML.
 * Các hàm add/edit trả về null nếu thành công, ngược lại trả về nội dung lỗi.
 */
public class ManagerHangHoa { // lớp quản lý danh sách hàng hoá

    private static final String HANGHOA_FILE_NAME = "HangHoa.xml"; // tên file lưu dữ liệu
    private List<HangHoa> listHangHoa; // danh sách hàng hoá trong bộ nhớ

    public ManagerHangHoa() { // constructor
        this.listHangHoa = readListHangHoa(); // nạp dữ liệu từ file XML
    } // kết thúc

    // ---------- XML ----------
    public final List<HangHoa> readListHangHoa() { // đọc danh sách từ file XML
        List<HangHoa> list = new ArrayList<HangHoa>(); // mặc định là danh sách rỗng
        HangHoaXML xml = (HangHoaXML) FileUtils.readXMLFile(HANGHOA_FILE_NAME, HangHoaXML.class); // đọc file và ép kiểu
        if (xml != null && xml.getHangHoa() != null) { // nếu đọc được
            list = xml.getHangHoa(); // lấy danh sách
        } // kết thúc if
        return list; // trả về
    } // kết thúc

    public void writeListHangHoa(List<HangHoa> list) { // ghi danh sách xuống file XML
        HangHoaXML xml = new HangHoaXML(); // tạo lớp bao
        xml.setHangHoa(list); // đưa danh sách vào
        FileUtils.writeXMLtoFile(HANGHOA_FILE_NAME, xml); // ghi ra file
    } // kết thúc

    // ---------- Hàm hỗ trợ ----------
    private static String cat(String s) { // cắt khoảng trắng, null thành chuỗi rỗng
        return s == null ? "" : s.trim(); // an toàn với null (file XML cũ chưa có các trường mới)
    } // kết thúc

    /** Danh sách tên nhà cung cấp đã có (không trùng) để gợi ý trong ô chọn. */
    public List<String> getDanhSachNhaCungCap() { // lấy tên các nhà cung cấp đã nhập
        Set<String> tap = new LinkedHashSet<String>(); // tập không trùng
        for (HangHoa h : listHangHoa) { // duyệt danh sách
            if (!cat(h.getNhaCungCap()).isEmpty()) { // có tên nhà cung cấp
                tap.add(cat(h.getNhaCungCap())); // thêm vào tập
            } // kết thúc if
        } // kết thúc for
        return new ArrayList<String>(tap); // đổi sang danh sách
    } // kết thúc

    // ---------- Kiểm tra dữ liệu ----------
    private String validate(HangHoa h, int ignoreId) { // kiểm tra dữ liệu; ignoreId là id bỏ qua khi so trùng (dùng khi sửa)
        if (cat(h.getMaHang()).isEmpty()) { // mã hàng rỗng
            return "Mã hàng không được để trống."; // báo lỗi
        } // kết thúc if
        if (cat(h.getTenHang()).isEmpty()) { // tên hàng rỗng
            return "Tên hàng không được để trống."; // báo lỗi
        } // kết thúc if
        if (cat(h.getNhomHang()).isEmpty()) { // nhóm hàng rỗng
            return "Nhóm hàng không được để trống."; // báo lỗi
        } // kết thúc if
        if (cat(h.getDonViTinh()).isEmpty()) { // đơn vị tính rỗng
            return "Đơn vị tính không được để trống."; // báo lỗi
        } // kết thúc if
        if (h.getDonGia() <= 0) { // đơn giá không dương
            return "Đơn giá phải lớn hơn 0."; // báo lỗi
        } // kết thúc if
        if (h.getHanSuDung() == null) { // chưa chọn ngày
            return "Vui lòng chọn hạn sử dụng."; // báo lỗi
        } // kết thúc if
        if (cat(h.getMaLo()).isEmpty()) { // mã lô rỗng
            return "Mã lô không được để trống."; // báo lỗi
        } // kết thúc if
        if (cat(h.getNhaCungCap()).isEmpty()) { // nhà cung cấp rỗng
            return "Nhà cung cấp không được để trống (để biết lô này do ai cấp)."; // báo lỗi
        } // kết thúc if
        for (HangHoa x : listHangHoa) { // duyệt để kiểm tra trùng lô
            if (x.getId() != ignoreId // bỏ qua chính dòng đang sửa
                    && cat(x.getMaHang()).equalsIgnoreCase(cat(h.getMaHang())) // cùng mã hàng
                    && cat(x.getMaLo()).equalsIgnoreCase(cat(h.getMaLo()))) { // và cùng mã lô
                return "Lô \"" + cat(h.getMaLo()) + "\" của mã hàng \"" + cat(h.getMaHang()) + "\" đã tồn tại."; // báo lỗi
            } // kết thúc if
        } // kết thúc for
        return null; // không có lỗi
    } // kết thúc

    /** Chuẩn hoá dữ liệu trước khi lưu: cắt khoảng trắng, viết hoa mã, tình trạng mặc định là Bình thường. */
    private void chuanHoa(HangHoa h) { // chuẩn hoá dữ liệu
        h.setMaHang(cat(h.getMaHang()).toUpperCase()); // mã hàng viết hoa
        h.setTenHang(cat(h.getTenHang())); // cắt khoảng trắng
        h.setNhomHang(cat(h.getNhomHang())); // cắt khoảng trắng
        h.setDonViTinh(cat(h.getDonViTinh())); // cắt khoảng trắng
        h.setMaLo(cat(h.getMaLo()).toUpperCase()); // mã lô viết hoa
        h.setNhaCungCap(cat(h.getNhaCungCap())); // cắt khoảng trắng
        h.setTinhTrang(cat(h.getTinhTrang()).isEmpty() ? HangHoa.BINH_THUONG : cat(h.getTinhTrang())); // chưa chọn thì là Bình thường
    } // kết thúc

    // ---------- Thêm ----------
    public String add(HangHoa h) { // thêm hàng hoá (một lô) mới
        String error = validate(h, -1); // kiểm tra; -1 là không bỏ qua dòng nào
        if (error != null) { // có lỗi
            return error; // trả lỗi, không thêm
        } // kết thúc if
        int max = 0; // id lớn nhất hiện có
        for (HangHoa x : listHangHoa) { // duyệt danh sách
            if (x.getId() > max) { // lớn hơn max
                max = x.getId(); // cập nhật
            } // kết thúc if
        } // kết thúc for
        h.setId(max + 1); // id mới = lớn nhất + 1 (tự tăng)
        chuanHoa(h); // chuẩn hoá dữ liệu
        listHangHoa.add(h); // thêm vào danh sách
        writeListHangHoa(listHangHoa); // ghi ngay xuống file XML
        return null; // thành công
    } // kết thúc

    // ---------- Sửa ----------
    public String edit(HangHoa h) { // sửa hàng hoá đã có (tìm theo id)
        String error = validate(h, h.getId()); // kiểm tra, bỏ qua chính dòng này khi so trùng
        if (error != null) { // có lỗi
            return error; // trả lỗi
        } // kết thúc if
        for (HangHoa x : listHangHoa) { // duyệt tìm dòng cần sửa
            if (x.getId() == h.getId()) { // tìm thấy theo id
                chuanHoa(h); // chuẩn hoá dữ liệu mới
                x.setMaHang(h.getMaHang()); // cập nhật mã hàng
                x.setTenHang(h.getTenHang()); // cập nhật tên
                x.setNhomHang(h.getNhomHang()); // cập nhật nhóm
                x.setDonViTinh(h.getDonViTinh()); // cập nhật đơn vị
                x.setDonGia(h.getDonGia()); // cập nhật đơn giá
                x.setHanSuDung(h.getHanSuDung()); // cập nhật hạn sử dụng
                x.setMaLo(h.getMaLo()); // cập nhật mã lô
                x.setNhaCungCap(h.getNhaCungCap()); // cập nhật nhà cung cấp
                x.setTinhTrang(h.getTinhTrang()); // cập nhật tình trạng
                writeListHangHoa(listHangHoa); // ghi xuống file XML
                return null; // thành công
            } // kết thúc if
        } // kết thúc for
        return "Không tìm thấy hàng hoá cần sửa."; // không có id này
    } // kết thúc

    // ---------- Xoá ----------
    public boolean delete(int id) { // xoá theo id, trả true nếu xoá được
        for (int i = 0; i < listHangHoa.size(); i++) { // duyệt theo chỉ số
            if (listHangHoa.get(i).getId() == id) { // tìm thấy
                listHangHoa.remove(i); // xoá khỏi danh sách
                writeListHangHoa(listHangHoa); // ghi lại file XML
                return true; // thành công
            } // kết thúc if
        } // kết thúc for
        return false; // không tìm thấy
    } // kết thúc

    // ---------- Hiển thị ----------
    public List<HangHoa> getListHangHoa() { // lấy danh sách để hiển thị
        return listHangHoa; // trả về danh sách hiện tại
    } // kết thúc
} // kết thúc lớp
