package controller;

import dao.NhanVienDAO;
import model.NhanVien;
import java.util.ArrayList;
import java.util.List;

public class QuanLyNhanVien { 
    private List<NhanVien> danhSachNhanVien = new ArrayList<>(); 
    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    
    public QuanLyNhanVien() {
        refreshData();
    }

    public void refreshData() {
        this.danhSachNhanVien = nhanVienDAO.getAll();
    }
    
    public List<NhanVien> getNhanVienList(){ 
        return this.danhSachNhanVien;
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

    public boolean themNhanVien(NhanVien nv) {
        boolean success = nhanVienDAO.insert(nv);
        if (success) refreshData();
        return success;
    }

    public boolean suaNhanVien(NhanVien nv) {
        boolean success = nhanVienDAO.update(nv);
        if (success) refreshData();
        return success;
    }

    public boolean xoaNhanVien(String maNV) {
        boolean success = nhanVienDAO.delete(maNV);
        if (success) refreshData();
        return success;
    }
}
