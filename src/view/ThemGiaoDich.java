package view;

import controller.QuanLyKho;
import controller.QuanLySanPham;
import dao.NhacCungCapDAO;
import dao.SeriSanPhamDAO;
import model.NhacCungCap;
import model.PhieuNhapKho;
import model.SanPham;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ThemGiaoDich extends JDialog {
    private QuanLyKho quanLiKho;
    private QuanLySanPham qlSanPham;
    private NhacCungCapDAO nccDAO = new NhacCungCapDAO();
    private SeriSanPhamDAO seriDAO = new SeriSanPhamDAO();

    private JTextField txtMaPN, txtMaSP, txtTenSP, txtGiaNhap;
    private JComboBox<NhacCungCap> cbNCC;
    private JTextArea txtSeris;
    private JLabel lblCount;

    public ThemGiaoDich(Window owner, QuanLyKho qlk, QuanLySanPham qlsp) {
        super(owner, "NHẬP HÀNG MỚI (PHÂN LOẠI SÊRI)", ModalityType.APPLICATION_MODAL);
        this.quanLiKho = qlk;
        this.qlSanPham = qlsp;
        thietLapGiaoDien();
    }

    private void thietLapGiaoDien() {
        setLayout(new BorderLayout(10, 10));
        
        // --- Panel Nhập liệu (Left) ---
        JPanel pnlLeft = new JPanel(new GridBagLayout());
        pnlLeft.setBorder(BorderFactory.createTitledBorder("Thông tin phiếu nhập"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Mã PN
        gbc.gridx = 0; gbc.gridy = 0; pnlLeft.add(new JLabel("Mã Phiếu:"), gbc);
        txtMaPN = new JTextField(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        txtMaPN.setEditable(false);
        gbc.gridx = 1; pnlLeft.add(txtMaPN, gbc);

        // Sản phẩm
        gbc.gridx = 0; gbc.gridy = 1; pnlLeft.add(new JLabel("Mã Sản Phẩm:"), gbc);
        txtMaSP = new JTextField(10);
        gbc.gridx = 1; pnlLeft.add(txtMaSP, gbc);
        
        JButton btnCheckSP = new JButton("Kiểm tra");
        gbc.gridy = 2; pnlLeft.add(btnCheckSP, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; pnlLeft.add(new JLabel("Tên SP:"), gbc);
        txtTenSP = new JTextField(); txtTenSP.setEditable(false);
        gbc.gridx = 1; pnlLeft.add(txtTenSP, gbc);

        // Nhà cung cấp
        gbc.gridx = 0; gbc.gridy = 4; pnlLeft.add(new JLabel("Nhà Cung Cấp:"), gbc);
        cbNCC = new JComboBox<>(new java.util.Vector<>(nccDAO.getAll()));
        gbc.gridx = 1; pnlLeft.add(cbNCC, gbc);

        // Giá nhập
        gbc.gridx = 0; gbc.gridy = 5; pnlLeft.add(new JLabel("Giá Nhập (Đơn vị):"), gbc);
        txtGiaNhap = new JTextField();
        gbc.gridx = 1; pnlLeft.add(txtGiaNhap, gbc);

        // --- Panel Serial (Right) ---
        JPanel pnlRight = new JPanel(new BorderLayout(5, 5));
        pnlRight.setBorder(BorderFactory.createTitledBorder("Danh sách sêri (Tối đa 100)"));
        txtSeris = new JTextArea(15, 20);
        lblCount = new JLabel("Số lượng: 0 cái");
        pnlRight.add(new JScrollPane(txtSeris), BorderLayout.CENTER);
        pnlRight.add(lblCount, BorderLayout.SOUTH);

        // --- Sự kiện ---
        btnCheckSP.addActionListener(e -> {
            SanPham sp = qlSanPham.timSanPhamTheoID(txtMaSP.getText().trim());
            if (sp != null) {
                txtTenSP.setText(sp.getTenSP());
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm!");
                txtTenSP.setText("");
            }
        });

        // --- Bottom Buttons ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnHuy = new JButton("Hủy");
        JButton btnXacNhan = new JButton("Xác nhận nhập kho");
        
        btnHuy.addActionListener(e -> dispose());
        btnXacNhan.addActionListener(e -> thucHienNhapKho());

        pnlBot.add(btnHuy); pnlBot.add(btnXacNhan);

        add(pnlLeft, BorderLayout.WEST);
        add(pnlRight, BorderLayout.CENTER);
        add(pnlBot, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void thucHienNhapKho() {
        // 1. Kiểm tra đầu vào
        String maSP = txtMaSP.getText().trim();
        NhacCungCap ncc = (NhacCungCap) cbNCC.getSelectedItem();
        String giaStr = txtGiaNhap.getText().trim();
        String serisRaw = txtSeris.getText().trim();

        if (maSP.isEmpty() || ncc == null || giaStr.isEmpty() || serisRaw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        // 2. Xử lý danh sách sêri
        List<String> listSeri = Arrays.stream(serisRaw.split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        if (listSeri.size() > 100) {
            JOptionPane.showMessageDialog(this, "Lỗi: Số lượng sêri vượt quá giới hạn 100 cái (Hiện có: " + listSeri.size() + ")", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Kiểm tra mã sêri tồn tại
        for (String s : listSeri) {
            if (seriDAO.kiemTraTonTai(s)) {
                JOptionPane.showMessageDialog(this, "Lỗi: Mã sêri [" + s + "] đã tồn tại trong hệ thống!", "Trùng sêri", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            double giaNhap = Double.parseDouble(giaStr);
            double tongTien = giaNhap * listSeri.size();

            // 4. Tạo đối tượng PhieuNhapKho
            PhieuNhapKho pn = new PhieuNhapKho();
            pn.setMaPN(txtMaPN.getText());
            pn.setMaNV("NV01"); // Tạm thời hardcode do chưa có Login
            pn.setMaNCC(ncc.getMaNCC());
            pn.setTongTien(tongTien);

            // 5. Gọi Controller thực hiện nghiệp vụ
            boolean success = quanLiKho.nhapHangMoi(pn, listSeri, maSP, giaNhap);
            if (success) {
                JOptionPane.showMessageDialog(this, "Nhập kho thành công " + listSeri.size() + " sản phẩm!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi hệ thống khi thực hiện Transaction!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá nhập không hợp lệ!");
        }
    }
}
