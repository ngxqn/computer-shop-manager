package view;

import dao.PhieuBaoHanhDAO;
import model.PhieuBaoHanh;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class BangDieuKhienBaoHanh extends JPanel {
    private PhieuBaoHanhDAO pbhDAO = new PhieuBaoHanhDAO();
    private JTable bang;
    private DefaultTableModel model;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public BangDieuKhienBaoHanh() {
        setLayout(new BorderLayout(10, 10));

        // 1. Table
        String[] columns = {"Mã Phiếu", "Serial", "Mã KH", "Ngày Tiếp Nhận", "Ngày Hẹn Trả", "Trạng Thái", "Chi Phí"};
        model = new DefaultTableModel(columns, 0);
        bang = new JTable(model);
        add(new JScrollPane(bang), BorderLayout.CENTER);

        // 2. Buttons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        JButton btnThem = new JButton("Lập Phiếu Tiếp Nhận");
        JButton btnXuLy = new JButton("Xử Lý & Trả Máy");
        JButton btnLamMoi = new JButton("Làm Mới");

        pnlButtons.add(btnThem);
        pnlButtons.add(btnXuLy);
        pnlButtons.add(btnLamMoi);
        add(pnlButtons, BorderLayout.SOUTH);

        // 3. Events
        btnThem.addActionListener(e -> {
            TiepNhanBaoHanhDialog dialog = new TiepNhanBaoHanhDialog((Frame) SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            taiDuLieu();
        });

        btnXuLy.addActionListener(e -> {
            int row = bang.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu bảo hành!");
                return;
            }
            String maPBH = (String) model.getValueAt(row, 0);
            PhieuBaoHanh selectedPBH = timPhieuTrongDanhSach(maPBH);
            if (selectedPBH != null) {
                ChiTietBaoHanhDialog dialog = new ChiTietBaoHanhDialog((Frame) SwingUtilities.getWindowAncestor(this), selectedPBH);
                dialog.setVisible(true);
                taiDuLieu();
            }
        });

        btnLamMoi.addActionListener(e -> taiDuLieu());

        taiDuLieu();
    }

    public void taiDuLieu() {
        model.setRowCount(0);
        List<PhieuBaoHanh> ds = pbhDAO.getAll();
        for (PhieuBaoHanh p : ds) {
            model.addRow(new Object[]{
                p.getMaPBH(),
                p.getMaSeri(),
                p.getMaKH(),
                sdf.format(p.getNgayTiepNhan()),
                p.getNgayTraDuKien() != null ? sdf.format(p.getNgayTraDuKien()) : "N/A",
                p.getTinhTrang(),
                String.format("%,.0f VNĐ", p.getChiPhi())
            });
        }
    }

    private PhieuBaoHanh timPhieuTrongDanhSach(String maPBH) {
        return pbhDAO.getAll().stream()
                .filter(p -> p.getMaPBH().equals(maPBH))
                .findFirst().orElse(null);
    }
}
