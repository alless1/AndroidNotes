> 在android{}里添加代码。


### 生成aar的方式 ###

点击右边的gradle-tasks-build-assemble

生成文件的目录build-outputs-aar

如果生成的so文件没有打包进去。

        release {
			ndk {
				abiFilters 'armeabi','armeabi-v7a'
			}
        }

#### 一、自定义apk的名字 ####

	
	android.applicationVariants.all { variant ->
	    variant.outputs.each { output ->
	        def outputFile = output.outputFile
	        if (outputFile != null && outputFile.name.endsWith('.apk')) {
	            def fileName = "自定义.apk"
	            output.outputFile = new File(outputFile.parent, fileName)
	        }
	    }
	}

#### 二、自定义aar的名字 ####

	
	android.libraryVariants.all { variant ->
	        variant.outputs.each { output ->
	            def outputFile = output.outputFile
	            if (outputFile != null && outputFile.name.endsWith('.aar')) {
	                def fileName = "自定义.aar"
	                output.outputFile = new File(outputFile.parent, fileName)
	            }
	        }
	}

#### 三、如果报错 ####

	Cannot set the value of read-only property 'outputFile' for object of type com.android.build.gradle.internal.api.LibraryVariantOutputImpl.

#### apk ####

	
	android.applicationVariants.all { variant ->
	        variant.outputs.all {
	            def fileName = "自定义.apk"
	            outputFileName = fileName
	        }
	}

#### aar ####

	
	android.libraryVariants.all { variant ->
	        variant.outputs.all {
	            def fileName = "自定义.aar"
	            outputFileName = fileName
	        }
	}

#### 四、示例 ####

	//打包日期
	def releaseTime() {
	    return new Date().format("yyyyMMdd_HH");
	}

	android {
	    libraryVariants.all { variant ->
	        variant.outputs.each { output ->
	            def outputFile = output.outputFile
	            if (outputFile != null && outputFile.name.endsWith('.aar')) {
	                // 输出apk名称为
	                if (variant.buildType.name == "release") {
	                    def fileName = "yunva_gameusersdk_v${defaultConfig.versionName}_${releaseTime()}_release.aar"
	                    output.outputFile = new File(outputFile.parent, fileName)
	                } else {
	                    def fileName = "yunva_gameusersdk_v${defaultConfig.versionName}_${releaseTime()}_debug.aar"
	                    output.outputFile = new File(outputFile.parent, fileName)
	                }
	            }
	        }
	    }
	}