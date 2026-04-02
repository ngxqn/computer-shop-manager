package model;



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

    public String getMaKH() { return getID(); }
    public void setMaKH(String maKH) { setID(maKH); }

    @Override
    public String toString() {
        return id + " - " + hoTen;
    }
}
