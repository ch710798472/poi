package com.test.excel.domain;

/**
 * @author banmo
 * 字段参考BaseColNameEnum
 * @create 2018-05-12
 **/
public class BaseItemDO extends BaseDO{
    private String itemName;
    private String itemCode;
    private String itemBarCode;
    private String itemType;
    private String itemUnit;
    private String itemNum;
    private String itemPrice;
    private String itemAmount;
    private String itemTax;
    private String itemBuyTax;
    private String itemSalesTax;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemBarCode() {
        return itemBarCode;
    }

    public void setItemBarCode(String itemBarCode) {
        this.itemBarCode = itemBarCode;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemUnit() {
        return itemUnit;
    }

    public void setItemUnit(String itemUnit) {
        this.itemUnit = itemUnit;
    }

    public String getItemNum() {
        return itemNum;
    }

    public void setItemNum(String itemNum) {
        this.itemNum = itemNum;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(String itemAmount) {
        this.itemAmount = itemAmount;
    }

    public String getItemTax() {
        return itemTax;
    }

    public void setItemTax(String itemTax) {
        this.itemTax = itemTax;
    }

    public String getItemBuyTax() {
        return itemBuyTax;
    }

    public void setItemBuyTax(String itemBuyTax) {
        this.itemBuyTax = itemBuyTax;
    }

    public String getItemSalesTax() {
        return itemSalesTax;
    }

    public void setItemSalesTax(String itemSalesTax) {
        this.itemSalesTax = itemSalesTax;
    }
}
