package com.test.excel.biz;

import com.alibaba.fastjson.JSON;
import com.ch.test.poi.util.POIUtils;
import com.google.common.collect.Lists;
import com.test.excel.constans.BaseColNameEnum;
import com.test.excel.constans.ConfigColNameEnum;
import com.test.excel.constans.TargetColNameEnum;
import com.test.excel.domain.*;
import com.test.excel.process.AbstractProcess;
import com.test.excel.util.FileUtils;
import com.test.excel.util.ItemGHMapUtils;
import com.test.excel.util.TaxUtils;
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
import java.util.List;

/**
 * @author banmo
 * @create 2018-05-12
 **/
public class ConfigExcelProcess extends AbstractProcess {
    private static final Logger logger = LoggerFactory.getLogger(ConfigExcelProcess.class);

    @Autowired
    private ItemGHMapUtils itemGHMapUtils;

    @Override
    public void uploadAfter(RequestExc request) {
    }

    @Override
    public RequestExc buildRequest() {
        RequestExc<ConfigDO, BaseDO, BaseDO> request = new RequestExc();
        List<ConfigDO> base = Lists.newArrayList();
        request.setBase(base);
        return request;
    }

    @Override
    public void upload(RequestExc request) {
        List<ConfigDO> list = null;
        try {
            list = POIUtils.convert2List(request.getFis(), ConfigDO.class, ConfigColNameEnum.getConfigFieldMapStr(), request.getFileName());
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        request.setBase(list);
    }

    @Override
    public void transfor(RequestExc request) {
        List<ConfigDO> list = request.getBase();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(configDO -> {
            configDO.setItemTax("16.00%");
            itemGHMapUtils.putConfigDO(configDO.getItemBarCode(),configDO);
        });
    }

    @Override
    public void split(RequestExc request) {
    }

    @Override
    public void download(RequestExc request) {
    }

}
