package com.test.excel.biz;

import com.ch.test.poi.util.POIUtils;
import com.google.common.collect.Lists;
import com.test.excel.constans.ConfigColNameEnum;
import com.test.excel.domain.BaseDO;
import com.test.excel.domain.ConfigDO;
import com.test.excel.domain.RequestExc;
import com.test.excel.process.AbstractProcess;
import com.test.excel.util.ItemGHMapUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.List;

/**
 * @author banmo
 * @create 2018-05-12
 **/
public class ExecutorCsvProcess extends AbstractProcess {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorCsvProcess.class);

    @Override
    public void uploadAfter(RequestExc request) {
    }

    @Override
    public RequestExc buildRequest() {
        RequestExc<ConfigDO, BaseDO, BaseDO> request = new RequestExc();
        return request;
    }

    @Override
    public void upload(RequestExc request) {
        BufferedWriter out = null;
        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(request.getFis(), "gb2312"));
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(request.getDownloadDir() + "/new_" +request.getFileName()), "gb2312"));
            String str = null;
            while ((str = in.readLine()) != null) {
                str = str.replaceAll("\"","");
                out.write(str);
                out.newLine();
            }
            out.flush();
            in.close();
            out.close();
        }catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }


    }

    @Override
    public void transfor(RequestExc request) {

    }

    @Override
    public void split(RequestExc request) {
    }

    @Override
    public void download(RequestExc request) {
    }

}
