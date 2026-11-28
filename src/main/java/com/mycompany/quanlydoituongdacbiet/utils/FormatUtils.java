package com.mycompany.quanlydoituongdacbiet.utils; // gói tiện ích

import java.text.DecimalFormat; // định dạng số theo mẫu
import java.text.DecimalFormatSymbols; // ký hiệu dùng khi định dạng số (dấu phân cách)
import java.text.SimpleDateFormat; // định dạng ngày tháng
import java.util.Calendar;
import java.util.Date; // kiểu ngày

/** Định dạng tiền tệ (1.250.000 đ) và ngày (dd/MM/yyyy) theo quy định Việt Nam. */
public class FormatUtils { // lớp tiện ích định dạng

    public static final String DATE_PATTERN = "dd/MM/yyyy"; // mẫu ngày dùng chung toàn chương trình

    private static DecimalFormat moneyFormat() { // tạo bộ định dạng tiền kiểu Việt Nam
        DecimalFormatSymbols s = new DecimalFormatSymbols(); // tạo bộ ký hiệu mặc định
        s.setGroupingSeparator('.'); // dấu phân cách hàng nghìn là dấu chấm
        s.setDecimalSeparator(','); // dấu thập phân là dấu phẩy
        return new DecimalFormat("#,##0", s); // mẫu: nhóm 3 chữ số
    }

    /** 1250000 -> "1.250.000 đ" */
    public static String formatMoney(long amount) {
        return moneyFormat().format(amount) + " đ";
    }

    /** 1250000 -> "1.250.000" (không kèm đơn vị, dùng cho ô nhập) */
    public static String formatNumber(long amount) {
        return moneyFormat().format(amount);
    }

    /** "1.250.000", "1250000 đ", "1,250,000" -> 1250000. Ném NumberFormatException nếu rỗng/sai. */
    public static long parseMoney(String text) {
        String digits = text == null ? "" : text.replaceAll("[^0-9]", ""); // chỉ giữ lại chữ số
        if (digits.isEmpty()) {
            throw new NumberFormatException("Đơn giá không hợp lệ");
        }
        if (digits.length() > 15) {
            throw new NumberFormatException("Số tiền quá lớn");
        }
        return Long.parseLong(digits);
    }

    public static String formatDate(Date d) { // ngày -> chuỗi dd/MM/yyyy
        return d == null ? "" : new SimpleDateFormat(DATE_PATTERN).format(d);
    }

    public static String formatDateTime(Date d) {
        return d == null ? "" : new SimpleDateFormat("dd/MM/yyyy HH:mm").format(d);
    }

    /**
     * Kiểm tra năm hợp lý (2000..2100). Tránh lỗi gõ nhầm kiểu 16/05/20272027
     * mà bản cũ đã gặp (JDateChooser vẫn chấp nhận năm 8 chữ số).
     */
    public static boolean isNamHopLe(Date d) {
        if (d == null) {
            return false;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int nam = c.get(Calendar.YEAR);
        return nam >= 2000 && nam <= 2100;
    }

    // ======================= Đọc số tiền bằng chữ (in trên hoá đơn) =======================
    private static final String[] CHU_SO = {"không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
    private static final String[] HANG = {"", " nghìn", " triệu", " tỷ", " nghìn tỷ", " triệu tỷ"};

    /** 1250000 -> "Một triệu hai trăm năm mươi nghìn đồng" */
    public static String docTienBangChu(long soTien) {
        if (soTien == 0) {
            return "Không đồng";
        }
        StringBuilder kq = new StringBuilder();
        int hang = 0;
        long so = soTien;
        boolean coNhomPhiaTruoc = false;
        while (so > 0) {
            int nhom = (int) (so % 1000);
            so /= 1000;
            if (nhom > 0) {
                String chu = docBaChuSo(nhom, so > 0) + HANG[hang];
                kq.insert(0, chu + (coNhomPhiaTruoc ? " " : ""));
                coNhomPhiaTruoc = true;
            }
            hang++;
        }
        String s = kq.toString().trim() + " đồng";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    /** Đọc 1 nhóm 3 chữ số. dayDu = true khi phía trước còn nhóm khác (phải đọc "không trăm"). */
    private static String docBaChuSo(int n, boolean dayDu) {
        int tram = n / 100, chuc = (n / 10) % 10, donVi = n % 10;
        StringBuilder sb = new StringBuilder();
        if (tram > 0 || dayDu) {
            sb.append(CHU_SO[tram]).append(" trăm");
        }
        if (chuc > 1) {
            sb.append(" ").append(CHU_SO[chuc]).append(" mươi");
        } else if (chuc == 1) {
            sb.append(" mười");
        } else if (donVi > 0 && (tram > 0 || dayDu)) {
            sb.append(" lẻ");
        }
        if (donVi > 0) {
            String dv;
            if (donVi == 1 && chuc > 1) {
                dv = "mốt";
            } else if (donVi == 5 && chuc > 0) {
                dv = "lăm";
            } else if (donVi == 4 && chuc > 1) {
                dv = "tư";
            } else {
                dv = CHU_SO[donVi];
            }
            sb.append(" ").append(dv);
        }
        return sb.toString().trim();
    }
} // kết thúc lớp
