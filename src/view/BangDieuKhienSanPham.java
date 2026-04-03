package view;

import model.SanPham;
import controller.QuanLySanPham;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import util.DinhDang;
import com.formdev.flatlaf.extras.FlatSVGIcon;

public class BangDieuKhienSanPham extends JPanel {

    private QuanLySanPham quanLySanPham;
    private JTable tableSanPham;
    private DefaultTableModel moHinhBang;
    private JTextField fieldID, fieldTen, fieldGiaBan, fieldBaoHanh;
    private JComboBox<String> hopChonLoai;
    private JTextField fieldSearch;
    private javax.swing.table.TableRowSorter<DefaultTableModel> boLoc;

    private final String[] DANH_SACH_LOAI = { "Laptop", "Desktop", "Linh kiện", "Màn hình", "Chuột & Bàn phím",
            "Khác" };

    public BangDieuKhienSanPham(QuanLySanPham qlsp) {
        this.quanLySanPham = qlsp;

        initComponents();
        initEvents();

        taiDuLieuVaoBang();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Table Section (Center) ---
        thietLapBang();
        add(new JScrollPane(tableSanPham), BorderLayout.CENTER);

        // --- Control Section (South) ---
        JPanel pnlControl = new JPanel(new BorderLayout(10, 10));
        pnlControl.add(thietLapBangForm(), BorderLayout.CENTER);
        pnlControl.add(thietLapBangNut(), BorderLayout.SOUTH);
        add(pnlControl, BorderLayout.SOUTH);
    }

    private void thietLapBang() {
        String[] tenCot = { "Mã SP", "Tên sản phẩm", "Loại", "Giá bán", "Bảo hành (tháng)", "Trạng thái" };
        moHinhBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableSanPham = new JTable(moHinhBang);
        tableSanPham.setRowHeight(30);
        tableSanPham.getTableHeader().setReorderingAllowed(false);

        // --- UX: Thiết lập bộ lọc thời gian thực ---
        boLoc = new javax.swing.table.TableRowSorter<>(moHinhBang);
        tableSanPham.setRowSorter(boLoc);

        tableSanPham.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableSanPham.getSelectedRow() != -1) {
                hienThiSanPhamDuocChon();
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
        fieldTen = new JTextField(15);
        fieldGiaBan = new JTextField(15);
        fieldBaoHanh = new JTextField(15);
        hopChonLoai = new JComboBox<>(DANH_SACH_LOAI);

        themVaoForm(bang, new JLabel("Mã sản phẩm:"), 0, 0, gbc);
        themVaoForm(bang, fieldID, 1, 0, gbc);
        themVaoForm(bang, new JLabel("Tên sản phẩm:"), 2, 0, gbc);
        themVaoForm(bang, fieldTen, 3, 0, gbc);

        themVaoForm(bang, new JLabel("Loại sản phẩm:"), 0, 1, gbc);
        themVaoForm(bang, hopChonLoai, 1, 1, gbc);
        themVaoForm(bang, new JLabel("Giá bán:"), 2, 1, gbc);
        themVaoForm(bang, fieldGiaBan, 3, 1, gbc);

        themVaoForm(bang, new JLabel("Bảo hành (tháng):"), 0, 2, gbc);
        themVaoForm(bang, fieldBaoHanh, 1, 2, gbc);

        return bang;
    }

    private void themVaoForm(JPanel p, JComponent c, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x;
        gbc.gridy = y;
        p.add(c, gbc);
    }

    private JPanel thietLapBangNut() {
        JPanel bangNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnCreate = new JButton("Thêm mới");
        btnCreate.setIcon(new FlatSVGIcon("resources/icons/plus.svg", 16, 16));

        JButton btnUpdate = new JButton("Cập nhật");
        btnUpdate.setIcon(new FlatSVGIcon("resources/icons/edit-3.svg", 16, 16));

        JButton btnDelete = new JButton("Xóa");
        btnDelete.setIcon(new FlatSVGIcon("resources/icons/trash-2.svg", 16, 16));

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setIcon(new FlatSVGIcon("resources/icons/refresh-cw.svg", 16, 16));

        fieldSearch = new JTextField(25);
        fieldSearch.putClientProperty("JTextField.placeholderText", "🔍 Nhập mã hoặc tên sản phẩm để lọc nhanh...");

        bangNut.add(btnCreate);
        bangNut.add(btnUpdate);
        bangNut.add(btnDelete);
        bangNut.add(btnRefresh);
        bangNut.add(new JLabel(" | "));
        bangNut.add(new JLabel("Tìm kiếm: "));
        bangNut.add(fieldSearch);

        // Action Listeners
        btnCreate.addActionListener(e -> themSanPham());
        btnUpdate.addActionListener(e -> suaSanPham());
        btnDelete.addActionListener(e -> xoaSanPham());
        btnRefresh.addActionListener(e -> resetForm());

        return bangNut;
    }

    private void initEvents() {
        // --- UX: Lắng nghe thay đổi trên ô tìm kiếm để lọc ngay lập tức ---
        fieldSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                thucHienLoc();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                thucHienLoc();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                thucHienLoc();
            }

            private void thucHienLoc() {
                String text = fieldSearch.getText().trim();
                if (text.isEmpty()) {
                    boLoc.setRowFilter(null);
                } else {
                    boLoc.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + text, 0, 1));
                }
            }
        });

        tableSanPham.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableSanPham.getSelectedRow() != -1) {
                hienThiSanPhamDuocChon();
            }
        });
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
        int dongDuocChon = tableSanPham.getSelectedRow();
        if (dongDuocChon == -1)
            return;

        String id = (String) tableSanPham.getValueAt(tableSanPham.convertRowIndexToModel(dongDuocChon), 0);
        SanPham sanPham = quanLySanPham.timSanPhamTheoID(id);

        if (sanPham != null) {
            fieldID.setText(sanPham.getMaSP());
            fieldTen.setText(sanPham.getTenSP());
            hopChonLoai.setSelectedItem(sanPham.getLoaiSP());
            fieldGiaBan.setText(String.format("%.0f", sanPham.getGiaBan()));
            fieldBaoHanh.setText(String.valueOf(sanPham.getTgBaoHanh()));
            fieldID.setEditable(false);
        }
    }

    private void themSanPham() {
        String id = fieldID.getText().trim();
        String ten = fieldTen.getText().trim();
        String loai = (String) hopChonLoai.getSelectedItem();
        String giaBanStr = fieldGiaBan.getText().trim();
        String baoHanhStr = fieldBaoHanh.getText().trim();

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
        String id = fieldID.getText().trim();
        if (fieldID.isEditable())
            return;

        try {
            String ten = fieldTen.getText().trim();
            String loai = (String) hopChonLoai.getSelectedItem();
            double giaBan = Double.parseDouble(fieldGiaBan.getText().trim());
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
        String id = fieldID.getText().trim();
        if (fieldID.isEditable())
            return;

        int xacNhan = JOptionPane.showConfirmDialog(this, "Xóa danh mục sản phẩm " + id + "?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (xacNhan == JOptionPane.YES_OPTION && quanLySanPham.xoaSanPham(id)) {
            taiDuLieuVaoBang();
            resetForm();
            JOptionPane.showMessageDialog(this, "Đã xóa sản phẩm khỏi danh mục.");
        }
    }

    private void resetForm() {
        fieldID.setText("");
        fieldTen.setText("");
        fieldGiaBan.setText("");
        fieldBaoHanh.setText("");
        hopChonLoai.setSelectedIndex(0);
        fieldID.setEditable(true);
        tableSanPham.clearSelection();
        taiDuLieuVaoBang();
    }
}
