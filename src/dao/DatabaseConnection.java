package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Cấu hình mạng mặc định cho XAMPP MySQL
    // Bạn hãy thay thế "cuahangmaytinh" bằng tên cơ sở dữ liệu thực tế đã tạo nhé
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/cuahangmaytinh";
    private static final String DEFAULT_USER = "root";     // XAMPP mặc định là root
    private static final String DEFAULT_PASSWORD = "";     // XAMPP mặc định không có mật khẩu

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        // Tải MySQL JDBC Driver (CJ driver hỗ trợ MySQL 8.0 trở lên)
        Class.forName("com.mysql.cj.jdbc.Driver");
        
        // Thiết lập và trả về kết nối
        return DriverManager.getConnection(DEFAULT_URL, DEFAULT_USER, DEFAULT_PASSWORD);
    }
}
