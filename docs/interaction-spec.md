# Tv Info — 交互设计规范文档

## 1. 产品概述

Tv Info 是一款运行在 Android TV / Google TV 上的系统信息展示应用。
用户通过遥控器 D-pad 在 6 个信息 Tab 之间切换，查看设备的硬件和软件详情。

**目标设备**: Android TV、Google TV 机顶盒、智能电视
**输入方式**: TV 遥控器 D-pad（方向键 + 确认键 + 返回键）

---

## 2. 页面结构

```
┌──────────┬─────────────────────────────────┐
│ Sidebar  │          Content Area           │
│ (动态宽)  │  ┌───────────────────────────┐  │
│          │  │  Header (地区 + 时钟)       │  │
│ Logo     │  ├───────────────────────────┤  │
│          │  │                           │  │
│ Nav[0]   │  │  Tab Content              │  │
│ Nav[1]   │  │  (Bento Grid 卡片)         │  │
│ Nav[2]   │  │                           │  │
│ Nav[3]   │  │                           │  │
│ Nav[4]   │  ├───────────────────────────┤  │
│ Nav[5]   │  │  Footer (状态指示)         │  │
│          │  └───────────────────────────┘  │
│ Health   │                                 │
└──────────┴─────────────────────────────────┘
```

**布局方式**: `Row` — Sidebar 固定宽度 + Content `weight(1f)`
**关系**: Sidebar 宽度变化时推动 Content，Content 紧贴 Sidebar 右边界

---

## 3. 侧边栏 (Navigation Rail)

### 3.1 状态定义

| 状态 | 宽度 | 显示内容 | 背景 |
|------|------|---------|------|
| `collapsed` | 72dp | 图标 + 透明文字(alpha=0) | `#0A0B0E` |
| `expanded` | 240dp | 图标 + 可见文字(alpha=1) | `#0A0B0E` |

### 3.2 状态转换

```
collapsed ──[Nav item 获得焦点]──→ expanded
expanded  ──[D-pad → / Enter]───→ collapsed (焦点移至 Content)
expanded  ──[Content 获得焦点]──→ collapsed
```

### 3.3 动画参数

| 属性 | 起始值 | 目标值 | 缓动函数 | 时长 |
|------|--------|--------|---------|------|
| `width` | 72dp | 240dp | `FastOutSlowIn` | 280ms |
| `textAlpha` | 0f | 1f | `Linear` | 展开 250ms / 收起 120ms |
| `contentAlpha` | 1f | 0.6f | `Linear` | 280ms |

### 3.4 布局约束（防止动画跳动）

- 所有子元素始终存在于 Composition 中，不使用 `if (expanded)` 条件渲染
- 文字可见性仅通过 `Modifier.alpha(textAlpha)` 控制
- `horizontalAlignment` 固定为 `Alignment.Start`，不随状态切换
- Active indicator 始终占位（不可见时用 `Color.Transparent`）
- 禁止使用 `expandVertically` / `shrinkVertically`
- 文字设置 `maxLines = 1, overflow = TextOverflow.Clip` 防止换行

### 3.5 Nav Item 视觉状态

| 状态 | 背景 alpha | 边框 | 图标色 | 文字色 |
|------|-----------|------|--------|--------|
| `default` | 0% | 无 | White 40% | TextTertiary |
| `active` | White 8% | 1dp White 6% | item.color | White |
| `focused` | White 15% | 1.5dp Accent 40% | item.color | White |
| `active+focused` | White 15% | 1.5dp Accent 40% | item.color | White |

---

## 4. 焦点系统 (D-pad Navigation)

### 4.1 焦点流状态机

```
                        ┌─────────────┐
                        │  App Launch  │
                        │ focus→Nav[0] │
                        │ sidebar=展开  │
                        └──────┬──────┘
                               │
                ┌──────────────┼──────────────┐
                ▼              ▼              ▼
           D-pad ↑         D-pad ↓        D-pad → / Enter
           Nav[i-1]        Nav[i+1]       focus→Content
           auto-select     auto-select    sidebar=收起
                │              │              │
                └──────┬───────┘              │
                       │                      ▼
                  sidebar=展开          ┌─────────────┐
                                       │   Content    │
                                       │  D-pad ↑↓←→  │
                                       │  卡片间导航    │
                                       └──────┬──────┘
                                              │
                                         D-pad ←
                                       (最左侧时)
                                              │
                                              ▼
                                       focus→Nav[active]
                                       sidebar=展开
```

### 4.2 焦点规则

| 当前位置 | 按键 | 行为 |
|---------|------|------|
| Nav[i] | ↑ | 焦点→Nav[i-1]（i=0 时无响应） |
| Nav[i] | ↓ | 焦点→Nav[i+1]（i=last 时无响应） |
| Nav[i] | → | 焦点→Content 首个可聚焦卡片，sidebar 收起 |
| Nav[i] | Enter | 同 → |
| Content 卡片 | ← | 焦点→Nav[activeTab]，sidebar 展开 |
| Content 卡片 | ↑↓→ | Compose 默认焦点遍历 |

### 4.3 焦点视觉反馈

- **Nav Item**: 1.5dp cyan 边框 + 背景变亮
- **BentoCard**: 2dp cyan 边框 + 阴影发光(12dp elevation) + 背景变亮至 `#1A1D23`

---

## 5. 内容区 (Content Area)

### 5.1 Tab 切换动画

| 属性 | 进入 | 退出 |
|------|------|------|
| opacity | fadeIn 200ms | fadeOut 150ms |
| translateX | slideIn +10% 250ms | slideOut -10% 200ms |

### 5.2 Tab 页面列表

| Tab ID | 标签 | 图标 | 主色 | 展示信息 |
|--------|------|------|------|---------|
| `overview` | Overview | FlashOn | Cyan | 设备概况、CPU、内存、网络状态 |
| `hardware` | Hardware | Memory | Orange | SoC架构、设备信息、RAM、ABI |
| `display` | Display | Monitor | Purple | 分辨率、刷新率、色深、方向 |
| `network` | Network | Wifi | Emerald | 接口类型、IP/DNS/网关、MAC |
| `software` | Software | Shield | Blue | Build信息、API、安全补丁、内核 |
| `storage` | Storage | Storage | Rose | 存储用量、外部存储、文件系统 |

### 5.3 BentoCard 组件

布局: 圆角 24dp，内边距 20dp

| 属性 | 默认 | 聚焦 | 时长 |
|------|------|------|------|
| border | 1dp CardBorder | 2dp Accent 60% | 200ms |
| background | SurfaceCard | CardHover `#1A1D23` | 200ms |
| elevation | 0dp | 12dp (Accent 30% shadow) | 200ms |

---

## 6. Header / Footer

### Header
- 左侧: 绿色圆点 + "REMOTE NODE ACCESS" + 地区信息
- 右侧: 时钟图标 + 时间(36sp) + 日期
- 时钟每秒更新 (`LaunchedEffect` + `delay(1000)`)

### Footer
- 高度 44dp，顶部 1px 分隔线 (White 5%)
- 左侧: "ENGINE ACTIVE" + "SECURED NODE" 状态灯
- 右侧: "AUTHORIZED SYSTEM CONSOLE // Tv Info"

---

## 7. 视觉 Token

### 颜色

| Token | 值 | 用途 |
|-------|-----|------|
| Background | `#030406` | 全局背景 |
| SurfaceCard | `White 3%` | 卡片背景 |
| CardBorder | `White 6%` | 卡片边框 |
| CardHover | `#1A1D23` | 聚焦背景 |
| Accent | `#22D3EE` | 主强调色 |
| TextPrimary | `#FFFFFF` | 主文字 |
| TextSecondary | `#9CA3AF` | 次文字 |
| TextTertiary | `#6B7280` | 标签文字 |
| TextDim | `#4B5563` | 最弱文字 |

### 间距

| Token | 值 |
|-------|-----|
| 卡片间距 | 12dp |
| 卡片内边距 | 20dp |
| 卡片圆角 | 24dp |
| Nav item 圆角 | 12dp |
| 内容区边距 | 16dp(左) / 32dp(右) |

### 字体

| 用途 | 大小 | 字重 |
|------|------|------|
| 设备名 | 32sp | Bold |
| 数值 | 28sp | Bold |
| 时钟 | 36sp | Black |
| 卡片标题 | 22sp | Bold |
| Nav 标签 | 15sp | Medium/Bold |
| Stat Label | 10sp | Medium, 大写, 2sp 字距 |
| 信息行 | 12sp | Normal/Medium |

---

## 8. 数据源

所有数据来自真实 Android API，禁止硬编码。

| 数据 | API |
|------|-----|
| OS 版本 | `Build.VERSION.RELEASE` |
| CPU 核心数 | `Runtime.availableProcessors()` |
| 内存 | `ActivityManager.MemoryInfo` |
| 分辨率 | `WindowManager.defaultDisplay.getRealMetrics()` |
| 网络类型 | `NetworkCapabilities.hasTransport()` |
| IP/DNS/网关 | `NetworkInterface` / `WifiManager.dhcpInfo` |
| 存储 | `StatFs(Environment.getDataDirectory())` |
| CPU 使用率 | `/proc/stat`（必须 IO 线程） |
| OpenGL | `ActivityManager.deviceConfigurationInfo.glEsVersion` |
| SELinux | `/sys/fs/selinux/enforce` |

---

## 9. 边界条件

| 场景 | 处理方式 |
|------|---------|
| 无网络连接 | 显示 "DISCONNECTED" + 红色 OFFLINE 标签 |
| 无外部存储 | 显示 "No USB Drive Detected" |
| API 读取失败 | 显示 "N/A" 或 "Unknown" |
| 快速连续切换 Tab | AnimatedContent 自动处理中断 |
| 侧边栏动画中切换焦点 | 动画被新目标值覆盖，自然过渡 |
