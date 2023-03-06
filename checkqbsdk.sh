#!/bin/bash

replaceArr=(	"android\.\*"
		"com.tencent.smtt.export.external.interfaces.*"
		"android\.webkit\.WebStorage\.\*"
		"com.tencent.smtt.export.external.interfaces.*"
		"android\.webkit\.\*" 
		"com.tencent.smtt.export.external.interfaces.*"
		"android\.net\.\*"
		"com.tencent.smtt.export.external.interfaces.*"
		"android\.net\.http\.\*"
		"com.tencent.smtt.export.external.interfaces.*"
		"android\.webkit\.ConsoleMessage"
		"com.tencent.smtt.export.external.interfaces.ConsoleMessage"
		"android\.webkit\.Cachemanager"
		"com.tencent.smtt.sdk.Cachemanager"
		"android\.webkit\.CookieManager"
		"com.tencent.smtt.sdk.CookieManager"
		"android\.webkit\.CookieSyncManager"
		"com.tencent.smtt.sdk.CookieSyncManager"
		"android\.webkit\.DownloadListener"
		"com.tencent.smtt.sdk.DownloadListener"
		"android\.webkit\.GeolocationPermissions"
		"com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback"
		"android\.webkit\.HttpAuthHandler"
		"com.tencent.smtt.export.external.interfaces.HttpAuthHandler"
		"android\.webkit\.JspromptResult"
		"com.tencent.smtt.export.external.interfaces.JsPromptResult"
		"android\.webkit\.JsResult"
		"com.tencent.smtt.export.external.interfaces.JsResult"
		"android\.webkit\.SslErrorHandler"
		"com.tencent.smtt.export.external.interfaces.SslErrorHandler"
		"android\.webkit\.ValueCallback"
		"com.tencent.smtt.sdk.ValueCallback"
		"android\.webkit\.WebBackForwardList"
		"com.tencent.smtt.sdk.WebBackForwardList"
		"android\.webkit\.WebChromeClient"
		"com.tencent.smtt.sdk.WebChromeClient"
		"android\.webkit\.WebHistoryItem"
		"com.tencent.smtt.sdk.WebHistoryItem"
		"android\.webkit\.WebIconDatabase"
		"com.tencent.smtt.sdk.WebIconDatabase"		
		"android\.webkit\.WebResourceResponse"
		"com.tencent.smtt.export.external.interfaces.WebResourceResponse"
		"android\.webkit\.WebSettings"
		"com.tencent.smtt.sdk.WebSettings"
		"android\.webkit\.WebStorage"
		"com.tencent.smtt.sdk.WebStorage"
		"android\.webkit\.WebView[^C]"
		"com.tencent.smtt.sdk.WebView"
		"android\.webkit\.WebViewClient"
		"com.tencent.smtt.sdk.WebViewClient"
		"android\.webkit\.WebStorage.QuotaUpdater"
		"com.tencent.smtt.export.external.interfaces.QuotaUpdater"
		"android\.net\.SslError"
		"com.tencent.smtt.export.external.interfaces.SslError"
		"android\.net\.\*"
		"com.tencent.smtt.export.external.interfaces.*"
		"android\.webkit\.WebViewDatabase"
		"com.tencent.smtt.sdk.WebViewDatabase"
	)

#ignoreFolder="ReaderZone"
ignoreFolder="somefolder"

#step1 find all java files containing potential bad codings
BAD_FILES=(`grep -E "android\.webkit\.|android\.net\." . -rl | grep "\.java$" | grep -v ${ignoreFolder}`)
#step2 print every bad coding.
for file in ${BAD_FILES[@]}; do
	i=0
	while [ $i -lt ${#replaceArr[@]} ]
	do
	    #echo ${replaceArr[$i]}
	    result=`grep "${replaceArr[$i]}" ${file} | grep -v "^\s*\*" | grep -v "^\s*//"`
	    if [ "${result}"x != ""x ]; then	    	
	    	echo "======" ${file} "=========="
	    	grep "${replaceArr[$i]}" ${file} -n
		echo "!!!please replace with ${replaceArr[${i}+1]}"
	    fi
	    let i+=2
	done
done


#step2 find all xml files containing potential bad codings
BAD_FILES_XML=(`grep -E "<\s*WebView" . -rl | grep "\.xml$" | grep -v ${ignoreFolder}`)
ALL_JAVA_FILES=(`find . -name "*.java" | grep -v ${ignoreFolder}`)
for file in ${BAD_FILES_XML[@]}; do
    result=`grep "<\s*WebView" ${file} | grep -v "^\s*<!"`
    if [ "${result}"x != ""x ]; then	    	
    	#echo "======" ${file} "=========="
	viewIds=`sed -n '/<\s*WebView/,/\/.*>/ p'  ${file} | grep "android:id.*/\(.*\)\"" | sed -e 's/.*android.*\///' | sed -e 's/".*//'`
	for viewId in  ${viewIds[@]}; do
		#echo ${viewId}
		foundInJava=`grep -E "\b${viewId}\b" . -rl | grep -E "\.java$"`
		#if a viewId is never used, ignore it.
		if [ "${foundInJava}"x != ""x ]; then
  			echo "======" ${file} "=========="
			echo "!!!please replace \"<WebView\" of id ${viewId} with \"<com.tencent.smtt.sdk.WebView\""
		fi
	done	
    fi
done



