# Kế hoạch Thực hiện Stage 2: Kiểm thử Chuyên sâu (Deep Testing)

Giai đoạn này tập trung vào việc xác nhận các nghiệp vụ cốt lõi của hệ thống thông qua việc kiểm tra mã nguồn (Audit) và thực hiện các kịch bản kiểm thử thực tế trên Cơ sở dữ liệu.

## User Review Required

> [!IMPORTANT]
> **Rủi ro logic bảo hành**: Trong `PhieuBaoHanhDAO.traCuuBaoHanh()`, nếu một Serial chưa được bán (`MaHD` là NULL), câu lệnh SQL hiện tại sẽ trả về `NgayLap` và `NgayHetHan` là NULL. Hệ thống cần có thông báo lỗi cụ thể cho trường hợp này thay vì để trống.

> [!WARNING]
> **Transaction Rollback**: Cần xác nhận rằng khi `psUpdateSeri.executeBatch()` lỗi, toàn bộ `HOADON` đã insert trước đó phải được thu hồi hoàn toàn.

## Proposed Changes

Tôi sẽ triển khai các kịch bản kiểm thử chi tiết và thực hiện kiểm tra tại các file sau:

### 1. Kiểm thử Nghiệp vụ Bán hàng (POS Flow)
#### [AUDIT] [HoaDonDAO.java]
- **Kịch bản Happy Path**: Bán thành công 1 hóa đơn có 2-3 sản phẩm. Xác nhận:
    - Bảng `HOADON` có bản ghi mới.
    - Bảng `CHITIETHOADON` có đủ các dòng serial.
    - Bảng `SERISANPHAM` chuyển trạng thái sang 'Đã bán' và cập nhật `MaHD`.
- **Kịch bản Rollback**: Giả lập lỗi ở bước cập nhật trạng thái Serial (ví dụ: Serial bị khóa). Xác nhận không có dữ liệu "mồ côi" trong `HOADON`.

#### [AUDIT] [TaoHoaDonDialog.java]
- Kiểm tra logic `xuLyQuetSerial`:
    - Chặn Serial không tồn tại.
    - Chặn Serial đã bán (Trạng thái != 'Trong kho').
    - Chặn quét trùng mã Serial trong cùng một giỏ hàng.

### 2. Kiểm thử Nghiệp vụ Bảo hành (Warranty Logic)
#### [AUDIT] [PhieuBaoHanhDAO.java]
- **Kịch bản Tra cứu**:
    - Serial hợp lệ (đã bán, còn hạn).
    - Serial hết hạn bảo hành (Kiểm tra `NgayHetHan < CURRENT_DATE`).
    - Serial chưa bán (Tra cứu trả về NULL hoặc lỗi logic JOIN).
- **Kịch bản Lập phiếu**:
    - Xác nhận Serial chuyển từ 'Đã bán' -> 'Đang bảo hành'.
    - Xác nhận Transaction bảo vệ cả việc insert phiếu và update serial.

### 3. Kiểm thử Báo cáo & Thống kê
- Kiểm tra tính chính xác của hàm `thongKeDoanhThuTheoThang()` bằng cách so sánh SQL `SUM` với tổng thủ công các hóa đơn trong tháng đó.

## Open Questions
- Bạn có muốn tôi viết các hàm Test (JUnit hoặc Main test) để tự động hóa việc kiểm tra Transaction không, hay thực hiện kiểm tra thủ công (Manual Check) trên DB?
- Có cần kiểm tra trường hợp Serial ở trạng thái 'Lỗi' hoặc 'Đang bảo hành' mà khách vẫn cố tình đem đi bán không?

## Verification Plan
### Manual Verification
- Sử dụng Database Tool (như MySQL Workbench) để kiểm tra trạng thái dòng dữ liệu trước và sau mỗi test case.
- Chạy giao diện `MainFrame` -> `Chọn Bán hàng` -> `Thanh toán` và quan sát log/thông báo lỗi.
