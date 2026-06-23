@echo off
echo Starting to compile DfPubBinPlugin...

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Java not found. Please make sure Java is installed and added to PATH.
    exit /b 1
)

REM Check if dependency JAR exists
if not exist "lib\paper-api.jar" (
    echo Error: Dependency JAR lib\paper-api.jar not found
    exit /b 1
)

REM Create output directory
if not exist "target\classes" (
    mkdir "target\classes"
)

REM Find all Java source files and compile them
echo Compiling Java source files...
dir "src\main\java" /s /b *.java > temp_sources.txt
set /p SOURCES= < temp_sources.txt
for /f "usebackq delims=" %%i in (`dir "src\main\java" /s /b *.java`) do (
    set SOURCES=!SOURCES! %%i
)

javac -cp "lib\paper-api.jar" -d "target\classes" %SOURCES%

if %errorlevel% neq 0 (
    echo Compilation failed
    del temp_sources.txt
    exit /b 1
) else (
    echo Compilation successful!
)

REM Clean up
del temp_sources.txt

REM Copy resource files
echo Copying resource files...
if exist "src\main\resources" (
    xcopy /s /e /y "src\main\resources\*" "target\classes\"
)

REM Create JAR file
echo Creating JAR file...
if not exist "target" mkdir "target"
jar -cf "target\dfpubbin-1.0-SNAPSHOT.jar" -C "target\classes" .

if %errorlevel% equ 0 (
    echo JAR file created successfully: target\dfpubbin-1.0-SNAPSHOT.jar
) else (
    echo Failed to create JAR file
)

echo Build process completed.
