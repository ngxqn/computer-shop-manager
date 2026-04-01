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
    private BangDieuKhienNhanVien nhanVienPanel;
    private BangDieuKhienKhachHang khachHangPanel;
    private BangDieuKhienBaoHanh baoHanhPanel; // MỚI

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
        hoaDonPanel = new BangDieuKhienHoaDon(qlsp);
        sanPhamPanel = new BangDieuKhienSanPham(qlsp);
        nhanVienPanel = new BangDieuKhienNhanVien(qlnv);
        khachHangPanel = new BangDieuKhienKhachHang(qlkh);
        baoHanhPanel = new BangDieuKhienBaoHanh(); // MỚI

        // --- 4. Thêm các Tab vào JTabbedPane ---
        tabs.addTab("Kho Nội Bộ", giaoDichPanel);
        tabs.addTab("Bán Hàng & Hóa Đơn", hoaDonPanel);
        tabs.addTab("Quản Lý Bảo Hành", baoHanhPanel); // MỚI
        tabs.addTab("Sản Phẩm Cửa Hàng", sanPhamPanel);
        tabs.addTab("Quản Lý Nhân Viên", nhanVienPanel);
        tabs.addTab("Quản Lý Khách Hàng", khachHangPanel);
        
        BangDieuKhienBaoCao baoCaoPanel = new BangDieuKhienBaoCao();
        tabs.addTab("Báo Cáo Doanh Thu", baoCaoPanel);

        // --- 5. THIẾT LẬP MENU BAR (PHASE 5) ---
        JMenuBar menuBar = new JMenuBar();
        
        JMenu menuHeThong = new JMenu("Hệ Thống");
        JMenuItem itemThoat = new JMenuItem("Thoát");
        itemThoat.addActionListener(e -> System.exit(0));
        menuHeThong.add(itemThoat);
        
        JMenu menuNghiepVu = new JMenu("Nghiệp Vụ");
        JMenuItem itemBaoHanh = new JMenuItem("Tiếp nhận Bảo hành");
        itemBaoHanh.addActionListener(e -> {
            new TiepNhanBaoHanhDialog(this).setVisible(true);
        });
        menuNghiepVu.add(itemBaoHanh);
        
        JMenu menuBaoCao = new JMenu("Báo Cáo");
        JMenuItem itemDoanhThu = new JMenuItem("Doanh thu tháng");
        itemDoanhThu.addActionListener(e -> tabs.setSelectedComponent(baoCaoPanel));
        menuBaoCao.add(itemDoanhThu);
        
        menuBar.add(menuHeThong);
        menuBar.add(menuNghiepVu);
        menuBar.add(menuBaoCao);
        this.setJMenuBar(menuBar);

        // --- 6. BỘ LẮNG NGHE SỰ KIỆN TỰ ĐỘNG LÀM MỚI (REFRESH) ---
        tabs.addChangeListener(e -> {
            int selectedIndex = tabs.getSelectedIndex();
            String titleTab = tabs.getTitleAt(selectedIndex);

            if (titleTab.equals("Sản Phẩm Cửa Hàng")) {
                sanPhamPanel.taiDuLieuVaoBang();
            } else if (titleTab.equals("Kho Nội Bộ")) {
                giaoDichPanel.taiDuLieuVaoBang();
            } else if (titleTab.equals("Bán Hàng & Hóa Đơn")) {
                hoaDonPanel.taiDuLieu();
            } else if (titleTab.equals("Quản Lý Nhân Viên")) {
                qlnv.refreshData();
            } else if (titleTab.equals("Quản Lý Khách Hàng")) {
                qlkh.refreshData();
            } else if (titleTab.equals("Báo Cáo Doanh Thu")) {
                baoCaoPanel.taiDuLieu();
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
