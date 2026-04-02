package controller;

import model.NhanVien;

/**
 * Singleton để quản lý phiên đăng nhập hiện tại của ứng dụng.
 */
public class AppSession {
    private static AppSession instance;
    private NhanVien nhanVienDangNhap;

    private AppSession() {}

    public static AppSession getInstance() {
        if (instance == null) {
            instance = new AppSession();
        }
        return instance;
    }

    public void login(NhanVien nv) {
        this.nhanVienDangNhap = nv;
    }

    public void logout() {
        this.nhanVienDangNhap = null;
    }

    public NhanVien getCurNV() {
        return nhanVienDangNhap;
    }

    public String getMaNV() {
        return nhanVienDangNhap != null ? nhanVienDangNhap.getID() : "GUEST";
    }

    public String getTenNV() {
        return nhanVienDangNhap != null ? nhanVienDangNhap.getHoTen() : "Khách";
    }

    public boolean isLoggedIn() {
        return nhanVienDangNhap != null;
    }
}
