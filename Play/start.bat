@echo off
echo Avvio dell'applicazione JavaFX...

set JAVA_PATH=custom-jre-win\bin\java.exe

if not exist "%JAVA_PATH%" (
    echo ERRORE: file java.exe non trovato in %JAVA_PATH%
    pause
    exit /b 1
)

"%JAVA_PATH%" -jar play.jar
pause
