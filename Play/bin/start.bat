@echo off
cd /d "%~dp0"

set "JAVA_BIN=custom-jre\bin\java.exe"
set "JAR_FILE=play.jar"

if not exist "%JAVA_BIN%" (
    echo ERRORE: Java non trovato in %JAVA_BIN%
    pause
    exit /b
)

if not exist "%JAR_FILE%" (
    echo ERRORE: File %JAR_FILE% non trovato
    pause
    exit /b
)

echo Avvio dell'applicazione JavaFX...
"%JAVA_BIN%" -jar "%JAR_FILE%"
pause
