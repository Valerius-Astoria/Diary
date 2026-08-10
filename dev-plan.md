# Diary — Development Plan

## Planned work: `chore-baseline`

**Status:** in progress

### Functionality to add

- Maven dependencies (JPA, Security, Thymeleaf, Web MVC, validation, PostgreSQL, H2, Lombok)
- Baseline application and test configuration
- `.gitignore` updates for secrets

### Files expected to change

- `pom.xml`
- `src/main/resources/application.properties`
- `src/test/resources/application.properties`
- `.gitignore`

### Files that must not change

- `src/main/java/com/valerius/diary/DiaryApplication.java`

---

## Planned work: `feat-auth`

**Status:** planned

### Functionality to add

- User entity and repository
- Registration form with validation
- Spring Security configuration (BCrypt, form login)
- Login and registration pages
- Security integration tests

### Files expected to change

- `src/main/java/com/valerius/diary/model/User.java`
- `src/main/java/com/valerius/diary/repository/UserRepository.java`
- `src/main/java/com/valerius/diary/security/SecurityConfig.java`
- `src/main/java/com/valerius/diary/security/RegistrationForm.java`
- `src/main/java/com/valerius/diary/controller/AuthController.java`
- `src/main/java/com/valerius/diary/controller/HomeController.java`
- `src/main/resources/templates/login.html`
- `src/main/resources/templates/register.html`
- `src/test/java/com/valerius/diary/SecurityIntegrationTest.java`
- `src/test/java/com/valerius/diary/RegistrationIntegrationTest.java`

---

## Planned work: `feat-diary-crud`

**Status:** planned

### Functionality to add

- DiaryEntry entity with author ownership
- DiaryEntryService for CRUD with ownership checks
- DiaryController and entry templates
- Ownership integration tests

### Files expected to change

- `src/main/java/com/valerius/diary/model/DiaryEntry.java`
- `src/main/java/com/valerius/diary/repository/DiaryEntryRepository.java`
- `src/main/java/com/valerius/diary/security/DiaryEntryForm.java`
- `src/main/java/com/valerius/diary/service/DiaryEntryService.java`
- `src/main/java/com/valerius/diary/controller/DiaryController.java`
- `src/main/resources/templates/entries/*.html`
- `src/test/java/com/valerius/diary/DiaryEntryIntegrationTest.java`

---

## Planned work: `feat-diary-search-ui`

**Status:** planned

### Functionality to add

- Keyword search, date range filter, sorting, pagination
- Base layout template and responsive CSS
- Global error handling

### Files expected to change

- `src/main/java/com/valerius/diary/service/DiaryEntryService.java`
- `src/main/java/com/valerius/diary/repository/DiaryEntryRepository.java`
- `src/main/java/com/valerius/diary/controller/DiaryController.java`
- `src/main/java/com/valerius/diary/config/GlobalControllerAdvice.java`
- `src/main/resources/templates/layout.html`
- `src/main/resources/templates/entries/list.html`
- `src/main/resources/static/css/app.css`
- `src/main/resources/templates/error.html`
- `src/test/java/com/valerius/diary/DiaryEntryServiceTest.java`

---

## Planned work: `ops-deploy`

**Status:** planned

### Functionality to add

- Dockerfile and `.dockerignore`
- Render blueprint and `.env.example`

### Files expected to change

- `Dockerfile`
- `.dockerignore`
- `render.yaml`
- `.env.example`
