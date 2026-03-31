# Refactor Model & DAO cho Cửa hàng Máy tính

Kế hoạch này sẽ cập nhật các class Model (`SanPham`, `HoaDon`), tạo thêm `SeriSanPham`, `ChiTietHoaDon` để khớp với Lược đồ CSDL mới, và viết lớp `HoaDonDAO` xử lý Transaction cho quy trình tạo hóa đơn.

## Các thay đổi đề xuất

### 1. Cập nhật Model (`src/model/`)

#### [MODIFY] [SanPham.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/model/SanPham.java)
Cập nhật các thuộc tính khớp với DB schema: `MaSP`, `TenSP`, `LoaiSP`, `MaNCC`, `GiaBan`, `TGBaoHanh`, `TrangThai`. Xóa các hàm tính giá cũ vì schema đã lưu trực tiếp `GiaBan`.

#### [MODIFY] [HoaDon.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/model/HoaDon.java)
Cập nhật thuộc tính: `MaHD`, `MaNV`, `MaKH`, `NgayLap`, `TongTien`. 
Xóa bỏ cách lưu trữ cũ `List<SanPham>`, thay vào đó sẽ quản lý qua các Entity riêng ở cấp độ DAO/DB.

#### [NEW] [SeriSanPham.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/model/SeriSanPham.java)
Tạo Java Bean với thuộc tính: `MaSeri`, `MaSP`, `MaPN`, `MaHD`, `TinhTrang`.

#### [NEW] [ChiTietHoaDon.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/model/ChiTietHoaDon.java)
Tạo Java Bean với thuộc tính: `MaHD`, `MaSP`, `SoLuong`, `DonGiaBan`.

---

### 2. Thiết kế DAO (`src/dao/`)

#### [NEW] [HoaDonDAO.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/dao/HoaDonDAO.java)
Tạo class `HoaDonDAO` chứa hàm `taoHoaDon(HoaDon hd, List<SeriSanPham> dsSeri)`.
Sử dụng **JDBC Transaction** (`conn.setAutoCommit(false)`) theo 3 bước:
1. **Insert vào HOADON**: Dùng `PreparedStatement` thêm mới `HoaDon` (Lấy `MaHD`).
2. **Insert vào CHITIETHOADON**: Nhóm các `SeriSanPham` theo `MaSP` để tính tổng số lượng (`SoLuong`) và chèn vào `CHITIETHOADON`.
3. **Cập nhật SERISANPHAM**: Chạy lệnh UPDATE bảng `SERISANPHAM` gán `MaHD = ?` và `TinhTrang = 'Đã bán'` theo danh sách `MaSeri`.
Cuối cùng gọi `conn.commit()`. Nếu lỗi thì `conn.rollback()`.

## User Review Required

> [!WARNING]
> Việc cập nhật Model `SanPham` và `HoaDon` sẽ làm hỏng các class phụ thuộc cũ. Bạn có muốn cập nhật tất cả code liên quan không, hay tôi chỉ tập trung vào refactor độc lập theo đúng chỉ thị trong Model và tạo `HoaDonDAO`? 
> Ngoài ra, hàm `taoHoaDon()` sẽ nhận vào danh sách đối tượng `SeriSanPham` để bóc tách thông tin lưu vào DB. Thiết kế này có hợp lý với luồng hoạt động mà bạn đang nhắm tới không?

## Verification Plan
1. Viết mã thành công, không bị lỗi cú pháp học.
2. Kiểm tra JDBC logic trong `HoaDonDAO` đảm bảo tuân thủ tiêu chuẩn ACID và dùng đúng preparestatement để tránh lỗi bảo mật.
