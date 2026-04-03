package view;

import javax.swing.*;
import java.awt.*;
import controller.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.io.InputStream;
import java.util.Date;
import java.text.SimpleDateFormat;

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

        // --- 4. Thêm các Tab vào JTabbedPane với SVG Icons ---
        tabs.addTab("Kho nội bộ", new FlatSVGIcon("resources/icons/package.svg", 18, 18), giaoDichPanel);
        tabs.addTab("Hóa đơn bán hàng", new FlatSVGIcon("resources/icons/shopping-cart.svg", 18, 18), hoaDonPanel);
        tabs.addTab("Bảo hành", new FlatSVGIcon("resources/icons/shield-check.svg", 18, 18), baoHanhPanel);
        tabs.addTab("Danh mục sản phẩm", new FlatSVGIcon("resources/icons/laptop.svg", 18, 18), sanPhamPanel);
        tabs.addTab("Nhân viên", new FlatSVGIcon("resources/icons/users.svg", 18, 18), nhanVienPanel);
        tabs.addTab("Khách hàng", new FlatSVGIcon("resources/icons/user.svg", 18, 18), khachHangPanel);
        
        BangDieuKhienBaoCao baoCaoPanel = new BangDieuKhienBaoCao();
        tabs.addTab("Báo cáo doanh thu", new FlatSVGIcon("resources/icons/bar-chart-3.svg", 18, 18), baoCaoPanel);

        // --- 5. THIẾT LẬP MENU BAR ---
        JMenuBar menuBar = new JMenuBar();

        // 1. Menu Hệ thống
        JMenu menuHeThong = new JMenu("Hệ thống");
        menuHeThong.setIcon(new FlatSVGIcon("resources/icons/settings.svg", 16, 16));
        menuHeThong.setMnemonic('s');

        JMenuItem itemDangXuat = new JMenuItem("Đăng xuất");
        itemDangXuat.setIcon(new FlatSVGIcon("resources/icons/log-out.svg", 16, 16));
        itemDangXuat.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        itemDangXuat.addActionListener(e -> {
            this.dispose();
            main(null); 
        });

        JMenuItem itemThoat = new JMenuItem("Thoát");
        itemThoat.setIcon(new FlatSVGIcon("resources/icons/x-circle.svg", 16, 16));
        itemThoat.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        itemThoat.addActionListener(e -> System.exit(0));

        menuHeThong.add(itemDangXuat);
        menuHeThong.add(new JSeparator());
        menuHeThong.add(itemThoat);

        // 2. Menu Chức năng
        JMenu menuDieuHuong = new JMenu("Chức năng");
        menuDieuHuong.setIcon(new FlatSVGIcon("resources/icons/menu.svg", 16, 16));
        
        String[] danhSachTab = {"Kho nội bộ", "Hóa đơn bán hàng", "Bảo hành", "Danh mục sản phẩm", "Nhân viên", "Khách hàng", "Báo cáo doanh thu"};
        for (int i = 0; i < danhSachTab.length; i++) {
            final int index = i;
            JMenuItem item = new JMenuItem(danhSachTab[i]);
            item.addActionListener(ev -> tabs.setSelectedIndex(index));
            menuDieuHuong.add(item);
        }

        // 3. Menu Trợ giúp
        JMenu menuTroGiup = new JMenu("Trợ giúp");
        menuTroGiup.setIcon(new FlatSVGIcon("resources/icons/help-circle.svg", 16, 16));
        
        JMenuItem itemAbout = new JMenuItem("Thông tin chương trình");
        itemAbout.setIcon(new FlatSVGIcon("resources/icons/info.svg", 16, 16));
        itemAbout.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "PHẦN MỀM QUẢN LÝ CỬA HÀNG MÁY TÍNH\nPhiên bản: 2.1.0 (Build 2026.04.03)\nTeam ISAD C5-13_05", "Thông tin", JOptionPane.INFORMATION_MESSAGE);
        });
        menuTroGiup.add(itemAbout);

        menuBar.add(menuHeThong);
        menuBar.add(menuDieuHuong);
        menuBar.add(menuTroGiup);
        this.setJMenuBar(menuBar);

        this.add(tabs, BorderLayout.CENTER);
        this.add(taoStatusBar(), BorderLayout.SOUTH);

        // Tab Change Listener
        tabs.addChangeListener(e -> {
            int selectedIndex = tabs.getSelectedIndex();
            String titleTab = tabs.getTitleAt(selectedIndex);
            lblStatus.setText("Đang xem: " + titleTab);
        });

        this.setVisible(true);
    }

    private JPanel taoStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        panel.setPreferredSize(new Dimension(this.getWidth(), 30));

        lblStatus = new JLabel("Hệ thống sẵn sàng");
        lblStatus.setIcon(new FlatSVGIcon("resources/icons/check-circle.svg", 14, 14));
        lblStatus.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        String name = controller.AppSession.getInstance().getTenNV();
        lblUser = new JLabel("Nhân viên: " + name);
        lblUser.setIcon(new FlatSVGIcon("resources/icons/user.svg", 14, 14));
        lblUser.setHorizontalAlignment(SwingConstants.CENTER);

        lblTime = new JLabel();
        lblTime.setIcon(new FlatSVGIcon("resources/icons/clock.svg", 14, 14));
        lblTime.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss | dd/MM/yyyy");
        Timer timer = new Timer(1000, e -> lblTime.setText(sdf.format(new Date())));
        timer.start();
        lblTime.setText(sdf.format(new Date()));

        panel.add(lblStatus, BorderLayout.WEST);
        panel.add(lblUser, BorderLayout.CENTER);
        panel.add(lblTime, BorderLayout.EAST);

        return panel;
    }

    public static void main(String[] args) {
        setupAesthetics();

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

    private static void setupAesthetics() {
        try {
            com.formdev.flatlaf.themes.FlatMacDarkLaf.setup();
            
            // 2. Thiết lập Typography: Ưu tiên SF Pro, Fallback về Inter
            try {
                // Thử nạp Font hệ thống SF Pro trước (Main Typeface)
                Font mainFont = new Font("SF Pro Text", Font.PLAIN, 13);
                
                // Nếu font hệ thống không khả dụng (Java mặc định trả về Dialog), nạp Inter từ resources
                if (mainFont.getFamily().equalsIgnoreCase("Dialog") || mainFont.getFamily().equalsIgnoreCase("SansSerif")) {
                    InputStream is = MainFrame.class.getResourceAsStream("/resources/fonts/InterVariable.ttf");
                    if (is != null) {
                        Font inter = Font.createFont(Font.TRUETYPE_FONT, is);
                        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(inter);
                        UIManager.put("defaultFont", inter.deriveFont(13f));
                    }
                } else {
                    UIManager.put("defaultFont", mainFont);
                }
            } catch (Exception e) {
                System.err.println("Lỗi nạp Typography.");
            }

            // 3. Tối ưu tương phản Icon (Global Color Filter)
            // Lấy instance toàn cục và thực hiện ánh xạ màu cho Dark Mode
            FlatSVGIcon.ColorFilter filter = FlatSVGIcon.ColorFilter.getInstance();
            filter.add(Color.BLACK, Color.BLACK, Color.WHITE);
            filter.add(new Color(80, 80, 80), new Color(80, 80, 80), new Color(245, 245, 245)); 
            filter.add(new Color(150, 150, 150), new Color(150, 150, 150), new Color(220, 220, 220));

            // 4. Cấu hình MacOS Accent & UI Properties
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("Table.rowHeight", 30);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.intercellSpacing", new Dimension(0, 1));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
