package dao;

import model.KhachHang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {

    public List<KhachHang> layTatCa() {
        List<KhachHang> ds = new ArrayList<>();
        // SELECT bổ sung NamSinh so với Java Model KhachHang
        String sql = "SELECT MaKH, HoTen, GioiTinh, NamSinh, SDT, DiaChi FROM KHACHHANG";

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
            System.err.println("Lỗi layTatCa KhachHang: " + e.getMessage());
        }
        return ds;
    }
}
