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
│   │   ├── application.yml               # Base configuration (profiles.active: @active.profile@)
│   │   ├── application-devlocal.yml      # devlocal profile (Local system database, console, SQL logging)
│   │   └── application-prod.yml          # prod profile (Hosted database from server/cloud)
│   ├── pom.xml                           # Maven profiles (devlocal, prod)
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
    │   │   ├── common/                   # Linked to salesnotes-angular/src/app/common
    │   │   ├── pages/                    # Mobile pages extending Common* base components
    │   │   └── tabs/                     # Ionic Tab Bar & navigation
    │   └── environments/                 # environment.devlocal.ts & environment.prod.ts
    ├── package.json
    └── ionic.config.json
```

---

## ⚙️ Environments & Maven Profiles: `devlocal` and `prod`

The application includes two Maven profiles configured in `pom.xml`:

```xml
<profiles>
    <profile>
        <id>devlocal</id>
        <properties>
            <active.profile>devlocal</active.profile>
        </properties>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
    </profile>
    <profile>
        <id>prod</id>
        <properties>
            <active.profile>prod</active.profile>
        </properties>
    </profile>
</profiles>
```

When building or running, Maven resource filtering injects `<active.profile>` into `application.yml`:
```yaml
spring:
  profiles:
    active: @active.profile@
```

### 1. `devlocal` (Default Profile — Local System Database)
Configured in `application-devlocal.yml`:
- **Database**: Local System Database (`jdbc:h2:file:./data/salesnotes_local` or local PostgreSQL/MySQL via `LOCAL_DB_URL`).
- **H2 Console**: Enabled at `http://localhost:8080/h2-console`.
- **SQL Logging**: Enabled (`show-sql: true` with formatted output).
- **Seed Data**: Automatically seeds default users and demo records.

Run with `devlocal` (default):
```bash
cd salesnotes
.\mvnw.cmd spring-boot:run
# Or explicitly with Maven profile:
.\mvnw.cmd spring-boot:run -Pdevlocal
```

### 2. `prod` (Production Profile — Hosted Database from Server)
Configured in `application-prod.yml`:
- **Database**: Hosted Database Server (AWS RDS, Neon, Supabase, Google Cloud SQL, etc.):
  - `HOSTED_DB_URL` / `SPRING_DATASOURCE_URL` (default: `jdbc:postgresql://hosted-db-server.yourcloud.com:5432/salesnotes_prod?sslmode=require`)
  - `HOSTED_DB_USERNAME` / `SPRING_DATASOURCE_USERNAME`
  - `HOSTED_DB_PASSWORD` / `SPRING_DATASOURCE_PASSWORD`
- **Connection Pool**: Optimized HikariCP pooling (pool size: 20).
- **H2 Console & SQL Logging**: Disabled.

Run with `prod`:
```bash
cd salesnotes
.\mvnw.cmd spring-boot:run -Pprod
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

## 📡 REST API Reference

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/dashboard/summary` | Fetch pipeline metrics and recent notes |
| `GET` | `/api/notes` | List notes (supports `search`, `status`, `priority`, `customerId`) |
| `GET` | `/api/notes/{id}` | Get note details by ID |
| `POST` | `/api/notes` | Create a new sales note |
| `PUT` | `/api/notes/{id}` | Update existing sales note |
| `PATCH` | `/api/notes/{id}/status` | Update only note status |
| `DELETE` | `/api/notes/{id}` | Delete a sales note |
| `GET` | `/api/customers` | List customers (supports `search`) |
| `POST` | `/api/customers` | Create a new customer |
| `GET` | `/api/users` | List all users (supports `search`, `role`, `active`) |
| `GET` | `/api/users/{id}` | Get user by ID |
| `POST` | `/api/users` | Create a new user (with role validation) |
| `PUT` | `/api/users/{id}` | Update existing user details |
| `PATCH` | `/api/users/{id}/status` | Activate / deactivate a user |
| `DELETE` | `/api/users/{id}` | Delete a user |

---

## 💡 Common Component Architecture (`common[componentname]`)

The code commonly used from Angular and Ionic from `component.ts` is defined in separate `common[componentname]` components:

```
                       +----------------------------------------------------+
                       |      CommonNoteListComponent (@common)             |
                       |  - notes, filteredNotes, loading, errorMessage     |
                       |  - searchTerm, selectedStatus, selectedPriority    |
                       |  - loadNotes(), applyFilter(), deleteNote()...     |
                       +-------------------------+--------------------------+
                                                 |
                     +---------------------------+---------------------------+
                     |                                                       |
                     v                                                       v
+------------------------------------------+    +------------------------------------------+
|  Angular Component (Web)                 |    |  Ionic Component (Mobile)                |
|  NoteListComponent extends Common...     |    |  NoteListIonicComponent extends Common...|
|                                          |    |                                          |
|  note-list.component.html:               |    |  note-list.page.html:                    |
|  - Desktop SaaS Table & Grid             |    |  - <ion-header>, <ion-toolbar>           |
|  - Search Bar & Status Filter Pills      |    |  - <ion-searchbar>, <ion-segment>        |
|  - Web Action Buttons & Badges           |    |  - <ion-refresher> (pull-to-refresh)     |
|                                          |    |  - <ion-item-sliding> (swipe to delete)  |
|                                          |    |  - <ion-fab> (mobile action button)      |
+------------------------------------------+    +------------------------------------------+
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
