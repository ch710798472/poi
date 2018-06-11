package com.test.excel.util;

import com.ch.test.poi.util.POIUtils;
import com.csvreader.CsvReader;
import com.google.common.collect.Lists;
import com.test.excel.constans.BaseColNameEnum;
import com.test.excel.constans.RequestConstants;
import com.test.excel.constans.TargetColNameEnum;
import com.test.excel.constans.TransColNameEnum;
import com.test.excel.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author banmo
 * @create 2018-05-13
 **/
public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    public static final String NEW_FILE = "new_";
    public static final String FILE_CHAR = "gb2312";
    public static final String FILE_DIR = "/";

    public static String getFileType(String file) {
        if (StringUtils.isBlank(file)) {
            return null;
        }
        int dot = file.lastIndexOf(".");
        return file.substring(dot + 1, file.length());
    }

    public static String getFileType(RequestExc request) {
        if (null == request || StringUtils.isBlank(request.getFileName())) {
            return null;
        }
        return getFileType(request.getFileName());
    }

    public static void read(RequestExc request) {
        List<BaseItemDO> baseTmp = Lists.newArrayList();
        try {
            // 创建CSV读对象
            CsvReader csvReader = new CsvReader(request.getFis(), ',', Charset.forName("gbk"));

            // 读表头
            csvReader.readHeaders();

            while (csvReader.readRecord()){
                // 读一整行
                //System.out.println(csvReader.getRawRecord());
                // 读这行的某一列
                //System.out.println(csvReader.get("Link"));
                baseTmp.add(buildBaseItemDO(csvReader));
            }

        } catch (IOException e) {
           logger.error(ExceptionUtils.getStackTrace(e));
        }
        request.setBase(baseTmp.subList(0, baseTmp.size() - 1));
    }

    private static BaseItemDO buildBaseItemDO(CsvReader csvReader) throws IOException {
        BaseItemDO record = new BaseItemDO();

        record.setItemName(csvReader.get(BaseColNameEnum.ITEM_NAME.getName()));
        record.setItemCode(csvReader.get(BaseColNameEnum.ITEM_CODE.getName()));
        record.setItemBarCode(csvReader.get(BaseColNameEnum.ITEM_BAR_CODE.getName()));
        record.setItemType(csvReader.get(BaseColNameEnum.ITEM_TYPE.getName()));
        record.setItemUnit(csvReader.get(BaseColNameEnum.ITEM_UNIT.getName()));
        record.setItemNum(csvReader.get(BaseColNameEnum.ITEM_NUM.getName()));
        record.setItemPrice(csvReader.get(BaseColNameEnum.ITEM_PRICE.getName()));
        record.setItemAmount(csvReader.get(BaseColNameEnum.ITEM_AMOUNT.getName()));
        record.setItemTax(csvReader.get(BaseColNameEnum.ITEM_TAX.getName()));
        record.setItemBuyTax(csvReader.get(BaseColNameEnum.ITEM_BUY_TAX.getName()));
        record.setItemSalesTax(csvReader.get(BaseColNameEnum.ITEM_SALES_TAX.getName()));
        return record;
    }

    public static void addErrorLine(RequestExc request, TransItemDO tmp){
        List<TransItemDO> errorLine = (List<TransItemDO>)request.getParam(RequestConstants.ERROR_LINE);
        if (CollectionUtils.isEmpty(errorLine)) {
            errorLine = Lists.newArrayList();
        }
        errorLine.add(tmp);
        request.addParams(RequestConstants.ERROR_LINE, errorLine);
    }

    public static List<TransItemDO> getErrorLine(RequestExc request){
        List<TransItemDO> errorLine = (List<TransItemDO>)request.getParam(RequestConstants.ERROR_LINE);
        if (CollectionUtils.isEmpty(errorLine)) {
            errorLine = Lists.newArrayList();
        }
        return errorLine;
    }

    public static void saveFiles(List<TransItemDO> tmp, File file) {
        try {
            byte[] outputByte = POIUtils.convert2Excel(tmp, TransItemDO.class,
                TransColNameEnum.getTransFieldMapStr());
            if (file != null) {
                InputStream fi = new ByteArrayInputStream(outputByte);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int byteLength = 0;
                while ((byteLength = fi.read(bytes)) != -1) {
                    fileOutputStream.write(bytes, 0, byteLength);
                }
                fileOutputStream.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage() + ExceptionUtils.getStackTrace(e));
        }
    }

    public static TransItemDO transforBuildTransItemDO(TransItemDO configDO) {
        TransItemDO transItemDO = new TransItemDO();
        transItemDO.setItemName(configDO.getItemName());
        transItemDO.setItemBarCode(configDO.getItemBarCode());
        transItemDO.setItemGroup(configDO.getItemGroup());
        transItemDO.setItemTaxCode(configDO.getItemTaxCode());
        transItemDO.setItemTax("16%");
        transItemDO.setItemType(configDO.getItemType());
        transItemDO.setItemUnit(configDO.getItemUnit());

        //非主要
        transItemDO.setItemNum(configDO.getItemNum());
        transItemDO.setItemPriceWithoutTax(configDO.getItemPriceWithoutTax());
        transItemDO.setItemPrice(configDO.getItemPrice());
        transItemDO.setItemAmountWithoutTax(configDO.getItemAmountWithoutTax());
        transItemDO.setItemAmount(configDO.getItemAmount());
        transItemDO.setItemTaxAmount(configDO.getItemTaxAmount());
        if (StringUtils.isNotBlank(configDO.getItemTax())) {
            transItemDO.setItemTax(configDO.getItemTax());
        }
        transItemDO.setItemBuyTax(configDO.getItemBuyTax());
        transItemDO.setItemSalesTax(configDO.getItemSalesTax());
        return transItemDO;
    }
}
