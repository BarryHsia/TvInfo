# TvInfo 项目规范

## 项目概述

Android TV / Google TV 系统信息展示应用，使用 Kotlin + Jetpack Compose 构建。
深色科技风仪表盘 UI，通过 6 个 Tab 展示设备硬件和软件的真实信息。

## 技术栈

- **语言**: Kotlin
- **UI**: Jetpack Compose (非 Compose for TV 库)
- **最低 SDK**: 21 / **编译 SDK**: 36
- **图标库**: Material Icons Extended (`Icons.Rounded.*`)
- **构建**: Gradle 8.9, AGP 8.7.3, Kotlin 2.1.0
- **Compose BOM**: 2024.12.01

## 架构

```
com.tvinfo.app/
├── MainActivity.kt          # 入口 + 主布局(侧边栏+内容区)
├── data/
│   └── SystemInfoProvider.kt # 系统信息采集(纯 Android SDK API)
└── ui/
    ├── theme/Theme.kt        # AppColors + Typography + TvInfoTheme
    ├── components/Components.kt  # 可复用组件(BentoCard, NavItem等)
    └── screens/Screens.kt    # 6个Tab页面
```

## 关键设计决策

### TV 遥控器优先
- 所有交互基于 D-pad 焦点系统，不依赖触摸
- 使用 `FocusRequester` + `onKeyEvent` 手动管理焦点流
- 不使用 `clickable`，使用 `focusable` + key event

### Netflix 风格侧边栏
- 折叠态 72dp / 展开态 240dp，Row 布局推动内容区
- **禁止条件渲染** (`if/expanded`)：所有子元素始终 composed，通过 `alpha` 控制可见性
- **禁止垂直动画** (`expandVertically`)：只允许水平宽度 + alpha 变化
- `horizontalAlignment` 固定为 `Alignment.Start`

### 数据采集
- 所有数据来自真实 Android API，禁止硬编码模拟数据
- CPU 使用率从 `/proc/stat` 读取，必须在 IO 线程执行
- DNS 从 `DhcpInfo` 读取，fallback 到 `getprop net.dns1`
- OpenGL 版本从 `ActivityManager.deviceConfigurationInfo` 读取

## 颜色体系

| Token | 值 | 用途 |
|-------|-----|------|
| Background | `#030406` | 全局背景 |
| SurfaceCard | `White 3%` | 卡片背景 |
| CardBorder | `White 6%` | 卡片边框 |
| Accent | `#22D3EE` | 主强调色(cyan) |
| Orange | `#FB923C` | Hardware 色 |
| Purple | `#A855F7` | Display 色 |
| Emerald | `#34D399` | Network 色 |
| Blue | `#60A5FA` | Software 色 |
| Rose | `#FB7185` | Storage 色 |

## 编码规范

- Composable 函数参数超过 3 个时使用命名参数
- 动画统一使用 `tween`，不使用 `spring`（TV 上更可预测）
- 焦点相关的状态变化使用 `onFocusChanged`，不使用 `interactionSource`
- 所有 `@Suppress("DEPRECATION")` 必须注释说明原因
- 文件系统/网络读取操作必须在 IO 线程

## 构建与部署

```bash
# 构建
ANDROID_HOME=~/Android/Sdk ./gradlew assembleDebug

# 安装到 TV 模拟器
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 启动
adb shell am start -n com.tvinfo.app/.MainActivity
```
