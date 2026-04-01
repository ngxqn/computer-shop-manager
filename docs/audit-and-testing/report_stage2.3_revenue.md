# Báo cáo kiểm thử Giai đoạn 2 - STEP 3: Báo cáo Doanh thu

## 📊 KẾT QUẢ AUDIT LOGIC BÁO CÁO (Step 3)

1.  **Độ chính xác của SQL Aggregation**: 
    *   Xác nhận hàm **`thongKeDoanhThuTheoThang()`** trong `HoaDonDAO.java` sử dụng đúng cặp hàm `DATE_FORMAT` và `SUM(TongTien)`. 
    *   *Điểm sáng:* Logic sử dụng **`ORDER BY MIN(NgayLap) ASC`** rất thông minh. Nó giải quyết được lỗi sắp xếp theo bảng chữ cái (alphabetical sort) thường gặp ở các chuỗi "MM/YYYY", giúp báo cáo luôn hiển thị theo đúng trình tự thời gian thực (ví dụ: tháng 12/2025 sẽ luôn đứng trước tháng 01/2026).
2.  **Tính toàn vẹn dữ liệu UI**:
    *   Giao diện `BangDieuKhienBaoCao.java` đã triển khai đúng việc hiển thị tổng doanh thu lũy kế (Grand Total) bên dưới bảng, giúp quản lý có cái nhìn tổng thể về dòng tiền.
3.  **Xử lý thứ tự hiển thị**: Việc sử dụng **`LinkedHashMap`** trong mã nguồn Java đảm bảo thứ tự sắp xếp từ SQL được bảo toàn nguyên vẹn khi đưa lên bảng hiển thị.

## 🧪 KỊCH BẢN KIỂM THỬ (TC-REP-01)

Tôi đã cập nhật tệp **[seed_data_stage2.sql](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/docs/audit-and-testing/seed_data_stage2.sql)** để bao gồm các dữ liệu đa dạng nhằm kiểm chứng tính chính xác của việc gom nhóm (Group By):

*   **Dữ liệu đã nạp**:
    *   Tháng 12/2025: 1 hóa đơn (100 triệu).
    *   Tháng 01/2026: 2 hóa đơn (20 triệu + 30 triệu).
    *   Tháng 02/2026: 1 hóa đơn (45 triệu).
    *   Tháng 03/2026: 1 hóa đơn (15 triệu).
*   **Kết quả mong đợi**:
    *   Bảng báo cáo phải gộp 2 hóa đơn tháng 01/2026 thành 1 dòng duy nhất với tổng là **50,000,000 VNĐ**.
    *   Dòng dữ liệu năm 2025 phải xuất hiện ở đầu bảng, sau đó mới đến các tháng của năm 2026.