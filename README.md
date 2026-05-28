###  spike-ai-code-mother


#### 介绍
ai零代码网页生成平台学习版，
支持HTML文件和前端三剑客（html,css,javascript）文件,还有vue文件生成，
并同步展示代码页面（由于经费有限生成代码量不能太大）并且支持部署功能。
以及一些应用管理和用户管理和ai对话记忆功能（这些仅支持管理员）。

#### 软件架构


1. 后端  springboot+Mybatis-Flux+WebFlux
1. 缓存  Redis+Spring Cache+Caffeine
1. 流处理  Reactor Flux+SSE
1. 截图  Selenuim ChromeDriver+图片压缩
1. 对象存储 腾讯云COS
1. 前端  Vue3+TypeScript+Ant Design Vue 4
1. 部署 Nginx+OpenResty+HTTPS

 #### 架构图
1. 系统全景架构图
 ![系统全景架构图](https://foruda.gitee.com/images/1779969077976732533/72cf5152_16002479.png "屏幕截图")
1. ai对话和代码核心生成流程
 ![ai对话和代码核心生成流程](https://foruda.gitee.com/images/1779969302906962031/db280c14_16002479.png "屏幕截图")
1. 部署流程
 ![部署流程](https://foruda.gitee.com/images/1779969593848586309/00e16391_16002479.png "屏幕截图")
1. AI服务实例生命周期
 ![AI服务实例生命周期](https://foruda.gitee.com/images/1779969767333842409/3e4a1194_16002479.png "屏幕截图")
1. 部署链路流程
 ![部署链路流程](https://foruda.gitee.com/images/1779970107725864766/76dff1c0_16002479.png "屏幕截图")
1. 限流和权限拦截链路
 ![限流和权限拦截链路](https://foruda.gitee.com/images/1779970450259832166/ae9826cd_16002479.png "屏幕截图")
 #### 项目概览
1. 网址主页
 ![网址主页](https://foruda.gitee.com/images/1779971012735186039/fd4a820e_16002479.png "屏幕截图")
1. 用户对话页面以及对话历史
 ![用户对话页面以及对话历史](https://foruda.gitee.com/images/1779971137504800269/3bc15b56_16002479.png "屏幕截图")
1. 部署成功时
 ![部署成功时](https://foruda.gitee.com/images/1779971262942622092/10cd2f7a_16002479.png "屏幕截图")







## 项目亮点

---

### 1. Spring Boot AOP 权限校验

```java
@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
public BaseResponse<Boolean> deleteApp(...) { ... }

// AuthInterceptor.java
@Around("@annotation(authCheck)")
public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
    User loginUser = userService.getLoginUser(request);
    UserRoleEnum userRole = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
    if (userRole == null) throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    return joinPoint.proceed();
}
```

// 使用Aop切面编程验证该接口是否有权限被访问

---

### 2. SSE 流式传输拼接

```java
Flux<String> contentFlux = appService.chatToGenCode(appId, message, loginUser);

// 调用服务生成代码（流式）
return contentFlux.map(chunk->{
    //
    Map<String, String> wrapper = Map.of("d", chunk);
    String jsonData = JSONUtil.toJsonStr(wrapper);
    return ServerSentEvent.<String>builder().data(jsonData).build();
});
```

// 将ai生成的结果流式封装成key为"d"的map类型并转化成jsonStr型方便前端进行拼接以免空格字符消失

---

### 3. 策略模式流处理器

```java
// AiCodeGeneratorFacade.java — 工厂分发
public Flux<String> generateAndSaveCodeStream(...) {
    return switch (codeGenTypeEnum) {
        case HTML, MULTI_FILE -> processCodeStream(aiCodeGeneratorService.generateHtmlCodeStream(...));
        case VUE_PROJECT -> processTokenStream(aiCodeGeneratorService.generateVueProjectCodeStream(appId, ...));
    };
}

// StreamHandlerExecutor.java — 策略选择
public Flux<String> doExecute(...) {
    return switch (codeGenType) {
        case VUE_PROJECT -> jsonMessageStreamHandler.handle(...);
        case HTML, MULTI_FILE -> new SimpleTextStreamHandler().handle(...);
    };
}
```

// 根据生成代码类型重定向到对应的处理器

---

### 4. appId 隔离对话记忆

```java
// AiCodeGenerateServiceFactory.java — appId 隔离对话记忆
MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
    .id(appId)                         // 按 appId 隔离
    .chatMemoryStore(redisChatMemoryStore) // Redis 持久化
    .maxMessages(20)
    .build();

chatHistoryService.loadChatHistoryToMemory(appId, chatMemory, 10); // 从DB恢复最近10条
```

// 用户每生成一个应用会分配一个对话记忆，使每个应用对话记忆都是独立的

---

### 5. Caffeine 三级缓存 + Spring Cache

```java
// Caffeine — AI 实例缓存
Cache<String, AiCodeGenerateService> serviceCache = Caffeine.newBuilder()
    .maximumSize(1000)
    .expireAfterWrite(Duration.ofMinutes(30))
    .expireAfterAccess(Duration.ofMinutes(10))
    .build();

MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
    .id(appId)
    .chatMemoryStore(redisChatMemoryStore)
    .maxMessages(20)
    .build();

// 从数据库加载历史对话到记忆
chatHistoryService.loadChatHistoryToMemory(appId, chatMemory, 10);
```

// 如果用户创建一个应用首先查询缓存有没有如果有则从缓存返回对话历史和该应用数据(缓存命中)
// 如果缓存不存在则会向下查询redis其次才是数据库有则会把数据重新加载到缓存中
// 如果都没有则会创建一个AI服务实例和chatmodel

```java
// Spring Cache — 精选列表缓存
@Cacheable(value = "good_app_page", key = "...", condition = "#appQueryRequest.pageNum<=10")
@CacheEvict(value = "good_app_page", allEntries = true)  // 删除时清除
```

// 利用SpringCache来构建缓存把精选应用放入缓存中，减少查询负担

---

### 6. Java 21 虚拟线程异步截图

```java
Thread.startVirtualThread(() -> {
    String screenshotUrl = screenshotService.generateAndUploadScreenshot(appDeployUrl);
    App updateApp = new App(); updateApp.setId(appId); updateApp.setCover(screenshotUrl);
    this.updateById(updateApp);
});
```

// 利用Java21特性异步实现截图防止用户忙等

---

### 7. 单线程执行器保护 ChromeDriver

```java
// 单线程执行器（保证所有截图任务串行执行）
private static final ExecutorService executor = Executors.newSingleThreadExecutor();
// 提交任务到单线程队列，并同步等待结果
Future<String> future = executor.submit(() -> doScreenshot(webUrl));
return future.get(60, TimeUnit.SECONDS);  // 超时60秒
```

// 将截图请求放在单线程执行器中执行，两个线程同时调 webDriver.get(url) 会互相覆盖页面，截图就错乱了

---

### 8. Redisson 分布式限流

```java
// 使用只需一行
@RateLimit(limitType = RateLimitType.USER, rate = 5, rateInterval = 60, message = "太频繁了")

// RateLimitAspect.java — AOP 切面核心
@Before("@annotation(rateLimit)")
public void doBefore(JoinPoint joinPoint, RateLimit rateLimit) {
    RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
    rateLimiter.trySetRate(RateType.OVERALL, rateLimit.rate(), rateLimit.rateInterval(), RateIntervalUnit.SECONDS);
    if (!rateLimiter.tryAcquire(1)) {
        throw new BusinessException(ErrorCode.TOO_MANY_REQUEST, rateLimit.message());
    }
}
```

// 根据Redisson令牌桶防止用户恶意刷取token，一分钟最多请求5次

---

### 9. Prompt 注入防护 — LangChain4j Guardrail

```java
// PromptSafetyInputGuardrail.java
private static final List<Pattern> INJECTION_PATTERNS = Arrays.asList(
    Pattern.compile("(?i)ignore\\s+(?:previous|above|all)\\s+(?:instructions?|commands?)"),
    Pattern.compile("(?i)system\\s*:\\s*you\\s+are"),
    Pattern.compile("(?i)new\\s+(?:instructions?|commands?|prompts?)\\s*:")
);
```

// 限制一些敏感词防止用户越狱获取系统提示词







### 标题1. - 1. 这里是列表文本
