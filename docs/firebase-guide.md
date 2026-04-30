# Firebase 集成说明

## 当前集成状态

### 已集成

| 服务 | 版本 | 用途 |
|------|------|------|
| Firebase Analytics | BOM 33.7.0 | 用户行为追踪、Tab 切换事件 |
| Firebase Crashlytics | BOM 33.7.0 | 崩溃上报、ANR 检测 |

### 配置文件

- `app/google-services.json` — Firebase 项目凭证（**已 gitignore，不入仓库**）
- 包名：`com.tvinfo.app`
- Firebase 项目：TvInfo (Spark 免费方案)

### Gradle 依赖

```kotlin
// 根 build.gradle.kts
id("com.google.gms.google-services") version "4.4.2"
id("com.google.firebase.crashlytics") version "3.0.2"

// app/build.gradle.kts
implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
implementation("com.google.firebase:firebase-analytics-ktx")
implementation("com.google.firebase:firebase-crashlytics-ktx")
```

---

## 已埋点事件

| 事件名 | 参数 | 触发时机 |
|--------|------|---------|
| `tab_switch` | `tab_name`: String | 用户切换 Tab 时 |

### 自动采集事件（Firebase 默认）

| 事件 | 说明 |
|------|------|
| `first_open` | 首次打开应用 |
| `session_start` | 会话开始 |
| `screen_view` | 页面浏览 |
| `app_update` | 应用更新后首次打开 |
| `app_remove` | 应用卸载 |

---

## 调试方法

### 开启 DebugView 实时模式

```bash
# 开启
adb shell setprop debug.firebase.analytics.app com.tvinfo.app

# 查看：Firebase Console → Analytics → DebugView

# 关闭
adb shell setprop debug.firebase.analytics.app .none.
```

### 验证 Crashlytics

```bash
# 查看 logcat 确认初始化
adb logcat | grep -i "firebase\|crashlytics"

# 应看到：
# FirebaseInitProvider: FirebaseApp initialization successful
# FirebaseCrashlytics: ...session...
```

---

## 新开发者接入步骤

1. 前往 [Firebase Console](https://console.firebase.google.com) → TvInfo 项目
2. 项目设置 → 常规 → 你的应用 → 下载 `google-services.json`
3. 放到 `app/google-services.json`
4. 构建运行即可

---

## 后续功能规划

### Phase 1：基础监控完善

| 功能 | Firebase 服务 | 优先级 | 说明 |
|------|-------------|--------|------|
| 页面停留时长 | Analytics | P0 | 记录每个 Tab 的浏览时长，分析用户最关注的信息 |
| 自定义用户属性 | Analytics | P0 | 设备型号、Android 版本、屏幕分辨率作为用户属性上报 |
| 性能监控 | Performance Monitoring | P1 | 应用启动时间、数据采集耗时 |
| 非致命异常上报 | Crashlytics | P1 | 捕获数据读取失败（网络/存储/CPU）并上报 |

### Phase 2：远程配置与 A/B 测试

| 功能 | Firebase 服务 | 优先级 | 说明 |
|------|-------------|--------|------|
| 远程配置 UI | Remote Config | P1 | 远程控制 Tab 显示/隐藏、卡片排序 |
| 主题切换 | Remote Config | P2 | 远程下发配色方案（深色/浅色/自定义） |
| A/B 测试侧边栏 | A/B Testing | P2 | 测试不同侧边栏宽度/动画时长对用户停留的影响 |
| 功能开关 | Remote Config | P2 | 新功能灰度发布控制 |

### Phase 3：用户互动与推送

| 功能 | Firebase 服务 | 优先级 | 说明 |
|------|-------------|--------|------|
| 系统异常推送 | Cloud Messaging (FCM) | P2 | 检测到存储不足/网络异常时推送通知 |
| 应用内消息 | In-App Messaging | P3 | 新功能引导、更新提示 |
| 用户反馈 | Firestore | P3 | 用户提交设备问题反馈，存储到 Firestore |

### Phase 4：数据分析深化

| 功能 | Firebase 服务 | 优先级 | 说明 |
|------|-------------|--------|------|
| 漏斗分析 | Analytics | P2 | 分析用户从 Overview → 其他 Tab 的转化路径 |
| 受众细分 | Analytics | P2 | 按设备型号/地区/Android 版本细分用户群 |
| BigQuery 导出 | BigQuery Export | P3 | 原始事件数据导出到 BigQuery 做深度分析 |
| 预测用户流失 | Predictions | P3 | 基于使用频率预测用户是否会卸载 |

---

## 埋点规划（Phase 1 详细）

### 新增事件

| 事件名 | 参数 | 触发时机 |
|--------|------|---------|
| `tab_view_duration` | `tab_name`, `duration_ms` | 离开某个 Tab 时记录停留时长 |
| `sidebar_expand` | `trigger`: focus/dpad | 侧边栏展开时 |
| `sidebar_collapse` | `trigger`: right/enter | 侧边栏收起时 |
| `card_focus` | `card_name`, `tab_name` | BentoCard 获得焦点时 |
| `data_load_error` | `source`: cpu/network/storage | 数据采集失败时 |
| `app_launch` | `boot_time_ms` | 应用启动完成时记录耗时 |

### 新增用户属性

| 属性名 | 值 | 来源 |
|--------|-----|------|
| `device_model` | Build.MODEL | 设备型号 |
| `android_version` | Build.VERSION.RELEASE | 系统版本 |
| `screen_resolution` | "1920×1080" | 屏幕分辨率 |
| `device_type` | "tv" / "box" / "stick" | 设备类型推断 |
| `ram_size` | "2GB" / "4GB" | 内存档位 |
