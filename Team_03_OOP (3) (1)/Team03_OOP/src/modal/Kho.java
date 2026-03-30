package modal;

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
    // 1. CHỨC NĂNG NHẬP KHO (Cộng thêm số lượng vào sản phẩm hiện có)
    // ======================================================================
    public void nhapKho(String idSanPham, int soLuongNhap) {
        SanPham sp = timSanPhamTrongKho(idSanPham);
        if (sp != null) {
            sp.datSoLuong(sp.laySoLuong() + soLuongNhap);
        }
    }

    // ======================================================================
    // 2. CHỨC NĂNG KIỂM TRA XUẤT KHO (SỬA LỖI TRỪ GẤP ĐÔI)
    // ======================================================================
    // Chuyển từ việc trừ trực tiếp sang chỉ kiểm tra điều kiện tồn kho
    public boolean coTheXuatKho(String idSanPham, int soLuongXuat) {
        SanPham sp = timSanPhamTrongKho(idSanPham);
        if (sp != null) {
            return sp.laySoLuong() >= soLuongXuat;
        }
        return false;
    }

    // Thực hiện trừ số lượng sau khi đã kiểm tra thành công ở Controller
    public void thucHienTruSoLuong(String idSanPham, int soLuong) {
        SanPham sp = timSanPhamTrongKho(idSanPham);
        if (sp != null) {
            sp.datSoLuong(sp.laySoLuong() - soLuong);
        }
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
            double giaTriTon = sp.layGiaGoc() * sp.laySoLuong();
            tongGiaTriKho += giaTriTon;
            System.out.printf("| %-10s | %-25s | %-15s | %-10d | %-15.2f |%n",
                    sp.layID(), sp.layTen(), sp.layLoai(), sp.laySoLuong(), giaTriTon);
        }
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.printf("Tong gia tri hang hoa trong kho: %,.2f VND%n", tongGiaTriKho);
    }

    private SanPham timSanPhamTrongKho(String id) {
        return danhSachSanPham.stream()
                .filter(sp -> sp.layID().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public void themSanPham(SanPham sanPham) {
        if (timSanPhamTrongKho(sanPham.layID()) == null) {
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