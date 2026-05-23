package com.spike.spikeaicodemother.utils;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;

/**
 * 缓存key生成工具
 */
public class CacheKeyUtils {

    /**
     * 根据对象生成key的对象
     * @param obj 要生成key的对象
     * @return md5哈希后的缓存key
     */
    public static String generateKey(Object obj){
        if(obj == null){
            return DigestUtil.md5Hex("null");
        }
        //先转json
        String jsonStr = JSONUtil.toJsonStr(obj);
        return DigestUtil.md5Hex(jsonStr);
    }

}
