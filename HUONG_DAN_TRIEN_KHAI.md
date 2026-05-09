# Hướng dẫn Cài đặt & Triển khai Dự án Quản lý Khách sạn Azure Coast

Tài liệu này cung cấp các bước chi tiết để thiết lập và chạy dự án Azure Coast Luxury Resort trên một máy tính mới sau khi clone từ GitHub.

---

## 1. Yêu cầu Hệ thống (Prerequisites)

Trước khi bắt đầu, hãy đảm bảo máy tính của bạn đã cài đặt các công cụ sau:

*   **Java Development Kit (JDK):** Phiên bản **17** trở lên. (Khuyên dùng: OpenJDK 17).
*   **Git:** Để clone mã nguồn từ kho lưu trữ.
*   **Trình duyệt web:** Chrome, Edge, hoặc Firefox phiên bản mới nhất.
*   **IDE (Tùy chọn):** IntelliJ IDEA (khuyên dùng), VS Code hoặc Eclipse.

---

## 2. Clone mã nguồn (Cloning the Repository)

Mở Terminal (hoặc Command Prompt/PowerShell) và thực hiện các lệnh sau:

```bash
# 1. Di chuyển tới thư mục bạn muốn lưu dự án
cd path/to/your/projects

# 2. Clone dự án từ GitHub (Thay link bằng repo của bạn)
git clone https://github.com/DuongVinh2004/Hotel.git

# 3. Truy cập vào thư mục dự án
cd Hotel
```

---

## 3. Cấu hình Cơ sở dữ liệu (Database Configuration)

Dự án sử dụng **H2 Database (File-based)**, cơ sở dữ liệu sẽ tự động được khởi tạo dưới dạng file trong thư mục dự án.

*   **File cấu hình:** `src/main/resources/application.properties`
*   **Đường dẫn DB:** `./data/hotel-db` (Dữ liệu sẽ được lưu tại đây).
*   **Lưu ý:** Không cần cài đặt SQL Server hay MySQL.

---

## 4. Biên dịch và Chạy dự án (Build & Run)

Dự án tích hợp sẵn Maven bên trong (thư mục `apache-maven-3.9.6`). Bạn có thể dùng lệnh sau để chạy:

### Trên Windows (PowerShell):
```powershell
.\apache-maven-3.9.6\bin\mvn clean spring-boot:run
```

### Trên Windows (Command Prompt):
```cmd
apache-maven-3.9.6\bin\mvn clean spring-boot:run
```

---

## 5. Truy cập Hệ thống

Sau khi Terminal hiện dòng chữ `Started QuanLyKhachSanApplication in ... seconds`, bạn có thể truy cập qua:

*   **Trang chủ (Customer):** [http://localhost:8080](http://localhost:8080)
*   **Trang Quản trị (Admin):** [http://localhost:8080/dang-nhap](http://localhost:8080/dang-nhap)
*   **Quản lý Database (H2 Console):** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
    *   *JDBC URL:* `jdbc:h2:file:./data/hotel-db`
    *   *User:* `sa`
    *   *Password:* (để trống)

---

## 6. Tài khoản Đăng nhập Mặc định

Hệ thống đã được nạp sẵn dữ liệu mẫu để bạn trải nghiệm ngay:

### Tài khoản Quản trị (Admin/Staff):
*   **Username:** `quantri`
*   **Password:** `quantri123`

### Tài khoản Khách hàng (Customer):
*   **Username:** `khachhang`
*   **Password:** `khachhang123`

---

## 7. Các lỗi thường gặp (Troubleshooting)

1.  **Lỗi cổng 8080 bị chiếm dụng:**
    *   Mở PowerShell và chạy: `Stop-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess -Force`
2.  **Lỗi không nhận diện Java:**
    *   Kiểm tra lệnh `java -version`. Đảm bảo đã cài đặt JDK 17 và thiết lập biến môi trường `JAVA_HOME`.
3.  **Lỗi H2 Database locked:**
    *   Xảy ra khi có hai tiến trình cùng truy cập vào file Database. Hãy tắt dự án đang chạy trước khi khởi động lại.

---

**Chúc bạn có những trải nghiệm tuyệt vời cùng Azure Coast Luxury Resort!**
