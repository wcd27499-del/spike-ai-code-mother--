package com.spike.spikeaicodemother.core.saver;

import cn.hutool.core.util.StrUtil;
import com.spike.spikeaicodemother.ai.model.HtmlCodeResult;
import com.spike.spikeaicodemother.exception.BusinessException;
import com.spike.spikeaicodemother.exception.ErrorCode;
import com.spike.spikeaicodemother.model.enums.CodeGenTypeEnum;

import java.io.File;

public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult> {

    @Override
    protected void saveFiles(HtmlCodeResult result, String baseDirPath) {

        this.writeToFile(baseDirPath,"index.html",result.getHtmlCode());
    }

    @Override
    protected CodeGenTypeEnum codeGenTypeEnum() {
        return CodeGenTypeEnum.HTML;
    }

    protected void validateInput(HtmlCodeResult result){
        super.validateInput(result);
        //代码不能为空
        if (StrUtil.isBlank(result.getHtmlCode())){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"HTML代码内不能为空");
        }
    }
}
