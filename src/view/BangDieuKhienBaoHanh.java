package view;

import dao.PhieuBaoHanhDAO;
import model.PhieuBaoHanh;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import util.DinhDang;
import com.formdev.flatlaf.extras.FlatSVGIcon;

public class BangDieuKhienBaoHanh extends JPanel {
    private PhieuBaoHanhDAO pbhDAO = new PhieuBaoHanhDAO();
    private JTextField txtSearch;
    private javax.swing.table.TableRowSorter<DefaultTableModel> sorter;
    private JTable tblShow;
    private DefaultTableModel model;
    private JButton btnLapPhieu, btnXuLy, btnRefresh;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public BangDieuKhienBaoHanh() {
        initComponents();
        initEvents();
        taiDuLieu();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Top Panel (Actions & Search) ---
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));

        btnLapPhieu = new JButton("Lập phiếu tiếp nhận");
        btnLapPhieu.setIcon(new FlatSVGIcon("resources/icons/plus-circle.svg", 16, 16));
        btnLapPhieu.setFont(btnLapPhieu.getFont().deriveFont(Font.BOLD));
        btnLapPhieu.setBackground(new Color(0, 122, 255));
        btnLapPhieu.setForeground(Color.WHITE);

        btnXuLy = new JButton("Xử lý & Trả máy");
        btnXuLy.setIcon(new FlatSVGIcon("resources/icons/check-circle.svg", 16, 16));
        btnXuLy.setFont(btnXuLy.getFont().deriveFont(Font.BOLD));
        btnXuLy.setBackground(new Color(48, 209, 88));
        btnXuLy.setForeground(Color.BLACK);

        btnRefresh = new JButton("Làm mới");
        btnRefresh.setIcon(new FlatSVGIcon("resources/icons/refresh-cw.svg", 14, 14));

        txtSearch = new JTextField(20);
        txtSearch.putClientProperty("JTextField.placeholderText", "🔍 Nhập mã phiếu hoặc sêri...");

        panelTop.add(btnLapPhieu);
        panelTop.add(btnXuLy);
        panelTop.add(new JLabel(" | "));
        panelTop.add(new JLabel("Tìm kiếm: "));
        panelTop.add(txtSearch);
        panelTop.add(btnRefresh);
        add(panelTop, BorderLayout.NORTH);

        // --- Table Section ---
        String[] columns = { "Mã phiếu", "Mã sêri", "Mã KH", "Ngày tiếp nhận", "Ngày hẹn trả", "Trạng thái",
                "Chi phí" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblShow = new JTable(model);
        tblShow.setRowHeight(30);
        tblShow.getTableHeader().setReorderingAllowed(false);

        sorter = new javax.swing.table.TableRowSorter<>(model);
        tblShow.setRowSorter(sorter);

        add(new JScrollPane(tblShow), BorderLayout.CENTER);
    }

    private void initEvents() {
        // --- Real-time Filter ---
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
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
                String val = txtSearch.getText().trim();
                if (val.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + val, 0, 1));
                }
            }
        });

        // --- Actions ---
        btnLapPhieu.addActionListener(e -> {
            TiepNhanBaoHanhDialog dialog = new TiepNhanBaoHanhDialog((Frame) SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            taiDuLieu();
        });

        btnXuLy.addActionListener(e -> {
            int row = tblShow.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu bảo hành!");
                return;
            }
            int modelRow = tblShow.convertRowIndexToModel(row);
            String maPBH = (String) model.getValueAt(modelRow, 0);
            PhieuBaoHanh selectedPBH = timPhieuTrongDanhSach(maPBH);
            if (selectedPBH != null) {
                ChiTietBaoHanhDialog dialog = new ChiTietBaoHanhDialog((Frame) SwingUtilities.getWindowAncestor(this),
                        selectedPBH);
                dialog.setVisible(true);
                taiDuLieu();
            }
        });

        btnRefresh.addActionListener(e -> taiDuLieu());
    }

    public void taiDuLieu() {
        model.setRowCount(0);
        List<PhieuBaoHanh> ds = pbhDAO.getAll();
        for (PhieuBaoHanh p : ds) {
            model.addRow(new Object[] {
                    p.getMaPBH(),
                    p.getMaSeri(),
                    p.getMaKH(),
                    sdf.format(p.getNgayTiepNhan()),
                    p.getNgayTraDuKien() != null ? sdf.format(p.getNgayTraDuKien()) : "N/A",
                    p.getTinhTrang(),
                    DinhDang.tien(p.getChiPhi())
            });
        }
    }

    private PhieuBaoHanh timPhieuTrongDanhSach(String maPBH) {
        return pbhDAO.getAll().stream()
                .filter(p -> p.getMaPBH().equals(maPBH))
                .findFirst().orElse(null);
    }
}
