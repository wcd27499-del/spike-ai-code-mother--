package com.spike.spikeaicodemother.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.spike.spikeaicodemother.exception.BusinessException;
import com.spike.spikeaicodemother.exception.ErrorCode;
import com.spike.spikeaicodemother.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;


/**
 * 抽象代码文件保存器-模板方法模式
 */
public abstract class CodeFileSaverTemplate<T> {
    // 文件保存根目录
    private static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";

    protected final File saveCode(T result){
        //验证是否有值
        validateInput(result);
        //构建唯一目录
        String baseDirPath = buildUniqueDir();
        //保存文件
        saveFiles(result,baseDirPath);
        //返回文件对象
        return new File(baseDirPath);
    }

    /**
     * 验证·输入参数
     * @param result
     */
    protected void validateInput(T result){
        if(result == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"代码结果对象不能为空");

        }
    }

    /**
     * 构建唯一目录路径
     * @return 目录路径
     */
    protected String buildUniqueDir(){
        String codeType = codeGenTypeEnum().getValue();
        String fileName=StrUtil.format("{}_{}",codeType,IdUtil.getSnowflakeNextIdStr());
        String dirPath=FILE_SAVE_ROOT_DIR+File.separator+fileName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 写入单个文件的方法
     * @param dirPath 目录·路径
     * @param fileName 文件名
     * @param content 文件内容
     */
    protected final void writeToFile(String dirPath, String fileName,String content){
        if (StrUtil.isNotBlank(content)){
            String filePath = dirPath+File.separator+fileName;
            FileUtil.writeString(content,filePath, StandardCharsets.UTF_8);
        }
    }

    /**
     * 保存文件的具体实现
     * @param result
     * @param baseDirPath
     */
    protected abstract void saveFiles(T result, String baseDirPath);

    /**
     * 获取代码类型
     * @return
     */
    protected abstract CodeGenTypeEnum codeGenTypeEnum();
}
