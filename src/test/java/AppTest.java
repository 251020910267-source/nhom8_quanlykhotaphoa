import com.mycompany.quanlydoituongdacbiet.action.ManagerLoHang;
import com.mycompany.quanlydoituongdacbiet.action.ManagerPhieuGiaoDich;
import com.mycompany.quanlydoituongdacbiet.entity.LoHang;
import com.mycompany.quanlydoituongdacbiet.entity.LyDoHuHong;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuHuy;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuNhap;
import com.mycompany.quanlydoituongdacbiet.entity.PhieuXuat;
import com.mycompany.quanlydoituongdacbiet.entity.TrangThaiLoHang;
import com.mycompany.quanlydoituongdacbiet.utils.FormatUtils;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import junit.framework.TestCase;

/**
 * Kiểm thử tự động các quy tắc nghiệp vụ thực tế của kho tạp hoá (bản v2).
 */
public class AppTest extends TestCase {

    public void testDocDuLieuMauVaFEFO() {
        ManagerPhieuGiaoDich qlPhieu = new ManagerPhieuGiaoDich();
        ManagerLoHang qlLo = qlPhieu.getManagerLoHang();

        // Mặt hàng 1 (Sữa Fami) có 2 lô từ 2 NCC khác nhau, HSD khác nhau, Mã đơn nhập khác nhau
        List<LoHang> dsLoFami = qlLo.getLoCuaHang(1);
        assertEquals(2, dsLoFami.size());
        LoHang lo1 = qlLo.findById(1);
        LoHang lo2 = qlLo.findById(2);
        assertFalse(lo1.getMaDonNhap().equals(lo2.getMaDonNhap()));
        assertFalse(lo1.getHanSuDung().equals(lo2.getHanSuDung()));

        // Lô 1: nhập 40 - xuất 15 - chuột cắn 3 = còn 22, trạng thái Còn hàng (có hư hỏng)
        assertEquals(22, lo1.getSoLuongCon());
        assertEquals(TrangThaiLoHang.CON_HANG_CO_HAO_HUT, lo1.getTrangThai());

        // FEFO: Lô 1 HSD tháng 5/2027 sớm hơn Lô 2 HSD tháng 9/2027 => xếp trước
        List<LoHang> loCoTheXuat = qlLo.getLoCoTheXuat(1);
        assertEquals(2, loCoTheXuat.size());
        assertEquals(1, loCoTheXuat.get(0).getId());

        // Lô 4: đã xuất hết sạch
        assertEquals(TrangThaiLoHang.DA_XUAT_HET, qlLo.findById(4).getTrangThai());

        // Lô 5: bị ẩm mốc do thời tiết huỷ toàn bộ
        assertEquals(TrangThaiLoHang.DA_HUY, qlLo.findById(5).getTrangThai());

        // Lô 6: đã hết hạn => cấm xuất (không nằm trong danh sách getLoCoTheXuat)
        assertEquals(TrangThaiLoHang.HET_HAN, qlLo.findById(6).getTrangThai());
        assertEquals(0, qlLo.getLoCoTheXuat(4).size());
    }

    public void testChanGopHaiNhaCungCapVaoMotMaDonVaChanXuatLoHetHan() {
        ManagerPhieuGiaoDich qlPhieu = new ManagerPhieuGiaoDich();

        Calendar calHsd = Calendar.getInstance();
        calHsd.add(Calendar.MONTH, 8);

        // Cố tình dùng mã đơn DHN0001 (của NCC Thành Long) để nhập cho NCC Minh Tâm => phải bị chặn
        PhieuNhap pnSaiDon = new PhieuNhap("DHN0001", new Date(), 1, 10, 185000,
                2, "Nhà Phân Phối Minh Tâm", calHsd.getTime(), "Test trùng mã đơn khác NCC");
        String loiTrungDon = qlPhieu.nhapKho(pnSaiDon);
        assertNotNull("Phải chặn 2 NCC khác nhau dùng chung 1 mã đơn nhập", loiTrungDon);

        // Cố tình xuất bán từ lô 6 (đã hết hạn) => phải bị chặn
        PhieuXuat pxLoHetHan = new PhieuXuat("HDX9999", new Date(), 4, 6, 2, 32000, "Khách thử", "");
        String loiHetHan = qlPhieu.xuatKho(pxLoHetHan);
        assertNotNull("Phải chặn xuất bán lô hàng đã quá hạn sử dụng", loiHetHan);

        // Lập phiếu huỷ rồi xoá hoàn tác để không làm bẩn dữ liệu mẫu
        PhieuHuy phTam = new PhieuHuy("BBH9999", new Date(), 2, 2, LyDoHuHong.VO_MOP_MEO, "Vỡ khi xếp kệ");
        assertNull(qlPhieu.baoHuHong(phTam));
        assertEquals(28, qlPhieu.getManagerLoHang().findById(2).getSoLuongCon());
        assertNull(qlPhieu.xoaPhieu(phTam.getId()));
        assertEquals(30, qlPhieu.getManagerLoHang().findById(2).getSoLuongCon());

        // Kiểm tra đọc tiền bằng chữ trên Bill
        String bangChu = FormatUtils.docTienBangChu(5968600L);
        assertTrue(bangChu.toLowerCase().contains("năm triệu"));
    }
}
