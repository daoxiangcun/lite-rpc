# lite-rpc
lite-rpc是一个轻量级的rpc框架，使用java语言编写，通信层使用`Netty`来实现。用于java客户端与服务器端远程通信，为简单起见，不支持其他语言。  

## 编译
使用maven进行编译，在项目的根目录下输入:  
mvn clean package

## 描述
如项目中的名称所示，项目由四子模块组成：  
>lite-rpc
>>rpc-client  
>>rpc-common  
>>rpc-parent  
>>rpc-server  
其中的含义根据名字就可以猜出来，客户端使用rpc-client，服务器端使用rpc-server

## 使用
下面以一个简单的示例来看一下如何使用

### 定义接口
在rpc-common中定义一个接口，如IHelloService:  
```java
public interface IHelloService {
    String hello(String name);
}
```
### 服务端
定义一个服务类，实现上面的接口，需要注意的是，该服务类需要添加注解`@RpcService`
```java
@RpcService
public class HelloServiceImpl implements IHelloService {
    @Override
    public String hello(String name) {
        return "hello " + name;
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
    public void testHelloOneTime() throws Exception {
        IHelloService helloService = proxy.syncCreate(IHelloService.class);
        Assert.assertNotNull(helloService);
        String result = helloService.hello("123456");
        System.out.println("testHello " + result);
    }
 }
 ```
