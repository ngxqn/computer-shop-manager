package controller;

import dao.LuuTruDuLieu;
import dao.KhachHangDAO;
import model.KhachHang;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class QuanLyKhachHang extends LuuTruDuLieu {
    private List<KhachHang> danhSachKhachHang = new ArrayList<>();
    private KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final Scanner sc = new Scanner(System.in);

    public List<KhachHang> getKhachHangList(){
        return this.danhSachKhachHang;
    }

    @Override
    public void docFileTXT() {
        // Chuyển sang JDBC: Nạp dữ liệu từ Database
        this.danhSachKhachHang = khachHangDAO.getAll();
        System.out.println("✅ Đã nạp danh sách khách hàng từ Database.");
    }

    @Override
    public void ghiFileTXT() {
        // Đã chuyển toàn bộ sang JDBC Database, không còn lưu File TXT
        System.out.println("ℹ️ Thông tin khách hàng hiện tại được đồng bộ với Database.");
    }

    // --- LOGIC TÌM KIẾM ---

    public ArrayList<KhachHang> timKhachHang(String keyword) {
        ArrayList<KhachHang> ketQua = new ArrayList<>();
        for (KhachHang kh : danhSachKhachHang) {
            if (kh.getID().equalsIgnoreCase(keyword) || kh.getHoTen().toLowerCase().contains(keyword.toLowerCase())) {
                ketQua.add(kh);
            }
        }
        return ketQua;
    }

    private KhachHang timChinhXacTheoID(String id) {
        for (KhachHang kh : danhSachKhachHang) {
            if (kh.getID().equalsIgnoreCase(id)) return kh;
        }
        return null;
    }

    public String phatSinhIDTuDong() {
        if (danhSachKhachHang.isEmpty()) return "KH001";
        int maxID = 0;
        for (KhachHang kh : danhSachKhachHang) {
            try {
                int idSo = Integer.parseInt(kh.getID().substring(2).trim());
                if (idSo > maxID) maxID = idSo;
            } catch (Exception e) {}
        }
        return String.format("KH%03d", maxID + 1);
    }

    public void themKhachHang(Scanner sc) {
        System.out.println("Tính năng thêm khách hàng mới chưa được chuyển hoàn toàn sang JDBC INSERT.");
    }

    public void suaKhachHang(String id) {
        // Triển khai logic GUI tương ứng
    }

    public void xoaKhachHang(String id) {
        // Triển khai logic GUI tương ứng
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
                    kh.getID(), kh.getHoTen(), kh.getGioiTinh(), kh.getNamSinh(), kh.getSdt(), kh.getDiaChi());
        }
    }
}
