package com.onyx.android.sdk.rx;

import android.support.annotation.WorkerThread;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : liao lin tao
 *     time   : 2018/10/26 14:23
 *     desc   :
 * </pre>
 */
public class RequestChain<T extends RxRequest> extends RxRequest {

    private List<T> requestList = new ArrayList<>();

    @Override
    public void execute() throws Exception {
        for (RxRequest request : requestList) {
            if (isAbort()) {
                return;
            }
            request.setContext(getContext());
            beforeExecute(request);
            request.execute();
            afterExecute(request);
        }
    }

    @Override
    public void setAbort() {
        super.setAbort();
        for (T t : requestList) {
            t.setAbort();
        }
    }

    @WorkerThread
    public void beforeExecute(RxRequest request) throws Exception {

    }

    @WorkerThread
    public void afterExecute(RxRequest request) throws Exception {

    }

    public RequestChain addRequest(T request) {
        requestList.add(request);
        return this;
    }

    public List<T> getRequestList() {
        return requestList;
    }

}
