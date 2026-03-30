package controller;
import dao.LuuTruDuLieu;

import model.SanPham;
import model.HoaDon;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class QuanLySanPham extends LuuTruDuLieu {
    private List<SanPham> danhSachSanPham = new ArrayList<>();
    private final String DUONG_DAN_FILE = "data/DSSanPham.txt";
    private final String DUONG_DAN_HOA_DON = "data/DSHoaDon.txt";

    public QuanLySanPham() {
        docFileTXT();
    }

    // --- CÁC HÀM NGHIỆP VỤ BÁN HÀNG ---

    /**
     * Trừ trực tiếp số lượng sản phẩm bày bán tại cửa hàng khi có khách mua
     */
    public boolean thucHienBanHangTruTrucTiep(String idSP, int slBan) {
        SanPham sp = timSanPhamTheoID(idSP);
        if (sp != null && sp.laySoLuong() >= slBan) {
            sp.datSoLuong(sp.laySoLuong() - slBan);
            ghiFileTXT(); // Lưu lại thay đổi vào file DSSanPham.txt ngay lập tức
            return true;
        }
        return false;
    }

    /**
     * Ghi lịch sử hóa đơn bán hàng vào file DSHoaDon.txt
     */
    public void ghiLichSuHoaDon(HoaDon hd) {
        // Sử dụng FileWriter với tham số true để ghi tiếp vào cuối file (append)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DUONG_DAN_HOA_DON, true))) {
            bw.write(hd.taoDongLuuTru());
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi file lịch sử hóa đơn: " + e.getMessage());
        }
    }

    // --- CÁC HÀM QUẢN LÝ (CRUD) GIỮ NGUYÊN ---

    public List<SanPham> layDanhSachSanPham() {
        return danhSachSanPham;
    }

    public SanPham timSanPhamTheoID(String id) {
        return danhSachSanPham.stream()
                .filter(s -> s.layID().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public boolean themSanPham(SanPham sp) {
        if (timSanPhamTheoID(sp.layID()) == null) {
            danhSachSanPham.add(sp);
            ghiFileTXT();
            return true;
        }
        return false;
    }

    public boolean xoaSanPham(String id) {
        SanPham sp = timSanPhamTheoID(id);
        if (sp != null) {
            danhSachSanPham.remove(sp);
            ghiFileTXT();
            return true;
        }
        return false;
    }

    public boolean suaSanPham(String id, String tenMoi, String loaiMoi, int slMoi, double giaMoi) {
        SanPham sp = timSanPhamTheoID(id);
        if (sp != null) {
            sp.datTen(tenMoi);
            sp.datLoai(loaiMoi);
            sp.datSoLuong(slMoi);
            sp.datGiaGoc(giaMoi);
            ghiFileTXT();
            return true;
        }
        return false;
    }

    // --- CÁC HÀM ĐỌC/GHI FILE HỆ THỐNG ---

    @Override
    public void docFileTXT() {
        danhSachSanPham.clear();
        File f = new File(DUONG_DAN_FILE);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] d = line.split(",");
                // Cấu trúc: ID, Ten, Loai, SoLuong, GiaGoc
                if (d.length == 5) {
                    danhSachSanPham.add(new SanPham(
                            d[0], d[1], d[2],
                            Integer.parseInt(d[3]),
                            Double.parseDouble(d[4])
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi đọc file sản phẩm: " + e.getMessage());
        }
    }

    @Override
    public void ghiFileTXT() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DUONG_DAN_FILE))) {
            for (SanPham sp : danhSachSanPham) {
                bw.write(sp.layID() + "," +
                        sp.layTen() + "," +
                        sp.layLoai() + "," +
                        sp.laySoLuong() + "," +
                        sp.layGiaGoc());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Lỗi ghi file sản phẩm: " + e.getMessage());
        }
    }
}
