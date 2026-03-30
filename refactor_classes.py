import os

renames = {
    "QuanLi_KhachHang": "QuanLyKhachHang",
    "QuanLi_Kho": "QuanLyKho",
    "QuanLi_NhanVien": "QuanLyNhanVien",
    "QuanLi_SanPham": "QuanLySanPham",
    "Luu_Tru_Du_Lieu": "LuuTruDuLieu",
    "BangDieuKhien_KhachHang": "BangDieuKhienKhachHang",
    "BangDieuKhien_NhanVien": "BangDieuKhienNhanVien",
    "BangDieuKhien_SanPham": "BangDieuKhienSanPham",
    "BangDieuKhien_HoaDon": "BangDieuKhienHoaDon",
    "BangDieuKhien_GiaoDich": "BangDieuKhienGiaoDich",
    "NhanVien_QuanLiKho": "NhanVienQuanLyKho",
    "NhanVien_BanHang": "NhanVienBanHang",
    "NhanVien_ThuNgan": "NhanVienThuNgan"
}

src_dir = "src"

# Step 1: Replace contents in all java files
for root, dirs, files in os.walk(src_dir):
    for filename in files:
        if filename.endswith(".java"):
            filepath = os.path.join(root, filename)
            with open(filepath, "r", encoding="utf-8") as f:
                content = f.read()
            
            new_content = content
            for old, new in renames.items():
                new_content = new_content.replace(old, new)
                
            if content != new_content:
                with open(filepath, "w", encoding="utf-8") as f:
                    f.write(new_content)
                print(f"Updated content in {filepath}")

# Step 2: Rename files
for root, dirs, files in os.walk(src_dir):
    for filename in files:
        if filename.endswith(".java"):
            base = filename[:-5]
            if base in renames:
                new_base = renames[base]
                old_path = os.path.join(root, filename)
                new_path = os.path.join(root, new_base + ".java")
                os.rename(old_path, new_path)
                print(f"Renamed {old_path} to {new_path}")
