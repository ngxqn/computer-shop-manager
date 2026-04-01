package controller;

import model.Kho;
import model.SanPham;
import model.HoaDon;
import java.util.ArrayList;
import java.util.List;

public class QuanLyKho {
    private List<Kho> danhSachKho = new ArrayList<>();
    private QuanLySanPham qlSanPham;

    public QuanLyKho(QuanLySanPham qlSanPham) {
        this.qlSanPham = qlSanPham;
        napDuLieuTuHT();
    }

    private void napDuLieuTuHT() {
        if (danhSachKho.isEmpty()) {
            Kho khoChinh = new Kho("K01", "Kho Tong");
            if (qlSanPham != null) {
                for (SanPham sp : qlSanPham.getSanPhamList()) {
                    SanPham spKho = new SanPham(
                        sp.getMaSP(), sp.getTenSP(), sp.getLoaiSP(), 
                        sp.getMaNCC(), sp.getGiaBan(), sp.getTgBaoHanh(), 
                        sp.getTrangThai()
                    );
                    khoChinh.themSanPham(spKho);
                }
            }
            danhSachKho.add(khoChinh);
        }
    }

    // NGHIỆP VỤ XUẤT KHO NỘI BỘ: Đã được stub lại để tương thích với kiến trúc mới
    public boolean thucHienXuatKhoNoiBo(String idSP, int sl) {
        Kho kho = timKhoTheoMa("K01");
        if (kho != null && kho.coTheXuatKho(idSP, sl)) {
            kho.thucHienTruSoLuong(idSP, sl);
            return true;
        }
        return false;
    }

    // HÀM GHI HÓA ĐƠN
    public void ghiLichSuHoaDon(HoaDon hd) {
        // Đã chuyển sang JDBC, không còn dùng ghi file TXT
    }

    public void thucHienNhapKhoGiaoDien(String maKho, String idSP, int sl) {
        Kho kho = timKhoTheoMa(maKho);
        if (kho != null) {
            kho.nhapKho(idSP, sl);
        }
    }

    public Kho timKhoTheoMa(String maKho) {
        return danhSachKho.stream()
                .filter(k -> k.getMaKho().equalsIgnoreCase(maKho))
                .findFirst().orElse(null);
    }

}
