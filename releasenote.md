* v_5.1.5 更新
	* **修复发布产物中缺少 AAR 的问题**。此前 `publishing` 的 publication 没有 `from components.release`，
	  发布出去的只有一个空壳 POM，不含任何库产物；现已补上，并按 AGP 要求声明 `singleVariant('release')`
	* 发布渠道统一到 JitPack，坐标改为 `com.github.Justson.AgentWeb:*`，不再使用 `io.github.justson:*`

* v_5.1.4 更新
	* 修复 SSL 证书错误弹窗会叠加出多个的问题 [#1022](https://github.com/Justson/AgentWeb/issues/1022)
	* 修复 `AgentActionFragment.onRequestPermissionsResult` 空指针崩溃 [#1062](https://github.com/Justson/AgentWeb/issues/1062)
	* 修复 Android 14 下选择「仅选定照片」后文件选择失效 [#1077](https://github.com/Justson/AgentWeb/issues/1077)
	* 修复拍照流程多余索要媒体权限，导致 Android 14 多弹一次照片访问弹窗 [#1077](https://github.com/Justson/AgentWeb/issues/1077)
	* 减少 `queryIntentActivities` 调用；`DISALLOW` 模式下不再调用 [#1078](https://github.com/Justson/AgentWeb/issues/1078)
	* 移除无调用点的 `getInstallApkIntentCompat()`，消除安全扫描告警 [#969](https://github.com/Justson/AgentWeb/issues/969)
	* README 补充 Client 覆盖、`OpenOtherPageWays` 与应用列表查询的说明

* v_5.1.3 更新
	* 修复 SSL 证书错误弹窗未校验 Activity 状态导致的 BadTokenException 崩溃 [#1065](https://github.com/Justson/AgentWeb/issues/1065)
	* 修复 `file://` 本地页面互跳被 `interceptUnkownUrl` 静默拦截导致无响应 [#762](https://github.com/Justson/AgentWeb/issues/762)
	* 随库下发 consumer proguard 规则，使用者无需再自行声明 `@JavascriptInterface` keep 规则 [#1072](https://github.com/Justson/AgentWeb/issues/1072)
	* 感谢 [@jim-daf](https://github.com/jim-daf) 的贡献

* v_5.1.2 更新
	* compileSdk / targetSdk 升级到 36 (Android 16)
	* Android Gradle Plugin 升级到 8.13.2，Gradle Wrapper 升级到 8.13
	* buildToolsVersion 升级到 36.1.0

* v_5.0.0 更新
	* ActionActivity 重构， 使用Fragment 替代 Activity，解决多进程使用问题
	* 新增 WebRTC Sample
	* 新增 FileCompressor ，允许选择文件后对文件进行操作，如文件压缩，图片方向调整等
	* DefaultWebClient#onReceivedSslError 添加默认处理
	* 文件选择器开放多选
	* fix #777 ，FileChooserParams.createIntent() 导致AcceptTypes丢失问题
	* androidx Grade version upgrade to 7.0.2
	* 新增 AgentWebCompat.setDataDirectorySuffix(context) 修复 Using WebView from more than one process 崩溃

* v_4.1.1 更新
    * [#587](https://github.com/Justson/AgentWeb/pull/587) input 支持视屏拍摄
    * [#614](https://github.com/Justson/AgentWeb/pull/614)修复上传文件选择的兼容性bug
    * 重构了Download
    * 最小SDK提升到了 14
    
* v_4.0.3 更新
	* 部分手机下载过程中～声音一直响 [#523](https://github.com/Justson/AgentWeb/issues/523)
	* 抽离[Downloader](https://github.com/Justson/Downloader)
	* 放弃反射回调WebViewClient#methods，使用洋葱模型的Middleware代替

* v_4.0.2 更新
	* 修复断点续传时进度计算错误
	* 修复无法通过`Extra`关闭进度通知

* v_4.0.0 更新
	* `AgentWeb` 拆分出 `AgentWeb-Download` 、 `AgentWeb-FileChooser` 、`AgentWeb-core` 三个库，用户可以按需选择
	* 重新设计了 `AgentWeb-Download` 
	* 删除了 `DownloadListener` 、`DefaultMsgConfig` 以及相关API
	* 旧废弃的API，4.0.0 直接删除，不在提供兼容
	* 部分类和API重命名 
	* `Fragment`和`Activity`构建一致。[#227](https://github.com/Justson/AgentWeb/issues/227)
	* 从AgentWeb-core删除 `BaseAgentWebFragment`和`BaseAgentWebActivity` ，于Sample形式提供参考
* v_3.1.0 更新
	* `WebProgress` 进度条动画更细腻
	* 修复部分机型拍照文件大小为0情况
	* 更新了`FileUpLoadChooserImpl`
* v_3.0.0 更新
	* 加入 `MiddlewareWebChromeBase` 中间件 ，支持多个 `WebChromeClient` 
	* 加入 `MiddlewareWebClientBase`中间件 ， 支持多个 `WebViewClient` 
	* 加入了默认的错误页，并支持自定义错误页 
	* 加入 `AgentWebUIController` ，统一控制UI 
	* 支持拦截未知的页面 
	* 支持调起其他应用 
* v_2.0.1 更新
	* 支持并行下载 ， 修复 #114 #109 
* v_2.0.0 更新
	* 加入动态权限 
	* 拍照
* v_1.2.6 更新
	* 修复Android 4.4以下布局错乱 
* v_1.2.5 提示信息支持配置 
	* 提示信息支持配置 
* v_1.2.4 更新
	* 支持传入 IWebLayout ，支持下拉回弹，下拉刷新效果 
* v_1.2.3 更新
	* 新增下载结果回调 
* v_1.2.2 更新
	* 修复已知 Bug 
* v_1.2.1 更新 
	* 支持调起支付宝 ， 微信支付 
* v_1.2.0 更新
	* 全面支持全屏视频 
* v_1.1.2 更新
	* 完善功能 

