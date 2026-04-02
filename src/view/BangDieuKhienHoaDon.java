package view;

import dao.HoaDonDAO;
import model.HoaDon;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import util.DinhDang;

public class BangDieuKhienHoaDon extends JPanel {
    private JTable bangHoaDon;
    private DefaultTableModel moHinhBang;
    private SimpleDateFormat dinhDangNgay = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public BangDieuKhienHoaDon() {
        this.setLayout(new BorderLayout(10, 10));

        // 1. Panel chứa nút thao tác phía trên
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton btnLapHoaDon = new JButton("+ Lập hóa đơn bán hàng");
        btnLapHoaDon.setFont(new Font("Inter", Font.BOLD, 14));
        btnLapHoaDon.setBackground(new Color(0, 120, 215));
        btnLapHoaDon.setForeground(Color.WHITE);
        btnLapHoaDon.addActionListener(e -> {
            TaoHoaDonDialog dialog = new TaoHoaDonDialog(SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            taiDuLieu(); // Cập nhật lại danh sách ngay sau khi lập hóa đơn
        });

        JButton btnRefresh = new JButton("↻ Làm mới");
        btnRefresh.setFont(new Font("Inter", Font.PLAIN, 14));
        btnRefresh.addActionListener(e -> taiDuLieu());

        panelTop.add(btnLapHoaDon);
        panelTop.add(btnRefresh);
        this.add(panelTop, BorderLayout.NORTH);

        // 2. Thiết lập bảng Hóa Đơn
        String[] columns = {"Mã hoá đơn", "Thời gian", "Khách hàng", "Nhân viên", "Tổng tiền"};
        moHinhBang = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        bangHoaDon = new JTable(moHinhBang);
        bangHoaDon.setRowHeight(25);
        bangHoaDon.setAutoCreateRowSorter(true);
        this.add(new JScrollPane(bangHoaDon), BorderLayout.CENTER);

        // 3. Tải dữ liệu từ DB
        taiDuLieu();
    }

    public void taiDuLieu() {
        moHinhBang.setRowCount(0); 
        
        try {
            HoaDonDAO dao = new HoaDonDAO();
            List<HoaDon> danhSach = dao.getAllHoaDon();
            
            for (HoaDon hd : danhSach) {
                String tienFormatted = DinhDang.tien(hd.getTongTien());
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
