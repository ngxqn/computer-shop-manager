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
	public String getID() { 
		return id;
	}
	public String getHoTen() { 
		return hoTen;
	}
	public String getGioiTinh() { 
		return gioiTinh;
	}
	public String getNamSinh() { 
		return namSinh;
	}
	public String getSdt() { 
		return sdt;
	}
	public String getDiaChi() { 
		return diaChi;
	}
	
	// --- SETTERS ---
	public void setID(String id) { 
		this.id = id;
	}
	public void setHoTen(String hoTen) { 
		this.hoTen = hoTen;
	}
	public void setGioiTinh(String gioiTinh) { 
		this.gioiTinh = gioiTinh;
	}
	public void setNamSinh(String namSinh) { 
		this.namSinh = namSinh;
	}
	public void setSdt(String sdt) { 
		this.sdt = sdt;
	}
	public void setDiaChi(String diaChi) { 
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
		this.setHoTen(sc.nextLine()); 
		System.out.println("Moi nhap gioi tinh (Nam/Nu):");
		this.setGioiTinh(sc.nextLine()); 
		System.out.println("Moi nhap nam sinh (VD: 1990):");
		this.setNamSinh(sc.nextLine()); 
		System.out.println("Moi nhap SDT:");
		this.setSdt(sc.nextLine()); 
		System.out.println("Moi nhap dia chi:");
		this.setDiaChi(sc.nextLine()); 
		} catch (Exception e) {
			System.out.println("Loi khi nhap Nguoi: " + e.getMessage());
		}
	}
	
	public void xuat() {
		System.out.printf("| %-10s | %-25s | %-10s | %-10s | %-15s | %-30s |", id, hoTen, gioiTinh, namSinh, sdt, diaChi); // Đổi tên thuộc tính
	}

	// --- PHƯƠNG THỨC HỖ TRỢ GHI FILE (cho lớp con) ---
	public StringBuilder getBaseDataString() { 
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
