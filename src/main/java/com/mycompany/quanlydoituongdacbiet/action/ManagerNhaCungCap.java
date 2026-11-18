package com.mycompany.quanlydoituongdacbiet.action;

import com.mycompany.quanlydoituongdacbiet.entity.NhaCungCap;
import com.mycompany.quanlydoituongdacbiet.entity.NhaCungCapXML;
import com.mycompany.quanlydoituongdacbiet.utils.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ManagerNhaCungCap {
    private static final String NCC_FILE_NAME = "nhacungcap.xml";
    private List<NhaCungCap> listNCC;

    public ManagerNhaCungCap() {
        this.listNCC = readListNCC();
        if (this.listNCC == null) {
            this.listNCC = new ArrayList<>();
        }
    }

    public void writeListNCC(List<NhaCungCap> nccs) {
        NhaCungCapXML nccXML = new NhaCungCapXML();
        nccXML.setNhaCungCap(nccs);
        FileUtils.writeXMLtoFile(NCC_FILE_NAME, nccXML);
    }

    public List<NhaCungCap> readListNCC() {
        List<NhaCungCap> list = new ArrayList<>();
        File file = new File(NCC_FILE_NAME);
        if (!file.exists()) {
            writeListNCC(list);
            return list;
        }
        NhaCungCapXML nccXML = (NhaCungCapXML) FileUtils.readXMLFile(NCC_FILE_NAME, NhaCungCapXML.class);
        if (nccXML != null && nccXML.getNhaCungCap() != null) {
            list = nccXML.getNhaCungCap();
        }
        return list;
    }

    private int getNextId() {
        int maxId = 0;
        for (NhaCungCap ncc : listNCC) {
            if (ncc.getId() > maxId) {
                maxId = ncc.getId();
            }
        }
        return maxId + 1;
    }

    public boolean isNCCUnique(NhaCungCap nccCheck) {
        for (NhaCungCap ncc : listNCC) {
            if (ncc.getId() != nccCheck.getId()) {
                if (ncc.getTenNCC().equalsIgnoreCase(nccCheck.getTenNCC().trim()) ||
                        ncc.getSoDienThoai().equals(nccCheck.getSoDienThoai().trim())) {
                    return false;
                }
            }
        }
        return true;
    }

    public void add(NhaCungCap ncc) {
        ncc.setId(getNextId());
        listNCC.add(ncc);
        writeListNCC(listNCC);
    }

    public void edit(NhaCungCap ncc) {
        for (int i = 0; i < listNCC.size(); i++) {
            if (listNCC.get(i).getId() == ncc.getId()) {
                listNCC.get(i).setTenNCC(ncc.getTenNCC());
                listNCC.get(i).setSoDienThoai(ncc.getSoDienThoai());
                listNCC.get(i).setDiaChi(ncc.getDiaChi());
                listNCC.get(i).setEmail(ncc.getEmail());
                writeListNCC(listNCC);
                break;
            }
        }
    }

    public boolean delete(NhaCungCap ncc) {
        for (int i = 0; i < listNCC.size(); i++) {
            if (listNCC.get(i).getId() == ncc.getId()) {
                listNCC.remove(i);
                writeListNCC(listNCC);
                return true;
            }
        }
        return false;
    }

    public List<NhaCungCap> searchNCC(String keyword) {
        List<NhaCungCap> result = new ArrayList<>();
        String key = keyword.toLowerCase().trim();
        for (NhaCungCap ncc : listNCC) {
            if (ncc.getTenNCC().toLowerCase().contains(key) ||
                    ncc.getSoDienThoai().contains(key)) {
                result.add(ncc);
            }
        }
        return result;
    }

    public List<NhaCungCap> getListNCC() {
        return listNCC;
    }
}