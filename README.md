# Midterm OOP Inventory Management System

A Java 8 console application for managing Clothing, Electronics, and
Entertainment inventory items. Inventory is kept in memory and starts empty on
every run.

## Compile and run

From the repository root:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -d out src\*.java
java -cp out Main
```

If your terminal is already inside the `out` directory, run:

```powershell
java -cp . Main
```
