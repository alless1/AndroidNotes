### 一、设置只编译 ###

> 在module的build.gradle里设置

	dependencies {
	   
	    compileOnly files('libs/framwork.jar')//3.0以前是provided
	
	}

### 二、设置引用优先级 ###

> 在module的xxx.iml文件中调换次序。
	//将framwork移到sdk上面。
    <orderEntry type="library" name="Gradle: __local_aars__:D.\demo\RemoteDemo\AutoTest\libs\framwork.jar:unspecified@jar" level="project" />
    <orderEntry type="sourceFolder" forTests="false" />
    <orderEntry type="jdk" jdkName="Android API 19 Platform" jdkType="Android SDK" />

### 三、设置编译优先级 ###

> 在project的build.gradle里设置

	allprojects {
	    repositories {
	        google()
	        jcenter()
	
	    }

	    gradle.projectsEvaluated {
	        tasks.withType(JavaCompile) {
	            options.compilerArgs.add('-Xbootclasspath/p:AutoTest\\libs\\framwork.jar')//这里注意jar包名，之前写错了，一直打包不过。
	        }
	    }
	
	}