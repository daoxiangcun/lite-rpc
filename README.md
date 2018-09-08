# lite-rpc
lite-rpc是一个轻量级的rpc框架，使用java语言编写，通信层使用`Netty`来实现。用于java客户端与服务器端远程通信，是自己学习rpc过程中造的一个轮子，为简单起见，不支持其他语言。

## 描述
如项目中的名称所示，项目由六个子模块组成：  
>lite-rpc
>>rpc-client  
>>rpc-common  
>>rpc-parent  
>>rpc-server  
>>concrete-interface  
>>concrete-server

各模块的含义根据名字就可以猜出来，用户在使用时，需要实现的地方如下：  
rpc-client：客户端client接口，参考下面的`客户端`部分  
concrete-server：定义server端的具体实现  
concrete-interface：定义client与server端的接口  

## 编译
使用maven进行编译，在项目的根目录下输入: mvn clean package

## 运行
#### 准备
运行时server会注册到zookeeper上，而client从zookeeper节点上得到server的地址后连接server   
需要在以下两个文件中指定zookeeper的地址：   
concrete-server/src/test/resources/zookeeper.properties   
rpc-client/src/test/resources/zookeeper.properties   
可以在本地安装`伪zookeeper集群`，方法可参见：[zookeeper安装和配置](http://coolxing.iteye.com/blog/1871009)   

#### 启动Server
可在IDE中运行concrete-server/src/test/java目录下的ConcreteServerTest，从而启动server

#### 启动Client
可在IDE中运行rpc-client/src/test/java目录下的RpcNormalTest中的某个test case，这样就会启动client并得到执行结果。

## 传输协议
传输时序列化及反序列化使用`ProtoStuff`来进行，其原理与Thrift序列化类似，可以大大减小传输数据的大小，提高传输效率。

## 使用
下面以一个简单的示例来看一下如何使用

### 定义接口
用户自定义一个普通的java接口，如IHelloService:  
```java
public interface IHelloService {
    String hello(String name);
}
```
### 服务端
定义一个服务类，实现上面的接口，同时需要实现一个`IRpcServiceHandler`的接口（具体实现可参考concrete-server中的示例）。
需要注意的是，该服务类需要添加注解`@RpcService`
```java
@RpcService
public class HelloServiceImpl implements IHelloService, IRpcServiceHandler {
    HelloServiceImplHandler helloHandler = new HelloServiceImplHandler(this);
    
    @Override
    public String hello(String name) {
        return "hello " + name;
    }
    
    @Override
    public AbstractServiceImplHandler getServiceHandler() {
        return helloHandler;
    }
}
```

### 客户端
```java
@RunWith(JUnit4Log4jClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml"})
public class TestRpcNormal {
    protected static Logger LOGGER = LoggerFactory.getLogger(TestRpcNormal.class);

    @Autowired
    private RpcServiceProxy proxy;

    @Test
    public void testHello() throws Exception {
        IHelloService helloService = proxy.syncCreate(IHelloService.class);
        Assert.assertNotNull(helloService);
        String result = helloService.hello("123456");
        System.out.println("testHello " + result);
        Assert.assertEquals("hello 123456", result);
    }
 }
 ```
 
## 其他帮助
在各子模块中都有相关的unit test，可以作为参考来使用，其中：
> concrete-server下的test/java中ConcreteServerTest示例了如何创建一个server  
> rpc-client下的test/java中RpcNormalTest示例了怎么创建一个client去访问server  
> rpc-client下的RpcPressTest示例了使用5000个线程模拟并发对Server进行压力测试