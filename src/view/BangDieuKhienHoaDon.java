package view;

import controller.QuanLySanPham;
import dao.HoaDonDAO;
import model.HoaDon;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class BangDieuKhienHoaDon extends JPanel {
    private JTable bangHoaDon;
    private DefaultTableModel moHinhBang;
    private QuanLySanPham quanLySanPham;
    private DecimalFormat dinhDangTien = new DecimalFormat("###,###,### VNĐ");
    private SimpleDateFormat dinhDangNgay = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public BangDieuKhienHoaDon(QuanLySanPham qlsp) {
        this.quanLySanPham = qlsp;
        this.setLayout(new BorderLayout(10, 10));

        // 1. Panel chứa nút thao tác phía trên
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton nutLapHoaDon = new JButton("Lập Hóa Đơn Bán Hàng (POS)");
        nutLapHoaDon.setFont(new Font("Arial", Font.BOLD, 14));
        nutLapHoaDon.setBackground(new Color(0, 120, 215));
        nutLapHoaDon.setForeground(Color.WHITE);
        
        nutLapHoaDon.addActionListener(e -> {
            TaoHoaDonDialog dialog = new TaoHoaDonDialog(SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            taiDuLieu(); // Cập nhật lại danh sách ngay sau khi lập hóa đơn
        });

        panelTop.add(nutLapHoaDon);
        this.add(panelTop, BorderLayout.NORTH);

        // 2. Thiết lập bảng Hóa Đơn
        String[] columns = {"Mã HD", "Ngày Giờ", "Khách Hàng", "Nhân Viên", "Tổng Tiền"};
        moHinhBang = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        bangHoaDon = new JTable(moHinhBang);
        this.add(new JScrollPane(bangHoaDon), BorderLayout.CENTER);

        // 3. Nút làm mới ở dưới
        JButton nutLamMoi = new JButton("Làm Mới Lịch Sử");
        nutLamMoi.addActionListener(e -> taiDuLieu());
        this.add(nutLamMoi, BorderLayout.SOUTH);

        // 4. Tải dữ liệu từ DB
        taiDuLieu();
    }

    public void taiDuLieu() {
        moHinhBang.setRowCount(0); 
        
        try {
            HoaDonDAO dao = new HoaDonDAO();
            List<HoaDon> danhSach = dao.getAllHoaDon();
            
            for (HoaDon hd : danhSach) {
                String tienFormatted = dinhDangTien.format(hd.getTongTien());
                String ngayFormatted = hd.getNgayLap() != null ? dinhDangNgay.format(hd.getNgayLap()) : "";
                
                moHinhBang.addRow(new Object[]{
                    hd.getMaHD(),
                    ngayFormatted,
                    hd.getMaKH(),
                    hd.getMaNV(),
                    tienFormatted
                });
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi lấy danh sách hóa đơn từ DB: " + e.getMessage());
        }
    }
}
