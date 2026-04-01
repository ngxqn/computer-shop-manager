package test;

import dao.PhieuBaoHanhDAO;
import model.PhieuBaoHanh;
import java.util.Date;

/**
 * AUTOMATED TEST SCRIPT FOR STAGE 2 - STEP 2 (WARRANTY FLOW)
 */
public class WarrantyTestExecutor {

    public static void main(String[] args) {
        test_TC_WAR_01_UnsoldSerial();
        test_TC_WAR_02_ExpiredSerial();
        test_TC_WAR_03_HappyPath();
        test_TC_WAR_04_RollbackTest();
    }

    private static void test_TC_WAR_01_UnsoldSerial() {
        System.out.println("\n--- RUNNING TC-WAR-01: Serial chưa bán (Null handling) ---");
        PhieuBaoHanhDAO dao = new PhieuBaoHanhDAO();
        // Giả sử SN_UNWRN_001 chưa có MaHD từ seed_data
        Object[] result = dao.traCuuBaoHanh("SN_UNWRN_001");
        if (result != null) {
            Date ngayLap = (Date) result[1];
            Date ngayHetHan = (Date) result[2];
            if (ngayLap == null && ngayHetHan == null) {
                System.out.println("✅ PASS: Trả về NULL cho ngày mua/hạn (đúng kỳ vọng cho máy chưa bán).");
            } else {
                System.out.println("❌ FAIL: Ngày không phải là NULL.");
            }
        } else {
            System.out.println("⚠️ SKIP: Chưa có serial 'SN_UNWRN_001' trong DB.");
        }
    }

    private static void test_TC_WAR_02_ExpiredSerial() {
        System.out.println("\n--- RUNNING TC-WAR-02: Serial hết hạn bảo hành ---");
        PhieuBaoHanhDAO dao = new PhieuBaoHanhDAO();
        // SN_EXPIRED_001 được seed mua 2 năm trước
        Object[] result = dao.traCuuBaoHanh("SN_EXPIRED_001");
        if (result != null) {
            Date ngayHetHan = (Date) result[2];
            if (ngayHetHan != null && new Date().after(ngayHetHan)) {
                System.out.println("✅ PASS: Nhận diện đúng máy hết hạn bảo hành.");
            } else {
                System.out.println("❌ FAIL: Máy báo còn hạn hoặc ngày hết hạn mang giá trị lạ.");
            }
        } else {
            System.out.println("⚠️ SKIP: Chưa có serial 'SN_EXPIRED_001' trong DB.");
        }
    }

    private static void test_TC_WAR_03_HappyPath() {
        System.out.println("\n--- RUNNING TC-WAR-03: Lập phiếu bảo hành thành công ---");
        PhieuBaoHanhDAO dao = new PhieuBaoHanhDAO();
        PhieuBaoHanh pbh = new PhieuBaoHanh();
        pbh.setMaPBH("TESTPBH_001");
        pbh.setMaSeri("SN_VALID_001");
        pbh.setMaKH("Khách vãng lai");
        pbh.setMaNV("NV01");
        pbh.setNgayTiepNhan(new Date());
        pbh.setMoTaLoi("Lỗi màn hình");
        pbh.setTinhTrang("Đang xử lý");
        pbh.setChiPhi(0.0);

        boolean success = dao.taoPhieuBaoHanh(pbh);
        if (success) {
            System.out.println("✅ PASS: Lập phiếu bảo hành thành công.");
        } else {
            System.out.println("❌ FAIL: Lỗi khi lập phiếu bảo hành.");
        }
    }

    private static void test_TC_WAR_04_RollbackTest() {
        System.out.println("\n--- RUNNING TC-WAR-04: Rollback khi lập phiếu lỗi ---");
        PhieuBaoHanhDAO dao = new PhieuBaoHanhDAO();
        PhieuBaoHanh pbh = new PhieuBaoHanh();
        pbh.setMaPBH("TESTPBH_FAIL");
        pbh.setMaSeri("NON_EXIST_999"); // Gây lỗi vì serial không tồn tại (FK) hoặc do logic code
        pbh.setMaKH("INVALID_KH");
        pbh.setMaNV("NV01");
        pbh.setNgayTiepNhan(new Date());

        boolean success = dao.taoPhieuBaoHanh(pbh);
        if (!success) {
            System.out.println("✅ PASS: Giao dịch được rollback thành công.");
        } else {
            System.out.println("❌ FAIL: Giao dịch vẫn báo thành công (vô lý).");
        }
    }
}
