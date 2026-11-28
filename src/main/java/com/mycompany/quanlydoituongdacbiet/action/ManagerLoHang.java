package com.mycompany.quanlydoituongdacbiet.action;

import com.mycompany.quanlydoituongdacbiet.entity.LoHang;
import com.mycompany.quanlydoituongdacbiet.entity.LoHangXML;
import com.mycompany.quanlydoituongdacbiet.utils.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Quản lý danh sách LÔ HÀNG (đọc/ghi lohang.xml) và các phép tính tồn kho.
 * Tồn kho của 1 mặt hàng = tổng số lượng còn lại của tất cả các lô của mặt hàng đó.
 */
public class ManagerLoHang implements IQuanLy<LoHang> {
    private static final String LOHANG_FILE_NAME = "lohang.xml";
    private List<LoHang> listLo;

    public ManagerLoHang() {
        taiLai();
    }

    /** Đọc lại dữ liệu mới nhất từ file (khi màn hình khác vừa thay đổi dữ liệu). */
    public final void taiLai() {
        listLo = new ArrayList<>();
        if (!new File(LOHANG_FILE_NAME).exists()) {
            luu();
            return;
        }
        LoHangXML xml = (LoHangXML) FileUtils.readXMLFile(LOHANG_FILE_NAME, LoHangXML.class);
        if (xml != null && xml.getLoHang() != null) {
            listLo = xml.getLoHang();
        }
    }

    public void luu() {
        LoHangXML xml = new LoHangXML();
        xml.setLoHang(listLo);
        FileUtils.writeXMLtoFile(LOHANG_FILE_NAME, xml);
    }

    private int getNextId() {
        int maxId = 0;
        for (LoHang lo : listLo) {
            maxId = Math.max(maxId, lo.getId());
        }
        return maxId + 1;
    }

    // ================= IQuanLy =================
    @Override
    public void add(LoHang lo) {
        lo.setId(getNextId());
        listLo.add(lo);
        luu();
    }

    @Override
    public void edit(LoHang lo) {
        for (int i = 0; i < listLo.size(); i++) {
            if (listLo.get(i).getId() == lo.getId()) {
                listLo.set(i, lo);
                luu();
                return;
            }
        }
    }

    @Override
    public boolean delete(LoHang lo) {
        boolean removed = listLo.removeIf(x -> x.getId() == lo.getId());
        if (removed) {
            luu();
        }
        return removed;
    }

    @Override
    public List<LoHang> getList() {
        return listLo;
    }

    // ================= Truy vấn =================
    public LoHang findById(int id) {
        for (LoHang lo : listLo) {
            if (lo.getId() == id) {
                return lo;
            }
        }
        return null;
    }

    public List<LoHang> getLoCuaHang(int idHangHoa) {
        List<LoHang> kq = new ArrayList<>();
        for (LoHang lo : listLo) {
            if (lo.getIdHangHoa() == idHangHoa) {
                kq.add(lo);
            }
        }
        return kq;
    }

    /** So sánh theo HSD tăng dần (lô không có HSD xếp cuối), cùng HSD thì lô nhập trước xếp trước. */
    public static final Comparator<LoHang> THEO_HSD = (a, b) -> {
        if (a.getHanSuDung() == null && b.getHanSuDung() == null) {
            return Integer.compare(a.getId(), b.getId());
        }
        if (a.getHanSuDung() == null) {
            return 1;
        }
        if (b.getHanSuDung() == null) {
            return -1;
        }
        int c = a.getHanSuDung().compareTo(b.getHanSuDung());
        return c != 0 ? c : Integer.compare(a.getId(), b.getId());
    };

    /**
     * Các lô CÓ THỂ XUẤT BÁN của một mặt hàng, sắp theo nguyên tắc FEFO
     * (First Expired - First Out: hết hạn trước thì xuất trước). Lô hết hạn bị loại.
     */
    public List<LoHang> getLoCoTheXuat(int idHangHoa) {
        List<LoHang> kq = new ArrayList<>();
        for (LoHang lo : getLoCuaHang(idHangHoa)) {
            if (lo.coTheXuat()) {
                kq.add(lo);
            }
        }
        kq.sort(THEO_HSD);
        return kq;
    }

    /** Các lô còn hàng (kể cả đã hết hạn) – dùng khi lập phiếu huỷ. */
    public List<LoHang> getLoConHang(int idHangHoa) {
        List<LoHang> kq = new ArrayList<>();
        for (LoHang lo : getLoCuaHang(idHangHoa)) {
            if (lo.getSoLuongCon() > 0) {
                kq.add(lo);
            }
        }
        kq.sort(THEO_HSD);
        return kq;
    }

    /** Tổng tồn thực tế trong kho (gồm cả hàng hết hạn chưa xử lý). */
    public int tinhTonKho(int idHangHoa) {
        int ton = 0;
        for (LoHang lo : getLoCuaHang(idHangHoa)) {
            ton += lo.getSoLuongCon();
        }
        return ton;
    }

    /** Tồn có thể bán (chỉ tính các lô còn hạn). */
    public int tinhTonKhaDung(int idHangHoa) {
        int ton = 0;
        for (LoHang lo : getLoCoTheXuat(idHangHoa)) {
            ton += lo.getSoLuongCon();
        }
        return ton;
    }

    /** HSD sớm nhất trong các lô còn hàng của mặt hàng (null nếu không có). */
    public Date getHanSuDungGanNhat(int idHangHoa) {
        Date ganNhat = null;
        for (LoHang lo : getLoCuaHang(idHangHoa)) {
            if (lo.getSoLuongCon() > 0 && lo.getHanSuDung() != null
                    && (ganNhat == null || lo.getHanSuDung().before(ganNhat))) {
                ganNhat = lo.getHanSuDung();
            }
        }
        return ganNhat;
    }

    /** Bảng tồn kho: idHangHoa -> số lượng tồn. */
    public Map<Integer, Integer> getBangTonKho() {
        Map<Integer, Integer> map = new HashMap<>();
        for (LoHang lo : listLo) {
            map.merge(lo.getIdHangHoa(), lo.getSoLuongCon(), Integer::sum);
        }
        return map;
    }

    /** Bảng HSD gần nhất: idHangHoa -> HSD sớm nhất của các lô còn hàng. */
    public Map<Integer, Date> getBangHsdGanNhat() {
        Map<Integer, Date> map = new HashMap<>();
        for (LoHang lo : listLo) {
            if (lo.getSoLuongCon() > 0 && lo.getHanSuDung() != null) {
                Date cu = map.get(lo.getIdHangHoa());
                if (cu == null || lo.getHanSuDung().before(cu)) {
                    map.put(lo.getIdHangHoa(), lo.getHanSuDung());
                }
            }
        }
        return map;
    }

    public boolean coLoCuaHang(int idHangHoa) {
        return listLo.stream().anyMatch(lo -> lo.getIdHangHoa() == idHangHoa);
    }

    public boolean coLoCuaNhaCungCap(int idNhaCungCap, String tenNhaCungCap) {
        return listLo.stream().anyMatch(lo -> lo.getIdNhaCungCap() == idNhaCungCap
                || (tenNhaCungCap != null && tenNhaCungCap.equalsIgnoreCase(lo.getTenNhaCungCap())));
    }
}
