package util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Lớp tiện ích dùng để chuẩn hóa việc hiển thị dữ liệu (Tiền tệ, Ngày tháng, v.v.)
 */
public class DinhDang {
    private static final NumberFormat dinhDangTien = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

    /**
     * Định dạng giá trị sang chuỗi tiền tệ VNĐ (ví dụ: 1.000.000 ₫)
     * @param value Giá trị cần định dạng
     * @return Chuỗi đã định dạng
     */
    public static String tien(double value) {
        return dinhDangTien.format(value);
    }
    
    /**
     * Định dạng giá trị sang chuỗi số thông thường có phân tách hàng nghìn
     * @param value Giá trị cần định dạng
     * @return Chuỗi đã định dạng
     */
    public static String so(double value) {
        return NumberFormat.getInstance(Locale.of("vi", "VN")).format(value);
    }
}
