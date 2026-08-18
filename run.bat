@echo off
setlocal
echo ===================================================
echo   TaskFlow Enterprise Backend - Run Script
echo ===================================================

set "JAVA_HOME=C:\Users\Kavi\.p2\pool\plugins\org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_21.0.10.v20260205-0638\jre"
set "PATH=%JAVA_HOME%\bin;%PATH%"

if not exist "target\taskflow-backend-1.0.0-SNAPSHOT.jar" (
    echo Target JAR not found! Building first...
    call build.bat
)

echo Starting TaskFlow Backend on http://localhost:8080 (Profile: local)...
echo Swagger UI will be available at: http://localhost:8080/swagger-ui.html
echo Press Ctrl+C to stop the server.
echo.

"%JAVA_HOME%\bin\java.exe" -jar "target\taskflow-backend-1.0.0-SNAPSHOT.jar" --spring.profiles.active=local

endlocal
