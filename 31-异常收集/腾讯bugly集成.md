### 腾讯bugly 异常上报 ###

#### 1.gradle.build ####

	compile 'com.tencent.bugly:crashreport:latest.release'

#### 2.权限配置 ####

    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.READ_LOGS" />

#### 3.初始化 ####

	CrashReport.initCrashReport(getApplicationContext(), "d0c39a8918", true);