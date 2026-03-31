package view;

import model.PhieuNhapKho;
import model.SanPham;
import model.HoaDon;
import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.List;

public class ChiTietGiaoDich extends JDialog {

    private final SimpleDateFormat dinhDangNgay = new SimpleDateFormat("dd/MM/yyyy");
    private final NumberFormat dinhDangVN = NumberFormat.getInstance(Locale.of("vi", "VN"));

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
        this.nhanDoiTac = "Mã Khách Hàng:";
        this.maDoiTac = hd.getMaKH();
        this.ngayGD = hd.getNgayLap();
        this.dssp = new java.util.ArrayList<>(); // Đã chuyển sang quản lý qua ChiTietHoaDonDAO

        khoiTaoGiaoDien(owner);
    }

    // CONSTRUCTOR 2: Xử lý Phiếu Nhập Kho
    public ChiTietGiaoDich(Window owner, PhieuNhapKho pnk) {
        super(owner, "Chi Tiết Phiếu Nhập: " + pnk.layMaGiaoDich(), ModalityType.APPLICATION_MODAL);
        this.maGD = pnk.layMaGiaoDich();
        this.loaiGD = "PHIẾU NHẬP KHO (BỔ SUNG)";
        this.nhanDoiTac = "Mã Nhân Viên:";
        this.maDoiTac = pnk.layMaNhanVien();
        this.ngayGD = pnk.layNgayNhap();
        this.tongGiaTri = pnk.tinhTongGiaTri();
        this.tienGiam = 0;
        this.dssp = pnk.layDanhSachSanPham();

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
        JButton nutDong = new JButton("Đóng Cửa Sổ");
        nutDong.addActionListener(e -> dispose());
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelNut.add(nutDong);
        this.add(panelNut, BorderLayout.SOUTH);
    }

    private JPanel taoPanelThongTinChung() {
        JPanel panelThongTin = new JPanel(new GridLayout(0, 2, 10, 8));
        panelThongTin.setBorder(BorderFactory.createTitledBorder("Thông Tin Chứng Từ"));

        panelThongTin.add(new JLabel("Loại Giao Dịch:"));
        panelThongTin.add(new JLabel("<html><b>" + loaiGD + "</b></html>"));

        panelThongTin.add(new JLabel("Mã Giao Dịch:"));
        panelThongTin.add(new JLabel(maGD));

        panelThongTin.add(new JLabel(nhanDoiTac));
        panelThongTin.add(new JLabel(maDoiTac));

        panelThongTin.add(new JLabel("Ngày Thực Hiện:"));
        panelThongTin.add(new JLabel(dinhDangNgay.format(ngayGD)));

        panelThongTin.add(new JLabel("Tổng Giá Trị Hàng:"));
        panelThongTin.add(new JLabel(dinhDangVN.format(tongGiaTri) + " VNĐ"));

        if (tienGiam > 0) {
            panelThongTin.add(new JLabel("Chiết Khấu/Giảm Giá:"));
            panelThongTin.add(new JLabel("- " + dinhDangVN.format(tienGiam) + " VNĐ"));

            panelThongTin.add(new JLabel("<html><font color='red'>THỰC THU:</font></html>"));
            panelThongTin.add(new JLabel("<html><b><font color='red'>" + dinhDangVN.format(tongGiaTri - tienGiam) + " VNĐ</font></b></html>"));
        }

        return panelThongTin;
    }

    private String taoChuoiChiTietSanPham() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(" --- DANH SÁCH MẶT HÀNG ---%n%n"));
        sb.append(String.format(" %-12s %-25s %-8s %-15s %-15s%n",
                "Mã SP", "Tên Sản Phẩm", "SL", "Giá Gốc", "Thành Tiền"));
        sb.append(" -----------------------------------------------------------------------------%n");

        if (dssp != null && !dssp.isEmpty()) {
            for (SanPham sp : dssp) {
                int soLuong = 0; // Đã chuyển sang Serial
                double thanhTien = (double) soLuong * sp.getGiaBan();
                sb.append(String.format(" %-12s %-25s %-8d %-15s %-15s%n",
                        sp.getMaSP(), sp.getTenSP(), soLuong,
                        dinhDangVN.format(sp.getGiaBan()), dinhDangVN.format(thanhTien)));
            }
        }
        sb.append(" -----------------------------------------------------------------------------%n");
        return sb.toString();
    }
}
