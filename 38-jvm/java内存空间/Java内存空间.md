#### 内存空间

1. 虚拟机栈：Stack Frame 栈帧

   * 本地方法栈：主要用于处理本地方法（native），和虚拟机栈类似

2. 程序计数器（Program Counter），记录字节码行号，程序跳转。

   > 虚拟机栈和程序计数器属于线程私有。

3. 堆（Heap）：JVM管理的最大一块内存空间。新生代、老年代。

4. 方法区（Method Area）：存储元信息。永久代（Permanent Generation），从JDK1.0开始，已经彻底废弃了永久代，使用元空间（meta space）

   * 运行时常量池：方法区的一部分内容

5. 直接内存：Direct Memory（操作系统管理的），与Java NIO密切相关，JVM通过堆上的DirectByteBuffer来操作直接内存。

#### 创建对象过程

new关键字创建对象的3个步骤：

1. 在堆内存中创建出对象的实例。
   * 指针碰撞：前提是堆中的空间通过一个指针进行分割，一侧是已经被占用的空间，另一侧是未被占用的空间。（垃圾回收器会移动对象）
   * 空闲列表：前提是堆内存空间中已被使用与未被使用的空间是交织在一起的，这时，虚拟机就需要通过一个列表来记录哪些空间是可以使用的，哪些空间是已被使用的，接下来找出可以容纳下新创建对象且未被使用的空间，在此空间存放该对象，同时还要修改列表上的记录。
2. 为对象的实例成员变量赋初值。
   * 调用<init>方法。
3. 将对象的引用返回。

#### 对象的结构

1. 对象头：hash数据，分代信息
2. 实例数据，在一个类中所声明的各项信息。
3. 对齐填充（可选）

#### VM Option

* -Xms128m（最小heap内存）
* -Xmx256m（最大heap内存）
* -XX:+HeapDumpOnOutOfMemoryError（打印oom信息）
* -Xss160k（堆栈的大小，最少160k以上）
* -XX:MaxMetaspaceSize=200m（元空间的内存大小，64位机器初始值21M，达到这个值会触发Full GC）
  * -XX:MaxPermSize 永久代的大小（32位机器默认64M，64位的机器默认85M）

#### 元空间调试工具

* jmap -clstats PID 打印类加载器数据
* jstat -gc PID 打印空间占据信息。MC当前元空间分配大小，MU元空间使用大小（kb）
* jps 打开jvm相关进程。
  * jps -l 显示进程名和pid
  * jps -m 显示更多信息 （和jcmd -l一样）
* jcmd PID VM.flags 显示进程启动的jvm参数
* jcmd PID help 查看可以对当前进程执行的操作

#### jcmd（从JDK1.7开始新增加的命令）

* jcmd pid VM.flags：查看JVM的启动参数
* jcmd pid help：列出当前运行的java进程可以执行的操作
* jcmd pid help JFR.dump：查看具体命令的选项
* jcmd pid PerfCounter.print：查看JVM性能相关的参数
* jcmd pid VM.uptime：查看JVM的启动时长
* jcmd pid GC.class_histogram：查看系统中类的统计信息
* jcmd pid Thread.print：查看线程堆栈信息
* jcmd pid GC.heap_dump filename：导出Heap dump文件，导出的文件可以通过jvisualvm查看
* jcmd pid VM.system_properties：查看JVM的属性信息
* jcmd pid VM.version：查看目标JVM进程的版本信息
* jcmd pid VM.command_line：查看JVM启动的命令行参数信息

#### 测试工具

JConsole

jvisualvm