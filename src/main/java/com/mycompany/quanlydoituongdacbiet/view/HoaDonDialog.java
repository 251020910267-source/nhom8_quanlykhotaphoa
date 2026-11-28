package com.mycompany.quanlydoituongdacbiet.view;

import com.mycompany.quanlydoituongdacbiet.action.ManagerHangHoa;
import com.mycompany.quanlydoituongdacbiet.action.ManagerLoHang;
import com.mycompany.quanlydoituongdacbiet.action.ManagerNhaCungCap;
import com.mycompany.quanlydoituongdacbiet.action.ManagerPhieuGiaoDich;
import com.mycompany.quanlydoituongdacbiet.entity.HangHoa;
import com.mycompany.quanlydoituongdacbiet.entity.LoHang;
import com.mycompany.quanlydoituongdacbiet.entity.NhaCungCap;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuGiaoDich;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuHuy;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuNhap;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuXuat;
import com.mycompany.quanlydoituongdacbiet.utils.FormatUtils;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Cửa sổ Xem trước & Xuất Bill / Chứng từ kho (Phiếu Nhập, Hoá Đơn Bán Hàng, Biên Bản Huỷ).
 * Hỗ trợ gộp nhiều dòng hàng cùng Mã đơn / Số Bill lên 1 tờ chứng từ duy nhất,
 * in trực tiếp ra máy in / PDF hoặc xuất ra file .html / .txt.
 */
public class HoaDonDialog extends JDialog {

    private final String maDonHang;
    private final String htmlContent;
    private final String textContent;
    private final JEditorPane editorPane;

    public HoaDonDialog(Window owner, String maDonHang,
            ManagerPhieuGiaoDich qlPhieu, ManagerHangHoa qlHang, ManagerNhaCungCap qlNCC) {
        super(owner, "Xem & Xuất Bill - Mã chứng từ: " + maDonHang, ModalityType.APPLICATION_MODAL);
        this.maDonHang = maDonHang;

        List<PhieuGiaoDich> dsDong = qlPhieu.layDanhSachTheoMaDon(maDonHang);
        ManagerLoHang qlLo = qlPhieu.getManagerLoHang();
        this.htmlContent = taoNoiDungHTML(maDonHang, dsDong, qlHang, qlNCC, qlLo);
        this.textContent = taoNoiDungTextK80(maDonHang, dsDong, qlHang, qlNCC, qlLo);

        editorPane = new JEditorPane("text/html", htmlContent);
        editorPane.setEditable(false);
        editorPane.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(editorPane);
        scroll.setPreferredSize(new Dimension(760, 560));

        JButton btnIn = new JButton("In Bill / Xuất PDF (Máy in)");
        JButton btnLuuHtml = new JButton("Lưu file Bill (.html)");
        JButton btnLuuTxt = new JButton("Lưu Bill in nhiệt (.txt)");
        JButton btnDong = new JButton("Đóng");

        btnIn.addActionListener(e -> inChungTu());
        btnLuuHtml.addActionListener(e -> luuRaFile(true));
        btnLuuTxt.addActionListener(e -> luuRaFile(false));
        btnDong.addActionListener(e -> dispose());

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        pnlBottom.add(btnIn);
        pnlBottom.add(btnLuuHtml);
        pnlBottom.add(btnLuuTxt);
        pnlBottom.add(btnDong);

        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    private void inChungTu() {
        try {
            boolean done = editorPane.print();
            if (done) {
                JOptionPane.showMessageDialog(this, "Đã gửi lệnh in chứng từ thành công!");
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi in chứng từ: " + ex.getMessage(),
                    "Lỗi máy in", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void luuRaFile(boolean laHtml) {
        JFileChooser chooser = new JFileChooser(new File("."));
        String duoi = laHtml ? "html" : "txt";
        chooser.setDialogTitle("Lưu chứng từ / Bill ra file ." + duoi);
        chooser.setFileFilter(new FileNameExtensionFilter(
                laHtml ? "Trang HTML (*.html)" : "Văn bản in nhiệt (*.txt)", duoi));
        chooser.setSelectedFile(new File("Bill_" + maDonHang + "." + duoi));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith("." + duoi)) {
                file = new File(file.getAbsolutePath() + "." + duoi);
            }
            try (Writer w = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                w.write(laHtml ? htmlContent : textContent);
                int chon = JOptionPane.showConfirmDialog(this,
                        "Đã xuất Bill thành công tại:\n" + file.getAbsolutePath()
                        + "\n\nBạn có muốn mở file vừa xuất ngay không?",
                        "Xuất Bill thành công", JOptionPane.YES_NO_OPTION);
                if (chon == JOptionPane.YES_OPTION && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Không thể ghi file: " + ex.getMessage(),
                        "Lỗi lưu file", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ====================== Tạo nội dung HTML chuẩn để xem & in ======================
    private static String taoNoiDungHTML(String maDon, List<PhieuGiaoDich> ds,
            ManagerHangHoa qlHang, ManagerNhaCungCap qlNCC, ManagerLoHang qlLo) {
        if (ds == null || ds.isEmpty()) {
            return "<html><body><h3>Không tìm thấy dữ liệu cho chứng từ " + escape(maDon) + "</h3></body></html>";
        }
        PhieuGiaoDich p0 = ds.get(0);
        boolean laNhap = p0 instanceof PhieuNhap;
        boolean laXuat = p0 instanceof PhieuXuat;
        boolean laHuy = p0 instanceof PhieuHuy;

        String thongTinDoiTac;
        if (laNhap) {
            PhieuNhap pn = (PhieuNhap) p0;
            NhaCungCap ncc = qlNCC.findById(pn.getIdNhaCungCap());
            if (ncc == null) {
                for (NhaCungCap item : qlNCC.getListNCC()) {
                    if (item.getTenNCC().equalsIgnoreCase(pn.getTenNhaCungCap())) {
                        ncc = item;
                        break;
                    }
                }
            }
            thongTinDoiTac = "<b>Nhà cung cấp:</b> " + escape(pn.getTenNhaCungCap())
                    + (ncc != null ? "<br/><b>SĐT:</b> " + escape(ncc.getSoDienThoai())
                    + " &nbsp;|&nbsp; <b>Địa chỉ:</b> " + escape(ncc.getDiaChi()) : "");
        } else if (laXuat) {
            thongTinDoiTac = "<b>Khách hàng:</b> " + escape(((PhieuXuat) p0).getTenKhachHang());
        } else {
            thongTinDoiTac = "<b>Loại xử lý:</b> Xuất huỷ / Loại bỏ hàng hư hỏng khỏi kho";
        }

        long tongTienHang = 0;
        long tongThue = 0;
        long tongThanhToan = 0;
        int tongSoLuong = 0;

        StringBuilder rows = new StringBuilder();
        int stt = 1;
        for (PhieuGiaoDich p : ds) {
            HangHoa hh = qlHang.findById(p.getMaHangHoa());
            LoHang lo = qlLo.findById(p.getIdLoHang());
            String maHang = hh != null ? hh.getMaHang() : ("#" + p.getMaHangHoa());
            String tenHang = hh != null ? hh.getTenHang() : ("Mặt hàng #" + p.getMaHangHoa());
            String dvt = hh != null ? hh.getDonViTinh() : "";
            String maLo = lo != null ? lo.getMaLo() : ("LO#" + p.getIdLoHang());
            Date hsd = lo != null ? lo.getHanSuDung() : (p instanceof PhieuNhap ? ((PhieuNhap) p).getHanSuDung() : null);
            String chuoiLoHsd = maLo + (hsd != null ? "<br/><span style='font-size:10px;color:#555;'>HSD: "
                    + FormatUtils.formatDate(hsd) + "</span>" : "");

            long tienHang = Math.round(p.tinhTienHang());
            long thanhTien = Math.round(p.tinhThanhTien());
            tongTienHang += tienHang;
            if (p instanceof PhieuXuat) {
                tongThue += Math.round(((PhieuXuat) p).tinhTienThue());
            }
            tongThanhToan += thanhTien;
            tongSoLuong += p.getSoLuong();

            String cotPhu = laHuy ? ("<td>" + escape(p.getDoiTac())
                    + (p.getGhiChu().isEmpty() ? "" : "<br/><i>(" + escape(p.getGhiChu()) + ")</i>") + "</td>") : "";

            rows.append("<tr>")
                    .append("<td align='center'>").append(stt++).append("</td>")
                    .append("<td align='center'>").append(escape(maHang)).append("</td>")
                    .append("<td>").append(escape(tenHang)).append("</td>")
                    .append("<td align='center'>").append(escape(dvt)).append("</td>")
                    .append("<td align='center'>").append(chuoiLoHsd).append("</td>")
                    .append(cotPhu)
                    .append("<td align='right'>").append(p.getSoLuong()).append("</td>")
                    .append("<td align='right'>").append(FormatUtils.formatMoney(Math.round(p.getDonGia()))).append("</td>")
                    .append("<td align='right'><b>").append(FormatUtils.formatMoney(tienHang)).append("</b></td>")
                    .append("</tr>");
        }

        String cotTieuDePhu = laHuy ? "<th>Nguyên nhân hư hỏng</th>" : "";
        int soCot = laHuy ? 9 : 8;

        StringBuilder tongKet = new StringBuilder();
        if (laXuat) {
            tongKet.append("<tr><td colspan='").append(soCot - 1).append("' align='right'>Cộng tiền hàng (chưa VAT):</td>")
                    .append("<td align='right'>").append(FormatUtils.formatMoney(tongTienHang)).append("</td></tr>")
                    .append("<tr><td colspan='").append(soCot - 1).append("' align='right'>Thuế GTGT (VAT 10%):</td>")
                    .append("<td align='right'>").append(FormatUtils.formatMoney(tongThue)).append("</td></tr>");
        }
        String nhanTong = laNhap ? "TỔNG TIỀN NHẬP HÀNG:"
                : (laXuat ? "TỔNG TIỀN THANH TOÁN:" : "TỔNG GIÁ TRỊ THIỆT HẠI:");
        tongKet.append("<tr style='background:#f0f4fa;font-size:13px;'>")
                .append("<td colspan='").append(soCot - 1).append("' align='right'><b>").append(nhanTong).append("</b></td>")
                .append("<td align='right' style='color:#b30000;'><b>")
                .append(FormatUtils.formatMoney(tongThanhToan)).append("</b></td></tr>");

        String chuKyTrai = laNhap ? "Đại diện Nhà cung cấp" : (laXuat ? "Khách mua hàng" : "Người chứng kiến");
        String chuKyPhai = laXuat ? "Nhân viên bán hàng" : "Thủ kho / Người lập phiếu";

        return "<!DOCTYPE html><html><head><meta charset='UTF-8'/>"
                + "<style>"
                + "body { font-family: 'Times New Roman', serif; margin: 18px; color: #111; font-size: 12px; }"
                + "table.items { width: 100%; border-collapse: collapse; margin-top: 10px; }"
                + "table.items th, table.items td { border: 1px solid #444; padding: 5px 6px; }"
                + "table.items th { background: #e6ecf5; }"
                + "</style></head><body>"
                + "<div style='text-align:center;'>"
                + "<div style='font-size:15px;font-weight:bold;'>CỬA HÀNG TẠP HOÁ TỔNG HỢP NHÓM 8</div>"
                + "<div style='font-size:11px;'>Đ/c: Yên Nghĩa, Hà Đông, Hà Nội &bull; ĐT: 0988.123.456</div>"
                + "<hr style='border:0;border-top:1px dashed #666;margin:8px 0;'/>"
                + "<div style='font-size:18px;font-weight:bold;color:#003366;margin-top:4px;'>"
                + escape(p0.getTieuDeChungTu()) + "</div>"
                + "<div><b>Số chứng từ / Mã đơn:</b> " + escape(maDon)
                + " &nbsp;&nbsp;|&nbsp;&nbsp; <b>Ngày lập:</b> " + FormatUtils.formatDate(p0.getNgayGiaoDich())
                + " &nbsp;&nbsp;|&nbsp;&nbsp; <b>Ngày in:</b> " + FormatUtils.formatDateTime(new Date()) + "</div>"
                + "</div>"
                + "<div style='margin-top:10px;line-height:1.5;'>" + thongTinDoiTac + "</div>"
                + "<table class='items'><thead><tr>"
                + "<th>STT</th><th>Mã hàng</th><th>Tên hàng hoá</th><th>ĐVT</th><th>Mã lô</th>"
                + cotTieuDePhu
                + "<th>SL</th><th>Đơn giá</th><th>Thành tiền</th>"
                + "</tr></thead><tbody>"
                + rows
                + tongKet
                + "</tbody></table>"
                + "<div style='margin-top:8px;'><b>Tổng số lượng:</b> " + tongSoLuong
                + " &nbsp;|&nbsp; <b>Số tiền bằng chữ:</b> <i>"
                + escape(FormatUtils.docTienBangChu(tongThanhToan)) + ".</i></div>"
                + "<table style='width:100%;margin-top:22px;text-align:center;'><tr>"
                + "<td style='width:50%;'><b>" + chuKyTrai + "</b><br/><i>(Ký, ghi rõ họ tên)</i><br/><br/><br/><br/></td>"
                + "<td style='width:50%;'><b>" + chuKyPhai + "</b><br/><i>(Ký, ghi rõ họ tên)</i><br/><br/><br/><br/></td>"
                + "</tr></table>"
                + "</body></html>";
    }

    // ====================== Tạo nội dung Text khổ K80 máy in nhiệt ======================
    private static String taoNoiDungTextK80(String maDon, List<PhieuGiaoDich> ds,
            ManagerHangHoa qlHang, ManagerNhaCungCap qlNCC, ManagerLoHang qlLo) {
        if (ds == null || ds.isEmpty()) {
            return "Không có dữ liệu cho chứng từ " + maDon;
        }
        PhieuGiaoDich p0 = ds.get(0);
        boolean laXuat = p0 instanceof PhieuXuat;
        StringBuilder sb = new StringBuilder();
        String gach = "------------------------------------------------\n";
        sb.append("       CỬA HÀNG TẠP HOÁ TỔNG HỢP NHÓM 8\n");
        sb.append("     Đ/c: Yên Nghĩa, Hà Đông, Hà Nội\n");
        sb.append(gach);
        sb.append("   ").append(p0.getTieuDeChungTu()).append("\n");
        sb.append("Số chứng từ : ").append(maDon).append("\n");
        sb.append("Ngày GD     : ").append(FormatUtils.formatDate(p0.getNgayGiaoDich())).append("\n");
        sb.append("Đối tác     : ").append(p0.getDoiTac()).append("\n");
        sb.append(gach);
        sb.append(String.format("%-20s %5s %10s %10s\n", "Mặt hàng (Lô)", "SL", "Đơn giá", "T.Tiền"));
        sb.append(gach);

        long tongTienHang = 0;
        long tongThue = 0;
        long tongThanhToan = 0;
        for (PhieuGiaoDich p : ds) {
            HangHoa hh = qlHang.findById(p.getMaHangHoa());
            LoHang lo = qlLo.findById(p.getIdLoHang());
            String ten = hh != null ? hh.getTenHang() : ("HH#" + p.getMaHangHoa());
            String maLo = lo != null ? lo.getMaLo() : ("LO#" + p.getIdLoHang());
            long tienHang = Math.round(p.tinhTienHang());
            tongTienHang += tienHang;
            if (p instanceof PhieuXuat) {
                tongThue += Math.round(((PhieuXuat) p).tinhTienThue());
            }
            tongThanhToan += Math.round(p.tinhThanhTien());

            sb.append(ten).append(" [").append(maLo).append("]\n");
            sb.append(String.format("%20s %5d %10s %10s\n",
                    "", p.getSoLuong(),
                    FormatUtils.formatNumber(Math.round(p.getDonGia())),
                    FormatUtils.formatNumber(tienHang)));
        }
        sb.append(gach);
        if (laXuat) {
            sb.append(String.format("%-30s %17s\n", "Tiền hàng:", FormatUtils.formatMoney(tongTienHang)));
            sb.append(String.format("%-30s %17s\n", "VAT (10%):", FormatUtils.formatMoney(tongThue)));
        }
        sb.append(String.format("%-30s %17s\n", "TỔNG CỘNG:", FormatUtils.formatMoney(tongThanhToan)));
        sb.append("Bằng chữ: ").append(FormatUtils.docTienBangChu(tongThanhToan)).append(".\n");
        sb.append(gach);
        sb.append("      Cảm ơn Quý khách - Hẹn gặp lại!\n");
        return sb.toString();
    }

    private static String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
