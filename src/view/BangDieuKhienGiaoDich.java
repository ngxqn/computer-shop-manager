package view;

import controller.BaoCaoTonKho;
import controller.QuanLyKho;
import controller.QuanLySanPham;
import model.SanPham;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BangDieuKhienGiaoDich extends JPanel {
    private QuanLyKho quanLiKho;
    private QuanLySanPham quanLiSanPham;
    private JTable bang;
    private DefaultTableModel model;

    public BangDieuKhienGiaoDich(QuanLyKho qlk, QuanLySanPham qlsp) {
        this.quanLiKho = qlk;
        this.quanLiSanPham = qlsp;
        setLayout(new BorderLayout(10, 10));

        model = new DefaultTableModel(new String[]{"Mã SP", "Tên SP", "Loại", "Tồn Kho", "Giá Gốc"}, 0);
        bang = new JTable(model);
        add(new JScrollPane(bang), BorderLayout.CENTER);

        JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnNhapXuat = new JButton("Thực Hiện Nhập/Xuất Kho");
        JButton btnBaoCao = new JButton("In Báo Cáo Tồn Kho");

        // FIX LỖI TẠI ĐÂY: Gọi đúng Constructor 3 tham số
        btnNhapXuat.addActionListener(e -> {
            ThemGiaoDich dialog = new ThemGiaoDich(SwingUtilities.getWindowAncestor(this), quanLiKho, quanLiSanPham);
            dialog.setVisible(true);
            taiDuLieuVaoBang();
        });

        btnBaoCao.addActionListener(e -> {
            new BaoCaoTonKho(SwingUtilities.getWindowAncestor(this), quanLiKho).setVisible(true);
        });

        pnlNut.add(btnNhapXuat); pnlNut.add(btnBaoCao);
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
                sp.getGiaBan()
            });
        }
    }
}
