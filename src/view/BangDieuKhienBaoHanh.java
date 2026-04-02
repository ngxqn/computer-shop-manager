package view;

import dao.PhieuBaoHanhDAO;
import model.PhieuBaoHanh;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import util.DinhDang;

public class BangDieuKhienBaoHanh extends JPanel {
    private PhieuBaoHanhDAO pbhDAO = new PhieuBaoHanhDAO();
    private JTable bang;
    private DefaultTableModel model;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public BangDieuKhienBaoHanh() {
        setLayout(new BorderLayout(10, 10));

        // 1. Buttons
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton btnLapPhieu = new JButton("+ Lập phiếu tiếp nhận");
        btnLapPhieu.setFont(new Font("Inter", Font.BOLD, 14));
        btnLapPhieu.setBackground(new Color(0, 120, 200));
        btnLapPhieu.setForeground(Color.WHITE);
        
        JButton btnXuLy = new JButton("⛭ Xử lý & trả máy");
        btnXuLy.setFont(new Font("Inter", Font.BOLD, 14));
        btnXuLy.setBackground(new Color(235, 215, 0));
        btnXuLy.setForeground(Color.BLACK);
        
        JButton btnRefresh = new JButton("↻ Làm mới");
        btnRefresh.setFont(new Font("Inter", Font.PLAIN, 14));

        panelTop.add(btnLapPhieu);
        panelTop.add(btnXuLy);
        panelTop.add(btnRefresh);
        add(panelTop, BorderLayout.NORTH);

        // 2. Table
        String[] columns = {"Mã phiếu", "Mã sêri", "Mã KH", "Ngày tiếp nhận", "Ngày hẹn trả", "Trạng thái", "Chi phí"};
        model = new DefaultTableModel(columns, 0);
        bang = new JTable(model);
        bang.setRowHeight(25);
        bang.setAutoCreateRowSorter(true);
        add(new JScrollPane(bang), BorderLayout.CENTER);

        // 3. Events
        btnLapPhieu.addActionListener(e -> {
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

        btnRefresh.addActionListener(e -> taiDuLieu());

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
                DinhDang.tien(p.getChiPhi())
            });
        }
    }

    private PhieuBaoHanh timPhieuTrongDanhSach(String maPBH) {
        return pbhDAO.getAll().stream()
                .filter(p -> p.getMaPBH().equals(maPBH))
                .findFirst().orElse(null);
    }
}
