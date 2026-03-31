package view;

import dao.HoaDonDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class BangDieuKhienBaoCao extends JPanel {
    private JTable bangBaoCao;
    private DefaultTableModel moHinhBang;
    private JLabel lblTongCong;
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private NumberFormat ndTien = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

    public BangDieuKhienBaoCao() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("BÁO CÁO DOANH THU THEO THÁNG", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 102, 204));
        this.add(lblTitle, BorderLayout.NORTH);

        String[] tenCot = {"Tháng / Năm", "Tổng Doanh Thu (VNĐ)"};
        moHinhBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        bangBaoCao = new JTable(moHinhBang);
        bangBaoCao.setRowHeight(30);
        bangBaoCao.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        this.add(new JScrollPane(bangBaoCao), BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTongCong = new JLabel("Tổng doanh thu: 0 VNĐ");
        lblTongCong.setFont(new Font("Arial", Font.BOLD, 16));
        JButton btnRefresh = new JButton("Tải lại báo cáo");
        
        btnRefresh.addActionListener(e -> taiDuLieu());
        
        pnlBottom.add(lblTongCong);
        pnlBottom.add(btnRefresh);
        this.add(pnlBottom, BorderLayout.SOUTH);

        taiDuLieu();
    }

    public void taiDuLieu() {
        moHinhBang.setRowCount(0);
        Map<String, Double> data = hoaDonDAO.thongKeDoanhThuTheoThang();
        double tong = 0;
        
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            moHinhBang.addRow(new Object[]{
                entry.getKey(),
                ndTien.format(entry.getValue())
            });
            tong += entry.getValue();
        }
        
        lblTongCong.setText("Tổng cộng toàn thời gian: " + ndTien.format(tong));
    }
}
