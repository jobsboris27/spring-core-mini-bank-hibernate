# MiniBank (Spring Core)

Консольное учебное банковское приложение на Java + Spring Core.

## Что умеет
- создавать пользователей;
- показывать всех пользователей и их счета;
- создавать дополнительные счета;
- пополнять и снимать деньги;
- переводить между счетами (с комиссией для разных пользователей);
- закрывать счет с переносом остатка;
- завершать работу по команде `EXIT`.

## Технологии
- Java 21
- Spring Core (`spring-context`)
- Конфигурация через `@Configuration`, `@PropertySource`, `@Component`
- Хранение данных в памяти (`Map`)

## Архитектура
- `User`, `Account` — POJO-модели.
- `UserService`, `AccountService` — бизнес-логика и хранение данных.
- `OperationCommand` + `ConsoleOperationType` — обработка команд (Command pattern).
- `OperationsConsoleListener` — главный цикл приложения.
- `ConsoleInput` — единая точка чтения/валидации консольного ввода.

## Команды и примеры использования
Приложение работает в интерактивном консольном режиме. Вводите команды из списка ниже:

* **`USER_CREATE`** — Создать нового пользователя
    * *Ввод:* `Иван Иванов`
    * *Вывод:* `Пользователь Иван Иванов успешно создан. ID: 1`
* **`SHOW_ALL_USERS`** — Показать всех пользователей и их баланс
    * *Вывод:* `ID: 1 | Иван Иванов | Счета: [ACC-101: 500.00 RUR]`
* **`ACCOUNT_CREATE`** — Создать дополнительный счет пользователю
    * *Ввод:* `1` (ID пользователя)
    * *Вывод:* `Создан новый счет ACC-102 для пользователя ID 1`
* **`ACCOUNT_DEPOSIT`** — Пополнить счет
    * *Ввод:* `ACC-101 1500`
    * *Вывод:* `Счет ACC-101 успешно пополнен на 1500. Текущий баланс: 2000.00`
* **`ACCOUNT_WITHDRAW`** — Снять деньги
    * *Ввод:* `ACC-101 200`
    * *Вывод:* `Со счета ACC-101 снято 200. Текущий баланс: 1800.00`
* **`ACCOUNT_TRANSFER`** — Перевод между счетами (с комиссией)
    * *Ввод:* `ACC-101 ACC-102 500`
    * *Вывод:* `Перевод 500 с ACC-101 на ACC-102 выполнен. Комиссия: 10.00`
* **`ACCOUNT_CLOSE`** — Закрыть счет с переносом остатка
    * *Ввод:* `ACC-102 ACC-101` (Закрываем ACC-102, остаток переносим на ACC-101)
    * *Вывод:* `Account ACC-102 closed. Transfer to account ACC-101 completed.`
* **`EXIT`** — Завершить работу приложения

## Настройки
Файл: `src/main/resources/application.properties`

```properties
# Лимиты и комиссии
account.default-amount=500
account.transfer-commission=0.02

# Настройки подключения к БД
db.driver=org.postgresql.Driver
db.url=jdbc:postgresql://\${DB_HOST:localhost}:\({DB_PORT:5432}/\){DB_NAME:bank}
db.username=\${DB_USER:postgres}
db.password=\${DB_PASSWORD:root}

# Конфигурация Hibernate
db.dialect=org.hibernate.dialect.PostgreSQLDialect
hibernate.hbm2ddl.auto=update
hibernate.show_sql=true
hibernate.format_sql=true
```

## Запуск

### 1. Запуск инфраструктуры (Docker)
Перед стартом приложения необходимо поднять локальную базу данных PostgreSQL. Из корневой папки проекта выполните:
```bash
docker compose up -d
```
*Контейнер запустится в фоновом режиме на порту 5432. База данных `bank` создается автоматически.*

### 2. Сборка и запуск приложения
1. Собрать проект:
```bash
mvn clean package
```
2. Запустить:
```bash
mvn exec:java -Dexec.mainClass="sorokin.java.course.Main"
```

Если `exec-maven-plugin` не настроен, можно запускать из IDE через класс `Main`.

### Управление контейнером
* Остановить базу данных: `docker compose down`
* Сбросить данные (очистить Volume): `docker compose down -v`

Если `exec-maven-plugin` не настроен, можно запускать из IDE через класс `Main`.

## Дополнительные материалы
- Подробная формулировка Hibernate-ДЗ: `docs/hibernate-homework.md`
- Подсказки: `docs/hibernate-hints.md`
