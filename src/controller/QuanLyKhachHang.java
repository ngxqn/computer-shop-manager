package controller;

import dao.KhachHangDAO;
import model.KhachHang;
import java.util.ArrayList;
import java.util.List;

public class QuanLyKhachHang {
    private List<KhachHang> danhSachKhachHang = new ArrayList<>();
    private KhachHangDAO khachHangDAO = new KhachHangDAO();

    public QuanLyKhachHang() {
        refreshData();
    }

    public void refreshData() {
        this.danhSachKhachHang = khachHangDAO.getAll();
    }

    public List<KhachHang> getKhachHangList(){
        return this.danhSachKhachHang;
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


    public boolean themKhachHang(KhachHang kh) {
        boolean success = khachHangDAO.insert(kh);
        if (success) refreshData();
        return success;
    }

    public boolean suaKhachHang(KhachHang kh) {
        boolean success = khachHangDAO.update(kh);
        if (success) refreshData();
        return success;
    }

    public boolean xoaKhachHang(String maKH) {
        boolean success = khachHangDAO.delete(maKH);
        if (success) refreshData();
        return success;
    }
}
