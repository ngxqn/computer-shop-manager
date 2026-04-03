package view;

import controller.BaoCaoTonKho;
import controller.QuanLyKho;
import controller.QuanLySanPham;
import model.SanPham;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import util.DinhDang;

public class BangDieuKhienGiaoDich extends JPanel {
    private QuanLyKho quanLyKho;
    private QuanLySanPham quanLySanPham;
    private JTable tblShow;
    private DefaultTableModel model;
    private JTextField txtTimKiem;
    private JButton btnNhapHang, btnBaoCao;
    private javax.swing.table.TableRowSorter<DefaultTableModel> sorter;

    public BangDieuKhienGiaoDich(QuanLyKho qlk, QuanLySanPham qlsp) {
        this.quanLyKho = qlk;
        this.quanLySanPham = qlsp;

        initComponents();
        initEvents();
        
        taiDuLieuVaoBang();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Table Section ---
        model = new DefaultTableModel(new String[]{
            "Mã SP", "Tên sản phẩm", "Loại", "Tồn kho", "Giá bán"
        }, 0);
        
        tblShow = new JTable(model);
        tblShow.setRowHeight(30);
        tblShow.getTableHeader().setReorderingAllowed(false);
        
        sorter = new javax.swing.table.TableRowSorter<>(model);
        tblShow.setRowSorter(sorter);

        add(new JScrollPane(tblShow), BorderLayout.CENTER);

        // --- Search Section (North) ---
        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        txtTimKiem = new JTextField(25);
        txtTimKiem.putClientProperty("JTextField.placeholderText", "🔍 Nhập mã hoặc tên sản phẩm để lọc nhanh...");
        
        btnNhapHang = new JButton("Nhập hàng");
        btnNhapHang.setFont(btnNhapHang.getFont().deriveFont(Font.BOLD));
        btnNhapHang.setBackground(new Color(0, 120, 215));
        btnNhapHang.setIcon(UIManager.getIcon("FileView.floppyDriveIcon")); // Placeholder/Default icon if needed

        btnBaoCao = new JButton("Báo cáo tồn kho");
        
        panelHeader.add(btnNhapHang);
        panelHeader.add(btnBaoCao);
        panelHeader.add(new JLabel(" | "));
        panelHeader.add(new JLabel("Tìm kiếm: "));
        panelHeader.add(txtTimKiem);
        add(panelHeader, BorderLayout.NORTH);
    }

    private void initEvents() {
        // --- Real-time Filter ---
        txtTimKiem.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { loc(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { loc(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { loc(); }
            
            private void loc() {
                String val = txtTimKiem.getText().trim();
                if (val.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    // Filter code 0 (MaSP) and 1 (TenSP)
                    sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + val, 0, 1));
                }
            }
        });

        // --- New Inventory ---
        btnNhapHang.addActionListener(e -> {
            ThemGiaoDich dialog = new ThemGiaoDich(
                SwingUtilities.getWindowAncestor(this), 
                quanLyKho, 
                quanLySanPham
            );
            dialog.setVisible(true);
            taiDuLieuVaoBang();
        });

        // --- Report ---
        btnBaoCao.addActionListener(e -> {
            new BaoCaoTonKho(
                SwingUtilities.getWindowAncestor(this), 
                quanLyKho
            ).setVisible(true);
        });
    }

    public void taiDuLieuVaoBang() {
        model.setRowCount(0);
        List<SanPham> dssp = quanLySanPham.getSanPhamList();
        
        for (SanPham sp : dssp) {
            int tonKho = quanLyKho.layTonKhoThucTe(sp.getMaSP());
            model.addRow(new Object[]{
                sp.getMaSP(), 
                sp.getTenSP(), 
                sp.getLoaiSP(), 
                tonKho, 
                DinhDang.tien(sp.getGiaBan())
            });
        }
    }
}
