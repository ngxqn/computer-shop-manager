package view;

import javax.swing.*;
import java.awt.*;
import controller.*;

public class MainFrame extends JFrame {
    // 1. Khởi tạo các Controller tập trung để quản lý dữ liệu duy nhất
    private QuanLySanPham qlsp = new QuanLySanPham(); //
    private QuanLyKho qlk = new QuanLyKho(qlsp);     //
    private QuanLyNhanVien qlnv = new QuanLyNhanVien(); //
    private QuanLyKhachHang qlkh = new QuanLyKhachHang(); //

    // 2. Khai báo các Panel giao diện cho từng tab
    private BangDieuKhienSanPham sanPhamPanel;
    private BangDieuKhienGiaoDich giaoDichPanel;
    private BangDieuKhienHoaDon hoaDonPanel;
    private BangDieuKhienNhanVien nhanVienPanel; //
    private BangDieuKhienKhachHang khachHangPanel; //

    public MainFrame(String title) {
        super(title);

        // --- Cấu hình JFrame cơ bản ---
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1350, 750);
        this.setLocationRelativeTo(null);
        this.setMinimumSize(new Dimension(1100, 600));

        // --- Tạo JTabbedPane chứa các phân hệ ---
        JTabbedPane tabs = new JTabbedPane();

        // 3. Khởi tạo các Panel và truyền đúng Controller vào
        // Tab Kho: Chuyên trách nhập/xuất nội bộ và báo cáo tồn kho
        giaoDichPanel = new BangDieuKhienGiaoDich(qlk, qlsp);

        // Tab Hóa Đơn: Chuyên trách lập hóa đơn bán hàng và hiển thị lịch sử
        hoaDonPanel = new BangDieuKhienHoaDon(qlsp);

        // Tab Sản Phẩm: Quản lý danh mục sản phẩm tại cửa hàng
        sanPhamPanel = new BangDieuKhienSanPham(qlsp);

        // Tab Nhân Viên: Quản lý thông tin và lương nhân sự
        nhanVienPanel = new BangDieuKhienNhanVien(qlnv);

        // Tab Khách Hàng: Quản lý thông tin khách hàng thân thiết
        khachHangPanel = new BangDieuKhienKhachHang(qlkh);

        // 4. Thêm các Tab vào JTabbedPane
        tabs.addTab("Kho Nội Bộ", giaoDichPanel);
        tabs.addTab("Bán Hàng & Hóa Đơn", hoaDonPanel);
        tabs.addTab("Sản Phẩm Cửa Hàng", sanPhamPanel);
        tabs.addTab("Quản Lý Nhân Viên", nhanVienPanel);
        tabs.addTab("Quản Lý Khách Hàng", khachHangPanel);

        // --- 5. BỘ LẮNG NGHE SỰ KIỆN TỰ ĐỘNG LÀM MỚI (REFRESH) ---
        // Giúp đồng bộ dữ liệu ngay lập tức khi người dùng chuyển tab
        tabs.addChangeListener(e -> {
            int selectedIndex = tabs.getSelectedIndex();
            String titleTab = tabs.getTitleAt(selectedIndex);

            if (titleTab.equals("Sản Phẩm Cửa Hàng")) {
                sanPhamPanel.taiDuLieuVaoBang(); // Nạp lại DSSanPham.txt
            } else if (titleTab.equals("Kho Nội Bộ")) {
                giaoDichPanel.taiDuLieuVaoBang(); // Nạp lại dữ liệu kho
            } else if (titleTab.equals("Bán Hàng & Hóa Đơn")) {
                hoaDonPanel.taiDuLieu(); // Nạp lại DSHoaDon.txt
            } else if (titleTab.equals("Quản Lý Nhân Viên")) {
                qlnv.docFileTXT(); // Cập nhật danh sách nhân viên
                // nhanVienPanel.taiDuLieuVaoBang(); // Nếu có hàm refresh trong Panel NV
            } else if (titleTab.equals("Quản Lý Khách Hàng")) {
                qlkh.docFileTXT(); // Cập nhật danh sách khách hàng
                // khachHangPanel.taiDuLieuVaoBang(); // Nếu có hàm refresh trong Panel KH
            }
        });

        this.add(tabs);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        // Thiết lập giao diện Nimbus cho hiện đại
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Không thể thiết lập giao diện Nimbus.");
        }

        SwingUtilities.invokeLater(() -> {
            new MainFrame("HỆ THỐNG QUẢN LÝ SIÊU THỊ MINI - TEAM 03");
        });
    }
}
