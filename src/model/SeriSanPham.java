package model;

public class SeriSanPham {
    private String maSeri; // Định danh duy nhất (Serial/IMEI)
    private String maSP;
    private String maPN;   // Thuộc phiếu nhập nào
    private String maHD;   // Thuộc hóa đơn nào (nếu đã bán)
    private String tinhTrang;

    public SeriSanPham() {}

    public SeriSanPham(String maSeri, String maSP, String maPN, String maHD, String tinhTrang) {
        this.maSeri = maSeri;
        this.maSP = maSP;
        this.maPN = maPN;
        this.maHD = maHD;
        this.tinhTrang = tinhTrang;
    }

    // --- Getters ---
    public String getMaSeri() { return maSeri; }
    public String getMaSP() { return maSP; }
    public String getMaPN() { return maPN; }
    public String getMaHD() { return maHD; }
    public String getTinhTrang() { return tinhTrang; }

    // --- Setters ---
    public void setMaSeri(String maSeri) { this.maSeri = maSeri; }
    public void setMaSP(String maSP) { this.maSP = maSP; }
    public void setMaPN(String maPN) { this.maPN = maPN; }
    public void setMaHD(String maHD) { this.maHD = maHD; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }
}
