package com.mycompany.quanlydoituongdacbiet.action;

import com.mycompany.quanlydoituongdacbiet.entity.LoHang;
import com.mycompany.quanlydoituongdacbiet.entity.NhaCungCap;
import com.mycompany.quanlydoituongdacbiet.entity.NhaCungCapXML;
import com.mycompany.quanlydoituongdacbiet.utils.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp quản lý Nhà cung cấp (đọc/ghi nhacungcap.xml) cài đặt chuẩn IQuanLy<NhaCungCap>.
 */
public class ManagerNhaCungCap implements IQuanLy<NhaCungCap> {
    private static final String NCC_FILE_NAME = "nhacungcap.xml";
    private List<NhaCungCap> listNCC;

    public ManagerNhaCungCap() {
        taiLai();
    }

    public final void taiLai() {
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
                if (ncc.getTenNCC().equalsIgnoreCase(nccCheck.getTenNCC().trim())
                        || ncc.getSoDienThoai().equals(nccCheck.getSoDienThoai().trim())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void add(NhaCungCap ncc) {
        ncc.setId(getNextId());
        listNCC.add(ncc);
        writeListNCC(listNCC);
    }

    @Override
    public void edit(NhaCungCap ncc) {
        for (int i = 0; i < listNCC.size(); i++) {
            if (listNCC.get(i).getId() == ncc.getId()) {
                String tenCu = listNCC.get(i).getTenNCC();
                listNCC.get(i).setTenNCC(ncc.getTenNCC());
                listNCC.get(i).setSoDienThoai(ncc.getSoDienThoai());
                listNCC.get(i).setDiaChi(ncc.getDiaChi());
                listNCC.get(i).setEmail(ncc.getEmail());
                writeListNCC(listNCC);
                // Đồng bộ tên NCC mới sang các lô hàng đang lưu
                if (tenCu != null && !tenCu.equals(ncc.getTenNCC())) {
                    ManagerLoHang qlLo = new ManagerLoHang();
                    boolean doi = false;
                    for (LoHang lo : qlLo.getList()) {
                        if (lo.getIdNhaCungCap() == ncc.getId() || tenCu.equalsIgnoreCase(lo.getTenNhaCungCap())) {
                            lo.setIdNhaCungCap(ncc.getId());
                            lo.setTenNhaCungCap(ncc.getTenNCC());
                            doi = true;
                        }
                    }
                    if (doi) {
                        qlLo.luu();
                    }
                }
                break;
            }
        }
    }

    /** Xoá có kiểm tra ràng buộc khoá ngoại với Lô hàng. */
    public String xoaNCC(NhaCungCap ncc) {
        if (ncc == null) {
            return "Chưa chọn nhà cung cấp cần xoá.";
        }
        ManagerLoHang qlLo = new ManagerLoHang();
        if (qlLo.coLoCuaNhaCungCap(ncc.getId(), ncc.getTenNCC())) {
            return "Không thể xoá Nhà cung cấp '" + ncc.getTenNCC()
                    + "' vì đang có lô hàng nhập từ đối tác này trong kho!";
        }
        for (int i = 0; i < listNCC.size(); i++) {
            if (listNCC.get(i).getId() == ncc.getId()) {
                listNCC.remove(i);
                writeListNCC(listNCC);
                return null;
            }
        }
        return "Không tìm thấy nhà cung cấp cần xoá.";
    }

    @Override
    public boolean delete(NhaCungCap ncc) {
        return xoaNCC(ncc) == null;
    }

    public NhaCungCap findById(int id) {
        for (NhaCungCap ncc : listNCC) {
            if (ncc.getId() == id) {
                return ncc;
            }
        }
        return null;
    }

    public List<NhaCungCap> searchNCC(String keyword) {
        List<NhaCungCap> result = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>(listNCC);
        }
        String key = keyword.toLowerCase().trim();
        for (NhaCungCap ncc : listNCC) {
            boolean matchTen = ncc.getTenNCC() != null && ncc.getTenNCC().toLowerCase().contains(key);
            boolean matchSdt = ncc.getSoDienThoai() != null && ncc.getSoDienThoai().contains(key);
            boolean matchDiaChi = ncc.getDiaChi() != null && ncc.getDiaChi().toLowerCase().contains(key);
            boolean matchEmail = ncc.getEmail() != null && ncc.getEmail().toLowerCase().contains(key);
            if (matchTen || matchSdt || matchDiaChi || matchEmail) {
                result.add(ncc);
            }
        }
        return result;
    }

    @Override
    public List<NhaCungCap> getList() {
        return listNCC;
    }

    public List<NhaCungCap> getListNCC() {
        return listNCC;
    }
}
