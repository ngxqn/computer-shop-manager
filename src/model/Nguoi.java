package model;



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

	
}
