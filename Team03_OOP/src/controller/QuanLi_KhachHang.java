package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Scanner;
import modal.KhachHang;

public class QuanLi_KhachHang extends Luu_Tru_Du_Lieu {
    private List<KhachHang> danhSachKhachHang = new ArrayList<>();
    private final Scanner sc = new Scanner(System.in);

    public List<KhachHang> layDanhSachKhachHang(){
        return this.danhSachKhachHang;
    }

    @Override
    public void docFileTXT() {
        List<KhachHang> danhSachTam = new ArrayList<>();
        File tapTinKH = new File ("data/DSKhachHang.txt");

        if (!tapTinKH.exists()) {
            System.err.println("❌ Lỗi: Không tìm thấy file tại: " + tapTinKH.getAbsolutePath());
            return;
        }

        try (BufferedReader boDoc = new BufferedReader(new FileReader(tapTinKH))) {
            String dong;
            int soDong = 0;
            while ((dong = boDoc.readLine()) != null) {
                soDong++;
                if (dong.trim().isEmpty()) continue;
                try {
                    String duLieu[] = dong.split(",");
                    if (duLieu.length < 6) continue;

                    KhachHang kh = new KhachHang(duLieu[0].trim(), duLieu[1].trim(), duLieu[2].trim(),
                            duLieu[3].trim(), duLieu[4].trim(), duLieu[5].trim());
                    danhSachTam.add(kh);
                } catch (Exception e) {
                    System.err.println("❌ Lỗi dòng " + soDong);
                }
            }
            danhSachKhachHang.clear();
            danhSachKhachHang.addAll(danhSachTam);
            System.out.println("✅ Đã đọc " + danhSachKhachHang.size() + " khách hàng.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ghiFileTXT() {
        File tapTinKH = new File ("data/DSKhachHang.txt");
        tapTinKH.getParentFile().mkdirs();

        try (BufferedWriter boGhi = new BufferedWriter(new FileWriter(tapTinKH))) {
            for (KhachHang kh : danhSachKhachHang) {
                // Sử dụng hàm taoDong() để lấy chuỗi CSV chuẩn
                boGhi.write(kh.taoDong().toString());
                boGhi.newLine();
            }
        } catch (IOException e) {
            System.err.println("❌ Lỗi ghi file: " + e.getMessage());
        }
    }

    // --- SỬA LẠI LOGIC TÌM KIẾM ---

    // Hàm này trả về danh sách kết quả (phục vụ tìm kiếm theo tên)
    public ArrayList<KhachHang> timKhachHang(String keyword) {
        ArrayList<KhachHang> ketQua = new ArrayList<>();
        for (KhachHang kh : danhSachKhachHang) {
            if (kh.layID().equalsIgnoreCase(keyword) || kh.layHoTen().toLowerCase().contains(keyword.toLowerCase())) {
                ketQua.add(kh);
            }
        }
        return ketQua;
    }

    // Hàm bổ trợ: Tìm chính xác 1 người theo ID (Dùng cho Thêm/Sửa/Xóa)
    private KhachHang timChinhXacTheoID(String id) {
        for (KhachHang kh : danhSachKhachHang) {
            if (kh.layID().equalsIgnoreCase(id)) return kh;
        }
        return null;
    }

    public String phatSinhIDTuDong() {
        if (danhSachKhachHang.isEmpty()) {
            return "KH001";
        }

        // Tìm ID lớn nhất hiện tại
        int maxID = 0;
        for (KhachHang kh : danhSachKhachHang) {
            // Cắt chuỗi "KH001" -> lấy phần số "001" -> chuyển thành số 1
            try {
                int idSo = Integer.parseInt(kh.layID().substring(2));
                if (idSo > maxID) {
                    maxID = idSo;
                }
            } catch (Exception e) {
                // Đề phòng trường hợp ID không đúng định dạng KHxxx
                continue;
            }
        }

        // Tăng ID lên 1 và định dạng lại thành KHxxx (ví dụ KH009 -> KH010)
        return String.format("KH%03d", maxID + 1);
    }

    public void themKhachHang(Scanner sc) {
        System.out.println("--- THEM KHACH HANG MOI (ID TU DONG) ---");

        // Tự động phát sinh ID
        String idMoi = phatSinhIDTuDong();
        System.out.println("ID cap cho khach hang moi: " + idMoi);

        KhachHang khachHangMoi = new KhachHang();
        khachHangMoi.datID(idMoi);

        // Bạn cần đảm bảo trong lớp Nguoi hoặc KhachHang
        // hàm nhap() không còn hỏi "Mời nhập id" nữa.
        khachHangMoi.nhap(sc);

        danhSachKhachHang.add(khachHangMoi);
        ghiFileTXT();
        System.out.println("✅ Them khach hang thanh cong!");
    }

    public void suaKhachHang(String id) {
        KhachHang khCanSua = timChinhXacTheoID(id);
        if (khCanSua == null) {
            System.out.println("❌ Không tìm thấy ID: " + id);
            return;
        }

        System.out.println("1. Họ Tên | 2. Giới Tính | 3. Năm Sinh | 4. SDT | 5. Địa Chỉ");
        System.out.print("Chọn: ");
        String chon = sc.nextLine(); // Dùng nextLine để tránh trôi lệnh

        switch (chon) {
            case "1" -> { System.out.print("Tên mới: "); khCanSua.datHoTen(sc.nextLine()); }
            case "2" -> { System.out.print("Giới tính mới: "); khCanSua.datGioiTinh(sc.nextLine()); }
            case "3" -> { System.out.print("Năm sinh mới: "); khCanSua.datNamSinh(sc.nextLine()); }
            case "4" -> { System.out.print("SDT mới: "); khCanSua.datSDT(sc.nextLine()); }
            case "5" -> { System.out.print("Địa chỉ mới: "); khCanSua.datDiaChi(sc.nextLine()); }
            default -> { System.out.println("Sai lựa chọn!"); return; }
        }
        ghiFileTXT();
        System.out.println("✅ Đã cập nhật!");
    }

    public void xoaKhachHang(String id) {
        KhachHang khCanXoa = timChinhXacTheoID(id);
        if (khCanXoa == null) {
            System.out.println("❌ Không tìm thấy ID: " + id);
            return;
        }
        System.out.print("Xác nhận xóa (Y/N): ");
        if (sc.nextLine().equalsIgnoreCase("Y")) {
            danhSachKhachHang.remove(khCanXoa);
            ghiFileTXT();
            System.out.println("✅ Đã xóa!");
        }
    }

    public void xuatDanhSachKhachHang() {
        if (danhSachKhachHang.isEmpty()) {
            System.out.println("Danh sách rỗng!");
            return;
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-10s | %-25s | %-10s | %-10s | %-15s | %-30s |%n", "ID", "Họ Tên", "G.Tính", "N.Sinh", "SDT", "Địa Chỉ");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------");
        for (KhachHang kh : danhSachKhachHang) {
            System.out.printf("| %-10s | %-25s | %-10s | %-10s | %-15s | %-30s |%n",
                    kh.layID(), kh.layHoTen(), kh.layGioiTinh(), kh.layNamSinh(), kh.laySDT(), kh.layDiaChi());
        }
    }
}