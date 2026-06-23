# DfPubBin

DfPubBin is a Minecraft Paper/Spigot server plugin that provides public and private trash bin functionality, allowing players to safely discard and retrieve items.

DfPubBin是一款Minecraft Paper/Spigot服务器插件，提供公共和私人垃圾桶功能，允许玩家安全地丢弃和取回物品。

---

## Basic Information | 基本信息

- **Plugin Name |   插件名称**: DfPubBin
- **Minecraft Version Support | Minecraft版本支持**: 1.21+
- **API Version | API版本**: 1.21

---

## Feature Highlights | 功能特色

- **Public Trash Bin | 公共垃圾桶**: A shared trash bin for all players, allowing viewing and retrieval of items discarded by any player
- **Private Trash Bin | 私人垃圾桶**: An individual trash bin for each player, only allowing viewing and retrieval of items discarded by themselves
- **Auto Clean | 自动清理**: Configurable auto-cleaning functionality with whitelist item protection
- **Rare Item Detection | 稀有物品检测**: Detects and broadcasts the appearance of rare items, alerting other players
- **Pagination System | 分页系统**: Supports multi-page trash bin interface with 45 item slots per page
- **Item Tracking | 物品追踪**: Displays the discarder's name and time of discarding
- **Item Retrieval Cooldown | 物品取回冷却**: Prevents players from frequently retrieving items, protecting server performance
- **Enchantment Preservation | 附魔保留**: Preserves all enchantments when retrieving items

---

## Commands | 命令

| Command / 命令 | Description / 描述 | 
|------|------|
| `/pbin pub` | Open public trash bin interface / 打开公共垃圾桶界面 |
| `/pbin priv` | Open private trash bin interface / 打开私人垃圾桶界面 |
| `/pbin2load` | Reload configuration file (OP only) / 重新加载配置文件 (仅OP) |

---

## Permissions | 权限

- `dfpubbin.reload` - Allows executing `/pbin2load` command to reload configuration / 允许执行 `/pbin2load` 命令重新加载配置

---

## Usage | 使用方法

### English Version:
1. **Open Trash Bin**:
   - Type `/pbin pub` to open the public trash bin
   - Type `/pbin priv` to open the private trash bin

2. **Operations in Trash Bin Interface**:
   - **Left-click or right-click on item** - Retrieve item from trash bin
   - **Previous/Next page** - Browse trash bin content by pages
   - **Discard items** - Click yellow glass pane to enter discard confirmation interface
   - **Switch trash bin** - In public trash bin interface, click blue glass pane to switch to private trash bin

3. **Discard Confirmation Interface**:
   - Place items to be discarded in the first 4 rows of slots
   - Click green glass pane to confirm discard
   - Click red glass pane to cancel discard operation
   - Click the purple glass panel to go back and check the trash can

### 中文版本：
1. **打开垃圾桶**:
   - 输入 `/pbin pub` 打开公共垃圾桶
   - 输入 `/pbin priv` 打开私人垃圾桶

2. **在垃圾桶界面中操作**:
   - **左键或右键点击物品** - 从垃圾桶中取出物品
   - **上一页/下一页** - 分页浏览垃圾桶内容
   - **丢弃物品** - 点击黄色玻璃板进入丢弃确认界面
   - **切换垃圾桶** - 在公共垃圾桶界面点击蓝色玻璃板切换到私人垃圾桶

3. **丢弃确认界面**:
   - 将要丢弃的物品放入前4行格子中
   - 点击绿色玻璃板确认丢弃
   - 点击红色玻璃板取消丢弃操作
   - 点击紫色玻璃板回到垃圾桶查看

---

## Installation | 安装

### English Version:
1. Download the compiled JAR file from the releases
2. Place the JAR file in your server's `plugins` folder
3. Restart your server
4. The plugin will generate configuration files automatically
5. Configure settings in `config.yml` as needed
6. Use `/pbin2load` command to reload configuration without restarting the server

### 中文版本：
1. 从发布版本下载编译好的JAR文件
2. 将JAR文件放入服务器的`plugins`文件夹
3. 重启服务器
4. 插件会自动生成配置文件
5. 根据需要在`config.yml`中配置设置
6. 使用`/pbin2load`命令无需重启服务器即可重载配置

---



## System Requirements | 系统要求

### English Version:
- **Java Version**: Java 21 or higher
- **Server Software**: Paper-1.21 or higher
- **Minecraft Version**: 1.21 or higher
- **Storage**: At least 50MB free space for plugin files and database

### 中文版本：
- **Java版本**: Java 21或更高版本
- **服务器软件**: Paper-1.21或更高版本
- **Minecraft版本**: 1.21或更高版本
- **存储**: 至少1MB空闲空间用于插件文件和数据库

---

## Known Issues | 已知问题

### English Version:
- Items in the discard interface cannot be dragged or removed with shift+left-click after placement, only by clicking the red glass pane to cancel the discard
- Item dragging operations in the interface may have some limitations


### 中文版本：
- 丢弃界面的物品放入后无法拖动或者按shift+鼠标左键取出，只能按红色玻璃板取消丢弃
- 界面中的物品拖拽操作可能存在一些限制


---


## Changelog | 更新日志

### Version 1.4-SNAPSHOT | 版本 1.4-SNAPSHOT
- Added item retrieval cooldown functionality / 添加物品取回冷却功能
- Optimized auto-cleaning mechanism / 优化自动清理机制
- Improved rare item detection and broadcasting functionality / 改进稀有物品检测和广播功能
- Fixed some GUI interface issues / 修复GUI界面的一些问题
- The discard interface has added a 'View in Trash' button/丢弃界面新增回到垃圾桶查看按钮

---
### 开发者
- ie155
- 18ay


---

## Support | 支持

For support, please open an issue in the GitHub repository.

如需支持，请在GitHub仓库中开启问题。






