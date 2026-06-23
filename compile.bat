@echo off
echo 正在编译 DfPubBin 插件...

REM 创建输出目录
if not exist target mkdir target
if not exist target\classes mkdir target\classes

REM 编译Java源文件
echo 编译中...
javac -cp "lib\paper-api.jar;." -d target\classes src\main\java\org\example1\dfPubBin\DfPubBinPlugin.java src\main\java\org\example1\dfPubBin\command\PBinCommand.java src\main\java\org\example1\dfPubBin\command\ReloadConfigCommand.java src\main\java\org\example1\dfPubBin\config\ConfigManager.java src\main\java\org\example1\dfPubBin\data\GarbageContainer.java src\main\java\org\example1\dfPubBin\data\GarbageManager.java src\main\java\org\example1\dfPubBin\data\GarbagePage.java src\main\java\org\example1\dfPubBin\data\GarbageType.java src\main\java\org\example1\dfPubBin\database\DatabaseManager.java src\main\java\org\example1\dfPubBin\gui\DiscardConfirmGui.java src\main\java\org\example1\dfPubBin\gui\GarbageGui.java src\main\java\org\example1\dfPubBin\gui\GuiHolder.java src\main\java\org\example1\dfPubBin\listener\GuiListener.java src\main\java\org\example1\dfPubBin\listener\InventoryListener.java src\main\java\org\example1\dfPubBin\rare\RareItemManager.java src\main\java\org\example1\dfPubBin\task\AutoCleanTask.java

if %errorlevel% == 0 (
    echo 编译成功!
    
    REM 打包成JAR
    echo 正在打包成JAR文件...
    
    REM 复制plugin.yml到classes目录
    if exist src\main\resources\plugin.yml copy src\main\resources\plugin.yml target\classes\plugin.yml > nul
    
    REM 创建JAR
    jar cfm target\DfPubBin.jar src\main\resources\plugin.yml -C target\classes .
    
    if %errorlevel% == 0 (
        echo 插件构建成功！JAR文件位于 target\DfPubBin.jar
    ) else (
        echo JAR打包失败！
    )
) else (
    echo 编译失败！
)