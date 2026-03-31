# Phase 3: Setup JDBC & Refactor Model/DAO

Kế hoạch này đã được cập nhật dựa trên phản biện `phase3_implementation_plan_assessments.md` (bao gồm bổ sung thiết lập JDBC, sửa đổi khóa chính `CHITIETHOADON`, và cập nhật logic tính tổng tiền hóa đơn).

---

## Phase 3.0: Setup JDBC (Nền tảng CSDL)

Do project hiện tại hoàn toàn sử dụng `data/DSSanPham.txt` và chưa có cấu hình CSDL, ta cần thiết lập kết nối JDBC trước tiên. Vì project không dùng Maven (không có `pom.xml`), driver JDBC sẽ cần được tải thủ công và link vào project.

#### [NEW] [DatabaseConnection.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/dao/DatabaseConnection.java)
- Tạo lớp Singleton quản lý `Connection` tới MySQL/SQL Server.
- Cung cấp phương thức `getConnection()`.

#### [ACTION REQUESTED] Tích hợp JDBC Driver
- Người dùng cần tải file MySQL/SQL Server JDBC `.jar` (VD: `mysql-connector-x.x.x.jar`), tạo thư mục `lib/` (nếu chưa có) và nhúng thư viện đó vào cấu hình Project (`File -> Project Structure -> Libraries` trong IntelliJ hoặc Cấu hình build path của eclipse/vscode).

---

## Phase 3.1: Cập nhật Model (`src/model/`)

#### [MODIFY] [SanPham.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/model/SanPham.java)
- Mapping các trường theo CSDL chuẩn: `maSP`, `tenSP`, `loaiSP`, `maNCC`, `giaBan`, `tgBaoHanh`, `trangThai`. 
- Gỡ bỏ hoàn toàn logic tính định dạng "file TXT Siêu thị" cũ (`heSoLoiNhuan()`, `tinhGiaBan()`, `taoDong()`).

#### [MODIFY] [HoaDon.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/model/HoaDon.java)
- Xóa bỏ `List<SanPham>` và logic đọc/ghi cũ.
- Thiết lập POJO thuần mapping cho bảng `HOADON`: `maHD`, `maNV`, `maKH`, `ngayLap`, `tongTien`.

#### [NEW] [SeriSanPham.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/model/SeriSanPham.java)
- POJO ánh xạ `SERISANPHAM`: `maSeri`, `maSP`, `maPN`, `maHD`, `tinhTrang`.

#### [NEW] [ChiTietHoaDon.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/model/ChiTietHoaDon.java)
- Sửa lại theo khóa chính mới (Dựa vào thiết bị bán ra): `maHD`, `maSeri`, `donGiaBan`.
- Thay đổi này phù hợp chính xác với DFD Level 2 để xử lý module bảo hành dựa vào máy sau này (tránh mất vết Serial).

---

## Phase 3.2: Thiết kế DAO (`src/dao/`)

#### [NEW] [HoaDonDAO.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/dao/HoaDonDAO.java)
Sử dụng **JDBC Transaction** (`conn.setAutoCommit(false)`) theo 3 bước:
1. Tính `TongTien = SUM(DonGiaBan)` từ danh sách chi tiết ở Application Layer trước khi Insert hóa đơn (hoặc có thể viết Trigger DB tính tự động, nhưng ở đây sẽ thực hiện tính toán ngay trên code Java trước).
2. **Insert vào HOADON**: Dùng `PreparedStatement` thêm mới hóa đơn.
3. **Insert vào CHITIETHOADON**: Sử dụng vòng lặp các bản ghi `ChiTietHoaDon` (`maHD`, `maSeri`, `donGiaBan`) và chèn từng dòng một. Dùng `executeBatch()` để chèn nhiều dòng tối ưu.
4. **Cập nhật SERISANPHAM**: Cập nhật với điều kiện `MaSeri = ?` bằng vòng lặp lô (`executeBatch()`), gắn thêm `MaHD = ?` và đổi trạng thái `TinhTrang = 'Đã bán'`.
Cuối cùng gọi `conn.commit()`. Nếu bắt `Exception` hoặc `SQLException` thì `conn.rollback()` gỡ bỏ tác động của nửa chừng giao dịch để duy trì tính toàn vẹn dư liệu (Consitency).

## User Review Required

> [!WARNING]
> Bản kế hoạch đã được sửa phù hợp với ý kiến phản biện của bạn, lược bỏ phần gom nhóm tính `số lượng` và áp dụng trực tiếp khóa chính `CHITIETHOADON` theo Serial để tương thích tuyệt đối với Luồng Bảo Hành (Phase 5).
> Vui lòng xác định bạn đang dùng chuẩn MySQL hay SQL Server (hay CSDL khác) để tôi viết Class `DatabaseConnection.java` cho hợp lệ?
