package com.mycompany.quanlydoituongdacbiet.action;

import com.mycompany.quanlydoituongdacbiet.entity.HangHoa;
import com.mycompany.quanlydoituongdacbiet.entity.HangHoaXML;
import com.mycompany.quanlydoituongdacbiet.utils.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ManagerHangHoa {
    private static final String HANGHOA_FILE_NAME = "hanghoa.xml";
    private List<HangHoa> listHangHoa;

    public ManagerHangHoa() {
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

    // Kiểm tra dữ liệu và trả về null nếu hợp lệ, ngược lại trả về thông báo lỗi
    private String validateHangHoa(HangHoa h) {
        if (h.getMaHang() == null || h.getMaHang().trim().isEmpty()) {
            return "Mã hàng không được để trống.";
        }
        if (h.getTenHang() == null || h.getTenHang().trim().isEmpty()) {
            return "Tên hàng không được để trống.";
        }
        for (HangHoa item : listHangHoa) {
            if (item.getId() != h.getId() && item.getMaHang().equalsIgnoreCase(h.getMaHang().trim())) {
                return "Mã hàng '" + h.getMaHang() + "' đã tồn tại.";
            }
        }
        return null;
    }

    public String add(HangHoa obj) {
        String error = validateHangHoa(obj);
        if (error != null) {
            return error;
        }
        obj.setId(getNextId());
        listHangHoa.add(obj);
        writeListHangHoa(listHangHoa);
        return null;
    }

    public String edit(HangHoa obj) {
        String error = validateHangHoa(obj);
        if (error != null) {
            return error;
        }
        for (int i = 0; i < listHangHoa.size(); i++) {
            if (listHangHoa.get(i).getId() == obj.getId()) {
                listHangHoa.set(i, obj);
                writeListHangHoa(listHangHoa);
                return null;
            }
        }
        return "Không tìm thấy hàng hóa cần sửa.";
    }

    public boolean delete(HangHoa obj) {
        for (int i = 0; i < listHangHoa.size(); i++) {
            if (listHangHoa.get(i).getId() == obj.getId()) {
                listHangHoa.remove(i);
                writeListHangHoa(listHangHoa);
                return true;
            }
        }
        return false;
    }

    public boolean delete(int id) {
        for (int i = 0; i < listHangHoa.size(); i++) {
            if (listHangHoa.get(i).getId() == id) {
                listHangHoa.remove(i);
                writeListHangHoa(listHangHoa);
                return true;
            }
        }
        return false;
    }

    public List<HangHoa> getList() {
        return listHangHoa;
    }

    public List<HangHoa> getListHangHoa() {
        return listHangHoa;
    }
}