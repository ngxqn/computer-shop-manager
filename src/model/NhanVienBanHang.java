package model;

import java.util.Scanner;

public class NhanVienBanHang extends NhanVien  { // Giữ nguyên tên Interface
	
	// SỬA: Constructor 11 đối số
	public NhanVienBanHang (String chucVu, String id, String hoTen, String gioiTinh, String namSinh, String sdt, String diaChi, int soNgayNghi, String caLamViec) {
	    super(id, hoTen, gioiTinh, namSinh, sdt, diaChi, soNgayNghi, caLamViec);
	    super.setChucVu("Ban Hang"); 
	}

	public NhanVienBanHang() {
		super.setChucVu("Ban Hang"); 
	}

    @Override
    public float tinhLuong() {
        // Công thức mới: Lương cơ bản nhân với các hệ số trước, sau đó mới cộng thưởng
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
