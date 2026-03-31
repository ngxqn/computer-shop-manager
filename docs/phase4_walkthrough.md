# Phase 4 Walkthrough: Refactoring Điểm Bán Hàng (POS)

Quá trình chuyển đổi module Bán Hàng từ việc sử dụng file `.txt` dựa trên khối lượng (số lượng sp) sang mô hình Quản lý qua Database SQL dựa trên dải số Serial đã hoàn tất. Dưới đây là tóm tắt những thay đổi then chốt trong quá trình thực hiện Kế hoạch.

## Những thay đổi đã thực hiện

### 1. Dứt điểm Lỗi Biên Dịch (Controller & Warehouse UI)
Do sự lột xác của `SanPham` và `HoaDon` ở Phase 3, rất nhiều nơi trong ứng dụng cũ báo lỗi. Tôi đã:
- **`QuanLySanPham.java`**: Thay đổi hoặc tạm vô hiệu hóa hoàn toàn logic đọc/ghi text cũ. Chỉnh sửa tất cả các getter/setter để khớp với Model (`getMaSP()`, `getTenSP()` thay cho `layID()`, v.v...).
- **`ThemGiaoDich.java`**: Class này vốn bị lai tạp (xài chung cho Nhập Kho và Lập Hóa Đơn Bán Hàng). Tôi đã thẳng tay gỡ bỏ toàn bộ logic Hóa Đơn ở đây, đưa nó quay về đúng bản chất là **Giao Dịch Kho Nội Bộ** theo thiết kế DFD.
- **`BangDieuKhienGiaoDich.java`**: Chỉnh sửa nhỏ mảng `Object[]` để gọi đúng Getter mới của SanPham, đảm bảo UI tab Kho không bị vỡ.

### 2. Mở rộng Tầng DAO cho Nghiệp Vụ Mới
Để phục vụ giao diện quét mã, tôi đã xây dựng 2 luồng DAO mới:
- **`SeriSanPhamDAO.java`**: Thêm phương thức tra cứu trực tiếp bằng tham số Serial để trả về Tên SP, Đơn giá và Trạng thái (`JOIN` 2 cột của 2 bảng).
- **`HoaDonDAO.java`**: Thêm phương thức `layTatCaHoaDon()` để list toàn bộ lịch sử bán hàng theo chuẩn JDBC thay cho luồng đọc file cũ. Dữ liệu sẽ sort theo ngày Lập từ Mới -> Cũ để tiện quan sát.

### 3. Ra mắt Giao Diện Lập Hóa Đơn Trực Tiếp (POS Dialog Mới)
Thay vì sử dụng chung class cũ, một Class giao diện hoàn toàn mới ra đời: `TaoHoaDonDialog.java`.
- Tích hợp một Textbox chỉ để "Scan Mã Serial" bằng cách bắt sự kiện phím **Enter**.
- Ngay khi Enter, DAO tra cứu thông tin Real-time từ Database:
   - Cảnh báo "Lỗi: Không tìm thấy" nếu Serial sai.
   - Cảnh báo "Lỗi: Đã bán/Bảo hành..." nếu Tình Trạng không phải là "Trong kho".
   - Nếu qua mọi Validation, dòng sản phẩm tự động nhảy vào Giỏ Hàng với Đơn giá chuẩn, đồng thời tự chốt Tổng Tiền hiện lên màu đỏ nổi bật.
- Nút "Thanh Toán" chỉ 1 Cú Click: Dựng `List<ChiTietHoaDon>` và gọi xuống `HoaDonDAO.taoHoaDon(hd, dsChiTiet)`.

### 4. Nâng cấp Tab "Lịch Sử Bán Hàng"
- **`BangDieuKhienHoaDon.java`**: Giờ đây có thêm một nút vinh dự: **"Lập Hóa Đơn Mới"** ở góc trên cùng.
- Khi một hóa đơn được lập thành công và Dialog khép lại, Tab Lịch sử bên ngoài sẽ lập tức gọi Database, Reload lại Grid và đổ dữ liệu hóa đơn mới nhất vừa được lập vào ngay hàng số 1.

## Kiểm Thử & Chạy Thử Tiếp Theo

Để xác nhận luồng chạy này ở máy cá nhân của bạn trên IDE (IntelliJ/Eclipse), hãy làm theo các bước chuẩn bị (Mock DB Data) sau:

1. **Chuẩn bị Dữ liệu Test (Bắt Buộc)**
Do Foreign Key Constraints, hãy nạp một vài dữ liệu đầu vào trong MySQL:
```sql
INSERT INTO NHACUNGCAP (MaNCC, TenNCC, SoDienThoai, Email, DiaChi) 
VALUES ('NCC01', 'Test NCC', '0123456789', 'test@example.com', 'Dia chi');

INSERT INTO SANPHAM(MaSP, TenSP, LoaiSP, MaNCC, GiaBan, TGBaoHanh, TrangThai) 
VALUES ('MAY01', 'DELL XPS 15', 'Laptop', 'NCC01', 10000000, 24, 'Đang kinh doanh');

INSERT INTO SERISANPHAM(MaSeri, MaSP, TinhTrang) 
VALUES ('SR001', 'MAY01', 'Trong kho');
```

2. **Quy Trình Chạy Hệ Thống Bằng IDE**
- Build/Run System, đăng nhập hoặc vào thẳng `MainFrame`.
- Bấm sang Tab "Bán Hàng & Hóa Đơn" -> Click "Lập Hóa Đơn Mới".
- Cắm chuột vào ô Nhập Serial -> Gõ `SR001` -> Nhấn **Enter**. 
- Thấy thông tin DELL chui vào Giỏ hàng -> Click "Thanh Toán".
- Nhận thông báo Thành Công -> Ra ngoài xem Hóa Đơn vừa tạo ở list Lịch Sử.

> [!TIP]
> Bạn có thể trực tiếp mở XAMPP PHPMyAdmin kiểm tra lại hàng `SR001` trong bảng `SERISANPHAM`, chắc chắn rằng cột `TinhTrang` của nó đã tự động bị biến đổi thành **"Đã bán"** và cột `MaHD` đã lưu chuỗi 8 kí tự được tự gen ở hệ thống UI, chứng tỏ luồng Database Transaction đã xử lý trọn vẹn. 
