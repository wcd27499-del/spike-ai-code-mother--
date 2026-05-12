package com.spike.spikeaicodemother.core.parser;

import com.spike.spikeaicodemother.ai.model.HtmlCodeResult;
import com.spike.spikeaicodemother.ai.model.MultiFileCodeResult;
import com.spike.spikeaicodemother.exception.BusinessException;
import com.spike.spikeaicodemother.exception.ErrorCode;
import com.spike.spikeaicodemother.model.enums.CodeGenTypeEnum;

/**
 * 代码解析执行器
 * 根据代码生成类型执行相应的解析逻辑
 */
public class CodeParserExecutor {
    private final static HtmlCodeParser htmlCodeParser = new HtmlCodeParser();
    private final static MultiFileCodeParser multiFileCodeParser = new MultiFileCodeParser();

    public static Object executeParser(String codeContent, CodeGenTypeEnum codeGenType){
        return switch (codeGenType){
            case HTML -> htmlCodeParser.parserCode(codeContent);
            case MULTI_FILE -> multiFileCodeParser.parserCode(codeContent);
            default -> {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"不支持此类型"+codeGenType);
            }
        };
    }
}
