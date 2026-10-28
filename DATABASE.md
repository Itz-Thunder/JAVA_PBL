# Database Guide

This project uses MySQL to persist donor information, blood stock, and successful blood issue requests. The Java code connects through `DataStore.java`.

## What is created automatically?

Create the database itself once:

```sql
CREATE DATABASE blood_bank;
```

When the app starts, `DataStore.initialize()` creates these tables if they do not already exist:

| Table | Purpose |
|---|---|
| `donors` | Stores each donor's name, age, gender, blood group, phone, and city. |
| `blood_stock` | Stores the current number of units available for each blood group. |
| `blood_requests` | Records successful issues, including patient, hospital, blood group, and issue time. |

Each table has an auto-incrementing `id` primary key, except `blood_stock`, which uses `blood_group` as its primary key. The tables do not currently have foreign-key relationships; the app uses the blood-group text value to update stock.

## How the data changes

- **App startup:** The app ensures all three tables exist. It inserts the initial stock only for blood groups that have no row yet, so existing stock is not reset on later starts. It also loads saved donors for the donor list.
- **Add donor:** A donor row is inserted and that donor's blood-group stock increases by one. Both changes happen in one database transaction; if either fails, neither change is kept.
- **View inventory:** The app reads the current `units` value from `blood_stock`.
- **Issue blood:** If at least one unit is available, stock is reduced by one and a row is added to `blood_requests`. Both changes are committed together. If no unit is available, stock stays unchanged and no request is recorded.

Initial stock on the first startup:

| Blood group | Units |
|---|---:|
| A+ | 10 |
| A- | 5 |
| B+ | 8 |
| B- | 4 |
| AB+ | 6 |
| AB- | 3 |
| O+ | 12 |
| O- | 5 |

## Connection settings

By default, the app connects to `localhost:3306`, database `blood_bank`, with MySQL user `root` and an empty password. Set these environment variables to use different connection details:

| Variable | Default |
|---|---|
| `MYSQL_HOST` | `localhost` |
| `MYSQL_PORT` | `3306` |
| `MYSQL_DATABASE` | `blood_bank` |
| `MYSQL_USER` | `root` |
| `MYSQL_PASSWORD` | empty |

For example, in PowerShell, set the password for the current terminal before launching the app:

```powershell
$env:MYSQL_PASSWORD = "your_mysql_password"
mvn exec:java
```

Avoid putting a real password in source files or committing it.

## Useful queries

Run these in MySQL Workbench or the MySQL command line to inspect the app's data:

```sql
USE blood_bank;

SELECT * FROM donors ORDER BY id;
SELECT * FROM blood_stock ORDER BY blood_group;
SELECT * FROM blood_requests ORDER BY issued_at DESC;
```

Deleting or editing rows manually can make the displayed data differ from what users expect. In particular, changing stock directly changes the quantity the app will show and issue.
