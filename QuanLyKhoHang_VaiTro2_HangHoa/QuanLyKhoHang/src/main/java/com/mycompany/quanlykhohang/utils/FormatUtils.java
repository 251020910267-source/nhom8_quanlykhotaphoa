package com.mycompany.quanlydoituongdacbiet.utils; // gói tiện ích

import java.text.DecimalFormat; // định dạng số theo mẫu
import java.text.DecimalFormatSymbols; // ký hiệu dùng khi định dạng số (dấu phân cách)
import java.text.SimpleDateFormat; // định dạng ngày tháng
import java.util.Date; // kiểu ngày

/** Định dạng tiền tệ (1.250.000 đ) và ngày (dd/MM/yyyy) theo quy định Việt Nam. */
public class FormatUtils { // lớp tiện ích định dạng

    public static final String DATE_PATTERN = "dd/MM/yyyy"; // mẫu ngày dùng chung toàn chương trình

    private static DecimalFormat moneyFormat() { // tạo bộ định dạng tiền kiểu Việt Nam
        DecimalFormatSymbols s = new DecimalFormatSymbols(); // tạo bộ ký hiệu mặc định
        s.setGroupingSeparator('.'); // dấu phân cách hàng nghìn là dấu chấm
        s.setDecimalSeparator(','); // dấu thập phân là dấu phẩy
        return new DecimalFormat("#,##0", s); // mẫu: nhóm 3 chữ số, tối thiểu 1 chữ số, dùng ký hiệu ở trên
    } // kết thúc hàm

    /** 1250000 -> "1.250.000 đ" */
    public static String formatMoney(long amount) { // số tiền -> chuỗi hiển thị
        return moneyFormat().format(amount) + " đ"; // định dạng số rồi nối thêm đơn vị đ
    } // kết thúc hàm

    /** "1.250.000", "1250000 đ", "1,250,000" -> 1250000. Ném NumberFormatException nếu rỗng/sai. */
    public static long parseMoney(String text) { // chuỗi người dùng nhập -> số
        String digits = text == null ? "" : text.replaceAll("[^0-9]", ""); // giữ lại duy nhất các chữ số, bỏ dấu chấm/phẩy/chữ
        if (digits.isEmpty()) { // nếu không còn chữ số nào
            throw new NumberFormatException("Đơn giá không hợp lệ"); // ném lỗi để nơi gọi báo cho người dùng
        } // kết thúc if
        return Long.parseLong(digits); // đổi chuỗi số thành long
    } // kết thúc hàm

    public static String formatDate(Date d) { // ngày -> chuỗi dd/MM/yyyy
        return d == null ? "" : new SimpleDateFormat(DATE_PATTERN).format(d); // null thì trả chuỗi rỗng, ngược lại định dạng theo mẫu
    } // kết thúc hàm
} // kết thúc lớp
