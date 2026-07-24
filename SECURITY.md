# Security Notes

This document tracks security-relevant decisions in SmartStock AI, and the
hardening work done as the app moved from local prototype to a publicly
deployed service.

## Authentication & Authorization

- Spring Security is applied to every route except `/login`, `/register`,
  static assets, and `/actuator/health`. `/dashboard` and all of `/api/**`
  require an authenticated session — this is enforced by the filter chain
  in `SecurityConfig`, not by individual controllers.
- Two roles exist: `ADMIN` (full read/write, including product management)
  and `VIEWER` (read-only). Write actions (`/add-product`, `/edit-product/**`,
  `/update-product/**`, `/delete-product/**`) are restricted to `ADMIN` at
  the filter-chain level.
- This is proven by an automated test (`SecurityEnforcementTest`), not just
  manual verification — see [Testing](#testing) below.

## Credential Management

- `admin` and `viewer` accounts are seeded on startup from
  `APP_SEED_ADMIN_PASSWORD` and `APP_SEED_VIEWER_PASSWORD` environment
  variables. Seeding is **idempotent**: it checks whether the account
  already exists before creating it, so a redeploy does not silently reset
  either password back to a default.
- No credentials are hardcoded in source. Database credentials
  (`SPRING_DATASOURCE_*`, `SPRING_FLYWAY_*`) and the seed passwords above
  are all supplied via environment variables at deploy time.
- Database credentials have been rotated twice during hardening: once when
  the original Neon connection string was found exposed, and again as a
  precaution after being pasted multiple times in a chat/log context during
  debugging. Rotation process: generate new password via Neon, verify with
  a local `psql` connection test, then update Render env vars and redeploy
  — never the reverse order.

## What changed, and why

Earlier in development, `/dashboard` and API routes were reachable without
authentication, and `admin`/`admin` was a real working default login. Both
were fixed:

1. Added the `authorizeHttpRequests` rules described above so every
   non-public route requires a session.
2. Replaced the hardcoded `admin`/`admin` seed with env-var-driven,
   idempotent seeding, so there is no default credential to guess.

## Testing

`SecurityEnforcementTest` (`src/test/java/com/Luxa/inventory/`) runs
against the real `SecurityFilterChain` via `@SpringBootTest` +
`@AutoConfigureMockMvc`, using the H2 test database — not a mocked
security slice. It asserts:

- Unauthenticated requests to `/dashboard` and `/api/users` redirect to
  `/login`.
- A logged-in `VIEWER` can reach `/dashboard` but gets `403` on the
  admin-only `/add-product` route.
- A logged-in `ADMIN` can reach `/add-product`.
- `/login` itself stays public.

Run it directly with:

```bash
./mvnw test -Dtest=SecurityEnforcementTest
```

## Known gaps / not yet done

- No rate limiting on `/login` (brute-force protection).
- No structured audit logging of login attempts or admin actions.
- `application.properties` still contains some hardcoded dev-profile
  defaults intended only for local development; these should be reviewed
  before treating any profile other than `prod` as safe to expose.
- No CSRF protection (`csrf().disable()` in `SecurityConfig`) — acceptable
  for the current form-login/session setup but worth revisiting if the API
  is ever consumed by a separate frontend origin.
