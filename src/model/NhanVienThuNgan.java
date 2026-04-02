package model;



public class NhanVienThuNgan extends NhanVien{ // Giữ nguyên tên Interface
	private float doanhThu; // Đổi tên thuộc tính
	
	public float getDoanhThu() { 
		return doanhThu;
	}
	public void setDoanhThu(float doanhThu) { 
		this.doanhThu = doanhThu;
	}
	
	// SỬA: Constructor 11 đối số
	public NhanVienThuNgan(String chucVu, String id, String hoTen, String gioiTinh, String namSinh, String sdt, String diaChi, int soNgayNghi) {
	    super(id, hoTen, gioiTinh, namSinh, sdt, diaChi, soNgayNghi);
	    super.setChucVu("Thu Ngan"); 
	}
	

	    
	public NhanVienThuNgan() {
		super.setChucVu("Thu Ngan"); 
	}

    @Override
    public float tinhLuong() {
        return (LuongCoBan * super.heSoXepLoai());
    }

}
