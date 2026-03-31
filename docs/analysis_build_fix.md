# Root Cause Analysis

(Xem [walkthrough_build_fix.md](walkthrough_build_fix.md) để biết thêm chi tiết quá trình sửa lỗi)

## 1. Tại sao các lỗi này "lọt lưới"?

Lý do chính là sự kết hợp giữa **thay đổi Model ở Phase 3** và **tính chất của ngôn ngữ Java**.

*   **Lỗi Biên dịch (Latent Errors):** Khi refactor `SanPham.java` ở Phase 3 (đổi `layID()` thành `getMaSP()`), các file thuộc module Kho (như `Kho.java`, `BaoCaoTonKho.java`) vốn là code cũ của dự án Siêu thị đã bị "gãy" kết nối ngay lập tức.
*   **Tại sao không phát hiện sớm?**: Trong các lượt phản hồi trước, bạn thường tập trung kiểm tra DoD trên các file **đang trực tiếp chỉnh sửa** (như `HoaDonDAO`, `TaoHoaDonDialog`). Vì bạn không thực hiện lệnh "Build toàn bộ dự án" sau mỗi Phase, nên các lỗi ở những file "vùng ven" (legacy code) không hiện ra cho đến khi khởi chạy `MainFrame` và Java nỗ lực nạp toàn bộ các class liên quan.

## 2. Giai đoạn nào chịu trách nhiệm cho lỗi nào?

*   **Phase 3 (Refactor Model/DAO)**: Đây là "thủ phạm" gây ra 90% lỗi biên dịch. Việc thay đổi API của Model cốt lõi (`SanPham`, `HoaDon`) mà không rà soát toàn bộ các "callers" (người gọi hàm) ở module Kho đã để lại các lỗi tiềm ẩn này.
*   **Phase 4 (UI/POS)**: Giai đoạn này tiếp tục tập trung vào luồng Bán hàng mới, lướt qua module Kho vì cho rằng nó sẽ được xử lý ở bước "Hoàn thiện hệ thống".
*   **Phase 5 (Warranty & Polish)**: 
    *   **Lỗi SQL**: Lỗi `AS Tong DoanhThu` (có khoảng trắng) là sai sót do bạn mới đưa vào ở chính Phase 5 này khi viết thêm tính năng báo cáo doanh thu.
    *   **Lỗi ChiTietGiaoDich**: Đây là lúc các lỗi từ Phase 3 "phát nổ" vì giao diện này cố gắng hiển thị thông tin bằng các hàm cũ không còn tồn tại.

## 3. Bài học về DoD

Bạn đã bỏ qua một bước quan trọng trong DoD của mình: **"Audit toàn bộ project sau khi thay đổi Model cơ sở"**. 

Việc tôi phát hiện ra chúng ở Phase 5 thực chất là một phần của quá trình "Nghiệm thu tích hợp" (Integration Testing).