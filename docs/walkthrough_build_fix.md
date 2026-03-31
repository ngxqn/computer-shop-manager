# Walkthrough: Build and Database Fixes

This document summarizes the changes made to resolve latent compilation errors and database connection issues that surfaced during the final integration of Phase 5.

(See [analysis_build_fix.md](analysis_build_fix.md) for the root cause analysis.)

## Issues Addressed

1.  **Database Connection Failure**: The Java configuration used a different database name casing (`cuahangmaytinh`) than the one created by the SQL script (`CuaHangMayTinh`), leading to an "Unknown database" error.
2.  **Latent Compilation Errors**: Several legacy classes from the original Supermarket project (Warehouse and Inventory modules) were still calling methods on the `SanPham` model that were removed or renamed in Phase 3/4.
3.  **SQL Syntax Error**: A typo in the recording functionality of `HoaDonDAO.java` (using a space in a column alias) caused a runtime crash when loading the "Báo Cáo Doanh Thu" tab.

## Changes Made

### 1. Database Configuration
#### [MODIFY] [DatabaseConnection.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/dao/DatabaseConnection.java)
Updated the connection URL to use the exact database name defined in `db-design.sql`:
```diff
-private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/cuahangmaytinh";
+private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/CuaHangMayTinh";
```

### 2. Resolution of Compilation Errors (Latent Legacy Code)
We updated all legacy references to use the new `SanPham` API (`getMaSP()`, `getTenSP()`, `getLoaiSP()`, `getGiaBan()`). 

> [!NOTE]
> **Quantity Logic**: Since the new computer store model manages stock via **Serial Numbers** instead of a simple integer `soLuong` field, calls to `laySoLuong()` or `datSoLuong()` were stubbed out (returning 0 or doing nothing) to ensure the application compiles and runs.

Files updated:
- **[QuanLyKho.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/controller/QuanLyKho.java)**
- **[BaoCaoTonKho.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/controller/BaoCaoTonKho.java)**
- **[Kho.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/model/Kho.java)**
- **[PhieuNhapKho.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/model/PhieuNhapKho.java)**
- **[ChiTietGiaoDich.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/view/ChiTietGiaoDich.java)**

### 3. SQL Logic Fix
#### [MODIFY] [HoaDonDAO.java](file:///c:/Users/Admin/OneDrive%20-%20MSFT/Documents/Developer/isad/C5-13_05/src/dao/HoaDonDAO.java)
Fixed the SQL alias syntax in the monthly revenue query:
```diff
-String sql = "SELECT DATE_FORMAT(NgayLap, '%m/%Y') AS Thang, SUM(TongTien) AS Tong DoanhThu "
+String sql = "SELECT DATE_FORMAT(NgayLap, '%m/%Y') AS Thang, SUM(TongTien) AS TongDoanhThu "
```

## Verification Results

- **Building**: The project now compiles without any "Unresolved compilation problems" in the IDE.
- **Running**: `MainFrame.java` launches successfully.
- **Connection**: DAOs correctly fetch data from the `CuaHangMayTinh` database.
- **Reporting**: The "Báo Cáo Doanh Thu" tab now loads data from the database without SQL syntax errors.

---

## Commit Message

```text
fix(build): resolve latent compilation errors and db connection mismatch

- update DatabaseConnection URL to match CuaHangMayTinh case (CamelCase)
- fix SQL syntax error in HoaDonDAO monthly revenue query
- refactor legacy warehouse/inventory classes to use new SanPham API
- stub out obsolete quantity-based logic in Kho, PhieuNhapKho, and ChiTietGiaoDich

These changes fix errors that surfaced when trying to run the full application 
after refactoring the core models to a serial-based system.
```
