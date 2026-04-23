# Consumer Proguard/R8 rules for AgentWeb.
#
# Issue #1072: in release builds the JS bridge methods were stripped because
# the host app did not have a keep rule for @JavascriptInterface. AgentWeb
# itself uses reflection-style discovery on these methods, so the rule needs
# to apply to every consumer that ships the library.

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# AgentWeb's own bridge plumbing relies on reflection over its public API.
-keep class com.just.agentweb.** { *; }
-dontwarn com.just.agentweb.**
