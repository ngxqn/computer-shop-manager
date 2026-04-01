package dao;

import model.KhachHang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {

    public List<KhachHang> getAll() {
        List<KhachHang> ds = new ArrayList<>();
        // SELECT bổ sung NamSinh so với Java Model KhachHang
        String sql = "SELECT MaKH, HoTen, GioiTinh, NamSinh, SDT, DiaChi FROM KHACHHANG WHERE TrangThai = 'Hoạt động'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("MaKH");
                String hoTen = rs.getString("HoTen");
                String gioiTinh = rs.getString("GioiTinh");
                String namSinh = rs.getString("NamSinh"); // Sẽ cần cập nhật DB
                String sdt = rs.getString("SDT");
                String diaChi = rs.getString("DiaChi");

                KhachHang kh = new KhachHang(id, hoTen, gioiTinh, namSinh, sdt, diaChi);
                ds.add(kh);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Lỗi getAll KhachHang: " + e.getMessage());
        }
        return ds;
    }

    public boolean insert(KhachHang kh) {
        String sql = "INSERT INTO KHACHHANG (MaKH, HoTen, GioiTinh, NamSinh, SDT, DiaChi) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kh.getID());
            ps.setString(2, kh.getHoTen());
            ps.setString(3, kh.getGioiTinh());
            ps.setString(4, kh.getNamSinh());
            ps.setString(5, kh.getSdt());
            ps.setString(6, kh.getDiaChi());
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Lỗi insert KhachHang: " + e.getMessage());
            return false;
        }
    }

    public boolean update(KhachHang kh) {
        String sql = "UPDATE KHACHHANG SET HoTen = ?, GioiTinh = ?, NamSinh = ?, SDT = ?, DiaChi = ? WHERE MaKH = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kh.getHoTen());
            ps.setString(2, kh.getGioiTinh());
            ps.setString(3, kh.getNamSinh());
            ps.setString(4, kh.getSdt());
            ps.setString(5, kh.getDiaChi());
            ps.setString(6, kh.getID());
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Lỗi update KhachHang: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String maKH) {
        String sql = "UPDATE KHACHHANG SET TrangThai = 'Ngừng hoạt động' WHERE MaKH = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKH);
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Lỗi delete KhachHang: " + e.getMessage());
            return false;
        }
    }
}
