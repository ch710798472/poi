package com.test.excel.util;

import java.util.List;

/**
 * @author banmo
 * @create 2018-06-04
 **/
public class NuoNuoStringUtils {
    public static String getLabelString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        list.forEach(sb::append);
        return sb.toString();
    }
}
