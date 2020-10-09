#### 两种方式加载本地html文件（android 7.1.2系统下测试）

#### 1.使用系统浏览器打开html文件。

> 需要使用provider，最好自定义一个类，不会发生冲突。

1.自定义provider

~~~java
public class MyFileProvider extends FileProvider {
}
~~~

2.配置清单文件

~~~java
        <provider
            android:name=".MyFileProvider"
            android:authorities="${applicationId}.provider"
            android:grantUriPermissions="true"
            android:exported="false">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/provider_paths"/>
        </provider>
~~~

3.在res目录下，创建xml文件夹，创建文件provider_paths.xml文件。(root-path 是使用sd卡目录)

~~~java
<?xml version="1.0" encoding="utf-8"?>
<paths>
        <root-path
            name="mytest"
            path="" />
<!--    <external-path-->
<!--        name="."-->
<!--        path="external_files"></external-path>-->
</paths>
~~~

4.打开本地html文件

~~~java
        Uri fileUrl = FileProvider.getUriForFile(this, getApplicationContext().getPackageName()+".provider", new File(path));
        Intent intent = new Intent (Intent.ACTION_VIEW, fileUrl);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//这里一定要加，否则浏览器会提示打不开网页，url不对什么的。
        this.startActivity(intent);
~~~



#### 2.使用WebView打开html文件。

> 要注意的一点就是协议头为file://

~~~java
mWebview.loadUrl("file://"+path);
~~~

