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

    public BangDieuKhienGiaoDich(QuanLyKho qlk, QuanLySanPham qlsp) {
        this.quanLiKho = qlk;
        this.quanLiSanPham = qlsp;
        setLayout(new BorderLayout(10, 10));

        model = new DefaultTableModel(new String[]{"Mã sp", "Tên sp", "Loại", "Tồn kho", "Giá gốc"}, 0);
        bang = new JTable(model);
        bang.setRowHeight(25);
        bang.setAutoCreateRowSorter(true);
        add(new JScrollPane(bang), BorderLayout.CENTER);

        JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnNhapKho = new JButton("Thực hiện nhập kho");
        JButton btnBaoCao = new JButton("In báo cáo tồn kho");

        btnNhapKho.addActionListener(e -> {
            ThemGiaoDich dialog = new ThemGiaoDich(SwingUtilities.getWindowAncestor(this), quanLiKho, quanLiSanPham);
            dialog.setVisible(true);
            taiDuLieuVaoBang();
        });

        btnBaoCao.addActionListener(e -> {
            new BaoCaoTonKho(SwingUtilities.getWindowAncestor(this), quanLiKho).setVisible(true);
        });

        pnlNut.add(btnNhapKho); pnlNut.add(btnBaoCao);
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
