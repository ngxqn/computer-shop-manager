# Kế hoạch Kiểm thử & Audit Hệ thống Quản lý Cửa hàng Máy tính (Refined)

Kế hoạch này được chia thành 3 giai đoạn (Stages) để đảm bảo tính chặt chẽ trong việc nghiệm thu và hoàn thiện hệ thống sau migration.

## Stage 1: Audit DAO & Clean-up Legacy Code
Mục tiêu: Đảm bảo nền tảng JDBC vững chắc và loại bỏ hoàn toàn dấu vết của hệ thống File I/O cũ.

### [DELETE] [LuuTruDuLieu.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/dao/LuuTruDuLieu.java)
### [MODIFY] Các Controller (SanPham, NhanVien, KhachHang, Kho)
- Loại bỏ `extends LuuTruDuLieu`.
- Loại bỏ các phương thức `docFileTXT()` và `ghiFileTXT()`.
- Chuyển đổi logic khởi tạo từ nạp file sang nạp từ DAO.

## Stage 2: Kiểm thử chuyên sâu (Deep Testing)
Mục tiêu: Xác nhận các luồng nghiệp vụ cốt lõi hoạt động đúng với logic Serial Tracking và Transaction.

### Nghiệp vụ POS (Point of Sale)
- Kiểm tra tính nguyên tử (Atomicity) của hóa đơn trong `HoaDonDAO.taoHoaDon()`.
- Kiểm tra giao diện `TaoHoaDonDialog`: Logic chống quét trùng Serial, lọc trạng thái sản phẩm 'Trong kho'.

### Nghiệp vụ Bảo hành (Warranty)
- Kiểm tra tính toán `NgayHetHan` bằng SQL `DATE_ADD` trong `PhieuBaoHanhDAO.traCuuBaoHanh()`.
- Kiểm tra quy trình cập nhật trạng thái `SERISANPHAM` khi lập phiếu bảo hành.

## Stage 3: Xử lý module Hybrid/Stub
Mục tiêu: Chuyển đổi hoàn toàn các module quản lý từ trạng thái Stub sang persistent JDBC.

### Quản lý Kho (Inventory)
- Thay thế logic tính "Số lượng" bằng đếm Serial thực tế từ `SeriSanPhamDAO`.
- Loại bỏ các `Kho` in-memory tạm thời.

### Quản lý Nhân viên & Khách hàng
- Triển khai đầy đủ các phương thức `them/sua/xoa` gọi trực tiếp vào DAO.
- Loại bỏ các logic nhập liệu cũ dùng `Scanner` trong code Controller.

## Verification Plan
### Automated/Code Audit
- Kiểm tra `try-with-resources` trong toàn bộ lớp DAO.
- Xác nhận các kết nối được đóng hoàn toàn sau mỗi giao dịch.

### Manual Verification
- Chạy hệ thống, thực hiện các thao tác quản lý và kiểm tra trực tiếp trong DB (MySQL Workbench/Command Line).
