package model;

import java.util.Scanner;

public class NhanVienThuNgan extends NhanVien{ // Giữ nguyên tên Interface
	private float doanhThu; // Đổi tên thuộc tính
	
	public float layDoanhThu() { // Đổi tên phương thức
		return doanhThu;
	}
	public void datDoanhThu(float doanhThu) { // Đổi tên phương thức
		this.doanhThu = doanhThu;
	}
	
	// SỬA: Constructor 11 đối số
	public NhanVienThuNgan(String chucVu, String id, String hoTen, String gioiTinh, String namSinh, String sdt, String diaChi, int soNgayNghi,String caLamViec) {
	    super(id, hoTen, gioiTinh, namSinh, sdt, diaChi, soNgayNghi, caLamViec);
	    super.datChucVu("Thu Ngan"); // Đổi tên phương thức
	}
	
	@Override
	public StringBuilder taoDong() { // Đổi tên phương thức
		StringBuilder sb = super.taoDong();
		return sb;
	}
	    
	public NhanVienThuNgan() {
		super.datChucVu("Thu Ngan"); // Đổi tên phương thức
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
