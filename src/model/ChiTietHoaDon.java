package model;

public class ChiTietHoaDon {
    private String maHD;
    private String maSeri;
    private double donGiaBan;

    public ChiTietHoaDon() {}

    public ChiTietHoaDon(String maHD, String maSeri, double donGiaBan) {
        this.maHD = maHD;
        this.maSeri = maSeri;
        this.donGiaBan = donGiaBan;
    }

    // --- Getters ---
    public String getMaHD() { return maHD; }
    public String getMaSeri() { return maSeri; }
    public double getDonGiaBan() { return donGiaBan; }

    // --- Setters ---
    public void setMaHD(String maHD) { this.maHD = maHD; }
    public void setMaSeri(String maSeri) { this.maSeri = maSeri; }
    public void setDonGiaBan(double donGiaBan) { this.donGiaBan = donGiaBan; }
}
