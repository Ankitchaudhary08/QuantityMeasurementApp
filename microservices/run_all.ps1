# Quantity Measurement Microservices - Runner Script (PowerShell)
# This script starts all 4 services in new terminal windows.

$services = @(
    @{ Name = "User Service";        Dir = "user-service";        Port = 8081 },
    @{ Name = "Measurement Service"; Dir = "measurement-service"; Port = 8082 },
    @{ Name = "History Service";     Dir = "history-service";     Port = 8083 },
    @{ Name = "API Gateway";         Dir = "api-gateway";         Port = 8090 }
)

Write-Host "--- Starting Quantity Measurement Microservices ---" -ForegroundColor Cyan

foreach ($service in $services) {
    Write-Host "Launching $($service.Name) on port $($service.Port)..." -ForegroundColor Yellow
    
    # Start each service in a separate PowerShell window
    Start-Process powershell -ArgumentList "-NoExit -Command `"cd $($service.Dir); mvn spring-boot:run`""
    
    # Wait a few seconds for resources to stabilize
    Start-Sleep -Seconds 5
}

Write-Host "`nAll services have been launched in separate windows." -ForegroundColor Green
Write-Host "API Gateway is available at: http://localhost:8090" -ForegroundColor Green
Write-Host "You can monitor the logs in the newly opened windows." -ForegroundColor Cyan
