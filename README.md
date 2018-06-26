# lite-rpc
lite-rpc是一个轻量级的rpc框架，使用java语言编写，通信层使用`Netty`来实现。用于java客户端与服务器端远程通信，为简单起见，不支持其他语言。  

## 编译
使用maven进行编译，在项目的根目录下输入:  
mvn clean package

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

    @BeforeClass
    public static void init() {
        System.setProperty("zookeeper.host", "staging");
        LOGGER.info("init finish");
    }
    
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
 
### 其他帮助
在各子模块中都有相关的unit test，可以作为参考来使用，其中：
> concrete-server下的test/java中ConcreteServerTest示例了如何创建一个server  
> rpc-client下的test/java中RpcNormalTest示例了怎么创建一个client去访问server  
> rpc-client下的RpcPressTest示例了使用3000个线程模拟并发对Server进行压力测试