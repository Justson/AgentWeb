# AgentWeb 随库下发的 consumer proguard/R8 规则。
#
# 这些规则会施加到每一个依赖 AgentWeb 的 App，因此只保留确有必要的部分，
# 避免让使用者失去对整个库的裁剪与混淆能力。

# Issue #1072：使用者自己的 JS bridge 对象上、被 @JavascriptInterface 标注的方法
# 由 WebView 在运行时反射调用，R8 无法感知，release 包中会被剥离，
# 表现为 JsInterfaceObjectException。这条规则作用于使用者的类，必须随库下发。
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-dontwarn com.just.agentweb.**
