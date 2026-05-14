package com.spike.spikeaicodemother.core;

import com.jfinal.template.stat.ast.Case;
import com.jfinal.template.stat.ast.Switch;
import com.spike.spikeaicodemother.ai.AiCodeGenerateService;
import com.spike.spikeaicodemother.ai.model.HtmlCodeResult;
import com.spike.spikeaicodemother.ai.model.MultiFileCodeResult;
import com.spike.spikeaicodemother.core.parser.CodeParserExecutor;
import com.spike.spikeaicodemother.core.saver.CodeFileSaverExecutor;
import com.spike.spikeaicodemother.exception.BusinessException;
import com.spike.spikeaicodemother.exception.ErrorCode;
import com.spike.spikeaicodemother.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * AI 代码生成外观类，组合生成和保存功能
 */
@Service
@Slf4j
public class AiCodeGeneratorFacade {

    @Resource
    private AiCodeGenerateService aiCodeGeneratorService;

    /**
     *
     * @param codeStream
     * @param codeGenType
     * @param appId 应用id
     * @return
     */
    private Flux<String> processCodeStream(Flux<String> codeStream,CodeGenTypeEnum codeGenType,Long appId) {
        StringBuilder sb = new StringBuilder();
        return codeStream.doOnNext(chunk->{
            //将流代码拼接
            sb.append(chunk);
        }).doOnComplete(()->{
            //完成后进行保存在文件中
            try{
                String completeCode = sb.toString();
                //使用解析器解析代码
                Object parserResult = CodeParserExecutor.executeParser(completeCode, codeGenType);
                //使用执行器保存代码
                File file = CodeFileSaverExecutor.executeSave(parserResult, codeGenType,appId);
                log.info("保存成功，；路径为：",file.getAbsolutePath());
            }catch (Exception e){
                log.error("保存失败",e.getMessage());
            }
        });

    }

    /**
     * 统一入口：根据类型生成并保存代码
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @param appId 应用id
     * @return 保存的目录
     */
    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum,Long appId) {

        if (codeGenTypeEnum==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数不能为空");
        }
        return switch (codeGenTypeEnum){
            case HTML -> {
                HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(userMessage);
                yield CodeFileSaverExecutor.executeSave(result,CodeGenTypeEnum.HTML,appId);
            }
            case MULTI_FILE -> {
                MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode(userMessage);
                yield CodeFileSaverExecutor.executeSave(result,CodeGenTypeEnum.MULTI_FILE,appId);
            }
            default -> {
                String errorMessage=codeGenTypeEnum.getValue()+"不支持生成类型";

                throw new BusinessException(ErrorCode.SYSTEM_ERROR,errorMessage);
            }
        };
        }
    /**
     * 统一入口：根据类型生成并保存代码（流式）
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @param appId 应用id
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum,Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> {
                Flux<String> resultFlux = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
                yield this.processCodeStream(resultFlux, codeGenTypeEnum,appId);

            }
            case MULTI_FILE -> {
                Flux<String> resultFlux = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
                yield this.processCodeStream(resultFlux, codeGenTypeEnum,appId);
            }
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }

//    /**生成多文件流式的代码并保存（流式）
//     *
//     * @param userMessage
//     * @return
//     */
//    private Flux<String> generateAndSaveMultiFileCodeStream(String userMessage) {
//
//        Flux<String> result = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
//        //当流式返回代码完成后，再保存代码
//        StringBuilder sb = new StringBuilder();
//        return result.doOnNext(chunk->{
//            //将流代码拼接
//            sb.append(chunk);
//        }).doOnComplete(()->{
//            //完成后进行保存在文件中
//            try{
//                String completeMultiCode = sb.toString();
//                MultiFileCodeResult MultiCodeResult = CodeParser.parseMultiFileCode(completeMultiCode);
//                //
//                File savedDir = CodeFileSaver.saveMultiFileCodeResult(MultiCodeResult);
//                log.info("保存成功，路径为："+savedDir.getAbsolutePath());
//
//            }catch (Exception e){
//                log.error(e.getMessage(),e);
//            }
//        });
//    }
//
//    /**
//     * 生成HTML流式的代码并保存（流式）
//     * @param userMessage
//     * @return
//     */
//    private Flux<String> generateAndSaveHtmlCodeStream(String userMessage) {
//        Flux<String> result = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
//        //当流式返回代码完成后，再保存代码
//        StringBuilder sb = new StringBuilder();
//        return result.doOnNext(chunk->{
//            //将流代码拼接
//            sb.append(chunk);
//        }).doOnComplete(()->{
//            //完成后进行保存在文件中
//            try{
//                String completeHtmlCode = sb.toString();
//                HtmlCodeResult htmlCodeResult = CodeParser.parseHtmlCode(completeHtmlCode);
//                //
//                File savedDir = CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
//                log.info("保存成功，路径为："+savedDir.getAbsolutePath());
//
//            }catch (Exception e){
//                log.error(e.getMessage(),e);
//            }
//        });
//
//    }
//
//    /**
//     * 生成 HTML 模式的代码并保存
//     *
//     * @param userMessage 用户提示词
//     * @return 保存的目录
//     */
//    private File generateAndSaveHtmlCode(String userMessage) {
//        HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(userMessage);
//        return CodeFileSaver.saveHtmlCodeResult(result);
//    }
//
//
//        /**
//         * 生成多文件模式的代码并保存
//         *
//         * @param userMessage 用户提示词
//         * @return 保存的目录
//         */
//        private File generateAndSaveMultiFileCode(String userMessage) {
//
//            MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode(userMessage);
//            return CodeFileSaver.saveMultiFileCodeResult(result);
//        }
}



