package model;



abstract public class NhanVien extends Nguoi {
    public final int LuongCoBan = 5000000;
    protected int soNgayNghi;
    protected String chucVu;

    public String getChucVu() { return chucVu; }
    public int getLuongCoBan() { return LuongCoBan; }
    public int getSoNgayNghi() { return this.soNgayNghi; }

    public void setChucVu(String chucVu) { this.chucVu = chucVu; }
    public void setSoNgayNghi(int soNgayNghi) { this.soNgayNghi = soNgayNghi; }

    public NhanVien(String id, String hoTen, String gioiTinh, String namSinh, String sdt, String diaChi, int soNgayNghi) {
        super(id, hoTen, gioiTinh, namSinh, sdt, diaChi);
        this.soNgayNghi = soNgayNghi;
    }

    public NhanVien() {}

    public char xepLoai() {
        if (soNgayNghi <= 1) return 'A';
        else if (soNgayNghi <= 3) return 'B';
        else if (soNgayNghi <= 5) return 'C';
        else return 'D';
    }

    public float heSoXepLoai() {
        switch (xepLoai()) {
            case 'A': return 1.5f;
            case 'B': return 1.0f;
            case 'C': return 0.8f;
            case 'D': return 0.5f;
            default: return 0.0f;
        }
    }

    public abstract float tinhLuong();

}
