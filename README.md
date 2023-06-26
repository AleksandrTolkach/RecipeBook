# RecipeBook project

## Описание

RecipeBook - социальная сеть для публикации/просмотра рецептов. 

Композиция содержит следующие элементы:
- Класс "User". Этот класс содержит информацию о пользователе, такую как идентификатор, время создания и обновления пользователя, логин, пароль, имя, название фотографии пользователя, список любимых рецептов.
- Класс "Recipe". Этот класс содержит информацию о самом рецепте, такую как идентификатор, время создания и обновления рецепта, название, автор, список ингредиентов, шаги выполнения рецепта, время приготовления, название фотографии рецепта.
- Связь "Авторство". Эта связь указывает на то, какой пользователь является автором рецепта, он же является экземпляром класса "User".
- Класс "Ingredient". Этот класс содержит информацию об ингредиентах, которые используются в рецептах, такую как идентификатор, время создания и обновления ингредиента, название, мера измерения.
- Связь "Ингредиенты". Эта связь указывает на то, какие ингредиенты должен содержать рецепт, которые нужны для его приготовления.
- Класс "Step". Этот класс содержит информацию о шаге приготовления рецепта, такую как идентификатор шага, время создания и обновления шага, описание действий, и фото шага приготовления.
- Класс "Шаги приготовления". Эта связь указывает на то, какие шаги необходимо выполнить, чтобы приготовить блюдо по рецепту.

## Запуск приложения

1. Для того чтобы запустить приложение, необходимо иметь установленное ПО Docker на локальной машине. 
2. Перейти в корень приложения RecipeBook и выполнить команду  
```
docker-compose up
```
3. Дождаться запуска приложения.
4. Для проверки API можно воспользоваться Postman и импортировать [запросы](RecipeBook.postman_collection.json), которые находятся в корне приложения RecipeBook. Для загрузки изображений можно использовать файлы, которые расположены в resource/static/images.

## Дополнительно

Приложение написано в соответствии с Google Style.  
Реализован функционал авторизации и аутентификации с использованием токенов. При входе в приложение токены передаются с помощью cookie.

Docker-compose содержит три сервиса:  
- S3 хранилище minio;  
- Базу данных MySQL;
- Само приложение RecipeBook;

При запуске контейнеров выполняются unit и интеграционные тесты, checkstyle. 
В связи с этим запуск приложения на слабых машинах может занять достаточно много времени. Для того чтобы ускорить запуск, если это необходимо, необходимо удалить следующие команды и Dockerfile в корне проекта  
```
./mvnw package && ./mvnw site && 
```
Для управления версионностью БД подключен flyway. В resource/db/migration находятся скрипты для создания таблиц в БД.
Для хранения изображений используется S3 хранилище minio, которое развернуто в контейнере.