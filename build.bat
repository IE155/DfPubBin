@echo off
echo 开始编译 DfPubBinPlugin...

REM 检查 Java 是否已安装
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未找到 Java。请确保已安装 Java 并添加到 PATH。
    exit /b 1
)

REM 检查依赖 JAR
if not exist "lib\paper-api.jar" (
    echo 错误: 未找到依赖 JAR lib\paper-api.jar
    exit /b 1
)

REM 创建编译输出目录
if not exist "target\classes" (
    mkdir "target\classes"
)

REM 编译 Java 源代码
echo 开始编译 Java 源代码...

REM 使用 findstr 和 forfiles 命令来查找并编译所有 Java 文件
for /r "src\main\java" %%f in (*.java) do (
    echo 编译: %%f
)

REM 将所有 Java 源文件路径写入一个文件，然后使用 javac -cp "lib\paper-api.jar" -d "target\classes" @sourcefiles.txt
for /r "src\main\java" %%f in (*.java) do echo %%f >> sourcefiles.txt

javac -cp "lib\paper-api.jar" -d "target\classes" @sourcefiles.txt

REM 清理临时文件
del sourcefiles.txt

if %errorlevel% neq 0 (
    echo 编译失败
    exit /b 1
) else (
    echo 编译成功!
)

REM 复制资源文件
echo 复制资源文件...
if exist "src\main\resources" (
    xcopy /s /e /y "src\main\resources\*" "target\classes\"
)

REM 创建 JAR 文件
echo 创建 JAR 文件...
if not exist "target" mkdir "target"
jar -cf "target\dfpubbin-1.0-SNAPSHOT.jar" -C "target\classes" .

if %errorlevel% equ 0 (
    echo JAR 文件创建成功: target\dfpubbin-1.0-SNAPSHOT.jar
) else (
    echo 创建 JAR 文件失败
)

echo 构建过程完成。
