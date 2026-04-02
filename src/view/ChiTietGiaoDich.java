package view;

import model.PhieuNhapKho;
import model.SanPham;
import model.HoaDon;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import util.DinhDang;

public class ChiTietGiaoDich extends JDialog {

    private final SimpleDateFormat dinhDangNgay = new SimpleDateFormat("dd/MM/yyyy");

    // Thuộc tính hiển thị
    private String maGD, loaiGD, nhanDoiTac, maDoiTac;
    private Date ngayGD;
    private double tongGiaTri, tienGiam;
    private List<SanPham> dssp;

    // CONSTRUCTOR 1: Xử lý Hóa Đơn
    public ChiTietGiaoDich(Window owner, HoaDon hd) {
        super(owner, "Chi Tiết Hóa Đơn: " + hd.getMaHD(), ModalityType.APPLICATION_MODAL);
        this.maGD = hd.getMaHD();
        this.loaiGD = "HÓA ĐƠN XUẤT KHO (BÁN HÀNG)";
        this.nhanDoiTac = "Mã khách hàng:";
        this.maDoiTac = hd.getMaKH();
        this.ngayGD = hd.getNgayLap();
        this.dssp = new java.util.ArrayList<>(); // Đã chuyển sang quản lý qua ChiTietHoaDonDAO

        khoiTaoGiaoDien(owner);
    }

    // CONSTRUCTOR 2: Xử lý Phiếu Nhập Kho
    public ChiTietGiaoDich(Window owner, PhieuNhapKho pnk) {
        super(owner, "Chi tiết phiếu nhập: " + pnk.getMaPN(), ModalityType.APPLICATION_MODAL);
        this.maGD = pnk.getMaPN();
        this.loaiGD = "PHIẾU NHẬP KHO (BỔ SUNG)";
        this.nhanDoiTac = "Mã nhân viên:";
        this.maDoiTac = pnk.getMaNV();
        this.ngayGD = pnk.getNgayNhap();
        this.tongGiaTri = pnk.getTongTien();
        this.tienGiam = 0;
        
        // Chuyển đổi ChiTietPhieuNhap sang String để hiển thị đơn giản
        khoiTaoGiaoDien(owner);
    }

    private void khoiTaoGiaoDien(Window owner) {
        this.setLayout(new BorderLayout(10, 10));
        this.setSize(750, 550);
        this.setLocationRelativeTo(owner);

        // Header: Thông tin chung
        this.add(taoPanelThongTinChung(), BorderLayout.NORTH);

        // Body: Danh sách sản phẩm
        JTextArea vungChiTiet = new JTextArea();
        vungChiTiet.setFont(new Font("Monospaced", Font.PLAIN, 13));
        vungChiTiet.setBackground(new Color(245, 245, 245));
        vungChiTiet.setEditable(false);
        vungChiTiet.setText(taoChuoiChiTietSanPham());
        vungChiTiet.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.add(new JScrollPane(vungChiTiet), BorderLayout.CENTER);

        // Footer: Nút đóng
        JButton nutDong = new JButton("Đóng cửa sổ");
        nutDong.addActionListener(e -> dispose());
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelNut.add(nutDong);
        this.add(panelNut, BorderLayout.SOUTH);
    }

    private JPanel taoPanelThongTinChung() {
        JPanel panelThongTin = new JPanel(new GridLayout(0, 2, 10, 8));
        panelThongTin.setBorder(BorderFactory.createTitledBorder("Thông tin chứng từ"));

        panelThongTin.add(new JLabel("Loại giao dịch:"));
        panelThongTin.add(new JLabel("<html><b>" + loaiGD + "</b></html>"));

        panelThongTin.add(new JLabel("Mã giao dịch:"));
        panelThongTin.add(new JLabel(maGD));

        panelThongTin.add(new JLabel(nhanDoiTac));
        panelThongTin.add(new JLabel(maDoiTac));

        panelThongTin.add(new JLabel("Ngày thực hiện:"));
        panelThongTin.add(new JLabel(dinhDangNgay.format(ngayGD)));

        panelThongTin.add(new JLabel("Tổng giá trị hàng:"));
        panelThongTin.add(new JLabel(DinhDang.tien(tongGiaTri)));

        if (tienGiam > 0) {
            panelThongTin.add(new JLabel("Chiết khấu/Giảm giá:"));
            panelThongTin.add(new JLabel("- " + DinhDang.tien(tienGiam)));

            panelThongTin.add(new JLabel("<html><font color='red'>Thực thu:</font></html>"));
            panelThongTin.add(new JLabel("<html><b><font color='red'>" + DinhDang.tien(tongGiaTri - tienGiam) + "</font></b></html>"));
        }

        return panelThongTin;
    }

    private String taoChuoiChiTietSanPham() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(" --- DANH SÁCH MẶT HÀNG ---%n%n"));
        sb.append(String.format(" %-12s %-25s %-8s %-15s %-15s%n",
                "Mã SP", "Thông tin", "SL", "Giá", "Thành Tiền"));
        sb.append(" -----------------------------------------------------------------------------%n");

        if (dssp != null && !dssp.isEmpty()) {
            for (SanPham sp : dssp) {
                int soLuong = 1; // Mặc định 1 cho Hóa đơn (Serial-based)
                double thanhTien = sp.getGiaBan();
                sb.append(String.format(" %-12s %-25s %-8d %-15s %-15s%n",
                        sp.getMaSP(), sp.getTenSP(), soLuong,
                        DinhDang.tien(sp.getGiaBan()), DinhDang.tien(thanhTien)));
            }
        } else {
            sb.append(" [Chi tiết sản phẩm sẽ được nạp từ Database trong phiên bản tới]%n");
        }
        sb.append(" -----------------------------------------------------------------------------%n");
        return sb.toString();
    }
}
