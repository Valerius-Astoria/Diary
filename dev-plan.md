# Diary — Development Plan

## Completed: `chore-baseline` (merged 2026-08-10)

### Features added

- Maven dependencies for JPA, Security, Thymeleaf, validation, PostgreSQL, and H2 tests
- Baseline application and test configuration
- Secret-safe `.gitignore` entries

### Files added

- `src/test/resources/application.properties`

### Files modified

- `pom.xml`
- `src/main/resources/application.properties`
- `.gitignore`

---

## Completed: `feat-auth` (merged 2026-08-10)

### Features added

- User entity and repository with BCrypt form login
- Registration and login pages
- Security integration tests

### Files added

- `src/main/java/com/valerius/diary/model/User.java`
- `src/main/java/com/valerius/diary/repository/UserRepository.java`
- `src/main/java/com/valerius/diary/security/SecurityConfig.java`
- `src/main/java/com/valerius/diary/security/RegistrationForm.java`
- `src/main/java/com/valerius/diary/controller/AuthController.java`
- `src/main/java/com/valerius/diary/controller/HomeController.java`
- `src/main/resources/templates/login.html`
- `src/main/resources/templates/register.html`
- `src/main/resources/templates/index.html`
- `src/main/resources/static/css/app.css`
- `src/test/java/com/valerius/diary/SecurityIntegrationTest.java`
- `src/test/java/com/valerius/diary/RegistrationIntegrationTest.java`

---

## Completed: `feat-diary-crud` (merged 2026-08-10)

### Features added

- DiaryEntry entity with author ownership
- CRUD service, controller, and templates
- Ownership integration tests

### Files added

- `src/main/java/com/valerius/diary/model/DiaryEntry.java`
- `src/main/java/com/valerius/diary/repository/DiaryEntryRepository.java`
- `src/main/java/com/valerius/diary/security/DiaryEntryForm.java`
- `src/main/java/com/valerius/diary/service/DiaryEntryService.java`
- `src/main/resources/templates/entries/form.html`
- `src/main/resources/templates/entries/view.html`
- `src/test/java/com/valerius/diary/DiaryEntryIntegrationTest.java`

### Files modified

- `src/main/java/com/valerius/diary/controller/DiaryController.java`
- `src/main/resources/templates/entries/list.html`
- `src/main/resources/static/css/app.css`

---

## Completed: `feat-diary-search-ui` (merged 2026-08-10)

### Features added

- Keyword search, date range filter, sorting, and pagination
- Shared layout fragments and error page
- Search service tests

### Files added

- `src/main/java/com/valerius/diary/config/GlobalControllerAdvice.java`
- `src/main/resources/templates/layout.html`
- `src/main/resources/templates/error.html`
- `src/test/java/com/valerius/diary/DiaryEntryServiceTest.java`

### Files modified

- `src/main/java/com/valerius/diary/service/DiaryEntryService.java`
- `src/main/java/com/valerius/diary/repository/DiaryEntryRepository.java`
- `src/main/java/com/valerius/diary/controller/DiaryController.java`
- `src/main/resources/templates/entries/list.html`
- `src/main/resources/templates/entries/form.html`
- `src/main/resources/templates/entries/view.html`
- `src/main/resources/static/css/app.css`
- `src/test/java/com/valerius/diary/RegistrationIntegrationTest.java`
- `src/test/java/com/valerius/diary/DiaryEntryIntegrationTest.java`

---

## Planned work: `feat-ui-beautify`

**Status:** in progress

### Functionality to add

- Warm journal theme: paper tones, serif headings, polished cards and forms
- Shared layout fragments for auth/landing pages
- Consistent typography via Google Fonts

### Files expected to change

- `src/main/resources/static/css/app.css`
- `src/main/resources/templates/layout.html`
- `src/main/resources/templates/index.html`
- `src/main/resources/templates/login.html`
- `src/main/resources/templates/register.html`
- `src/main/resources/templates/error.html`
- `src/main/resources/templates/entries/list.html`
- `src/main/resources/templates/entries/form.html`
- `src/main/resources/templates/entries/view.html`

### Files that must not change

- Java controllers, services, security config, tests


### Features added

- Docker image build for Render deployment
- Render blueprint and environment variable template

### Files added

- `Dockerfile`
- `.dockerignore`
- `render.yaml`
- `.env.example`

### Notes

- Local Neon credentials belong in gitignored `application-local.properties`
- Production credentials are set on Render via `SPRING_DATASOURCE_*`
