package view;

import dao.PhieuBaoHanhDAO;
import model.PhieuBaoHanh;
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
        super(parent, "Tiếp Nhận & Tra Cứu Bảo Hành", true);
        this.setSize(600, 500);
        this.setLayout(new BorderLayout(10, 10));
        this.setLocationRelativeTo(parent);

        // --- PANEL TRA CỨU (NORTH) ---
        JPanel pnlNorth = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlNorth.setBorder(BorderFactory.createTitledBorder("Nhập thông tin tra cứu"));
        pnlNorth.add(new JLabel("Mã Serial: "));
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
        txtMaNV = new JTextField("NV001"); // Mặc định hoặc lấy từ session
        txtChiPhi = new JTextField("0");
        
        areaMoTa = new JTextArea(3, 20);
        spinNgayTra = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinNgayTra, "dd/MM/yyyy");
        spinNgayTra.setEditor(dateEditor);
        // Set ngày trả dự kiến là +7 ngày từ hôm nay
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 7);
        spinNgayTra.setValue(cal.getTime());

        pnlCenter.add(new JLabel("Tên sản phẩm:")); pnlCenter.add(txtTenSP);
        pnlCenter.add(new JLabel("Ngày mua:")); pnlCenter.add(txtNgayMua);
        pnlCenter.add(new JLabel("Hạn bảo hành:")); pnlCenter.add(txtHanBH);
        pnlCenter.add(new JLabel("Tình trạng máy:")); pnlCenter.add(txtTinhTrang);
        pnlCenter.add(new JLabel("Mã Khách hàng:")); pnlCenter.add(txtMaKH);
        pnlCenter.add(new JLabel("Mã NV Tiếp nhận:")); pnlCenter.add(txtMaNV);
        pnlCenter.add(new JLabel("Ngày trả dự kiến:")); pnlCenter.add(spinNgayTra);
        pnlCenter.add(new JLabel("Chi phí (nếu có):")); pnlCenter.add(txtChiPhi);

        JPanel pnlCenterScroll = new JPanel(new BorderLayout());
        pnlCenterScroll.add(pnlCenter, BorderLayout.NORTH);
        pnlCenterScroll.add(new JLabel(" Mô tả lỗi:"), BorderLayout.CENTER);
        pnlCenterScroll.add(new JScrollPane(areaMoTa), BorderLayout.SOUTH);

        this.add(pnlCenterScroll, BorderLayout.CENTER);

        // --- PANEL NÚT (SOUTH) ---
        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLapPhieu = new JButton("Lập Phiếu Bảo Hành");
        btnLapPhieu.setEnabled(false);
        btnThoat = new JButton("Thoát");
        pnlSouth.add(btnLapPhieu);
        pnlSouth.add(btnThoat);
        this.add(pnlSouth, BorderLayout.SOUTH);

        // --- XỬ LÝ SỰ KIỆN ---
        btnTraCuu.addActionListener(e -> traCuuAction());
        btnLapPhieu.addActionListener(e -> lapPhieuAction());
        btnThoat.addActionListener(e -> this.dispose());
    }

    private void traCuuAction() {
        String serial = txtSerial.getText().trim();
        if (serial.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số Serial.");
            return;
        }

        Object[] measurements = baoHanhDAO.traCuuBaoHanh(serial);
        if (measurements == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin cho Serial này.");
            resetFields();
            return;
        }

        // measurement: {TenSP, NgayMua, NgayHetHan, TinhTrang, MaKH}
        txtTenSP.setText(String.valueOf(measurements[0]));
        txtNgayMua.setText(measurements[1] != null ? sdf.format((Date)measurements[1]) : "N/A");
        
        Date ngayHetHan = (Date)measurements[2];
        txtHanBH.setText(ngayHetHan != null ? sdf.format(ngayHetHan) : "N/A");
        
        String tinhTrangHienTai = String.valueOf(measurements[3]);
        txtMaKH.setText(String.valueOf(measurements[4]));

        // Kiểm tra hạn
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
        
        btnLapPhieu.setEnabled(ngayHetHan != null); // Chỉ cho lập phiếu nếu máy đã bán
    }

    private void lapPhieuAction() {
        try {
            PhieuBaoHanh pbh = new PhieuBaoHanh();
            // Tự phát sinh mã phiếu
            pbh.setMaPBH("PBH" + System.currentTimeMillis() % 1000000);
            pbh.setMaSeri(txtSerial.getText().trim());
            pbh.setMaKH(txtMaKH.getText());
            pbh.setMaNV(txtMaNV.getText().trim());
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
