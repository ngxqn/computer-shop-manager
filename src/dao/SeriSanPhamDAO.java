package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SeriSanPhamDAO {

    /**
     * Tra cứu thông tin một Serial để phục vụ việc bán hàng.
     * @param maSeri Mã Serial cần tra cứu
     * @return Object[] chứa {MaSeri, TenSP, GiaBan, TinhTrang}. Trả về null nếu không tồn tại.
     */
    public Object[] traCuuSerialBanHang(String maSeri) {
        String sql = "SELECT ss.MaSeri, ss.TinhTrang, sp.TenSP, sp.GiaBan " +
                     "FROM SERISANPHAM ss " +
                     "JOIN SANPHAM sp ON ss.MaSP = sp.MaSP " +
                     "WHERE ss.MaSeri = ?";
                     
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, maSeri);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                        rs.getString("MaSeri"),
                        rs.getString("TenSP"),
                        rs.getDouble("GiaBan"),
                        rs.getString("TinhTrang")
                    };
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Lỗi tra cứu Serial: " + e.getMessage());
        }
        return null;
    }
}
