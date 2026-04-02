package controller;

import model.SanPham;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import util.DinhDang;

public class BaoCaoTonKho extends JDialog {
    public BaoCaoTonKho(Window owner, QuanLyKho qlKho) {
        super(owner, "BÁO CÁO TỒN KHO CHI TIẾT", ModalityType.APPLICATION_MODAL);
        this.setSize(800, 500);
        this.setLocationRelativeTo(owner);
        this.setLayout(new BorderLayout(10, 10));

        // 1. Tạo bảng dữ liệu
        String[] columns = {"Mã SP", "Tên Sản Phẩm", "Loại", "Tồn Kho", "Giá Trị Tồn"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        double tongGiaTri = 0;

        List<SanPham> dssp = qlKho.getQlSanPham().getSanPhamList();
        for (SanPham sp : dssp) {
            int soLuong = qlKho.layTonKhoThucTe(sp.getMaSP());
            // Giá trị tồn tối thiểu nên tính dựa trên giá nhập, nhưng tạm thời dùng giá bán * 0.8
            double giaTri = (sp.getGiaBan() * 0.8) * soLuong; 
            tongGiaTri += giaTri;
            model.addRow(new Object[]{
                    sp.getMaSP(), sp.getTenSP(), sp.getLoaiSP(),
                    soLuong, DinhDang.tien(giaTri)
            });
        }

        // 2. Panel tổng cộng
        JLabel lblTong = new JLabel("TỔNG GIÁ TRỊ TỒN (ƯỚC TÍNH): " + DinhDang.tien(tongGiaTri) + "  ");
        lblTong.setFont(new Font("Arial", Font.BOLD, 16));
        lblTong.setHorizontalAlignment(SwingConstants.RIGHT);

        this.add(new JLabel("HỆ THỐNG QUẢN LÝ KHO - JDBC LIVE", SwingConstants.CENTER), BorderLayout.NORTH);
        this.add(new JScrollPane(table), BorderLayout.CENTER);
        this.add(lblTong, BorderLayout.SOUTH);
    }
}
