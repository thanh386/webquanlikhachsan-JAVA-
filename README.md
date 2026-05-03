## Hướng dẫn chạy project

- Tạo database: hotel_db
- Username: thanh
- Password: 123456
- Port: 1433

Chạy:
mvn spring-boot:run    
    # Hotel Management System

A Spring Boot application for managing hotel operations, including room management, booking, customer management, and staff roles.

## Features

- **Authentication**: Login/Register with roles (Staff, Customer)
- **Room Management**: Add, edit, delete rooms (Staff only)
- **Booking Management**: Book rooms, view bookings, cancel bookings
- **Customer Dashboard**: View available rooms, make bookings
- **Admin Dashboard**: Manage rooms and bookings

## Prerequisites

- Java 17 (download from https://adoptium.net/)
- Maven (download from https://maven.apache.org/download.cgi)
- MySQL (download from https://dev.mysql.com/downloads/mysql/)

## Setup

1. Install Java 17, Maven, and MySQL.
2. Create a MySQL database named `hotel_db`.
3. Update `src/main/resources/application.properties` with your MySQL credentials (replace `root` and `password`).
4. Open terminal in project root and run `mvn clean install`.
5. Run `mvn spring-boot:run` to start the application.
6. Access at http://localhost:8080

## Usage

- Go to /register to create accounts (choose role).
- Login at /login.
- Staff: Access /admin for management.
- Customers: Access /customer for booking.

## Troubleshooting

- Ensure ports are free (MySQL on 3306, app on 8080).
- Check MySQL service is running.
- If compile errors, run `mvn dependency:resolve`.