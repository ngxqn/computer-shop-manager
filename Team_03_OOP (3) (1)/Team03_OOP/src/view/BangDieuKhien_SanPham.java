package view;

import modal.SanPham;
import controller.QuanLi_SanPham;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ArrayList;

public class BangDieuKhien_SanPham extends JPanel {

    private QuanLi_SanPham quanLySanPham;
    private JTable bangSanPham;
    private DefaultTableModel moHinhBang;
    private JTextField truongID, truongTen, truongSoLuong, truongGiaGoc;
    private JComboBox<String> hopChonLoai;
    private JTextField truongTimKiem;

    private final String[] DANH_SACH_LOAI = {"Nuoc", "Banh", "Do gia dung", "Điện tử", "Thời trang", "Khác"};
    private final NumberFormat dinhDangTien = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public BangDieuKhien_SanPham(QuanLi_SanPham qlsp) {
        this.quanLySanPham = qlsp;
        this.setLayout(new BorderLayout(10, 10));

        thietLapBang();

        JPanel bangDieuKhien = new JPanel(new BorderLayout(10, 10));
        JPanel bangForm = thietLapBangForm();
        bangDieuKhien.add(bangForm, BorderLayout.CENTER);

        JPanel bangNut = thietLapBangNut();
        bangDieuKhien.add(bangNut, BorderLayout.SOUTH);

        this.add(new JScrollPane(bangSanPham), BorderLayout.NORTH);
        this.add(bangDieuKhien, BorderLayout.CENTER);

        taiDuLieuVaoBang();
    }

    private void thietLapBang() {
        String[] tenCot = {"ID", "Tên SP", "Loại", "SL", "Giá Gốc", "Giá Bán"};
        moHinhBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        bangSanPham = new JTable(moHinhBang);
        bangSanPham.setFont(new Font("Arial", Font.PLAIN, 12));
        bangSanPham.setRowHeight(25);
        bangSanPham.setAutoCreateRowSorter(true);

        bangSanPham.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bangSanPham.getSelectedRow() != -1) {
                hienThiSanPhamDuocChon();
            }
        });
    }

    private JPanel thietLapBangForm() {
        JPanel bang = new JPanel(new GridLayout(3, 4, 10, 10));
        truongID = new JTextField(15);
        truongTen = new JTextField(15);
        truongSoLuong = new JTextField(15);
        truongGiaGoc = new JTextField(15);
        hopChonLoai = new JComboBox<>(DANH_SACH_LOAI);

        bang.add(new JLabel("ID Sản Phẩm:")); bang.add(truongID);
        bang.add(new JLabel("Tên Sản Phẩm:")); bang.add(truongTen);
        bang.add(new JLabel("Loại:")); bang.add(hopChonLoai);
        bang.add(new JLabel("Số Lượng:")); bang.add(truongSoLuong);
        bang.add(new JLabel("Giá Gốc (VNĐ):")); bang.add(truongGiaGoc);

        return bang;
    }

    private JPanel thietLapBangNut() {
        JPanel bang = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton nutThem = new JButton("Thêm Sản Phẩm");
        JButton nutSua = new JButton("Sửa Sản Phẩm");
        JButton nutXoa = new JButton("Xóa Sản Phẩm");
        JButton nutReset = new JButton("Làm mới/Tải lại");
        truongTimKiem = new JTextField(15);
        JButton nutTimKiem = new JButton("Tìm Kiếm (ID)");

        nutThem.addActionListener(e -> themSanPham());
        nutSua.addActionListener(e -> suaSanPham());
        nutXoa.addActionListener(e -> xoaSanPham());
        nutReset.addActionListener(e -> datLaiForm());
        nutTimKiem.addActionListener(e -> timKiemSanPham(truongTimKiem.getText()));

        bang.add(nutThem); bang.add(nutSua); bang.add(nutXoa);
        bang.add(nutReset); bang.add(truongTimKiem); bang.add(nutTimKiem);

        return bang;
    }

    // --- PHẦN SỬA ĐỔI ĐỂ ĐỒNG BỘ ---
    public void taiDuLieuVaoBang() {
        // Luôn đọc lại file để lấy số lượng mới nhất từ tab Giao dịch
        quanLySanPham.docFileTXT();
        dienDuLieuVaoBang(quanLySanPham.layDanhSachSanPham());
    }

    private void dienDuLieuVaoBang(List<SanPham> danhSachSanPham) {
        moHinhBang.setRowCount(0);
        for (SanPham sanPham : danhSachSanPham) {
            Object[] duLieuHang = {
                    sanPham.layID(),
                    sanPham.layTen(),
                    sanPham.layLoai(),
                    sanPham.laySoLuong(),
                    dinhDangTien.format(sanPham.layGiaGoc()),
                    dinhDangTien.format(sanPham.tinhGiaBan())
            };
            moHinhBang.addRow(duLieuHang);
        }
    }

    private void hienThiSanPhamDuocChon() {
        int dongDuocChon = bangSanPham.getSelectedRow();
        if (dongDuocChon == -1) return;

        String id = (String) bangSanPham.getValueAt(bangSanPham.convertRowIndexToModel(dongDuocChon), 0);
        SanPham sanPham = quanLySanPham.timSanPhamTheoID(id);

        if (sanPham != null) {
            truongID.setText(sanPham.layID());
            truongTen.setText(sanPham.layTen());
            hopChonLoai.setSelectedItem(sanPham.layLoai());
            truongSoLuong.setText(String.valueOf(sanPham.laySoLuong()));
            // SỬA: Dùng String.format để tránh lỗi hiển thị số thực lớn
            truongGiaGoc.setText(String.format("%.0f", sanPham.layGiaGoc()));
            truongID.setEditable(false);
        }
    }

    private SanPham taoSanPhamTuForm() {
        String id = truongID.getText().trim();
        String ten = truongTen.getText().trim();
        String loai = (String) hopChonLoai.getSelectedItem();
        String slText = truongSoLuong.getText().trim().replace(",", "");
        String giaGocText = truongGiaGoc.getText().trim().replace(",", "");

        if (id.isEmpty() || ten.isEmpty() || slText.isEmpty() || giaGocText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
            return null;
        }

        try {
            int soLuong = Integer.parseInt(slText);
            double giaGoc = Double.parseDouble(giaGocText);
            return new SanPham(id, ten, loai, soLuong, giaGoc);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng hoặc Giá không hợp lệ.");
            return null;
        }
    }

    private void themSanPham() {
        SanPham sanPhamMoi = taoSanPhamTuForm();
        if (sanPhamMoi != null && quanLySanPham.themSanPham(sanPhamMoi)) {
            taiDuLieuVaoBang();
            datLaiForm();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
        } else if (sanPhamMoi != null) {
            JOptionPane.showMessageDialog(this, "ID đã tồn tại!");
        }
    }

    private void suaSanPham() {
        String id = truongID.getText().trim();
        if (truongID.isEditable()) return;

        try {
            String ten = truongTen.getText().trim();
            String loai = (String) hopChonLoai.getSelectedItem();
            int sl = Integer.parseInt(truongSoLuong.getText().trim());
            double giaGoc = Double.parseDouble(truongGiaGoc.getText().trim());

            if (quanLySanPham.suaSanPham(id, ten, loai, sl, giaGoc)) {
                taiDuLieuVaoBang();
                datLaiForm();
                JOptionPane.showMessageDialog(this, "Sửa thành công!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi dữ liệu.");
        }
    }

    private void xoaSanPham() {
        String id = truongID.getText().trim();
        if (truongID.isEditable()) return;

        int xacNhan = JOptionPane.showConfirmDialog(this, "Xóa sản phẩm " + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (xacNhan == JOptionPane.YES_OPTION && quanLySanPham.xoaSanPham(id)) {
            taiDuLieuVaoBang();
            datLaiForm();
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
        }
    }

    private void timKiemSanPham(String id) {
        if (id.trim().isEmpty()) { taiDuLieuVaoBang(); return; }
        SanPham sp = quanLySanPham.timSanPhamTheoID(id.trim());
        List<SanPham> list = new ArrayList<>();
        if (sp != null) list.add(sp);
        dienDuLieuVaoBang(list);
    }

    private void datLaiForm() {
        truongID.setText(""); truongTen.setText("");
        truongSoLuong.setText(""); truongGiaGoc.setText("");
        hopChonLoai.setSelectedIndex(0);
        truongID.setEditable(true);
        bangSanPham.clearSelection();
        taiDuLieuVaoBang();
    }
}