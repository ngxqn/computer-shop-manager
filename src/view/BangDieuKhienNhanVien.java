package view;

import model.*;
import controller.QuanLyNhanVien;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.text.DecimalFormat;

public class BangDieuKhienNhanVien extends JPanel {

    private QuanLyNhanVien quanLyNhanVien;
    private JTable bangNhanVien;
    private DefaultTableModel moHinhBang;
    private DecimalFormat dinhDangLuong = new DecimalFormat("###,###,###");

    private JTextField truongID, truongHoTen, truongNamSinh, truongSoNgayNghi;
    private JTextField truongSDT, truongDiaChi, truongTimKiem;
    private JComboBox<String> hopChonGioiTinh, hopChonCaLamViec, hopChonChucVu;

    private final String[] DANH_SACH_CHUC_VU = {"Ban Hang", "Thu Ngan", "Quan Li Kho"};
    private final String[] DANH_SACH_GIOI_TINH = {"Nam", "Nu"};
    private final String[] DANH_SACH_CA_LAM = {"Sang", "Toi"};

    public BangDieuKhienNhanVien(QuanLyNhanVien quanLyNhanVien) {
        this.quanLyNhanVien = quanLyNhanVien;
        this.setLayout(new BorderLayout(10, 10));

        thietLapBang();

        JPanel panelPhiaDuoi = new JPanel(new BorderLayout(5, 5));
        panelPhiaDuoi.add(thietLapBangForm(), BorderLayout.CENTER);
        panelPhiaDuoi.add(thietLapBangNut(), BorderLayout.SOUTH);

        this.add(new JScrollPane(bangNhanVien), BorderLayout.CENTER);
        this.add(panelPhiaDuoi, BorderLayout.SOUTH);

        taiDuLieuVaoBang();
        truongID.setText(quanLyNhanVien.phatSinhIDTuDong());
    }

    private void thietLapBang() {
        String[] tenCot = {"ID", "Họ Tên", "Giới Tính", "Năm Sinh", "SDT", "Địa Chỉ", "Chức Vụ", "Xếp Loại", "Tổng Lương"};
        moHinhBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        bangNhanVien = new JTable(moHinhBang);
        bangNhanVien.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bangNhanVien.getSelectedRow() != -1) {
                hienThiNhanVienDuocChon();
            }
        });
    }

    private JPanel thietLapBangForm() {
        JPanel bang = new JPanel(new GridBagLayout());
        bang.setBorder(BorderFactory.createTitledBorder("Thông tin chi tiết"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        truongID = new JTextField(15);
        truongID.setEditable(false);
        truongID.setBackground(new Color(230, 230, 230));
        truongHoTen = new JTextField(15);
        hopChonGioiTinh = new JComboBox<>(DANH_SACH_GIOI_TINH);
        truongNamSinh = new JTextField(15);
        truongSDT = new JTextField(15);
        truongDiaChi = new JTextField(15);
        hopChonChucVu = new JComboBox<>(DANH_SACH_CHUC_VU);
        truongSoNgayNghi = new JTextField(15);
        hopChonCaLamViec = new JComboBox<>(DANH_SACH_CA_LAM);

        themVaoForm(bang, new JLabel("ID (Tự động):"), 0, 0, gbc);
        themVaoForm(bang, truongID, 1, 0, gbc);
        themVaoForm(bang, new JLabel("Họ Tên:"), 2, 0, gbc);
        themVaoForm(bang, truongHoTen, 3, 0, gbc);

        themVaoForm(bang, new JLabel("Giới Tính:"), 0, 1, gbc);
        themVaoForm(bang, hopChonGioiTinh, 1, 1, gbc);
        themVaoForm(bang, new JLabel("Năm Sinh:"), 2, 1, gbc);
        themVaoForm(bang, truongNamSinh, 3, 1, gbc);

        themVaoForm(bang, new JLabel("Số Điện Thoại:"), 0, 2, gbc);
        themVaoForm(bang, truongSDT, 1, 2, gbc);
        themVaoForm(bang, new JLabel("Địa Chỉ:"), 2, 2, gbc);
        themVaoForm(bang, truongDiaChi, 3, 2, gbc);

        themVaoForm(bang, new JLabel("Chức Vụ:"), 0, 3, gbc);
        themVaoForm(bang, hopChonChucVu, 1, 3, gbc);
        themVaoForm(bang, new JLabel("Ca Làm Việc:"), 2, 3, gbc);
        themVaoForm(bang, hopChonCaLamViec, 3, 3, gbc);

        themVaoForm(bang, new JLabel("Số Ngày Nghỉ:"), 0, 4, gbc);
        themVaoForm(bang, truongSoNgayNghi, 1, 4, gbc);

        return bang;
    }

    private void themVaoForm(JPanel p, JComponent c, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x; gbc.gridy = y;
        p.add(c, gbc);
    }

    private JPanel thietLapBangNut() {
        JPanel bang = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton nutThem = new JButton("Thêm Mới");
        JButton nutSua = new JButton("Cập Nhật");
        JButton nutXoa = new JButton("Xóa Nhân Viên");
        JButton nutReset = new JButton("Làm Mới Form");

        // SỬA NHÃN: Ghi rõ tìm theo ID/Tên
        truongTimKiem = new JTextField(15);
        JButton nutTimKiem = new JButton("Tìm Kiếm");

        nutThem.addActionListener(e -> themNhanVien());
        nutSua.addActionListener(e -> suaNhanVien());
        nutXoa.addActionListener(e -> xoaNhanVien());
        nutReset.addActionListener(e -> resetForm());

        // Gọi hàm tìm kiếm mới
        nutTimKiem.addActionListener(e -> thucHienTimKiem(truongTimKiem.getText()));

        bang.add(nutThem); bang.add(nutSua); bang.add(nutXoa);
        bang.add(nutReset); bang.add(new JLabel("|"));
        bang.add(new JLabel("ID hoặc Tên:")); bang.add(truongTimKiem); bang.add(nutTimKiem);
        return bang;
    }

    // --- CẬP NHẬT LOGIC TÌM KIẾM ---
    private void thucHienTimKiem(String keyword) {
        String tuKhoa = keyword.trim();
        if (tuKhoa.isEmpty()) {
            taiDuLieuVaoBang();
            return;
        }

        // Sử dụng hàm timNhanVien(keyword) của Controller để lấy danh sách kết quả
        List<NhanVien> ketQua = quanLyNhanVien.timNhanVien(tuKhoa);

        moHinhBang.setRowCount(0);
        if (!ketQua.isEmpty()) {
            for (NhanVien nv : ketQua) {
                moHinhBang.addRow(new Object[]{
                        nv.getID(), nv.getHoTen(), nv.getGioiTinh(), nv.getNamSinh(),
                        nv.getSdt(), nv.getDiaChi(), nv.getChucVu(), nv.xepLoai(),
                        dinhDangLuong.format(nv.tinhLuong())
                });
            }
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên phù hợp với: " + tuKhoa);
            taiDuLieuVaoBang();
        }
    }

    // --- CÁC PHƯƠNG THỨC KHÁC GIỮ NGUYÊN ---
    private void taiDuLieuVaoBang() {
        quanLyNhanVien.refreshData();
        moHinhBang.setRowCount(0);
        for (NhanVien nv : quanLyNhanVien.getNhanVienList()) {
            moHinhBang.addRow(new Object[]{
                    nv.getID(), nv.getHoTen(), nv.getGioiTinh(), nv.getNamSinh(),
                    nv.getSdt(), nv.getDiaChi(), nv.getChucVu(), nv.xepLoai(),
                    dinhDangLuong.format(nv.tinhLuong())
            });
        }
    }

    private void themNhanVien() {
        NhanVien nvMoi = taoNhanVienTuForm();
        if (nvMoi == null) return;
        
        boolean success = quanLyNhanVien.themNhanVien(nvMoi);
        if (success) {
            taiDuLieuVaoBang();
            resetForm();
            JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm nhân viên vào Database!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaNhanVien() {
        String id = truongID.getText();
        NhanVien nvCu = quanLyNhanVien.timNhanVienTheoID(id);
        if (nvCu == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên từ bảng để sửa!");
            return;
        }
        NhanVien nvMoi = taoNhanVienTuForm();
        if (nvMoi == null) return;
        
        boolean success = quanLyNhanVien.suaNhanVien(nvMoi);
        if (success) {
            taiDuLieuVaoBang();
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật nhân viên vào Database!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaNhanVien() {
        String id = truongID.getText();
        NhanVien nv = quanLyNhanVien.timNhanVienTheoID(id);
        if (nv == null) {
            JOptionPane.showMessageDialog(this, "Hãy chọn nhân viên cần xóa trong bảng!");
            return;
        }
        int xacNhan = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa nhân viên " + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (xacNhan == JOptionPane.YES_OPTION) {
            boolean success = quanLyNhanVien.xoaNhanVien(id);
            if (success) {
                taiDuLieuVaoBang();
                resetForm();
                JOptionPane.showMessageDialog(this, "Đã xóa nhân viên (Soft Delete)!");
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa nhân viên trong Database!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hienThiNhanVienDuocChon() {
        int row = bangNhanVien.getSelectedRow();
        if (row == -1) return;
        String id = (String) bangNhanVien.getValueAt(row, 0);
        NhanVien nv = quanLyNhanVien.timNhanVienTheoID(id);
        if (nv != null) {
            truongID.setText(nv.getID());
            truongHoTen.setText(nv.getHoTen());
            hopChonGioiTinh.setSelectedItem(nv.getGioiTinh());
            truongNamSinh.setText(nv.getNamSinh());
            truongSDT.setText(nv.getSdt());
            truongDiaChi.setText(nv.getDiaChi());
            hopChonChucVu.setSelectedItem(nv.getChucVu());
            truongSoNgayNghi.setText(String.valueOf(nv.getSoNgayNghi()));
            hopChonCaLamViec.setSelectedItem(nv.getCaLamViec());
        }
    }

    private void resetForm() {
        truongID.setText(quanLyNhanVien.phatSinhIDTuDong());
        truongHoTen.setText(""); truongNamSinh.setText("");
        truongSDT.setText(""); truongDiaChi.setText("");
        truongSoNgayNghi.setText(""); truongTimKiem.setText("");
        bangNhanVien.clearSelection();
        taiDuLieuVaoBang();
    }

    private NhanVien taoNhanVienTuForm() {
        try {
            String id = truongID.getText();
            String ten = truongHoTen.getText().trim();
            String gt = (String) hopChonGioiTinh.getSelectedItem();
            String ns = truongNamSinh.getText().trim();
            String sdt = truongSDT.getText().trim();
            String dc = truongDiaChi.getText().trim();
            String cv = (String) hopChonChucVu.getSelectedItem();
            String ca = (String) hopChonCaLamViec.getSelectedItem();
            int nghi = Integer.parseInt(truongSoNgayNghi.getText().trim());
            if (ten.isEmpty()) throw new Exception("Tên không được để trống!");
            if (cv.equals("Ban Hang")) return new NhanVienBanHang(cv, id, ten, gt, ns, sdt, dc, nghi, ca);
            if (cv.equals("Thu Ngan")) return new NhanVienThuNgan(cv, id, ten, gt, ns, sdt, dc, nghi, ca);
            return new NhanVienQuanLyKho(cv, id, ten, gt, ns, sdt, dc, nghi, ca);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi dữ liệu: " + e.getMessage());
            return null;
        }
    }
}
