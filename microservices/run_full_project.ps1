# Quantity Measurement Full Project - Runner Script (PowerShell)
# This script starts all 4 microservices AND the Angular frontend.

$frontendDir = "C:\QuantityMeasurementApp-Frontend"
$backendRoot = "c:\QuantityMeasurementApp\microservices"

$services = @(
    @{ Name = "User Service";        Dir = "$backendRoot\user-service";        Port = 8081 },
    @{ Name = "Measurement Service"; Dir = "$backendRoot\measurement-service"; Port = 8082 },
    @{ Name = "History Service";     Dir = "$backendRoot\history-service";     Port = 8083 },
    @{ Name = "API Gateway";         Dir = "$backendRoot\api-gateway";         Port = 8090 }
)

Write-Host "--- Starting Quantity Measurement Full Stack ---" -ForegroundColor Cyan

# 1. Start Backend Services
foreach ($service in $services) {
    Write-Host "Launching $($service.Name) on port $($service.Port)..." -ForegroundColor Yellow
    Start-Process powershell -ArgumentList "-NoExit -Command `"cd $($service.Dir); mvn spring-boot:run`""
    Start-Sleep -Seconds 3
}

# 2. Start Frontend
Write-Host "`nLaunching Angular Frontend..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit -Command `"cd $frontendDir; npm start`""

Write-Host "`nBackend services and Frontend have been launched in separate windows." -ForegroundColor Green
Write-Host "Frontend: http://localhost:4200" -ForegroundColor Green
Write-Host "API Gateway: http://localhost:8090" -ForegroundColor Green
Write-Host "You can monitor the logs in the newly opened windows." -ForegroundColor Cyan
