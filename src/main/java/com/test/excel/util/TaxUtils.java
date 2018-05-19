package com.test.excel.util;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * @author banmo
 * @create 2018-05-16
 **/
public class TaxUtils {
    public static int TAX_ROUNDING_MODE = BigDecimal.ROUND_HALF_UP;
    public static int TAX_POINT = 8;

    public static String getTaxAmount(String taxRate, String amount) {
        BigDecimal amountBD = new BigDecimal(amount);
        amountBD.setScale(TAX_POINT, TAX_ROUNDING_MODE);

        //taxRate = taxRate.replace("%", "");
        //Float taxRate_f = Float.valueOf(taxRate) / 100;
        //BigDecimal taxRateBD = new BigDecimal(taxRate_f);
        BigDecimal taxRateBD = new BigDecimal(0.16);

        return amountBD.divide(taxRateBD.add(BigDecimal.valueOf(1L)), TAX_POINT, TAX_ROUNDING_MODE).multiply(taxRateBD).setScale(TAX_POINT, TAX_ROUNDING_MODE)
            .toString();

    }

    public static String getItemAmountWithoutTax(String amount, String taxAmount) {
        return new BigDecimal(amount).subtract(new BigDecimal(taxAmount)).setScale(TAX_POINT, TAX_ROUNDING_MODE).toString();
    }

    public static String getItemPriceWithoutTax(String amount, String num){
        return new BigDecimal(amount).divide(new BigDecimal(num), TAX_POINT, TAX_ROUNDING_MODE).toString();
    }


    public static double getTaxWithPoint(String tax) {
        NumberFormat nf= NumberFormat.getPercentInstance();
        try {
            Number m = nf.parse(tax);
            return m.doubleValue();
        }catch (Exception e) {
            return 0;
        }
    }
    public static void main(String[] args) {
        System.out.println(getTaxAmount("16.00%", "311"));
    }
}
