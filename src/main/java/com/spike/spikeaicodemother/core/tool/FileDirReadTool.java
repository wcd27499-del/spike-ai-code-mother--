package com.spike.spikeaicodemother.core.tool;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.spike.spikeaicodemother.constant.AppConstant;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

/**
 * 文件目录读取工具
 * 使用 Hutool 简化文件操作
 */
@Slf4j
@Component
public class FileDirReadTool extends BaseTool {
    /**
     * 需要忽略的文件和目录
     */
    private static final Set<String> IGNORED_NAMES = Set.of(
            "node_modules", ".git", "dist", "build", ".DS_Store",
            ".env", "target", ".mvn", ".idea", ".vscode", "coverage"
    );

    /**
     * 需要忽略的文件扩展名
     */
    private static final Set<String> IGNORED_EXTENSIONS = Set.of(
            ".log", ".tmp", ".cache", ".lock"
    );

    @Tool("读取目录结构，获取指定目录下的所有文件和子目录信息")
    public String readDir(
            @P("目录的相对路径，为空则读取整个项目结构")
            String relativeDirPath,
            @ToolMemoryId Long appId
    ) {
        try {
            Path path = Paths.get(relativeDirPath == null ? "" : relativeDirPath);
            if (!path.isAbsolute()) {
                String projectDirName = "vue_project_" + appId;
                Path projectRoot = Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR, projectDirName);
                path = projectRoot.resolve(relativeDirPath);
            }
            File targetFile = path.toFile();
            if (!targetFile.exists() || !targetFile.isDirectory()) {
                return "错误：目录不存在或不是目录" + relativeDirPath;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("项目目录结构：\n");
            //使用hutool递归获取所有文件
            List<File> allFiles = FileUtil.loopFiles(targetFile, file -> !shouldIgnore(file.getName()));
            //按路径深度和名称排序显示
            allFiles.stream()
                    //首先比较两个文件相对于 targetFile 的深度（即目录层数）。
                    // 例如：targetFile 为 root，文件 root/a/b.txt 的深度为 2（a 和 b.txt）。深度小的排在前面（浅层文件先出现）。
                    .sorted((f1, f2) -> {
                        int depth1 = getRelativeDepth(targetFile, f1);
                        int depth2 = getRelativeDepth(targetFile, f2);
                        if (depth1 != depth2) {
                            //
                            return Integer.compare(depth1, depth2);
                        }
                        //按文件路径的字典序（lexicographic order）比较两个 File 对象。
                        return f1.getPath().compareTo(f2.getPath());
                    })
                    //遍历排序后的文件列表，对每个文件：
                    //再次计算其深度（depth）。
                    //生成由 depth 个空格组成的缩进字符串
                    //将缩进和文件名追加到 stringBuilder 中
                    .forEach(file -> {
                        int depth = getRelativeDepth(targetFile, file);
                        String indent = " ".repeat(depth);
                        stringBuilder.append(indent).append(file.getName());
                    });
            return stringBuilder.toString();

        } catch (Exception e) {
            String errorMessage = "读取目录结构失败: " + relativeDirPath + ", 错误: " + e.getMessage();
            log.error(errorMessage, e);
            return errorMessage;
        }
    }

    /**
     * 计算文件相对于根目录的深度
     * @param root
     * @param file
     * @return
     */
        private int getRelativeDepth (File root, File file){
            Path rootPath = root.toPath();
            Path filePath = file.toPath();
            return rootPath.relativize(filePath).getNameCount() - 1;
        }

    /**
     * 判断是否应该忽略该文件或目录
     * @param fileName
     * @return
     */
        private boolean shouldIgnore (String fileName){
            if (IGNORED_NAMES.contains(fileName)) {
                return true;
            }
            return IGNORED_EXTENSIONS.stream().anyMatch(fileName::endsWith);
        }


        @Override
        public String getToolName () {
            return "readDir";
        }

        @Override
        public String getDisplayName () {
            return "读取目录";
        }

        @Override
        public String generateToolExecutedResult (JSONObject arguments){
            String relativeDirPath = arguments.getStr("relativeDirPath");
            if (StrUtil.isEmpty(relativeDirPath)) {
                relativeDirPath = "根目录";
            }
            return String.format("[工具调用] %s %s", getDisplayName(), relativeDirPath);
        }

}