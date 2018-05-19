package com.test.excel.constans;

import org.apache.commons.lang3.StringUtils;

/**
 * @author banmo
 * @create 2018-05-12
 **/
public enum TransColNameEnum {
    //货物名称或者应税劳务名称,商品条码,规格型号,分组,税收分类编码,单位,数量,不含税单价,单价,不含税金额,金额,税额,税率,进项税,销项税
    ITEM_NAME("货物名称或者应税劳务名称","itemName"),
    ITEM_BAR_CODE("商品条码","itemBarCode"),
    ITEM_TYPE("规格型号","itemType"),
    ITEM_GROUP("分组","itemGroup"),
    ITEM_TAX_CODE("税收分类编码","itemTaxCode"),
    ITEM_UNIT("单位","itemUnit"),
    ITEM_NUM("数量","itemNum"),
    ITEM_PRICE_WITHOUT_TAX("不含税单价","itemPriceWithoutTax"),
    ITEM_PRICE("单价","itemPrice"),
    ITEM_AMOUNT_WITHOUT_TAX("不含税金额","itemAmountWithoutTax"),
    ITEM_AMOUNT("金额","itemAmount"),
    ITEM_TAX_AMOUNT("税额","itemTaxAmount"),
    ITEM_TAX("税率","itemTax"),
    ITEM_BUY_TAX("进项税","itemBuyTax"),
    ITEM_SALES_TAX("销项税","itemSalesTax")
    ;
    private String name;
    private String code;

    TransColNameEnum() {
    }

    TransColNameEnum(String name, String code) {
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
    public TransColNameEnum getTransColNameEnum(String name, String code) {
        if (StringUtils.isBlank(code) && StringUtils.isBlank(name)) {
            return null;
        }
        if (!StringUtils.isBlank(code) && StringUtils.isBlank(name)) {
            for (TransColNameEnum transColNameEnum : TransColNameEnum.values()) {
                if (transColNameEnum.code.equals(code)) {
                    return transColNameEnum;
                }
            }
        }

        if (!StringUtils.isBlank(name) && StringUtils.isBlank(code)) {
            for (TransColNameEnum transColNameEnum : TransColNameEnum.values()) {
                if (transColNameEnum.name.equals(name)) {
                    return transColNameEnum;
                }
            }
        }
        if (!StringUtils.isBlank(code) && !StringUtils.isBlank(name)) {
            for (TransColNameEnum transColNameEnum : TransColNameEnum.values()) {
                if (transColNameEnum.code.equals(code) && transColNameEnum.name.equals(name)) {
                    return transColNameEnum;
                }
            }
        }
        return null;
    }

    public  static String getTransFieldMapStr() {
        StringBuilder fieldMapStr = new StringBuilder();
        for (TransColNameEnum transColNameEnum: TransColNameEnum.values()) {
            fieldMapStr.append(transColNameEnum.getCode()).append(":").append(transColNameEnum.getName()).append(",");
        }
        return fieldMapStr.substring(0,fieldMapStr.length()-1);
    }
}
