package dao;

import model.PhieuNhapKho;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class PhieuNhapDAO {

    /**
     * Tạo phiếu nhập mới cùng với chi tiết và danh sách Serial đi kèm.
     * Sử dụng Transaction để đảm bảo tính toàn vẹn dữ liệu.
     */
    public boolean taoPhieuNhap(PhieuNhapKho pn, List<String> danhSachSeri, String maSP, double giaNhap) {
        // Kiểm tra giới hạn 100 serial (Bảo vệ thêm ở tầng DAO)
        if (danhSachSeri.size() > 100) {
            System.err.println("Lỗi: Số lượng Serial vượt quá giới hạn 100 cái.");
            return false;
        }

        String sqlPN = "INSERT INTO PHIEUNHAP (MaPN, MaNV, MaNCC, TongTien) VALUES (?, ?, ?, ?)";
        String sqlCT = "INSERT INTO CHITIETPHIEUNHAP (MaPN, MaSP, SoLuong, DonGiaNhap) VALUES (?, ?, ?, ?)";
        String sqlSeri = "INSERT INTO SERISANPHAM (MaSeri, MaSP, MaPN, TinhTrang) VALUES (?, ?, ?, 'Trong kho')";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Insert Phếu Nhập
            try (PreparedStatement psPN = conn.prepareStatement(sqlPN)) {
                psPN.setString(1, pn.getMaPN());
                psPN.setString(2, pn.getMaNV());
                psPN.setString(3, pn.getMaNCC());
                psPN.setDouble(4, pn.getTongTien());
                psPN.executeUpdate();
            }

            // 2. Insert Chi Tiết Phiếu Nhập
            try (PreparedStatement psCT = conn.prepareStatement(sqlCT)) {
                psCT.setString(1, pn.getMaPN());
                psCT.setString(2, maSP);
                psCT.setInt(3, danhSachSeri.size());
                psCT.setDouble(4, giaNhap);
                psCT.executeUpdate();
            }

            // 3. Insert Danh sách Serial (Batch Processing)
            try (PreparedStatement psSeri = conn.prepareStatement(sqlSeri)) {
                for (String seri : danhSachSeri) {
                    psSeri.setString(1, seri.trim());
                    psSeri.setString(2, maSP);
                    psSeri.setString(3, pn.getMaPN());
                    psSeri.addBatch();
                }
                psSeri.executeBatch();
            }

            conn.commit(); // Hoàn tất thành công
            return true;

        } catch (SQLException | ClassNotFoundException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Hoàn tác nếu có lỗi
                    System.err.println("Transaction Rollback: " + e.getMessage());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
