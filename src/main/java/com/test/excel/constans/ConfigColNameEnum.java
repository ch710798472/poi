package com.test.excel.constans;

import org.apache.commons.lang3.StringUtils;

/**
 * @author banmo
 * @create 2018-05-12
 **/
public enum ConfigColNameEnum {
    //商品条码,分组,税收分类编码
    ITEM_BAR_CODE("商品条码","itemBarCode"),
    ITEM_GROUP("分组","itemGroup"),
    ITEM_TAX_CODE("税收分类编码","itemTaxCode"),
    ITEM_TYPE("规格型号","itemType"),
    ITEM_UNIT("单位","itemUnit"),
    ITEM_TAX("税率","itemTax")
    ;
    private String name;
    private String code;

    ConfigColNameEnum() {
    }

    ConfigColNameEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ConfigColNameEnum getConfigColNameEnum(String name, String code) {
        if (StringUtils.isBlank(code) && StringUtils.isBlank(name)) {
            return null;
        }
        if (!StringUtils.isBlank(code) && StringUtils.isBlank(name)) {
            for (ConfigColNameEnum configColNameEnum : ConfigColNameEnum.values()) {
                if (configColNameEnum.code.equals(code)) {
                    return configColNameEnum;
                }
            }
        }

        if (!StringUtils.isBlank(name) && StringUtils.isBlank(code)) {
            for (ConfigColNameEnum configColNameEnum : ConfigColNameEnum.values()) {
                if (configColNameEnum.name.equals(name)) {
                    return configColNameEnum;
                }
            }
        }
        if (!StringUtils.isBlank(code) && !StringUtils.isBlank(name)) {
            for (ConfigColNameEnum configColNameEnum : ConfigColNameEnum.values()) {
                if (configColNameEnum.code.equals(code) && configColNameEnum.name.equals(name)) {
                    return configColNameEnum;
                }
            }
        }
        return null;
    }

    public  static String getConfigFieldMapStr() {
        StringBuilder fieldMapStr = new StringBuilder();
        for (ConfigColNameEnum transColNameEnum: ConfigColNameEnum.values()) {
            fieldMapStr.append(transColNameEnum.getCode()).append(":").append(transColNameEnum.getName()).append(",");
        }
        return fieldMapStr.substring(0,fieldMapStr.length()-1);
    }
}
