# rpc
 rpc

1.通用性
反射+动态代理

2、集成Spring
在实现了代理对象通用化之后，下一步就可以考虑集成Spring的IOC功能了，通过Spring来创建代理对象，这一点就需要对Spring的bean初始化有一定掌握了。
step1：扫描所有需要代理的类类型（可以通过Reflections包实现）
step2：遍历并创建代理类的BeanDefinition
step3：通过 defaultListableBeanFactory.registerBeanDefinition 将代理类的BeanDefinition注册到Spring容器中

3、长连接or短连接
总不能每次要调用RPC接口时都去开启一个Socket建立连接吧？是不是可以保持若干个长连接，然后每次有rpc请求时，把请求放到任务队列中，然后由线程池去消费执行？只是一个思路，后续可以参考一下Dubbo是如何实现的。

4、 服务端线程池
我们现在的Server端，是单线程的，每次都要等一个请求处理完，才能去accept另一个socket的连接，这样性能肯定很差，是不是可以通过一个线程池，来实现同时处理多个RPC请求？同样只是一个思路。

5、服务注册中心
正如之前提到的，要调用服务，首先你需要一个服务注册中心，告诉你对方服务都有哪些实例。Dubbo的服务注册中心是可以配置的，官方推荐使用Zookeeper。如果使用Zookeeper的话，要怎样往上面注册实例，又要怎样获取实例，这些都是要实现的。

6、负载均衡
如何从多个实例里挑选一个出来，进行调用，这就要用到负载均衡了。负载均衡的策略肯定不只一种，要怎样把策略做成可配置的？又要如何实现这些策略？同样可以参考Dubbo，Dubbo - 负载均衡

7、结果缓存
每次调用查询接口时都要真的去Server端查询吗？是不是要考虑一下支持缓存？

8、多版本控制
服务端接口修改了，旧的接口怎么办？

9、异步调用
客户端调用完接口之后，不想等待服务端返回，想去干点别的事，可以支持不？

10、优雅停机
服务端要停机了，还没处理完的请求，怎么办？
