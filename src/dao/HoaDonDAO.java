package dao;

import model.ChiTietHoaDon;
import model.HoaDon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class HoaDonDAO {

    /**
     * Thực thi giao dịch bán hàng (JDBC Transaction):
     * 1. Tính toán tổng tiền hóa đơn (TongTien)
     * 2. Thêm mới bản ghi vào HOADON
     * 3. Thêm mới các bản ghi vào CHITIETHOADON (Batch Processing)
     * 4. Cập nhật trạng thái "Đã bán" trên SERISANPHAM (Batch Processing)
     */
    public boolean taoHoaDon(HoaDon hd, List<ChiTietHoaDon> dsChiTiet) {
        Connection conn = null;
        PreparedStatement psHoaDon = null;
        PreparedStatement psChiTiet = null;
        PreparedStatement psUpdateSeri = null;

        try {
            // Lấy kết nối CSDL
            conn = DatabaseConnection.getConnection();
            if (conn == null) return false;

            // Vô hiệu hóa auto-commit để bắt đầu Transaction
            conn.setAutoCommit(false);

            // B1: Tính toán tổng tiền hoá đơn trước khi insert 
            double tongTien = 0;
            for (ChiTietHoaDon ct : dsChiTiet) {
                tongTien += ct.getDonGiaBan();
            }
            hd.setTongTien(tongTien);

            // B2: Insert vào bảng HOADON
            String sqlHoaDon = "INSERT INTO HOADON (MaHD, MaNV, MaKH, NgayLap, TongTien) VALUES (?, ?, ?, ?, ?)";
            psHoaDon = conn.prepareStatement(sqlHoaDon);
            psHoaDon.setString(1, hd.getMaHD());
            psHoaDon.setString(2, hd.getMaNV());
            psHoaDon.setString(3, hd.getMaKH());
            
            // Xử lý an toàn thời gian ngày lập tự động nếu chưa có
            if (hd.getNgayLap() == null) hd.setNgayLap(new java.util.Date());
            java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(hd.getNgayLap().getTime());
            psHoaDon.setTimestamp(4, sqlTimestamp);
            
            psHoaDon.setDouble(5, hd.getTongTien());
            psHoaDon.executeUpdate();

            // Chuẩn bị SQL cho Batch Processing (B3 & B4)
            String sqlChiTiet = "INSERT INTO CHITIETHOADON (MaHD, MaSeri, DonGiaBan) VALUES (?, ?, ?)";
            psChiTiet = conn.prepareStatement(sqlChiTiet);

            String sqlUpdateSeri = "UPDATE SERISANPHAM SET TinhTrang = 'Đã bán', MaHD = ? WHERE MaSeri = ?";
            psUpdateSeri = conn.prepareStatement(sqlUpdateSeri);

            for (ChiTietHoaDon ct : dsChiTiet) {
                // Thêm vào hàng đợi lô của CHITIETHOADON
                psChiTiet.setString(1, hd.getMaHD());
                psChiTiet.setString(2, ct.getMaSeri());
                psChiTiet.setDouble(3, ct.getDonGiaBan());
                psChiTiet.addBatch();

                // Thêm vào hàng đợi lô của SERISANPHAM
                psUpdateSeri.setString(1, hd.getMaHD());
                psUpdateSeri.setString(2, ct.getMaSeri());
                psUpdateSeri.addBatch();
            }

            // B3 và B4: Thực thi nạp hàng loạt (Batch Execution)
            psChiTiet.executeBatch();
            psUpdateSeri.executeBatch();

            // B5: Ghi nhận giao dịch thành công toàn bộ
            conn.commit();
            return true;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            // Nếu có lỗi, hoàn tác khôi phục trạng thái ban đầu để tránh sai mẫu tin
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            // Đóng các luồng con (Ngăn ngừa rò rỉ bộ nhớ/connection pool)
            try {
                if (psHoaDon != null) psHoaDon.close();
                if (psChiTiet != null) psChiTiet.close();
                if (psUpdateSeri != null) psUpdateSeri.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Trả lại hành động mặc định sau khi xong
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Lấy toàn bộ Hóa Đơn từ Database để hiển thị trên bảng lịch sử
     */
    public List<HoaDon> layTatCaHoaDon() {
        List<HoaDon> dsHoaDon = new java.util.ArrayList<>();
        String sql = "SELECT MaHD, MaNV, MaKH, NgayLap, TongTien FROM HOADON ORDER BY NgayLap DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getString("MaHD"));
                hd.setMaNV(rs.getString("MaNV"));
                hd.setMaKH(rs.getString("MaKH"));
                
                java.sql.Timestamp ts = rs.getTimestamp("NgayLap");
                if (ts != null) {
                    hd.setNgayLap(new java.util.Date(ts.getTime()));
                }
                
                hd.setTongTien(rs.getDouble("TongTien"));
                dsHoaDon.add(hd);
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return dsHoaDon;
    }

    /**
     * Thống kê tổng doanh thu theo từng tháng.
     * Trả về Map với Key là "Tháng/Năm" và Value là Tổng tiền.
     */
    public java.util.Map<String, Double> thongKeDoanhThuTheoThang() {
        java.util.Map<String, Double> ketQua = new java.util.LinkedHashMap<>();
        String sql = "SELECT DATE_FORMAT(NgayLap, '%m/%Y') AS Thang, SUM(TongTien) AS TongDoanhThu " +
                     "FROM HOADON " +
                     "GROUP BY Thang " +
                     "ORDER BY MIN(NgayLap) ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ketQua.put(rs.getString("Thang"), rs.getDouble("TongDoanhThu"));
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return ketQua;
    }
}
