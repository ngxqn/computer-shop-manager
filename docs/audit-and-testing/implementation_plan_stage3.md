# Kế Hoạch Triển Khai Giai Đoạn 3 (Stage 3): Hoàn Thiện Hệ Thống JDBC

## Tổng Quan

Stage 2 đã hoàn thành việc JDBC hóa luồng cốt lõi (POS, Bảo hành, Báo cáo) với đầy đủ Transaction và logic nghiệp vụ đúng đắn. Tuy nhiên, hệ thống vẫn ở **trạng thái lai (Hybrid)**:

- **DAO**: `NhanVienDAO`, `KhachHangDAO` chỉ có `getAll()` — chưa có CRUD.
- **Controller**: `QuanLyNhanVien`, `QuanLyKhachHang` — Thêm/Sửa/Xóa đang thao tác trên **List in-memory**, mất dữ liệu khi tắt app.
- **View**: `BangDieuKhienNhanVien`, `BangDieuKhienKhachHang` — gọi `.add()`, `.set()`, `.remove()` trực tiếp lên List, **bỏ qua DAO hoàn toàn**.
- **Module Kho**: `QuanLyKho` dùng `List<Kho>` in-memory, số lượng tồn kho không phản ánh DB thực tế.
- **UI POS & Bảo hành**: Hardcode `"NV01"` và `"Khách vãng lai"` dưới dạng plain text — rủi ro **Foreign Key Constraint** cao.

Mục tiêu của Stage 3 là **đóng hoàn toàn tất cả khoảng cách** này.

---

## Yêu Cầu Cần Xem Xét

> [!IMPORTANT]
> **Thứ tự triển khai có phụ thuộc (Dependency Order):** STEP 1 (CRUD DAO) phải được hoàn thành trước STEP 4 (Session & Lookup UI), vì UI tra cứu cần gọi `NhanVienDAO.getAll()` và `KhachHangDAO.getAll()` để nạp dữ liệu vào ComboBox.

> [!WARNING]
> **Rủi ro FK khi Delete:** Khi xóa Nhân viên (`NHANVIEN`) hoặc Khách hàng (`KHACHHANG`), MySQL sẽ chặn nếu họ còn dữ liệu liên kết trong `HOADON` hoặc `PHIEUBAOHANH`. Chiến lược xử lý được đề xuất là **Soft Delete** (cập nhật `TrangThai = 'Ngừng hoạt động'`) thay vì `DELETE` vật lý.

> [!NOTE]
> **Thiết kế `KhoBiz`:** Để tránh xung đột logic, `QuanLyKho` sẽ được đơn giản hóa thành một lớp facade mỏng, delegate hoàn toàn sang `SeriSanPhamDAO`. Lớp `Kho.java` và `PhieuNhapKho.java` trở thành model thuần (POJO).

---

## Proposed Changes

### STEP 1: Hoàn Thiện Full JDBC CRUD — NhanVien & KhachHang

Đây là bước quan trọng nhất — chuyển 2 thực thể còn lại sang JDBC hoàn toàn.

---

#### [MODIFY] [NhanVienDAO.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/dao/NhanVienDAO.java)

Bổ sung 3 phương thức:

| Method | SQL | Ghi chú |
|---|---|---|
| `insert(NhanVien nv)` | `INSERT INTO NHANVIEN (...)` | Dùng `PreparedStatement`, trả về `boolean` |
| `update(NhanVien nv)` | `UPDATE NHANVIEN SET ... WHERE MaNV=?` | Cập nhật tất cả các field trừ `MaNV` |
| `delete(String maNV)` | `UPDATE NHANVIEN SET TrangThai='Ngừng hoạt động' WHERE MaNV=?` | **Soft Delete** để bảo vệ tính toàn vẹn referential |

```java
// Ví dụ cấu trúc insert()
public boolean insert(NhanVien nv) {
    String sql = "INSERT INTO NHANVIEN (MaNV, HoTen, GioiTinh, NamSinh, SDT, DiaChi, ChucVu, SoNgayNghi, CaLamViec) VALUES (?,?,?,?,?,?,?,?,?)";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, nv.getID());
        // ... set các tham số còn lại
        ps.executeUpdate();
        return true;
    } catch (SQLException | ClassNotFoundException e) {
        System.err.println("Lỗi insert NhanVien: " + e.getMessage());
        return false;
    }
}
```

---

#### [MODIFY] [KhachHangDAO.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/dao/KhachHangDAO.java)

Tương tự `NhanVienDAO`, bổ sung:

| Method | SQL | Ghi chú |
|---|---|---|
| `insert(KhachHang kh)` | `INSERT INTO KHACHHANG (...)` | |
| `update(KhachHang kh)` | `UPDATE KHACHHANG SET ... WHERE MaKH=?` | |
| `delete(String maKH)` | `UPDATE KHACHHANG SET TrangThai='Ngừng hoạt động' WHERE MaKH=?` | Soft Delete |

---

#### [MODIFY] [QuanLyNhanVien.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/controller/QuanLyNhanVien.java)

**Vấn đề hiện tại:** Controller không có hàm `them/sua/xoa` — View đang thao tác thẳng lên List.

Bổ sung các method gọi DAO:

```java
public boolean themNhanVien(NhanVien nv) {
    boolean success = nhanVienDAO.insert(nv);
    if (success) refreshData(); // Đồng bộ lại List từ DB
    return success;
}

public boolean suaNhanVien(NhanVien nv) {
    boolean success = nhanVienDAO.update(nv);
    if (success) refreshData();
    return success;
}

public boolean xoaNhanVien(String maNV) {
    boolean success = nhanVienDAO.delete(maNV); // Soft delete
    if (success) refreshData();
    return success;
}
```

> Xóa bỏ `xuatDanhSachNhanVien()` (dùng `System.out`) vì đây là code CLI cũ không cần thiết.

---

#### [MODIFY] [QuanLyKhachHang.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/controller/QuanLyKhachHang.java)

Điền vào các stub hiện có và bổ sung giống `QuanLyNhanVien`:

```java
public boolean themKhachHang(KhachHang kh) { ... }
public boolean suaKhachHang(KhachHang kh)  { ... } // Thay thế stub suaKhachHang(String id)
public boolean xoaKhachHang(String maKH)   { ... } // Soft delete
```

---

#### [MODIFY] [BangDieuKhienNhanVien.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/view/BangDieuKhienNhanVien.java)

**Refactor 3 hàm nghiệp vụ** để thay vì thao tác on List, gọi Controller:

```java
// TRƯỚC (thao tác in-memory — SAI)
quanLyNhanVien.getNhanVienList().add(nvMoi);

// SAU (gọi JDBC qua Controller — ĐÚNG)
boolean ok = quanLyNhanVien.themNhanVien(nvMoi);
if (ok) {
    taiDuLieuVaoBang(); // refresh từ DB
    JOptionPane.showMessageDialog(this, "Thêm thành công!");
} else {
    JOptionPane.showMessageDialog(this, "Lỗi! Kiểm tra kết nối DB.", "Lỗi", JOptionPane.ERROR_MESSAGE);
}
```

> Áp dụng pattern tương tự cho `suaNhanVien()` và `xoaNhanVien()`.

---

#### [MODIFY] [BangDieuKhienKhachHang.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/view/BangDieuKhienKhachHang.java)

Refactor `themKhachHang()`, `suaKhachHang()`, `xoaKhachHang()` theo cùng pattern — gọi Controller thay vì thao tác List.

---

#### [MODIFY] [NhanVien.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/model/NhanVien.java)

- Xóa `nhap(Scanner sc)` và `xuat()` — legacy console code.
- Xóa `getDataString()` — legacy file I/O serialization.
- Giữ nguyên `tinhLuong()`, `xepLoai()`, `heSoXepLoai()`, `heSoCa()` — đây là **business logic** cần thiết cho bảng UI.

---

### STEP 2: Cải Tổ Module Kho & Luồng Nhập Hàng

---

#### [MODIFY] [SeriSanPhamDAO.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/dao/SeriSanPhamDAO.java)

Bổ sung 2 phương thức:

**1. `demTonKho(String maSP)`** — Live Counting thay thế in-memory counter:

```java
public int demTonKho(String maSP) {
    String sql = "SELECT COUNT(*) FROM SERISANPHAM WHERE MaSP = ? AND TinhTrang = 'Trong kho'";
    // ... trả về số nguyên
}
```

**2. `nhapKhoHangLoat(List<String> danhSachSeri, String maSP, String maPN)`** — Batch insert serials mới:

```java
public boolean nhapKhoHangLoat(List<String> danhSachSeri, String maSP, String maPN) {
    // Dùng PreparedStatement trong một Transaction
    // INSERT INTO SERISANPHAM (MaSeri, MaSP, MaPN, TinhTrang) VALUES (?, ?, ?, 'Trong kho')
    // Rollback toàn bộ nếu bất kỳ insert nào thất bại
}
```

---

#### [NEW] [PhieuNhapDAO.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/dao/PhieuNhapDAO.java)

Tạo DAO mới để quản lý bảng `PHIEUNHAP` và `CHITIETPHIEUNHAP`:

```java
public class PhieuNhapDAO {
    // Tạo phiếu nhập + insert serial hàng loạt trong 1 Transaction
    public boolean taoPhieuNhapVaNhapSeri(String maNV, String maNCC, 
                                           String maSP, double donGiaNhap,
                                           List<String> danhSachSeri) { ... }
    
    public List<Object[]> getLichSuNhapKho() { ... }
}
```

---

#### [MODIFY] [QuanLyKho.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/controller/QuanLyKho.java)

**Đơn giản hóa triệt để** — loại bỏ `List<Kho>` in-memory, chỉ giữ vai trò facade:

```java
public class QuanLyKho {
    private SeriSanPhamDAO seriDAO = new SeriSanPhamDAO();
    private PhieuNhapDAO phieuNhapDAO = new PhieuNhapDAO();

    // Trả số tồn kho thực tế từ DB
    public int getTonKho(String maSP) {
        return seriDAO.demTonKho(maSP);
    }

    // Luồng nhập hàng chính
    public boolean thucHienNhapHang(String maNV, String maNCC, String maSP,
                                     double donGia, List<String> serials) {
        return phieuNhapDAO.taoPhieuNhapVaNhapSeri(maNV, maNCC, maSP, donGia, serials);
    }
}
```

---

#### [MODIFY] [BangDieuKhienGiaoDich.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/view/BangDieuKhienGiaoDich.java)

Nâng cấp từ "Xuất kho nội bộ" đơn giản thành **màn hình Nhập Hàng** có chức năng:
- Chọn sản phẩm và nhà cung cấp (từ JComboBox nạp từ DB).
- Nhập `JTextArea` cho danh sách Serial (mỗi dòng 1 Serial — batch input).
- Cột "Tồn kho hiện tại" trong bảng sẽ gọi `QuanLyKho.getTonKho()` (Live Count từ DB).

---

### STEP 3: Hoàn Thiện Vòng Đời Bảo Hành

---

#### [MODIFY] [PhieuBaoHanhDAO.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/dao/PhieuBaoHanhDAO.java)

Bổ sung 2 phương thức:

**1. `getAll()`** — Lấy toàn bộ phiếu bảo hành hiện có:

```java
public List<PhieuBaoHanh> getAll() { ... }
```

**2. `capNhatTrangThai(String maPBH, String trangThaiMoi)`** — Cập nhật vòng đời phiếu + đồng bộ Serial:

```java
// Logic vòng đời: "Đang xử lý" -> "Hoàn thành" -> "Đã trả máy"
public boolean capNhatTrangThai(String maPBH, String trangThaiMoi) {
    // Transaction:
    // 1. UPDATE PHIEUBAOHANH SET TinhTrang = ? WHERE MaPBH = ?
    // 2a. Nếu trangThaiMoi = "Hoàn thành": UPDATE SERISANPHAM SET TinhTrang = 'Đang bảo hành'
    // 2b. Nếu trangThaiMoi = "Đã trả máy":  UPDATE SERISANPHAM SET TinhTrang = 'Đã bán' (trả về tay KH)
}
```

---

#### [NEW] [BangDieuKhienBaoHanh.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/view/BangDieuKhienBaoHanh.java)

Tạo một **JPanel tab mới** "Quản Lý Bảo Hành" hiển thị danh sách tất cả phiếu bảo hành với:
- Bảng dữ liệu: `MaPBH`, `MaSeri`, `MaKH`, `MaNV`, `NgayTiepNhan`, `TinhTrang`, `ChiPhi`.
- Nút **"Cập nhật trạng thái"**: Mở dialog chọn trạng thái mới (`Đang xử lý` / `Hoàn thành` / `Đã trả máy`).
- Nút **"Tiếp nhận mới"**: Mở `TiepNhanBaoHanhDialog` hiện có.

---

#### [MODIFY] [MainFrame.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/view/MainFrame.java)

Thêm tab "Quản Lý Bảo Hành" vào `JTabbedPane` và bổ sung refresh listener.

---

### STEP 4: Xây Dựng Session & Lookup UI (Data Safety)

Đây là bước **vá lỗ hổng bảo mật dữ liệu** lớn nhất, xử lý `RISK-POS-01`.

---

#### [NEW] [AppSession.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/controller/AppSession.java)

Tạo lớp **Singleton** lưu thông tin nhân viên đang đăng nhập:

```java
package controller;

import model.NhanVien;

public class AppSession {
    private static AppSession instance;
    private NhanVien nhanVienHienTai;

    private AppSession() {}

    public static AppSession getInstance() {
        if (instance == null) instance = new AppSession();
        return instance;
    }

    public NhanVien getNhanVienHienTai() { return nhanVienHienTai; }
    public void setNhanVienHienTai(NhanVien nv) { this.nhanVienHienTai = nv; }
    public String getMaNVHienTai() {
        return nhanVienHienTai != null ? nhanVienHienTai.getID() : null;
    }
}
```

---

#### [NEW] [DangNhapDialog.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/view/DangNhapDialog.java)

Tạo dialog **chọn nhân viên** khi khởi động app (thay thế hardcode "NV01"):

- `JComboBox<NhanVien>` nạp danh sách từ `NhanVienDAO.getAll()`.
- Nút "Vào làm việc" → Gọi `AppSession.getInstance().setNhanVienHienTai(selectedNV)`.
- Không cho phép đóng dialog nếu chưa chọn (prevent escape/close).

---

#### [MODIFY] [TaoHoaDonDialog.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/view/TaoHoaDonDialog.java)

**Thay đổi 1:** Thay `txtNV` (plain text, hardcode `"NV01"`) bằng label hiển thị NV từ Session:
```java
// TRƯỚC
txtNV = new JTextField("NV01");

// SAU  
NhanVien nvSession = AppSession.getInstance().getNhanVienHienTai();
JLabel lblNV = new JLabel(nvSession.getID() + " - " + nvSession.getHoTen());
```

**Thay đổi 2:** Thay `txtKH` (plain text `"Khách vãng lai"`) bằng `JComboBox<KhachHang>` nạp từ `KhachHangDAO.getAll()`, với item đầu tiên là "Khách vãng lai" (`KH_VLAN`).

---

#### [MODIFY] [TiepNhanBaoHanhDialog.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/view/TiepNhanBaoHanhDialog.java)

Tương tự, thay `txtMaNV = new JTextField("NV01")` bằng label lấy từ `AppSession`. Trường `txtMaKH` đã được auto-fill sau `traCuuAction()` nên không cần thay đổi.

---

#### [MODIFY] [MainFrame.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/view/MainFrame.java)

Trong `main()`, trước khi tạo `MainFrame`, hiển thị `DangNhapDialog`:

```java
SwingUtilities.invokeLater(() -> {
    DangNhapDialog login = new DangNhapDialog(null);
    login.setVisible(true);
    // DangNhapDialog sẽ block (modal) và set AppSession trước khi trả quyền điều khiển
    if (AppSession.getInstance().getNhanVienHienTai() != null) {
        new MainFrame("HỆ THỐNG QUẢN LÝ - " + AppSession.getInstance().getMaNVHienTai());
    }
});
```

---

### STEP 5: Clean-up & Integration Test

---

#### Legacy Code cần Xóa

| File | Đoạn cần xóa | Lý do |
|---|---|---|
| `NhanVien.java` | `nhap(Scanner sc)`, `xuat()`, `getDataString()` | Legacy CLI / File I/O |
| `QuanLyNhanVien.java` | `xuatDanhSachNhanVien()` | CLI output, không dùng trong GUI |
| `QuanLyKhachHang.java` | `xuatDanhSachKhachHang()` | CLI output |
| `QuanLyKho.java` | `List<Kho>`, `napDuLieuTuHT()`, `thucHienXuatKhoNoiBo()`, `ghiLichSuHoaDon()` | In-memory / stub rỗng |
| `Nguoi.java` | `nhap(Scanner)`, `xuat()` nếu có | CLI code |

---

#### Seed Data Cập Nhật

Cập nhật `seed_data_stage2.sql` thành `seed_data_stage3.sql`: Đảm bảo có sẵn `NCC01` và nhà cung cấp mẫu để test luồng Nhập hàng; thêm một số phiếu bảo hành mẫu với trạng thái `"Đang xử lý"`.

---

## Verification Plan

### Kiểm Thử Tự Động (Unit Test Logic)

| Test Case | Mục tiêu | Điều kiện Pass |
|---|---|---|
| **TC-S3-NV-01** | `NhanVienDAO.insert()` | NV mới xuất hiện trong DB sau khi gọi |
| **TC-S3-NV-02** | `NhanVienDAO.delete()` (Soft) | TrangThai = 'Ngừng hoạt động', hóa đơn cũ vẫn còn FK |
| **TC-S3-KHO-01** | `SeriSanPhamDAO.demTonKho()` | Số đếm khớp với COUNT thực tế trong DB |
| **TC-S3-KHO-02** | `PhieuNhapDAO.taoPhieuNhapVaNhapSeri()` | Serials mới xuất hiện trong `SERISANPHAM` với TinhTrang='Trong kho' |
| **TC-S3-BH-01** | `PhieuBaoHanhDAO.capNhatTrangThai("Đã trả máy")` | Serial → 'Đã bán', Phiếu → 'Đã trả máy' |
| **TC-S3-SESSION-01** | Session Injection vào POS | `hd.MaNV` = ID từ Session, không phải "NV01" cứng |

### Kiểm Thử Luồng Khép Kín (End-to-End)

```
[Nhập hàng] → Serial mới vào → TinhTrang = "Trong kho"
     ↓
[POS Bán hàng] → Scan serial → TinhTrang = "Đã bán"
     ↓
[Bảo hành] → Tiếp nhận → TinhTrang = "Đang bảo hành"
     ↓
[Cập nhật BH] → "Đã trả máy" → TinhTrang = "Đã bán"
     ↓
[Báo cáo] → Doanh thu phản ánh đúng giao dịch
```

---

## Thứ Tự Triển Khai Khuyến Nghị

```
STEP 1 (CRUD DAO + Controller + View NV/KH)
    ↓
STEP 2 (Kho: SeriDAO + PhieuNhapDAO + QuanLyKho + UI)
    ↓
STEP 3 (Bảo hành: capNhatTrangThai + BangDieuKhienBaoHanh)
    ↓
STEP 4 (AppSession + DangNhapDialog + Inject vào POS/BH)
    ↓
STEP 5 (Dọn dẹp + Integration Test + Seed Data mới)
```

Mỗi STEP độc lập và có thể commit riêng. STEP 1 là dependency của STEP 4.
