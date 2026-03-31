package dao;

import model.PhieuBaoHanh;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhieuBaoHanhDAO {

    /**
     * Tra cứu thông tin bảo hành của một Serial.
     * Sử dụng DATE_ADD để tính ngày hết hạn trực tiếp từ SQL.
     */
    public Object[] traCuuBaoHanh(String maSeri) {
        String sql = "SELECT sp.TenSP, hd.NgayLap, " +
                     "DATE_ADD(hd.NgayLap, INTERVAL sp.TGBaoHanh MONTH) AS NgayHetHan, " +
                     "ss.TinhTrang, hd.MaKH " +
                     "FROM SERISANPHAM ss " +
                     "JOIN SANPHAM sp ON ss.MaSP = sp.MaSP " +
                     "LEFT JOIN HOADON hd ON ss.MaHD = hd.MaHD " +
                     "WHERE ss.MaSeri = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maSeri);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                        rs.getString("TenSP"),
                        rs.getTimestamp("NgayLap"),
                        rs.getTimestamp("NgayHetHan"),
                        rs.getString("TinhTrang"),
                        rs.getString("MaKH")
                    };
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Tạo phiếu bảo hành mới và cập nhật trạng thái Serial thành 'Đang bảo hành'.
     * Thực hiện trong một Transaction.
     */
    public boolean taoPhieuBaoHanh(PhieuBaoHanh pbh) {
        Connection conn = null;
        PreparedStatement psPhieu = null;
        PreparedStatement psSeri = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Thêm phiếu bảo hành
            String sqlPhieu = "INSERT INTO PHIEUBAOHANH (MaPBH, MaSeri, MaKH, MaNV, NgayTiepNhan, NgayTraDuKien, MoTaLoi, TinhTrang, ChiPhi) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            psPhieu = conn.prepareStatement(sqlPhieu);
            psPhieu.setString(1, pbh.getMaPBH());
            psPhieu.setString(2, pbh.getMaSeri());
            psPhieu.setString(3, pbh.getMaKH());
            psPhieu.setString(4, pbh.getMaNV());
            psPhieu.setTimestamp(5, new Timestamp(pbh.getNgayTiepNhan().getTime()));
            psPhieu.setTimestamp(6, pbh.getNgayTraDuKien() != null ? new Timestamp(pbh.getNgayTraDuKien().getTime()) : null);
            psPhieu.setString(7, pbh.getMoTaLoi());
            psPhieu.setString(8, pbh.getTinhTrang());
            psPhieu.setDouble(9, pbh.getChiPhi());
            psPhieu.executeUpdate();

            // 2. Cập nhật trạng thái Serial
            String sqlSeri = "UPDATE SERISANPHAM SET TinhTrang = 'Đang bảo hành' WHERE MaSeri = ?";
            psSeri = conn.prepareStatement(sqlSeri);
            psSeri.setString(1, pbh.getMaSeri());
            psSeri.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException | ClassNotFoundException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (psPhieu != null) psPhieu.close();
                if (psSeri != null) psSeri.close();
                if (conn != null) conn.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
