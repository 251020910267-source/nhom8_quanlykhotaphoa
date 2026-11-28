package com.mycompany.quanlydoituongdacbiet.action;

import com.mycompany.quanlydoituongdacbiet.entity.HangHoa;
import com.mycompany.quanlydoituongdacbiet.entity.HangHoaXML;
import com.mycompany.quanlydoituongdacbiet.utils.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp quản lý danh mục Hàng hoá (đọc/ghi hanghoa.xml) cài đặt chuẩn IQuanLy<HangHoa>.
 */
public class ManagerHangHoa implements IQuanLy<HangHoa> {
    private static final String HANGHOA_FILE_NAME = "hanghoa.xml";
    private List<HangHoa> listHangHoa;

    public ManagerHangHoa() {
        taiLai();
    }

    public final void taiLai() {
        this.listHangHoa = readListHangHoa();
        if (this.listHangHoa == null) {
            this.listHangHoa = new ArrayList<>();
        }
    }

    public void writeListHangHoa(List<HangHoa> list) {
        HangHoaXML xml = new HangHoaXML();
        xml.setHangHoa(list);
        FileUtils.writeXMLtoFile(HANGHOA_FILE_NAME, xml);
    }

    public List<HangHoa> readListHangHoa() {
        List<HangHoa> list = new ArrayList<>();
        File file = new File(HANGHOA_FILE_NAME);
        if (!file.exists()) {
            writeListHangHoa(list);
            return list;
        }
        HangHoaXML xml = (HangHoaXML) FileUtils.readXMLFile(HANGHOA_FILE_NAME, HangHoaXML.class);
        if (xml != null && xml.getHangHoa() != null) {
            list = xml.getHangHoa();
        }
        return list;
    }

    private int getNextId() {
        int maxId = 0;
        for (HangHoa hh : listHangHoa) {
            if (hh.getId() > maxId) {
                maxId = hh.getId();
            }
        }
        return maxId + 1;
    }

    /** Kiểm tra dữ liệu hợp lệ; trả về null nếu đúng, ngược lại trả về thông báo lỗi. */
    public String validateHangHoa(HangHoa h) {
        if (h.getMaHang() == null || h.getMaHang().trim().isEmpty()) {
            return "Mã hàng không được để trống.";
        }
        if (h.getTenHang() == null || h.getTenHang().trim().isEmpty()) {
            return "Tên hàng không được để trống.";
        }
        if (h.getDonViTinh() == null || h.getDonViTinh().trim().isEmpty()) {
            return "Đơn vị tính không được để trống.";
        }
        if (h.getDonGia() <= 0) {
            return "Giá bán niêm yết phải lớn hơn 0.";
        }
        if (h.getTonToiThieu() < 0) {
            return "Định mức tồn tối thiểu không được âm.";
        }
        String ma = h.getMaHang().trim();
        for (HangHoa item : listHangHoa) {
            if (item.getId() != h.getId() && item.getMaHang() != null
                    && item.getMaHang().trim().equalsIgnoreCase(ma)) {
                return "Mã hàng '" + ma + "' đã tồn tại (" + item.getTenHang()
                        + ").\nLưu ý: Nếu nhập thêm mặt hàng này từ nhà cung cấp khác, hãy sang mục Lập Phiếu Nhập Kho!";
            }
        }
        return null;
    }

    public String themHangHoa(HangHoa obj) {
        String error = validateHangHoa(obj);
        if (error != null) {
            return error;
        }
        obj.setMaHang(obj.getMaHang().trim());
        obj.setTenHang(obj.getTenHang().trim());
        obj.setDonViTinh(obj.getDonViTinh().trim());
        obj.setId(getNextId());
        listHangHoa.add(obj);
        writeListHangHoa(listHangHoa);
        return null;
    }

    public String suaHangHoa(HangHoa obj) {
        String error = validateHangHoa(obj);
        if (error != null) {
            return error;
        }
        obj.setMaHang(obj.getMaHang().trim());
        obj.setTenHang(obj.getTenHang().trim());
        obj.setDonViTinh(obj.getDonViTinh().trim());
        for (int i = 0; i < listHangHoa.size(); i++) {
            if (listHangHoa.get(i).getId() == obj.getId()) {
                listHangHoa.set(i, obj);
                writeListHangHoa(listHangHoa);
                return null;
            }
        }
        return "Không tìm thấy hàng hoá cần sửa.";
    }

    /** Kiểm tra ràng buộc lô hàng trước khi xoá danh mục mặt hàng. */
    public String xoaHangHoa(int id) {
        ManagerLoHang qlLo = new ManagerLoHang();
        if (qlLo.coLoCuaHang(id)) {
            return "Không thể xoá mặt hàng này vì đang có lô hàng / lịch sử nhập kho liên kết!\n"
                    + "Vui lòng xoá các phiếu giao dịch của mặt hàng này trước.";
        }
        for (int i = 0; i < listHangHoa.size(); i++) {
            if (listHangHoa.get(i).getId() == id) {
                listHangHoa.remove(i);
                writeListHangHoa(listHangHoa);
                return null;
            }
        }
        return "Không tìm thấy hàng hoá cần xoá.";
    }

    public HangHoa findById(int id) {
        for (HangHoa hh : listHangHoa) {
            if (hh.getId() == id) {
                return hh;
            }
        }
        return null;
    }

    @Override
    public void add(HangHoa obj) {
        String err = themHangHoa(obj);
        if (err != null) {
            throw new IllegalArgumentException(err);
        }
    }

    @Override
    public void edit(HangHoa obj) {
        String err = suaHangHoa(obj);
        if (err != null) {
            throw new IllegalArgumentException(err);
        }
    }

    @Override
    public boolean delete(HangHoa obj) {
        return obj != null && xoaHangHoa(obj.getId()) == null;
    }

    public boolean delete(int id) {
        return xoaHangHoa(id) == null;
    }

    @Override
    public List<HangHoa> getList() {
        return listHangHoa;
    }

    public List<HangHoa> getListHangHoa() {
        return listHangHoa;
    }
}
