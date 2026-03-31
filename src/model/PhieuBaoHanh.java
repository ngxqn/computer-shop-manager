package model;

import java.util.Date;

public class PhieuBaoHanh {
    private String maPBH;
    private String maSeri;
    private String maKH;
    private String maNV;
    private Date ngayTiepNhan;
    private Date ngayTraDuKien;
    private String moTaLoi;
    private String tinhTrang;
    private double chiPhi;

    // Constructors
    public PhieuBaoHanh() {}

    public PhieuBaoHanh(String maPBH, String maSeri, String maKH, String maNV, Date ngayTiepNhan, Date ngayTraDuKien, String moTaLoi, String tinhTrang, double chiPhi) {
        this.maPBH = maPBH;
        this.maSeri = maSeri;
        this.maKH = maKH;
        this.maNV = maNV;
        this.ngayTiepNhan = ngayTiepNhan;
        this.ngayTraDuKien = ngayTraDuKien;
        this.moTaLoi = moTaLoi;
        this.tinhTrang = tinhTrang;
        this.chiPhi = chiPhi;
    }

    // Getters and Setters
    public String getMaPBH() { return maPBH; }
    public void setMaPBH(String maPBH) { this.maPBH = maPBH; }

    public String getMaSeri() { return maSeri; }
    public void setMaSeri(String maSeri) { this.maSeri = maSeri; }

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public Date getNgayTiepNhan() { return ngayTiepNhan; }
    public void setNgayTiepNhan(Date ngayTiepNhan) { this.ngayTiepNhan = ngayTiepNhan; }

    public Date getNgayTraDuKien() { return ngayTraDuKien; }
    public void setNgayTraDuKien(Date ngayTraDuKien) { this.ngayTraDuKien = ngayTraDuKien; }

    public String getMoTaLoi() { return moTaLoi; }
    public void setMoTaLoi(String moTaLoi) { this.moTaLoi = moTaLoi; }

    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }

    public double getChiPhi() { return chiPhi; }
    public void setChiPhi(double chiPhi) { this.chiPhi = chiPhi; }
}
