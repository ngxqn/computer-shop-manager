package model;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class PhieuNhapKho {
    private String maGiaoDich;
    private Date ngayNhap;
    private String maNhanVien;
    private List<SanPham> danhSachSanPham = new ArrayList<>();

    public PhieuNhapKho(String maGiaoDich, Date ngayNhap, String maNhanVien) {
        this.maGiaoDich = maGiaoDich;
        this.ngayNhap = ngayNhap;
        this.maNhanVien = maNhanVien;
    }

    public PhieuNhapKho() {
        this.ngayNhap = new Date();
    }

    public double tinhTongGiaTri() {
        double tongGiaNhap = 0;
        for (SanPham sp : danhSachSanPham) {
            tongGiaNhap += sp.getGiaBan() * 0; // Giá trị tạm thời là 0
        }
        return tongGiaNhap;
    }

    // Getters & Setters
    public String getMaGiaoDich() { return maGiaoDich; }
    public Date getNgayNhap() { return ngayNhap; }
    public String getMaNhanVien() { return maNhanVien; }
    public List<SanPham> getSanPhamList() { return danhSachSanPham; }

    public void themSanPham(SanPham sp) {
        this.danhSachSanPham.add(sp);
    }
}
