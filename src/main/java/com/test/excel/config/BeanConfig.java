package com.test.excel.config;

import com.test.excel.biz.ConfigExcelProcess;
import com.test.excel.biz.ExecutorCsvProcess;
import com.test.excel.biz.TransforExcelProcess;
import com.test.excel.util.ItemGHMapUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author banmo
 * @create 2018-05-12
 **/
@Configuration
public class BeanConfig {

    @Bean(autowire = Autowire.BY_NAME)
    public TransforExcelProcess transforExcelProcess() {
        return new TransforExcelProcess();
    }

    @Bean(autowire = Autowire.BY_NAME)
    public ConfigExcelProcess configExcelProcess() {
        return new ConfigExcelProcess();
    }
    @Bean(autowire = Autowire.BY_NAME)
    public ItemGHMapUtils itemGHMapUtils() {
        return new ItemGHMapUtils();
    }

    @Bean(autowire = Autowire.BY_NAME)
    public ExecutorCsvProcess executorCsvProcess() {
        return new ExecutorCsvProcess();
    }

}
