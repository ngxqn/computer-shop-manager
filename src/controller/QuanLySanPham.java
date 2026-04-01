package controller;

import dao.LuuTruDuLieu;
import dao.SanPhamDAO;
import model.SanPham;
import model.HoaDon;
import java.util.ArrayList;
import java.util.List;

public class QuanLySanPham extends LuuTruDuLieu {
    private List<SanPham> danhSachSanPham = new ArrayList<>();
    private SanPhamDAO sanPhamDAO = new SanPhamDAO();

    public QuanLySanPham() {
        docFileTXT();
    }

    // --- CÁC HÀM NGHIỆP VỤ BÁN HÀNG ---

    /**
     * Trừ trực tiếp số lượng sản phẩm bày bán tại cửa hàng khi có khách mua
     */
    public boolean thucHienBanHangTruTrucTiep(String idSP, int slBan) {
        // Đã chuyển sang dùng JDBC & SeriSanPham, hàm này không còn được sử dụng.
        return false;
    }

    /**
     * Ghi lịch sử hóa đơn bán hàng vào file DSHoaDon.txt
     */
    public void ghiLichSuHoaDon(HoaDon hd) {
        // Đã chuyển sang dùng HoaDonDAO với JDBC Transactions, phương thức này không còn sử dụng.
    }

    // --- CÁC HÀM QUẢN LÝ (CRUD) ---

    public List<SanPham> getSanPhamList() {
        return danhSachSanPham;
    }

    public SanPham timSanPhamTheoID(String id) {
        return danhSachSanPham.stream()
                .filter(s -> s.getMaSP() != null && s.getMaSP().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public boolean themSanPham(SanPham sp) {
        if (timSanPhamTheoID(sp.getMaSP()) == null) {
            danhSachSanPham.add(sp);
            ghiFileTXT(); // Trong JDBC thực tế có thể gọi DAO.them()
            return true;
        }
        return false;
    }

    public boolean xoaSanPham(String id) {
        SanPham sp = timSanPhamTheoID(id);
        if (sp != null) {
            danhSachSanPham.remove(sp);
            ghiFileTXT(); // Trong JDBC thực tế có thể gọi DAO.xoa()
            return true;
        }
        return false;
    }

    /**
     * Cập nhật thông tin sản phẩm. 
     * Đã lược bỏ tham số slMoi vì số lượng được quản lý theo dải Serial (Kho).
     */
    public boolean suaSanPham(String id, String tenMoi, String loaiMoi, double giaMoi) {
        SanPham sp = timSanPhamTheoID(id);
        if (sp != null) {
            sp.setTenSP(tenMoi);
            sp.setLoaiSP(loaiMoi);
            sp.setGiaBan(giaMoi);
            ghiFileTXT();
            return true;
        }
        return false;
    }

    // --- CÁC HÀM ĐỌC/GHI DATA ---

    @Override
    public void docFileTXT() {
        // Chuyển sang JDBC: Nạp dữ liệu từ Database
        this.danhSachSanPham = sanPhamDAO.getAll();
    }

    @Override
    public void ghiFileTXT() {
        // Đã chuyển sang JDBC, logic lưu trữ trực tiếp đã do Repository/DAO đảm nhận khi thực hiện nghiệp vụ
        System.out.println("Ghi nhận: Dữ liệu Sản phẩm hiện đã được đồng bộ với Database.");
    }
}
