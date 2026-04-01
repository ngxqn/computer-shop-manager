package controller;

import dao.LuuTruDuLieu;
import dao.NhanVienDAO;
import model.NhanVien;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class QuanLyNhanVien extends LuuTruDuLieu { 
    private List<NhanVien> danhSachNhanVien = new ArrayList<>(); 
    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    
    public List<NhanVien> getNhanVienList(){ 
        return this.danhSachNhanVien;
    }

    @Override
    public void docFileTXT() {
        // Chuyển sang JDBC: Nạp dữ liệu từ Database
        this.danhSachNhanVien = nhanVienDAO.getAll();
        System.out.println("✅ Đã nạp danh sách nhân viên từ Database.");
    }

    @Override
    public void ghiFileTXT() { 
        // Đã chuyển sang JDBC Database, không còn lưu File TXT
        System.out.println("Ghi nhận: Dữ liệu nhân viên hiện tại được quản lý bởi Database.");
    }

    // Hàm hỗ trợ phát sinh ID tự động
    public String phatSinhIDTuDong() {
        int maxID = 0;
        for (NhanVien nv : danhSachNhanVien) {
            try {
                int idSo = Integer.parseInt(nv.getID().replaceAll("[^0-9]", ""));
                if (idSo > maxID) maxID = idSo;
            } catch (Exception e) {
                // Bỏ qua nếu ID không đúng định dạng số
            }
        }
        return String.format("NV%03d", maxID + 1); 
    }

    public ArrayList<NhanVien> timNhanVien(String keyword) {
        ArrayList<NhanVien> ketQua = new ArrayList<>();
        for (NhanVien nv : danhSachNhanVien) {
            if (nv.getID().equalsIgnoreCase(keyword) || nv.getHoTen().toLowerCase().contains(keyword.toLowerCase())) {
                ketQua.add(nv);
            }
        }
        return ketQua;
    }

    public NhanVien timNhanVienTheoID(String id) { 
        for (NhanVien nhanVien : danhSachNhanVien) { 
            if (nhanVien.getID().equalsIgnoreCase(id)) { 
                return nhanVien; 
            }
        }
        return null;
    }

    public void themNhanVien(Scanner sc) { 
        // Triển khai logic GUI tương ứng sẽ cần nạp JDBC thêm mới
        System.out.println("Tính năng thêm nhân viên hiện chưa được chuyển sang INSERT JDBC hoàn toàn.");
    }

    public void suaNhanVien(Scanner sc) { 
        // Triển khai logic GUI tương ứng
    }

    public void xoaNhanVien(Scanner sc) { 
        // Triển khai logic GUI tương ứng
    }

    public void xuatDanhSachNhanVien() { 
        if (danhSachNhanVien.isEmpty()) { 
            System.out.println("Danh sach nhan vien hien tai dang rong.");
            return;
        }
        
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-10s | %-25s | %-10s | %-10s | %-15s | %-30s | %-15s | %-12s | %-15s | %-15s | %-10s | %n", "ID", "Ho Ten", "Gioi Tinh", "Nam Sinh", "SDT", "Dia Chi", "Chuc Vu", "Ngay Nghi", "Ca Lam Viec", "Xep Loai", "Tong Luong");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        for (NhanVien nhanVien : danhSachNhanVien) { 
            nhanVien.xuat(); 
        }
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }
}
