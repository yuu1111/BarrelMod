@rem Gradle startup script for Windows
@if "%DEBUG%"=="" @echo off
@rem Set local scope for variables
setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.

@rem Check for java
set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto init

echo ERROR: JAVA_HOME is not set and no 'java' command could be found.
exit /b 1

:init
@rem Download gradle-wrapper.jar if not exists
set WRAPPER_JAR=%DIRNAME%\gradle\wrapper\gradle-wrapper.jar
if exist "%WRAPPER_JAR%" goto execute

echo Downloading Gradle Wrapper...
powershell -Command "Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/gradle/gradle/v8.12.0/gradle/wrapper/gradle-wrapper.jar' -OutFile '%WRAPPER_JAR%'"

:execute
@rem Execute Gradle
"%JAVA_EXE%" -classpath "%WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*

:end
endlocal
