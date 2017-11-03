/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package name.chengchao.courier.utils;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 类JsonUtils.java的实现描述：json工具类
 * 
 * @author charles 2013-1-15 下午4:23:51
 */
public class JsonUtils {

    // fastjson 的序列化配置
    public final static SerializeConfig fastjson_serializeConfig_noYear = new SerializeConfig();
    public final static SerializeConfig fastjson_serializeConfig_time = new SerializeConfig();
    public final static SerializeConfig fastjson_free_datetime = new SerializeConfig();

    // 默认打出所有属性(即使属性值为null)|属性排序输出,为了配合历史记录
    private final static SerializerFeature[] fastJsonFeatures =
        {SerializerFeature.WriteMapNullValue, SerializerFeature.WriteEnumUsingToString, SerializerFeature.SortField};

    static {
        // fastjson_serializeConfig_noYear.put(Date.class, new
        // SimpleDateFormatSerializer("MM-dd HH:mm:ss"));
    }

    public static byte[] toByte(Object object) {
        return JSON.toJSONBytes(object, fastjson_serializeConfig_noYear);
    }

    public static <T> T parseObject(byte[] item, Class<T> clazz) {
        return JSON.parseObject(item, clazz);
    }

    public static <T> T parseObject(String item, Class<T> clazz) {
        if (StringUtils.isBlank(item)) {
            return null;
        }
        return JSON.parseObject(item, clazz);
    }

    public static final <T> List<T> parseArray(String text, Class<T> clazz) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        return JSON.parseArray(text, clazz);
    }

    public static String toJsonString(Object object) {
        return toJsonString(object, fastjson_serializeConfig_noYear);
    }

    public static String toJsonString(Object object, SerializeConfig serializeConfig) {
        if (null == object) {
            return "";
        }
        return JSON.toJSONString(object, serializeConfig, fastJsonFeatures);
    }

}
