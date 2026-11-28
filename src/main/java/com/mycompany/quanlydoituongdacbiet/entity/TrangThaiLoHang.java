package com.mycompany.quanlydoituongdacbiet.entity;

/**
 * Trạng thái thực tế của một LÔ HÀNG trong kho tạp hoá.
 * Trạng thái được tính tự động từ số lượng nhập / đã xuất / hư hỏng và hạn sử dụng,
 * giúp dữ liệu luôn chính xác tuyệt đối, không bị lệch thủ công.
 */
public enum TrangThaiLoHang {
    CON_HANG("Còn trong kho"),
    CON_HANG_CO_HAO_HUT("Còn hàng (có hư hỏng)"),
    SAP_HET_HAN("Sắp hết hạn (<= 30 ngày)"),
    HET_HAN("Đã hết hạn - cấm xuất"),
    DA_XUAT_HET("Đã xuất kho hết"),
    DA_HUY("Bị hư hỏng / Đã huỷ");

    private final String tenHienThi;

    TrangThaiLoHang(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }

    @Override
    public String toString() {
        return tenHienThi;
    }
}
