package com.test.excel.biz;


import com.alibaba.fastjson.JSON;
import com.ch.test.poi.util.POIUtils;
import com.google.common.collect.Lists;
import com.test.excel.domain.*;
import com.test.excel.util.FileUtils;
import com.test.excel.constans.BaseColNameEnum;
import com.test.excel.constans.TargetColNameEnum;
import com.test.excel.process.AbstractProcess;
import com.test.excel.util.ItemGHMapUtils;
import com.test.excel.util.TaxUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author banmo
 * @create 2018-05-12
 **/
public class TransforExcelProcess extends AbstractProcess {
    private static final Logger logger = LoggerFactory.getLogger(TransforExcelProcess.class);

    private StringBuilder logText = new StringBuilder();

    @Autowired
    private ItemGHMapUtils itemGHMapUtils;

    @Override
    public void uploadAfter(RequestExc request) {
        if ("csv".equals(FileUtils.getFileType(request))) {
            return;
        }

        List<BaseItemDO> list = null;
        try {
            list = POIUtils.convert2List(request.getFis(), BaseItemDO.class, BaseColNameEnum.getBaseFieldMapStr(), request.getFileName());
        } catch (Exception e) {
            logText.append("Exception=").append(ExceptionUtils.getStackTrace(e));
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        //System.out.println(JSON.toJSONString(list));

        if (!CollectionUtils.isEmpty(list)) {
            request.setBase(list);
        }
    }

    @Override
    public RequestExc buildRequest() {
        RequestExc<BaseItemDO, TargetItemDO, TransItemDO> request = new RequestExc();
        List<BaseItemDO> base = Lists.newArrayList();
        List<TargetItemDO> tmp = Lists.newArrayList();
        List<TransItemDO> target = Lists.newArrayList();

        request.setBase(base);
        request.setTarget(target);
        request.setTmp(tmp);
        return request;
    }

    @Override
    public void upload(RequestExc request) {
        // csv
        if ("csv".equals(FileUtils.getFileType(request))){
            FileUtils.read(request);
        }
    }

    @Override
    public void transfor(RequestExc request) {
        List<BaseItemDO> baseItemDOS = request.getBase();
        List<TransItemDO> transItemDOS =  Lists.newArrayList();;
        if (CollectionUtils.isEmpty(baseItemDOS)) {
            return;
        }
        baseItemDOS.forEach(baseItemDO -> {
            transItemDOS.add(transforBuildTransItemDO(baseItemDO));
        });
        request.setTmp(transItemDOS);

    }

    @Override
    public void split(RequestExc request) {
        List<TransItemDO> tmp = request.getTmp();
        List<List<TransItemDO>> splitTmp = Lists.newArrayList();
        int splitNum = 0;
        int i_start = 0;
        BigDecimal maxAmountOfOne = BigDecimal.valueOf(115999L);
        BigDecimal tmpAmount = BigDecimal.ZERO;
        for (int i = 0; i < tmp.size(); i++) {
            tmpAmount = tmpAmount.add(new BigDecimal(tmp.get(i).getItemAmount()));
            if (tmpAmount.compareTo(maxAmountOfOne) >= 0) {
                List<TransItemDO> tmp_i = Lists.newArrayList();
                tmp_i.addAll(tmp.subList(i_start, i));
                splitTmp.add(tmp_i);
                i_start = i;
                i--;
                tmpAmount = BigDecimal.ZERO;
            }
        }

        if( i_start <= tmp.size()) {
            List<TransItemDO> tmp_i = Lists.newArrayList();
            tmp_i.addAll(tmp.subList(i_start, tmp.size()));
            splitTmp.add(tmp_i);
        }

        if (!CollectionUtils.isEmpty(splitTmp)) {
            request.setSplitTmp(splitTmp);
        }
    }

    @Override
    public void download(RequestExc request) {
        //将拆分后的数据保存
        if (CollectionUtils.isEmpty(request.getSplitTmp())) {
            return;
        }
        String fileName = request.getFileName();
        String newFileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".xlsx";
        for (int i = 0; i < request.getSplitTmp().size();i++) {
            List<TransItemDO> l = (List<TransItemDO>) request.getSplitTmp().get(i);
            List<TargetItemDO> result = Lists.newArrayList();
            for (int j = 0; j < l.size(); j++) {
                result.add(downloadTransfor(l.get(j), j + 1));
            }

            if (CollectionUtils.isEmpty(result)) {
                logger.error("download result is empty.i={}", i);
                continue;
            }

            saveFiles(result, new File(request.getDownloadDir() + "/part_" + i + "_" + newFileName));
        }
    }

    private TargetItemDO downloadTransfor(TransItemDO tmp, int i) {
        TargetItemDO targetItemDO = new TargetItemDO();
        targetItemDO.setItemId(i + "");
        targetItemDO.setItemName(tmp.getItemName());
        targetItemDO.setItemUnitType(tmp.getItemUnit());
        targetItemDO.setItemType(tmp.getItemType());
        targetItemDO.setItemNum(tmp.getItemNum());
        targetItemDO.setItemAmount(tmp.getItemAmountWithoutTax());
        targetItemDO.setItemTax(TaxUtils.getTaxWithPoint(tmp.getItemTax()) + "");
        targetItemDO.setItemTaxAmount(tmp.getItemTaxAmount());
        targetItemDO.setItemPrice(tmp.getItemPriceWithoutTax());
        targetItemDO.setItemPriceType("0");
        targetItemDO.setItemTaxCodeVersion("28.0");
        targetItemDO.setItemTaxCode(tmp.getItemTaxCode());
        targetItemDO.setItemDiscountCode("0");
        targetItemDO.setItemGasFlag("0");
        fixField(tmp, targetItemDO);

        return targetItemDO;
    }

    private void fixField(TransItemDO tmp, TargetItemDO targetItemDO) {

        ConfigDO configDO = itemGHMapUtils.getConfigDO(ItemGHMapUtils.fixedBarCode(tmp.getItemBarCode()));
        if (null == configDO) {
            logText.append("itemName=").append(tmp.getItemName()).append("itemBarCode=").append(tmp.getItemBarCode() + "\r\n");
            logger.error("itemName={},itemBarCode={}", tmp.getItemName(), tmp.getItemBarCode());
            return;
        }
        targetItemDO.setItemType(StringUtils.isBlank(tmp.getItemType()) ? configDO.getItemType() : tmp.getItemType());
    }

    private void saveFiles(List<TargetItemDO> tmp, File file) {
        try {
            byte[] outputByte = POIUtils.convert2Excel(tmp, TargetItemDO.class, TargetColNameEnum.getTargetFieldMapStr());
            if(file != null){
                InputStream fi = new ByteArrayInputStream(outputByte);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int byteLength = 0;
                while((byteLength = fi.read(bytes)) != -1){
                    fileOutputStream.write(bytes,0,byteLength);
                }
                fileOutputStream.close();
            }
        } catch (Exception e) {
            logText.append("Exception=").append(ExceptionUtils.getStackTrace(e));
            logger.error(e.getMessage() + ExceptionUtils.getStackTrace(e));
        }
    }

    private TransItemDO transforBuildTransItemDO(BaseItemDO baseItemDO) {
        TransItemDO transItemDO = new TransItemDO();
        transItemDO.setItemName(baseItemDO.getItemName());
        transItemDO.setItemBarCode(baseItemDO.getItemBarCode());
        transItemDO.setItemType(baseItemDO.getItemType());
        transItemDO.setItemUnit(baseItemDO.getItemUnit());
        transItemDO.setItemNum(baseItemDO.getItemNum());
        //transItemDO.setItemTax(baseItemDO.getItemTax());
        transItemDO.setItemPrice(baseItemDO.getItemPrice());
        transItemDO.setItemAmount(baseItemDO.getItemAmount());
        transItemDO.setItemBuyTax(baseItemDO.getItemBuyTax());
        transItemDO.setItemSalesTax(baseItemDO.getItemSalesTax());

        transItemDO.setItemTaxAmount(TaxUtils.getTaxAmount(baseItemDO.getItemTax(), baseItemDO.getItemAmount()));
        transItemDO.setItemAmountWithoutTax(TaxUtils.getItemAmountWithoutTax(baseItemDO.getItemAmount(),transItemDO.getItemTaxAmount()));
        transItemDO.setItemPriceWithoutTax(TaxUtils.getItemPriceWithoutTax(transItemDO.getItemAmountWithoutTax(), baseItemDO.getItemNum()));

        ConfigDO configDO = itemGHMapUtils.getConfigDO(ItemGHMapUtils.fixedBarCode(transItemDO.getItemBarCode()));
        if (null == configDO) {
            logText.append("itemName=").append(baseItemDO.getItemName()).append("itemBarCode=").append(baseItemDO.getItemBarCode() + "\r\n");
            logger.error("itemName={},itemBarCode={}", baseItemDO.getItemName(), baseItemDO.getItemBarCode());
            return null;
        }
        transItemDO.setItemTax("16%");
        transItemDO.setItemGroup(configDO.getItemGroup());
        transItemDO.setItemTaxCode(configDO.getItemTaxCode());
        transItemDO.setItemType(configDO.getItemType());
        transItemDO.setItemUnit(configDO.getItemUnit());

        return transItemDO;
    }

    public StringBuilder getLogText() {
        return logText;
    }

    public void setLogText(StringBuilder logText) {
        this.logText = logText;
    }
}
