# 🏠 Mate Production – Платформа по аренде квартир и поиску сожителей

**Mate Production** – это современная веб-платформа для поиска квартир и сожителей. Система поддерживает публикацию объявлений, чат в реальном времени, административную модерацию, отчёты о нарушениях и многоуровневую авторизацию.

Проект построен на **Java 17 + Spring Boot**, с использованием **чистой архитектуры**, поддержкой **WebSocket-чата**, **двухфакторной аутентификации**, **email-верификации**, **Docker**, CI/CD и unit-тестов.

---

## 🚀 Возможности

- 👤 Регистрация / Вход (email, Google OAuth2)
- 📬 Email-верификация аккаунта
- 🔐 Двухфакторная аутентификация (2FA) через Google Authenticator
- 🏠 Добавление объявлений по аренде квартиры / поиску сожителя  
  Ограничения:
  - Пользователь может создать не более 10 объявлений
  - Сожитель может создать только одно объявление
- 🗂 Модерация объявлений (перед публикацией)
- ✅ Админ-панель (управление пользователями, объявлениями, жалобами)
- ⭐ Избранные объявления
- 💬 WebSocket-чат между пользователями
- 🚩 Жалобы на пользователей / объявления
- 🧪 Unit-тесты + GitHub Actions (CI/CD)
- 🐳 Docker + Docker Compose
- ☁️ Продакшн деплой на [Railway](https://railway.app)
- 🌐 Фронтенд: [mate-production.netlify.app](https://animated-salamander-7746f5.netlify.app/listings/houses)

---

## 🧰 Используемые технологии

- Java 17  
- Spring Boot  
- Spring Security (JWT, OAuth2)  
- PostgreSQL + JPA (Hibernate)  
- WebSocket (чат)  
- Docker + Docker Compose  
- JUnit 5 + Mockito  
- GitHub Actions (CI/CD)  
- MapStruct, Lombok  
- Clean Architecture + Factory Pattern  
- SMTP Email-сервис  
- Google Authenticator (2FA)

---

## 📁 Структура проекта

```plaintext
mate-production
├── config/        → Конфигурации (security, WebSocket, CORS и т.д.)
├── controller/    → REST-контроллеры
├── dto/           → Data Transfer Objects
├── entity/        → JPA-сущности (таблицы БД)
├── exception/     → Глобальный ExceptionHandler и кастомные исключения
├── mapper/        → MapStruct-мапперы
├── repository/    → Интерфейсы Spring Data JPA
├── security/      → JWT, OAuth2, 2FA, фильтры
├── service/       → Бизнес-логика, ограничения, правила
├── util/          → Утилиты (QR-коды, генерация токенов и др.)
└── websocket/     → WebSocket-конфигурация и обработка сообщений
🧪 CI/CD с GitHub Actions
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
⚙️ Запуск проекта (локально с Docker)
git clone https://github.com/aidyn2006/mate-production.git
cd mate-production
docker-compose up --build

