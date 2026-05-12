package com.spike.spikeaicodemother.core.parser;

/**
 * 代码解析器接口
 * @param <T>
 */
public interface CodeParser<T> {

    /**
     * 解析代码内容
     * @param contentCode 代码原始内容
     * @return 解析后的代码对象
     */
    T parserCode(String contentCode);
}
