# Java并发编程

#### 性能问题

CPU、内存、I/O 设备之间的速度差异太大。

#### 解决性能问题

1. CPU 增加了缓存，以均衡与内存的速度差异；
2. 操作系统增加了进程、线程，以分时复用 CPU，进而均衡 CPU 与 I/O 设备的速度差异；
3. 编译程序优化指令执行次序，使得缓存能够得到更加合理地利用。

#### 导致并发问题

1. 缓存导致的可见性问题。

   > 可见性：一个线程对资源的操作对其他线程可见的。

2. 线程切换带来的原子性问题。

   > 原子性：操作指令在CPU执行的过程中不被中断。

3. 编译优化带来的有序性问题。

   > 有序性：程序按照代码的先后顺序执行。

> 其实缓存、线程、编译优化的目的和我们写并发程序的目的是相同的，都是提高程序性能。但是技术在解决一个问题的同时，必然会带来另外一个问题，所以在采用一项技术的同时，一定要清楚它带来的问题是什么，以及如何规避。

#### 解决可见性和有序性问题

> Java 内存模型是个很复杂的规范，可以从不同的视角来解读，站在我们这些程序员的视角，本质上可以理解为，Java 内存模型规范了 JVM 如何提供按需禁用缓存和编译优化的方法。具体来说，这些方法包括 volatile、synchronized 和 final 三个关键字，以及六项 Happens-Before 规则

1. 程序的顺序性规则
2. volatile 变量规则
3. 传递性
4. 管程中锁的规则
5. 线程 start() 规则
6. 线程 join() 规则

#### 解决原子性问题

互斥锁

#### 等待-通知 机制

synchronized、wait、notifyAll。

~~~java
    public void apply(){
        synchronized (mObject){
            //条件判断
            while (!pass()){
                try {
                    mObject.wait();//不符合条件，进入等待线程队列，释放锁资源
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            //todo 执行逻辑代码
            
            //释放锁资源，唤醒其他等待线程
            mObject.notifyAll();
        }
    }
~~~

> wait和sleep的区别，wait会释放锁资源，sleep不会释放锁资源，并且需要指定时间。

#### 竞态条件

> 竞态条件，指的是程序的执行结果依赖线程执行的顺序。

Vector 是一个线程安全的容器，以下代码是不安全的。

~~~java
void addIfNotExist(Vector v, Object o){ 
  if(!v.contains(o)) {
    v.add(o); 
  }
}
~~~

#### 线程中断，会清除标志

> 线程在sleep期间被打断了，抛出一个InterruptedException异常，try catch捕捉此异常，应该重置一下中断标示，因为抛出异常后，中断标示会自动清除掉！

~~~java
Thread th = Thread.currentThread();
while(true) {
  if(th.isInterrupted()) {
    break;
  }
  // 省略业务代码无数
  try {
    Thread.sleep(100);
  }catch (InterruptedException e)｛
    Thread.currentThread().interrupt();//如果不加这行代码，程序可能进入死循环。
    e.printStackTrace();
  }
}
~~~

#### Lock和Condition组合

实现多个条件变量控制

1. 入队和出队线程安全

2. 当队列满时,入队线程会被阻塞;当队列为空时,出队线程会被阻塞。

~~~java

public class BlockedQueue<T>{
  final Lock lock =
    new ReentrantLock();
  // 条件变量：队列不满  
  final Condition notFull =
    lock.newCondition();
  // 条件变量：队列不空  
  final Condition notEmpty =
    lock.newCondition();

  // 入队
  void enq(T x) {
    lock.lock();
    try {
      while (队列已满){
        // 等待队列不满
        notFull.await();
      }  
      // 省略入队操作...
      //入队后,通知可出队
      notEmpty.signal();
    }finally {
      lock.unlock();
    }
  }
  // 出队
  void deq(){
    lock.lock();
    try {
      while (队列已空){
        // 等待队列不空
        notEmpty.await();
      }  
      // 省略出队操作...
      //出队后，通知可入队
      notFull.signal();
    }finally {
      lock.unlock();
    }  
  }
}
~~~

#### 读写锁

1. 允许多个线程同时读共享变量；
2. 只允许一个线程写共享变量；
3. 如果一个写线程正在执行写操作，此时禁止读线程读共享变量。

~~~java

class Cache<K,V> {
  final Map<K, V> m =
    new HashMap<>();
  final ReadWriteLock rwl =
    new ReentrantReadWriteLock();
  // 读锁
  final Lock r = rwl.readLock();
  // 写锁
  final Lock w = rwl.writeLock();
  // 读缓存
  V get(K key) {
    r.lock();
    try { return m.get(key); }
    finally { r.unlock(); }
  }
  // 写缓存
  V put(K key, V value) {
    w.lock();
    try { return m.put(key, v); }
    finally { w.unlock(); }
  }
}
~~~

