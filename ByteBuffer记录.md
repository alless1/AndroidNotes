#### 测试日志记录

~~~java
 printBuffer: position=0,remaining=1024,limit=1024,capacity=1024
 onClick: 写入10字节                                                     
 printBuffer: position=10,remaining=1014,limit=1024,capacity=1024    
 onClick: mark                                                       
 printBuffer: position=10,remaining=1014,limit=1024,capacity=1024    
 onClick: flip                                                       
 printBuffer: position=0,remaining=10,limit=10,capacity=1024         
 onClick: 读取5字节                                                      
 printBuffer: position=5,remaining=5,limit=10,capacity=1024          
 onClick: compact                                                    
 printBuffer: position=5,remaining=1019,limit=1024,capacity=1024     
 onClick: 写入10字节                                                     
 printBuffer: position=15,remaining=1009,limit=1024,capacity=1024    
~~~

#### 变量关系

* capactity：分配的容量，不会变。
* limit：可用大小，可能会变。
* position：某个字节的位置（length），随着读写变化。
* remaining：剩余字节数量，和position相对应，position+remaining=limit。

#### 切换动作

> 执行切换动作以后，mark会被设为-1（mark初始值也为-1），执行reset会出错，InvalidMarkException。

开始读取前执行一次。

* flip：执行逻辑，remaining = position，limit = remaining，position = 0

  ~~~java
  onClick: 写入10字节
  printBuffer: position=10,remaining=90,limit=100,capacity=100
  onClick: flip
  printBuffer: position=0,remaining=10,limit=10,capacity=100
  onClick: flip
  printBuffer: position=0,remaining=0,limit=0,capacity=100
  onClick: flip
  printBuffer: position=0,remaining=0,limit=0,capacity=100
  ~~~

读取结束，开始写入前执行一次。

* compact：执行逻辑，先将remaining的数值赋值给position（相当于将剩余的数据量前移），再将limit-position赋值给remaining。

  ~~~java
  onClick: 写入10字节
  printBuffer: position=10,remaining=90,limit=100,capacity=100
  onClick: compact
  printBuffer: position=90,remaining=10,limit=100,capacity=100
  onClick: compact
  printBuffer: position=10,remaining=90,limit=100,capacity=100
  onClick: compact
  printBuffer: position=90,remaining=10,limit=100,capacity=100
  ~~~

  ~~~java
  onClick: 读取5字节
  printBuffer: position=10,remaining=0,limit=10,capacity=100
  onClick: compact
  printBuffer: position=0,remaining=100,limit=100,capacity=100
  onClick: compact
  printBuffer: position=100,remaining=0,limit=100,capacity=100
  onClick: compact
  printBuffer: position=0,remaining=100,limit=100,capacity=100
  ~~~

#### 读写动作

> position超出limit会出错，BufferOverflowException

* put：position = position + 写入的字节数	, remaining = limit - position

  ~~~java
  onClick: 写入10字节
  printBuffer: position=10,remaining=90,limit=100,capacity=100
  onClick: 写入10字节
  printBuffer: position=20,remaining=80,limit=100,capacity=100
  onClick: 写入10字节
  printBuffer: position=30,remaining=70,limit=100,capacity=100
  onClick: 写入10字节
  printBuffer: position=40,remaining=60,limit=100,capacity=100
  ~~~

*  get：position = position + 读取的字节数	,remaining = limit - position

  ~~~java
  onClick: 读取5字节
  printBuffer: position=5,remaining=95,limit=100,capacity=100
  onClick: 读取5字节
  printBuffer: position=10,remaining=90,limit=100,capacity=100
  onClick: 读取5字节
  printBuffer: position=15,remaining=85,limit=100,capacity=100
  onClick: 读取5字节
  printBuffer: position=20,remaining=80,limit=100,capacity=100
  ~~~

**读写之前，都需要判断remaining，remaining不够读写的大小就会出错。**

#### 使用方式

> 写入数据，切换方式，读取所有数据。

~~~java
onClick: 写入10字节
printBuffer: position=10,remaining=90,limit=100,capacity=100
onClick: flip
printBuffer: position=0,remaining=10,limit=10,capacity=100
onClick: 读取5字节
printBuffer: position=5,remaining=5,limit=10,capacity=100
onClick: 读取5字节
printBuffer: position=10,remaining=0,limit=10,capacity=100
~~~

> 