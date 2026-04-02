package model;



public class NhanVienQuanLyKho extends NhanVien {
	
	// SỬA: Constructor 11 đối số
	public NhanVienQuanLyKho (String chucVu, String id, String hoTen, String gioiTinh, String namSinh, String sdt, String diaChi, int soNgayNghi) {
	    super(id,hoTen,gioiTinh,namSinh,sdt,diaChi,soNgayNghi);
	    super.setChucVu("Quan Li Kho");
	}

	public NhanVienQuanLyKho() {
		super.setChucVu("Quan Li Kho"); 
	}

    @Override
    public float tinhLuong() {
        // Quản lý kho thường có thêm phụ cấp trách nhiệm
        return (LuongCoBan * super.heSoXepLoai() + 1000000);
    }
}
