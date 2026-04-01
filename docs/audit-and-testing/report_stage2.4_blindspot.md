# Báo cáo Kiểm thử Giai đoạn 2 - STEP 4: Điểm mù POS ↔ Module Hybrid

Tôi đã thực hiện kiểm tra các "điểm mù" trong sự tương tác giữa Module Bán hàng (đã JDBC hóa) và các module Nhân viên/Khách hàng (đang trong trạng thái Hybrid).

## 🕵️ KẾT QUẢ AUDIT ĐIỂM MÙ

### 1. Rủi ro Ràng buộc Khóa ngoại (FK Constraint Risk)
Hệ thống POS (`TaoHoaDonDialog`) hiện đang sử dụng các giá trị mặc định được nhập dưới dạng văn bản (Plain Text) vào các ô:
- **MaNV**: Mặc định `"NV01"`
- **MaKH**: Mặc định `"Khách vãng lai"`

**Vấn đề:** Trong Database, bảng `HOADON` có các khóa ngoại trỏ đến `NHANVIEN(MaNV)` và `KHACHHANG(MaKH)`. Nếu người dùng nhấn thanh toán mà các mã này không tồn tại trong DB (do DB rỗng hoặc nhập sai), hệ thống sẽ trả về lỗi `SQLException` từ Driver MySQL.

### 2. Kết quả Kiểm thử Kịch bản "Điểm mù"

| ID | Kịch bản | Kết quả thực tế | Hệ quả |
| :--- | :--- | :--- | :--- |
| **TC-BM-01** | Thanh toán với Mã KH không tồn tại | **FAILED (SQL Error)** | `HoaDonDAO` báo lỗi "Lỗi khi commit...", không cho tạo hóa đơn. Rollback hoạt động tốt nhưng thông báo chưa thân thiện. |
| **TC-BM-02** | Thanh toán khi DB Nhân viên rỗng | **FAILED (FK Error)** | Lỗi xảy ra ngay lập tức vì mã `"NV01"` không được tìm thấy. Người dùng không thể bán hàng nếu chưa tạo nhân viên trước. |

---

## 🛠️ ĐỀ XUẤT KHẮC PHỤC (MITIGATION)

Để hệ thống vận hành trơn tru sau khi migrate, tôi đề xuất 2 hành động:

1.  **Seed Data mặc định**: Luôn tạo sẵn bản ghi `NV01` và khách hàng `"Khách vãng lai"` trong database khi khởi tạo hệ thống (đã bao gồm trong [seed_data_stage2.sql](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/docs/audit-and-testing/seed_data_stage2.sql)).
2.  **Cải thiện Validation**:
    - [ ] Thêm bước kiểm tra sự tồn tại của `MaNV` và `MaKH` trước khi gọi `taoHoaDon()`.
    - [ ] Chuyển các ô nhập `txtNV`, `txtKH` thành `JComboBox` (nạp từ DB) để tránh người dùng nhập sai mã thủ công.

---

## 🏁 TỔNG KẾT STEP 4

Hệ thống đã lộ diện rủi ro về tính phụ thuộc giữa các module. Tuy nhiên, nhờ cơ chế **Transaction Rollback** đã Audit ở STEP 1, tính toàn vẹn dữ liệu vẫn được bảo vệ (không sinh ra hóa đơn rác khi lỗi FK).

**Tôi đã sẵn sàng chuyển sang STEP 5: Tổng kết & Đóng Stage 2. Bạn có đồng ý không?**
