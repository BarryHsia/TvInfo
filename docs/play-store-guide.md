# Google Play 上架指南

## 构建 Release AAB

```bash
ANDROID_HOME=~/Android/Sdk ./gradlew bundleRelease
# 输出: app/build/outputs/bundle/release/app-release.aab
```

## 签名信息

| 项目 | 值 |
|------|-----|
| Keystore | `keystore/tvinfo-release.jks` |
| Alias | `tvinfo` |
| Validity | 10000 天 (约 27 年) |
| Algorithm | RSA 2048 |

⚠️ **Keystore 文件和密码务必安全备份，丢失后无法更新应用。**

## 上架步骤

### 1. 托管隐私政策

在 GitHub 仓库 Settings → Pages：
- Source: `main` 分支，`/docs` 目录
- 隐私政策 URL: `https://barryhsia.github.io/TvInfo/privacy-policy.html`

### 2. Google Play Console 创建应用

1. 打开 https://play.google.com/console
2. 创建应用 → 填写：
   - 应用名称: `Tv Info - System Dashboard`
   - 默认语言: English (United States)
   - 应用类型: 应用
   - 免费/付费: 免费
   - 声明: 勾选开发者计划政策和美国出口法

### 3. 商店设置

#### 主要商店列表

| 字段 | 内容 |
|------|------|
| 应用名称 | Tv Info - System Dashboard |
| 简短说明 | View your Android TV's hardware, software, network & storage info at a glance. |
| 完整说明 | 见 `docs/store-listing.md` |
| 应用图标 | 512×512 PNG (从 `mipmap-xxxhdpi` 放大或重新生成) |
| 置顶大图 | 1024×500 PNG |
| TV Banner | 1280×720 PNG |
| 截图 | `docs/screenshots/` 下的 1920×1080 PNG，至少 2 张 |

#### 分类

| 字段 | 值 |
|------|-----|
| 应用类别 | 工具 |
| 标签 | system info, android tv, hardware info |
| 联系邮箱 | 你的邮箱 |
| 隐私政策 URL | `https://barryhsia.github.io/TvInfo/privacy-policy.html` |

### 4. 内容分级

填写 IARC 问卷：
- 暴力: 否
- 色情: 否
- 赌博: 否
- 受控物质: 否
- 粗俗语言: 否
- 用户交互: 否

预期分级: **Everyone (所有人)**

### 5. 目标受众和内容

- 目标年龄段: 18 岁以上（工具类应用选这个最简单）
- 不面向儿童

### 6. 应用访问权限

- 应用功能无需登录或特殊访问
- 选择"所有功能无需特殊访问权限即可使用"

### 7. 数据安全

| 问题 | 回答 |
|------|------|
| 是否收集或共享用户数据 | 是 (Analytics) |
| 数据类型 | 应用活动 (页面浏览) |
| 是否加密传输 | 是 (Firebase 默认 HTTPS) |
| 用户能否请求删除数据 | 否 (匿名数据) |
| 是否面向儿童 | 否 |

### 8. 上传 AAB 并发布

1. 发布 → 正式版 → 创建新版本
2. 上传 `app-release.aab`
3. 版本说明填写: "Initial release - TV system information dashboard"
4. 审核并发布

### 9. TV 特定要求

Google Play 会自动检测 `AndroidManifest.xml` 中的：
- `android.software.leanback` → 标记为 TV 应用
- `android.hardware.touchscreen required=false` → 不要求触摸屏
- `android:banner` → TV 主屏幕横幅

确保这些都已配置（当前项目已配置完成）。

## 审核时间

- 首次提交: 通常 1-7 天
- 后续更新: 通常 1-3 天
- TV 应用可能额外审核 TV 兼容性

## 版本更新流程

1. 修改 `app/build.gradle.kts` 中的 `versionCode` (递增) 和 `versionName`
2. 运行 `./gradlew bundleRelease`
3. 上传新的 AAB 到 Play Console
