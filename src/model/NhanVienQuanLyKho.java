package model;

import java.util.Scanner;

public class NhanVienQuanLyKho extends NhanVien {
	
	// SỬA: Constructor 11 đối số
	public NhanVienQuanLyKho (String chucVu, String id, String hoTen, String gioiTinh, String namSinh, String sdt, String diaChi, int soNgayNghi, String caLamViec) {
	    super(id,hoTen,gioiTinh,namSinh,sdt,diaChi,soNgayNghi, caLamViec);
	    super.datChucVu("Quan Li Kho"); // Đổi tên phương thức
	}
	
	@Override
	public StringBuilder taoDong() { // Đổi tên phương thức
		StringBuilder sb = super.taoDong();
		return sb;
	}
	
	public NhanVienQuanLyKho() {
		super.datChucVu("Quan Li Kho"); // Đổi tên phương thức
	}

    @Override
    public float tinhLuong() {
        return (LuongCoBan * super.heSoXepLoai() * super.heSoCa());
    }
	
	@Override
	public void nhap(Scanner sc) {
		super.nhap(sc);
	}
	
	@Override
	public void xuat() {
		super.xuat();
	}
}
