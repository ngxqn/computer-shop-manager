# Kế hoạch Kiểm thử Chuyên sâu (Stage 2)

Giai đoạn này tập trung vào việc xác nhận tính chính xác của các luồng nghiệp vụ cốt lõi sau khi đã migrate sang JDBC, đặc biệt là cơ chế Serial Tracking và Quản lý Giao dịch (Transactions).

## User Review Required

> [!IMPORTANT]
> **Kịch bản Rollback**: Tôi sẽ thực hiện một "Stress/Error Test" bằng cách giả lập lỗi SQL giữa chừng trong quá trình lưu Hóa đơn để xác nhận hệ thống có rollback trạng thái Serial về 'Trong kho' hay không.

> [!TIP]
> **Kiểm thử Biên (Boundary Testing)**: Cần chuẩn bị dữ liệu mẫu trong DB có ngày mua sát với ngày hết hạn (ví dụ: mua 12 tháng trước, hạn 12 tháng) để kiểm tra logic `DATE_ADD` của MySQL.

## Proposed Test Scenarios

### 1. Luồng Bán hàng (POS Flow)
- **TC-POS-01: Quét Serial không tồn tại**: Nhập mã Serial linh tinh vào `TaoHoaDonDialog`. Mong đợi: Thông báo lỗi "Không tìm thấy".
- **TC-POS-02: Bán máy đã bán**: Nhập mã Serial có `TinhTrang = 'Đã bán'`. Mong đợi: Thông báo lỗi "Sản phẩm không ở trạng thái Trong kho".
- **TC-POS-03: Giao dịch thành công**: Thanh toán giỏ hàng 2 sản phẩm. Mong đợi: `HOADON` mới được tạo, `SERISANPHAM` cập nhật trạng thái 'Đã bán' và gán `MaHD`.
- **TC-POS-04: Giao dịch thất bại (Atomic Test)**: Giả lập lỗi khi lưu Chi tiết hóa đơn. Mong đợi: Toàn bộ phiên giao dịch bị hủy, `SERISANPHAM` vẫn giữ nguyên trạng thái 'Trong kho'.

### 2. Luồng Bảo hành (Warranty Flow)
- **TC-WAR-01: Tra cứu máy chưa bán**: Nhập Serial 'Trong kho'. Mong đợi: Hiển thị "Chưa kích hoạt" và khóa nút "Lập phiếu".
- **TC-WAR-02: Tra cứu máy hết hạn**: Nhập Serial đã bán từ 2 năm trước. Mong đợi: Hiển thị "HẾT HẠN BẢO HÀNH" màu đỏ.
- **TC-WAR-03: Lập phiếu bảo hành**: Thực hiện lập phiếu cho máy còn hạn. Mong đợi: `PHIEUBAOHANH` mới xuất hiện, `SERISANPHAM.TinhTrang` chuyển sang 'Đang bảo hành'.

### 3. Luồng Báo cáo (Reporting)
- **TC-REP-01: Thống kê doanh thu**: Kiểm tra `thongKeDoanhThuTheoThang()`. Mong đợi: SQL `GROUP BY` trả về đúng số tiền tổng của các hóa đơn trong tháng.

## Open Questions

- Bạn có dữ liệu mẫu (Dump SQL) để tôi nạp vào kiểm thử các trường hợp "Máy bán từ năm ngoái" không? Nếu không, tôi sẽ tự script tạo dữ liệu mẫu.
- Tôi có nên kiểm tra logic "Đổi trả hàng" (Return Policy) không? (Hiện tại code chưa thấy module này, có thể nằm trong Stage 3).

## Verification Plan

### Manual Verification (Scripted)
1. Sử dụng SQL để chuẩn bị trạng thái dữ liệu (Setup).
2. Thực hiện hành động trên GUI Swing.
3. Sử dụng SQL để kiểm tra kết quả cuối cùng trong Database (Assert).
