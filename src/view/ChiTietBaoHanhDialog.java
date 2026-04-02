package view;

import dao.PhieuBaoHanhDAO;
import model.PhieuBaoHanh;
import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import util.DinhDang;

public class ChiTietBaoHanhDialog extends JDialog {
    private PhieuBaoHanh pbh;
    private PhieuBaoHanhDAO dao = new PhieuBaoHanhDAO();
    private JComboBox<String> cbTrangThai;
    private JTextField txtChiPhi;
    private JTextArea areaLoi;

    public ChiTietBaoHanhDialog(Window owner, PhieuBaoHanh pbh) {
        super(owner, "Chi tiết xử lý bảo hành: " + pbh.getMaPBH(), ModalityType.APPLICATION_MODAL);
        this.pbh = pbh;
        thietLapGiaoDien();
        
        // --- UX: Đóng bằng phím ESC ---
        this.getRootPane().registerKeyboardAction(e -> dispose(), 
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), 
            JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void thietLapGiaoDien() {
        this.setLayout(new BorderLayout(10, 10));
        this.setSize(450, 400);
        this.setLocationRelativeTo(getOwner());

        JPanel pnlMain = new JPanel(new GridLayout(6, 2, 5, 5));
        pnlMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pnlMain.add(new JLabel("Mã phiếu:"));
        pnlMain.add(new JLabel(pbh.getMaPBH()));

        pnlMain.add(new JLabel("Mã sêri:"));
        pnlMain.add(new JLabel(pbh.getMaSeri()));

        pnlMain.add(new JLabel("Mã khách hàng:"));
        pnlMain.add(new JLabel(pbh.getMaKH()));

        pnlMain.add(new JLabel("Trạng thái:"));
        cbTrangThai = new JComboBox<>(new String[]{"Đang xử lý", "Đã xong", "Đã trả khách"});
        cbTrangThai.setSelectedItem(pbh.getTinhTrang());
        pnlMain.add(cbTrangThai);

        pnlMain.add(new JLabel("Chi phí phát sinh:"));
        txtChiPhi = new JTextField(String.valueOf(pbh.getChiPhi()));
        pnlMain.add(txtChiPhi);

        pnlMain.add(new JLabel("Mô tả lỗi ban đầu:"));
        areaLoi = new JTextArea(6, 20);
        areaLoi.setText(pbh.getMoTaLoi());
        areaLoi.setEditable(false);
        pnlMain.add(new JScrollPane(areaLoi));

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLuu = new JButton("Cập nhật & lưu");
        JButton btnHuy = new JButton("Hủy");

        btnLuu.addActionListener(e -> capNhatAction());
        btnHuy.addActionListener(e -> dispose());

        pnlButtons.add(btnLuu);
        pnlButtons.add(btnHuy);

        add(pnlMain, BorderLayout.CENTER);
        add(pnlButtons, BorderLayout.SOUTH);
    }

    private void capNhatAction() {
        try {
            String status = cbTrangThai.getSelectedItem().toString();
            double cost = Double.parseDouble(txtChiPhi.getText());

            if (dao.capNhatPhieu(pbh.getMaPBH(), pbh.getMaSeri(), status, cost)) {
                if (status.equals("Đã trả khách")) {
                    inBienNhan(status, cost);
                }
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật database!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Chi phí không hợp lệ!");
        }
    }

    private void inBienNhan(String status, double cost) {
        String fileName = "biencan-baohanh-" + pbh.getMaPBH() + ".txt";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("==========================================\n");
            writer.write("       BIÊN NHẬN TRẢ MÁY BẢO HÀNH        \n");
            writer.write("==========================================\n");
            writer.write("Mã phiếu: " + pbh.getMaPBH() + "\n");
            writer.write("Ngày trả: " + sdf.format(new Date()) + "\n");
            writer.write("Mã sêri: " + pbh.getMaSeri() + "\n");
            writer.write("Mã khách hàng: " + pbh.getMaKH() + "\n");
            writer.write("Tình trạng: " + status + "\n");
            writer.write("------------------------------------------\n");
            writer.write("Nội dung lỗi: " + pbh.getMoTaLoi() + "\n");
            writer.write("------------------------------------------\n");
            writer.write("CHI PHÍ BẢO TRÌ: " + DinhDang.tien(cost) + "\n");
            writer.write("==========================================\n");
            writer.write("          CẢM ƠN QUÝ KHÁCH!               \n");
            
            JOptionPane.showMessageDialog(this, "Đã xuất biên nhận: " + fileName);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi in biên nhận: " + e.getMessage());
        }
    }
}
