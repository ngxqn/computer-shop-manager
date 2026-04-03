package view;

import javax.swing.*;
import java.awt.*;
import controller.*;

public class MainFrame extends JFrame {
    // 1. Khởi tạo các Controller tập trung để quản lý dữ liệu duy nhất
    private QuanLySanPham qlsp = new QuanLySanPham();
    private QuanLyKho qlk = new QuanLyKho(qlsp);
    private QuanLyNhanVien qlnv = new QuanLyNhanVien();
    private QuanLyKhachHang qlkh = new QuanLyKhachHang();

    // 2. Khai báo các Panel giao diện cho từng tab
    private BangDieuKhienSanPham sanPhamPanel;
    private BangDieuKhienGiaoDich giaoDichPanel;
    private BangDieuKhienHoaDon hoaDonPanel;
    private BangDieuKhienNhanVien nhanVienPanel;
    private BangDieuKhienKhachHang khachHangPanel;
    private BangDieuKhienBaoHanh baoHanhPanel; 
    private JLabel lblStatus, lblUser, lblTime;

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
        giaoDichPanel = new BangDieuKhienGiaoDich(qlk, qlsp);
        hoaDonPanel = new BangDieuKhienHoaDon();
        sanPhamPanel = new BangDieuKhienSanPham(qlsp);
        nhanVienPanel = new BangDieuKhienNhanVien(qlnv);
        khachHangPanel = new BangDieuKhienKhachHang(qlkh);
        baoHanhPanel = new BangDieuKhienBaoHanh();

        // --- 4. Thêm các Tab vào JTabbedPane ---
        tabs.addTab("Kho nội bộ", giaoDichPanel);
        tabs.addTab("Hóa đơn bán hàng", hoaDonPanel);
        tabs.addTab("Quản lý bảo hành", baoHanhPanel);
        tabs.addTab("Danh mục sản phẩm", sanPhamPanel);
        tabs.addTab("Quản lý nhân viên", nhanVienPanel);
        tabs.addTab("Quản lý khách hàng", khachHangPanel);

        BangDieuKhienBaoCao baoCaoPanel = new BangDieuKhienBaoCao();
        tabs.addTab("Báo cáo doanh thu", baoCaoPanel);

        // --- 5. THIẾT LẬP MENU BAR (PHASE 5) ---
        JMenuBar menuBar = new JMenuBar();

        // 1. Menu Hệ thống
        JMenu menuHeThong = new JMenu("⛭ Hệ thống");
        menuHeThong.setMnemonic('s');

        JMenuItem itemDangXuat = new JMenuItem("↪ Đăng xuất");
        itemDangXuat.setAccelerator(
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        itemDangXuat.addActionListener(e -> {
            this.dispose();
            main(null); // Quay lại màn hình đăng nhập
        });

        JMenuItem itemThoat = new JMenuItem("× Thoát");
        itemThoat.setAccelerator(
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        itemThoat.addActionListener(e -> System.exit(0));

        menuHeThong.add(itemDangXuat);
        menuHeThong.add(new JSeparator());
        menuHeThong.add(itemThoat);

        // 2. Menu Nghiệp vụ & Danh mục (Điều hướng nhanh)
        JMenu menuDieuHuong = new JMenu("☰ Chức năng");
        menuDieuHuong.setMnemonic('f');

        String[] danhSachTab = {
                "Kho nội bộ", "Hóa đơn bán hàng", "Quản lý bảo hành",
                "Danh mục sản phẩm", "Quản lý nhân viên", "Quản lý khách hàng", "Báo cáo doanh thu"
        };
        char[] phimTat = { '1', '2', '3', '4', '5', '6', '7' };

        for (int i = 0; i < danhSachTab.length; i++) {
            final int index = i;
            JMenuItem item = new JMenuItem(danhSachTab[i]);
            item.setAccelerator(KeyStroke.getKeyStroke(phimTat[i], java.awt.event.InputEvent.CTRL_DOWN_MASK));
            item.addActionListener(e -> tabs.setSelectedIndex(index));
            menuDieuHuong.add(item);
            if (i == 2 || i == 5)
                menuDieuHuong.addSeparator(); // Ngăn cách nhóm
        }

        // 3. Menu Trợ giúp
        JMenu menuTroGiup = new JMenu("❓ Trợ giúp");
        menuTroGiup.setMnemonic('h');

        JMenuItem itemAbout = new JMenuItem("ℹ Thông tin chương trình");
        itemAbout.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "PHẦN MỀM QUẢN LÝ CỬA HÀNG MÁY TÍNH\n" +
                            "Phiên bản: 2.0 (Build 2026.04.03)\n" +
                            "Phát triển bởi: Team ISAD C5-13_05\n" +
                            "Hệ thống đã sẵn sàng phục vụ.",
                    "Thông tin", JOptionPane.INFORMATION_MESSAGE);
        });
        menuTroGiup.add(itemAbout);

        menuBar.add(menuHeThong);
        menuBar.add(menuDieuHuong);
        menuBar.add(menuTroGiup);

        this.setJMenuBar(menuBar);

        this.add(tabs, BorderLayout.CENTER);
        this.add(taoStatusBar(), BorderLayout.SOUTH);

        // --- 7. CẬP NHẬT STATUS BAR KHI ĐỔI TAB ---
        tabs.addChangeListener(e -> {
            int selectedIndex = tabs.getSelectedIndex();
            String titleTab = tabs.getTitleAt(selectedIndex);
            lblStatus.setText("✪ Đang xem: " + titleTab);

            if (titleTab.equals("Danh mục sản phẩm")) {
                sanPhamPanel.taiDuLieuVaoBang();
            } else if (titleTab.equals("Kho nội bộ")) {
                giaoDichPanel.taiDuLieuVaoBang();
            } else if (titleTab.equals("Hoá đơn bán hàng")) {
                hoaDonPanel.taiDuLieu();
            } else if (titleTab.equals("Quản lý nhân viên")) {
                qlnv.refreshData();
            } else if (titleTab.equals("Quản lý khách hàng")) {
                qlkh.refreshData();
            } else if (titleTab.equals("Báo cáo doanh thu")) {
                baoCaoPanel.taiDuLieu();
            }
        });

        this.setVisible(true);
    }

    private JPanel taoStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        panel.setPreferredSize(new Dimension(this.getWidth(), 30));

        lblStatus = new JLabel("✪ Hệ thống sẵn sàng");
        lblStatus.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        String name = controller.AppSession.getInstance().getTenNV();
        lblUser = new JLabel("👤 Nhân viên: " + name);
        lblUser.setHorizontalAlignment(SwingConstants.CENTER);

        lblTime = new JLabel();
        lblTime.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        
        // Timer cập nhật đồng hồ mỗi giây
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss | dd/MM/yyyy");
        Timer timer = new Timer(1000, e -> lblTime.setText(sdf.format(new java.util.Date())));
        timer.start();
        lblTime.setText(sdf.format(new java.util.Date()));

        panel.add(lblStatus, BorderLayout.WEST);
        panel.add(lblUser, BorderLayout.CENTER);
        panel.add(lblTime, BorderLayout.EAST);

        return panel;
    }

    public static void main(String[] args) {
        // Thiết lập giao diện FlatLaf Dark
        try {
            com.formdev.flatlaf.themes.FlatMacDarkLaf.setup();
        } catch (Exception e) {
            System.err.println("Không thể thiết lập FlatLaf. Đang dùng theme mặc định.");
        }

        SwingUtilities.invokeLater(() -> {
            DangNhapDialog login = new DangNhapDialog(null);
            login.setVisible(true);

            if (login.isThanhCong()) {
                String name = controller.AppSession.getInstance().getTenNV();
                MainFrame frame = new MainFrame("Computer Shop Manager | Xin chào: " + name);
                frame.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}
