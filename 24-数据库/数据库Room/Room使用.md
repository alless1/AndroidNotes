 * 谷歌推出的一款ORM类型数据库框架Room。

### 特点 ###
> 1.编译时SQL语句检查（比如查询语句）
> 2.SQL查询结果关联到java对象。
> 3.耗时操作主动要求异步处理。
> 4.基于注解编译时自动生成代码。
> 5.API设计符合SQL标准。

### 使用方式 ###
#### 一、引入框架 ####

	dependencies {
	   
	    implementation "android.arch.persistence.room:runtime:1.0.0"
	    annotationProcessor "android.arch.persistence.room:compiler:1.0.0"
	}


	allprojects {
	    repositories {
	        jcenter()
	        google()//引入谷歌仓库
	    }
	}

> 对android studio和gradle版本有要求，当前使用andriod studio3.0.1,gradle-4.1-all。

#### 二、三大组件 ####
	1.@Entity ：实体类
	2.@Dao ：数据操作接口
	3.@Database ：数据库，生成表单

#### 三、@Entity ####
> 简单使用

	字段如果不是public，需给出set/get方法。
	
	@Entity(tableName = "student_info")//表名student_info
	public class StudentEntity {
	    @NonNull
	    @PrimaryKey
	    public String serialNum;
	    
	    public String name;
	    public int age;
	}

> 添加主键自增长

	@PrimaryKey(autoGenerate = true)
    public int id;

> 添加多个主键
	
	@Entity(primaryKeys = {"firstName", "lastName"})


> 添加字段索引
	只是增加对象的时候可以根据索引判断，不过根据主键也可以，所以索引的目的还是不太清楚。

	@Entity(indices = {@Index(value = {"first_name", "last_name"},
        unique = true)})
> 自定义列名
 
	@ColumnInfo(name = "first_name")
    public String firstName;


#### 四、@Dao ####

	接口注释@Dao，方法参数可以是对象，也可以是对象集合。

	@Dao
	public interface IStudentDao {
	    /**
	     * 如果不指定onConflict,插入重复主键对象会carsh
	     *可以根据主键（多个主键一致）或者索引来判断OnConflictStrategy的条件
	     *OnConflictStrategy.IGNORE 无反应
	     *OnConflictStrategy.REPLACE 更新原来的
	     * @param entity 可以插入单个对象，也可以插入对象集合
	     * @return 可以为void，long返回插入的行Id，如果参数是集合,也会返回long[] 或者long{}
	     */
	    @Insert(onConflict = OnConflictStrategy.IGNORE)
	    long addStudent(StudentEntity entity);
	
	    /**
	     * 根据主键查找对象删除
	     * @param entity 也可以为集合
	     * @return 删除的行号
	     */
	    @Delete
	    int deleteStudent(StudentEntity entity);
	
	
	    /**
	     * 根据主键查找对象更新
	     * @param entity
	     * @return 返回更新的行号
	     */
	    @Update
	    int updateStudent(StudentEntity entity);
	
	
	
	    //查询所有
	    @Query("select * from student_info")
	    List<StudentEntity> getStudentAll();
	
	    //按条件查询
	    @Query("select * from student_info where name = :name")
	    StudentEntity getStudentByName(String name);
	
	}

> 增 @Insert

	根据主键或者索引唯一的条件来增加对象。
	返回值是增加行位置数，-1是没有增加。
	方法参数可以是实体类或者实体类的集合，返回值可以是void，long,long[],List<long>;
	
	冲突条件onConflict，如果不设置，遇到重复主键的添加会crash。
	一般使用这两个，更新或者不处理。
	*OnConflictStrategy.REPLACE 更新原来的
	*OnConflictStrategy.IGNORE 不处理

> 删 @Delete

	根据主键来寻找对象。
	返回值是删除的数量，0是没有删除条目
	@Delete
    int delete(T t);
> 改 @Update
	
	根据主键来寻找对象。
	返回值是修改的数量，0是没有修改条目
	@Update
    int update(T t);

> 查 @Query

	查询支持SQL语句，编译器语法检查。
	There are 3 types of queries supported in {@code Query} methods: SELECT, UPDATE and DELETE.
	可以指定条件删除
	//可以执行删除语句
    @Query("delete from user_account where account = :account")
    int deleteByAccount(String account);

	//查询所有
    @Query("select * from student_info")
    List<StudentEntity> getStudentAll();

    //根据条件查询
    @Query("select * from student_info where name = :name")
    StudentEntity getStudentByName(String name);

#### 五、@Database ####

	当实例化AppDatabase对象时，你可以遵循单例设计模式，因为每个RoomDatabase实例代价是非常昂贵的，并且你几乎不需要访问多个实例。

	
	@Database(entities = {PersonBean.class, StudentEntity.class},version = 2,exportSchema = false)
	public abstract class AppDatabase extends RoomDatabase {
	    private static AppDatabase sAppDatabase;
	    public static AppDatabase getAppDatabase(Context context){
	        if(sAppDatabase==null){
	            synchronized (AppDatabase.class){
	                if(sAppDatabase == null){
	                    sAppDatabase = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"user.db")
	                            .allowMainThreadQueries()//允许在主线程操作
	                           // .addMigrations(MIGRATION_1_2,MIGRATION_2_3)//数据库升级迁移数据
	                            .fallbackToDestructiveMigration()//数据库更新时清除数据；
	                            .build();
	                }
	            }
	        }
	        return sAppDatabase;
	    }
	
	    public static void onDestroy(){
	        sAppDatabase = null;
	    }
	
	    public abstract IPersonDao getPersonDao();
	
	    public abstract IStudentDao getStudentDao();
	
	    //数据库版本升级
	    static final Migration MIGRATION_1_2 = new Migration(1,2) {
	        @Override
	        public void migrate(@NonNull SupportSQLiteDatabase database) {
	            database.execSQL("alter table person add column phone text");
	        }
	    };
	    static final Migration MIGRATION_2_3 = new Migration(2,3) {
	        @Override
	        public void migrate(@NonNull SupportSQLiteDatabase database) {
	            database.execSQL("CREATE TABLE 'student_info'('name' TEXT,'age' INTEGER not null,'serialNum' TEXT not null,PRIMARY KEY ('serialNum'))");
	        }
	    };
	
	}

> 创建抽象类继承RoomDatabase，并且注释

	@Database(entities = {PersonBean.class, StudentEntity.class},version = 2,exportSchema = false)

	entities：实体类
	version:版本号，数据库有更新的时候需要更新版本号。
	exportSchema:对外提供的…
	
	抽象类提供抽象方法生成Dao.
	public abstract IPersonDao getPersonDao();
	
>获取数据库对象-->Dao

	//user.db 数据库名
	AppDatabase db = Room.databaseBuilder(context,AppDatabase.class,"user.db").build();

>操作，通过dao实现增删改查。

	 mStudentDao = AppDatabase.getAppDatabase(MainActivity.this).getStudentDao()

#### 六、主线程执行 ####

	默认操作在子线程，如果在主线程执行会抛出异常。
	强制在主线程执行增加参数。
    .allowMainThreadQueries()//允许在主线程操作

#### 七、数据库修改升级 ####

> 数据库每次有改动都需要增加版本号。

	比如增加表的列，修改表的字段，修改主键。

> 默认会保存数据，数据迁移需要设置Migration。

	version = 3

	.addMigrations(MIGRATION_1_2,MIGRATION_2_3)//数据库升级迁移数据

	    //数据库版本升级
    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("alter table person add column phone text");
        }
    };
    static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE 'student_info'('name' TEXT,'age' INTEGER not null,'serialNum' TEXT not null,PRIMARY KEY ('serialNum'))");
        }
    };
	
> 如果不需要保存数据，就不需要Migration，设置参数。版本号还是要更新。

	version = 3
	.fallbackToDestructiveMigration()//数据库更新时清除数据；
	