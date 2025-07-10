# 🏠 Mate Production – Платформа по аренде квартир и поиску сожителей

**Mate Production** – это современная веб-платформа для поиска квартир и сожителей. Система поддерживает публикацию объявлений, чат в реальном времени, административную модерацию, отчёты о нарушениях и многоуровневую авторизацию.

Проект построен на **Java 17 + Spring Boot**, с использованием **чистой архитектуры**, поддержкой **WebSocket-чата**, **двухфакторной аутентификации**, **email-верификации**, **Docker**, CI/CD и unit-тестов.

---

## 🚀 Возможности

- 👤 Регистрация / Вход (по email, через Google OAuth2)
- 📬 Email-верификация аккаунта
- 🔐 Двухфакторная аутентификация через Google Authenticator (QR-код + код подтверждения)
- 🏠 Добавление объявлений по аренде квартиры / поиску сожителя
  - Ограничения:
    - Пользователь может добавить не более 10 объявлений
    - Сожитель может создать только одно объявление
- 🗂 Модерация объявлений (объявления сначала отправляются на рассмотрение администраторам)
- ✅ Админ-панель для управления пользователями, объявлениями, модерацией и жалобами
- ⭐ Добавление объявлений в избранное
- 💬 Чат между пользователями (реализован на WebSocket)
- 🚩 Возможность пожаловаться на пользователя/объявление
- 🧪 Покрытие unit-тестами и настройка CI/CD
- 🐳 Docker + Docker Compose для лёгкого запуска
- ☁️ Развёртывание на [Railway](https://railway.app)
- 🌐 Продакшн фронт: [mate-production.netlify.app](https://animated-salamander-7746f5.netlify.app/listings/houses)

---

## 🧰 Используемые технологии

- Java 17
- Spring Boot
- Spring Security + JWT
- OAuth2 (Google)
- PostgreSQL
- JPA (Hibernate)
- WebSocket (чат)
- Docker + Docker Compose
- JUnit 5 + Mockito
- GitHub Actions (CI/CD)
- MapStruct
- Lombok
- Clean Architecture
- Factory Pattern
- Email-сервис (через SMTP)
- Google Authenticator (2FA)

---

## 📁 Структура проекта (чистая архитектура)

mate-production
├── config/ → Конфигурации (security, WebSocket, CORS и др.)
├── controller/ → REST-контроллеры
├── dto/ → Data Transfer Objects (вход/выход)
├── entity/ → JPA-сущности (PostgreSQL таблицы)
├── exception/ → Кастомные исключения и глобальный ExceptionHandler
├── mapper/ → MapStruct-мапперы между Entity и DTO
├── repository/ → Интерфейсы доступа к данным (Spring Data JPA)
├── security/ → Безопасность: JWT, OAuth2, 2FA, фильтры
├── service/ → Бизнес-логика, валидации, ограничения
├── util/ → Утилиты: QR-коды, генерация токенов, время и т.д.
└── websocket/ → Конфигурация и логика чата на WebSocket


---

## 🧪 Тесты и CI/CD

- При каждом push или pull request выполняется GitHub Actions workflow:
  - Сборка проекта
  - Прогон unit-тестов
  - Проверка безопасности и качества кода

```yaml
name: CI Pipeline

on: [push, pull_request]

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Build and test
        run: ./mvnw clean verify
⚙️ Запуск проекта
📦 Локально (через Docker)
git clone https://github.com/aidyn2006/mate-production.git
cd mate-production
docker-compose up --build
<img width="1076" height="935" alt="image" src="https://github.com/user-attachments/assets/b8ef43d4-0d7e-43ea-8db2-7d7900171c9a" />
<img width="1826" height="1103" alt="image" src="https://github.com/user-attachments/assets/d17587d8-7a06-4eb0-bf00-16ae674d7144" />
<img width="1761" height="1052" alt="image" src="https://github.com/user-attachments/assets/da21ad63-85e5-4b8f-855a-71c95c0127c8" />
<img width="1597" height="1115" alt="image" src="https://github.com/user-attachments/assets/89e8b993-f75b-42de-b816-4cde2784a598" />
<img width="1139" height="1131" alt="image" src="https://github.com/user-attachments/assets/b22c3298-9179-4453-9e83-ebc2085fdb5f" />
<img width="1219" height="1060" alt="image" src="https://github.com/user-attachments/assets/2e4eca5d-6593-42b1-90e3-e6b447b724e4" />
<img width="747" height="1011" alt="image" src="https://github.com/user-attachments/assets/ba258e26-f9a8-4560-b3b5-6c462bfc472e" />
<img width="1768" height="1055" alt="image" src="https://github.com/user-attachments/assets/ca65ca7a-a9bc-4a79-b0d1-d4f23b5fc1c2" />
<img width="1101" height="958" alt="image" src="https://github.com/user-attachments/assets/c47a4374-f3a1-4ff5-9926-b3f042467ea1" />
<img width="1184" height="1166" alt="image" src="https://github.com/user-attachments/assets/6b443949-d27a-4174-9d8d-634f7299ef41" />
<img width="2498" height="1231" alt="image" src="https://github.com/user-attachments/assets/4d703f09-12cb-4825-8f48-f08a52aec78a" />
