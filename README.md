# Ứng Dụng Quản Lý Siêu Thị Mini (Java Swing)

## Mô Tả Dự Án
Đây là ứng dụng Desktop giúp quản lý hàng hóa, bán hàng và theo dõi kho cho mô hình siêu thị mini hoặc cửa hàng tiện lợi. 
Dự án được xây dựng dựa trên cốt lõi là **Lập trình Hướng đối tượng (OOP)**, áp dụng mô hình **MVC (Model - View - Controller)** kết hợp lưu trữ dữ liệu thông qua các tệp văn bản (Text File).

Ứng dụng chính thức chạy bằng giao diện Swing (Desktop App).

## Tính Năng Chính
- **Quản Lý Bán Hàng & Hóa Đơn**: Tạo hóa đơn bán hàng, tính tổng tiền, lưu trữ lịch sử giao dịch.
- **Quản Lý Kho & Sản Phẩm**: Thêm, sửa, xóa, tìm kiếm sản phẩm. Tự động kiểm tra và cập nhật thiết lập số lượng hàng trong kho sau khi có giao dịch hoặc nhập xuất.
- **Quản Lý Khách Hàng**: Lưu trữ thông tin khách hàng, đánh giá và phân loại (thân thiết).
- **Quản Lý Nhân Sự**: Lưu trữ nhân viên theo phân quyền (Thu ngân, Bán hàng, Kho...), tự động tính lương theo số ngày nghỉ và ca làm việc.

## Kiến Trúc Dự Án (MVC)
Dự án được tổ chức theo cấu trúc rõ ràng trong thư mục `src/`:
- **`model` (`modal`)**: Chứa các lớp thực thể (Entity) được mô hình hóa từ thực tế (`SanPham`, `HoaDon`, `NhanVien`, `KhachHang`, ...). Thể hiện đầy đủ tính Đóng gói, Kế thừa, Đa hình và Trừu tượng.
- **`view`**: Chứa toàn bộ giao diện người dùng Desktop bằng Java Swing. Giao diện được thiết kế dạng Tab (`JTabbedPane`) tại `MainFrame.java`, chia thành các màn hình `BangDieuKhien_...` độc lập.
- **`controller`**: Chứa các lớp nghiệp vụ (`QuanLi_SanPham`, `QuanLi_NhanVien`, ...) chịu trách nhiệm điều phối xử lý dữ liệu giữa `model` và `view`, đồng thời thao tác đọc/ghi với file hệ thống.
- **`data`**: Chứa các file `.txt` đóng vai trò thay thế cơ sở dữ liệu (`DSSanPham.txt`, `DSNhanVien.txt`, `DSHoaDon.txt`, ...).

## Công Nghệ Sử Dụng
- **Ngôn ngữ cốt lõi**: Java (JDK 11+)
- **Giao diện người dùng**: Java Swing (áp dụng theme Nimbus cho giao diện hiện đại hơn).
- **Lưu trữ dữ liệu**: File I/O (Sử dụng `BufferedReader`, `BufferedWriter` với Text File).
- **Mô hình lập trình**: OOP (Hướng đối tượng) & MVC.

## Cài Đặt và Chạy Ứng Dụng
1. **Yêu cầu hệ thống**: Máy tính cần cài đặt sẵn Java JDK phiên bản 11 trở lên. Có thể dùng IDE như IntelliJ IDEA, Eclipse hoặc VS Code (với extension cho Java).
2. **Khởi chạy**: 
   - Mở dự án bằng IDE của bạn.
   - Tìm file tĩnh `MainFrame.java` nằm trong package `src/view`.
   - Chạy hàm `public static void main(String[] args)` bên trong `MainFrame` để khởi động ứng dụng.
3. **Dữ liệu**: Dữ liệu có sẵn trong thư mục `data/` sẽ tự động được ứng dụng tải khi bắt đầu và ghi đè khi có sự thay đổi (Ví dụ: Thêm mới sản phẩm).