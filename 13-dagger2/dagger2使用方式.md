#环境配置

    配置apt插件(在build.gradle(Project:xxx)中添加如下代码)

      dependencies {
          classpath 'com.android.tools.build:gradle:2.1.0'
          //添加apt插件
          classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'

      }

    添加依赖(在build.gradle(Module:app)中添加如下代码)

      apply plugin: 'com.android.application'
      //添加如下代码，应用apt插件
      apply plugin: 'com.neenbedankt.android-apt'
      ...
      dependencies {
          ...
          compile 'com.google.dagger:dagger:2.4'
          apt 'com.google.dagger:dagger-compiler:2.4'
          //java注解
          compile 'org.glassfish:javax.annotation:10.0-b28'
          ...
      }

#简单使用方式:
##1.定义一个类似于工厂的类,提供需求的业务类.

		@Module
		public class MainActivityModule {
		    MainActivity mMainActivity;
		
		    public MainActivityModule(MainActivity mainActivity) {
		        mMainActivity = mainActivity;
		    }
		
		    @Provides
		    MainActivityPresenter provideMainActivityPresenter() {
		        return new MainActivityPresenter(mMainActivity);
		    }
		}
##2.定义连接器Component类.
		@Component(modules = MainActivityModule.class)
		public interface MainActivityComponent {
		    //此方法是相当于MainActivityModule的构造方法.(方法名自定义);
		    void get(MainActivity mainActivity);
		}

##3.使用业务类
	声明
		@Inject
		MainActivityPresenter presenter;
	初始化
		DaggerMainActivityComponent.builder().mainActivityModule(new MainActivityModule(this)).build().get(this);