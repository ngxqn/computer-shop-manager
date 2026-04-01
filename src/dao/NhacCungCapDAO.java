package dao;

import model.NhacCungCap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NhacCungCapDAO {

    public List<NhacCungCap> getAll() {
        List<NhacCungCap> ds = new ArrayList<>();
        String sql = "SELECT MaNCC, TenNCC, DiaChi, SDT, TrangThai FROM NHACUNGCAP WHERE TrangThai = 'Hoạt động'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                NhacCungCap ncc = new NhacCungCap(
                    rs.getString("MaNCC"),
                    rs.getString("TenNCC"),
                    rs.getString("DiaChi"),
                    rs.getString("SDT"),
                    rs.getString("TrangThai")
                );
                ds.add(ncc);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Lỗi getAll NhacCungCap: " + e.getMessage());
        }
        return ds;
    }
}
