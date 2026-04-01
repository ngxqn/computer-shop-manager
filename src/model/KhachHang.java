package model;

import java.util.Scanner;

public class KhachHang extends Nguoi { // Giữ nguyên tên Class KhachHang
	
	// Constructor 6 đối số (Theo Nguoi đã sửa)
	public KhachHang(String id, String hoTen, String gioiTinh, String namSinh, String sdt, String diaChi) { // Đổi tên biến
		super(id, hoTen, gioiTinh, namSinh, sdt, diaChi); // Đổi tên biến
	}
	
	// GIỮ CONSTRUCTOR CŨ CHO TƯƠNG THÍCH (nếu có trường extra, bỏ qua extra)
	public KhachHang(String id, String hoTen, String gioiTinh, String namSinh, String sdt, String diaChi, String extra) { // Đổi tên biến
		super(id, hoTen, gioiTinh, namSinh, sdt, diaChi); // Đổi tên biến
	}
	
	public KhachHang() {
		super();
	}
	
	@Override
	public void nhap(Scanner sc) {
		super.nhap(sc); // Phương thức nhap của Nguoi
	}
	
	@Override
	public void xuat() {
		super.xuat(); // Phương thức xuat của Nguoi
		System.out.printf(" %n");
	}
	
	// PHƯƠNG THỨC HỖ TRỢ GHI FILE
	public StringBuilder getDataString() { 
		StringBuilder sb = super.getBaseDataString(); 
		sb.deleteCharAt(sb.length() - 1); // Xóa dấu phẩy cuối cùng
		return sb;
	}
}
