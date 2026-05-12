package com.spike.spikeaicodemother.core.saver;

import com.spike.spikeaicodemother.ai.model.HtmlCodeResult;
import com.spike.spikeaicodemother.ai.model.MultiFileCodeResult;
import com.spike.spikeaicodemother.exception.BusinessException;
import com.spike.spikeaicodemother.exception.ErrorCode;
import com.spike.spikeaicodemother.model.enums.CodeGenTypeEnum;

import java.io.File;

/**
 * 代码文件保存执行器
 * 根据代码生成类型执行文件保存逻辑
 */
public class CodeFileSaverExecutor {
    private final static HtmlCodeFileSaverTemplate template = new HtmlCodeFileSaverTemplate();
    private final static MultiFileCodeFileSaverTemplate multiTemplate = new MultiFileCodeFileSaverTemplate();

    public static File executeSave(Object codeResult, CodeGenTypeEnum codeGenTypeEnum) {
        return switch (codeGenTypeEnum){
            case HTML ->template.saveCode((HtmlCodeResult)codeResult);
            case MULTI_FILE -> multiTemplate.saveCode((MultiFileCodeResult)codeResult);
            default ->{
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"不支持代码的生成类型"+codeGenTypeEnum);
            }
        };
    }
}
