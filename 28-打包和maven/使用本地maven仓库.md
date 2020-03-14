### 在android studio中上传aar到本地maven ###

1.在android library项目的build.gradle里添加如下代码：

	apply plugin: 'maven'
	uploadArchives{
	    repositories{
	        def localUrl = mavenLocal().getUrl()
	        mavenDeployer{
	// 本地仓库路径
	            repository(url: localUrl)
	// 唯一标识
	            pom.groupId = "com.uboxol.usocket"
	// 项目名称
	            pom.artifactId = "uSocket"
	// 版本号
	            pom.version = "1.6.4"
	        }
	    }
	}



2.在项目根目录的build.gradle，添加mavenLocal();

    repositories {
    
        ...
        mavenLocal();


​        
    }

3.在app目录的build.gradle，添加依赖

	compile 'com.uboxol.usocket:uSocket:1.6.4'

### 上传jar到本地maven ###

方法参考上传aar，但实际使用中发现，无法将libs目录下的jar包依赖进去。所以如果是有依赖第三方jar，建议先将源码和其他jar包合并成一个jar，再使用maven工具上传jar。

### 使用maven工具上传jar ###

1.下载maven工具。

2.配置maven环境变量。

3.使用命令，将准备好的jar上传到本地仓库。

```shell
mvn install:install-file -Dfile=D:\my_lib.jar -DgroupId=com.uboxol.usocket -DartifactId=uSocket -Dversion=1.6.4-SNAPSHOT -Dpackaging=jar

mvn install:install-file -Dfile=/Users/chengjie/Desktop/my_uSocket.jar -DgroupId=com.uboxol.usocket -DartifactId=uSocket -Dversion=1.6.4-SNAPSHOT -Dpackaging=jar


```


