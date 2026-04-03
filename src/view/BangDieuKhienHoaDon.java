package view;

import dao.HoaDonDAO;
import model.HoaDon;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import util.DinhDang;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.text.SimpleDateFormat;

public class BangDieuKhienHoaDon extends JPanel {
    private JTable bangHoaDon;
    private DefaultTableModel moHinhBang;
    private JTextField fieldSearch;
    private JButton btnLapHoaDon, btnRefresh;
    private SimpleDateFormat dinhDangNgay = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private javax.swing.table.TableRowSorter<DefaultTableModel> sorter;

    public BangDieuKhienHoaDon() {
        initComponents();
        initEvents();
        taiDuLieu();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Top Panel (North) ---
        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));

        btnLapHoaDon = new JButton("Lập hóa đơn");
        btnLapHoaDon.setIcon(new FlatSVGIcon("resources/icons/plus-circle.svg", 16, 16));
        btnLapHoaDon.setFont(btnLapHoaDon.getFont().deriveFont(Font.BOLD));
        btnLapHoaDon.setBackground(new Color(0, 122, 255));
        btnLapHoaDon.setForeground(Color.WHITE);

        fieldSearch = new JTextField(20);
        fieldSearch.putClientProperty("JTextField.placeholderText", "🔍 Nhập mã hóa đơn để lọc...");

        btnRefresh = new JButton("Làm mới");
        btnRefresh.setIcon(new FlatSVGIcon("resources/icons/refresh-cw.svg", 14, 14));

        panelHeader.add(btnLapHoaDon);
        panelHeader.add(new JLabel(" | "));
        panelHeader.add(new JLabel("Tìm kiếm: "));
        panelHeader.add(fieldSearch);
        panelHeader.add(btnRefresh);
        add(panelHeader, BorderLayout.NORTH);

        // --- Table Section ---
        String[] columns = { "Mã hoá đơn", "Thời gian", "Khách hàng", "Nhân viên", "Tổng tiền" };
        moHinhBang = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bangHoaDon = new JTable(moHinhBang);
        bangHoaDon.setRowHeight(30);
        bangHoaDon.getTableHeader().setReorderingAllowed(false);

        sorter = new javax.swing.table.TableRowSorter<>(moHinhBang);
        bangHoaDon.setRowSorter(sorter);

        add(new JScrollPane(bangHoaDon), BorderLayout.CENTER);
    }

    private void initEvents() {
        // --- Real-time Filter ---
        fieldSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                loc();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                loc();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                loc();
            }

            private void loc() {
                String val = fieldSearch.getText().trim();
                if (val.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + val, 0)); // Filter by MaHD (index 0)
                }
            }
        });

        btnLapHoaDon.addActionListener(e -> {
            TaoHoaDonDialog dialog = new TaoHoaDonDialog(SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            taiDuLieu();
        });

        btnRefresh.addActionListener(e -> taiDuLieu());
    }

    public void taiDuLieu() {
        moHinhBang.setRowCount(0);

        try {
            HoaDonDAO dao = new HoaDonDAO();
            List<HoaDon> danhSach = dao.getAllHoaDon();

            for (HoaDon hd : danhSach) {
                String tienFormatted = DinhDang.tien(hd.getTongTien());
                String ngayFormatted = hd.getNgayLap() != null ? dinhDangNgay.format(hd.getNgayLap()) : "";

                moHinhBang.addRow(new Object[] {
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
