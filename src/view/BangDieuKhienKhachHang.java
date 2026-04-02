package view;

import model.KhachHang;
import controller.QuanLyKhachHang;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BangDieuKhienKhachHang extends JPanel {

    private QuanLyKhachHang quanLyKhachHang;
    private JTable tableKhachHang;
    private DefaultTableModel moHinhBang;

    private JTextField fieldID, fieldHoTen, fieldNamSinh, fieldSDT, fieldDiaChi;
    private JComboBox<String> hopChonGioiTinh;
    private final String[] DANH_SACH_GIOI_TINH = {"Nam", "Nữ"};
    private JTextField fieldTimKiem;
    private javax.swing.table.TableRowSorter<DefaultTableModel> sorter;

    public BangDieuKhienKhachHang(QuanLyKhachHang quanLyKhachHang) {
        this.quanLyKhachHang = quanLyKhachHang;
        this.setLayout(new BorderLayout(10, 10));

        // Khởi tạo Table trước để các hàm sau không bị NullPointerException
        thietLapBang();

        JPanel bangDieuKhien = new JPanel(new BorderLayout(10, 10));
        bangDieuKhien.add(thietLapBangForm(), BorderLayout.CENTER);
        bangDieuKhien.add(thietLapBangNut(), BorderLayout.SOUTH);

        // CENTER: Bảng hiển thị
        JScrollPane scrollPane = new JScrollPane(tableKhachHang);
        this.add(scrollPane, BorderLayout.CENTER);

        // SOUTH: Form và Nút
        this.add(bangDieuKhien, BorderLayout.SOUTH);

        taiDuLieuVaoBang();
        resetForm(); // Để thiết lập trạng thái ban đầu cho ô ID
    }

    private void thietLapBang() {
        String[] tenCot = {"ID", "Họ tên", "Giới tính", "Năm sinh", "Sđt", "Địa chỉ"};
        moHinhBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableKhachHang = new JTable(moHinhBang);
        tableKhachHang.setRowHeight(25);
        tableKhachHang.setAutoCreateRowSorter(true);
        
        // --- UX: Thiết lập bộ lọc thời gian thực ---
        sorter = new javax.swing.table.TableRowSorter<>(moHinhBang);
        tableKhachHang.setRowSorter(sorter);

        tableKhachHang.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableKhachHang.getSelectedRow() != -1) {
                hienThiKhachHangDuocChon();
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

        fieldID = new JTextField(15);
        fieldID.setEditable(false);
        fieldHoTen = new JTextField(15);
        hopChonGioiTinh = new JComboBox<>(DANH_SACH_GIOI_TINH);
        fieldNamSinh = new JTextField(15);
        fieldSDT = new JTextField(15);
        fieldDiaChi = new JTextField(15);

        themVaoForm(bang, new JLabel("Mã khách hàng:"), 0, 0, gbc);
        themVaoForm(bang, fieldID, 1, 0, gbc);
        themVaoForm(bang, new JLabel("Họ tên:"), 2, 0, gbc);
        themVaoForm(bang, fieldHoTen, 3, 0, gbc);

        themVaoForm(bang, new JLabel("Giới tính:"), 0, 1, gbc);
        themVaoForm(bang, hopChonGioiTinh, 1, 1, gbc);
        themVaoForm(bang, new JLabel("Năm sinh:"), 2, 1, gbc);
        themVaoForm(bang, fieldNamSinh, 3, 1, gbc);

        themVaoForm(bang, new JLabel("Số điện thoại:"), 0, 2, gbc);
        themVaoForm(bang, fieldSDT, 1, 2, gbc);
        themVaoForm(bang, new JLabel("Địa chỉ:"), 2, 2, gbc);
        themVaoForm(bang, fieldDiaChi, 3, 2, gbc);

        return bang;
    }

    private void themVaoForm(JPanel p, JComponent c, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x; gbc.gridy = y;
        p.add(c, gbc);
    }

    private JPanel thietLapBangNut() {
        JPanel bangNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton nutThem = new JButton("+ Thêm mới");
        JButton nutSua = new JButton("✎ Cập nhật");
        JButton nutXoa = new JButton("🗑 Xóa");
        JButton nutReset = new JButton("↻ Làm mới");
        
        fieldTimKiem = new JTextField(15);
        fieldTimKiem.putClientProperty("JTextField.placeholderText", "🔍 Nhập mã hoặc tên khách hàng...");
        
        // --- UX: Lọc thời gian thực ---
        fieldTimKiem.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { loc(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { loc(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { loc(); }
            
            private void loc() {
                String val = fieldTimKiem.getText().trim();
                if (val.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    // Lọc trên cột ID (0) và Họ tên (1)
                    sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + val, 0, 1));
                }
            }
        });

        JButton nutTimKiem = new JButton("🔍 Tìm kiếm");

        nutThem.addActionListener(e -> themKhachHang());
        nutSua.addActionListener(e -> suaKhachHang());
        nutXoa.addActionListener(e -> xoaKhachHang());
        nutReset.addActionListener(e -> resetForm());
        nutTimKiem.addActionListener(e -> timKiemKhachHang(fieldTimKiem.getText()));

        bangNut.add(nutThem); bangNut.add(nutSua); bangNut.add(nutXoa);
        bangNut.add(nutReset); 
        bangNut.add(new JLabel("|"));
        bangNut.add(new JLabel("Tìm kiếm:")); 
        bangNut.add(fieldTimKiem); bangNut.add(nutTimKiem);

        return bangNut;
    }

    private void themKhachHang() {
        if (fieldHoTen.getText().trim().isEmpty() || fieldSDT.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ít nhất họ tên và số điện thoại!");
            return;
        }

        KhachHang khMoi = new KhachHang();
        String idMoi = quanLyKhachHang.phatSinhIDTuDong(); 

        khMoi.setID(idMoi);
        khMoi.setHoTen(fieldHoTen.getText().trim());
        khMoi.setGioiTinh((String) hopChonGioiTinh.getSelectedItem());
        khMoi.setNamSinh(fieldNamSinh.getText().trim());
        khMoi.setSdt(fieldSDT.getText().trim());
        khMoi.setDiaChi(fieldDiaChi.getText().trim());

        boolean success = quanLyKhachHang.themKhachHang(khMoi);
        if (success) {
            taiDuLieuVaoBang();
            resetForm();
            JOptionPane.showMessageDialog(this, "Thêm thành công! ID khách hàng là: " + idMoi);
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm khách hàng vào database!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaKhachHang() {
        int row = tableKhachHang.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng từ bảng!");
            return;
        }

        KhachHang khMoi = new KhachHang();
        khMoi.setID(fieldID.getText().trim());
        khMoi.setHoTen(fieldHoTen.getText().trim());
        khMoi.setGioiTinh((String) hopChonGioiTinh.getSelectedItem());
        khMoi.setNamSinh(fieldNamSinh.getText().trim());
        khMoi.setSdt(fieldSDT.getText().trim());
        khMoi.setDiaChi(fieldDiaChi.getText().trim());

        boolean success = quanLyKhachHang.suaKhachHang(khMoi);
        if (success) {
            taiDuLieuVaoBang();
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật khách hàng vào database!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaKhachHang() {
        int row = tableKhachHang.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa!");
            return;
        }

        String id = (String) tableKhachHang.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa khách hàng " + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = quanLyKhachHang.xoaKhachHang(id);
            if (success) {
                taiDuLieuVaoBang();
                resetForm();
                JOptionPane.showMessageDialog(this, "Đã xóa khách hàng (soft delete).");
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa khách hàng trong database!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
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
        quanLyKhachHang.refreshData();
        dienDuLieuVaoBang(quanLyKhachHang.getKhachHangList());
    }

    private void hienThiKhachHangDuocChon() {
        int row = tableKhachHang.getSelectedRow();
        if (row != -1) {
            fieldID.setText(tableKhachHang.getValueAt(row, 0).toString());
            fieldHoTen.setText(tableKhachHang.getValueAt(row, 1).toString());
            hopChonGioiTinh.setSelectedItem(tableKhachHang.getValueAt(row, 2).toString());
            fieldNamSinh.setText(tableKhachHang.getValueAt(row, 3).toString());
            fieldSDT.setText(tableKhachHang.getValueAt(row, 4).toString());
            fieldDiaChi.setText(tableKhachHang.getValueAt(row, 5).toString());

            fieldID.setEditable(false); // Không cho sửa ID khi đang chọn từ bảng
            fieldID.setBackground(Color.LIGHT_GRAY);
        }
    }

    private void resetForm() {
        fieldID.setText("ID tự động");
        fieldID.setEditable(false);
        fieldHoTen.setText("");
        fieldNamSinh.setText("");
        fieldSDT.setText("");
        fieldDiaChi.setText("");
        hopChonGioiTinh.setSelectedIndex(0);
        tableKhachHang.clearSelection();
        taiDuLieuVaoBang();
    }
}
