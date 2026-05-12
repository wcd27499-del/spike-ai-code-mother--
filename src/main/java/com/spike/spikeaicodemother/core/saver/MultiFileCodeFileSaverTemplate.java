package com.spike.spikeaicodemother.core.saver;

import cn.hutool.core.util.StrUtil;
import com.spike.spikeaicodemother.ai.model.MultiFileCodeResult;
import com.spike.spikeaicodemother.exception.BusinessException;
import com.spike.spikeaicodemother.exception.ErrorCode;
import com.spike.spikeaicodemother.model.enums.CodeGenTypeEnum;

public class MultiFileCodeFileSaverTemplate extends CodeFileSaverTemplate<MultiFileCodeResult> {
    @Override
    protected void saveFiles(MultiFileCodeResult result, String baseDirPath) {
        this.writeToFile(baseDirPath,"index.html",result.getHtmlCode());
        this.writeToFile(baseDirPath,"style.css",result.getCssCode());
        this.writeToFile(baseDirPath,"script.js",result.getJsCode());
    }

    @Override
    protected CodeGenTypeEnum codeGenTypeEnum() {
        return CodeGenTypeEnum.MULTI_FILE;
    }
    protected void validateInput(MultiFileCodeResult result) {
        super.validateInput(result);
        //至少html不能为空，css，js可以为空
        if(StrUtil.isBlank(result.getHtmlCode())){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"html内容不能为空");
        }
    }
}
