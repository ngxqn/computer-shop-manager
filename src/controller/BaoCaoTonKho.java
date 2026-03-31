package controller;

import model.SanPham;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BaoCaoTonKho extends JDialog {
    public BaoCaoTonKho(Window owner, List<SanPham> dssp) {
        super(owner, "BÁO CÁO TỒN KHO CHI TIẾT", ModalityType.APPLICATION_MODAL);
        this.setSize(800, 500);
        this.setLocationRelativeTo(owner);
        this.setLayout(new BorderLayout(10, 10));

        // 1. Tạo bảng dữ liệu
        String[] columns = {"Mã SP", "Tên Sản Phẩm", "Loại", "Tồn Kho", "Giá Trị Tồn"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        NumberFormat nf = NumberFormat.getInstance(Locale.of("vi", "VN"));
        double tongGiaTri = 0;

        for (SanPham sp : dssp) {
            int soLuong = 0; // Tồn kho sẽ được lấy từ SERISANPHAM ở phase tiếp theo
            double giaTri = sp.getGiaBan() * soLuong;
            tongGiaTri += giaTri;
            model.addRow(new Object[]{
                    sp.getMaSP(), sp.getTenSP(), sp.getLoaiSP(),
                    soLuong, nf.format(giaTri) + " VNĐ"
            });
        }

        // 2. Panel tổng cộng
        JLabel lblTong = new JLabel("TỔNG GIÁ TRỊ KHO: " + nf.format(tongGiaTri) + " VNĐ  ");
        lblTong.setFont(new Font("Arial", Font.BOLD, 16));
        lblTong.setHorizontalAlignment(SwingConstants.RIGHT);

        this.add(new JLabel("HỆ THỐNG QUẢN LÝ KHO - TEAM 03", SwingConstants.CENTER), BorderLayout.NORTH);
        this.add(new JScrollPane(table), BorderLayout.CENTER);
        this.add(lblTong, BorderLayout.SOUTH);
    }
}
