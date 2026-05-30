# BIR Form 1904 Database UI

This is a Spring Boot and Vaadin application for exploring and editing seeded BIR Form 1904 database tables.

## What Is Included

- Dashboard with row counts and links for each table
- CRUD screens for registrations, agents, taxpayers, IDs, and spouses
- Full database records view for reviewing current table data
- In-memory audit log for created, updated, and deleted records during the current run
- H2 in-memory database initialized from SQL seed files
- Vaadin drawer navigation and shared app layout

## Data Reset Behavior

This app uses an in-memory H2 database for demo purposes. Data is reset every time the app restarts, and seed records are loaded again from the SQL files.

## Run Locally

This project runs on Java 21. If you already have Java installed, check your version first:

```powershell
java -version
```

The output should start with `21`. If it shows another version, install Java 21.

### Install Java 21 On Windows

Install the Microsoft Build of OpenJDK 21:

```powershell
winget install Microsoft.OpenJDK.21
```

Close PowerShell, open a new PowerShell window, then check again:

```powershell
java -version
```

If Windows still uses another Java version, point the current terminal to Java 21:

```powershell
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-21.0.11.10-hotspot"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
```

If that folder name is different on your computer, list the installed Microsoft JDK folders:

```powershell
Get-ChildItem "C:\Program Files\Microsoft" -Directory | Where-Object Name -like "*jdk*"
```

Use the folder that appears in the `JAVA_HOME` command.

### Start The App

Start the app with the Maven wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

Then open:

```text
http://localhost:8080
```

Stop the app with `Ctrl+C`.

If PowerShell asks `Terminate batch job (Y/N)?`, type `Y` and press Enter.

### Common Fixes

If you see an error saying the app was compiled by a newer Java version, delete the build output and run again:

```powershell
Remove-Item .\target -Recurse -Force
.\mvnw.cmd spring-boot:run
```

If port `8080` is already in use, stop the other running app or run this app on another port:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"
```

Then open:

```text
http://localhost:8081
```

## Project Layout

```text
src/main/java/com/example
|-- Application.java
|-- base/ui
|-- bir1904
`-- schema/ui
```

The database schema is in `src/main/resources/sql/schema.sql`, and seed data is in `src/main/resources/sql/data.sql`.
