package model;



public class NhanVienBanHang extends NhanVien  { // Giữ nguyên tên Interface
	
	// SỬA: Constructor 11 đối số
	public NhanVienBanHang (String chucVu, String id, String hoTen, String gioiTinh, String namSinh, String sdt, String diaChi, int soNgayNghi) {
	    super(id, hoTen, gioiTinh, namSinh, sdt, diaChi, soNgayNghi);
	    super.setChucVu("Ban Hang"); 
	}

	public NhanVienBanHang() {
		super.setChucVu("Ban Hang"); 
	}

    @Override
    public float tinhLuong() {
        // Công thức mới: Lương cơ bản nhân hệ số xếp loại (nhân viên full-time)
        return (LuongCoBan * super.heSoXepLoai());
    }
	
}
