package com.mycompany.quanlydoituongdacbiet.entity;

/**
 * Lý do hàng bị hư hỏng / phải loại khỏi kho (dùng cho Phiếu huỷ).
 * Khi ghi XML, JAXB lưu tên hằng (ví dụ CHUOT_CON_TRUNG).
 */
public enum LyDoHuHong {
    CHUOT_CON_TRUNG("Chuột / côn trùng cắn phá"),
    THOI_TIET("Thời tiết: ẩm mốc, ngấm nước, nóng chảy"),
    HET_HAN("Hết hạn sử dụng"),
    VO_MOP_MEO("Vỡ / móp méo / rách bao bì"),
    MAT_MAT("Mất mát / thất lạc"),
    KHAC("Lý do khác");

    private final String tenHienThi;

    LyDoHuHong(String tenHienThi) {
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
