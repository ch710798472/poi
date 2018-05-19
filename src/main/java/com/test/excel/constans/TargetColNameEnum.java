package com.test.excel.constans;

import org.apache.commons.lang3.StringUtils;

/**
 * @author banmo
 * @create 2018-05-12
 **/
public enum TargetColNameEnum {
    //序号,货物或应税劳务、服务名称,计量单位,规格型号,数量,金额,税率,商品税目,折扣金额,税额,折扣税额,折扣率,单价,价格方式,
    //税收分类编码版本号,税收分类编码,企业商品编码,使用优惠政策标识,零税率标识,优惠政策说明,中外合作油气田标识
    ITEM_ID("序号","itemId"),
    ITEM_NAME("货物或应税劳务、服务名称","itemName"),
    ITEM_UNIT_TYPE("计量单位","itemUnitType"),
    ITEM_TYPE("规格型号","itemType"),
    ITEM_NUM("数量","itemNum"),
    ITEM_AMOUNT("金额","itemAmount"),
    ITEM_TAX("税率","itemTax"),
    ITEM_CAT("商品税目","itemTaxCat"),
    ITEM_DISCOUNT_AMOUNT("折扣金额","itemDiscountAmount"),
    ITEM_TAX_AMOUNT("税额","itemTaxAmount"),
    ITEM_DISCOUNT_TAX_AMOUNT("折扣税额","itemDiscountTaxAmount"),
    ITEM_DISCOUNT_RATE("折扣率","itemDiscountRate"),
    ITEM_PRICE("单价","itemPrice"),
    ITEM_PRICE_TYPE("价格方式","itemPriceType"),
    ITEM_TAX_CODE_VERSION("税收分类编码版本号","itemTaxCodeVersion"),
    ITEM_TAX_CODE("税收分类编码","itemTaxCode"),
    COMPANY_ITEM_CODE("企业商品编码","companyItemCode"),
    ITEM_DISCOUNT_CODE("使用优惠政策标识","itemDiscountCode"),
    ITEM_ZERO_TAX("零税率标识","itemZeroTax"),
    ITEM_DISCOUNT_DESC("优惠政策说明","itemDiscountDesc"),
    ITEM_GAS_FLAG("中外合作油气田标识","itemGasFlag")
    ;
    private String name;
    private String code;

    TargetColNameEnum() {
    }

    TargetColNameEnum(String name, String code) {
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
    public TargetColNameEnum getTargetColNameEnum(String name, String code) {
        if (StringUtils.isBlank(code) && StringUtils.isBlank(name)) {
            return null;
        }
        if (!StringUtils.isBlank(code) && StringUtils.isBlank(name)) {
            for (TargetColNameEnum targetColNameEnum : TargetColNameEnum.values()) {
                if (targetColNameEnum.code.equals(code)) {
                    return targetColNameEnum;
                }
            }
        }

        if (!StringUtils.isBlank(name) && StringUtils.isBlank(code)) {
            for (TargetColNameEnum targetColNameEnum : TargetColNameEnum.values()) {
                if (targetColNameEnum.name.equals(name)) {
                    return targetColNameEnum;
                }
            }
        }
        if (!StringUtils.isBlank(code) && !StringUtils.isBlank(name)) {
            for (TargetColNameEnum targetColNameEnum : TargetColNameEnum.values()) {
                if (targetColNameEnum.code.equals(code) && targetColNameEnum.name.equals(name)) {
                    return targetColNameEnum;
                }
            }
        }
        return null;
    }

    public  static String getTargetFieldMapStr() {
        StringBuilder fieldMapStr = new StringBuilder();
        for (TargetColNameEnum targetColNameEnum: TargetColNameEnum.values()) {
            fieldMapStr.append(targetColNameEnum.getCode()).append(":").append(targetColNameEnum.getName()).append(",");
        }
        return fieldMapStr.substring(0,fieldMapStr.length()-1);
    }
}
