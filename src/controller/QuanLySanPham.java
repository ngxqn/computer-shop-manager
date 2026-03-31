package controller;
import dao.LuuTruDuLieu;

import model.SanPham;
import model.HoaDon;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class QuanLySanPham extends LuuTruDuLieu {
    private List<SanPham> danhSachSanPham = new ArrayList<>();
    private final String DUONG_DAN_FILE = "data/DSSanPham.txt";
    private final String DUONG_DAN_HOA_DON = "data/DSHoaDon.txt";

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

    // --- CÁC HÀM QUẢN LÝ (CRUD) GIỮ NGUYÊN ---

    public List<SanPham> layDanhSachSanPham() {
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
            ghiFileTXT();
            return true;
        }
        return false;
    }

    public boolean xoaSanPham(String id) {
        SanPham sp = timSanPhamTheoID(id);
        if (sp != null) {
            danhSachSanPham.remove(sp);
            ghiFileTXT();
            return true;
        }
        return false;
    }

    public boolean suaSanPham(String id, String tenMoi, String loaiMoi, int slMoi, double giaMoi) {
        SanPham sp = timSanPhamTheoID(id);
        if (sp != null) {
            sp.setTenSP(tenMoi);
            sp.setLoaiSP(loaiMoi);
            // Logic cập nhật số lượng đã bị loại bỏ ở Model SanPham
            sp.setGiaBan(giaMoi);
            ghiFileTXT();
            return true;
        }
        return false;
    }

    // --- CÁC HÀM ĐỌC/GHI FILE HỆ THỐNG ---

    @Override
    public void docFileTXT() {
        // Đã chuyển toàn bộ sang JDBC Database, không còn đọc File TXT
        danhSachSanPham.clear();
    }

    @Override
    public void ghiFileTXT() {
        // Đã chuyển toàn bộ sang JDBC Database, không còn lưu File TXT
    }
}
