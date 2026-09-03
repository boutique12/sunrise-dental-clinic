# Sunrise Dental Clinic System

A Java web application for **Sunrise Dental Clinic**, developed for the **CIS6003 Advanced Programming** assignment (ICBT / Cardiff Metropolitan University). The system computerizes patient registration, appointment scheduling, treatment records, billing, payments, reporting, and staff authentication.

## Features

- **Authentication & security** — PBKDF2 password hashing, session-based login, role-based access (Receptionist / Dentist)
- **Patient management** — Register, search, update, and deactivate patients
- **Appointment scheduling** — Book appointments, prevent dentist double-booking, cancel/complete visits
- **Treatment records** — Diagnoses, treatment notes, prescriptions, and standard charges (Dentist role)
- **Billing & payments** — Generate bills, apply discounts, record cash/card/bank-transfer payments, partial payments
- **Reports** — Appointment, revenue, patient, treatment, payment, and outstanding-bills reports
- **REST JSON API** — `GET /api/appointments` for appointment data
- **Help section** — Staff guidance for all modules

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java 21 |
| Web | Jakarta Servlets, JSP, JSTL |
| Database | MySQL 8 |
| Build | Maven (WAR) |
| Server | Apache Tomcat 10.1 |
| Testing | JUnit 5, Mockito, H2 |

**Note:** This project does **not** use Spring Boot. It follows a three-tier MVC architecture with JDBC.

## Architecture

```
Presentation Layer  →  JSP pages, Servlets, Filters
Business Layer      →  Service interfaces & implementations
Data Access Layer   →  DAO interfaces & JDBC implementations
```

Design patterns: MVC, DAO, Service Layer, Dependency Injection, Front Controller, Filter (auth/authorization).

## Prerequisites

- Java 21 JDK
- Apache Maven 3.8+
- MySQL 8
- Apache Tomcat 10.1 (for deployment)

## Database Setup

1. Open MySQL Workbench or the MySQL CLI and run:

```bash
mysql -u root -p < database/sunrise_dental_clinic.sql
mysql -u root -p < database/advanced_database_objects.sql
```

2. Copy the database config:

```bash
cp src/main/resources/db.properties.example src/main/resources/db.properties
```

3. Edit `db.properties` with your MySQL credentials.

### Advanced Database Objects

- **Function:** `fn_calculate_bill_balance`
- **Stored procedure:** `sp_daily_appointment_report`
- **Trigger:** `trg_payment_update_bill_status`
- **View:** `vw_appointment_billing_summary`

## Demo Accounts

| Role | Username | Password |
|------|----------|----------|
| Receptionist | `reception01` | `Reception@123` |
| Dentist | `dentist.gayathri` | `Dentist@123` |
| Dentist | `dentist.vishban` | `Dentist@123` |
| Dentist | `dentist.nirushini` | `Dentist@123` |
| Dentist | `dentist.vedhik` | `Dentist@123` |

## Running Locally

### Build and test

```bash
mvn clean test
mvn clean package
```

### Deploy to Tomcat

1. Copy `target/sunrise-dental-clinic.war` to Tomcat's `webapps/` folder.
2. Start Tomcat.
3. Open: `http://localhost:8080/sunrise-dental-clinic/login`

### Embedded Tomcat (development)

```bash
mvn cargo:run
```

The app runs at `http://localhost:43123/sunrise-dental-clinic/login`.

## Application URLs

| URL | Description |
|-----|-------------|
| `/login` | Staff login |
| `/dashboard` | Role-based dashboard |
| `/patients` | Patient management |
| `/appointments` | Appointment scheduling |
| `/treatments` | Treatment records (Dentist) |
| `/bills` | Billing (Receptionist) |
| `/payments` | Payment recording |
| `/reports` | Financial & operational reports |
| `/help` | User guide |
| `/api/appointments` | JSON appointment API |

## Role Permissions

| Function | Receptionist | Dentist |
|----------|:------------:|:-------:|
| Dashboard | ✓ | ✓ |
| View patients | ✓ | ✓ |
| Register/update patients | ✓ | |
| Appointments | ✓ | View only |
| Treatments | | ✓ |
| Billing & payments | ✓ | |
| Reports | ✓ | |
| Help | ✓ | ✓ |

## Project Structure

```
sunrise-dental-clinic/
├── database/                  # SQL scripts
├── src/main/java/com/sunrise/dental/
│   ├── config/                # Application bootstrap
│   ├── controller/ → servlet/   # Servlets (Front Controller)
│   ├── dao/ & dao/impl/         # Data Access Layer
│   ├── service/ & service/impl/ # Business Layer
│   ├── model/                   # Entity classes
│   ├── filter/                  # Auth filters
│   └── util/                    # Utilities
├── src/main/webapp/
│   ├── assets/css/              # Purple theme stylesheet
│   ├── assets/images/           # Dental clinic images
│   └── WEB-INF/views/           # JSP views
└── src/test/java/               # Unit tests
```

## Security

- Passwords stored as PBKDF2-HMAC-SHA256 hashes
- Prepared statements for all SQL
- JSTL `<c:out>` for XSS prevention
- Session invalidation on logout
- No-cache headers on protected pages
- Database credentials excluded from Git

## License

Educational project for CIS6003 Advanced Programming.
