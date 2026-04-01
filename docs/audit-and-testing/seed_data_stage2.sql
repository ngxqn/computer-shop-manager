-- CHUẨN BỊ DỮ LIỆU KIỂM THỬ STAGE 2
USE CuaHangMayTinh;

-- 1. Xóa dữ liệu cũ để tránh xung đột PK
DELETE FROM CHITIETHOADON;
DELETE FROM PHIEUBAOHANH;
DELETE FROM SERISANPHAM;
DELETE FROM HOADON;
DELETE FROM SANPHAM;
DELETE FROM NHACUNGCAP;
DELETE FROM KHACHHANG;
DELETE FROM NHANVIEN;

-- 2. Seed NHANVIEN (Đáp ứng RISK-POS-01)
INSERT INTO NHANVIEN (MaNV, HoTen, GioiTinh, NamSinh, SDT, DiaChi, ChucVu) VALUES
('NV01', 'Nguyễn Văn Admin', 'Nam', '1990', '0901234567', 'Hà Nội', 'Quản lý');

-- 3. Seed KHACHHANG (Đáp ứng RISK-POS-01)
-- Lưu ý: TaoHoaDonDialog dùng "Khách vãng lai" làm mặc định
INSERT INTO KHACHHANG (MaKH, HoTen, GioiTinh, NamSinh, SDT, DiaChi) VALUES
('KH_VLAN', 'Khách vãng lai', 'Nam', '1990', '0000000000', 'Địa chỉ mặc định');

-- Cập nhật: TaoHoaDonDialog nên dùng 'KH_VLAN' làm mã khách hàng
-- Nhưng nếu code view đang hardcode text, thì ta insert đúng text đó làm MaKH
INSERT INTO KHACHHANG (MaKH, HoTen, GioiTinh, NamSinh, SDT, DiaChi) VALUES
('Khách vãng lai', 'Khách vãng lai', 'Nam', '1990', '0000000000', 'Địa chỉ mặc định');

-- 4. Seed NHACUNGCAP & SANPHAM
INSERT INTO NHACUNGCAP (MaNCC, TenNCC, DiaChi, SDT) VALUES
('NCC01', 'Dell Global', 'USA', '12345');

INSERT INTO SANPHAM (MaSP, TenSP, LoaiSP, MaNCC, GiaBan, TGBaoHanh, TrangThai) VALUES
('LAP-DELL-XPS', 'Dell XPS 13', 'Laptop', 'NCC01', 25000000.00, 12, 'Đang kinh doanh');

-- 5. Seed SERISANPHAM (SN_HAPPY_001 cho Happy Path, SN_TEST_001 cho TC-POS-02)
INSERT INTO SERISANPHAM (MaSeri, MaSP, MaPN, MaHD, TinhTrang) VALUES
('SN_HAPPY_001', 'LAP-DELL-XPS', NULL, NULL, 'Trong kho'),
('SN_HAPPY_002', 'LAP-DELL-XPS', NULL, NULL, 'Trong kho'),
('SN_TEST_001', 'LAP-DELL-XPS', NULL, NULL, 'Đã bán'),
('SN_EXPIRED_001', 'LAP-DELL-XPS', NULL, NULL, 'Đã bán'),
('SN_VALID_001', 'LAP-DELL-XPS', NULL, NULL, 'Đã bán'),
('SN_UNWRN_001', 'LAP-DELL-XPS', NULL, NULL, 'Trong kho');

-- 6. Chuẩn bị Hóa đơn cho máy đã bán (TC-WAR-02 hết hạn, TC-WAR-03 còn hạn)
-- Hóa đơn 2 năm trước (hết hạn)
INSERT INTO HOADON (MaHD, MaNV, MaKH, NgayLap, TongTien) VALUES
('HD_OLD_999', 'NV01', 'Khách vãng lai', DATE_SUB(NOW(), INTERVAL 24 MONTH), 25000000);
UPDATE SERISANPHAM SET MaHD = 'HD_OLD_999' WHERE MaSeri = 'SN_EXPIRED_001';

-- Hóa đơn mới hôm nay (còn hạn)
INSERT INTO HOADON (MaHD, MaNV, MaKH, NgayLap, TongTien) VALUES
('HD_NEW_001', 'NV01', 'Khách vãng lai', NOW(), 25000000);
UPDATE SERISANPHAM SET MaHD = 'HD_NEW_001' WHERE MaSeri = 'SN_VALID_001';
UPDATE SERISANPHAM SET MaHD = 'HD_NEW_001' WHERE MaSeri = 'SN_TEST_001';

-- 7. Seed dữ liệu cho Báo cáo Doanh thu (TC-REP-01)
-- Giả sử hôm nay là tháng 4/2026 (theo local time metadata), ta tạo thêm hóa đơn các tháng trước
INSERT INTO HOADON (MaHD, MaNV, MaKH, NgayLap, TongTien) VALUES
('HD_REP_MAR', 'NV01', 'KH_VLAN', '2026-03-15 10:00:00', 15000000),
('HD_REP_FEB', 'NV01', 'KH_VLAN', '2026-02-10 14:00:00', 45000000),
('HD_REP_JAN1', 'NV01', 'KH_VLAN', '2026-01-05 09:00:00', 20000000),
('HD_REP_JAN2', 'NV01', 'KH_VLAN', '2026-01-20 16:30:00', 30000000),
('HD_REP_DEC25', 'NV01', 'KH_VLAN', '2025-12-25 11:00:00', 100000000);
