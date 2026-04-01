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
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Thêm phiếu bảo hành
            String sqlPhieu = "INSERT INTO PHIEUBAOHANH (MaPBH, MaSeri, MaKH, MaNV, NgayTiepNhan, NgayTraDuKien, MoTaLoi, TinhTrang, ChiPhi) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlPhieu)) {
                ps.setString(1, pbh.getMaPBH());
                ps.setString(2, pbh.getMaSeri());
                ps.setString(3, pbh.getMaKH());
                ps.setString(4, pbh.getMaNV());
                ps.setTimestamp(5, new Timestamp(pbh.getNgayTiepNhan().getTime()));
                ps.setTimestamp(6, pbh.getNgayTraDuKien() != null ? new Timestamp(pbh.getNgayTraDuKien().getTime()) : null);
                ps.setString(7, pbh.getMoTaLoi());
                ps.setString(8, pbh.getTinhTrang());
                ps.setDouble(9, pbh.getChiPhi());
                ps.executeUpdate();
            }

            // 2. Cập nhật trạng thái Serial
            String sqlSeri = "UPDATE SERISANPHAM SET TinhTrang = 'Đang bảo hành' WHERE MaSeri = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlSeri)) {
                ps.setString(1, pbh.getMaSeri());
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException | ClassNotFoundException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public List<PhieuBaoHanh> getAll() {
        List<PhieuBaoHanh> ds = new ArrayList<>();
        String sql = "SELECT * FROM PHIEUBAOHANH ORDER BY NgayTiepNhan DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PhieuBaoHanh pbh = new PhieuBaoHanh(
                    rs.getString("MaPBH"),
                    rs.getString("MaSeri"),
                    rs.getString("MaKH"),
                    rs.getString("MaNV"),
                    rs.getTimestamp("NgayTiepNhan"),
                    rs.getTimestamp("NgayTraDuKien"),
                    rs.getString("MoTaLoi"),
                    rs.getString("TinhTrang"),
                    rs.getDouble("ChiPhi")
                );
                ds.add(pbh);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean capNhatPhieu(String maPBH, String maSeri, String trangThai, double chiPhi) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Cập nhật phiếu bảo hành
            String sqlPhieu = "UPDATE PHIEUBAOHANH SET TinhTrang = ?, ChiPhi = ? WHERE MaPBH = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlPhieu)) {
                ps.setString(1, trangThai);
                ps.setDouble(2, chiPhi);
                ps.setString(3, maPBH);
                ps.executeUpdate();
            }

            // 2. Nếu trả khách, cập nhật lại trạng thái Serial
            if (trangThai.equalsIgnoreCase("Đã trả khách")) {
                String sqlSeri = "UPDATE SERISANPHAM SET TinhTrang = 'Đã bán' WHERE MaSeri = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlSeri)) {
                    ps.setString(1, maSeri);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
