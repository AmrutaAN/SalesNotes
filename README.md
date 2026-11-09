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
│   ├── models/                           # SalesNote, Customer, NoteStatus, Priority, etc.
│   └── services/                         # SalesNoteService, CustomerService
│
├── salesnotes/                           # 1) Spring Boot Backend API (Java + SQL)
│   ├── src/main/java/com/salesnotes/
│   │   ├── config/                       # CorsConfig, DataInitializer (sample seed data)
│   │   ├── controller/                   # REST Controllers: Notes, Customers, Dashboard
│   │   ├── dto/                          # DTOs: SalesNoteRequest, SalesNoteResponse, DashboardSummary
│   │   ├── entity/                       # JPA Entities: SalesNote, Customer, NoteCategory
│   │   ├── repository/                   # Spring Data JPA Repositories
│   │   └── service/                      # Business Services
│   ├── src/main/resources/
│   │   └── application.yml               # H2 (in-memory) + MySQL + PostgreSQL configurations
│   ├── pom.xml
│   └── mvnw / mvnw.cmd
│
├── salesnotes-angular/                   # 2) Angular Web Application
│   ├── src/app/
│   │   ├── common/                       # Common[ComponentName] base components
│   │   └── components/
│   │       ├── dashboard/                # DashboardComponent extends CommonDashboardComponent
│   │       ├── note-list/                # NoteListComponent extends CommonNoteListComponent
│   │       ├── note-detail/              # NoteDetailComponent extends CommonNoteDetailComponent
│   │       ├── note-form/                # NoteFormComponent extends CommonNoteFormComponent
│   │       └── customer-list/            # CustomerListComponent extends CommonCustomerListComponent
│   ├── package.json
│   └── angular.json
│
└── salesnotes-ionic/                     # 3) Ionic Mobile Application
    ├── src/app/
    │   ├── common/                       # Common[ComponentName] base components
    │   ├── pages/
    │   │   ├── dashboard/                # DashboardIonicComponent extends CommonDashboardComponent
    │   │   ├── note-list/                # NoteListIonicComponent extends CommonNoteListComponent
    │   │   ├── note-detail/              # NoteDetailIonicComponent extends CommonNoteDetailComponent
    │   │   ├── note-form/                # NoteFormIonicComponent extends CommonNoteFormComponent
    │   │   └── customer-list/            # CustomerListIonicComponent extends CommonCustomerListComponent
    │   └── tabs/                         # Ionic Tab navigation
    ├── package.json
    └── ionic.config.json
```

---

## 💡 Common Component Architecture (`common[componentname]`)

As specified in the architecture requirements, **the code commonly used from Angular and Ionic from `component.ts` is defined in separate `common[componentname]` components**:

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
1. **`CommonNoteListComponent`** (`common-note-list.component.ts`):
   - Notes collection management, keyword search, priority/status filtering, note deletion, status transitions.
   - Extended by `NoteListComponent` (Angular Web) and `NoteListIonicComponent` (Ionic Mobile).
2. **`CommonNoteDetailComponent`** (`common-note-detail.component.ts`):
   - Single note fetching by route `:id`, quick status toggle, deletion with redirection.
   - Extended by `NoteDetailComponent` (Angular Web) and `NoteDetailIonicComponent` (Ionic Mobile).
3. **`CommonNoteFormComponent`** (`common-note-form.component.ts`):
   - Reactive form group setup, customer dropdown population, create vs edit detection, validation, submit handling.
   - Extended by `NoteFormComponent` (Angular Web) and `NoteFormIonicComponent` (Ionic Mobile).
4. **`CommonDashboardComponent`** (`common-dashboard.component.ts`):
   - Pipeline value calculation, metrics aggregation, recent notes feed.
   - Extended by `DashboardComponent` (Angular Web) and `DashboardIonicComponent` (Ionic Mobile).
5. **`CommonCustomerListComponent`** (`common-customer-list.component.ts`):
   - Account and client directory, search filtering, account removal.
   - Extended by `CustomerListComponent` (Angular Web) and `CustomerListIonicComponent` (Ionic Mobile).

---

## 🚀 Getting Started

### Prerequisites
- **Java**: 21 or 25
- **Node.js**: v22+ or v24+
- **npm**: 10+
- **Git**

---

### 1) Running the Backend (`salesnotes`)

The Spring Boot backend uses an **in-memory H2 SQL database** by default, with pre-loaded seed data (customers, categories, and sales notes).

```bash
cd salesnotes

# Run with Maven Wrapper
./mvnw spring-boot:run
# Or on Windows PowerShell:
.\mvnw.cmd spring-boot:run
```

- **API Base URL**: `http://localhost:8080/api`
- **H2 SQL Database Console**: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:salesnotesdb`
  - Username: `sa`
  - Password: *(leave blank)*

#### Switching to MySQL or PostgreSQL:
The project includes ready-to-use profiles in `application.yml`:
```bash
# MySQL
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql

# PostgreSQL
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres
```

---

### 2) Running the Angular Web App (`salesnotes-angular`)

```bash
cd salesnotes-angular

# Install dependencies (if not already installed)
npm install

# Start development server
npm start
# Or
ng serve
```

- Open your browser at: **`http://localhost:4200`**

---

### 3) Running the Ionic Mobile App (`salesnotes-ionic`)

```bash
cd salesnotes-ionic

# Install dependencies (if not already installed)
npm install

# Start development server
npm start
# Or using Ionic CLI:
ionic serve
```

- Open your browser at: **`http://localhost:8100`** (or use browser mobile device inspection mode: `Ctrl+Shift+M`)

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

---

## 📄 Git Repository

Remote repository origin is configured to:
`https://github.com/AmrutaAN/SalesNotes.git`
