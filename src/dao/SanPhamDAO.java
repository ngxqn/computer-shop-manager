package dao;

import model.SanPham;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SanPhamDAO {

    public List<SanPham> getAll() {
        List<SanPham> ds = new ArrayList<>();
        String sql = "SELECT MaSP, TenSP, LoaiSP, MaNCC, GiaBan, TGBaoHanh, TrangThai FROM SANPHAM";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String maSP = rs.getString("MaSP");
                String tenSP = rs.getString("TenSP");
                String loaiSP = rs.getString("LoaiSP");
                String maNCC = rs.getString("MaNCC");
                double giaBan = rs.getDouble("GiaBan");
                int tgBaoHanh = rs.getInt("TGBaoHanh");
                String trangThai = rs.getString("TrangThai");

                SanPham sp = new SanPham(maSP, tenSP, loaiSP, maNCC, giaBan, tgBaoHanh, trangThai);
                ds.add(sp);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Lỗi layTatCa SanPham: " + e.getMessage());
        }
        return ds;
    }
}
