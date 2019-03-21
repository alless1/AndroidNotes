#### 实现步骤： ####

1.按照umeng的要求，manifest文件中配置

	<meta-data android:name="UMENG_CHANNEL" android:value="${UMENG_CHANNEL_VALUE}" />
	//value的值动态配置。

2.在module的build.gradle的android{}中添加如下内容：

	productFlavors{
          wandoujia{
             manifestPlaceholders = [UMENG_CHANNEL_VALUE: "wandoujia"]
          }
          xiaomi{
             manifestPlaceholders=[UMENG_CHANNEL_VALUE: "xiaomi"]
          }
      }

3.优化，第二步改为如下，如果有很多个的话，这样更方便。

	productFlavors{
	  wandoujia{
	
	  }
	  xiaomi{
	
	  }
	 }
	 productFlavors.all { flavor ->
	  flavor.manifestPlaceholders = [UMENG_CHANNEL_VALUE: name]
	 }

4.修改生成apk的名字。

	 applicationVariants.all { variant ->
	    variant.outputs.each { output ->
	        def outputFile = output.outputFile
	        if (outputFile != null && outputFile.name.endsWith('.apk')) {
	            def fileName = outputFile.name.replace(".apk", "-${defaultConfig.versionName}.apk")
	            output.outputFile = new File(outputFile.parent, fileName)
	        }
	    }
	 }

5.代码中获取渠道信息。

	private String getChannel() {
	   try {
	       PackageManager pm = getPackageManager();
	       ApplicationInfo appInfo = pm.getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
	       return appInfo.metaData.getString("UMENG_CHANNEL");
	   } catch (PackageManager.NameNotFoundException ignored) {
	   }
	   return "";
	}