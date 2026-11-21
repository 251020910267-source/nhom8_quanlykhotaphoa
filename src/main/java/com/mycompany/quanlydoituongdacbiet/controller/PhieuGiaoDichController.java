package com.mycompany.quanlydoituongdacbiet.controller;

import com.mycompany.quanlydoituongdacbiet.action.ManagerHangHoa;
import com.mycompany.quanlydoituongdacbiet.action.ManagerNhaCungCap;
import com.mycompany.quanlydoituongdacbiet.action.ManagerPhieuGiaoDich;
import com.mycompany.quanlydoituongdacbiet.entity.HangHoa;
import com.mycompany.quanlydoituongdacbiet.entity.NhaCungCap;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuGiaoDich;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuNhap;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuXuat;
import com.mycompany.quanlydoituongdacbiet.utils.FormatUtils;
import java.util.Date;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class PhieuGiaoDichController {
    private ManagerPhieuGiaoDich managerPhieu;
    private ManagerHangHoa managerHangHoa;
    private ManagerNhaCungCap managerNCC;

    public PhieuGiaoDichController() {
        this.managerPhieu = new ManagerPhieuGiaoDich();
        this.managerHangHoa = new ManagerHangHoa();
        this.managerNCC = new ManagerNhaCungCap();
    }

    // Nạp danh sách Hàng hóa và Nhà cung cấp vào ComboBox trên giao diện
    public void loadComboBox(JComboBox<String> cboHangHoa, JComboBox<String> cboNhaCungCap) {
        cboHangHoa.removeAllItems();
        List<HangHoa> listHH = managerHangHoa.getList();
        if (listHH != null) {
            for (HangHoa hh : listHH) {
                cboHangHoa.addItem(hh.getId() + " - " + hh.getTenHang());
            }
        }

        cboNhaCungCap.removeAllItems();
        List<NhaCungCap> listNCC = managerNCC.getListNCC();
        if (listNCC != null) {
            for (NhaCungCap ncc : listNCC) {
                cboNhaCungCap.addItem(ncc.getTenNCC());
            }
        }
    }

    // Tìm tên hàng hóa theo ID để hiển thị lên bảng
    private String getTenHangById(int id) {
        List<HangHoa> listHH = managerHangHoa.getList();
        if (listHH != null) {
            for (HangHoa hh : listHH) {
                if (hh.getId() == id) {
                    return hh.getTenHang();
                }
            }
        }
        return "Mã HH #" + id;
    }

    // Đổ dữ liệu danh sách Phiếu Nhập/Xuất ra JTable
    public void showListPhieu(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (PhieuGiaoDich p : managerPhieu.getList()) {
            String loaiPhieu = (p instanceof PhieuNhap) ? "Phiếu Nhập" : "Phiếu Xuất";
            String doiTac = (p instanceof PhieuNhap)
                    ? ((PhieuNhap) p).getTenNhaCungCap()
                    : ((PhieuXuat) p).getTenKhachHang();

            model.addRow(new Object[]{
                    p.getId(),
                    loaiPhieu,
                    getTenHangById(p.getMaHangHoa()),
                    doiTac,
                    FormatUtils.formatDate(p.getNgayGiaoDich()),
                    p.getSoLuong(),
                    FormatUtils.formatMoney((long) p.getDonGia()),
                    FormatUtils.formatMoney((long) p.tinhThanhTien())
            });
        }
    }

    // Kiểm tra lỗi nhập liệu và thêm phiếu mới
    public boolean themPhieu(String loaiPhieu, String itemHangHoa, String nhaCungCap,
                             String khachHang, Date ngayGiaoDich, String strSoLuong, String strDonGia) {
        if (itemHangHoa == null || itemHangHoa.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn hàng hóa!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (ngayGiaoDich == null) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn ngày giao dịch!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        int soLuong;
        try {
            soLuong = Integer.parseInt(strSoLuong.trim());
            if (soLuong <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Số lượng phải là số nguyên dương (> 0)!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        long donGia;
        try {
            donGia = FormatUtils.parseMoney(strDonGia);
            if (donGia <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Đơn giá phải là số lớn hơn 0!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Tách lấy ID hàng hóa từ chuỗi "1 - Sữa tươi"
        int maHangHoa = Integer.parseInt(itemHangHoa.split(" - ")[0].trim());

        if ("Phiếu Nhập".equals(loaiPhieu)) {
            if (nhaCungCap == null || nhaCungCap.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn nhà cung cấp!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            PhieuNhap pn = new PhieuNhap(0, ngayGiaoDich, maHangHoa, soLuong, donGia, nhaCungCap.trim());
            managerPhieu.add(pn);
            JOptionPane.showMessageDialog(null, "Lập Phiếu Nhập thành công!");
            return true;
        } else {
            if (khachHang == null || khachHang.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập tên khách hàng!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            int tonHienTai = managerPhieu.tinhTonKho(maHangHoa);
            if (soLuong > tonHienTai) {
                JOptionPane.showMessageDialog(null,
                        "Số lượng xuất (" + soLuong + ") vượt quá tồn kho hiện tại (" + tonHienTai + ")!",
                        "Cảnh báo tồn kho", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            PhieuXuat px = new PhieuXuat(0, ngayGiaoDich, maHangHoa, soLuong, donGia, khachHang.trim());
            managerPhieu.add(px);
            JOptionPane.showMessageDialog(null, "Lập Phiếu Xuất thành công!");
            return true;
        }
    }

    // Xóa phiếu theo ID được chọn trên bảng
    public boolean xoaPhieu(int id) {
        PhieuGiaoDich target = null;
        for (PhieuGiaoDich p : managerPhieu.getList()) {
            if (p.getId() == id) {
                target = p;
                break;
            }
        }
        if (target != null) {
            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc muốn xóa phiếu ID = " + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                return managerPhieu.delete(target);
            }
        }
        return false;
    }

    public int getTonKho(int maHangHoa) {
        return managerPhieu.tinhTonKho(maHangHoa);
    }
}