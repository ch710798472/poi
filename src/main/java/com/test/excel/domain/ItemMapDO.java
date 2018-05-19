package com.test.excel.domain;

/**
 * @author banmo
 * @create 2018-05-16
 **/
public class ItemMapDO extends BaseDO{
    private String itemName;
    private String group;
    private String itemTaxCode;

    ItemMapDO() {
    }

    ItemMapDO(String itemName, String group, String itemTaxCode) {
        this.itemName = itemName;
        this.group = group;
        this.itemTaxCode = itemTaxCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getItemTaxCode() {
        return itemTaxCode;
    }

    public void setItemTaxCode(String itemTaxCode) {
        this.itemTaxCode = itemTaxCode;
    }
}
