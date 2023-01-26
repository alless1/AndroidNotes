# MacOS VSCode OpenCV的使用

> 基于《MacOS VSCode OpenCV的使用》文档，安装好vscode，配置好cmakeLists。



## 1.配置vscode

将opencv库文件路径添加到c_cpp_properties.json文件中。

~~~
{
    "configurations": [
        {
            "name": "Mac",
            "includePath": [
                "${workspaceFolder}/**",
                "${workspaceFolder}/include",
                "${workspaceFolder}/src",
                "/usr/local/Cellar/opencv/4.5.3_3/include/opencv4",
                "/usr/local/Cellar/opencv/4.5.3_3/lib",
                "/usr/local/Cellar/ffmpeg/4.4.1_3/include",//ffmpeg增加
                "/usr/local/Cellar/ffmpeg/4.4.1_3/lib"//ffmpeg增加
            ],
            "defines": [],
            "macFrameworkPath": [
                "/Library/Developer/CommandLineTools/SDKs/MacOSX.sdk/System/Library/Frameworks"
            ],
            "compilerPath": "/usr/bin/clang",
            "cStandard": "c11",
            "cppStandard": "c++98",
            "intelliSenseMode": "macos-clang-x64",
            "configurationProvider": "ms-vscode.cmake-tools"
        }
    ],
    "version": 4
}
~~~



## 2.配置CMakeLists.txt文件

~~~
find_package(PkgConfig REQUIRED)
pkg_check_modules(ffmpeg REQUIRED IMPORTED_TARGET libavcodec libavformat libavutil libswscale)
target_link_libraries(cmake_main PRIVATE ${OpenCV_LIBS} PkgConfig::ffmpeg)
#cmake_main是可执行的目标文件。
~~~



