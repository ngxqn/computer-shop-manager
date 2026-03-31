package model;

import java.util.Date;

public class HoaDon {
    private String maHD;
    private String maNV;
    private String maKH;
    private Date ngayLap;
    private double tongTien;

    public HoaDon() {}

    public HoaDon(String maHD, String maNV, String maKH, Date ngayLap, double tongTien) {
        this.maHD = maHD;
        this.maNV = maNV;
        this.maKH = maKH;
        this.ngayLap = ngayLap;
        this.tongTien = tongTien;
    }

    // --- Getters ---
    public String getMaHD() { return maHD; }
    public String getMaNV() { return maNV; }
    public String getMaKH() { return maKH; }
    public Date getNgayLap() { return ngayLap; }
    public double getTongTien() { return tongTien; }

    // --- Setters ---
    public void setMaHD(String maHD) { this.maHD = maHD; }
    public void setMaNV(String maNV) { this.maNV = maNV; }
    public void setMaKH(String maKH) { this.maKH = maKH; }
    public void setNgayLap(Date ngayLap) { this.ngayLap = ngayLap; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }
}
