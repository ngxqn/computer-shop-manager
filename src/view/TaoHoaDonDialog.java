package view;

import dao.HoaDonDAO;
import dao.SeriSanPhamDAO;
import dao.KhachHangDAO;
import model.ChiTietHoaDon;
import model.HoaDon;
import model.KhachHang;
import controller.AppSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

public class TaoHoaDonDialog extends JDialog {
    private JTextField txtMaHD, txtSerial;
    private JComboBox<KhachHang> cbKH;
    private JLabel lblNV;
    private JTable bang;
    private DefaultTableModel model;
    private JLabel lblTongTien;
    
    private double tongTienGiaoDich = 0.0;
    private DecimalFormat dinhDangTien = new DecimalFormat("###,###,### VNĐ");
    private SeriSanPhamDAO seriDAO = new SeriSanPhamDAO();
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private KhachHangDAO khDAO = new KhachHangDAO();
    
    private List<ChiTietHoaDon> danhSachChiTiet = new ArrayList<>();

    public TaoHoaDonDialog(Window owner) {
        super(owner, "LẬP HÓA ĐƠN BÁN HÀNG", ModalityType.APPLICATION_MODAL);
        thietLapGiaoDien();
    }

    private void thietLapGiaoDien() {
        setLayout(new BorderLayout(10, 10));
        
        // --- 1. Panel Trên (Thông tin cơ bản) ---
        JPanel pnlTop = new JPanel(new GridLayout(0, 2, 5, 5));
        pnlTop.setBorder(BorderFactory.createTitledBorder("Thông tin Hóa Đơn & Nhân viên"));

        txtMaHD = new JTextField(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        txtMaHD.setEditable(false);
        pnlTop.add(new JLabel("Mã Hóa Đơn:")); pnlTop.add(txtMaHD);

        cbKH = new JComboBox<>(new Vector<>(khDAO.getAll()));
        pnlTop.add(new JLabel("Chọn Khách Hàng:")); pnlTop.add(cbKH);

        String curNVStr = AppSession.getInstance().getMaNV() + " - " + AppSession.getInstance().getTenNV();
        lblNV = new JLabel(curNVStr);
        pnlTop.add(new JLabel("Nhân Viên Lập:")); pnlTop.add(lblNV);
        
        // --- 2. Panel Giữa (Khu vực Scan Serial & Giỏ Hàng) ---
        JPanel pnlCenter = new JPanel(new BorderLayout(5, 5));
        
        JPanel pnlScan = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlScan.add(new JLabel("🔍 Quét Mã Serial:"));
        txtSerial = new JTextField(20);
        txtSerial.setToolTipText("Nhập mã Serial và nhấn Enter để truy vấn");
        pnlScan.add(txtSerial);
        
        txtSerial.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    xuLyQuetSerial(txtSerial.getText().trim());
                    txtSerial.setText("");
                }
            }
        });

        pnlCenter.add(pnlScan, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Mã Seri", "Tên Sản Phẩm", "Đơn Giá Bán"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        bang = new JTable(model);
        pnlCenter.add(new JScrollPane(bang), BorderLayout.CENTER);

        // --- 3. Panel Dưới (Tổng tiền & Chốt đơn) ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        lblTongTien = new JLabel("Tổng Tiền: 0 VNĐ");
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 18));
        lblTongTien.setForeground(Color.RED);
        
        JButton btnThanhToan = new JButton("THANH TOÁN");
        btnThanhToan.setFont(new Font("Arial", Font.BOLD, 14));
        btnThanhToan.setBackground(new Color(0, 150, 0));
        btnThanhToan.setForeground(Color.WHITE);
        
        btnThanhToan.addActionListener(e -> thucHienThanhToan());

        pnlBot.add(lblTongTien);
        pnlBot.add(btnThanhToan);

        add(pnlTop, BorderLayout.NORTH);
        add(pnlCenter, BorderLayout.CENTER);
        add(pnlBot, BorderLayout.SOUTH);

        setSize(650, 500);
        setLocationRelativeTo(null);
    }

    private void xuLyQuetSerial(String maSeri) {
        if (maSeri.isEmpty()) return;

        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).toString().equalsIgnoreCase(maSeri)) {
                JOptionPane.showMessageDialog(this, "⚠️ Serial này đã được quét vào giỏ hàng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        Object[] thongTinSP = seriDAO.traCuuSerialBanHang(maSeri);
        if (thongTinSP == null) {
            JOptionPane.showMessageDialog(this, "❌ Không tìm thấy mã Serial này trong hệ thống!", "Lỗi tra cứu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tinhTrang = (String) thongTinSP[3];
        if (!tinhTrang.equalsIgnoreCase("Trong kho")) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi: Sản phẩm này đang ở trạng thái [" + tinhTrang + "], không thể bán!", "Lỗi sản phẩm", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tenSP = (String) thongTinSP[1];
        double giaBan = (Double) thongTinSP[2];

        ChiTietHoaDon ct = new ChiTietHoaDon();
        ct.setMaSeri(maSeri);
        ct.setDonGiaBan(giaBan);
        danhSachChiTiet.add(ct);

        String giaFormatted = dinhDangTien.format(giaBan);
        model.addRow(new Object[]{maSeri, tenSP, giaFormatted});

        tongTienGiaoDich += giaBan;
        lblTongTien.setText("Tổng Tiền: " + dinhDangTien.format(tongTienGiaoDich));
    }

    private void thucHienThanhToan() {
        if (danhSachChiTiet.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng rỗng, không thể lập hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        KhachHang kh = (KhachHang) cbKH.getSelectedItem();
        if (kh == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng!");
            return;
        }

        HoaDon hd = new HoaDon();
        hd.setMaHD(txtMaHD.getText());
        hd.setMaKH(kh.getMaKH());
        hd.setMaNV(AppSession.getInstance().getMaNV());
        hd.setNgayLap(new java.util.Date());

        for (ChiTietHoaDon ct : danhSachChiTiet) {
            ct.setMaHD(hd.getMaHD());
        }

        boolean success = hoaDonDAO.taoHoaDon(hd, danhSachChiTiet);
        if (success) {
            JOptionPane.showMessageDialog(this, "✅ Lập hóa đơn và Checkout thành công!\n" +
                                                 "Tổng tiển: " + dinhDangTien.format(tongTienGiaoDich), 
                                                 "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Lỗi khi commit Hóa Đơn vào Database!", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }
}
