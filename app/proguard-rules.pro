# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\work\studio\studio\android-sdk/tools/proguard/proguard-android.txt
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
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontoptimize
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keepattributes *Annotation*
-keepattributes Signature
#
#-libraryjars libs/apns_1.0.6.jar
#-libraryjars libs/armeabi/libBaiduMapSDK_v2_3_1.so
#-libraryjars libs/armeabi/liblocSDK4.so
#-libraryjars libs/baidumapapi_v2_3_1.jar
#-libraryjars libs/core.jar
#-libraryjars libs/gesture-imageview.jar
#-libraryjars libs/infogracesound.jar
#-libraryjars libs/locSDK_4.0.jar
#-libraryjars libs/ormlite-android-4.48.jar
#-libraryjars libs/ormlite-core-4.48.jar
#-libraryjars libs/universal-image-loader-1.9.0.jar

#-libraryjars libs/AMap_3DMap_V3.1.1_20151216.jar
#-libraryjars libs/AMap_Location_V2.2.0_20151222.jar
#-libraryjars libs/AMap_Search_V2.8.0_20160105.jar
#-libraryjars libs/com.umeng.message.lib_v2.6.0.jar
#-libraryjars libs/gson-2.2.2.jar
#-libraryjars libs/mpandroidchartlibrary-2-1-5.jar
#-libraryjars libs/okhttp-2.4.0.jar
#-libraryjars libs/okio-1.4.0.jar
#-libraryjars libs/picasso-2.5.2.jar
#-libraryjars libs/retrofit-1.9.0.jar
#-libraryjars libs/rxjava-1.0.10.jar
#-libraryjars libs/umeng-analytics-v5.6.1.jar
#-libraryjars libs/Volley.jar
#-libraryjars libs/xUtils-2.6.14.jar

#2016-4-5添加
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

 #保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable
#保持 Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
#保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
#-keepclassmembers enum * {
#  public static **[] values();
#  public static ** valueOf(java.lang.String);
#}
-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}
#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}

-keep class com.lidroid.** { *; }
#2016-4-5添加

-keep class com.baidu.** { *; }
-keep class vi.com.gdi.bgl.android.**{*;}

-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.**
-keep public class com.android.vending.licensing.ILicensingService

-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.uuhelper.Application.** { *; }
-keep class net.sourceforge.zbar.** { *; }
-keep class com.google.android.gms.** { *; }

-keep class com.bank.pingan.model.** { *; }

-keep public class * extends com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
-keep public class * extends com.j256.ormlite.android.apptools.OpenHelperManager

-keep class com.android.vending.licensing.ILicensingService
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class org.apache.commons.net.** { *; }
-keep class com.tencent.** { *; }

-keep class com.umeng.** { *; }
-keep class com.umeng.analytics.** { *; }
-keep class com.umeng.common.** { *; }
-keep class com.umeng.newxp.** { *; }

-keep class com.j256.ormlite.** { *; }
-keep class com.j256.ormlite.android.** { *; }
-keep class com.j256.ormlite.field.** { *; }
-keep class com.j256.ormlite.stmt.** { *; }

-dontwarn android.support.v4.**
-dontwarn org.apache.commons.net.**
-dontwarn com.tencent.**

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}

-dontshrink
-dontoptimize
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-dontwarn com.facebook.**

#自行添加
-dontwarn org.codehaus.mojo.**
-dontwarn java.nio.file.**
-dontwarn com.ut.mini.**
-dontwarn sun.misc.**
#自行添加


-keep enum com.facebook.**
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public interface com.facebook.**
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**

-keep public class com.umeng.socialize.* {*;}
-keep public class javax.**
-keep public class android.webkit.**

-keep class com.facebook.**
-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**

-keepattributes *Annotation*
-keep class com.idea.fifaalarmclock.entity.***
-keep class com.google.gson.stream.** { *; }
-keep class com.tencent.mm.sdk.openapi.WXMediaMessage {*;}

-keep class com.tencent.mm.sdk.openapi.** implements com.tencent.mm.sdk.openapi.WXMediaMessage$IMediaObject {*;}

-keep class im.yixin.sdk.api.YXMessage {*;}
-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}

#umeng推送相关

-dontwarn okio.**
-dontwarn com.xiaomi.**
-dontwarn com.squareup.wire.**


-keep class com.squareup.wire.* {
	public <fields>;
    public <methods>;
}

-keep class org.android.agoo.impl.*{
	public <fields>;
    public <methods>;
}

-keep class org.android.agoo.service.* {*;}

-keep class org.android.spdy.**{*;}


-keep class okio.** {*;}
-keep class com.squareup.wire.** {*;}

-keep class com.umeng.message.protobuffer.* {
	 public <fields>;
         public <methods>;
}

-keep class com.umeng.message.* {
	 public <fields>;
         public <methods>;
}

-keep class org.android.agoo.impl.* {
	 public <fields>;
         public <methods>;
}


#
#-keep public class [应用包名].R$*{
#    public static final int *;
#}
#如果compileSdkVersion为23，请添加以下混淆代码
-dontwarn org.apache.http.**
-dontwarn android.webkit.**
-keep class org.apache.http.** { *; }
-keep class org.apache.commons.codec.** { *; }
-keep class org.apache.commons.logging.** { *; }
-keep class android.net.compatibility.** { *; }
-keep class android.net.http.** { *; }

#umeng推送相关

#umeng统计相关
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
#umeng统计相关

#umeng 统计和推送共同相关
-keep public class com.yun.ycw.crm.R$*{
   public static final int *;
}
#umeng 统计和推送共同相关

#
##使用5.0.0及以上版本的SDK，请添加如下命令
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
##使用5.0.0及以上版本的SDK，请添加如下命令

#retrofit库相关
-dontwarn retrofit.**
-keep class retrofit.** {*;}
-keepattributes Signature
-keepattributes Exceptions
#retrofit库相关

#高德地图 相关
#3D 地图
-keep class com.amap.api.mapcore.**{*;}
-keep class com.amap.api.maps.**{*;}
-keep class com.autonavi.amap.mapcore.*{*;}

#    定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}
#    搜索
-keep   class com.amap.api.services.**{*;}
#    2D地图
-keep class com.amap.api.maps2d.**{*;}
-keep class com.amap.api.mapcore2d.**{*;}
#    导航
-keep class com.amap.api.navi.**{*;}
-keep class com.autonavi.**{*;}
#高德地图 相关
