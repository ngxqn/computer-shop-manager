package view;

import controller.QuanLyKho;
import controller.QuanLySanPham;
import model.SanPham;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.UUID;

public class ThemGiaoDich extends JDialog {
    private QuanLyKho quanLiKho;
    private QuanLySanPham qlSanPham;
    private JTextField txtMaGD;
    private JComboBox<String> cbLoaiKho;
    private JTable bang;
    private DefaultTableModel model;

    public ThemGiaoDich(Window owner, QuanLyKho qlk, QuanLySanPham qlsp) {
        super(owner, "GIAO DỊCH KHO NỘI BỘ", ModalityType.APPLICATION_MODAL);
        this.quanLiKho = qlk;
        this.qlSanPham = qlsp;
        thietLapGiaoDien();
    }

    private void thietLapGiaoDien() {
        setLayout(new BorderLayout(10, 10));
        JPanel pnlTop = new JPanel(new GridLayout(0, 2, 5, 5));
        pnlTop.setBorder(BorderFactory.createTitledBorder("Thông tin chứng từ"));

        txtMaGD = new JTextField(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        txtMaGD.setEditable(false);
        pnlTop.add(new JLabel("Mã Số:")); pnlTop.add(txtMaGD);

        cbLoaiKho = new JComboBox<>(new String[]{"Nhập Kho", "Xuất Kho (Ra Cửa Hàng)"});
        pnlTop.add(new JLabel("Loại Giao Dịch:")); pnlTop.add(cbLoaiKho);

        model = new DefaultTableModel(new String[]{"Mã SP", "Tên", "Số Lượng"}, 0);
        bang = new JTable(model);

        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("Thêm SP");
        JButton btnOk = new JButton("Xác Nhận");

        btnAdd.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(this, "Nhập Mã Sản Phẩm:");
            if (id != null && !id.trim().isEmpty()) {
                SanPham sp = qlSanPham.timSanPhamTheoID(id);
                if (sp != null) {
                    try {
                        String slStr = JOptionPane.showInputDialog(this, "Số lượng:");
                        if (slStr != null && !slStr.trim().isEmpty()) {
                            int sl = Integer.parseInt(slStr);
                            model.addRow(new Object[]{sp.getMaSP(), sp.getTenSP(), sl});
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy Mã Sản Phẩm này!");
                }
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
        String loai = cbLoaiKho.getSelectedItem().toString();
        for (int i = 0; i < model.getRowCount(); i++) {
            String id = (String) model.getValueAt(i, 0);
            int sl = (Integer) model.getValueAt(i, 2);
            if (loai.equals("Nhập Kho")) {
                quanLiKho.thucHienNhapKhoGiaoDien("K01", id, sl);
            } else {
                quanLiKho.thucHienXuatKhoNoiBo(id, sl);
            }
        }
        JOptionPane.showMessageDialog(this, "Giao dịch kho thành công!");
        dispose();
    }
}
