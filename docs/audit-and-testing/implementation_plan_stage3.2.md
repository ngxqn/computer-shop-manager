# Implementation Plan - STEP 2: Module Kho & Nhập hàng hàng loạt

Giai đoạn này tập trung vào việc chuyển đổi hoàn toàn module Kho từ quản lý số lượng ảo (in-memory) sang quản lý số lượng thực tế dựa trên bảng `SERISANPHAM` và triển khai luồng nhập hàng hàng loạt theo số lượng Serial.

## User Review Required

> [!IMPORTANT]
> **Thay đổi luồng nhập hàng**: Thay vì nhập ID sản phẩm + Số lượng (Enter Quantity), người dùng sẽ dán danh sách mã Serial (Batch Serials) vào vùng văn bản. Hệ thống sẽ tự động đếm số lượng dựa trên số dòng Serial hợp lệ.
> **Tính toán tồn kho**: Số lượng tồn kho trên bảng 'Giao dịch' sẽ hiển thị giá trị thực tế `COUNT(*)` từ bảng `SERISANPHAM` thay vì số 0 hoặc số giả lập.

## Proposed Changes

### 1. DAO Layer (Data Access)

#### [MODIFY] [SeriSanPhamDAO.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/dao/SeriSanPhamDAO.java)
- Thêm phương thức `demTonKho(String maSP)`: Đếm số lượng Serial có tình trạng 'Trong kho' của một sản phẩm.
- Thêm phương thức `kiemTraTonTai(String maSeri)`: Tránh nhập trùng mã Serial đã có trong hệ thống.

#### [NEW] [PhieuNhapDAO.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/dao/PhieuNhapDAO.java)
- Phương thức `taoPhieuNhap(PhieuNhapKho pn, List<String> danhSachSeri, String maSP, double giaNhap)`: 
    - Thực hiện trong 1 **Transaction**.
    - Insert vào `PHIEUNHAP`.
    - Insert vào `CHITIETPHIEUNHAP`.
    - Insert hàng loạt vào `SERISANPHAM` (Batch Insert).

---

### 2. Controller Layer (Logic)

#### [MODIFY] [QuanLyKho.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/controller/QuanLyKho.java)
- Xóa bỏ `List<Kho> danhSachKho` và cơ chế nạp dữ liệu từ hệ thống (hybrid).
- Thêm phương thức `layTonKhoThucTe(String maSP)` gọi `SeriSanPhamDAO`.
- Thêm phương thức `nhapHangMoi(...)` để kết nối UI với `PhieuNhapDAO`.

---

### 3. View Layer (UI)

#### [MODIFY] [BangDieuKhienGiaoDich.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/view/BangDieuKhienGiaoDich.java)
- Cập nhật hàm `taiDuLieuVaoBang()` để lấy số lượng tồn kho thực tế từ Controller/DAO.

#### [MODIFY] [ThemGiaoDich.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/view/ThemGiaoDich.java)
- Đổi tiêu đề thành "NHẬP HÀNG MỚI (PHÂN LOẠI SERIAL)".
- Thay thế ô nhập "Số lượng" bằng `JTextArea` để người dùng dán hàng loạt Serial (mỗi dòng 1 mã).
- Thêm ComboBox chọn Nhà Cung Cấp (NCC).
- Logic `Xác nhận`: Kiểm tra tính hợp lệ của Serial (không trống, không trùng) trước khi thực hiện Transaction.

## Open Questions

- Hiện tại hệ thống chưa có `NhacCungCapDAO`. Tôi nên tạo một DAO mới hay tạm thời code cứng danh sách NCC trong `ThemGiaoDich`?
- Bạn có muốn giới hạn số lượng Serial tối đa trong 1 lần nhập không (ví dụ 100 cái)?

## Verification Plan

### Automated Tests
- Kiểm tra `PhieuNhapDAO`: Nhập 5 Serial cho 1 sản phẩm, kiểm tra bảng `SERISANPHAM` có 5 dòng mới và `PHIEUNHAP` có 1 dòng mới.
- Kiểm tra Transaction: Nếu 1 Serial bị trùng, toàn bộ Phiếu nhập phải bị rollback.

### Manual Verification
- Mở bảng Giao dịch, thực hiện "Nhập hàng", dán 10 Serial. Sau đó kiểm tra cột "Tồn kho" có nhảy lên 10 không.
