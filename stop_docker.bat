@echo off

echo Changing directory to Desktop/station_start...
cd /d "station_start"

echo.
echo Running docker-compose down...
docker-compose down

echo.
echo Batch file execution completed.
pause
