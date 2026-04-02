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

    public boolean insert(SanPham sp) {
        String sql = "INSERT INTO SANPHAM (MaSP, TenSP, LoaiSP, MaNCC, GiaBan, TGBaoHanh, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sp.getMaSP());
            ps.setString(2, sp.getTenSP());
            ps.setString(3, sp.getLoaiSP());
            ps.setString(4, sp.getMaNCC());
            ps.setDouble(5, sp.getGiaBan());
            ps.setInt(6, sp.getTgBaoHanh());
            ps.setString(7, sp.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Lỗi insert SanPham: " + e.getMessage());
            return false;
        }
    }

    public boolean update(SanPham sp) {
        String sql = "UPDATE SANPHAM SET TenSP=?, LoaiSP=?, GiaBan=?, TGBaoHanh=? WHERE MaSP=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sp.getTenSP());
            ps.setString(2, sp.getLoaiSP());
            ps.setDouble(3, sp.getGiaBan());
            ps.setInt(4, sp.getTgBaoHanh());
            ps.setString(5, sp.getMaSP());
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Lỗi update SanPham: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String maSP) {
        String sql = "UPDATE SANPHAM SET TrangThai = 'Ngừng kinh doanh' WHERE MaSP = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSP);
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Lỗi delete SanPham: " + e.getMessage());
            return false;
        }
    }
}
