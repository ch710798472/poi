package com.test.excel.biz;

import com.alibaba.fastjson.JSON;
import com.ch.test.poi.util.POIUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.test.excel.constans.RequestConstants;
import com.test.excel.domain.*;
import com.test.excel.util.FileUtils;
import com.test.excel.constans.BaseColNameEnum;
import com.test.excel.constans.TargetColNameEnum;
import com.test.excel.process.AbstractProcess;
import com.test.excel.util.ItemGHMapUtils;
import com.test.excel.util.NuoNuoStringUtils;
import com.test.excel.util.TaxUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.swing.text.html.parser.Entity;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;
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
            list = POIUtils.convert2List(request.getFis(), BaseItemDO.class, BaseColNameEnum.getBaseFieldMapStr(),
                request.getFileName());
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
        if ("csv".equals(FileUtils.getFileType(request))) {
            FileUtils.read(request);
        }
    }

    @Override
    public void transfor(RequestExc request) {
        List<BaseItemDO> baseItemDOS = request.getBase();
        List<TransItemDO> transItemDOS = Lists.newArrayList();
        ;
        if (CollectionUtils.isEmpty(baseItemDOS)) {
            return;
        }
        baseItemDOS.forEach(baseItemDO -> {
            transItemDOS.add(transforBuildTransItemDO(baseItemDO, request));
        });

        //数据透视
        Map<String, TransItemDO> transItemDOMap = Maps.newHashMap();
        transItemDOS.forEach(transItemDO -> {
            if (null != transItemDOMap.get(transItemDO.getItemBarCode())) {
                TransItemDO tmp = transItemDOMap.get(transItemDO.getItemBarCode());
                BigDecimal bdtmpAmount = new BigDecimal(tmp.getItemAmount());
                BigDecimal bdaddAmount = new BigDecimal(transItemDO.getItemAmount());
                tmp.setItemAmount(bdtmpAmount.add(bdaddAmount).toString());

                BigDecimal bdtmpNum = new BigDecimal(tmp.getItemNum());
                BigDecimal bdaddNum = new BigDecimal(transItemDO.getItemNum());
                tmp.setItemNum(bdtmpNum.add(bdaddNum).toString());
                tmp.setItemTaxAmount(TaxUtils.getTaxAmount(tmp.getItemTax(), tmp.getItemAmount()));
                tmp.setItemAmountWithoutTax(
                    TaxUtils.getItemAmountWithoutTax(tmp.getItemAmount(), tmp.getItemTaxAmount()));
                tmp.setItemPriceWithoutTax(
                    TaxUtils.getItemPriceWithoutTax(tmp.getItemAmountWithoutTax(), tmp.getItemNum()));
            } else {
                transItemDOMap.put(transItemDO.getItemBarCode(), transItemDO);
            }
        });

        List<TransItemDO> resultTransItemDOS = Lists.newArrayList();
        for (Map.Entry<String, TransItemDO> m : transItemDOMap.entrySet()) {
            resultTransItemDOS.add(m.getValue());
        }

        request.setTmp(resultTransItemDOS);

    }

    @Override
    public void split(RequestExc request) {
        List<TransItemDO> tmp = request.getTmp();
        List<List<TransItemDO>> splitTmp = Lists.newArrayList();
        int splitNum = 0;
        int i_start = 0;
        Collections.sort(tmp, new Comparator<TransItemDO>() {
            @Override
            public int compare(TransItemDO s1, TransItemDO s2) {
                BigDecimal bd1 = new BigDecimal(s1.getItemAmount());
                BigDecimal bd2 = new BigDecimal(s2.getItemAmount());
                return bd1.compareTo(bd2);
            }
        });
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

        if (i_start <= tmp.size()) {
            List<TransItemDO> tmp_i = Lists.newArrayList();
            tmp_i.addAll(tmp.subList(i_start, tmp.size()));
            splitTmp.add(tmp_i);
        }

        if (!CollectionUtils.isEmpty(splitTmp)) {
            request.setSplitTmp(splitTmp);
            setSpiltTotalAmount(request);
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
        for (int i = 0; i < request.getSplitTmp().size(); i++) {
            List<TransItemDO> l = (List<TransItemDO>)request.getSplitTmp().get(i);
            List<TargetItemDO> result = Lists.newArrayList();
            for (int j = 0; j < l.size(); j++) {
                result.add(downloadTransfor(l.get(j), j + 1, request));
            }

            if (CollectionUtils.isEmpty(result)) {
                logger.error("download result is empty.i={}", i);
                continue;
            }

            saveFiles(result, new File(request.getDownloadDir() + "/part_" + i + "_" + newFileName));
        }
    }

    private TargetItemDO downloadTransfor(TransItemDO tmp, int i, RequestExc request) {
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
        fixField(tmp, targetItemDO, request);

        return targetItemDO;
    }

    private void fixField(TransItemDO tmp, TargetItemDO targetItemDO, RequestExc request) {

        TransItemDO configDO = itemGHMapUtils.getConfigDO(ItemGHMapUtils.fixedBarCode(tmp.getItemBarCode()));
        if (null == configDO) {
            logText.append("itemName=").append(tmp.getItemName()).append("itemBarCode=").append(
                tmp.getItemBarCode() + "\r\n");
            logger.error("itemName={},itemBarCode={}", tmp.getItemName(), tmp.getItemBarCode());
            FileUtils.addErrorLine(request, tmp);
            return;
        }
        targetItemDO.setItemType(StringUtils.isBlank(tmp.getItemType()) ? configDO.getItemType() : tmp.getItemType());
    }

    private void saveFiles(List<TargetItemDO> tmp, File file) {
        try {
            byte[] outputByte = POIUtils.convert2Excel(tmp, TargetItemDO.class,
                TargetColNameEnum.getTargetFieldMapStr());
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
            logText.append("Exception=").append(ExceptionUtils.getStackTrace(e));
            logger.error(e.getMessage() + ExceptionUtils.getStackTrace(e));
        }
    }

    private TransItemDO transforBuildTransItemDO(BaseItemDO baseItemDO, RequestExc request) {
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
        transItemDO.setItemAmountWithoutTax(
            TaxUtils.getItemAmountWithoutTax(baseItemDO.getItemAmount(), transItemDO.getItemTaxAmount()));
        transItemDO.setItemPriceWithoutTax(
            TaxUtils.getItemPriceWithoutTax(transItemDO.getItemAmountWithoutTax(), baseItemDO.getItemNum()));

        TransItemDO configDO = itemGHMapUtils.getConfigDO(ItemGHMapUtils.fixedBarCode(transItemDO.getItemBarCode()));
        if (null == configDO) {
            logText.append("itemName=").append(baseItemDO.getItemName()).append("itemBarCode=").append(
                baseItemDO.getItemBarCode() + "\r\n");
            logger.error("itemName={},itemBarCode={}", baseItemDO.getItemName(), baseItemDO.getItemBarCode());
            FileUtils.addErrorLine(request, transItemDO);
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

    private void setSpiltTotalAmount(RequestExc request) {
        List<List<TransItemDO>> split = request.getSplitTmp();
        List<String> msgList = Lists.newArrayList();
        BigDecimal amountTotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;
        BigDecimal amountWithoutTaxTotal = BigDecimal.ZERO;
        for (List<TransItemDO> transItemDOS : split) {
            String msg = "";
            BigDecimal amount = BigDecimal.ZERO;
            BigDecimal tax = BigDecimal.ZERO;
            BigDecimal amountWithoutTax = BigDecimal.ZERO;
            for (TransItemDO transItemDO : transItemDOS) {
                amount = amount.add(TaxUtils.add(transItemDO.getItemAmount()));
                tax = tax.add(TaxUtils.add(transItemDO.getItemTaxAmount()));
                amountWithoutTax = amountWithoutTax.add(TaxUtils.add(transItemDO.getItemAmountWithoutTax()));
            }
            msg += "金额：" + amount + "，税额：" + tax + "，不含税金额：" + amountWithoutTax + "\r\n";
            amountTotal = amountTotal.add(amount);
            taxTotal = taxTotal.add(tax);
            amountWithoutTaxTotal = amountWithoutTaxTotal.add(amountWithoutTax);
            msgList.add(msg);
        }
        String msg = "";
        msg += "总金额：" + amountTotal + "，总税额：" + taxTotal + "，总不含税金额：" + amountWithoutTaxTotal + "\r\n";
        msgList.add(msg);
        request.addParams(RequestConstants.SPLIT_MSG, NuoNuoStringUtils.getLabelString(msgList));

        List<BaseItemDO> base = request.getBase();
        String baseMsg = "";
        BigDecimal baseAmountTotal = BigDecimal.ZERO;

        BigDecimal baseAmountWithoutTaxTotal = BigDecimal.ZERO;
        for (BaseItemDO baseItemDO : base) {
            baseAmountTotal = baseAmountTotal.add(TaxUtils.add(baseItemDO.getItemAmount()));
        }
        BigDecimal baseTaxTotal = new BigDecimal(TaxUtils.getTaxAmount("0.16%", baseAmountTotal.toString()));
        baseMsg += "原始金额：" + baseAmountTotal + "，原始税额：" + baseTaxTotal + "，原始不含税金额：" + TaxUtils.getItemAmountWithoutTax(
            baseAmountTotal.toString(), baseTaxTotal.toString()) + "\r\n";
        String checkMsg = "";
        if (baseAmountTotal.subtract(amountTotal).abs().compareTo(BigDecimal.valueOf(TaxUtils.EXP)) > 0) {
            checkMsg += "总金额校验失败，大于" + TaxUtils.EXP;
        } else {
            checkMsg += "总金额校验成功";
        }
        if (baseTaxTotal.subtract(taxTotal).abs().compareTo(BigDecimal.valueOf(TaxUtils.EXP)) > 0) {
            checkMsg += "，总税费校验失败，大于" + TaxUtils.EXP;
        } else {
            checkMsg += "，总税费校验成功";
        }
        checkMsg += "\r\n";

        request.addParams(RequestConstants.BASE_MSG, baseMsg + checkMsg);
    }
}
