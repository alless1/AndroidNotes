### android studio 3.1 自定义打包jar和混淆 ###

> 使用说明：在module的gradle.gradle文件中定义，task后面的任务名可以随便起。在terminal中输入命令 gradlew 任务名。或者在右侧边的Gradle里面Tasks-->other-->任务名。

1.自定义生成jar包。

> 一般其实可以直接拿系统生成的classes.jar，因为自定义的话需要指定文件，如果引用了第三方jar还要指定第三方所有文件。

	//自定义生成jar，过滤文件。
	task makeJar(type: Jar, dependsOn: ['build']) {
	    archiveName = 'my-lib.jar'  //指定生成jar名
	    from('build/intermediates/classes/release/') //代码来源.class文件
	    from(project.zipTree("libs/yaya_sdk_lib.jar")) //代码来源 第三方jar
	    destinationDir = file('build/libs') //指定生成jar的位置
	    exclude('com/yunva/sensitivesdk/BuildConfig.class') //过滤不需要的文件。
	    exclude('**/R.class')
	    include('com/yunva/sensitivesdk/') //指定需要打包成jar的文件
	    include('com/blm/') //指定需要打包成jar的文件,第三方jar的文件。
	    include('com/pg/')
	    include('com/yunva/im/')
	}

2.复制系统的jar，改变jar名和目录。

> 将系统已经生成的jar包复制到指定目录。

	//将系统生成的jar复制到指定位置。
	task copyJar(type:Copy,dependsOn: ['build']){
	    delete 'build/sensitivesdk_V1.0.jar'//删除之前存在的
	    from( 'build/intermediates/packaged-classes/release')//指定jar包来源
	    //from( 'build/libs')//系统生成jar的位置
	    into( 'build/libs')//指定复制目标位置。
	    include('classes.jar')//要复制的文件。
	    //include('my-lib.jar')//要复制的文件。
	    rename('classes.jar', 'sensitivesdk_V1.1.jar')//重命名	
	}




3.混淆jar。

> 如果配置了proguard-rules.pro，并且开启了minifyEnabled true，那么系统生成的jar已经混淆过了，没必要手动混淆。

 	//混淆jar包
	task proguard(type: proguard.gradle.ProGuardTask,dependsOn: ['build']) {
	
	    injars 'build/libs/my-lib.jar' //输入路径
	
	    outjars 'build/libs/my-lib2.jar' //输出路径
	
	    configuration 'proguard-rules.pro' //添加配置信息
	}

总结，其实只要用系统生成的jar包就可以了，不用手动指定文件和第三方文件，也不用自己混淆。所以只需要用步骤2复制改名就可以，如果找不到或没有系统生成的jar，就用步骤1的方法生成jar包。

不同的android studio classes.jar路径可能不一样
from('build/intermediates/bundles/release/')
from( 'build/intermediates/packaged-classes/release')


4.游戏加速sdk，SpeedUp中jar包 示例。

	//Copy类型
	task makeJar(type: Copy, dependsOn: ['build']) {
	    //删除存在的
	   // delete 'build/libs/yaya_speed_up_sdk.jar'
	    //设置拷贝的文件
	    from('build/intermediates/bundles/release/')
	    //打进jar包后的文件目录
	    into('build/libs')
	    //将classes.jar放入build/libs/目录下
	    //include ,exclude参数来设置过滤
	    exclude "**/R.class"
	    exclude "**/R\$*.class"
	    include('classes.jar')
	    //重命名
	    rename('classes.jar', 'cloudvoice_speed_up_sdk.jar')
	}
