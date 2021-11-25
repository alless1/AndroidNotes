#最低支持的版本
cmake_minimum_required(VERSION 3.0)
#项目名称，任意指定
project(PROJECT)
#开启调试
set(CMAKE_BUILD_TYPE Debug)
#头文件目录
include_directories(include)
#新增C++文件需要在这里添加
set(SRC 
    src/Student.cpp
    )
#生成可执行文件main
add_executable(cmake_main main.cpp ${SRC})