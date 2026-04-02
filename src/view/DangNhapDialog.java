package view;

import controller.AppSession;
import dao.NhanVienDAO;
import model.NhanVien;

import javax.swing.*;
import java.awt.*;

public class DangNhapDialog extends JDialog {
    private JTextField txtMaNV;
    private JPasswordField txtMatKhau;
    private JButton btnDangNhap, btnThoat;
    private NhanVienDAO nvDAO = new NhanVienDAO();
    private boolean thanhCong = false;

    public DangNhapDialog(Frame parent) {
        super(parent, "ĐĂNG NHẬP HỆ THỐNG", true);
        thietLapGiaoDien();
    }

    private void thietLapGiaoDien() {
        this.setSize(400, 250);
        this.setLayout(new BorderLayout(10, 10));
        this.setLocationRelativeTo(null);

        // Header
        JLabel lblHeader = new JLabel("COMPUTER SHOP MANAGER", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 20));
        lblHeader.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(lblHeader, BorderLayout.NORTH);

        // Form
        JPanel pnlCenter = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; pnlCenter.add(new JLabel("Mã Nhân Viên:"), gbc);
        txtMaNV = new JTextField(15);
        gbc.gridx = 1; pnlCenter.add(txtMaNV, gbc);

        gbc.gridx = 0; gbc.gridy = 1; pnlCenter.add(new JLabel("Mật Khẩu:"), gbc);
        txtMatKhau = new JPasswordField(15);
        gbc.gridx = 1; pnlCenter.add(txtMatKhau, gbc);

        add(pnlCenter, BorderLayout.CENTER);

        // Buttons
        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        btnDangNhap = new JButton("Đăng Nhập");
        btnThoat = new JButton("Thoát");
        
        pnlSouth.add(btnDangNhap);
        pnlSouth.add(btnThoat);
        add(pnlSouth, BorderLayout.SOUTH);

        // Events
        btnDangNhap.addActionListener(e -> xuLyDangNhap());
        btnThoat.addActionListener(e -> System.exit(0));

        // Default button
        this.getRootPane().setDefaultButton(btnDangNhap);
    }

    private void xuLyDangNhap() {
        String maNV = txtMaNV.getText().trim();
        String password = new String(txtMatKhau.getPassword());

        if (maNV.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        NhanVien nv = nvDAO.dangNhap(maNV, password);
        if (nv != null) {
            AppSession.getInstance().login(nv);
            thanhCong = true;
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Mã NV hoặc Mật khẩu không đúng!", "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isThanhCong() {
        return thanhCong;
    }
}
