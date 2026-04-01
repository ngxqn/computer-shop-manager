# Stage 2: Kế hoạch Kiểm thử Chuyên sâu — Master Plan (Final)

> Tổng hợp từ: `implementation_plan_stage2a.md` (Test Cases) + `implementation_plan_stage2b.md` (Code Audit & Risk) + Bổ sung kiểm tra "Điểm mù" sau Stage 1.

**Tiền đề**: Stage 1 đã hoàn thành. Toàn bộ tầng Controller đã được làm sạch khỏi kế thừa legacy `LuuTruDuLieu`. Các Controller nạp và làm mới dữ liệu trực tiếp từ JDBC DAO.

---

## 🔴 RỦI RO KỸ THUẬT CẦN THEO DÕI (Risk Inventory)

Các rủi ro này được tổng hợp từ audit code và phải được kiểm chứng trong mỗi bước dưới đây.

| Mã rủi ro | Mức độ | Vị trí | Mô tả |
| :--- | :---: | :--- | :--- |
| `RISK-TX-01` | 🔴 Cao | `HoaDonDAO.taoHoaDon()` | Nếu `psUpdateSeri.executeBatch()` lỗi sau khi HOADON đã được INSERT, rollback phải thu hồi toàn bộ dòng HOADON & CHITIETHOADON để tránh dữ liệu "mồ côi". |
| `RISK-NL-01` | 🟡 Trung bình | `PhieuBaoHanhDAO.traCuuBaoHanh()` | Serial chưa bán có `MaHD = NULL`, câu SQL dùng `LEFT JOIN HOADON ON ss.MaHD = hd.MaHD` nên `NgayLap` và `NgayHetHan` sẽ trả về NULL. Chưa có thông báo lỗi cụ thể ở tầng giao diện. |
| `RISK-POS-01` | 🟡 Trung bình | `TaoHoaDonDialog` → `txtNV` / `txtKH` | Mã NV và KH được nhập thủ công dạng text-field (hardcode "NV01"). Nếu mã này không tồn tại trong DB do NHANVIEN/KHACHHANG rỗng, hóa đơn sẽ thất bại với FK Constraint Error. |
| `RISK-SER-01` | 🟡 Trung bình | `SeriSanPhamDAO.traCuuSerialBanHang()` | Hiện chỉ trả về 4 trường. Không phân biệt giữa Serial 'Đang bảo hành' và 'Lỗi' — cả hai đều bị chặn với cùng thông báo chung. |
| `RISK-CRED-01` | 🟢 Thấp | `DatabaseConnection.java` | Credential DB (`root`, không password) được hardcode trong mã nguồn. Không có file `.env` hoặc cơ chế đọc từ file ngoài. |

---

## STEP 1: Kiểm thử Luồng POS / Bán hàng

**Mục tiêu**: Xác nhận giao dịch bán hàng đảm bảo tính ACID (Atomicity) và logic chặn Serial hoạt động đúng.

**Files liên quan**: `HoaDonDAO.java`, `SeriSanPhamDAO.java`, `TaoHoaDonDialog.java`

**Rủi ro kỹ thuật liên quan**: `RISK-TX-01`, `RISK-POS-01`, `RISK-SER-01`

### 1.1. Kiểm tra Code (Code Audit)

**`HoaDonDAO.taoHoaDon()` — Xác nhận cơ chế Rollback:**
- [x] `conn.setAutoCommit(false)` được gọi trước khi thực hiện bất kỳ thao tác nào.
- [x] Khối `catch` gọi `conn.rollback()` khi có `SQLException`.
- [x] Khối `finally` luôn đặt lại `conn.setAutoCommit(true)` và đóng mọi `PreparedStatement`, `Connection`.
- [ ] **Điểm mù**: Thứ tự thực hiện Batch là `psChiTiet.executeBatch()` trước, `psUpdateSeri.executeBatch()` sau. Nếu bước 2 lỗi, HOADON và CHITIETHOADON đã insert — rollback sẽ đảm bảo xoá sạch tất cả không?

**`TaoHoaDonDialog.xuLyQuetSerial()` — Xác nhận ba lớp phòng thủ:**
- [x] **Lớp 1** (UI): Vòng lặp kiểm tra trùng trong model bảng trước khi gọi DB.
- [x] **Lớp 2** (DB Query): Gọi `seriDAO.traCuuSerialBanHang()` — trả về `null` nếu không tồn tại.
- [x] **Lớp 3** (Business Rule): Kiểm tra `tinhTrang.equalsIgnoreCase("Trong kho")` trước khi thêm vào giỏ.

**`TaoHoaDonDialog.thucHienThanhToan()` — Kiểm tra FK Integrity:**
- [ ] Trường `txtNV` hardcode "NV01" và `txtKH` mặc định "Khách vãng lai". Đây không phải MaKH hợp lệ trong DB → **RISK-POS-01**.

### 1.2. Kịch bản Kiểm thử (Test Cases)

#### **TC-POS-01: Quét Serial không tồn tại**
- **Setup SQL**: *(Không cần — serial giả sẽ không có trong DB)*
- **Hành động**: Mở `TaoHoaDonDialog`, nhập serial `"INVALID-999"`, nhấn Enter.
- **Mong đợi**: Xuất hiện hộp thoại lỗi `"Không tìm thấy mã Serial này"`. Giỏ hàng vẫn rỗng.
- **Liên quan rủi ro**: Không có.

#### **TC-POS-02: Bán máy đã bán (Duplicate Sale)**
- **Setup SQL**:
```sql
-- Đảm bảo có serial này với trạng thái 'Đã bán'
UPDATE SERISANPHAM SET TinhTrang = 'Đã bán' WHERE MaSeri = 'SN_TEST_001';
```
- **Hành động**: Nhập serial `"SN_TEST_001"` vào ô quét.
- **Mong đợi**: Thông báo lỗi `"Sản phẩm không ở trạng thái [Đã bán], không thể bán!"`. Giỏ hàng không thay đổi.
- **Liên quan rủi ro**: `RISK-SER-01` — thông báo sẽ hiển thị trạng thái thực. Tốt, nhưng chưa phân biệt giữa 'Đang bảo hành' và 'Lỗi'.

#### **TC-POS-03: Giao dịch Thành công (Happy Path)**
- **Setup SQL**:
```sql
-- Chuẩn bị 2 serial hợp lệ trong kho
UPDATE SERISANPHAM SET TinhTrang = 'Trong kho', MaHD = NULL WHERE MaSeri IN ('SN_HAPPY_001', 'SN_HAPPY_002');
```
- **Hành động**: Quét 2 serial trên, nhấn "THANH TOÁN".
- **Assert SQL** (sau khi chạy):
```sql
SELECT MaHD FROM HOADON ORDER BY NgayLap DESC LIMIT 1; -- Phải có bản ghi mới
SELECT MaSeri, TinhTrang, MaHD FROM SERISANPHAM WHERE MaSeri IN ('SN_HAPPY_001', 'SN_HAPPY_002');
-- Phải có TinhTrang = 'Đã bán' và MaHD được gán
SELECT * FROM CHITIETHOADON WHERE MaHD = '<MaHD vừa tạo>'; -- Phải có 2 dòng
```
- **Mong đợi**: Thông báo `"Checkout thành công"`, DB trạng thái như Assert SQL mong đợi.

#### **TC-POS-04: Giao dịch Thất bại — Kiểm tra Rollback (Atomic Test)**
- **Phương pháp**: Giả lập lỗi bằng cách thêm tạm thời một ràng buộc DB hoặc sửa mã nguồn tạm thời.
- **Thao tác**: Trong `HoaDonDAO.taoHoaDon()`, thêm dòng `if(true) throw new SQLException("Test rollback");` sau `psChiTiet.executeBatch()`.
- **Assert SQL** (sau khi lỗi xảy ra):
```sql
SELECT COUNT(*) FROM HOADON WHERE MaHD = '<MaHD thử nghiệm>'; -- Phải là 0
SELECT COUNT(*) FROM CHITIETHOADON WHERE  MaHD = '<MaHD thử nghiệm>'; -- Phải là 0
SELECT TinhTrang FROM SERISANPHAM WHERE MaSeri IN ('SN_HAPPY_001', 'SN_HAPPY_002');
-- Phải là 'Trong kho' (đã được rollback)
```
- **Mong đợi**: Không có dữ liệu "mồ côi". SERISANPHAM giữ nguyên trạng thái 'Trong kho'.
- **Liên quan rủi ro**: `RISK-TX-01` — Đây là test case quan trọng nhất để xác nhận toàn vẹn dữ liệu.

#### **TC-POS-05: Điểm mù — FK Constraint fail khi KH/NV không hợp lệ**
- **Setup SQL**:
```sql
-- Xác nhận 'NV01' VÀ 'Khách vãng lai' có tồn tại trong NHANVIEN và KHACHHANG
SELECT * FROM NHANVIEN WHERE MaNV = 'NV01';
SELECT * FROM KHACHHANG WHERE MaKH = 'Khách vãng lai';
```
- **Hành động**: Nếu một trong hai truy vấn trả về rỗng → hệ thống sẽ báo lỗi FK khi thanh toán.
- **Mong đợi** (Rủi ro): Nếu rỗng, thông báo lỗi Java sẽ xuất hiện ở tầng `HoaDonDAO`, không phải thông báo thân thiện với người dùng.
- **Liên quan rủi ro**: `RISK-POS-01`.

---

## STEP 2: Kiểm thử Luồng Bảo hành

**Mục tiêu**: Xác nhận logic tính ngày hết hạn bằng SQL `DATE_ADD` chính xác và quy trình chuyển trạng thái Serial khi lập phiếu đảm bảo tính nguyên tử.

**Files liên quan**: `PhieuBaoHanhDAO.java`, `TiepNhanBaoHanhDialog.java`

**Rủi ro kỹ thuật liên quan**: `RISK-NL-01`

### 2.1. Kiểm tra Code (Code Audit)

**`PhieuBaoHanhDAO.traCuuBaoHanh()` — Phân tích SQL:**
```sql
-- SQL hiện tại
SELECT sp.TenSP, hd.NgayLap,
  DATE_ADD(hd.NgayLap, INTERVAL sp.TGBaoHanh MONTH) AS NgayHetHan,
  ss.TinhTrang, hd.MaKH
FROM SERISANPHAM ss
JOIN SANPHAM sp ON ss.MaSP = sp.MaSP
LEFT JOIN HOADON hd ON ss.MaHD = hd.MaHD
WHERE ss.MaSeri = ?
```
- [x] `LEFT JOIN` đúng kỹ thuật — Serial chưa bán vẫn trả về kết quả với `NgayLap = NULL`.
- [ ] `DATE_ADD(NULL, INTERVAL x MONTH)` → MySQL trả về `NULL`. Tầng View chưa xử lý case `NgayHetHan = null` → **RISK-NL-01**.
- [x] Transaction trong `taoPhieuBaoHanh()` đảm bảo tính nguyên tử: INSERT phiếu + UPDATE serial đều nằm trong cùng một giao dịch.

### 2.2. Kịch bản Kiểm thử (Test Cases)

#### **TC-WAR-01: Tra cứu Serial chưa được bán**
- **Setup SQL**:
```sql
UPDATE SERISANPHAM SET TinhTrang = 'Trong kho', MaHD = NULL WHERE MaSeri = 'SN_UNWRN_001';
```
- **Hành động**: Mở "Tiếp nhận Bảo hành", tra cứu serial `"SN_UNWRN_001"`.
- **Mong đợi**: Giao diện hiển thị trạng thái "Trong kho". Ngày mua / ngày hết hạn hiển thị `N/A` hoặc thông báo "Chưa kích hoạt bảo hành". Nút "Lập phiếu" nên bị vô hiệu hóa.
- **Liên quan rủi ro**: `RISK-NL-01` — Hiện tại `NgayHetHan = null` có thể gây lỗi `NullPointerException` hoặc hiển thị "null".

#### **TC-WAR-02: Tra cứu máy hết hạn bảo hành**
- **Setup SQL** (giả lập mua 2 năm trước, bảo hành 12 tháng):
```sql
-- Cập nhật MaHD của serial để trỏ đến hóa đơn cũ
UPDATE HOADON SET NgayLap = DATE_SUB(NOW(), INTERVAL 24 MONTH) WHERE MaHD = 'HD_TEST_OLD';
UPDATE SERISANPHAM SET TinhTrang = 'Đã bán', MaHD = 'HD_TEST_OLD' WHERE MaSeri = 'SN_EXPIRED_001';
UPDATE SANPHAM SET TGBaoHanh = 12 WHERE MaSP = '<MaSP của serial trên>';
```
- **Hành động**: Tra cứu serial `"SN_EXPIRED_001"`.
- **Assert SQL**:
```sql
SELECT DATE_ADD(NgayLap, INTERVAL 12 MONTH) AS NgayHetHan, NOW() FROM HOADON WHERE MaHD = 'HD_TEST_OLD';
-- NgayHetHan phải nhỏ hơn NOW()
```
- **Mong đợi**: Giao diện hiển thị ngày hết hạn cụ thể và thông báo "HẾT HẠN BẢO HÀNH".

#### **TC-WAR-03: Lập phiếu bảo hành thành công**
- **Setup SQL**:
```sql
UPDATE SERISANPHAM SET TinhTrang = 'Đã bán' WHERE MaSeri = 'SN_VALID_001';
```
- **Hành động**: Tra cứu serial `"SN_VALID_001"` (máy còn hạn), điền đầy đủ thông tin, lập phiếu.
- **Assert SQL**:
```sql
SELECT COUNT(*) FROM PHIEUBAOHANH WHERE MaSeri = 'SN_VALID_001'; -- Phải = 1
SELECT TinhTrang FROM SERISANPHAM WHERE MaSeri = 'SN_VALID_001'; -- Phải = 'Đang bảo hành'
```
- **Mong đợi**: Phiếu bảo hành được tạo, Serial đổi trạng thái trong cùng một giao dịch.

#### **TC-WAR-04: Rollback khi lập phiếu thất bại**
- **Thao tác**: Giả lập lỗi tạm trong `taoPhieuBaoHanh()`: thêm `if(true) throw new SQLException("Test");` sau `psPhieu.executeUpdate()`.
- **Assert SQL**:
```sql
SELECT COUNT(*) FROM PHIEUBAOHANH WHERE MaSeri = 'SN_VALID_001'; -- Phải = 0
SELECT TinhTrang FROM SERISANPHAM WHERE MaSeri = 'SN_VALID_001'; -- Phải là TinhTrang ban đầu, không phải 'Đang bảo hành'
```

---

## STEP 3: Kiểm thử Báo cáo Doanh thu

**Mục tiêu**: Xác nhận SQL `GROUP BY MONTH` và `SUM` trả về kết quả chính xác.

**Files liên quan**: `HoaDonDAO.thongKeDoanhThuTheoThang()`, `BangDieuKhienBaoCao.java`

### 3.1. Kiểm tra Code (Code Audit)

**SQL `thongKeDoanhThuTheoThang()`:**
```sql
SELECT DATE_FORMAT(NgayLap, '%m/%Y') AS Thang, SUM(TongTien) AS TongDoanhThu
FROM HOADON
GROUP BY Thang
ORDER BY MIN(NgayLap) ASC
```
- [x] `DATE_FORMAT` đúng cú pháp MySQL cho định dạng `MM/YYYY`.
- [x] `ORDER BY MIN(NgayLap)` thay vì `ORDER BY Thang` (string) — cần thiết để sort theo thứ tự thời gian thực, không phải alphabet.
- [ ] **Điểm cần lưu ý**: `SUM(TongTien)` tổng hợp từ cột `HOADON.TongTien`, không phải từ `CHITIETHOADON`. Cần đảm bảo `TongTien` được tính đúng khi tạo hóa đơn.

### 3.2. Kịch bản Kiểm thử (Test Cases)

#### **TC-REP-01: Kiểm tra tính chính xác GROUP BY**
- **Setup SQL** (tạo 2 hóa đơn trong tháng hiện tại):
```sql
INSERT INTO HOADON (MaHD, MaNV, MaKH, NgayLap, TongTien) VALUES
  ('HD_REP_01', 'NV01', 'KH01', NOW(), 15000000),
  ('HD_REP_02', 'NV01', 'KH01', NOW(), 25000000);
```
- **Assert SQL** (kiểm tra thủ công):
```sql
SELECT DATE_FORMAT(NOW(), '%m/%Y') AS Thang, SUM(TongTien) FROM HOADON
WHERE DATE_FORMAT(NgayLap, '%m/%Y') = DATE_FORMAT(NOW(), '%m/%Y');
-- Kết quả phải là 40,000,000 VNĐ (= 15M + 25M)
```
- **Hành động**: Mở tab "Báo Cáo Doanh Thu" trong giao diện.
- **Mong đợi**: Tháng hiện tại hiển thị đúng tổng `40,000,000 VNĐ`.

---

## STEP 4: Kiểm tra Điểm mù — Tích hợp POS với Module Hybrid

**Mục tiêu**: Xác nhận liệu luồng POS (đã dùng JDBC hoàn toàn) có bị lỗi khi các module phụ (Khách hàng, Nhân viên) có dữ liệu không hợp lệ trong DB hay không.

**Rủi ro kỹ thuật liên quan**: `RISK-POS-01`

### 4.1. Phân tích tích hợp

| Luồng POS | Phụ thuộc | Trạng thái | Rủi ro |
| :--- | :--- | :--- | :--- |
| `txtNV.getText()` → `MaNV` trong `HOADON` | `NHANVIEN` bảng DB | FK Constraint | Nếu NV không tồn tại → **lỗi silently** |
| `txtKH.getText()` → `MaKH` trong `HOADON` | `KHACHHANG` bảng DB | FK Constraint | "Khách vãng lai" không phải MaKH hợp lệ → **INSERT fails** |

### 4.2. Kịch bản Kiểm thử (Test Cases)

#### **TC-BM-01: POS với Khách vãng lai không hợp lệ**
- **Điều kiện**: `KHACHHANG` không có bản ghi với `MaKH = 'Khách vãng lai'`.
- **Hành động**: Thực hiện thanh toán với giá trị `txtKH = "Khách vãng lai"`.
- **Mong đợi hiện tại**: Thông báo lỗi `"Lỗi khi commit Hóa Đơn vào Database"` (chung chung).
- **Mong đợi lý tưởng**: Thông báo `"Mã khách hàng không tồn tại. Vui lòng kiểm tra lại."`.
- **Hành động cần thiết**: Thêm bản ghi `KH00` (Khách vãng lai) mặc định vào DB seed data, hoặc validate `MaKH` trước khi gọi `taoHoaDon()`.

#### **TC-BM-02: POS với DB Khách hàng và Nhân viên rỗng hoàn toàn**
- **Điều kiện**: Chạy trên DB mới hoàn toàn, không có seed data.
- **Hành động**: Thực hiện toàn bộ luồng thanh toán.
- **Mong đợi**: Hệ thống báo lỗi rõ ràng, không crash `NullPointerException`.

---

## STEP 5: Tổng kết & Báo cáo Rủi ro

Sau khi thực hiện tất cả các Step trên, tổng hợp kết quả vào bảng sau:

| Test Case ID | Phân loại | Kết quả | Rủi ro liên quan | Ghi chú |
| :--- | :--- | :--- | :--- | :--- |
| TC-POS-01 | Happy Path | ⬜ Chưa chạy | — | |
| TC-POS-02 | Negative | ⬜ Chưa chạy | RISK-SER-01 | |
| TC-POS-03 | Happy Path | ⬜ Chưa chạy | — | |
| TC-POS-04 | Atomic Test | ⬜ Chưa chạy | RISK-TX-01 | TC quan trọng nhất |
| TC-POS-05 | Blind Spot | ⬜ Chưa chạy | RISK-POS-01 | |
| TC-WAR-01 | Negative | ⬜ Chưa chạy | RISK-NL-01 | |
| TC-WAR-02 | Boundary | ⬜ Chưa chạy | — | |
| TC-WAR-03 | Happy Path | ⬜ Chưa chạy | — | |
| TC-WAR-04 | Atomic Test | ⬜ Chưa chạy | — | |
| TC-REP-01 | Accuracy | ⬜ Chưa chạy | — | |
| TC-BM-01 | Blind Spot | ⬜ Chưa chạy | RISK-POS-01 | |
| TC-BM-02 | Stress Test | ⬜ Chưa chạy | RISK-POS-01 | |

---

## Tiêu chí Hoàn thành Stage 2 (Definition of Done)

- [ ] Toàn bộ TC-POS-01 đến TC-POS-03 đạt `✅ PASS`.
- [ ] TC-POS-04 (Rollback test) xác nhận không có dữ liệu mồ côi → đóng `RISK-TX-01`.
- [ ] TC-WAR-01 xác nhận giao diện xử lý `NgayHetHan = null` không crash → đóng `RISK-NL-01`.
- [ ] TC-BM-01 xác nhận POS có bản ghi KH/NV mặc định hoặc có validation → đóng `RISK-POS-01`.
- [ ] TC-REP-01 xác nhận `SUM` doanh thu chính xác.
