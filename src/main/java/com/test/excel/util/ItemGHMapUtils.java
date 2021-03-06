package com.test.excel.util;

import com.google.common.collect.Maps;
import com.test.excel.domain.ConfigDO;
import com.test.excel.domain.ItemMapDO;
import com.test.excel.domain.TransItemDO;

import java.util.Map;
import java.util.Optional;

/**
 * @author banmo
 * @create 2018-05-16
 **/
public class ItemGHMapUtils {
    private  Map<String, TransItemDO> configDOMap = Maps.newHashMap();

    public  TransItemDO getConfigDO(String key) {
        return Optional.ofNullable(configDOMap.get(key)).orElse(null);
    }

    public void putConfigDO(String key, TransItemDO o) {
        if (null == key || null == o) {
            return;
        }
        configDOMap.put(fixedBarCode(key), o);
    }

    public static String fixedBarCode(String barCode) {
        return barCode.replaceAll("^(0+)", "");
    }
}
