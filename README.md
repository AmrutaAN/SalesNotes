# SalesNotes

A modern, full-stack sales notes and deal tracking application built with **Spring Boot** (SQL database), **Angular** (web application), and **Ionic** (mobile application), using a **Common Component Architecture** to share TypeScript logic between Angular and Ionic components.

---

## 📁 Repository Structure

```
SalesNotes/
├── common/                               # Core shared TypeScript components, models & services
│   ├── components/
│   │   ├── common-note-list/             # CommonNoteListComponent
│   │   ├── common-note-detail/           # CommonNoteDetailComponent
│   │   ├── common-note-form/             # CommonNoteFormComponent
│   │   ├── common-dashboard/             # CommonDashboardComponent
│   │   └── common-customer-list/         # CommonCustomerListComponent
│   ├── models/                           # SalesNote, Customer, User, UserRole, etc.
│   └── services/                         # SalesNoteService, CustomerService, UserService
│
├── salesnotes/                           # 1) Spring Boot Backend API (Java + SQL)
│   ├── src/main/java/com/salesnotes/
│   │   ├── config/                       # CorsConfig, DataInitializer (sample seed data)
│   │   ├── controller/                   # REST: NoteController, CustomerController, DashboardController, UserController
│   │   ├── dto/                          # DTOs: SalesNoteRequest, SalesNoteResponse, UserRequest, UserResponse, DashboardSummary
│   │   ├── entity/                       # JPA Entities: User, UserRole, SalesNote, Customer, NoteCategory
│   │   ├── repository/                   # Spring Data JPA Repositories: UserRepository, SalesNoteRepository, etc.
│   │   └── service/                      # Business Services: UserService, SalesNoteService, CustomerService, DashboardService
│   ├── src/main/resources/
│   │   ├── application.yml               # Base configuration (default active profile: devlocal)
│   │   ├── application-devlocal.yml      # devlocal profile (H2 in-memory SQL DB, console, SQL logging)
│   │   └── application-prod.yml          # prod profile (Production SQL DB: PostgreSQL/MySQL, HikariCP)
│   ├── pom.xml
│   └── mvnw / mvnw.cmd
│
├── salesnotes-angular/                   # 2) Angular Web Application
│   ├── src/
│   │   ├── app/
│   │   │   ├── common/                   # Common[ComponentName] base components
│   │   │   └── components/               # Web components extending Common* base components
│   │   └── environments/                 # environment.devlocal.ts & environment.prod.ts
│   ├── package.json
│   └── angular.json
│
└── salesnotes-ionic/                     # 3) Ionic Mobile Application
    ├── src/
    │   ├── app/
    │   │   ├── common/                   # Common[ComponentName] base components
    │   │   ├── pages/                    # Mobile pages extending Common* base components
    │   │   └── tabs/                     # Ionic Tab Bar & navigation
    │   └── environments/                 # environment.devlocal.ts & environment.prod.ts
    ├── package.json
    └── ionic.config.json
```

---

## ⚙️ Environments & Profiles: `devlocal` and `prod`

The application includes two distinct profiles:

### 1. `devlocal` (Default Profile)
Designed for local development with zero external dependencies:
- **Database**: In-memory H2 SQL Database (`jdbc:h2:mem:salesnotesdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`).
- **H2 Console**: Enabled at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:salesnotesdb`, User: `sa`).
- **SQL Logging**: Enabled (`show-sql: true`, formatted SQL output).
- **Data Seed**: Automatically seeds default users and demo customer records on startup.

Run with `devlocal`:
```bash
cd salesnotes
.\mvnw.cmd spring-boot:run
# Or explicitly:
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=devlocal
```

### 2. `prod` (Production Profile)
Designed for production environments using external SQL databases (PostgreSQL or MySQL):
- **Database**: Configurable via standard environment variables:
  - `SPRING_DATASOURCE_URL` (default: `jdbc:postgresql://localhost:5432/salesnotes`)
  - `SPRING_DATASOURCE_DRIVER` (default: `org.postgresql.Driver`)
  - `SPRING_DATASOURCE_USERNAME` (default: `postgres`)
  - `SPRING_DATASOURCE_PASSWORD` (default: `password`)
- **Connection Pool**: Optimized HikariCP pooling (pool size: 20, connection timeout: 20s).
- **H2 Console**: Disabled.
- **SQL Logging**: Disabled (`show-sql: false`).

Run with `prod`:
```bash
cd salesnotes
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## 👥 Users Table & Roles

The `users` table is defined with the following schema:

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique user identifier |
| `username` | VARCHAR(255) | UNIQUE, NOT NULL | Login handle |
| `fullname` | VARCHAR(255) | NOT NULL | User's full display name |
| `email` | VARCHAR(255) | UNIQUE, NOT NULL | Validated email address |
| `password` | VARCHAR(255) | NOT NULL | User password |
| `role` | VARCHAR(50) | NOT NULL | Role from allowed set |
| `active` | BOOLEAN | NOT NULL, DEFAULT TRUE | User active status |
| `createdat` | TIMESTAMP | NOT NULL, UP-TO-DATE | Record creation timestamp |
| `updatedat` | TIMESTAMP | NULLABLE | Record update timestamp |

### Allowed User Roles:
- **`SYSADMIN`**: System-level administrator with global access across all companies and settings.
- **`companyadmin`**: Company-level administrator managing company employees, configuration, and notes.
- **`companyemployee`**: Company sales representative or employee creating and managing sales notes.

### Pre-seeded Default Users (in `devlocal`):
| Username | Full Name | Email | Password | Role |
|---|---|---|---|---|
| `sysadmin` | System Administrator | `admin@salesnotes.io` | `Admin@123` | `SYSADMIN` |
| `companyadmin` | Alice Johnson (Company Admin) | `alice@acmecloud.com` | `Admin@123` | `companyadmin` |
| `companyemployee` | Bob Miller (Sales Rep) | `bob@acmecloud.com` | `User@123` | `companyemployee` |

---

## 📡 Users REST API Reference

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/users` | List all users (supports `search`, `role`, `active`) |
| `GET` | `/api/users/{id}` | Get user by ID |
| `POST` | `/api/users` | Create a new user (with role validation) |
| `PUT` | `/api/users/{id}` | Update existing user details |
| `PATCH` | `/api/users/{id}/status` | Activate / deactivate a user (`{"active": true/false}`) |
| `DELETE` | `/api/users/{id}` | Delete a user |

---

## 💡 Common Component Architecture (`common[componentname]`)

The code commonly used from Angular and Ionic from `component.ts` is defined in separate `common[componentname]` components:

```
                  +-----------------------------------+
                  |      CommonNoteListComponent      |
                  |     (common-note-list.component)  |
                  |                                   |
                  | - Injected SalesNoteService       |
                  | - notes: SalesNote[]              |
                  | - filteredNotes: SalesNote[]      |
                  | - loading: boolean                |
                  | - searchTerm, status, priority    |
                  | - loadNotes(), applyFilter()      |
                  | - deleteNote(), updateStatus()    |
                  +-----------------+-----------------+
                                    |
            +-----------------------+-----------------------+
            |                                               |
            v                                               v
+-------------------------------+       +------------------------------------+
|       NoteListComponent       |       |       NoteListIonicComponent       |
|    (salesnotes-angular)       |       |        (salesnotes-ionic)          |
|                               |       |                                    |
| - Extends CommonNoteList      |       | - Extends CommonNoteList           |
| - Angular Web HTML Table &    |       | - Ionic <ion-content>, <ion-card>, |
|   Responsive SaaS Grid UI     |       |   <ion-searchbar>, <ion-refresher> |
| - Desktop search & modal UI   |       |   <ion-item-sliding>, <ion-fab>    |
+-------------------------------+       +------------------------------------+
```

### Components Implemented:
1. **`CommonNoteListComponent`**: Notes collection management, keyword search, priority/status filtering, note deletion, status transitions.
2. **`CommonNoteDetailComponent`**: Single note fetching by route `:id`, quick status toggle, deletion with redirection.
3. **`CommonNoteFormComponent`**: Reactive form group setup, customer dropdown population, create vs edit detection, validation, submit handling.
4. **`CommonDashboardComponent`**: Pipeline value calculation, metrics aggregation, recent notes feed.
5. **`CommonCustomerListComponent`**: Account and client directory, search filtering, account removal.

---

## 🚀 Running the Frontend Applications

### Angular Web (`salesnotes-angular`)
```bash
cd salesnotes-angular
npm start
```
- Web Application: **`http://localhost:4200`**

### Ionic Mobile (`salesnotes-ionic`)
```bash
cd salesnotes-ionic
npm start
```
- Mobile Application: **`http://localhost:8100`**

---

## 📄 Git Repository
Remote repository origin:
**[https://github.com/AmrutaAN/SalesNotes.git](https://github.com/AmrutaAN/SalesNotes.git)**
