![](img/t.png)
![Java](https://img.shields.io/badge/Java-21-orange)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21%2B-green)
![Paper](https://img.shields.io/badge/Paper-1.21%2B-blue)
![Version](https://img.shields.io/badge/版本-1.4-brightgreen)
![API](https://img.shields.io/badge/API-Paper_API_1.21-lightgrey)

DfPubBin 是一款 Minecraft Paper/Spigot 服务器垃圾桶插件，提供公共/私人双模式垃圾桶、稀有物品检测、自动清理等完整功能。

---

## 功能一览

### 核心功能

| 功能 | 说明 |
|------|------|
| **公共垃圾桶** | 全服共享，所有玩家可丢弃和取回物品 |
| **私人垃圾桶** | 每位玩家独立，仅自己可见和操作 |
| **分页系统** | 每页 45 格，支持无限翻页 |
| **物品追踪** | 显示丢弃者名称和丢弃时间 |
| **附魔保留** | 取回物品时完整保留所有附魔 |

### 自动化功能

| 功能 | 说明 |
|------|------|
| **自动清理** | 定时清理公共垃圾桶，白名单物品受保护不被清除 |
| **掉落物回收** | 定时将地面掉落物自动收入公共垃圾桶 |
| **禁用原版消失** | 可阻止掉落物 5 分钟自然消失 |
| **周期性提醒** | 定时广播提醒玩家查看公共垃圾桶 |

### 通知与检测

| 功能 | 说明 |
|------|------|
| **稀有物品检测** | 可配置稀有物品列表，出现时全服广播（可点击跳转） |
| **新物品提示** | 垃圾桶出现新类型物品时通知全服 |

### 交互与安全

| 功能 | 说明 |
|------|------|
| **丢弃确认流程** | 通过确认界面丢弃物品，防止误操作 |
| **物品取回冷却** | 可配置冷却时间（秒），防止频繁取回 |
| **热加载配置** | `/pbin2load` 无需重启即可重载所有配置 |
| **一键禁用** | `enable-pbin-command: false` 一键关闭垃圾桶功能 |

---

## 使用方法

### 命令

| 命令 | 说明 | 权限 |
|------|------|------|
| `/pbin pub` | 打开公共垃圾桶 | 无 |
| `/pbin priv` | 打开私人垃圾桶 | 无 |
| `/pbin2load` | 热加载配置文件 | `dfpubbin.reload` |

### 界面操作

#### 垃圾桶界面

| 操作 | 说明 |
|------|------|
| 左键 / 右键点击物品 | 从垃圾桶取出物品 |
| 点击 🔴 红色玻璃板 | 上一页 |
| 点击 🟢 绿色玻璃板 | 下一页 |
| 点击 🟡 黄色玻璃板 | 进入丢弃确认界面 |
| 点击 🔵 蓝色玻璃板 | 切换到私人垃圾桶（仅公共界面可用） |

#### 丢弃确认界面

1. 将要丢弃的物品放入前 4 行格子（0-35 槽位）
2. 点击 🟢 绿色玻璃板 → 确认丢弃
3. 点击 🔴 红色玻璃板 → 取消操作
4. 点击 🟣 紫色玻璃板 → 前往垃圾桶查看

---

## 安装

1. 将编译好的 JAR 文件放入服务器 `plugins` 目录
2. 重启服务器，插件自动生成 `config.yml` 和 `garbage_data.yml`
3. 根据需要修改 `config.yml`
4. 使用 `/pbin2load` 即时重载配置，无需重启


## 系统要求

- **Java**: 21+
- **服务端**: Paper 1.21+
- **Minecraft**: 1.21+

---

## 权限

| 权限节点 | 说明 | 默认 |
|----------|------|------|
| `dfpubbin.reload` | 允许执行 `/pbin2load` 重载配置 | OP |

---

## 配置说明

```yaml
# 一键关闭垃圾桶功能
enable-pbin-command: true

# 物品取回冷却（秒）
item-pickup-cooldown: 2

# 自动清理
auto-clean:
  enabled: true
  interval-ticks: 6000          # 6000 ticks = 5 分钟
  whitelist-items:              # 白名单物品不会被自动清理
    - "DIAMOND"
    - "NETHERITE_INGOT"
    # ...

# 掉落物回收
dropped-item-cleanup:
  enabled: true
  interval-minutes: 5           # 每 5 分钟回收一次

# 禁用原版掉落物 5 分钟消失
disable-item-despawn:
  enabled: true

# 稀有物品列表（出现时全服广播）
rare-items:
  items:
    - "DIAMOND_BLOCK"
    - "ELYTRA"
    # ...

# 新物品提示
new-item-notification:
  enabled: false

# 周期性提醒
periodic-reminder:
  enabled: false
  interval-minutes: 120
  message: "§2[DfPubBin]§e公共垃圾桶出现新物品"

# 自定义消息（支持 § 颜色代码）
messages:
  rare-item-broadcast: "§2§l[DfPubBin]§e公共垃圾桶出现§l§d稀有物品 "
  rare-item-click-text: "§a(点击查看!)"
  command-disabled: "§c该指令已被禁用"
  item-pickup-cooldown: "§c操作频繁，请%seconds%秒后重试"
  trash-full: "§c垃圾桶已满，无法丢弃更多物品！"
  discard-success: "§a物品已成功丢弃到垃圾桶"
  # ...
```

> 所有配置项均支持 `/pbin2load` 热加载。

---

---

## 开发者

- ie155
- 18ay
