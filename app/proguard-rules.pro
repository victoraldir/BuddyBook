# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/victoraldir/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class androidx.appcompat.widget.ShareActionProvider { *; }
-keep class androidx.appcompat.widget.SearchView { *; }
-keep class com.quartzodev.data.**  { *; }
-keep class com.quartzodev.api.entities.**  { *; }
-keep interface com.quartzodev.api.interfaces.** {*;}
-keep class com.quartzodev.api.strategies.**  { *; }
-keep class com.quartzodev.api.**  { *; }
-keep class com.firebase.**  { *; }


#Retrofit
-dontwarn okio.**
-dontwarn javax.annotation.**

-dontwarn org.simpleframework.xml.stream.**
# SimpleXMLParsing
-keep public class org.simpleframework.** { *; }
-keep class org.simpleframework.xml.** { *; }
-keep class org.simpleframework.xml.core.** { *; }
-keep class org.simpleframework.xml.util.** { *; }

-keepclassmembers class * {
    @org.simpleframework.xml.* *;
}

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# OKHttp Glide module
-keep class com.bumptech.glide.integration.okhttp.OkHttpGlideModule

#AboutLibraries
-keep class .R
-keep class **.R$* {
    <fields>;
}

