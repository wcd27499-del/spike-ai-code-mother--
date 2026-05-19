package com.spike.spikeaicodemother.utils;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.spike.spikeaicodemother.exception.BusinessException;
import com.spike.spikeaicodemother.exception.ErrorCode;
import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WebScreenshotUtils {

    private static WebDriver webDriver;
    private static final int DEFAULT_WIDTH = 1600;
    private static final int DEFAULT_HEIGHT = 900;

    // 单线程执行器（保证所有截图任务串行执行）
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    // 初始化 webDriver（静态块中）
    static {
        initWebDriver();
    }

    private static synchronized void initWebDriver() {
        if (webDriver == null) {
            webDriver = initChromeDriver(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        }
    }

    /**
     * 对外提供的截图方法（同步等待结果）
     * 内部通过单线程执行器排队，避免并发问题
     */
    public static String saveWebPageScreenshot(String webUrl) {
        if (StrUtil.isBlank(webUrl)) {
            log.error("网页URL不能为空");
            return null;
        }
        try {
            // 提交任务到单线程队列，并同步等待结果
            Future<String> future = executor.submit(() -> doScreenshot(webUrl));
            return future.get(60, TimeUnit.SECONDS);  // 超时60秒
        } catch (Exception e) {
            log.error("网页截图失败：{}", webUrl, e);
            return null;
        }
    }

    /**
     * 实际截图逻辑（从原方法提取）
     */
    private static String doScreenshot(String webUrl) throws Exception {
        // 检查 webDriver 是否存活，必要时重建
        checkAndReinitDriver();

        // 创建临时目录
        String rootPath = System.getProperty("user.dir") + File.separator + "tmp" + File.separator + "screenshot"
                + File.separator + UUID.randomUUID().toString().substring(0, 8);
        FileUtil.mkdir(rootPath);

        final String IMAGE_SUFFIX = ".png";
        String imageSavePath = rootPath + File.separator + RandomUtil.randomNumbers(5) + IMAGE_SUFFIX;

        webDriver.get(webUrl);
        waitForPAgeLoad(webDriver);
        byte[] screenshotBytes = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
        saveImages(screenshotBytes, imageSavePath);
        log.info("原始截图保存成功：{}", imageSavePath);

        final String COMPRESSION_SUFFIX = "_compressed.jpg";
        String compressedImagePath = rootPath + File.separator + RandomUtil.randomNumbers(5) + COMPRESSION_SUFFIX;
        compressImage(imageSavePath, compressedImagePath);
        log.info("压缩图片保存成功:{}", compressedImagePath);

        FileUtil.del(imageSavePath);
        return compressedImagePath;
    }

    /**
     * 检查驱动是否可用（防止浏览器进程意外退出）
     */
    private static void checkAndReinitDriver() {
        try {
            webDriver.getCurrentUrl();
        } catch (Exception e) {
            log.warn("WebDriver 不可用，重新初始化");
            synchronized (WebScreenshotUtils.class) {
                if (webDriver != null) {
                    webDriver.quit();
                }
                webDriver = initChromeDriver(DEFAULT_WIDTH, DEFAULT_HEIGHT);
            }
        }
    }

    @PreDestroy
    public  void destroy() {
        if (webDriver != null) {
            webDriver.quit();//关闭浏览器进程，释放系统资源
        }
        executor.shutdown();//停止接受新任务，等待已提交任务执行完成
        try {
            //等待最多 30 秒让正在执行的任务完成，
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();//如果超时，强制终止未完成的任务（会中断工作线程
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }



        /**
         * 初始化 Chrome 浏览器驱动
         */
        private static WebDriver initChromeDriver(int width, int height) {
            try {
                // 自动管理 ChromeDriver
                WebDriverManager.chromedriver().setup();
                // 配置 Chrome 选项
                ChromeOptions options = new ChromeOptions();
                // 无头模式
                options.addArguments("--headless");
                // 禁用GPU（在某些环境下避免问题）
                options.addArguments("--disable-gpu");
                // 禁用沙盒模式（Docker环境需要）
                options.addArguments("--no-sandbox");
                // 禁用开发者shm使用
                options.addArguments("--disable-dev-shm-usage");
                // 设置窗口大小
                options.addArguments(String.format("--window-size=%d,%d", width, height));
                // 禁用扩展
                options.addArguments("--disable-extensions");
                // 设置用户代理
                options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
                // 创建驱动
                WebDriver driver = new ChromeDriver(options);
                // 设置页面加载超时
                driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
                // 设置隐式等待
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                return driver;
            } catch (Exception e) {
                log.error("初始化 Chrome 浏览器失败", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "初始化 Chrome 浏览器失败");
            }
        }



    /**
     * 保存图片
     * @param imageBytes
     * @param imagePath
     */
    private static void saveImages(byte[] imageBytes,String imagePath){
        try{
            FileUtil.writeBytes(imageBytes,imagePath);
        }catch (Exception e){
            log.error("保存图片失败:{}",imageBytes,e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"保存图片失败");
        }
    }

    /**
     * 压缩图片
     * @param originalImagePath 源路径
     * @param compressedImagePath 压缩后路径
     */
    private static void compressImage(String originalImagePath,String compressedImagePath){
        //压缩图片质量(0.1=10%)
        final float COMPRESSION_QUALITY=0.3f;
        try{
            ImgUtil.compress(
                    FileUtil.file(originalImagePath),
                            FileUtil.file(compressedImagePath),
                            COMPRESSION_QUALITY
            );
        }catch (Exception e){
            log.error("图片加载失败：{}->{}",originalImagePath,compressedImagePath,e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"压缩图片失败");
        }
    }

    /**
     * 等待页面加载完成
     * @param driver
     */
    private static void waitForPAgeLoad(WebDriver driver){
        try{
            //创建等待页面加载对象
            WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(10));
            //等待document.readyState为complete
            wait.until(webDriver->((JavascriptExecutor)webDriver).executeScript("return document." +
                    "readyState")
                    .equals("complete")

            );
            //额外等待一段完成，确保动态内容加载完成
            Thread.sleep(2000);
            log.info("页面加载完成");
        }catch (Exception e){
            log.error("等待页面加载时出现异常，继续执行截图操作");
        }
    }




}
