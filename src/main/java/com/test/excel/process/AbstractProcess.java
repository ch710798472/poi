package com.test.excel.process;

import com.google.common.collect.Lists;
import com.test.excel.domain.RequestExc;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * @author banmo
 * @create 2018-05-12
 **/
public abstract class AbstractProcess {

    public abstract RequestExc buildRequest();

    public abstract void upload(RequestExc request);

    public abstract void uploadAfter(RequestExc request);

    public abstract void transfor(RequestExc request);

    public abstract void split(RequestExc request);

    public abstract void download(RequestExc request);

    public void execute(RequestExc request) {

        upload(request);
        uploadAfter(request);
        transfor(request);
        split(request);
    }
}