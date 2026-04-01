package dao;

import model.NhanVien;
import model.NhanVienBanHang;
import model.NhanVienThuNgan;
import model.NhanVienQuanLyKho;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {

    public List<NhanVien> getAll() {
        List<NhanVien> ds = new ArrayList<>();
        // SELECT bổ sung các cột còn thiếu so với Java Model để đảm bảo tính năng tính lương, liên lạc
        String sql = "SELECT MaNV, HoTen, GioiTinh, NamSinh, SDT, DiaChi, ChucVu, SoNgayNghi, CaLamViec FROM NHANVIEN";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String chucVu = rs.getString("ChucVu");
                String id = rs.getString("MaNV");
                String hoTen = rs.getString("HoTen");
                String gioiTinh = rs.getString("GioiTinh");
                String namSinh = rs.getString("NamSinh");
                String sdt = rs.getString("SDT");
                String diaChi = rs.getString("DiaChi");
                int soNgayNghi = rs.getInt("SoNgayNghi");
                String caLamViec = rs.getString("CaLamViec");

                NhanVien nv = null;
                if (chucVu != null) {
                    if (chucVu.equalsIgnoreCase("Thu Ngan")) {
                        nv = new NhanVienThuNgan(chucVu, id, hoTen, gioiTinh, namSinh, sdt, diaChi, soNgayNghi, caLamViec);
                    } else if (chucVu.contains("Kho")) {
                        nv = new NhanVienQuanLyKho(chucVu, id, hoTen, gioiTinh, namSinh, sdt, diaChi, soNgayNghi, caLamViec);
                    } else {
                        nv = new NhanVienBanHang(chucVu, id, hoTen, gioiTinh, namSinh, sdt, diaChi, soNgayNghi, caLamViec);
                    }
                }
                
                if (nv != null) ds.add(nv);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Lỗi layTatCa NhanVien: " + e.getMessage());
        }
        return ds;
    }
}
