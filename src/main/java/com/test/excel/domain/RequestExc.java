package com.test.excel.domain;

import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author banmo
 * @create 2018-05-12
 **/
public class RequestExc<B, M, T> {

    private List<B> base;
    private List<M> tmp;
    private List<T> target;
    private List<List<M>> splitTmp;

    private InputStream fis;

    private String fileName;

    private String downloadDir;

    private Map<String, Object> params = Maps.newHashMap();;

    public List<B> getBase() {
        return base;
    }

    public void setBase(List<B> base) {
        this.base = base;
    }

    public List<M> getTmp() {
        return tmp;
    }

    public void setTmp(List<M> tmp) {
        this.tmp = tmp;
    }

    public List<T> getTarget() {
        return target;
    }

    public void setTarget(List<T> target) {
        this.target = target;
    }

    public List<List<M>> getSplitTmp() {
        return splitTmp;
    }

    public void setSplitTmp(List<List<M>> splitTmp) {
        this.splitTmp = splitTmp;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public Object getParam(String key) {
        if (CollectionUtils.isEmpty(params)) {
            return null;
        }
        return params.get(key);
    }

    public void addParams(String k, Object o) {
        this.params.put(k, o);
    }

    public InputStream getFis() {
        return fis;
    }

    public void setFis(InputStream fis) {
        this.fis = fis;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDownloadDir() {
        return downloadDir;
    }

    public void setDownloadDir(String downloadDir) {
        this.downloadDir = downloadDir;
    }
}
