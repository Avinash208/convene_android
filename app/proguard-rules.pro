# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/mahiti/Library/Android/sdk/tools/proguard/proguard-android.txt
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
-keep class net.sqlcipher.** { *; }
-dontwarn net.sqlcipher.**
-dontwarn com.google.**
-dontwarn org.apache.**
-keep class com.android.volley.**{*;}
-dontwarn com.android.support.**
 #### -- Picasso --
 -dontwarn com.squareup.picasso.**

 #### -- OkHttp --
 -dontwarn com.squareup.okhttp.internal.**

 #### -- Apache Commons --
 -dontwarn org.apache.commons.logging.**
 -dontwarn org.apache.commons.**
 -keep class org.apache.http.** { *; }
 -dontwarn org.apache.http.**
 # Retrofit 1.X

 -keep class com.squareup.okhttp.** { *; }
 -keep class retrofit.** { *; }
 -keep interface com.squareup.okhttp.** { *; }

 -dontwarn com.squareup.okhttp.**
 -dontwarn okio.**
 -dontwarn retrofit.**
 -dontwarn rx.**

 -keepclasseswithmembers class * {
     @retrofit.http.* <methods>;
 }

 # If in your rest service interface you use methods with Callback argument.
 -keepattributes Exceptions

 # If your rest service methods throw custom exceptions, because you've defined an ErrorHandler.
 -keepattributes Signature

 -keep public class net.sqlcipher.** {
     *;
 }

 -keep public class net.sqlcipher.database.** {
     *;
 }

 -keep class javax.lang.model.** { *; }
 -keep class android.** { *; }
 -keep class org.apache.http.** { *; }

 -keep class butterknife.** { *; }
 -dontwarn butterknife.internal.**
 -keep class **$$ViewBinder { *; }
 -keepclasseswithmembernames class * { @butterknife.* <fields>; }
 -keepclasseswithmembernames class * { @butterknife.* <methods>; }
 -keep class net.sqlcipher.** { *; }

 # Also you must note that if you are using GSON for conversion from JSON to POJO representation, you must ignore those POJO classes from being obfuscated.
 # Here include the POJO's that have you have created for mapping JSON response to POJO for example.

