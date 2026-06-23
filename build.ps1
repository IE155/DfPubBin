# PowerShell 脚本用于编译 DfPubBinPlugin

# 检查 Java 是否可用
$javaCheck = java -version 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "错误: 未找到 Java。请确保已安装 Java 并添加到 PATH。" -ForegroundColor Red
    exit 1
}

# 检查源代码目录
$srcDir = "src\main\java"
if (!(Test-Path $srcDir)) {
    Write-Host "错误: 未找到源代码目录 $srcDir" -ForegroundColor Red
    exit 1
}

# 创建编译输出目录
$outputDir = "target\classes"
if (!(Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
}

# 检查依赖 JAR
$dependencyJar = "lib\paper-api.jar"
if (!(Test-Path $dependencyJar)) {
    Write-Host "错误: 未找到依赖 JAR $dependencyJar" -ForegroundColor Red
    exit 1
}

# 获取所有 Java 源文件
Write-Host "开始编译 Java 源代码..." -ForegroundColor Green
$javaFiles = Get-ChildItem -Path $srcDir -Recurse -Filter "*.java"

if ($javaFiles.Count -eq 0) {
    Write-Host "错误: 未找到任何 .java 文件" -ForegroundColor Red
    exit 1
}

# 构建源文件列表
$sourceFileList = @()
foreach ($file in $javaFiles) {
    $sourceFileList += $file.FullName
}

# 使用 javac 编译所有源文件
$sourceFileString = $sourceFileList -join " "
$compileCommand = "javac -cp \"$dependencyJar\" -d \"$outputDir\" $sourceFileString"

# 执行编译命令
$compileResult = cmd /c $compileCommand 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "编译失败:" -ForegroundColor Red
    Write-Host $compileResult -ForegroundColor Red
    exit 1
} else {
    Write-Host "编译成功!" -ForegroundColor Green
}

# 复制资源文件
$resourcesDir = "src\main\resources"
if (Test-Path $resourcesDir) {
    Write-Host "复制资源文件..." -ForegroundColor Green
    Copy-Item -Path "$resourcesDir\*" -Destination $outputDir -Recurse -Force
}

# 确保 target 目录存在
if (!(Test-Path "target")) {
    New-Item -ItemType Directory -Path "target" -Force | Out-Null
}

# 创建 JAR 文件
$jarFile = "target\dfpubbin-1.0-SNAPSHOT.jar"
Write-Host "创建 JAR 文件: $jarFile" -ForegroundColor Green

# 使用 jar 命令创建 JAR
$jarCommand = "jar -cf \"$jarFile\" -C \"$outputDir\" ."
$jarResult = cmd /c $jarCommand 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "JAR 文件创建成功: $jarFile" -ForegroundColor Green
} else {
    Write-Host "创建 JAR 文件失败" -ForegroundColor Red
    Write-Host $jarResult -ForegroundColor Red
}

Write-Host "构建过程完成。" -ForegroundColor Cyan