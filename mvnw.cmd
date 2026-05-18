@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.2 (simplified)
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set WRAPPER_DIR=%~dp0.mvn\wrapper

if not exist "%WRAPPER_DIR%\maven-wrapper.properties" (
  echo Missing %WRAPPER_DIR%\maven-wrapper.properties 1>&2
  exit /b 1
)

for /F "usebackq tokens=1,2 delims==" %%A in ("%WRAPPER_DIR%\maven-wrapper.properties") do (
  if "%%A"=="distributionUrl" set DISTRIBUTION_URL=%%B
)

if "%DISTRIBUTION_URL%"=="" (
  echo distributionUrl missing in maven-wrapper.properties 1>&2
  exit /b 1
)

for %%F in ("%DISTRIBUTION_URL%") do set DIST_NAME=%%~nF
set DIST_NAME=%DIST_NAME:-bin=%

if "%MAVEN_USER_HOME%"=="" set MAVEN_USER_HOME=%USERPROFILE%\.m2
set MAVEN_HOME=%MAVEN_USER_HOME%\wrapper\dists\%DIST_NAME%

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
  echo Maven wrapper not bootstrapped. Run 'mvn -N wrapper:wrapper' first, or use system 'mvn'. 1>&2
  exit /b 1
)

"%MAVEN_HOME%\bin\mvn.cmd" %*
