# Báo cáo Audit & Clean-up (Stage 1)

Báo cáo này tập trung vào việc đánh giá cấu trúc tầng truy cập dữ liệu (DAO) và xác định các thành phần mã nguồn cũ cần được loại bỏ hoặc tái cấu trúc để hoàn thiện quá trình migration.

## 1. Danh sách file cần làm sạch (Cleaning List)

Dưới đây là các file chứa logic File I/O cũ hoặc kế thừa không cần thiết từ hệ thống siêu thị cũ:

| File | Tình trạng | Hành động đề xuất |
| :--- | :--- | :--- |
| `dao/LuuTruDuLieu.java` | **Lỗi thời (Obsolete)** | Xóa hoàn toàn. Đây là lớp cơ sở cho File TXT cũ. |
| `controller/QuanLySanPham.java` | **Hybrid** | Loại bỏ `extends LuuTruDuLieu`. Xóa `docFileTXT`, `ghiFileTXT`. |
| `controller/QuanLyNhanVien.java` | **Hybrid** | Tương tự trên. Ngoài ra cần xóa các hàm dùng `Scanner` (CLI legacy).|
| `controller/QuanLyKhachHang.java`| **Hybrid** | Loại bỏ kế thừa và logic file TXT. |
| `controller/QuanLyKho.java` | **Stub** | Loại bỏ kế thừa. Cần viết lại logic nạp kho từ DB thay vì fake bộ nhớ. |

## 2. Đánh giá chất lượng Quản lý Connection

### Điểm mạnh (Strengths)
- **Try-with-resources**: Đa số các DAO (`SanPhamDAO`, `NhanVienDAO`, `KhachHangDAO`, `SeriSanPhamDAO`) đã sử dụng cú pháp `try-with-resources`. Điều này đảm bảo Connection luôn được đóng tự động, ngăn ngừa **Connection Leak**.
- **Transaction Integrity**: `HoaDonDAO` xử lý giao dịch thủ công rất chặt chẽ, có cơ chế `rollback()` khi gặp lỗi SQL, đảm bảo không có hóa đơn "rác" hoặc sai lệch trạng thái Serial.

### Điểm yếu & Rủi ro (Weaknesses & Risks)
- **Tần suất mở kết nối**: Hiện tại mỗi hàm DAO gọi `DatabaseConnection.getConnection()` sẽ mở một kết nối mới tới MySQL. Với ứng dụng Swing nội bộ, điều này chấp nhận được, nhưng nếu có nhiều người dùng đồng thời, nó sẽ gây áp lực lên tài nguyên DB.
    - *Khuyến nghị*: Sử dụng mẫu **Singleton** cho Connection hoặc **Connection Pool** (như HikariCP) nếu cần mở rộng.
- **Verbose Code**: `HoaDonDAO` và `PhieuBaoHanhDAO` vẫn dùng mẫu `try-finally` cũ. Dễ gây sai sót nếu quên đóng một trong các `PreparedStatement` cục bộ.

## 3. Đánh giá cấu trúc DAO (DAO Pattern Audit)

- **Chuẩn hóa**: Các DAO đã tách biệt rõ ràng logic SQL khỏi Model.
- **Tính đa hình**: `NhanVienDAO` đã xử lý nạp các lớp con (`NhanVienBanHang`, `NhanVienThuNgan`, `NhanVienQuanLyKho`) dựa trên cột `ChucVu` trong DB. Đây là một điểm xử lý logic tốt (Factory pattern-like).

---
**Kết luận Stage 1**: Hệ thống có nền tảng DAO tốt nhưng phần Controller vẫn bị "ràng buộc" bởi kiến trúc cũ. Cần thực hiện **Clean-up** ngay để tránh nhầm lẫn logic trong các giai đoạn sau.
