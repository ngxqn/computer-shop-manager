package view;

import model.*;
import controller.QuanLyNhanVien;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import util.DinhDang;

public class BangDieuKhienNhanVien extends JPanel {

    private QuanLyNhanVien quanLyNhanVien;
    private JTable tableNhanVien;
    private DefaultTableModel moHinhBang;

    private JTextField fieldID, fieldHoTen, fieldNamSinh, fieldSoNgayNghi;
    private JTextField fieldSDT, fieldDiaChi, fieldTimKiem;
    private javax.swing.table.TableRowSorter<DefaultTableModel> sorter;
    private JComboBox<String> hopChonGioiTinh, hopChonChucVu;

    private final String[] DANH_SACH_CHUC_VU = {"Ban Hang", "Thu Ngan", "Quan Li Kho"};
    private final String[] DANH_SACH_GIOI_TINH = {"Nam", "Nu"};

    public BangDieuKhienNhanVien(QuanLyNhanVien quanLyNhanVien) {
        this.quanLyNhanVien = quanLyNhanVien;
        
        initComponents();
        initEvents();
        
        taiDuLieuVaoBang();
        fieldID.setText(quanLyNhanVien.phatSinhIDTuDong());
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Table Section (Center) ---
        thietLapBang();
        add(new JScrollPane(tableNhanVien), BorderLayout.CENTER);

        // --- Control Section (South) ---
        JPanel pnlControl = new JPanel(new BorderLayout(10, 10));
        pnlControl.add(thietLapBangForm(), BorderLayout.CENTER);
        pnlControl.add(thietLapBangNut(), BorderLayout.SOUTH);
        add(pnlControl, BorderLayout.SOUTH);
    }

    private void thietLapBang() {
        String[] tenCot = {"ID", "Họ tên", "Giới tính", "Năm sinh", "SĐT", "Địa chỉ", "Chức vụ", "Xếp loại", "Tổng lương"};
        moHinhBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableNhanVien = new JTable(moHinhBang);
        tableNhanVien.setRowHeight(30);
        tableNhanVien.getTableHeader().setReorderingAllowed(false);
        
        // --- UX: Thiết lập bộ lọc thời gian thực ---
        sorter = new javax.swing.table.TableRowSorter<>(moHinhBang);
        tableNhanVien.setRowSorter(sorter);

        tableNhanVien.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableNhanVien.getSelectedRow() != -1) {
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

        fieldID = new JTextField(15);
        fieldID.setEditable(false);
        fieldHoTen = new JTextField(15);
        hopChonGioiTinh = new JComboBox<>(DANH_SACH_GIOI_TINH);
        fieldNamSinh = new JTextField(15);
        fieldSDT = new JTextField(15);
        fieldDiaChi = new JTextField(15);
        hopChonChucVu = new JComboBox<>(DANH_SACH_CHUC_VU);
        fieldSoNgayNghi = new JTextField(15);

        themVaoForm(bang, new JLabel("Mã nhân viên:"), 0, 0, gbc);
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

        themVaoForm(bang, new JLabel("Chức vụ:"), 0, 3, gbc);
        themVaoForm(bang, hopChonChucVu, 1, 3, gbc);
        themVaoForm(bang, new JLabel("Số ngày nghỉ:"), 2, 3, gbc);
        themVaoForm(bang, fieldSoNgayNghi, 3, 3, gbc);

        return bang;
    }

    private void themVaoForm(JPanel p, JComponent c, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x; gbc.gridy = y;
        p.add(c, gbc);
    }

    private JPanel thietLapBangNut() {
        JPanel bangNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton nutThem = new JButton("Thêm mới");
        JButton nutSua = new JButton("Cập nhật");
        JButton nutXoa = new JButton("Xóa");
        JButton nutReset = new JButton("Làm mới");

        fieldTimKiem = new JTextField(25);
        fieldTimKiem.putClientProperty("JTextField.placeholderText", "🔍 Nhập mã hoặc tên nhân viên để lọc nhanh...");
        
        bangNut.add(nutThem); 
        bangNut.add(nutSua); 
        bangNut.add(nutXoa);
        bangNut.add(nutReset); 
        bangNut.add(new JLabel(" | "));
        bangNut.add(new JLabel("Tìm kiếm: ")); 
        bangNut.add(fieldTimKiem);

        // Action Listeners
        nutThem.addActionListener(e -> themNhanVien());
        nutSua.addActionListener(e -> suaNhanVien());
        nutXoa.addActionListener(e -> xoaNhanVien());
        nutReset.addActionListener(e -> resetForm());

        return bangNut;
    }

    private void initEvents() {
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
                    sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + val, 0, 1));
                }
            }
        });

        tableNhanVien.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableNhanVien.getSelectedRow() != -1) {
                hienThiNhanVienDuocChon();
            }
        });
    }

    // --- CẬP NHẬT LOGIC TÌM KIẾM ---

    // --- CÁC PHƯƠNG THỨC KHÁC GIỮ NGUYÊN ---
    private void taiDuLieuVaoBang() {
        quanLyNhanVien.refreshData();
        moHinhBang.setRowCount(0);
        for (NhanVien nv : quanLyNhanVien.getNhanVienList()) {
            moHinhBang.addRow(new Object[]{
                    nv.getID(), nv.getHoTen(), nv.getGioiTinh(), nv.getNamSinh(),
                    nv.getSdt(), nv.getDiaChi(), nv.getChucVu(), nv.xepLoai(),
                    DinhDang.tien(nv.tinhLuong())
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
        String id = fieldID.getText();
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
        String id = fieldID.getText();
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
        int row = tableNhanVien.getSelectedRow();
        if (row == -1) return;
        String id = (String) tableNhanVien.getValueAt(row, 0);
        NhanVien nv = quanLyNhanVien.timNhanVienTheoID(id);
        if (nv != null) {
            fieldID.setText(nv.getID());
            fieldHoTen.setText(nv.getHoTen());
            hopChonGioiTinh.setSelectedItem(nv.getGioiTinh());
            fieldNamSinh.setText(nv.getNamSinh());
            fieldSDT.setText(nv.getSdt());
            fieldDiaChi.setText(nv.getDiaChi());
            fieldSoNgayNghi.setText(String.valueOf(nv.getSoNgayNghi()));
        }
    }

    private void resetForm() {
        fieldID.setText(quanLyNhanVien.phatSinhIDTuDong());
        fieldHoTen.setText(""); fieldNamSinh.setText("");
        fieldSDT.setText(""); fieldDiaChi.setText("");
        fieldSoNgayNghi.setText(""); fieldTimKiem.setText("");
        tableNhanVien.clearSelection();
        taiDuLieuVaoBang();
    }

    private NhanVien taoNhanVienTuForm() {
        try {
            String id = fieldID.getText();
            String ten = fieldHoTen.getText().trim();
            String gt = (String) hopChonGioiTinh.getSelectedItem();
            String ns = fieldNamSinh.getText().trim();
            String sdt = fieldSDT.getText().trim();
            String dc = fieldDiaChi.getText().trim();
            String cv = (String) hopChonChucVu.getSelectedItem();
            int nghi = Integer.parseInt(fieldSoNgayNghi.getText().trim());
            if (ten.isEmpty()) throw new Exception("Tên không được để trống!");
            if (cv.equals("Ban Hang")) return new NhanVienBanHang(cv, id, ten, gt, ns, sdt, dc, nghi);
            if (cv.equals("Thu Ngan")) return new NhanVienThuNgan(cv, id, ten, gt, ns, sdt, dc, nghi);
            return new NhanVienQuanLyKho(cv, id, ten, gt, ns, sdt, dc, nghi);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi dữ liệu: " + e.getMessage());
            return null;
        }
    }
}
