package view;

import controller.QuanLi_SanPham;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;

public class BangDieuKhien_HoaDon extends JPanel {
    private JTable bangHoaDon;
    private DefaultTableModel moHinhBang;
    private QuanLi_SanPham quanLySanPham;
    private DecimalFormat dinhDangTien = new DecimalFormat("###,###,### VNĐ");

    // Đường dẫn file dữ liệu
    private final String DUONG_DAN_FILE = "Team_03_OOP (3) (1)/Team03_OOP/data/DSHoaDon.txt";

    public BangDieuKhien_HoaDon(QuanLi_SanPham qlsp) {
        this.quanLySanPham = qlsp;
        this.setLayout(new BorderLayout(10, 10));

        // 1. Thiết lập tiêu đề bảng khớp với hình ảnh của bạn
        String[] columns = {"Mã HD", "Ngày Giờ", "Khách Hàng", "Nhân Viên", "Tổng Tiền"};
        moHinhBang = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        bangHoaDon = new JTable(moHinhBang);

        this.add(new JScrollPane(bangHoaDon), BorderLayout.CENTER);

        // 2. Thêm nút làm mới để cập nhật bảng thủ công
        JButton nutLamMoi = new JButton("Làm Mới Lịch Sử");
        nutLamMoi.addActionListener(e -> taiDuLieu());
        this.add(nutLamMoi, BorderLayout.SOUTH);

        // 3. Tự động tải dữ liệu khi khởi tạo
        taiDuLieu();
    }

    public void taiDuLieu() {
        moHinhBang.setRowCount(0); // Xóa bảng cũ
        File file = new File(DUONG_DAN_FILE);

        if (!file.exists()) {
            return; // Nếu chưa có file thì để bảng trống
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String dong;
            while ((dong = reader.readLine()) != null) {
                if (dong.trim().isEmpty()) continue;

                // Cấu trúc trong file: MaHD, NgayGio, KhachHang, NhanVien, TongTien
                String[] duLieu = dong.split(",");
                if (duLieu.length >= 5) {
                    try {
                        // Định dạng lại tiền cho đẹp (cột cuối cùng)
                        double tongTien = Double.parseDouble(duLieu[4].trim());
                        duLieu[4] = dinhDangTien.format(tongTien);

                        moHinhBang.addRow(duLieu);
                    } catch (NumberFormatException e) {
                        moHinhBang.addRow(duLieu); // Nếu lỗi định dạng số thì add chuỗi gốc
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi đọc lịch sử giao dịch: " + e.getMessage());
        }
    }
}