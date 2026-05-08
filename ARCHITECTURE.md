# Architecture & Interview Prep Guide

> Read this before your interview. Each section maps to a common question.

---

## System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Browser                                   │
│            React + Vite  (localhost:5173)                        │
│                                                                  │
│   LoginPage  →  TicketsPage  →  ClustersPage  →  DashboardPage  │
│              Zustand (auth token in localStorage)                │
│              Axios (JWT injected via interceptor)                │
└──────────────────────────┬──────────────────────────────────────┘
                           │  HTTP (proxied via Vite → port 8080)
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Spring Boot Backend                            │
│                   (localhost:8080)                               │
│                                                                  │
│  ┌─────────────┐   ┌─────────────┐   ┌──────────────────────┐  │
│  │AuthController│   │TicketControl│   │  ClusterController   │  │
│  │POST /auth/  │   │GET /tickets │   │  POST /clusters/     │  │
│  │login        │   │POST /tickets│   │  analyze             │  │
│  └──────┬──────┘   └──────┬──────┘   └──────────┬───────────┘  │
│         │                 │                      │              │
│  ┌──────▼──────┐   ┌──────▼──────┐   ┌──────────▼───────────┐  │
│  │ AuthService │   │TicketService│   │ GroqClusteringService│  │
│  └──────┬──────┘   └──────┬──────┘   └──────────┬───────────┘  │
│         │                 │                      │              │
│  ┌──────▼─────────────────▼──────────────────────▼───────────┐  │
│  │                 Spring Data JPA                            │  │
│  │            UserRepository  /  TicketRepository            │  │
│  └──────────────────────────┬──────────────────────────────┘  │
│                             │                                   │
│                    ┌────────▼────────┐                          │
│                    │  H2 In-Memory   │                          │
│                    │  (users+tickets)│                          │
│                    └─────────────────┘                          │
│                                                                  │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │           Spring Security Filter Chain                    │  │
│  │  Request → JwtAuthFilter → Controller (if valid token)    │  │
│  └───────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────┬──────────────────┘
                                               │  REST (HTTPS)
                                               ▼
                              ┌─────────────────────────────┐
                              │         Groq API             │
                              │  llama-3.3-70b-versatile     │
                              │  (clustering via LLM prompt) │
                              └─────────────────────────────┘
```

---

## JWT Authentication Flow

**"Walk me through how authentication works in your app."**

```
Step 1 — Login
  Client  →  POST /api/auth/login  {email, password}
  Server  →  AuthService.login()
              AuthenticationManager.authenticate()   ← Spring validates bcrypt hash
              JwtTokenProvider.generateToken(email)  ← signs HS256 JWT
  Response → {token: "eyJ...", email, role}
  Client  →  Zustand stores token in localStorage

Step 2 — Authenticated Request
  Client adds header:  Authorization: Bearer eyJ...

Step 3 — JwtAuthFilter (runs before every controller)
  Extracts token from header
  JwtTokenProvider.validateToken() → checks signature + expiry
  JwtTokenProvider.getEmailFromToken() → reads subject claim
  UserDetailsService.loadUserByUsername(email) → loads user from DB
  SecurityContextHolder.setAuthentication(auth) → marks request as authenticated

Step 4 — Controller runs
  Spring Security sees authenticated context, allows request
```

**Key points for interview:**

- JWT is **stateless** — server stores no session. Every token is self-contained.
- `JwtAuthFilter extends OncePerRequestFilter` — guaranteed to run exactly once per request.
- `BCryptPasswordEncoder` — passwords hashed with adaptive cost factor. Never stored plaintext.
- Token expiry: 24 hours (`86400000ms`). No refresh token in this demo (intentional simplicity).

---

## Groq LLM Clustering Flow

**"How does the AI clustering work? Why not use k-means?"**

````
Step 1 — Trigger
  Client  →  POST /api/clusters/analyze
  Server  →  GroqClusteringService.analyzeAndCluster()

Step 2 — Build Prompt
  Fetch all tickets from H2
  Construct prompt:
    "Here are 35 support tickets. Group them into clusters.
     Return ONLY a JSON array: [{id: 1, cluster: "Auth Issues"}, ...]
     Tickets:
     ID: 1 | Cannot log in after password reset | Password reset link..."

Step 3 — Call Groq API
  POST https://api.groq.com/openai/v1/chat/completions
  Model: llama-3.3-70b-versatile
  Temperature: 0.1 (low = consistent cluster names across runs)

Step 4 — Parse Response
  Extract content from choices[0].message.content
  Strip markdown if LLM added ``` despite instructions
  Parse JSON array → Map<ticketId, clusterName>

Step 5 — Persist
  For each ticket: ticket.clusterLabel = clusterName
  Save to H2

Step 6 — Return
  Response: full updated ticket list with clusterLabel filled
````

**Why LLM over k-means / traditional ML:**

- LLM understands **semantic meaning** without any training data
- No feature engineering (TF-IDF, embeddings, etc.)
- Natural language cluster names out of the box
- Single API call, zero infrastructure
- Tradeoff: costs money at scale, non-deterministic (low temp mitigates this)

**Why Groq specifically:**

- Free tier with generous rate limits
- Fastest inference available for open models
- `llama-3.3-70b-versatile` = best open-source model for instruction following

---

## Spring Security Configuration

**"How did you configure Spring Security?"**

Key decisions in `SecurityConfig.java`:

```java
SessionCreationPolicy.STATELESS     // No HttpSession — pure JWT
csrf(AbstractHttpConfigurer::disable) // CSRF protection not needed without cookies
.requestMatchers("/api/auth/**").permitAll()  // Login endpoint is public
.anyRequest().authenticated()        // Everything else requires JWT
.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
```

**Spring Security 6 changes (vs older versions):**

- No more `WebSecurityConfigurerAdapter` (deprecated) — use `SecurityFilterChain` bean
- `antMatchers` replaced by `requestMatchers`
- Lambda DSL instead of method chaining with `.and()`
- `@EnableWebSecurity` no longer disables default security config by itself

---

## Frontend Architecture

**"How does the frontend handle auth state?"**

```
Zustand Store (authStore.js)
  ├── token: string | null
  ├── user: {email, role} | null
  ├── setAuth(token, user) → login
  └── logout() → clear state

persist middleware → saves to localStorage → survives page refresh

Axios Interceptor (axiosClient.js)
  request: attach Authorization: Bearer <token> to every API call
  response: on 401 → logout() + redirect to /login

ProtectedRoute (App.jsx)
  Wraps all pages except /login
  Reads token from Zustand
  If null → <Navigate to="/login" />
```

**Why Zustand over Redux:**

- No boilerplate (no actions/reducers/dispatch)
- Direct state mutation via `set()`
- Built-in `persist` middleware — one line for localStorage persistence
- ~1KB bundle vs Redux Toolkit's ~11KB

---

## Database Schema

```sql
users
  id          BIGINT (PK, auto-increment)
  email       VARCHAR (unique, not null)
  password    VARCHAR (bcrypt hash)
  role        VARCHAR ('ADMIN')

tickets
  id            BIGINT (PK, auto-increment)
  title         VARCHAR (not null)
  description   TEXT
  submitted_by  VARCHAR
  created_at    TIMESTAMP
  cluster_label VARCHAR (null until AI analysis run)
```

**Why H2:**

- Zero setup — embedded, in-memory, JVM-native
- Perfect for demos: clone and run, no DB installation
- Spring names convention: Java field `submittedBy` → DB column `submitted_by` (automatic)
- Tradeoff: data resets on every restart (acceptable for portfolio demo)

---

## Common Interview Questions

### "Why Spring Boot for the backend?"

Standard in enterprise Java. Opinionated defaults (auto-configuration) reduce boilerplate. Spring Security is industry standard for Java auth. Familiar to hiring teams.

### "What would you change for production?"

1. Replace H2 with PostgreSQL or MySQL
2. Store JWT secret in AWS Secrets Manager / Vault
3. Add refresh tokens (short-lived access + long-lived refresh)
4. Add rate limiting on the `/api/clusters/analyze` endpoint (Groq costs money)
5. Cache cluster results — don't re-run Groq if tickets haven't changed
6. Add proper error handling and validation (Bean Validation / `@Valid`)

### "How does Spring Data JPA work?"

Spring generates SQL at runtime from interface methods. `findByEmail(String email)` → `SELECT * FROM users WHERE email = ?`. No SQL boilerplate needed. Under the hood: Hibernate ORM generates and executes SQL.

### "What is the filter chain?"

Spring Security is a chain of `javax.servlet.Filter` implementations. Each filter processes the request, optionally stops it (401), or passes it to the next. `JwtAuthFilter` runs before `UsernamePasswordAuthenticationFilter`, so JWT is validated before Spring's default form login logic runs.

### "Why did you use RestTemplate instead of WebClient?"

RestTemplate is synchronous and simpler for a demo with one external API call. WebClient (from Spring WebFlux) is reactive/non-blocking — better for high throughput but adds complexity. This demo doesn't need reactive programming.

### "Walk me through a full request lifecycle."

1. User clicks "Run AI Analysis"
2. React calls `POST /api/clusters/analyze` via Axios
3. Axios interceptor adds `Authorization: Bearer <token>` header
4. Spring receives request → `JwtAuthFilter` validates token → sets SecurityContext
5. `ClusterController.analyze()` is invoked
6. `GroqClusteringService` fetches 35 tickets from H2
7. Builds a natural-language prompt, POSTs to Groq API
8. Groq returns JSON cluster assignments
9. Service persists `clusterLabel` on each ticket in H2
10. Returns updated ticket list as JSON
11. React updates state → UI re-renders with grouped clusters
12. User navigates to Dashboard → Recharts renders bar/pie charts from cluster data

---

## Demo Script (for screen-share interviews)

1. Open `http://localhost:5173` → login page appears (JWT protecting all routes)
2. Login with `admin@demo.com` / `password` → redirected to Tickets page
3. Show 35 tickets pre-loaded (data.sql seeded H2 on startup)
4. Add a new ticket to show `POST /api/tickets` works
5. Navigate to **Clusters** → click "Run AI Analysis"
6. Wait ~5 seconds → tickets appear grouped by theme
7. Navigate to **Dashboard** → bar chart and pie chart show cluster distribution
8. Mention: open Network tab to show JWT in Authorization header
9. Mention: open `http://localhost:8080/h2-console` to show live DB state
