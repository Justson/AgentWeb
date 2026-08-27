# agentweb-filechooser 随库下发的 consumer proguard/R8 规则。
#
# agentweb-core 通过 Class.forName + getDeclaredMethod 按「字符串名」跨模块反射
# 调起本模块（见 AgentWebUtils#showFileChooserCompat）：
#   FileChooser.newBuilder(Activity, WebView)
#   FileChooser$Builder#setUriValueCallback(s) / setFileChooserParams /
#   setAcceptType / setJsChannelCallback / setPermissionInterceptor / build
#   FileChooser#openFileChooser()
# 这条链路无法被静态分析感知，一旦 R8 重命名这些方法，文件选择功能会静默失效，
# 因此必须精确保住这两个类型及其成员。
-keep class com.just.agentweb.filechooser.FileChooser { *; }
-keep class com.just.agentweb.filechooser.FileChooser$Builder { *; }

-dontwarn com.just.agentweb.filechooser.**
