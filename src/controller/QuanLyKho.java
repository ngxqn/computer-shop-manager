package controller;

import dao.SeriSanPhamDAO;
import dao.PhieuNhapDAO;
import model.Kho;
import model.PhieuNhapKho;
import java.util.List;

public class QuanLyKho {
    private SeriSanPhamDAO seriDAO = new SeriSanPhamDAO();
    private PhieuNhapDAO pnDAO = new PhieuNhapDAO();
    private QuanLySanPham qlSanPham;

    public QuanLyKho(QuanLySanPham qlSanPham) {
        this.qlSanPham = qlSanPham;
    }

    public int layTonKhoThucTe(String maSP) {
        return seriDAO.demTonKho(maSP);
    }

    public boolean nhapHangMoi(PhieuNhapKho pn, List<String> dsSeri, String maSP, double giaNhap) {
        return pnDAO.taoPhieuNhap(pn, dsSeri, maSP, giaNhap);
    }

    /** 
     * Hỗ trợ backward compatibility cho UI cũ (nếu còn). 
     * Trong Phase 3, nên dùng nhapHangMoi để có đầy đủ thông tin PN/Seri.
     */
    public void thucHienNhapKhoGiaoDien(String maKho, String idSP, int sl) {
        System.err.println("Cảnh báo: thucHienNhapKhoGiaoDien bị hạn chế trong Phase 3 (Thiếu Serial).");
    }

    public boolean thucHienXuatKhoNoiBo(String idSP, int sl) {
        System.err.println("Cảnh báo: thucHienXuatKhoNoiBo bị hạn chế. Vui lòng xuất qua Hóa đơn bán hàng.");
        return false;
    }

    // Dummy method cho UI cũ (MainFrame) để không lỗi biên dịch
    public Kho timKhoTheoMa(String maKho) {
        return new Kho(maKho, "Kho JDBC"); 
    }

    public QuanLySanPham getQlSanPham() {
        return qlSanPham;
    }
}
