package model;

import java.util.Scanner;

abstract public class Nguoi {
	protected String id;
	protected String hoTen; // Đổi tên thuộc tính
	protected String gioiTinh; // Đổi tên thuộc tính
	protected String namSinh; // Đổi tên thuộc tính
	
	protected String sdt;
	protected String diaChi; // Đổi tên thuộc tính
	
	// --- GETTERS ---
	public String layID() { // Đổi tên phương thức
		return id;
	}
	public String layHoTen() { // Đổi tên phương thức
		return hoTen;
	}
	public String layGioiTinh() { // Đổi tên phương thức
		return gioiTinh;
	}
	public String layNamSinh() { // Đổi tên phương thức
		return namSinh;
	}
	public String laySDT() { // Đổi tên phương thức
		return sdt;
	}
	public String layDiaChi() { // Đổi tên phương thức
		return diaChi;
	}
	
	// --- SETTERS ---
	public void datID(String id) { // Đổi tên phương thức
		this.id = id;
	}
	public void datHoTen(String hoTen) { // Đổi tên phương thức
		this.hoTen = hoTen;
	}
	public void datGioiTinh(String gioiTinh) { // Đổi tên phương thức
		this.gioiTinh = gioiTinh;
	}
	public void datNamSinh(String namSinh) { // Đổi tên phương thức
		this.namSinh = namSinh;
	}
	public void datSDT(String sdt) { // Đổi tên phương thức
		this.sdt = sdt;
	}
	public void datDiaChi(String diaChi) { // Đổi tên phương thức
		this.diaChi = diaChi;
	}
	
	// SỬA: Constructor với 6 đối số
	public Nguoi(String id, String hoTen, String gioiTinh, String namSinh, String sdt, String diaChi) { // Đổi tên biến
		this.id = id;
		this.hoTen = hoTen;
		this.gioiTinh = gioiTinh;
		this.namSinh = namSinh;
		this.sdt = sdt;
		this.diaChi = diaChi;
	}
	
	public Nguoi() {
	}

	
	// --- PHƯƠNG THỨC NHẬP/XUẤT ---
	public void nhap(Scanner sc) {
		try {
		System.out.println("Moi nhap ho ten:");
		this.datHoTen(sc.nextLine()); // Đổi tên phương thức
		System.out.println("Moi nhap gioi tinh (Nam/Nu):");
		this.datGioiTinh(sc.nextLine()); // Đổi tên phương thức
		System.out.println("Moi nhap nam sinh (VD: 1990):");
		this.datNamSinh(sc.nextLine()); // Đổi tên phương thức
		System.out.println("Moi nhap SDT:");
		this.datSDT(sc.nextLine()); // Đổi tên phương thức
		System.out.println("Moi nhap dia chi:");
		this.datDiaChi(sc.nextLine()); // Đổi tên phương thức
		} catch (Exception e) {
			System.out.println("Loi khi nhap Nguoi: " + e.getMessage());
		}
	}
	
	public void xuat() {
		System.out.printf("| %-10s | %-25s | %-10s | %-10s | %-15s | %-30s |", id, hoTen, gioiTinh, namSinh, sdt, diaChi); // Đổi tên thuộc tính
	}

	// --- PHƯƠNG THỨC HỖ TRỢ GHI FILE (cho lớp con) ---
	public StringBuilder taoDongCoBan() { // Đổi tên phương thức
		StringBuilder sb = new StringBuilder();
		sb.append(id).append(",");
		sb.append(hoTen).append(",");
		sb.append(gioiTinh).append(",");
		sb.append(namSinh).append(",");
		sb.append(sdt).append(",");
		sb.append(diaChi).append(",");
		return sb;
	}
}
