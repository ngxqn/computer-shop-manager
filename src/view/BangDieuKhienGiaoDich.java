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
    private QuanLyKho quanLiKho;
    private QuanLySanPham quanLiSanPham;
    private JTable bang;
    private DefaultTableModel model;
    private JTextField txtTimKiem;
    private javax.swing.table.TableRowSorter<DefaultTableModel> sorter;

    public BangDieuKhienGiaoDich(QuanLyKho qlk, QuanLySanPham qlsp) {
        this.quanLiKho = qlk;
        this.quanLiSanPham = qlsp;
        setLayout(new BorderLayout(10, 10));

        model = new DefaultTableModel(new String[]{"Mã sp", "Tên sp", "Loại", "Tồn kho", "Giá gốc"}, 0);
        bang = new JTable(model);
        bang.setRowHeight(25);
        bang.setAutoCreateRowSorter(true);
        
        // --- UX: Lọc thời gian thực ---
        sorter = new javax.swing.table.TableRowSorter<>(model);
        bang.setRowSorter(sorter);

        add(new JScrollPane(bang), BorderLayout.CENTER);

        JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton nutNhapHang = new JButton("+ Nhập Thêm Hàng");
        
        txtTimKiem = new JTextField(15);
        txtTimKiem.putClientProperty("JTextField.placeholderText", "🔍 Nhập mã hoặc tên sản phẩm...");
        
        // --- UX: Lắng nghe lọc ---
        txtTimKiem.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { loc(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { loc(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { loc(); }
            
            private void loc() {
                String val = txtTimKiem.getText().trim();
                if (val.isEmpty()) sorter.setRowFilter(null);
                else sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + val, 0, 1));
            }
        });

        JButton nutTimKiem = new JButton("Tìm kiếm");
        nutTimKiem.addActionListener(e -> {
            String val = txtTimKiem.getText().trim();
            if (val.isEmpty()) sorter.setRowFilter(null);
            else sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + val, 0, 1));
        });
        JButton btnBaoCao = new JButton("In báo cáo tồn kho");

        nutNhapHang.addActionListener(e -> {
            ThemGiaoDich dialog = new ThemGiaoDich(SwingUtilities.getWindowAncestor(this), quanLiKho, quanLiSanPham);
            dialog.setVisible(true);
            taiDuLieuVaoBang();
        });

        btnBaoCao.addActionListener(e -> {
            new BaoCaoTonKho(SwingUtilities.getWindowAncestor(this), quanLiKho).setVisible(true);
        });

        pnlNut.add(nutNhapHang); pnlNut.add(btnBaoCao);
        add(pnlNut, BorderLayout.SOUTH);
        taiDuLieuVaoBang();
    }

    public void taiDuLieuVaoBang() {
        model.setRowCount(0);
        List<SanPham> dssp = quanLiSanPham.getSanPhamList();
        
        for (SanPham sp : dssp) {
            int tonKho = quanLiKho.layTonKhoThucTe(sp.getMaSP());
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
