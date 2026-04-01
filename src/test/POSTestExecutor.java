package test;

import dao.HoaDonDAO;
import dao.SeriSanPhamDAO;
import model.ChiTietHoaDon;
import model.HoaDon;
import java.util.ArrayList;
import java.util.List;

/**
 * AUTOMATED TEST SCRIPT FOR STAGE 2 - STEP 1 (POS FLOW)
 */
public class POSTestExecutor {

    public static void main(String[] args) {
        test_TC_POS_01_InvalidSerial();
        test_TC_POS_02_AlreadySoldSerial();
        test_TC_POS_03_HappyPath();
        test_TC_POS_04_RollbackTest();
    }

    private static void test_TC_POS_01_InvalidSerial() {
        System.out.println("\n--- RUNNING TC-POS-01: Serial không tồn tại ---");
        SeriSanPhamDAO seriDAO = new SeriSanPhamDAO();
        Object[] result = seriDAO.traCuuSerialBanHang("INVALID_SERIAL_9999");
        if (result == null) {
            System.out.println("✅ PASS: Không tìm thấy serial giả.");
        } else {
            System.out.println("❌ FAIL: Tìm thấy serial giả (vô lý).");
        }
    }

    private static void test_TC_POS_02_AlreadySoldSerial() {
        System.out.println("\n--- RUNNING TC-POS-02: Serial đã bán ---");
        SeriSanPhamDAO seriDAO = new SeriSanPhamDAO();
        // Giả sử SN_TEST_001 đã có trong DB với trạng thái 'Đã bán' từ seed_data
        Object[] result = seriDAO.traCuuSerialBanHang("SN_TEST_001");
        if (result != null) {
            String tinhTrang = (String) result[3];
            if (tinhTrang.equalsIgnoreCase("Đã bán")) {
                System.out.println("✅ PASS: Trạng thái đúng là 'Đã bán'.");
            } else {
                System.out.println("❌ FAIL: Trạng thái là [" + tinhTrang + "], mong đợi 'Đã bán'.");
            }
        } else {
            System.out.println("⚠️ SKIP: Chưa có serial 'SN_TEST_001' trong DB. Hãy chạy seed_data_stage2.sql trước.");
        }
    }

    private static void test_TC_POS_03_HappyPath() {
        System.out.println("\n--- RUNNING TC-POS-03: Happy Path (Thanh toán) ---");
        HoaDonDAO hdDAO = new HoaDonDAO();
        HoaDon hd = new HoaDon();
        hd.setMaHD("HD_HAPPY_01");
        hd.setMaKH("Khách vãng lai");
        hd.setMaNV("NV01");
        hd.setNgayLap(new java.util.Date());

        List<ChiTietHoaDon> details = new ArrayList<>();
        ChiTietHoaDon ct1 = new ChiTietHoaDon();
        ct1.setMaSeri("SN_HAPPY_001");
        ct1.setDonGiaBan(25000000.0);
        details.add(ct1);

        boolean success = hdDAO.taoHoaDon(hd, details);
        if (success) {
            System.out.println("✅ PASS: Thanh toán thành công.");
        } else {
            System.out.println("❌ FAIL: Thanh toán thất bại (Có thể do FK constraint hoặc thiếu data).");
        }
    }

    private static void test_TC_POS_04_RollbackTest() {
        System.out.println("\n--- RUNNING TC-POS-04: Rollback Test (Failing on purpose) ---");
        HoaDonDAO hdDAO = new HoaDonDAO();
        HoaDon hd = new HoaDon();
        hd.setMaHD("HD_ROLLBACK_01");
        hd.setMaKH("Khách vãng lai");
        hd.setMaNV("NV01");
        
        List<ChiTietHoaDon> details = new ArrayList<>();
        ChiTietHoaDon ct1 = new ChiTietHoaDon();
        ct1.setMaSeri("NON_EXISTENT_SERIAL"); // Gây lỗi FK khi insert CHITIETHOADON
        ct1.setDonGiaBan(10.0);
        details.add(ct1);

        boolean success = hdDAO.taoHoaDon(hd, details);
        if (!success) {
            System.out.println("✅ PASS: Giao dịch bị từ chối và Rollback (đúng kỳ vọng).");
        } else {
            System.out.println("❌ FAIL: Giao dịch vẫn báo thành công (vô lý).");
        }
    }
}
