@echo off
cd /d "%~dp0"
set "JAVA_BIN=custom-jre-win\bin\java.exe"
set "JAR_FILE=play.jar"

if not exist "%JAVA_BIN%" (
    echo ERRORE: file java.exe non trovato in %JAVA_BIN%
    pause
    exit /b
)

if not exist "%JAR_FILE%" (
    echo ERRORE: file %JAR_FILE% non trovato
    pause
    exit /b
)

echo Avvio dell'applicazione JavaFX...
"%JAVA_BIN%" -jar "%JAR_FILE%"
pause
