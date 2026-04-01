package controller;

import dao.SanPhamDAO;
import model.SanPham;
import model.HoaDon;
import java.util.ArrayList;
import java.util.List;

public class QuanLySanPham {
    private List<SanPham> danhSachSanPham = new ArrayList<>();
    private SanPhamDAO sanPhamDAO = new SanPhamDAO();

    public QuanLySanPham() {
        refreshData();
    }

    public void refreshData() {
        this.danhSachSanPham = sanPhamDAO.getAll();
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
            return true;
        }
        return false;
    }

    public boolean xoaSanPham(String id) {
        SanPham sp = timSanPhamTheoID(id);
        if (sp != null) {
            danhSachSanPham.remove(sp);
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
            return true;
        }
        return false;
    }

}
