package model;

public class SanPham {
    private String maSP; 
    private String tenSP; 
    private String loaiSP; 
    private String maNCC;
    private double giaBan;
    private int tgBaoHanh; // Tính theo tháng
    private String trangThai;
    
    // --- Getters ---
    public String getMaSP() { return maSP; }
    public String getTenSP() { return tenSP; }
    public String getLoaiSP() { return loaiSP; }
    public String getMaNCC() { return maNCC; }
    public double getGiaBan() { return giaBan; }
    public int getTgBaoHanh() { return tgBaoHanh; }
    public String getTrangThai() { return trangThai; }

    // --- Setters ---
    public void setMaSP(String maSP) { this.maSP = maSP; }
    public void setTenSP(String tenSP) { this.tenSP = tenSP; }
    public void setLoaiSP(String loaiSP) { this.loaiSP = loaiSP; }
    public void setMaNCC(String maNCC) { this.maNCC = maNCC; }
    public void setGiaBan(double giaBan) { this.giaBan = giaBan; }
    public void setTgBaoHanh(int tgBaoHanh) { this.tgBaoHanh = tgBaoHanh; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    
    // Constructors
    public SanPham() {}

    public SanPham(String maSP, String tenSP, String loaiSP, String maNCC, double giaBan, int tgBaoHanh, String trangThai){ 
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.loaiSP = loaiSP;
        this.maNCC = maNCC;
        this.giaBan = giaBan;
        this.tgBaoHanh = tgBaoHanh;
        this.trangThai = trangThai;
    }
}
