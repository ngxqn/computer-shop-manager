package view;

import dao.PhieuBaoHanhDAO;
import model.PhieuBaoHanh;
import controller.AppSession;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TiepNhanBaoHanhDialog extends JDialog {
    private JTextField txtSerial, txtTenSP, txtNgayMua, txtHanBH, txtTinhTrang, txtMaKH, txtMaNV, txtChiPhi;
    private JTextArea areaMoTa;
    private JSpinner spinNgayTra;
    private JButton btnTraCuu, btnLapPhieu, btnThoat;
    
    private PhieuBaoHanhDAO baoHanhDAO = new PhieuBaoHanhDAO();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public TiepNhanBaoHanhDialog(Frame parent) {
        super(parent, "Tiếp nhận & tra cứu bảo hành", true);
        this.setSize(600, 500);
        this.setLayout(new BorderLayout(10, 10));
        this.setLocationRelativeTo(parent);

        // --- UX: Đóng bằng phím ESC ---
        this.getRootPane().registerKeyboardAction(e -> dispose(), 
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), 
            JComponent.WHEN_IN_FOCUSED_WINDOW);

        // --- PANEL TRA CỨU (NORTH) ---
        JPanel pnlNorth = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlNorth.setBorder(BorderFactory.createTitledBorder("Nhập thông tin tra cứu"));
        pnlNorth.add(new JLabel("Mã sêri: "));
        txtSerial = new JTextField(20);
        btnTraCuu = new JButton("Tra cứu");
        pnlNorth.add(txtSerial);
        pnlNorth.add(btnTraCuu);
        this.add(pnlNorth, BorderLayout.NORTH);

        // --- PANEL THÔNG TIN & FORM (CENTER) ---
        JPanel pnlCenter = new JPanel(new GridLayout(8, 2, 5, 5));
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtTenSP = new JTextField(); txtTenSP.setEditable(false);
        txtNgayMua = new JTextField(); txtNgayMua.setEditable(false);
        txtHanBH = new JTextField(); txtHanBH.setEditable(false);
        txtTinhTrang = new JTextField(); txtTinhTrang.setEditable(false);
        txtMaKH = new JTextField(); txtMaKH.setEditable(false);
        
        // Cập nhật: Lấy từ AppSession
        txtMaNV = new JTextField(AppSession.getInstance().getMaNV());
        txtMaNV.setEditable(false);
        
        txtChiPhi = new JTextField("0");
        
        areaMoTa = new JTextArea(3, 20);
        spinNgayTra = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinNgayTra, "dd/MM/yyyy");
        spinNgayTra.setEditor(dateEditor);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 7);
        spinNgayTra.setValue(cal.getTime());

        pnlCenter.add(new JLabel("Tên sản phẩm:")); pnlCenter.add(txtTenSP);
        pnlCenter.add(new JLabel("Ngày mua:")); pnlCenter.add(txtNgayMua);
        pnlCenter.add(new JLabel("Hạn bảo hành:")); pnlCenter.add(txtHanBH);
        pnlCenter.add(new JLabel("Tình trạng máy:")); pnlCenter.add(txtTinhTrang);
        pnlCenter.add(new JLabel("Mã Khách hàng:")); pnlCenter.add(txtMaKH);
        pnlCenter.add(new JLabel("Nhân viên tiếp nhận:")); pnlCenter.add(txtMaNV);
        pnlCenter.add(new JLabel("Ngày trả dự kiến:")); pnlCenter.add(spinNgayTra);
        pnlCenter.add(new JLabel("Chi phí (nếu có):")); pnlCenter.add(txtChiPhi);

        JPanel pnlCenterScroll = new JPanel(new BorderLayout());
        pnlCenterScroll.add(pnlCenter, BorderLayout.NORTH);
        pnlCenterScroll.add(new JLabel(" Mô tả lỗi:"), BorderLayout.CENTER);
        pnlCenterScroll.add(new JScrollPane(areaMoTa), BorderLayout.SOUTH);

        this.add(pnlCenterScroll, BorderLayout.CENTER);

        // --- PANEL NÚT (SOUTH) ---
        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLapPhieu = new JButton("Lập phiếu bảo hành");
        btnLapPhieu.setEnabled(false);
        btnThoat = new JButton("Thoát");
        pnlSouth.add(btnLapPhieu);
        pnlSouth.add(btnThoat);
        this.add(pnlSouth, BorderLayout.SOUTH);

        // --- SỰ KIỆN ---
        btnTraCuu.addActionListener(e -> traCuuAction());
        btnLapPhieu.addActionListener(e -> lapPhieuAction());
        btnThoat.addActionListener(e -> this.dispose());
    }

    private void traCuuAction() {
        String serial = txtSerial.getText().trim();
        if (serial.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã sêri.");
            return;
        }

        Object[] measurements = baoHanhDAO.traCuuBaoHanh(serial);
        if (measurements == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin cho sêri này.");
            resetFields();
            return;
        }

        txtTenSP.setText(String.valueOf(measurements[0]));
        txtNgayMua.setText(measurements[1] != null ? sdf.format((Date)measurements[1]) : "N/A");
        Date ngayHetHan = (Date)measurements[2];
        txtHanBH.setText(ngayHetHan != null ? sdf.format(ngayHetHan) : "N/A");
        txtMaKH.setText(String.valueOf(measurements[4]));

        if (ngayHetHan != null && new Date().after(ngayHetHan)) {
            txtTinhTrang.setText("HẾT HẠN BẢO HÀNH");
            txtTinhTrang.setForeground(Color.RED);
        } else if (ngayHetHan == null) {
            txtTinhTrang.setText("Chưa kích hoạt (Chưa bán)");
            txtTinhTrang.setForeground(Color.GRAY);
        } else {
            txtTinhTrang.setText("CÒN HẠN BẢO HÀNH");
            txtTinhTrang.setForeground(new Color(0, 153, 51));
        }
        btnLapPhieu.setEnabled(ngayHetHan != null);
    }

    private void lapPhieuAction() {
        try {
            PhieuBaoHanh pbh = new PhieuBaoHanh();
            pbh.setMaPBH("PBH" + System.currentTimeMillis() % 1000000);
            pbh.setMaSeri(txtSerial.getText().trim());
            pbh.setMaKH(txtMaKH.getText());
            pbh.setMaNV(AppSession.getInstance().getMaNV());
            pbh.setNgayTiepNhan(new Date());
            pbh.setNgayTraDuKien((Date)spinNgayTra.getValue());
            pbh.setMoTaLoi(areaMoTa.getText());
            pbh.setTinhTrang("Đang xử lý");
            pbh.setChiPhi(Double.parseDouble(txtChiPhi.getText()));

            if (baoHanhDAO.taoPhieuBaoHanh(pbh)) {
                JOptionPane.showMessageDialog(this, "Lập phiếu bảo hành thành công: " + pbh.getMaPBH());
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi lập phiếu bảo hành.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Dữ liệu nhập không hợp lệ.");
        }
    }

    private void resetFields() {
        txtTenSP.setText("");
        txtNgayMua.setText("");
        txtHanBH.setText("");
        txtTinhTrang.setText("");
        txtMaKH.setText("");
        btnLapPhieu.setEnabled(false);
    }
}
