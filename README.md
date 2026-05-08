# Ticket Cluster Analyzer

Full-stack AI portfolio project. Clusters support tickets using a Groq LLM. Built with Spring Boot, React, and JWT authentication.

---

## Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- Maven 3.8+
- Free Groq API key → https://console.groq.com

### 1. Set Groq API Key

**Windows (PowerShell):**
```powershell
$env:GROQ_API_KEY = "gsk_your_key_here"
```

**Mac/Linux:**
```bash
export GROQ_API_KEY=gsk_your_key_here
```

### 2. Start Backend
```bash
cd backend
mvn spring-boot:run
```
Backend runs on `http://localhost:8080`

### 3. Start Frontend
```bash
cd frontend
npm install
npm run dev
```
Frontend runs on `http://localhost:5173`

### 4. Open the App
Go to `http://localhost:5173`

**Demo login:**
- Email: `admin@demo.com`
- Password: `password`

---

## What it does

1. **Login** — JWT-secured. Enter credentials, get a token stored in localStorage.
2. **Tickets page** — View all 35 pre-loaded support tickets. Add new ones.
3. **Clusters page** — Click "Run AI Analysis". App sends all tickets to Groq LLM in one request. LLM groups them by theme and returns JSON. Results are saved to the database.
4. **Dashboard** — Bar chart and pie chart showing cluster distribution.

---

## Tech Stack

| Layer | Technology | Why |
|-------|-----------|-----|
| Backend | Spring Boot 3.2 | Industry standard Java framework |
| Auth | Spring Security + JWT | Stateless, demo-friendly (no OAuth setup) |
| Database | H2 In-Memory | Zero config, perfect for demo/portfolio |
| LLM | Groq API (`llama-3.3-70b-versatile`) | Free tier, fast, best available open model |
| Frontend | React 18 + Vite | Fast dev server, modern tooling |
| Charts | Recharts | React-native charting library |
| State | Zustand | Lightweight, no boilerplate vs Redux |
| HTTP Client | Axios | Interceptors for JWT injection + 401 handling |

---

## Project Structure

```
ticket-cluster-analyzer/
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/demo/ticketanalyzer/
│       │   ├── TicketAnalyzerApplication.java
│       │   ├── config/
│       │   │   └── SecurityConfig.java        ← Spring Security + CORS
│       │   ├── controller/
│       │   │   ├── AuthController.java        ← POST /api/auth/login
│       │   │   ├── TicketController.java      ← GET/POST /api/tickets
│       │   │   └── ClusterController.java     ← POST /api/clusters/analyze
│       │   ├── dto/
│       │   │   ├── LoginRequest.java
│       │   │   └── LoginResponse.java
│       │   ├── entity/
│       │   │   ├── User.java
│       │   │   └── Ticket.java
│       │   ├── repository/
│       │   │   ├── UserRepository.java
│       │   │   └── TicketRepository.java
│       │   ├── security/
│       │   │   ├── JwtTokenProvider.java      ← generate + validate tokens
│       │   │   ├── JwtAuthFilter.java         ← intercept every request
│       │   │   └── UserDetailsServiceImpl.java
│       │   └── service/
│       │       ├── AuthService.java
│       │       ├── TicketService.java
│       │       └── GroqClusteringService.java ← LLM integration
│       └── resources/
│           ├── application.properties
│           └── data.sql                       ← 35 demo tickets
├── frontend/
│   ├── src/
│   │   ├── api/axiosClient.js     ← JWT interceptor
│   │   ├── store/authStore.js     ← Zustand auth state
│   │   ├── components/Navbar.jsx
│   │   └── pages/
│   │       ├── LoginPage.jsx
│   │       ├── TicketsPage.jsx
│   │       ├── ClustersPage.jsx
│   │       └── DashboardPage.jsx
│   ├── App.jsx                    ← routing + ProtectedRoute
│   └── main.jsx
├── README.md
└── ARCHITECTURE.md                ← deep dive + interview prep
```

---

## API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/login` | No | Login, returns JWT |
| GET | `/api/tickets` | JWT | List all tickets |
| POST | `/api/tickets` | JWT | Create ticket |
| POST | `/api/clusters/analyze` | JWT | Trigger Groq clustering |

---

## H2 Database Console

While the backend is running, access the database at:
`http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:ticketdb`
- Username: `sa`
- Password: *(empty)*
