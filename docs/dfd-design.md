# Thiết kế Sơ đồ Luồng Dữ liệu (DFD)

Tài liệu này phân tích chi tiết các Sơ đồ Luồng Dữ liệu (Data Flow Diagram - DFD) dựa trên cấu trúc cơ sở dữ liệu và yêu cầu nghiệp vụ của hệ thống Quản lý Cửa hàng Máy tính. 

Đặc biệt, tài liệu này mô tả cách luồng dữ liệu tương tác chính xác với các Data Store (tương ứng với các bảng trong CSDL): `KHACHHANG`, `NHANVIEN`, `NHACUNGCAP`, `SANPHAM`, `PHIEUNHAP`, `CHITIETPHIEUNHAP`, `HOADON`, `CHITIETHOADON`, `SERISANPHAM`, và `PHIEUBAOHANH`.

---

## 1. DFD Level 0 (Sơ đồ Ngữ cảnh)

Sơ đồ Ngữ cảnh (Context Diagram) cung cấp cái nhìn tổng quan nhất về hệ thống, thể hiện cách hệ thống tương tác với các tác nhân bên ngoài (Entities) nhưng không đi sâu vào chi tiết bên trong.

```mermaid
flowchart TD
    %% Khai báo các External Entities
    KH([Khách hàng])
    NV([Nhân viên])
    NCC([Nhà cung cấp])
    QL([Quản lý])

    %% Khai báo Hệ thống
    HT((0. \nHệ thống Quản lý\nCửa hàng Máy tính))

    %% Luồng dữ liệu giữa Tác nhân và Hệ thống
    KH -->|Yêu cầu mua hàng, Yêu cầu bảo hành, Thanh toán| HT
    HT -->|Hóa đơn mua hàng, Phiếu bảo hành, Xác nhận| KH

    NCC -->|Hàng hóa, Báo giá, Phiếu giao hàng| HT
    HT -->|Yêu cầu nhập hàng, Thanh toán tiền hàng| NCC

    NV -->|Thông tin thao tác, Lập phiếu, Yêu cầu truy xuất| HT
    HT -->|Kết quả thao tác, Biên lai, Cảnh báo| NV

    QL -->|Yêu cầu thống kê, Thay đổi quy định| HT
    HT -->|Báo cáo doanh thu, Tồn kho, Hoạt động| QL
```

---

## 2. DFD Level 1 (Phân rã Hệ thống)

Ở mức độ phân rã đầu tiên, hệ thống được chia thành 4 tiến trình (Process) cốt lõi: Quản lý Nhập kho/Sản phẩm, Quản lý Bán hàng, Quản lý Bảo hành, và Báo cáo thống kê. Tất cả Data Store đều được ánh xạ chính xác bằng tên bảng trong lược đồ dữ liệu (Relational Schema).

```mermaid
flowchart TD
    %% Định dạng style cho các thành phần
    classDef process fill:#e1f5fe,stroke:#03a9f4,stroke-width:2px;
    classDef entity fill:#fff9c4,stroke:#fbc02d,stroke-width:2px;
    classDef store fill:#e8f5e9,stroke:#4caf50,stroke-width:2px;

    %% Entities
    KH([Khách hàng]):::entity
    NV([Nhân viên]):::entity
    NCC([Nhà cung cấp]):::entity
    QL([Quản lý]):::entity

    %% Processes
    P1((1.0 Quản lý Nhập kho\n& Sản phẩm)):::process
    P2((2.0 Quản lý\nBán hàng)):::process
    P3((3.0 Quản lý\nBảo hành)):::process
    P4((4.0 Báo cáo\nthống kê)):::process

    %% Data Stores
    D1[(KHACHHANG)]:::store
    D2[(NHANVIEN)]:::store
    D3[(NHACUNGCAP)]:::store
    D4[(SANPHAM)]:::store
    D5[(PHIEUNHAP)]:::store
    D6[(CHITIETPHIEUNHAP)]:::store
    D7[(HOADON)]:::store
    D8[(CHITIETHOADON)]:::store
    D9[(SERISANPHAM)]:::store
    D10[(PHIEUBAOHANH)]:::store

    %% Luồng dữ liệu Process 1.0 (Nhập kho/Sản phẩm)
    NCC -->|Giao hàng/Thông tin SP| P1
    NV -->|Lập phiếu nhập/Thêm SP| P1
    P1 -->|Lưu TT Nhà cung cấp| D3
    P1 -->|Lưu TT Sản phẩm| D4
    P1 -->|Lưu phiếu nhập| D5
    P1 -->|Lưu chi tiết PN| D6
    P1 -->|Sinh mã máy/vào kho| D9

    %% Luồng dữ liệu Process 2.0 (Bán hàng)
    KH -->|Yêu cầu mua hàng| P2
    NV -->|Tạo hóa đơn, Quét mã| P2
    P2 -->|Lưu/Tìm TT Khách hàng| D1
    P2 -->|Tra cứu SP| D4
    P2 -->|Tra cứu Tồn kho Serial| D9
    P2 -->|Lưu hóa đơn| D7
    P2 -->|Lưu chi tiết HĐ| D8
    P2 -->|Cập nhật Đã bán + MaHD| D9
    P2 -->|Xuất hóa đơn| KH

    %% Luồng dữ liệu Process 3.0 (Bảo hành)
    KH -->|Cung cấp Serial| P3
    P3 -->|Đọc TGBaoHanh của SP| D4
    P3 -->|Đọc Ngày bán qua MaHD| D7
    P3 -->|Lấy MaSP, MaHD| D9
    P3 -->|Lưu Phiếu bảo hành| D10
    P3 -->|Cập nhật trạng thái| D10
    P3 -->|Trả máy bảo hành| KH

    %% Luồng dữ liệu Process 4.0 (Báo cáo)
    QL -->|Tham số báo cáo| P4
    P4 -->|Đọc Phiếu Nhập, Chi Tiết| D5
    P4 -->|Đọc Hóa Đơn, Chi Tiết| D7
    P4 -->|Đọc Tình Trạng Serial| D9
    P4 -->|Đọc Doanh Thu, SP| D4
    P4 -->|Xuất báo cáo| QL
```

---

## 3. DFD Level 2

Phân rã chi tiết cho cấu trúc lõi liên quan đến quản lý Tồn kho vật lý (qua Serial) và Vòng đời sản phẩm (Bảo hành).

### 3.1 DFD Level 2: Quá trình Bán hàng (Tiến trình 2.0)

Quá trình bán hàng không chỉ lưu thông tin tổng quan của Hóa đơn mà còn yêu cầu kiểm tra và cấp phát chính xác các mã định danh phần cứng (Serial/IMEI). Khi chốt đơn, Serial sẽ được chuyển trạng thái nhằm cập nhật kho.

```mermaid
flowchart TD
    classDef process fill:#e1f5fe,stroke:#03a9f4,stroke-width:2px;
    classDef entity fill:#fff9c4,stroke:#fbc02d,stroke-width:2px;
    classDef store fill:#e8f5e9,stroke:#4caf50,stroke-width:2px;

    KH([Khách hàng]):::entity
    NV([Nhân viên]):::entity

    %% Processes Level 2
    P21((2.1 Tiếp nhận\nyêu cầu mua)):::process
    P22((2.2 Kiểm tra SP &\nTồn kho Serial)):::process
    P23((2.3 Lập Hóa đơn\nTổng quát)):::process
    P24((2.4 Lưu Chi tiết HĐ\n& Cập nhật Serial)):::process
    P25((2.5 Xác nhận\nThanh toán)):::process

    %% Data Stores
    D1[(KHACHHANG)]:::store
    D4[(SANPHAM)]:::store
    D7[(HOADON)]:::store
    D8[(CHITIETHOADON)]:::store
    D9[(SERISANPHAM)]:::store

    KH -->|Thông tin mặt hàng cần mua| P21
    P21 -->|Loại SP, Tên SP| P22
    
    %% Kiểm tra SP & Serial tồn kho
    P22 -->|Đọc MaSP, GiaBan| D4
    P22 -->|Kiểm tra TinhTrang='Tồn kho'| D9
    P22 -->|Phản hồi Tồn kho, Chọn đúng Seri| P23
    
    %% Thiết lập hóa đơn
    NV -->|Tạo Hóa đơn mới| P23
    P23 <-->|Tra cứu/Lưu Khách hàng| D1
    P23 -->|"Lưu HOADON (Mã, Ngày, Tổng tiền)"| D7
    P23 -->|Truyền MaHD, Danh sách MaSeri, DonGia| P24
    
    %% Cập nhật Serial & Chi tiết HĐ
    P24 -->|"Lưu CHITIETHOADON (MaHD, MaSeri, DonGiaBan)"| D8
    P24 -->|"Cập nhật SERISANPHAM (TinhTrang='Đã bán', MaHD)"| D9
    
    %% Hoàn tất
    P24 -->|Xác nhận Chi tiết HĐ thành công| P25
    P25 -->|Giao hàng, In Hóa đơn| KH
```

### 3.2 DFD Level 2: Quá trình Bảo hành (Tiến trình 3.0)

Khi Khách hàng mang máy đã mua đến bảo hành. Hệ thống không dựa trên hóa đơn giấy (người dùng làm mất) mà dựa trực tiếp vào Serial ghi nhận trên phần cứng, sau đó lấy thông số cấu hình thời gian bảo hành, đối chiếu ngày mua để xác định hợp lệ.

```mermaid
flowchart TD
    classDef process fill:#e1f5fe,stroke:#03a9f4,stroke-width:2px;
    classDef entity fill:#fff9c4,stroke:#fbc02d,stroke-width:2px;
    classDef store fill:#e8f5e9,stroke:#4caf50,stroke-width:2px;

    KH([Khách hàng]):::entity
    NV([Nhân viên kỹ thuật]):::entity

    %% Processes Level 2
    P31((3.1 Tiếp nhận\nmáy & Serial)):::process
    P32((3.2 Kiểm tra\nhạn bảo hành)):::process
    P33((3.3 Lập phiếu\nbảo hành)):::process
    P34((3.4 Cập nhật\ntình trạng xử lý)):::process

    %% Data Stores
    D1[(KHACHHANG)]:::store
    D4[(SANPHAM)]:::store
    D7[(HOADON)]:::store
    D9[(SERISANPHAM)]:::store
    D10[(PHIEUBAOHANH)]:::store

    KH -->|Cung cấp Máy bị lỗi + Serial| P31
    P31 -->|Truyền MaSeri| P32
    
    %% Quá trình móc nối dữ liệu kiểm tra thời hạn
    P32 -->|1. Lấy MaSP, MaHD theo MaSeri| D9
    P32 -->|2. Lấy TGBaoHanh theo MaSP| D4
    P32 -->|"3. Lấy NgayLap(Ngày mua) theo MaHD"| D7
    %% Logic: Ngày Hiện Tại <= NgayLap + TGBaoHanh(tháng)
    
    P32 -->|"Xác nhận Hợp lệ (Trong hạn bảo hành)"| P33
    
    %% Lập phiếu bảo hành thực tế
    P33 <-->|Truy xuất/Cập nhật TT Khách hàng| D1
    P33 -->|"Lưu PHIEUBAOHANH (MoTaLoi, NgayTiepNhan...)"| D10
    P33 -->|Trả Biên nhận bảo hành| KH
    
    %% Chuyên viên NV sửa máy và update tình trạng
    NV -->|Khắc phục lỗi, Sửa chữa| P34
    P34 -->|"Cập nhật PHIEUBAOHANH (TinhTrang='Đã hoàn thành', ChiPhi)"| D10
    P34 -->|Trả máy xử lý xong| KH
```
