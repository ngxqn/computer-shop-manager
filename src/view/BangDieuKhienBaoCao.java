package view;

import dao.HoaDonDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;
import util.DinhDang;

public class BangDieuKhienBaoCao extends JPanel {
    private JTable bangBaoCao;
    private DefaultTableModel moHinhBang;
    private JLabel lblTongCong;
    private JButton btnRefresh;
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();

    public BangDieuKhienBaoCao() {
        initComponents();
        initEvents();
        taiDuLieu();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel("BÁO CÁO DOANH THU THEO THÁNG", JLabel.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 22f));
        lblTitle.setForeground(new Color(0, 102, 204));
        add(lblTitle, BorderLayout.NORTH);

        String[] tenCot = {"Tháng/năm", "Tổng doanh thu"};
        moHinhBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        bangBaoCao = new JTable(moHinhBang);
        bangBaoCao.setRowHeight(30);
        bangBaoCao.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(bangBaoCao), BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        lblTongCong = new JLabel("Tổng doanh thu: 0");
        lblTongCong.setFont(lblTongCong.getFont().deriveFont(Font.BOLD, 14f));
        btnRefresh = new JButton("Làm mới");
        
        pnlBottom.add(lblTongCong);
        pnlBottom.add(btnRefresh);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void initEvents() {
        btnRefresh.addActionListener(e -> taiDuLieu());
    }

    public void taiDuLieu() {
        moHinhBang.setRowCount(0);
        Map<String, Double> data = hoaDonDAO.thongKeDoanhThuTheoThang();
        double tong = 0;
        
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            moHinhBang.addRow(new Object[]{
                entry.getKey(),
                DinhDang.tien(entry.getValue())
            });
            tong += entry.getValue();
        }
        
        lblTongCong.setText("Tổng cộng toàn thời gian: " + DinhDang.tien(tong));
    }
}
