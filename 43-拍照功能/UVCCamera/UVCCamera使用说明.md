## UVCCamera使用说明

### 一.项目集成问题

1. 下载不到依赖库。

   修改根目录下的maven路径，百度一个可以用的链接。

2. ndk编译出错。

   * 使用android-ndk-r14b版本的ndk
   * Application.mk文件里的APP_ABI := armeabi armeabi-v7a，不要使用all，有的平台编译不支持。
   * 删除掉obj目录下的文件，重新编译。

3. 无法断点到c代码。

   * module的build.gradle里填入android{ publishNonDefault true }

   * Run/Debug Cofigurations中的app的Debugger选项里的Symbol Directories内容里填入代码路径：/Users/chengjie/AndroidStudioProjects/test/TestVisual/libuvccamera/src/main/obj/local

### 二.项目使用见代码。