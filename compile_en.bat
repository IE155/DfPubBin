@echo off
echo Compiling DfPubBin plugin...

REM Create output directory
if not exist target mkdir target
if not exist target\classes mkdir target\classes

REM Compile Java source files
echo Compiling...
javac -cp "lib\paper-api.jar;." -d target\classes src\main\java\org\example1\dfPubBin\DfPubBinPlugin.java src\main\java\org\example1\dfPubBin\command\PBinCommand.java src\main\java\org\example1\dfPubBin\command\ReloadConfigCommand.java src\main\java\org\example1\dfPubBin\config\ConfigManager.java src\main\java\org\example1\dfPubBin\data\GarbageContainer.java src\main\java\org\example1\dfPubBin\data\GarbageManager.java src\main\java\org\example1\dfPubBin\data\GarbagePage.java src\main\java\org\example1\dfPubBin\data\GarbageType.java src\main\java\org\example1\dfPubBin\database\DatabaseManager.java src\main\java\org\example1\dfPubBin\gui\DiscardConfirmGui.java src\main\java\org\example1\dfPubBin\gui\GarbageGui.java src\main\java\org\example1\dfPubBin\gui\GuiHolder.java src\main\java\org\example1\dfPubBin\listener\GuiListener.java src\main\java\org\example1\dfPubBin\listener\InventoryListener.java src\main\java\org\example1\dfPubBin\rare\RareItemManager.java src\main\java\org\example1\dfPubBin\task\AutoCleanTask.java

if %errorlevel% == 0 (
    echo Compilation successful!
    
    REM Package to JAR
    echo Creating JAR file...
    
    REM Copy plugin.yml to classes directory
    if exist src\main\resources\plugin.yml copy src\main\resources\plugin.yml target\classes\plugin.yml > nul
    
    REM Create JAR
    jar cfm target\DfPubBin.jar src\main\resources\plugin.yml -C target\classes .
    
    if %errorlevel% == 0 (
        echo Plugin build successful! JAR file located at target\DfPubBin.jar
    ) else (
        echo JAR packaging failed!
    )
) else (
    echo Compilation failed!
)