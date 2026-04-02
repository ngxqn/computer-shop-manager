-- CHUẨN BỊ DỮ LIỆU KIỂM THỬ STAGE 3 (Dành cho JDBC & Login)
USE CuaHangMayTinh;

-- 1. Xóa dữ liệu cũ theo thứ tự ràng buộc FK (Phụ thuộc vào các bảng liên quan)
DELETE FROM PHIEUBAOHANH;
DELETE FROM CHITIETHOADON;
DELETE FROM SERISANPHAM;
DELETE FROM HOADON;
DELETE FROM CHITIETPHIEUNHAP;
DELETE FROM PHIEUNHAP;
DELETE FROM SANPHAM;
DELETE FROM NHACUNGCAP;
DELETE FROM KHACHHANG;
DELETE FROM NHANVIEN;

-- 2. Seed NHANVIEN (Bổ sung MatKhau cho Giai đoạn 3)
-- Mật khẩu mặc định là '123' cho tất cả tài khoản test
INSERT INTO NHANVIEN (MaNV, HoTen, GioiTinh, NamSinh, SDT, DiaChi, ChucVu, MatKhau) VALUES
('NV01', 'Nguyễn Văn Admin', 'Nam', '1990', '0901234567', 'Hà Nội', 'Quản lý', '123'),
('NV02', 'Lê Thị Thu Ngân', 'Nữ', '1995', '0907654321', 'TP.HCM', 'Thu Ngan', '123'),
('NV03', 'Trần Văn Kho', 'Nam', '1992', '0988001122', 'Đà Nẵng', 'Quản lý Kho', '123');

-- 3. Seed KHACHHANG (Sử dụng cho ComboBox trong POS)
INSERT INTO KHACHHANG (MaKH, HoTen, GioiTinh, NamSinh, SDT, DiaChi) VALUES
('KH_VLAN', 'Khách vãng lai', 'Nam', '1990', '0000000000', 'Địa chỉ mặc định'),
('KH001', 'Nguyễn Anh Tuấn', 'Nam', '1985', '0912123456', 'Quận 1, HCM'),
('KH002', 'Lê Minh Tâm', 'Nữ', '1993', '0913987654', 'Quận 3, HCM');

-- 4. Seed NHACUNGCAP & SANPHAM (Chỉ danh mục SP, chưa có Serial trong kho)
INSERT INTO NHACUNGCAP (MaNCC, TenNCC, DiaChi, SDT) VALUES
('NCC01', 'Dell Global Corp', 'USA', '123-456-789');

INSERT INTO SANPHAM (MaSP, TenSP, LoaiSP, MaNCC, GiaBan, TGBaoHanh, TrangThai) VALUES
('LAP-DELL-XPS', 'Dell XPS 13 9310', 'Laptop', 'NCC01', 25000000.00, 12, 'Đang kinh doanh'),
('LAP-MAC-M1', 'MacBook Air M1', 'Laptop', 'NCC01', 19000000.00, 12, 'Đang kinh doanh'),
('RAM-16G-K', 'RAM Kingston 16GB', 'Linh kiện', 'NCC01', 1500000.00, 36, 'Đang kinh doanh');

-- 5. Seed SERISANPHAM (Trạng thái và luồng dữ liệu)
-- 'Trong kho': Có thể dùng để Bán (POS)
-- 'Đã bán': Có thể dùng để Bảo hành
INSERT INTO SERISANPHAM (MaSeri, MaSP, MaPN, MaHD, TinhTrang) VALUES
('SN_STOCK_001', 'LAP-DELL-XPS', NULL, NULL, 'Trong kho'),
('SN_STOCK_002', 'LAP-DELL-XPS', NULL, NULL, 'Trong kho'),
('SN_STOCK_003', 'LAP-MAC-M1', NULL, NULL, 'Trong kho'),
('SN_SOLD_OLD', 'LAP-DELL-XPS', NULL, NULL, 'Đã bán'), -- Cho test bảo hành hết hạn
('SN_SOLD_NEW', 'LAP-DELL-XPS', NULL, NULL, 'Đã bán'); -- Cho test bảo hành còn hạn

-- 6. Seed HOADON (Mô phỏng lịch sử bán hàng cho Test Bảo hành)
-- Hóa đơn cũ cách đây 2 năm (để test Hết hạn bảo hành)
INSERT INTO HOADON (MaHD, MaNV, MaKH, NgayLap, TongTien) VALUES
('HD_2024_001', 'NV01', 'KH001', DATE_SUB(CURDATE(), INTERVAL 24 MONTH), 25000000);
UPDATE SERISANPHAM SET MaHD = 'HD_2024_001' WHERE MaSeri = 'SN_SOLD_OLD';

-- Hóa đơn mới cách đây 1 tháng (để test Còn bảo hành)
INSERT INTO HOADON (MaHD, MaNV, MaKH, NgayLap, TongTien) VALUES
('HD_2026_001', 'NV02', 'KH002', DATE_SUB(CURDATE(), INTERVAL 1 MONTH), 25000000);
UPDATE SERISANPHAM SET MaHD = 'HD_2026_001' WHERE MaSeri = 'SN_SOLD_NEW';

-- 7. Seed PHIEUBAOHANH (Lịch sử bảo hành đang xử lý)
INSERT INTO PHIEUBAOHANH (MaPBH, MaSeri, MaKH, MaNV, NgayTiepNhan, NgayTraDuKien, MoTaLoi, TinhTrang, ChiPhi) VALUES
('PBH_TEST_01', 'SN_SOLD_NEW', 'KH002', 'NV01', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 'Lỗi màn hình nhấp nháy', 'Đang xử lý', 0);
