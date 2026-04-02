package controller;

import dao.SanPhamDAO;
import model.SanPham;
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
        if (sanPhamDAO.insert(sp)) {
            refreshData();
            return true;
        }
        return false;
    }

    public boolean xoaSanPham(String id) {
        if (sanPhamDAO.delete(id)) {
            refreshData();
            return true;
        }
        return false;
    }

    /**
     * Cập nhật thông tin sản phẩm vào DB.
     */
    public boolean suaSanPham(String id, String tenMoi, String loaiMoi, double giaMoi) {
        SanPham sp = timSanPhamTheoID(id);
        if (sp != null) {
            sp.setTenSP(tenMoi);
            sp.setLoaiSP(loaiMoi);
            sp.setGiaBan(giaMoi);
            if (sanPhamDAO.update(sp)) {
                refreshData();
                return true;
            }
        }
        return false;
    }

}
