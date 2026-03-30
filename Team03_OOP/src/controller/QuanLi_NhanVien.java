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

import modal.*;


public class QuanLi_NhanVien extends Luu_Tru_Du_Lieu { // Giữ nguyên tên Class
	private List<NhanVien> danhSachNhanVien = new ArrayList<>(); // Đổi tên thuộc tính
	
	public List<NhanVien> layDanhSachNhanVien(){ // Đổi tên phương thức
		return this.danhSachNhanVien;
	}

    @Override
    public void docFileTXT() {
        this.danhSachNhanVien.clear();
        File tapTinNV = new File("data/DSNhanVien.txt");

        try (BufferedReader boDoc = new BufferedReader(new FileReader(tapTinNV))) {
            String dong;
            while ((dong = boDoc.readLine()) != null) {
                String duLieu[] = dong.split(",");
                // Cấu trúc mới: Chức vụ(0), ID(1), Tên(2), GT(3), NS(4), SDT(5), DC(6), Nghi(7), Ca(8)
                // Tổng cộng 9 cột
                if (duLieu.length < 9) continue;

                String chucVu = duLieu[0].trim();
                String id = duLieu[1].trim();
                String hoTen = duLieu[2].trim();
                String gioiTinh = duLieu[3].trim();
                String namSinh = duLieu[4].trim();
                String sdt = duLieu[5].trim();
                String diaChi = duLieu[6].trim();
                int soNgayNghi = Integer.parseInt(duLieu[7].trim());
                String caLamViec = duLieu[8].trim();

                NhanVien nv = null;
                if (chucVu.equalsIgnoreCase("Thu Ngan")) nv = new NhanVien_ThuNgan(chucVu, id, hoTen, gioiTinh, namSinh, sdt, diaChi, soNgayNghi, caLamViec);
                else if (chucVu.contains("Kho")) nv = new NhanVien_QuanLiKho(chucVu, id, hoTen, gioiTinh, namSinh, sdt, diaChi, soNgayNghi, caLamViec);
                else nv = new NhanVien_BanHang(chucVu, id, hoTen, gioiTinh, namSinh, sdt, diaChi, soNgayNghi, caLamViec);

                if (nv != null) this.danhSachNhanVien.add(nv);
            }
        } catch (IOException e) {
            System.out.println("Loi doc file.");
        }
    }

	@Override
	public void ghiFileTXT() { // Đổi tên phương thức
		File tapTinNV = new File ("data/DSNhanVien.txt"); // Đổi tên biến
		
		try (BufferedWriter boGhi = new BufferedWriter(new FileWriter(tapTinNV))) { // Đổi tên biến
			for (NhanVien nhanVien : danhSachNhanVien) { 
				StringBuilder dong = nhanVien.taoDong(); // Đổi tên phương thức
				boGhi.write(dong.toString()); 
				boGhi.newLine();
			}
			System.out.println("Da ghi danh sach nhan vien vao file");
		} catch (IOException e) {
			System.err.println("Loi khi ghi file: " + e.getMessage());
		}
	}

    // Hàm hỗ trợ phát sinh ID tự động
    public String phatSinhIDTuDong() {
        int maxID = 0;
        for (NhanVien nv : danhSachNhanVien) {
            try {
                // Giả sử ID có định dạng "NVxxx", ta lấy phần số
                int idSo = Integer.parseInt(nv.layID().replaceAll("[^0-9]", ""));
                if (idSo > maxID) maxID = idSo;
            } catch (Exception e) {
                // Bỏ qua nếu ID không đúng định dạng số
            }
        }
        return String.format("NV%03d", maxID + 1); // Trả về dạng NV001, NV002...
    }

    public ArrayList<NhanVien> timNhanVien(String keyword) {
        ArrayList<NhanVien> ketQua = new ArrayList<>();
        for (NhanVien nv : danhSachNhanVien) {
            if (nv.layID().equalsIgnoreCase(keyword) || nv.layHoTen().toLowerCase().contains(keyword.toLowerCase())) {
                ketQua.add(nv);
            }
        }
        return ketQua;
    }

	public NhanVien timNhanVienTheoID(String id) { // Giữ nguyên tên phương thức
		for (NhanVien nhanVien : danhSachNhanVien) { 
			if (nhanVien.layID().equalsIgnoreCase(id)) { 
				return nhanVien; 
			}
		}
		return null;
	}

	public void themNhanVien(Scanner sc) { // Giữ nguyên tên phương thức
		System.out.println("--- THEM NHAN VIEN ---");
		System.out.println("Chon chuc vu: (1: Ban Hang, 2: Thu Ngan, 3: Quan Li Kho)");
		int luaChon = -1;
		try {
			luaChon = Integer.parseInt(sc.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Nhap khong hop le. Thao tac bi huy.");
			return;
		}

		NhanVien nhanVienMoi = null; // Đổi tên biến

		switch (luaChon) {
		case 1:
			nhanVienMoi = new NhanVien_BanHang();
			System.out.println("Nhap thong tin Ban Hang:");
			break;
		case 2:
			nhanVienMoi = new NhanVien_ThuNgan();
			System.out.println("Nhap thong tin Thu Ngan:");
			break;
		case 3:
			nhanVienMoi = new NhanVien_QuanLiKho();
			System.out.println("Nhap thong tin Quan Li Kho:");
			break;
		default:
			System.out.println("Lua chon khong hop le. Thao tac bi huy.");
			return;
		}

        String idMoi = phatSinhIDTuDong();
        nhanVienMoi.datID(idMoi);

		nhanVienMoi.nhap(sc);

        danhSachNhanVien.add(nhanVienMoi);
        ghiFileTXT(); // Đổi tên phương thức
        System.out.println("Them nhan vien thanh cong!");
	}
	
	public void suaNhanVien(Scanner sc) { // Giữ nguyên tên phương thức
        System.out.println("Moi ban nhap id nhan vien can sua: ");
        String idCanSua = sc.nextLine().trim(); // Đổi tên biến
        NhanVien nvCanSua = timNhanVienTheoID(idCanSua);

        if (nvCanSua == null) {
            System.out.println("Khong tim thay nhan vien voi id: " + idCanSua);
            return;
        }

        System.out.println("Da tim thay nhan vien. Bat dau nhap thong tin moi...");
        NhanVien nhanVienMoi = null;

        try {
            if (nvCanSua instanceof NhanVien_BanHang) {
                nhanVienMoi = new NhanVien_BanHang();
                System.out.println("Moi nhap thong tin moi (Chuc vu: Ban Hang):");
            } else if (nvCanSua instanceof NhanVien_ThuNgan) {
                nhanVienMoi = new NhanVien_ThuNgan();
                System.out.println("Moi nhap thong tin moi (Chuc vu: Thu Ngan):");
            } else if (nvCanSua instanceof NhanVien_QuanLiKho) {
                nhanVienMoi = new NhanVien_QuanLiKho();
                System.out.println("Moi nhap thong tin moi (Chuc vu: Quan Li Kho):");
            } else {
                System.out.println("Loi: Khong xac dinh duoc loai nhan vien.");
                return;
            }

            String idnvCu = nvCanSua.layID();
            nhanVienMoi.datID(idnvCu);

            nhanVienMoi.nhap(sc);
			
			int chiSo = danhSachNhanVien.indexOf(nvCanSua); // Đổi tên biến
			if (chiSo != -1) { 
				danhSachNhanVien.set(chiSo, nhanVienMoi); 
				ghiFileTXT(); // Đổi tên phương thức
				System.out.println("Cap nhat thong tin nhan vien thanh cong!");
			} else {
				System.out.println("Loi: Khong tim thay vi tri nhan vien de cap nhat.");
			}
			
		} catch (NumberFormatException e) {
			System.out.println("Loi dinh dang so. Thao tac sua bi huy.");
		} catch (Exception e) {
			System.out.println("Loi khong xac dinh: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void xoaNhanVien(Scanner sc) { // Giữ nguyên tên phương thức
		System.out.println("Moi ban nhap id nhan vien can xoa: ");
		String id = sc.nextLine().trim();
		NhanVien nvCanXoa = timNhanVienTheoID(id);
		if (nvCanXoa == null) {
			System.out.println("Khong tim thay nhan vien voi id: " + id);
			return;
		}
		else {
			System.out.println("Da tim thay nhan vien.");
			System.out.println("Ban co chac chan muon xoa nhan vien nay khong? (Y/N)");
			String xacNhan = sc.nextLine(); 
			if (xacNhan.equalsIgnoreCase("Y")) { 
				danhSachNhanVien.remove(nvCanXoa); 
				ghiFileTXT(); // Đổi tên phương thức
				System.out.println("Xoa nhan vien thanh cong!");
			}
			else {
				System.out.println("Thao tac xoa bi huy.");
			}
		}
	}
	
	public void xuatDanhSachNhanVien() { // Đổi tên phương thức
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