package view;

import model.SanPham;
import controller.QuanLySanPham;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import util.DinhDang;

public class BangDieuKhienSanPham extends JPanel {

    private QuanLySanPham quanLySanPham;
    private JTable bangSanPham;
    private DefaultTableModel moHinhBang;
    private JTextField truongID, truongTen, truongGiaBan, truongBaoHanh;
    private JComboBox<String> hopChonLoai;
    private JTextField truongTimKiem;

    private final String[] DANH_SACH_LOAI = {"Laptop", "PC", "Linh kiện", "Màn hình", "Chuột & Bàn phím", "Khác"};

    public BangDieuKhienSanPham(QuanLySanPham qlsp) {
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
        String[] tenCot = {"Mã SP", "Tên Sản Phẩm", "Loại", "Giá Bán", "Bảo Hành (Tháng)", "Trạng Thái"};
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
        truongGiaBan = new JTextField(15);
        truongBaoHanh = new JTextField(15);
        hopChonLoai = new JComboBox<>(DANH_SACH_LOAI);

        bang.add(new JLabel("Mã Sản Phẩm:")); bang.add(truongID);
        bang.add(new JLabel("Tên Sản Phẩm:")); bang.add(truongTen);
        bang.add(new JLabel("Loại SP:")); bang.add(hopChonLoai);
        bang.add(new JLabel("Giá Bán:")); bang.add(truongGiaBan);
        bang.add(new JLabel("Bảo Hành (Tháng):")); bang.add(truongBaoHanh);

        return bang;
    }

    private JPanel thietLapBangNut() {
        JPanel bang = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton nutThem = new JButton("Thêm SP (Danh mục)");
        JButton nutSua = new JButton("Sửa SP (Danh mục)");
        JButton nutXoa = new JButton("Xóa SP (Danh mục)");
        JButton nutReset = new JButton("Làm mới UI");
        truongTimKiem = new JTextField(15);
        JButton nutTimKiem = new JButton("Tìm ID");

        nutThem.addActionListener(e -> themSanPham());
        nutSua.addActionListener(e -> suaSanPham());
        nutXoa.addActionListener(e -> xoaSanPham());
        nutReset.addActionListener(e -> resetForm());
        nutTimKiem.addActionListener(e -> timKiemSanPham(truongTimKiem.getText()));

        bang.add(nutThem); bang.add(nutSua); bang.add(nutXoa);
        bang.add(nutReset); bang.add(truongTimKiem); bang.add(nutTimKiem);

        return bang;
    }

    public void taiDuLieuVaoBang() {
        quanLySanPham.refreshData(); // Gọi hàm này để reload từ DB
        dienDuLieuVaoBang(quanLySanPham.getSanPhamList());
    }

    private void dienDuLieuVaoBang(List<SanPham> danhSachSanPham) {
        moHinhBang.setRowCount(0);
        for (SanPham sanPham : danhSachSanPham) {
            Object[] duLieuHang = {
                    sanPham.getMaSP(),
                    sanPham.getTenSP(),
                    sanPham.getLoaiSP(),
                    DinhDang.tien(sanPham.getGiaBan()),
                    sanPham.getTgBaoHanh(),
                    sanPham.getTrangThai()
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
            truongID.setText(sanPham.getMaSP());
            truongTen.setText(sanPham.getTenSP());
            hopChonLoai.setSelectedItem(sanPham.getLoaiSP());
            truongGiaBan.setText(String.format("%.0f", sanPham.getGiaBan()));
            truongBaoHanh.setText(String.valueOf(sanPham.getTgBaoHanh()));
            truongID.setEditable(false);
        }
    }

    private void themSanPham() {
        String id = truongID.getText().trim();
        String ten = truongTen.getText().trim();
        String loai = (String) hopChonLoai.getSelectedItem();
        String giaBanStr = truongGiaBan.getText().trim();
        String baoHanhStr = truongBaoHanh.getText().trim();

        if (id.isEmpty() || ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã và Tên sản phẩm.");
            return;
        }

        try {
            double giaBan = Double.parseDouble(giaBanStr);
            int baoHanh = Integer.parseInt(baoHanhStr);
            SanPham sanPhamMoi = new SanPham(id, ten, loai, "NCC01", giaBan, baoHanh, "Đang kinh doanh");
            if (quanLySanPham.themSanPham(sanPhamMoi)) {
                taiDuLieuVaoBang();
                resetForm();
                JOptionPane.showMessageDialog(this, "Thêm danh mục SP thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Mã SP đã tồn tại!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Giá bán hoặc Bảo hành không hợp lệ.");
        }
    }

    private void suaSanPham() {
        String id = truongID.getText().trim();
        if (truongID.isEditable()) return;

        try {
            String ten = truongTen.getText().trim();
            String loai = (String) hopChonLoai.getSelectedItem();
            double giaBan = Double.parseDouble(truongGiaBan.getText().trim());
            // Gọi hàm suaSanPham đã refactor (loại bỏ tham số số lượng)
            if (quanLySanPham.suaSanPham(id, ten, loai, giaBan)) {
                taiDuLieuVaoBang();
                resetForm();
                JOptionPane.showMessageDialog(this, "Cập nhật danh mục thành công!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi định dạng dữ liệu.");
        }
    }

    private void xoaSanPham() {
        String id = truongID.getText().trim();
        if (truongID.isEditable()) return;

        int xacNhan = JOptionPane.showConfirmDialog(this, "Xóa danh mục sản phẩm " + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (xacNhan == JOptionPane.YES_OPTION && quanLySanPham.xoaSanPham(id)) {
            taiDuLieuVaoBang();
            resetForm();
            JOptionPane.showMessageDialog(this, "Đã xóa sản phẩm khỏi danh mục.");
        }
    }

    private void timKiemSanPham(String id) {
        if (id.trim().isEmpty()) { taiDuLieuVaoBang(); return; }
        SanPham sp = quanLySanPham.timSanPhamTheoID(id.trim());
        List<SanPham> list = new ArrayList<>();
        if (sp != null) list.add(sp);
        dienDuLieuVaoBang(list);
    }

    private void resetForm() {
        truongID.setText(""); 
        truongTen.setText("");
        truongGiaBan.setText(""); 
        truongBaoHanh.setText("");
        hopChonLoai.setSelectedIndex(0);
        truongID.setEditable(true);
        bangSanPham.clearSelection();
        taiDuLieuVaoBang();
    }
}
