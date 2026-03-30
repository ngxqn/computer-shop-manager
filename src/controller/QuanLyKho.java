package controller;
import dao.LuuTruDuLieu;

import model.Kho;
import model.SanPham;
import model.HoaDon;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class QuanLyKho extends LuuTruDuLieu {
    private List<Kho> danhSachKho = new ArrayList<>();
    private QuanLySanPham qlSanPham;
    private final String FILE_HOA_DON = "data/DSHoaDon.txt";

    public QuanLyKho(QuanLySanPham qlSanPham) {
        this.qlSanPham = qlSanPham;
        docFileTXT();
    }

    // NGHIỆP VỤ XUẤT KHO NỘI BỘ: Trừ Kho K01 và CỘNG vào Sản phẩm cửa hàng
    public boolean thucHienXuatKhoNoiBo(String idSP, int sl) {
        Kho kho = timKhoTheoMa("K01");
        if (kho != null && kho.coTheXuatKho(idSP, sl)) {
            // 1. Trừ trong kho nội bộ
            kho.thucHienTruSoLuong(idSP, sl);

            // 2. Cộng vào số lượng bày bán ở cửa hàng (DSSanPham.txt)
            if (qlSanPham != null) {
                SanPham spCuaHang = qlSanPham.timSanPhamTheoID(idSP);
                if (spCuaHang != null) {
                    spCuaHang.datSoLuong(spCuaHang.laySoLuong() + sl);
                    qlSanPham.ghiFileTXT(); // Lưu lại file sản phẩm
                }
            }
            return true;
        }
        return false;
    }

    // HÀM GHI HÓA ĐƠN VÀO FILE
    public void ghiLichSuHoaDon(HoaDon hd) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_HOA_DON, true))) {
            bw.write(hd.taoDongLuuTru());
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Lỗi khi lưu hóa đơn: " + e.getMessage());
        }
    }

    public void thucHienNhapKhoGiaoDien(String maKho, String idSP, int sl) {
        Kho kho = timKhoTheoMa(maKho);
        if (kho != null) {
            kho.nhapKho(idSP, sl);
        }
    }

    public Kho timKhoTheoMa(String maKho) {
        return danhSachKho.stream()
                .filter(k -> k.layMaKho().equalsIgnoreCase(maKho))
                .findFirst().orElse(null);
    }

    @Override
    public void docFileTXT() {
        if (danhSachKho.isEmpty()) {
            Kho khoChinh = new Kho("K01", "Kho Tong");
            if (qlSanPham != null) {
                for (SanPham sp : qlSanPham.layDanhSachSanPham()) {
                    // Khởi tạo kho với số lượng 0 hoặc theo logic của bạn
                    SanPham spKho = new SanPham(sp.layID(), sp.layTen(), sp.layLoai(), 0, sp.layGiaGoc());
                    khoChinh.themSanPham(spKho);
                }
            }
            danhSachKho.add(khoChinh);
        }
    }

    @Override public void ghiFileTXT() {}
}
