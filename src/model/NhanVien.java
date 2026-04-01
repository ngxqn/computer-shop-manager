package model;

import java.util.Scanner;

abstract public class NhanVien extends Nguoi {
    public final int LuongCoBan = 5000000;
    protected int soNgayNghi;
    protected String caLamViec;
    protected String chucVu;

    public String getChucVu() { return chucVu; }
    public int getLuongCoBan() { return LuongCoBan; }
    public int getSoNgayNghi() { return this.soNgayNghi; }
    public String getCaLamViec() { return this.caLamViec; }

    public void setChucVu(String chucVu) { this.chucVu = chucVu; }
    public void setSoNgayNghi(int soNgayNghi) { this.soNgayNghi = soNgayNghi; }
    public void setCaLamViec(String caLamViec) { this.caLamViec = caLamViec; }

    public NhanVien(String id, String hoTen, String gioiTinh, String namSinh, String sdt, String diaChi, int soNgayNghi, String caLamViec) {
        super(id, hoTen, gioiTinh, namSinh, sdt, diaChi);
        this.soNgayNghi = soNgayNghi;
        this.caLamViec = caLamViec;
        this.kiemTraCaLamViec();
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

    public void kiemTraCaLamViec() {
        if (!caLamViec.equalsIgnoreCase("Sang") && !caLamViec.equalsIgnoreCase("Toi")) {
            this.caLamViec = "Sang";
        }
    }

    public float heSoCa() {
        return caLamViec.equalsIgnoreCase("Toi") ? 1.2f : 1.0f;
    }

    public abstract float tinhLuong();

    @Override
    public void nhap(Scanner sc) {
        super.nhap(sc);
        System.out.println("Moi nhap so ngay nghi: ");
        this.setSoNgayNghi(Integer.parseInt(sc.nextLine()));
        System.out.println("Moi nhap ca lam viec (Sang/Toi):");
        this.setCaLamViec(sc.nextLine());
        this.kiemTraCaLamViec();
    }

    @Override
    public void xuat() {
        super.xuat();
        System.out.printf(" %-15s | %-12d | %-12s | %-10s | %-15.0f |%n",
                chucVu, soNgayNghi, caLamViec, xepLoai(), tinhLuong());
    }

    public StringBuilder getDataString() {
        StringBuilder sb = new StringBuilder();
        sb.append(chucVu).append(",");
        sb.append(super.getBaseDataString()); // id, hoTen, gioiTinh, namSinh, sdt, diaChi,
        sb.append(soNgayNghi).append(",");
        sb.append(caLamViec);
        return sb;
    }
}
