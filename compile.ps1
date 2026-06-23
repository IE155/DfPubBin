# PowerShell script to compile DfPubBinPlugin
Write-Host "Starting compilation of DfPubBinPlugin..."

# Check if Java is available
try {
    $javaVersion = java -version 2>&1
    Write-Host "Java is available: $javaVersion"
} catch {
    Write-Host "Error: Java not found. Please make sure Java is installed and added to PATH." -ForegroundColor Red
    exit 1
}

# Check dependency JAR
$dependencyJar = "lib\paper-api.jar"
if (!(Test-Path $dependencyJar)) {
    Write-Host "Error: Dependency JAR $dependencyJar not found" -ForegroundColor Red
    exit 1
}

# Create output directory
$outputDir = "target\classes"
if (!(Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
}

# Find all Java source files
Write-Host "Finding Java source files..."
$sourceFiles = Get-ChildItem -Path "src\main\java" -Recurse -Filter "*.java"

if ($sourceFiles.Count -eq 0) {
    Write-Host "Error: No Java source files found" -ForegroundColor Red
    exit 1
}

# Build the compilation command
$sourcePaths = $sourceFiles | ForEach-Object { $_.FullName }
$sourcePathString = $sourcePaths -join " "

Write-Host "Compiling Java source files..."
$compileCmd = "javac"
$compileArgs = @("-cp", $dependencyJar, "-d", $outputDir) + $sourcePaths
$result = & $compileCmd $compileArgs 2>&1
$exitCode = $LASTEXITCODE

if ($exitCode -ne 0) {
    Write-Host "Compilation failed:" -ForegroundColor Red
    Write-Host $result -ForegroundColor Red
    exit 1
} else {
    Write-Host "Compilation successful!" -ForegroundColor Green
}

# Copy resource files
$resourcesDir = "src\main\resources"
if (Test-Path $resourcesDir) {
    Write-Host "Copying resource files..."
    Copy-Item -Path "$resourcesDir\*" -Destination $outputDir -Recurse -Force
}

# Create JAR file
$jarFile = "target\dfpubbin-1.0-SNAPSHOT.jar"
Write-Host "Creating JAR file: $jarFile"

# Ensure target directory exists
if (!(Test-Path "target")) {
    New-Item -ItemType Directory -Path "target" -Force | Out-Null
}

$jarCommand = "jar -cf \"$jarFile\" -C \"$outputDir\" ."
$result = cmd /c $jarCommand 2>&1
$exitCode = $LASTEXITCODE

if ($exitCode -eq 0) {
    Write-Host "JAR file created successfully: $jarFile" -ForegroundColor Green
} else {
    Write-Host "Failed to create JAR file" -ForegroundColor Red
    Write-Host $result -ForegroundColor Red
}

Write-Host "Build process completed." -ForegroundColor Cyan
