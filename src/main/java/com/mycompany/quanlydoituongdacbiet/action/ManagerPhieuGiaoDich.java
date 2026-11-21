package com.mycompany.quanlydoituongdacbiet.action;

import com.mycompany.quanlydoituongdacbiet.entity.PhieuGiaoDich;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuGiaoDichXML;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuNhap;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuXuat;
import com.mycompany.quanlydoituongdacbiet.utils.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ManagerPhieuGiaoDich implements IQuanLy<PhieuGiaoDich> {
    private static final String PHIEU_FILE_NAME = "phieugiaodich.xml";
    private List<PhieuGiaoDich> listPhieu;

    public ManagerPhieuGiaoDich() {
        this.listPhieu = readListPhieu();
        if (this.listPhieu == null) {
            this.listPhieu = new ArrayList<>();
        }
    }

    public void writeListPhieu(List<PhieuGiaoDich> list) {
        PhieuGiaoDichXML xml = new PhieuGiaoDichXML();
        xml.setPhieuGiaoDich(list);
        FileUtils.writeXMLtoFile(PHIEU_FILE_NAME, xml);
    }

    public List<PhieuGiaoDich> readListPhieu() {
        List<PhieuGiaoDich> list = new ArrayList<>();
        File file = new File(PHIEU_FILE_NAME);
        if (!file.exists()) {
            writeListPhieu(list);
            return list;
        }
        PhieuGiaoDichXML xml = (PhieuGiaoDichXML) FileUtils.readXMLFile(PHIEU_FILE_NAME, PhieuGiaoDichXML.class);
        if (xml != null && xml.getPhieuGiaoDich() != null) {
            list = xml.getPhieuGiaoDich();
        }
        return list;
    }

    private int getNextId() {
        int maxId = 0;
        for (PhieuGiaoDich p : listPhieu) {
            if (p.getId() > maxId) {
                maxId = p.getId();
            }
        }
        return maxId + 1;
    }

    // Tính số lượng tồn kho thực tế của 1 mặt hàng = Tổng Nhập - Tổng Xuất
    public int tinhTonKho(int maHangHoa) {
        int ton = 0;
        for (PhieuGiaoDich p : listPhieu) {
            if (p.getMaHangHoa() == maHangHoa) {
                if (p instanceof PhieuNhap) {
                    ton += p.getSoLuong();
                } else if (p instanceof PhieuXuat) {
                    ton -= p.getSoLuong();
                }
            }
        }
        return ton;
    }

    @Override
    public void add(PhieuGiaoDich obj) {
        obj.setId(getNextId());
        listPhieu.add(obj);
        writeListPhieu(listPhieu);
    }

    @Override
    public void edit(PhieuGiaoDich obj) {
        for (int i = 0; i < listPhieu.size(); i++) {
            if (listPhieu.get(i).getId() == obj.getId()) {
                listPhieu.set(i, obj);
                writeListPhieu(listPhieu);
                break;
            }
        }
    }

    @Override
    public boolean delete(PhieuGiaoDich obj) {
        for (int i = 0; i < listPhieu.size(); i++) {
            if (listPhieu.get(i).getId() == obj.getId()) {
                listPhieu.remove(i);
                writeListPhieu(listPhieu);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<PhieuGiaoDich> getList() {
        return listPhieu;
    }
}