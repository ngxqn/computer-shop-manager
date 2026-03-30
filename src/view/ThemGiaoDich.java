package view;

import controller.QuanLyKho;
import controller.QuanLySanPham;
import model.HoaDon;
import model.SanPham;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ThemGiaoDich extends JDialog {
    private QuanLyKho quanLiKho;
    private QuanLySanPham qlSanPham;
    private JTextField txtKH, txtNV, txtMaGD;
    private JComboBox<String> cbLoaiKho;
    private JTable bang;
    private DefaultTableModel model;
    private List<SanPham> gioHang = new ArrayList<>();
    private boolean laCheDoBanHang = false;

    // CONSTRUCTOR 1: Nhận 3 tham số (Dùng cho tab Kho)
    public ThemGiaoDich(Window owner, QuanLyKho qlk, QuanLySanPham qlsp) {
        super(owner, "GIAO DỊCH KHO NỘI BỘ", ModalityType.APPLICATION_MODAL);
        this.quanLiKho = qlk;
        this.qlSanPham = qlsp;
        this.laCheDoBanHang = false;
        thietLapGiaoDien();
    }

    // CONSTRUCTOR 2: Nhận 2 tham số (Dùng cho tab Hóa đơn - FIX LỖI CỦA BẠN)
    public ThemGiaoDich(Window owner, QuanLySanPham qlsp) {
        super(owner, "LẬP HÓA ĐƠN BÁN HÀNG", ModalityType.APPLICATION_MODAL);
        this.qlSanPham = qlsp;
        this.laCheDoBanHang = true;
        thietLapGiaoDien();
    }

    private void thietLapGiaoDien() {
        setLayout(new BorderLayout(10, 10));
        JPanel pnlTop = new JPanel(new GridLayout(0, 2, 5, 5));
        pnlTop.setBorder(BorderFactory.createTitledBorder("Thông tin chứng từ"));

        txtMaGD = new JTextField(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        txtMaGD.setEditable(false);
        pnlTop.add(new JLabel("Mã Số:")); pnlTop.add(txtMaGD);

        if (laCheDoBanHang) {
            txtKH = new JTextField("Khách vãng lai");
            txtNV = new JTextField();
            pnlTop.add(new JLabel("Tên Khách Hàng:")); pnlTop.add(txtKH);
            pnlTop.add(new JLabel("Mã NV Thu Ngân:")); pnlTop.add(txtNV);
        } else {
            cbLoaiKho = new JComboBox<>(new String[]{"Nhập Kho", "Xuất Kho (Ra Cửa Hàng)"});
            pnlTop.add(new JLabel("Loại Giao Dịch:")); pnlTop.add(cbLoaiKho);
        }

        model = new DefaultTableModel(new String[]{"Mã SP", "Tên", "Số Lượng"}, 0);
        bang = new JTable(model);

        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("Thêm SP");
        JButton btnOk = new JButton("Xác Nhận");

        btnAdd.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(this, "Nhập Mã Sản Phẩm:");
            SanPham sp = qlSanPham.timSanPhamTheoID(id);
            if (sp != null) {
                int sl = Integer.parseInt(JOptionPane.showInputDialog(this, "Số lượng:"));
                gioHang.add(new SanPham(sp.layID(), sp.layTen(), sp.layLoai(), sl, sp.layGiaGoc()));
                model.addRow(new Object[]{sp.layID(), sp.layTen(), sl});
            }
        });

        btnOk.addActionListener(e -> thucHienXacNhan());

        pnlBot.add(btnAdd); pnlBot.add(btnOk);
        add(pnlTop, BorderLayout.NORTH);
        add(new JScrollPane(bang), BorderLayout.CENTER);
        add(pnlBot, BorderLayout.SOUTH);
        setSize(550, 480);
        setLocationRelativeTo(null);
    }

    private void thucHienXacNhan() {
        if (laCheDoBanHang) {
            HoaDon hd = new HoaDon(txtMaGD.getText(), txtKH.getText(), txtNV.getText());
            for (SanPham s : gioHang) {
                if (qlSanPham.thucHienBanHangTruTrucTiep(s.layID(), s.laySoLuong())) {
                    hd.themSanPham(s);
                }
            }
            // Ghi lịch sử vào DSHoaDon.txt
            qlSanPham.ghiLichSuHoaDon(hd);
            new ChiTietGiaoDich(this, hd).setVisible(true);
        } else {
            String loai = cbLoaiKho.getSelectedItem().toString();
            for (SanPham s : gioHang) {
                if (loai.equals("Nhập Kho")) quanLiKho.thucHienNhapKhoGiaoDien("K01", s.layID(), s.laySoLuong());
                else quanLiKho.thucHienXuatKhoNoiBo(s.layID(), s.laySoLuong());
            }
            JOptionPane.showMessageDialog(this, "Giao dịch thành công!");
        }
        dispose();
    }
}
