package model;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

public class HoaDon {
    private String maGiaoDich;
    private Date ngayGiaoDich;
    private String maKhachHang;
    private String maNhanVien;
    private List<SanPham> danhSachSanPham = new ArrayList<>();

    public HoaDon(String maGiaoDich, String maKhachHang, String maNhanVien) {
        this.maGiaoDich = maGiaoDich;
        this.maKhachHang = maKhachHang;
        this.maNhanVien = maNhanVien;
        this.ngayGiaoDich = new Date();
    }

    public double tinhTongTien() {
        double tong = 0;
        for (SanPham sp : danhSachSanPham) {
            tong += sp.tinhGiaBan() * sp.laySoLuong();
        }
        return tong;
    }

    // Phương thức quan trọng để ghi dữ liệu vào DSHoaDon.txt
    public String taoDongLuuTru() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return maGiaoDich + "," + sdf.format(ngayGiaoDich) + "," + maKhachHang + "," + maNhanVien + "," + tinhTongTien();
    }

    // Getters
    public String layMaGiaoDich() { return maGiaoDich; }
    public Date layNgayGiaoDich() { return ngayGiaoDich; }
    public String layMaKhachHang() { return maKhachHang; }
    public String layMaNhanVien() { return maNhanVien; }
    public List<SanPham> layDanhSachSanPham() { return danhSachSanPham; }

    public void themSanPham(SanPham sp) {
        this.danhSachSanPham.add(sp);
    }
}
