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

