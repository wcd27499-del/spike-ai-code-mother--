package com.spike.spikeaicodemother.core.tool;

import cn.hutool.json.JSONObject;
import com.spike.spikeaicodemother.constant.AppConstant;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 文件修改工具
 * 支持 AI 通过工具调用的方式修改文件内容
 */
@Slf4j
@Component
public class FileModifyTool extends BaseTool {

    @Tool("修改文件内容，用新的内容替换指定旧内容")
    public String modifyFile(
            @P("相对路径")
            String relativeFilePath,
            @P("替换的旧内容")
            String oldContent,
            @P("替换的新内容")
            String newContent,
            @ToolMemoryId
            Long appId
    ) {

        try {
            Path path = Paths.get(relativeFilePath);
            //如果 path 不是绝对路径，则尝试将其解析为特定项目目录下的文件。
            if (!path.isAbsolute()) {
                String projectDirName = "vue_project_" + appId;
                //项目根路径，由全局常量 AppConstant.CODE_OUTPUT_ROOT_DIR（如 /output）加上项目文件夹名构成。
                Path projectRoot = Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR, projectDirName);
                //将相对路径附加到项目根路径后面，得到完整的绝对路径。例如：/output/vue_project_123/src/index.js。
                path = projectRoot.resolve(relativeFilePath);
            }
            if (!Files.exists(path) || !Files.isRegularFile(path) ){
                return "错误：文件不存在或不是文件-"+relativeFilePath;
            }
            String originalContent = Files.readString(path);
            if (!originalContent.contains(oldContent)){
                return "警告：文件中未找到要替换的内容：文件未修改-"+relativeFilePath;
            }
            String replaced = originalContent.replace(oldContent, newContent);
            if (originalContent.equals(replaced)){
                return "信息：替换后文件内容未发生变化-"+relativeFilePath;
            }
            Files.writeString(path,replaced, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            log.info("成功修改文件：{}",path.toAbsolutePath());
            return "文件修改成功"+relativeFilePath;
        }catch (Exception e){
            String errorMessage="修改文件失败："+relativeFilePath+"错误，"+e.getMessage();
            log.error(errorMessage,e);
            return errorMessage;
        }

    }
    
    @Override
    public String getToolName() {
        return "modifyFile";
    }

    @Override
    public String getDisplayName() {
        return "修改文件";
    }

    @Override
    public String generateToolExecutedResult(JSONObject arguments) {
        String relativeFilePath = arguments.getStr("relativeFilePath");
        String oldContent = arguments.getStr("oldContent");
        String newContent = arguments.getStr("newContent");
        // 显示对比内容
        return String.format("""
                [工具调用] %s %s
                
                替换前：
                ```
                %s
                ```
                
                替换后：
                ```
                %s
                ```
                """, getDisplayName(), relativeFilePath, oldContent, newContent);
    }
}
