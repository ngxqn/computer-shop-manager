package model;

public class ChiTietPhieuNhap {
    private String maPN;
    private String maSP;
    private int soLuong;
    private double donGiaNhap;

    public ChiTietPhieuNhap(String maPN, String maSP, int soLuong, double donGiaNhap) {
        this.maPN = maPN;
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.donGiaNhap = donGiaNhap;
    }

    public ChiTietPhieuNhap() {}

    // Getters & Setters
    public String getMaPN() { return maPN; }
    public void setMaPN(String maPN) { this.maPN = maPN; }

    public String getMaSP() { return maSP; }
    public void setMaSP(String maSP) { this.maSP = maSP; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public double getDonGiaNhap() { return donGiaNhap; }
    public void setDonGiaNhap(double donGiaNhap) { this.donGiaNhap = donGiaNhap; }
}
