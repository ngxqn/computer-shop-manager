package view;

import model.KhachHang;
import controller.QuanLyKhachHang;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BangDieuKhienKhachHang extends JPanel {

    private QuanLyKhachHang quanLyKhachHang;
    private JTable bangKhachHang;
    private DefaultTableModel moHinhBang;

    private JTextField truongID, truongHoTen, truongNamSinh, truongSDT, truongDiaChi;
    private JComboBox<String> hopChonGioiTinh;
    private final String[] DANH_SACH_GIOI_TINH = {"Nam", "Nữ"};
    private JTextField truongTimKiem;

    public BangDieuKhienKhachHang(QuanLyKhachHang quanLyKhachHang) {
        this.quanLyKhachHang = quanLyKhachHang;
        this.setLayout(new BorderLayout(10, 10));

        // Khởi tạo Table trước để các hàm sau không bị NullPointerException
        thietLapBang();

        JPanel bangDieuKhien = new JPanel(new BorderLayout(10, 10));
        bangDieuKhien.add(thietLapBangForm(), BorderLayout.CENTER);
        bangDieuKhien.add(thietLapBangNut(), BorderLayout.SOUTH);

        // NORTH: Bảng hiển thị
        JScrollPane scrollPane = new JScrollPane(bangKhachHang);
        scrollPane.setPreferredSize(new Dimension(800, 250));
        this.add(scrollPane, BorderLayout.NORTH);

        // CENTER: Form và Nút
        this.add(bangDieuKhien, BorderLayout.CENTER);

        taiDuLieuVaoBang();
        resetForm(); // Để thiết lập trạng thái ban đầu cho ô ID
    }

    private void thietLapBang() {
        String[] tenCot = {"ID", "Họ Tên", "Giới Tính", "Năm Sinh", "SDT", "Địa Chỉ"};
        moHinhBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        bangKhachHang = new JTable(moHinhBang);
        bangKhachHang.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bangKhachHang.getSelectedRow() != -1) {
                hienThiKhachHangDuocChon();
            }
        });
    }

    private JPanel thietLapBangForm() {
        JPanel bang = new JPanel(new GridLayout(3, 4, 10, 10));
        bang.setBorder(BorderFactory.createTitledBorder("Thông Tin Khách Hàng"));

        truongID = new JTextField(15);
        truongHoTen = new JTextField(15);
        hopChonGioiTinh = new JComboBox<>(DANH_SACH_GIOI_TINH);
        truongNamSinh = new JTextField(15);
        truongSDT = new JTextField(15);
        truongDiaChi = new JTextField(15);

        bang.add(new JLabel("  ID (Tự động):")); bang.add(truongID);
        bang.add(new JLabel("  Họ Tên:")); bang.add(truongHoTen);
        bang.add(new JLabel("  Giới Tính:")); bang.add(hopChonGioiTinh);
        bang.add(new JLabel("  Năm Sinh:")); bang.add(truongNamSinh);
        bang.add(new JLabel("  Số Điện Thoại:")); bang.add(truongSDT);
        bang.add(new JLabel("  Địa Chỉ:")); bang.add(truongDiaChi);

        return bang;
    }

    private JPanel thietLapBangNut() {
        JPanel bangNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton nutThem = new JButton("Thêm Mới");
        JButton nutSua = new JButton("Cập Nhật");
        JButton nutXoa = new JButton("Xóa");
        JButton nutReset = new JButton("Làm Mới");
        truongTimKiem = new JTextField(10);
        JButton nutTimKiem = new JButton("Tìm ID/Tên");

        nutThem.addActionListener(e -> themKhachHang());
        nutSua.addActionListener(e -> suaKhachHang());
        nutXoa.addActionListener(e -> xoaKhachHang());
        nutReset.addActionListener(e -> resetForm());
        nutTimKiem.addActionListener(e -> timKiemKhachHang(truongTimKiem.getText()));

        bangNut.add(nutThem); bangNut.add(nutSua); bangNut.add(nutXoa);
        bangNut.add(nutReset); bangNut.add(new JLabel(" | Tìm kiếm:"));
        bangNut.add(truongTimKiem); bangNut.add(nutTimKiem);

        return bangNut;
    }

    private void themKhachHang() {
        // Kiểm tra nhập liệu cơ bản (không kiểm tra ID vì tự động)
        if (truongHoTen.getText().trim().isEmpty() || truongSDT.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ít nhất Họ tên và SDT!");
            return;
        }

        // 1. Gọi logic phát sinh ID tự động từ Controller
        // Vì QuanLyKhachHang có phương thức phatSinhIDTuDong() là private,
        // ta nên gán ID trong phương thức themKhachHang(Scanner) hoặc tạo 1 hàm public phụ trợ.
        // Ở đây ta mô phỏng logic để GUI lấy được ID mới:

        KhachHang khMoi = new KhachHang();
        // Giả sử bạn đã sửa QuanLyKhachHang để phatSinhIDTuDong thành public
        // hoặc dùng phương thức tạo mới có sẵn:
        String idMoi = quanLyKhachHang.phatSinhIDTuDong(); // Hãy chuyển hàm này trong controller sang public

        khMoi.setID(idMoi);
        khMoi.setHoTen(truongHoTen.getText().trim());
        khMoi.setGioiTinh((String) hopChonGioiTinh.getSelectedItem());
        khMoi.setNamSinh(truongNamSinh.getText().trim());
        khMoi.setSdt(truongSDT.getText().trim());
        khMoi.setDiaChi(truongDiaChi.getText().trim());

        quanLyKhachHang.getKhachHangList().add(khMoi);
        quanLyKhachHang.ghiFileTXT();
        taiDuLieuVaoBang();
        resetForm();
        JOptionPane.showMessageDialog(this, "Thêm thành công! ID khách hàng là: " + idMoi);
    }

    private void suaKhachHang() {
        int row = bangKhachHang.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng từ bảng!");
            return;
        }

        String id = truongID.getText().trim();
        // Tìm khách hàng trong danh sách của controller
        for (KhachHang kh : quanLyKhachHang.getKhachHangList()) {
            if (kh.getID().equals(id)) {
                kh.setHoTen(truongHoTen.getText().trim());
                kh.setGioiTinh((String) hopChonGioiTinh.getSelectedItem());
                kh.setNamSinh(truongNamSinh.getText().trim());
                kh.setSdt(truongSDT.getText().trim());
                kh.setDiaChi(truongDiaChi.getText().trim());
                break;
            }
        }

        quanLyKhachHang.ghiFileTXT();
        taiDuLieuVaoBang();
        JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
    }

    private void xoaKhachHang() {
        int row = bangKhachHang.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa!");
            return;
        }

        String id = (String) bangKhachHang.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa khách hàng " + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Sử dụng logic xóa từ controller
            quanLyKhachHang.getKhachHangList().removeIf(kh -> kh.getID().equals(id));
            quanLyKhachHang.ghiFileTXT();
            taiDuLieuVaoBang();
            resetForm();
            JOptionPane.showMessageDialog(this, "Đã xóa khách hàng.");
        }
    }

    private void timKiemKhachHang(String keyword) {
        if (keyword.trim().isEmpty()) {
            taiDuLieuVaoBang();
            return;
        }
        // Sử dụng hàm tìm kiếm đã có trong controller
        List<KhachHang> ketQua = quanLyKhachHang.timKhachHang(keyword);
        dienDuLieuVaoBang(ketQua);
    }

    private void dienDuLieuVaoBang(List<KhachHang> danhSach) {
        moHinhBang.setRowCount(0);
        for (KhachHang kh : danhSach) {
            moHinhBang.addRow(new Object[]{
                    kh.getID(), kh.getHoTen(), kh.getGioiTinh(),
                    kh.getNamSinh(), kh.getSdt(), kh.getDiaChi()
            });
        }
    }

    private void taiDuLieuVaoBang() {
        quanLyKhachHang.docFileTXT();
        dienDuLieuVaoBang(quanLyKhachHang.getKhachHangList());
    }

    private void hienThiKhachHangDuocChon() {
        int row = bangKhachHang.getSelectedRow();
        if (row != -1) {
            truongID.setText(bangKhachHang.getValueAt(row, 0).toString());
            truongHoTen.setText(bangKhachHang.getValueAt(row, 1).toString());
            hopChonGioiTinh.setSelectedItem(bangKhachHang.getValueAt(row, 2).toString());
            truongNamSinh.setText(bangKhachHang.getValueAt(row, 3).toString());
            truongSDT.setText(bangKhachHang.getValueAt(row, 4).toString());
            truongDiaChi.setText(bangKhachHang.getValueAt(row, 5).toString());

            truongID.setEditable(false); // Không cho sửa ID khi đang chọn từ bảng
            truongID.setBackground(Color.LIGHT_GRAY);
        }
    }

    private void resetForm() {
        truongID.setText("ID Tự Động");
        truongID.setEditable(false);
        truongID.setBackground(Color.WHITE);
        truongHoTen.setText("");
        truongNamSinh.setText("");
        truongSDT.setText("");
        truongDiaChi.setText("");
        hopChonGioiTinh.setSelectedIndex(0);
        bangKhachHang.clearSelection();
        taiDuLieuVaoBang();
    }
}
