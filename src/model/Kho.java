package model;

import java.util.List;
import java.util.ArrayList;

public class Kho {
    private String maKho;
    private String tenKho;
    private List<SanPham> danhSachSanPham;

    public Kho(String maKho, String tenKho) {
        this.maKho = maKho;
        this.tenKho = tenKho;
        this.danhSachSanPham = new ArrayList<>();
    }

    public Kho() {
        this.danhSachSanPham = new ArrayList<>();
    }

    // ======================================================================
    // 1. CHỨC NĂNG NHẬP KHO (Stub - Giờ đây quản lý qua Serial)
    // ======================================================================
    public void nhapKho(String idSanPham, int soLuongNhap) {
        // Tạm dừng logic này ở Phase 5 vì model đã tách biệt Serial
    }

    // ======================================================================
    // 2. CHỨC NĂNG KIỂM TRA XUẤT KHO (Stub)
    // ======================================================================
    public boolean coTheXuatKho(String idSanPham, int soLuongXuat) {
        return true; // Cho phép biên dịch thành công
    }

    public void thucHienTruSoLuong(String idSanPham, int soLuong) {
        // Stub - Đã chuyển sang quản lý theo Serial
    }

    // ======================================================================
    // 3. CHỨC NĂNG BÁO CÁO TỒN KHO
    // ======================================================================
    public void baoCaoTonKho() {
        System.out.println("\n========== BAO CAO TON KHO: " + tenKho.toUpperCase() + " ==========");
        System.out.printf("| %-10s | %-25s | %-15s | %-10s | %-15s |%n", "ID", "Ten San Pham", "Loai", "Ton Kho", "Gia Tri Ton");
        System.out.println("-----------------------------------------------------------------------------------------");
        double tongGiaTriKho = 0;
        for (SanPham sp : danhSachSanPham) {
            double giaTriTon = sp.getGiaBan() * 0; // Số lượng mặc định 0
            tongGiaTriKho += giaTriTon;
            System.out.printf("| %-10s | %-25s | %-15s | %-10d | %-15.2f |%n",
                    sp.getMaSP(), sp.getTenSP(), sp.getLoaiSP(), 0, giaTriTon);
        }
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.printf("Tong gia tri hang hoa trong kho: %,.2f VND%n", tongGiaTriKho);
    }

    private SanPham timSanPhamTrongKho(String id) {
        return danhSachSanPham.stream()
                .filter(sp -> sp.getMaSP().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public void themSanPham(SanPham sanPham) {
        if (timSanPhamTrongKho(sanPham.getMaSP()) == null) {
            this.danhSachSanPham.add(sanPham);
        }
    }

    // Getters/Setters
    public String layMaKho() { return maKho; }
    public String layTenKho() { return tenKho; }
    public List<SanPham> layDanhSachSanPham() { return danhSachSanPham; }
    public void datMaKho(String maKho) { this.maKho = maKho; }
    public void datTenKho(String tenKho) { this.tenKho = tenKho; }
}
