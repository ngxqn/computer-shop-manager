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
        String sql = "SELECT MaNV, HoTen, GioiTinh, NamSinh, SDT, DiaChi, ChucVu, SoNgayNghi, CaLamViec FROM NHANVIEN WHERE TrangThai = 'Đang làm việc'";

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
            System.err.println("Lỗi getAll NhanVien: " + e.getMessage());
        }
        return ds;
    }

    public boolean insert(NhanVien nv) {
        String sql = "INSERT INTO NHANVIEN (MaNV, HoTen, GioiTinh, NamSinh, SDT, DiaChi, ChucVu, SoNgayNghi, CaLamViec) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getID());
            ps.setString(2, nv.getHoTen());
            ps.setString(3, nv.getGioiTinh());
            ps.setString(4, nv.getNamSinh());
            ps.setString(5, nv.getSdt());
            ps.setString(6, nv.getDiaChi());
            ps.setString(7, nv.getChucVu());
            ps.setInt(8, nv.getSoNgayNghi());
            ps.setString(9, nv.getCaLamViec());
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Lỗi insert NhanVien: " + e.getMessage());
            return false;
        }
    }

    public boolean update(NhanVien nv) {
        String sql = "UPDATE NHANVIEN SET HoTen = ?, GioiTinh = ?, NamSinh = ?, SDT = ?, DiaChi = ?, ChucVu = ?, SoNgayNghi = ?, CaLamViec = ? WHERE MaNV = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getHoTen());
            ps.setString(2, nv.getGioiTinh());
            ps.setString(3, nv.getNamSinh());
            ps.setString(4, nv.getSdt());
            ps.setString(5, nv.getDiaChi());
            ps.setString(6, nv.getChucVu());
            ps.setInt(7, nv.getSoNgayNghi());
            ps.setString(8, nv.getCaLamViec());
            ps.setString(9, nv.getID());
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Lỗi update NhanVien: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String maNV) {
        String sql = "UPDATE NHANVIEN SET TrangThai = 'Ngừng hoạt động' WHERE MaNV = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Lỗi delete NhanVien: " + e.getMessage());
            return false;
        }
    }
}
