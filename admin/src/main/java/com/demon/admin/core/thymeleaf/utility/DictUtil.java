package com.demon.admin.core.thymeleaf.utility;

import com.demon.admin.core.utils.EhCacheUtil;
import com.demon.admin.system.domain.Dict;
import com.demon.admin.system.service.DictService;
import com.demon.core.utils.SpringContextUtil;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: oneperfect
 * @Date: 2019/4/10
 */
public class DictUtil {

    private static Cache dictCache = EhCacheUtil.getDictCache();

    /**
     * 获取字典值集合
     * @param label 字典标识
     */
    public static Map<String, String> value(String label) {
        Map<String, String> value = null;
        Element dictEle = dictCache.get(label);
        if(dictEle != null) {
            value = (Map<String, String>) dictEle.getObjectValue();
        } else {
            DictService dictService = SpringContextUtil.getBean(DictService.class);
            Dict dict = dictService.findByName(label);
            if(dict != null) {
                String dictValue = dict.getValue();
                String[] dictValues = dictValue.split(",");
                value = new LinkedHashMap<>();
                for (String dvs : dictValues) {
                    String[] dv = dvs.split(":");
                    if(dv.length > 1) {
                        value.put(dv[0], dv[1]);
                    }
                }
                dictCache.put(new Element(dict.getName(), value));
            }
        }
        return value;
    }

    /**
     * 根据选项编码获取选项值
     * @param label 字典标识
     * @param code 选项编码
     */
    public static String keyValue(String label, String code){
        Map<String, String> list = DictUtil.value(label);
        if(list != null){
            return list.get(code);
        }else{
            return "";
        }
    }

    /**
     * 清除缓存中指定的数据
     * @param label 字典标识
     */
    public static void clearCache(String label){
        Element dictEle = dictCache.get(label);
        if (dictEle != null){
            dictCache.remove(label);
        }
    }
}