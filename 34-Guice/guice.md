### 介绍 ###

谷歌出的一款ioc依赖注入框架。

### 使用 ###

> 依赖

	dependencies {
	
	    implementation 'com.google.inject:guice:4.0:no_aop@jar'
	    implementation 'com.google.guava:guava:18.0'
	    implementation 'javax.inject:javax.inject:1'
	}

> 使用

	 Injector mInjector = Guice.createInjector(new MyAppModule());
	 Appli appli = mInjector.getInstance(Appli.class);
	 appli.work();

> 说明

	MyAppModule是继承AbstractModule的一个配置类，作用是绑定接口和实现类的对应关系。

	public class MyAppModule extends AbstractModule {
	    @Override
	    protected void configure() {
	        //初始化绑定映射关系（和@ImplementedB作用一样）
	        bind(LogService.class).to(LogServiceImpl.class);
	    }
	}

	普通类，如果没有继承关系或者不是通过父类应用，可以直接获取。有继承关系的如果有明确指定实现类也可以直接获取。
	

	@ImplementedBy(PersonImpl.class)
	public interface IPerson {
	    void running();
	}

	mInjector.getInstance(IPerson.class);//可以获取到PersonImpl

> 补充

	@Singleton表示单例
	Guice需要实例化对象，请确保相应被实例化的对象有默认构造器。
