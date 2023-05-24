@echo off

echo Changing directory to Desktop/station_start...
cd /d "C:\Users\kelme\Desktop\station_start"

echo.
echo Running docker-compose up...
docker-compose up

echo.
echo Batch file execution completed.
pause
