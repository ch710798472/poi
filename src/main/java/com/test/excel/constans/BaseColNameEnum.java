package com.test.excel.constans;

import org.apache.commons.lang3.StringUtils;

/**
 * @author banmo
 * @create 2018-05-12
 **/
public enum BaseColNameEnum {
    //货物名称或者应税劳务名称,商品编码,商品条码,规格型号,单位,数量,单价,金额,税率,进项税,销项税,
    ITEM_NAME("货物名称或者应税劳务名称","itemName"),
    ITEM_CODE("商品编码","itemCode"),
    ITEM_BAR_CODE("商品条码","itemBarCode"),
    ITEM_TYPE("规格型号","itemType"),
    ITEM_UNIT("单位","itemUnit"),
    ITEM_NUM("数量","itemNum"),
    ITEM_PRICE("单价","itemPrice"),
    ITEM_AMOUNT("金额","itemAmount"),
    ITEM_TAX("税率","itemTax"),
    ITEM_BUY_TAX("进项税","itemBuyTax"),
    ITEM_SALES_TAX("销项税","itemSalesTax")
    ;
    private String name;
    private String code;

    BaseColNameEnum() {
    }

    BaseColNameEnum(String name, String code) {
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
    public BaseColNameEnum getBaseColNameEnum(String name, String code) {
        if (StringUtils.isBlank(code) && StringUtils.isBlank(name)) {
            return null;
        }
        if (!StringUtils.isBlank(code) && StringUtils.isBlank(name)) {
            for (BaseColNameEnum baseColNameEnum : BaseColNameEnum.values()) {
                if (baseColNameEnum.code.equals(code)) {
                    return baseColNameEnum;
                }
            }
        }

        if (!StringUtils.isBlank(name) && StringUtils.isBlank(code)) {
            for (BaseColNameEnum baseColNameEnum : BaseColNameEnum.values()) {
                if (baseColNameEnum.name.equals(name)) {
                    return baseColNameEnum;
                }
            }
        }
        if (!StringUtils.isBlank(code) && !StringUtils.isBlank(name)) {
            for (BaseColNameEnum baseColNameEnum : BaseColNameEnum.values()) {
                if (baseColNameEnum.code.equals(code) && baseColNameEnum.name.equals(name)) {
                    return baseColNameEnum;
                }
            }
        }
        return null;
    }

    public  static String getBaseFieldMapStr() {
        StringBuilder fieldMapStr = new StringBuilder();
        for (BaseColNameEnum baseColNameEnum: BaseColNameEnum.values()) {
            fieldMapStr.append(baseColNameEnum.getCode()).append(":").append(baseColNameEnum.getName()).append(",");
        }
        return fieldMapStr.substring(0,fieldMapStr.length()-1);
    }
}
