# 🚀 Javito Platform API

![Swagger UI](https://img.shields.io/badge/Swagger-UI-%23Clojure?style=flat&logo=swagger)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green)
![Java](https://img.shields.io/badge/Java-17-blue)

Платформа для объявлений с системой комментариев, рейтингов и категорий

## 📌 Оглавление
- [Документация API](#📖-документация-api)
- [Сервисы](#🛠-сервисы)
  - [Комментарии](#💬-сервис-комментариев)
  - [Пользователи](#👥-сервис-пользователей)
  - [Категории](#🏷️-сервис-категорий)
  - [Объявления](#📢-сервис-объявлений)
  - [Рейтинги](#⭐-сервис-оценки-пользователей)
- [Модели данных](#📦-модели-данных)
- [Запуск проекта](#⚙️-запуск-проекта)

## 📖 Документация API
Доступна через Swagger UI:  
🔗 [Swagger UI](http://ваш-домен/swagger-ui.html)  
🔗 [OpenAPI 3.0 Spec](http://ваш-домен/v3/api-docs)

Версия API: `1.0.0`

## 🛠 Сервисы

### 💬 Сервис комментариев
| Метод | Путь | Описание |
|-------|------|----------|
| POST | `/api/comment/create-comment` | Создание комментария |
| PATCH | `/api/comment/update/{id}` | Редактирование комментария |
| GET | `/api/comment/{adId}` | Получение комментариев объявления |
| GET | `/api/comment/current` | Комментарии текущего пользователя |
| DELETE | `/api/comment/delete/{id}` | Удаление комментария |

### 👥 Сервис пользователей
| Метод | Путь | Описание |
|-------|------|----------|
| POST | `/api/user/register` | Регистрация пользователя |
| POST | `/api/user/login` | Авторизация |
| POST | `/api/user/refresh` | Обновление токена |
| POST | `/api/user/createmoderator` | Создание модератора |
| PATCH | `/api/user/update-password` | Смена пароля |
| PATCH | `/api/user/update-info` | Обновление информации |
| GET | `/api/user/{id}` | Получение пользователя по ID |
| GET | `/api/user/get-all-users` | Все пользователи |
| DELETE | `/api/user/delete/{id}` | Удаление пользователя |

### 🏷️ Сервис категорий
| Метод | Путь | Описание |
|-------|------|----------|
| POST | `/api/category/add-category` | Добавление категории |
| GET | `/api/category/{id}` | Получение категории |
| DELETE | `/api/category/{id}` | Удаление категории |
| PATCH | `/api/category/{id}` | Обновление категории |
| GET | `/api/category/all` | Все категории |

### 📢 Сервис объявлений
| Метод | Путь | Описание |
|-------|------|----------|
| POST | `/api/advertisement/createad` | Создание объявления |
| PATCH | `/api/advertisement/update/{id}` | Обновление объявления |
| GET | `/api/advertisement/page` | Постраничное получение |
| GET | `/api/advertisement/getpage/{category}` | Поиск по категории |
| GET | `/api/advertisement/get/{id}` | Получение по ID |
| GET | `/api/advertisement/current-user` | Объявления пользователя |
| DELETE | `/api/advertisement/{id}` | Удаление объявления |

### ⭐ Сервис оценки пользователей
| Метод | Путь | Описание |
|-------|------|----------|
| POST | `/api/rating/create-rate` | Создание отзыва |
| GET | `/api/rating/{id}` | Отзывы пользователя |
| GET | `/api/rating/current` | Отзывы текущего пользователя |
| DELETE | `/api/rating/delete/{id}` | Удаление отзыва |

## 📦 Модели данных
Основные DTO модели API:
- `UserRequest` / `UserResponse`
- `AuthResponse`
- `AdvertisementRequest` / `AdvertisementResponse` 
- `CommentRequest` / `CommentResponse`
- `CategoryRequest` / `CategoryResponse`
- `RatingRequest` / `RatingResponse`

## ⚙️ Запуск проекта
1. Клонировать репозиторий:
```bash
git clone https://github.com/ваш-username/javito.git
```

2. Запустить через Docker:
```bash
docker-compose up --build
```

3. Доступ к API:
```
http://localhost:8080
```

## 📜 Лицензия
[MIT](LICENSE)

---

<div align="center">
  
</div>
