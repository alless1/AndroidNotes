### 一.在java类中声明一个带native关键字的方法.
	
	public native String helloFromC();

###二.在jni目录中创建c代码文件.

		#include <stdio.h>
		#include <jni.h>
		...
		//返回值 Java_包名_类名_方法名(JNIEnv* env ,jobject obj){}
		jstring Java_cn_itcast_ndk_DemoActivity_helloFromC(JNIEnv* env ,jobject obj){

		//return  (*(*env)).NewStringUTF(env,"hello from c");

	     return (*env)->NewStringUTF(env,"hello from c");
		}
###三.在jni目录中创建Android.mk文件.(编译c代码的规则文件)

		LOCAL_PATH := $(call my-dir)
		include $(CLEAR_VARS)
		LOCAL_MODULE    :=Hello
		LOCAL_SRC_FILES :=Hello.c
		#liblog.so libGLESv2.so
		LOCAL_LDLIBS += -llog
		include $(BUILD_SHARED_LIBRARY)

###四.使用ndk-build编译c代码文件,会自动在libs目录里生成二进制.so文件.

		源文件 Hello.c
		生成   libHello.so

###五.在java类静态代码块中加载二进制文件.
		static{
	    // 加载libHello.so文件
		System.loadLibrary("Hello");
		}
### 完成

### 1.利用编译器生成第二步中的方法名.(工具:javah)

		javah xxx(class文件)

		生成xxx.h文件

		(xxx为完整的类名)

### 2.在c代码中引入方法签名,并使用对应的方法名.(双引号)
	
		#include <stdio.h>
		#include <jni.h>
		#include "cn_itcast_ndk_DemoActivity.h"
		...

		/*
		jstring Java_cn_itcast_ndk_DemoActivity_helloFromC(JNIEnv* env ,jobject obj){
		
				//return  (*(*env)).NewStringUTF(env,"hello from c");
		
			     return (*env)->NewStringUTF(env,"hello from c");
		}
		
		
		jstring Java_cn_itcast_ndk_DemoActivity_hello_1from_1c(JNIEnv* env ,jobject obj){
		
		     return (*env)->NewStringUTF(env,"hello from c ___-");
		}
		*/
		JNIEXPORT jstring JNICALL Java_cn_itcast_ndk_DemoActivity_helloFromC
		  (JNIEnv * env, jobject obj){
		
			return (*env)->NewStringUTF(env,"hello from c");
		}