package controller;

import java.util.Scanner;
import controller.QuanLi_NhanVien;
import controller.QuanLi_KhachHang;
import modal.NhanVien;
import modal.KhachHang;

import java.util.ArrayList;
import java.util.List;

public class Excute {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        QuanLi_NhanVien qlnv = new QuanLi_NhanVien();
        QuanLi_KhachHang qlkh = new QuanLi_KhachHang();

        // Tải dữ liệu từ file khi khởi động
        qlnv.docFileTXT();
        qlkh.docFileTXT();

        int luaChon;
        do {
            System.out.println("\n========== HE THONG QUAN LY Team03 ==========");
            System.out.println("1. Quan ly Nhan Vien");
            System.out.println("2. Quan ly Khach Hang");
            System.out.println("0. Thoat");
            System.out.print("Lua chon cua ban: ");

            try {
                luaChon = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                luaChon = -1;
            }

            switch (luaChon) {
                case 1: menuNhanVien(qlnv, sc); break;
                case 2: menuKhachHang(qlkh, sc); break;
                case 0: System.out.println("Tam biet!"); break;
                default: System.out.println("Lua chon khong hop le!");
            }
        } while (luaChon != 0);
    }

    private static void menuNhanVien(QuanLi_NhanVien qlnv, Scanner sc) {
        int chon;
        do {
            System.out.println("\n--- QUAN LY NHAN VIEN ---");
            System.out.println("1. Hien thi danh sach");
            System.out.println("2. Them nhan vien (ID Tu dong)");
            System.out.println("3. Sua nhan vien");
            System.out.println("4. Xoa nhan vien");
            System.out.println("5. Tim kiem nhan vien (ID/Ten)");
            System.out.println("0. Quay lai");
            System.out.print("Chon: ");
            chon = Integer.parseInt(sc.nextLine());

            switch (chon) {
                case 1: qlnv.xuatDanhSachNhanVien(); break;
                case 2: qlnv.themNhanVien(sc); break;
                case 3: qlnv.suaNhanVien(sc); break;
                case 4: qlnv.xoaNhanVien(sc); break;
                case 5:
                    System.out.print("Nhap ID hoac Ten nhan vien can tim: ");
                    String tuKhoa = sc.nextLine();
                    // Lưu ý: Bạn cần thêm hàm tìm kiếm List trong QuanLi_NhanVien tương tự KhachHang
                    List<NhanVien> KetQua = qlnv.timNhanVien(tuKhoa);
                    if(KetQua != null) {
                        System.out.println("Ket qua tim kiem:");
                        System.out.printf("| %-10s | %-25s | %-10s | %-10s | %-15s | %-30s | %-15s | %-12s | %-15s | %-15s | %-10s | %-15s | %-10s |%n", "ID", "Ho Ten", "Gioi Tinh", "Nam Sinh", "SDT", "Dia Chi", "Chuc Vu", "Ngay Nghi", "Ca Lam Viec", "Xep Loai","Tong Luong");
                        for (NhanVien nv : KetQua) {
                            nv.xuat();
                        }
                    } else {
                        System.out.println("Khong tim thay nhan vien phu hop.");
                    }
                    break;
            }
        } while (chon != 0);
    }

    private static void menuKhachHang(QuanLi_KhachHang qlkh, Scanner sc) {
        int chon;
        do {
            System.out.println("\n--- QUAN LY KHACH HANG ---");
            System.out.println("1. Hien thi danh sach");
            System.out.println("2. Them khach hang (ID Tu dong)");
            System.out.println("3. Sua khach hang");
            System.out.println("4. Xoa khach hang");
            System.out.println("5. Tim kiem khach hang (ID/Ten)");
            System.out.println("0. Quay lai");
            System.out.print("Chon: ");
            chon = Integer.parseInt(sc.nextLine());

            switch (chon) {
                case 1: qlkh.xuatDanhSachKhachHang(); break;
                case 2: qlkh.themKhachHang(sc); break;
                case 3:
                    System.out.print("Nhap ID khach hang can sua: ");
                    qlkh.suaKhachHang(sc.nextLine());
                    break;
                case 4:
                    System.out.print("Nhap ID khach hang can xoa: ");
                    qlkh.xoaKhachHang(sc.nextLine());
                    break;
                case 5:
                    System.out.print("Nhap ID hoac Ten khach hang can tim: ");
                    String keyword = sc.nextLine();
                    List<KhachHang> ketQua = qlkh.timKhachHang(keyword); // Sử dụng hàm timKhachHang đã có
                    if (ketQua.isEmpty()) {
                        System.out.println("Khong tim thay khach hang nao!");
                    } else {
                        System.out.println("Ket qua tim kiem:");
                        System.out.printf("| %-10s | %-25s | %-15s | %-15s |  %-15s| %-15s| %n", "ID", "Ho Ten", "Gioi Tinh", "Nam Sinh", "SDT", "Dia Chi");
                        for (KhachHang kh : ketQua) {
                            System.out.printf("| %-10s | %-25s | %-15s | %-15s | %-15s|  %-15s| %n", kh.layID(), kh.layHoTen(), kh.layGioiTinh(), kh.layNamSinh(), kh.laySDT(), kh.layDiaChi());
                        }
                    }
                    break;
            }
        } while (chon != 0);
    }
}