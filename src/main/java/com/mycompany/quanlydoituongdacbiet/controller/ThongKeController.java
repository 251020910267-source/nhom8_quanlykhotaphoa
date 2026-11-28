package com.mycompany.quanlydoituongdacbiet.controller;

import com.mycompany.quanlydoituongdacbiet.action.ManagerHangHoa;
import com.mycompany.quanlydoituongdacbiet.action.ManagerLoHang;
import com.mycompany.quanlydoituongdacbiet.action.ManagerPhieuGiaoDich;
import com.mycompany.quanlydoituongdacbiet.action.ThongKeAction;
import com.mycompany.quanlydoituongdacbiet.entity.HangHoa;
import com.mycompany.quanlydoituongdacbiet.entity.LoHang;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuGiaoDich;
import com.mycompany.quanlydoituongdacbiet.utils.FormatUtils;
import com.mycompany.quanlydoituongdacbiet.view.ThongKeView;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Controller điều phối giữa ThongKeView và ThongKeAction (Thành viên 5).
 */
public class ThongKeController {

    private final ThongKeView view;
    private final ThongKeAction thongKeAction;
    private final ManagerHangHoa managerHangHoa;
    private final ManagerPhieuGiaoDich managerPhieu;

    private List<HangHoa> danhSachDangHienThi;

    public ThongKeController(ThongKeView view) {
        this.view = view;
        this.thongKeAction = new ThongKeAction();
        this.managerHangHoa = new ManagerHangHoa();
        this.managerPhieu = new ManagerPhieuGiaoDich();

        this.view.addTimKiemListener(new TimKiemListener());
        this.view.addCanhBaoListener(new CanhBaoListener());
        this.view.addSapHetTonListener(new SapHetTonListener());
        this.view.addLamMoiListener(new LamMoiListener());
        this.view.addXuatBaoCaoListener(new XuatBaoCaoListener());

        taiLaiDuLieuVaThongKe();
    }

    public void showThongKeView() {
        taiLaiDuLieuVaThongKe();
        view.setVisible(true);
    }

    public final void taiLaiDuLieuVaThongKe() {
        managerHangHoa.taiLai();
        managerPhieu.taiLai();

        List<HangHoa> dsHang = managerHangHoa.getList();
        ManagerLoHang qlLo = managerPhieu.getManagerLoHang();
        List<LoHang> dsLo = qlLo.getList();
        List<PhieuGiaoDich> dsPhieu = managerPhieu.getList();

        this.danhSachDangHienThi = dsHang;
        view.showListHangHoa(dsHang, qlLo);

        int tongMatHang = thongKeAction.tinhTongSoMatHang(dsHang);
        int tongSoLo = dsLo.size();
        long tongGiaTriTon = thongKeAction.tinhTongGiaTriTonKho(dsLo);
        HangHoa maxGia = thongKeAction.timHangGiaCaoNhat(dsHang);
        HangHoa minGia = thongKeAction.timHangGiaThapNhat(dsHang);
        long tongNhap = thongKeAction.tinhTongTienNhap(dsPhieu);
        long tongXuat = thongKeAction.tinhTongDoanhThuXuat(dsPhieu);
        long tongThietHai = thongKeAction.tinhTongThietHaiHuy(dsPhieu);
        int soLoCanhBaoHSD = thongKeAction.layDanhSachLoCanhBaoHetHan(dsLo, 30).size();
        int soHangSapHetTon = thongKeAction.layMatHangSapHetTon(dsHang, qlLo).size();

        Map<String, String> theoNhom = thongKeAction.thongKeChiTietTheoNhom(dsHang, qlLo);
        Map<String, long[]> theoLyDoHuy = thongKeAction.thongKeThietHaiTheoLyDo(dsPhieu);

        view.updateThongKe(tongMatHang, tongSoLo, tongGiaTriTon, maxGia, minGia,
                tongNhap, tongXuat, tongThietHai, soLoCanhBaoHSD, soHangSapHetTon,
                theoNhom, theoLyDoHuy);
    }

    private class TimKiemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String tuKhoa = view.getTuKhoa();
            String strTu = view.getGiaTuText().trim();
            String strDen = view.getGiaDenText().trim();

            long giaTu = 0;
            long giaDen = Long.MAX_VALUE;
            try {
                if (!strTu.isEmpty()) {
                    giaTu = FormatUtils.parseMoney(strTu);
                }
                if (!strDen.isEmpty()) {
                    giaDen = FormatUtils.parseMoney(strDen);
                }
                if (!strTu.isEmpty() && !strDen.isEmpty() && giaTu > giaDen) {
                    view.showMessage("Khoảng giá không hợp lệ: 'Đơn giá từ' phải nhỏ hơn hoặc bằng 'Đến'!");
                    return;
                }
            } catch (NumberFormatException ex) {
                view.showMessage("Vui lòng nhập khoảng đơn giá là số hợp lệ (ví dụ: 5000 hoặc 5.000)!");
                return;
            }

            managerHangHoa.taiLai();
            managerPhieu.taiLai();
            ManagerLoHang qlLo = managerPhieu.getManagerLoHang();
            List<HangHoa> ketQua = thongKeAction.timKiemKetHop(
                    managerHangHoa.getList(), qlLo.getList(), tuKhoa, giaTu, giaDen);
            danhSachDangHienThi = ketQua;
            view.showListHangHoa(ketQua, qlLo);

            if (ketQua.isEmpty()) {
                view.showMessage("Không tìm thấy mặt hàng nào phù hợp với điều kiện lọc!");
            }
        }
    }

    private class CanhBaoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            managerHangHoa.taiLai();
            managerPhieu.taiLai();
            ManagerLoHang qlLo = managerPhieu.getManagerLoHang();
            List<HangHoa> dsCanhBao = thongKeAction.layMatHangCoLoSapHetHan(
                    managerHangHoa.getList(), qlLo.getList(), 30);
            danhSachDangHienThi = dsCanhBao;
            view.showListHangHoa(dsCanhBao, qlLo);
            if (dsCanhBao.isEmpty()) {
                view.showMessage("Không có mặt hàng nào có lô hết hạn hoặc sắp hết hạn trong 30 ngày tới.");
            } else {
                view.showMessage("Có " + dsCanhBao.size() + " mặt hàng có lô đã hết hạn hoặc sắp hết hạn trong 30 ngày!");
            }
        }
    }

    private class SapHetTonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            managerHangHoa.taiLai();
            managerPhieu.taiLai();
            ManagerLoHang qlLo = managerPhieu.getManagerLoHang();
            List<HangHoa> dsSapHet = thongKeAction.layMatHangSapHetTon(managerHangHoa.getList(), qlLo);
            danhSachDangHienThi = dsSapHet;
            view.showListHangHoa(dsSapHet, qlLo);
            if (dsSapHet.isEmpty()) {
                view.showMessage("Tất cả mặt hàng đều đang có tồn khả dụng trên mức tối thiểu.");
            } else {
                view.showMessage("Có " + dsSapHet.size() + " mặt hàng chạm/dưới mức tồn tối thiểu, cần nhập thêm!");
            }
        }
    }

    private class LamMoiListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.clearSearchInputs();
            taiLaiDuLieuVaThongKe();
        }
    }

    /** Tính năng nâng cao: Xuất báo cáo thống kê ra file HTML để in ấn (dữ liệu gốc vẫn lưu XML). */
    private class XuatBaoCaoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser(new File("."));
            chooser.setDialogTitle("Xuất báo cáo thống kê kho hàng ra file HTML để in");
            chooser.setFileFilter(new FileNameExtensionFilter("Báo cáo HTML (*.html)", "html"));
            chooser.setSelectedFile(new File("BaoCaoKhoHangTapHoa.html"));
            if (chooser.showSaveDialog(view) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File f = chooser.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith(".html")) {
                f = new File(f.getAbsolutePath() + ".html");
            }

            ManagerLoHang qlLo = managerPhieu.getManagerLoHang();
            List<PhieuGiaoDich> dsPhieu = managerPhieu.getList();
            List<HangHoa> dsXuat = danhSachDangHienThi != null ? danhSachDangHienThi : managerHangHoa.getList();

            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'/><title>Báo cáo kho hàng tạp hoá</title>")
              .append("<style>body{font-family:'Times New Roman',serif;margin:24px;}table{width:100%;border-collapse:collapse;margin-top:12px;}")
              .append("th,td{border:1px solid #444;padding:6px;}th{background:#e6ecf5;}</style></head><body>")
              .append("<h2 style='text-align:center;'>BÁO CÁO TỒN KHO & HOẠT ĐỘNG KHO HÀNG TẠP HOÁ</h2>")
              .append("<p style='text-align:center;'>Ngày xuất báo cáo: ").append(FormatUtils.formatDateTime(new Date())).append("</p>")
              .append("<ul>")
              .append("<li><b>Tổng giá trị tồn kho hiện tại (giá vốn):</b> ")
              .append(FormatUtils.formatMoney(thongKeAction.tinhTongGiaTriTonKho(qlLo.getList()))).append("</li>")
              .append("<li><b>Tổng tiền nhập kho:</b> ").append(FormatUtils.formatMoney(thongKeAction.tinhTongTienNhap(dsPhieu)))
              .append(" &nbsp;|&nbsp; <b>Tổng doanh thu xuất bán:</b> ").append(FormatUtils.formatMoney(thongKeAction.tinhTongDoanhThuXuat(dsPhieu)))
              .append(" &nbsp;|&nbsp; <b>Tổng thiệt hại hàng hư hỏng/huỷ:</b> ").append(FormatUtils.formatMoney(thongKeAction.tinhTongThietHaiHuy(dsPhieu)))
              .append("</li></ul>")
              .append("<table><thead><tr><th>STT</th><th>Mã hàng</th><th>Tên hàng</th><th>Nhóm</th><th>ĐVT</th>")
              .append("<th>Giá bán lẻ</th><th>Tồn khả dụng</th><th>HSD gần nhất</th></tr></thead><tbody>");

            int stt = 1;
            for (HangHoa h : dsXuat) {
                Date hsd = qlLo.getHanSuDungGanNhat(h.getId());
                sb.append("<tr><td align='center'>").append(stt++).append("</td>")
                  .append("<td align='center'>").append(h.getMaHang()).append("</td>")
                  .append("<td>").append(h.getTenHang()).append("</td>")
                  .append("<td>").append(h.getNhomHang()).append("</td>")
                  .append("<td align='center'>").append(h.getDonViTinh()).append("</td>")
                  .append("<td align='right'>").append(FormatUtils.formatMoney(h.getDonGia())).append("</td>")
                  .append("<td align='center'>").append(qlLo.tinhTonKhaDung(h.getId())).append("</td>")
                  .append("<td align='center'>").append(hsd == null ? "—" : FormatUtils.formatDate(hsd)).append("</td></tr>");
            }
            sb.append("</tbody></table></body></html>");

            try (Writer w = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8)) {
                w.write(sb.toString());
                int mo = JOptionPane.showConfirmDialog(view,
                        "Đã xuất báo cáo thành công tại:\n" + f.getAbsolutePath() + "\n\nMở file báo cáo ngay?",
                        "Xuất báo cáo thành công", JOptionPane.YES_NO_OPTION);
                if (mo == JOptionPane.YES_OPTION && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(f);
                }
            } catch (Exception ex) {
                view.showMessage("Lỗi khi xuất file báo cáo: " + ex.getMessage());
            }
        }
    }
}
