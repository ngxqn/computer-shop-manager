package model;

public class SanPham {
    private String id; 
    private String ten; 
    private String loai; 
    private int soLuong; // Đổi từ SL
    private double giaGoc; // Đổi từ Gia (Giả định là giá gốc)
    
    // --- Getters ---
    public String  layID() { return id; } // Đổi tên phương thức
    public String layTen() { return ten; } // Đổi tên phương thức
    public String layLoai() { return loai; } // Đổi tên phương thức
    public int laySoLuong() { return soLuong; } // Đổi tên phương thức
    public double layGiaGoc() { return giaGoc; } // Đổi tên phương thức

    // --- Setters ---
    public void datID(String id) { this.id = id; } // Đổi tên phương thức
    public void datTen(String ten) { this.ten = ten; } // Đổi tên phương thức
    public void datLoai(String loai) { this.loai = loai; } // Đổi tên phương thức
    public void datSoLuong(int soLuong) { this.soLuong = soLuong; } // Đổi tên phương thức
    public void datGiaGoc(double giaGoc) { this.giaGoc = giaGoc; } // Đổi tên phương thức
    
    // constructor
    public SanPham(String id, String ten, String loai, int soLuong, double giaGoc ){ 
        this.id = id;
        this.ten = ten;
        this.loai = loai;
        this.soLuong = soLuong;
        this.giaGoc = giaGoc;
    }

    // Phương thức: Hệ số lợi nhuận (từ loiNhuan)
    public double heSoLoiNhuan(){ 
        if(this.loai.equalsIgnoreCase("Nuoc"))
            return 0.2; 
        else if (this.loai.equalsIgnoreCase("Banh")) {
            return 0.3; 
        }
        else if(this.loai.equalsIgnoreCase("Do gia dung")){
            return 0.1; 
        }
        else if(this.loai.equalsIgnoreCase("Điện tử"))
            return 0.1;
        else if(this.loai.equalsIgnoreCase("Thời trang"))
            return 0.3;
        else if(this.loai.equalsIgnoreCase("Khác"))
            return 0.1;
        else return 0;
    }
    
    // Phương thức: Tính Giá Bán (từ giaBan)
    public double tinhGiaBan(){
        return this.giaGoc + (this.giaGoc * heSoLoiNhuan()); 
    }

    // Phương thức: Tạo chuỗi để ghi file (từ toString)
    public String taoDong() { 
        return id + "," + ten + "," + loai + "," + soLuong + "," + giaGoc;
    }
}
