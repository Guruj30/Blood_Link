# BloodLink Quick Launcher for VS Code & Terminal
$env:JAVA_HOME = "C:\Users\SHARMIJEEVA\.local\bin\jdk"
$env:Path = "C:\Users\SHARMIJEEVA\.local\bin\jdk\bin;C:\Users\SHARMIJEEVA\.local\bin\nodejs;C:\Users\SHARMIJEEVA\.local\bin\git\cmd;$env:Path"

Write-Host "=============================================" -ForegroundColor Cyan
Write-Host "  BloodLink - Healthcare Platform Launcher   " -ForegroundColor Green
Write-Host "=============================================" -ForegroundColor Cyan

Write-Host "`n[1/2] Starting Java Spring Boot Backend (Port 8080)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "`$env:JAVA_HOME='C:\Users\SHARMIJEEVA\.local\bin\jdk'; `$env:Path='C:\Users\SHARMIJEEVA\.local\bin\jdk\bin;' + `$env:Path; cd backend; .\mvnw.cmd spring-boot:run"

Write-Host "[2/2] Starting React + Vite Frontend (Port 5173)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "`$env:Path='C:\Users\SHARMIJEEVA\.local\bin\nodejs;' + `$env:Path; npm run dev -- --host"

Write-Host "`nBoth services are starting!" -ForegroundColor Green
Write-Host "Frontend Local:   http://localhost:5173/" -ForegroundColor White
Write-Host "Backend API:      http://localhost:8080/api/health" -ForegroundColor White
