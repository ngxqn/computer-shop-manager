package model;

public class NhacCungCap {
    private String maNCC;
    private String tenNCC;
    private String diaChi;
    private String sdt;
    private String trangThai;

    public NhacCungCap(String maNCC, String tenNCC, String diaChi, String sdt, String trangThai) {
        this.maNCC = maNCC;
        this.tenNCC = tenNCC;
        this.diaChi = diaChi;
        this.sdt = sdt;
        this.trangThai = trangThai;
    }

    public NhacCungCap() {}

    // Getters and Setters
    public String getMaNCC() { return maNCC; }
    public void setMaNCC(String maNCC) { this.maNCC = maNCC; }

    public String getTenNCC() { return tenNCC; }
    public void setTenNCC(String tenNCC) { this.tenNCC = tenNCC; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    @Override
    public String toString() {
        return tenNCC; // Để hiển thị trong ComboBox
    }
}
