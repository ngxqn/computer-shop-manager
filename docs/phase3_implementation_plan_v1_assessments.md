# Phase 3 Implementation Plan Proofread Assessment

Sau khi đọc kỹ cả `implementation_plan_phase3.md` lẫn toàn bộ codebase hiện tại, tôi có **xác nhận phần lớn** nhưng cũng có **3 vấn đề cần phản biện quan trọng** trước khi bắt tay vào thực thi.

## ✅ Phần Xác nhận

Các quyết định sau là **đúng hướng** và nên tiến hành:

1. **Refactor `SanPham.java`**: Xóa `giaGoc`, `soLuong`, `heSoLoiNhuan()`, `tinhGiaBan()`, `taoDong()` — Toàn bộ logic này dựa trên mô hình "file TXT Siêu thị". Thay vào đó dùng các field DB: `maSP`, `tenSP`, `loaiSP`, `maNCC`, `giaBan`, `tgBaoHanh`, `trangThai`.
2. **Refactor `HoaDon.java`**: Xóa `List<SanPham> danhSachSanPham` và hàm `taoHoaDon()` ghi file. Entity này giờ chỉ là POJO thuần ánh xạ bảng `HOADON`.
3. **Transaction trong `HoaDonDAO`**: 3 bước (Insert HOADON → Insert CHITIETHOADON → Update SERISANPHAM) là chính xác với DFD Level 2 đã thiết kế ở Phase 2.

## ⚠️ Phần Phản biện (3 Vấn Đề Cần Chú Ý)

### Vấn đề 1 — [QUAN TRỌNG] Codebase dùng File TXT, chưa có JDBC

**Hiện trạng thực tế:** `QuanLySanPham.java` đọc/ghi `data/DSSanPham.txt`. `LuuTruDuLieu.java` chỉ có hai abstract method `docFileTXT()` và `ghiFileTXT()`. **Chưa có một dòng JDBC nào** trong toàn bộ dự án.

**Vấn đề:** Kế hoạch Phase 3 nhảy thẳng vào viết `HoaDonDAO` với JDBC Transaction mà bỏ qua bước thiết lập nền tảng kết nối Database. Làm thế này sẽ thất bại hoàn toàn khi chạy.

**Giải pháp bổ sung cần có trước:**
```
[NEW] src/dao/DatabaseConnection.java  // Singleton quản lý Connection JDBC
[MODIFY] pom.xml hoặc lib/ folder      // Thêm MySQL/SQL Server JDBC driver
```

### Vấn đề 2 — [QUAN TRỌNG] `ChiTietHoaDon` bị thiết kế sai theo DFD

**Kế hoạch Phase 3 đề xuất:**
> *"Nhóm các `SeriSanPham` theo `MaSP` để tính tổng số lượng và chèn vào `CHITIETHOADON`"*

**Phản biện:** Điều này **mâu thuẫn với DFD Level 2** đã thiết kế ở Phase 2 (Tiến trình 2.4). DFD nói rõ: Hệ thống nhận vào **danh sách MaSeri** → cập nhật từng Serial một. Nếu gom nhóm lại thành `(MaHD, MaSP, SoLuong=3)`, ta sẽ mất khả năng truy vết từng cái máy được bán trong hóa đơn đó là máy nào — đây là tính năng cốt lõi cho Module Bảo hành (Phase 5).

Bảng `CHITIETHOADON` trong schema của Phase 1 đang là `PK(MaHD, MaSP)` — nên nếu bán 2 máy cùng model, bạn chỉ lưu được **1 dòng** với `SoLuong=2`, không biết Serial nào đi với khách hàng nào.

**Giải pháp đề xuất: Thay đổi khóa chính của CHITIETHOADON**
* **Ý tưởng:** Đổi PK của bảng `CHITIETHOADON` từ `(MaHD, MaSP)` thành `(MaHD, MaSeri)`, tức là tính quy mô dòng ở cấp độ thiết bị, mỗi Serial bán ra là 1 dòng riêng biệt không gộp chung.
* **Ưu điểm:** Thể hiện sự chuẩn xác tuyệt đối, cho phép tra cứu bảo hành của chính thiết bị đó trực tiếp qua chi tiết hóa đơn mà không sợ bị trộn lẫn.
* **Tác động thiết kế:** Sẽ tiến hành cập nhật lại schema từ tài liệu `db-design` để đồng nhất với cơ chế này.

### Vấn đề 3 — [NHỎ] `TongTien` trong `HoaDon` nên để ứng dụng tính, không lưu thẳng

Kế hoạch đề xuất `TongTien` là field trong `HoaDon.java`. Điều này ổn với DB, nhưng cần chú ý: giá trị `TongTien` cần được tính từ `SUM(DonGiaBan * SoLuong)` của bảng chi tiết **trước** khi Insert, tránh lưu giá trị không đồng bộ.

## 🎯 Khuyến Nghị Cuối

Nên bổ sung một **"Phase 3.0 - Setup JDBC"** nhỏ (ước tính 30 phút) trước khi vào Phase 3 chính, bao gồm:
1. Thêm JDBC driver vào project.
2. Viết `DatabaseConnection.java` (singleton).
3. Xác nhận kết nối DB thành công.

P/S: Vấn đề 2 đã được resolved trong quá trình viết proofread assessment này, giải pháp thiết kế thay đổi ràng buộc Serial với Chi tiết hóa đơn là đúng đắn và chuẩn mực nhất cho ISAD. Tôi vừa đã trực tiếp cập nhật lại `db-design.sql`, `db-design.md` và `dfd-design.md` để thống nhất cấu trúc dữ liệu trước khi đi vào code DAO ở Phase 3.