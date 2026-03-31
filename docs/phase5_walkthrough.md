# Phase 5 Walkthrough: Module Bảo hành & Hoàn thiện Hệ thống

Phase cuối cùng của dự án đã hoàn tất thành công, giúp hệ thống hoạt động thuần tuý trên Cơ sở dữ liệu và bổ sung các tính năng nghiệp vụ nâng cao theo yêu cầu.

## Những thay đổi chính

### 1. Chuyển đổi Database Toàn diện (Stage 1)
- **DAO Layer**: Tạo mới `NhanVienDAO`, `KhachHangDAO`, `SanPhamDAO`. Dữ liệu giờ đây được truy vấn trực tiếp từ MySQL thay vì các file `.txt` cũ.
- **Controller Refactoring**: `QuanLyNhanVien`, `QuanLyKhachHang`, và `QuanLySanPham` đã được làm sạch, loại bỏ các luồng File I/O rườm rà.
- **UI Update**: `BangDieuKhienSanPham` được cập nhật để bỏ trường "Số lượng" (quản lý qua Serial) và khớp với Model sản phẩm hiện đại.

### 2. Module Bảo hành Chuyên nghiệp (Stage 2)
- **Model & DAO**: Triển khai `PhieuBaoHanh` và `PhieuBaoHanhDAO`.
- **Công nghệ**: Sử dụng hàm `DATE_ADD` của SQL để tính toán chính xác ngày hết hạn bảo hành ngay khi tra cứu, giúp Back-end tối giản và hiệu quả.
- **Giao diện**: Ra mắt `TiepNhanBaoHanhDialog` (Modal). Nhân viên chỉ cần nhập Serial để biết ngay máy còn hạn hay không và lập phiếu sửa chữa chỉ với vài thao tác.

### 3. Báo cáo Doanh thu & Tích hợp Hệ thống (Stage 3)
- **Thống kê**: Bổ sung hàm `thongKeDoanhThuTheoThang()` trong `HoaDonDAO`, hỗ trợ nhóm dữ liệu theo `Tháng/Năm`.
- **Tab Báo cáo**: Thêm tab "Báo Cáo Doanh Thu" hiển thị bảng tổng hợp doanh thu trực quan.
- **Thanh Menu (JMenuBar)**: Tích hợp thanh menu trên cùng của `MainFrame` để truy cập nhanh các tính năng:
    - `Hệ thống` (Thoát).
    - `Nghiệp vụ` (Tiếp nhận bảo hành).
    - `Báo cáo` (Doanh thu tháng).

## Hướng dẫn Kiểm tra (Verify)

1. **Khởi chạy Hệ thống**: Chạy `MainFrame`.
2. **Kiểm tra Danh mục**: Vào các tab Nhân viên, Khách hàng, Sản phẩm. Dữ liệu sẽ được nạp từ DB (Đảm bảo script `db-design.sql` đã được cập nhật).
3. **Tra cứu Bảo hành**:
    - Vào menu **Nghiệp vụ** -> **Tiếp nhận Bảo hành**.
    - Nhập một số Serial đã bán.
    - Hệ thống sẽ hiển thị Tên SP, Ngày mua và **Hạn bảo hành** (Màu xanh nếu còn hạn).
    - Nhập mô tả lỗi và bấm **Lập Phiếu**.
4. **Xem Báo cáo**:
    - Vào tab **Báo Cáo Doanh Thu** hoặc qua Menu **Báo cáo**.
    - Bấm **Tải lại báo cáo** để xem tổng tiền từ các hoá đơn đã lập.

> [!TIP]
> Hệ thống hiện đã đóng toàn bộ các luồng ghi File. Mọi thay đổi bạn thực hiện trên UI sẽ được lưu trực tiếp vào SQL, đảm bảo tính nhất quán dữ liệu cho doanh nghiệp.

---
**Dự án kết thúc tốt đẹp tại Phase 5.** Chúc mừng bạn đã hoàn thiện hệ thống quản lý cửa hàng máy tính chuyên nghiệp!
