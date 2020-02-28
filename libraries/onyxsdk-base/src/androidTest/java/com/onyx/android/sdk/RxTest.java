package com.onyx.android.sdk;

import android.app.Application;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.test.ApplicationTestCase;
import android.text.TextUtils;
import android.util.Log;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.rx.RxManager;
import com.onyx.android.sdk.rx.RxRequest;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by john on 1/2/2018.
 */

public class RxTest extends ApplicationTestCase<Application> {

    public RxTest() {
        super(Application.class);
    }

    static public class MyRxRequest extends RxRequest {
        private volatile int value = 0;

        public MyRxRequest(int v) {
            value = v;
        }

        @Override
        public void execute() throws Exception {
            assertFalse(Looper.myLooper() == Looper.getMainLooper());
            Log.e("###############", "execute with value: " + value);
            if (value < 0){
                throw new Exception("error");
            }
            ++value;
            TestUtils.sleep(TestUtils.randInt(500, 1000));
        }
    }

    public void testRxDone() throws Exception {
        final Looper currentLooper = Looper.myLooper();
        final CountDownLatch countDownLatch = new CountDownLatch(4);
        RxManager.Builder.initAppContext(getContext());
        final RxManager.Builder builder = new RxManager.Builder();
        RxManager rxManager = builder.subscribeOn(Schedulers.computation())
                .build();
        final MyRxRequest request = new MyRxRequest(100);
        rxManager.enqueue(request, new RxCallback<MyRxRequest>() {
            @Override
            public void onNext(@NonNull MyRxRequest myRxRequest) {
                assertTrue(Looper.myLooper() == Looper.getMainLooper());
                countDownLatch.countDown();
            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                assertTrue(Looper.myLooper() == currentLooper);
                countDownLatch.countDown();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                assertTrue(false);
            }

            @Override
            public void onComplete() {
                assertTrue(Looper.myLooper() == Looper.getMainLooper());
                countDownLatch.countDown();
            }

            public void onFinally() {
                assertTrue(Looper.myLooper() == Looper.getMainLooper());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testRxError() throws Exception {
        final Looper currentLooper = Looper.myLooper();
        final CountDownLatch countDownLatch = new CountDownLatch(3);

        RxManager.Builder.initAppContext(getContext());
        final RxManager.Builder builder = new RxManager.Builder();
        RxManager rxManager =  builder.subscribeOn(Schedulers.computation()).build();
        final MyRxRequest request = new MyRxRequest(-100);
        rxManager.enqueue(request, new RxCallback<MyRxRequest>() {
            @Override
            public void onNext(@NonNull MyRxRequest myRxRequest) {
                assertTrue(false);
            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                assertTrue(Looper.myLooper() == currentLooper);
                countDownLatch.countDown();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                assertTrue(Looper.myLooper() == Looper.getMainLooper());
                countDownLatch.countDown();
            }

            @Override
            public void onComplete() {
                assertTrue(false);
            }

            public void onFinally() {
                assertTrue(Looper.myLooper() == Looper.getMainLooper());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testRxConcat2() throws Exception {
        final Looper currentLooper = Looper.myLooper();
        int limit = 10;
        final CountDownLatch countDownLatch = new CountDownLatch(limit + 3);

        RxManager.Builder.initAppContext(getContext());
        RxManager rxManager = RxManager.Builder.sharedSingleThreadManager();
        final List<MyRxRequest> requests = new ArrayList<>();
        for(int i = 0; i < limit; ++i) {
            final MyRxRequest request = new MyRxRequest(TestUtils.randInt(10, 100));
            requests.add(request);
        }
        rxManager.concat(requests, new RxCallback<MyRxRequest>() {
            @Override
            public void onNext(@NonNull MyRxRequest myRxRequest) {
                assertTrue(Looper.myLooper() == Looper.getMainLooper());
                assertTrue(requests.indexOf(myRxRequest) == 0);
                requests.remove(0);
                countDownLatch.countDown();
            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                assertTrue(Looper.myLooper() == currentLooper);
                countDownLatch.countDown();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                assertTrue(false);
            }

            @Override
            public void onComplete() {
                assertTrue(Looper.myLooper() == Looper.getMainLooper());
                assertTrue(requests.size() == 0);
                countDownLatch.countDown();
            }

            public void onFinally() {
                assertTrue(Looper.myLooper() == Looper.getMainLooper());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testRxConcat1() throws Exception {
        final Looper currentLooper = Looper.myLooper();
        final int limit = 10;
        final CountDownLatch countDownLatch = new CountDownLatch(2);

        RxManager.Builder.initAppContext(getContext());
        RxManager rxManager = RxManager.Builder.sharedSingleThreadManager();
        final List<MyRxRequest> requests = new ArrayList<>();
        for(int i = 0; i < limit; ++i) {
            final MyRxRequest request = new MyRxRequest(TestUtils.randInt(10, 100));
            requests.add(request);
        }
        final MyRxRequest request = new MyRxRequest(-100);
        requests.add(request);
        rxManager.concat(requests, new RxCallback<MyRxRequest>() {
            @Override
            public void onNext(@NonNull MyRxRequest myRxRequest) {
                assertTrue(Looper.myLooper() == Looper.getMainLooper());
                requests.remove(0);
            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                assertTrue(Looper.myLooper() == currentLooper);
                countDownLatch.countDown();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                assertTrue(Looper.myLooper() == Looper.getMainLooper());
                Log.e("#########", "request list size: " + requests.size());
                assertTrue(requests.size() == 1);
                requests.remove(0);
            }

            @Override
            public void onComplete() {
            }

            public void onFinally() {
                assertTrue(Looper.myLooper() == Looper.getMainLooper());
                assertTrue(requests.size() == 0);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }


    public void testRxZip1() throws Exception {
        final Looper currentLooper = Looper.myLooper();
        final CountDownLatch countDownLatch = new CountDownLatch(2);

        RxManager.Builder.initAppContext(getContext());
        RxManager rxManager = RxManager.Builder.sharedMultiThreadManager();
        final MyRxRequest r1 = new MyRxRequest(TestUtils.randInt(10, 100));
        final MyRxRequest r2 = new MyRxRequest(TestUtils.randInt(10, 100));
        final MyRxRequest r3 = new MyRxRequest(TestUtils.randInt(10, 100));
        final int value = r1.value + r2.value + r3.value + 3;
        rxManager.zip3(r1, r2, r3, new Function3<MyRxRequest, MyRxRequest, MyRxRequest, MyRxRequest>() {
            @Override
            public MyRxRequest apply(MyRxRequest rq1, MyRxRequest rq2, MyRxRequest rq3) throws Exception {
                return new MyRxRequest(rq1.value + rq2.value + rq3.value);
            }},

        new RxCallback<MyRxRequest>() {
            @Override
            public void onNext(@NonNull MyRxRequest myRxRequest) {
                assertTrue(Looper.myLooper() == Looper.getMainLooper());
                assertTrue(value == myRxRequest.value);
            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                assertTrue(Looper.myLooper() == currentLooper);
                countDownLatch.countDown();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                assertTrue(false);
                assertTrue(Looper.myLooper() == Looper.getMainLooper());
            }

            @Override
            public void onComplete() {
            }

            public void onFinally() {
                assertTrue(Looper.myLooper() == Looper.getMainLooper());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testRxZip2() throws Exception {
        final Looper currentLooper = Looper.myLooper();
        final CountDownLatch countDownLatch = new CountDownLatch(3);

        RxManager.Builder.initAppContext(getContext());
        RxManager rxManager = RxManager.Builder.sharedMultiThreadManager();
        final MyRxRequest r1 = new MyRxRequest(-100);
        final MyRxRequest r2 = new MyRxRequest(TestUtils.randInt(10, 100));
        final MyRxRequest r3 = new MyRxRequest(TestUtils.randInt(10, 100));
        rxManager.zip3(r1, r2, r3, new Function3<MyRxRequest, MyRxRequest, MyRxRequest, MyRxRequest>() {
                    @Override
                    public MyRxRequest apply(MyRxRequest rq1, MyRxRequest rq2, MyRxRequest rq3) throws Exception {
                        assertTrue(false);
                        return new MyRxRequest(rq1.value + rq2.value + rq3.value);
                    }},

                new RxCallback<MyRxRequest>() {
                    @Override
                    public void onNext(@NonNull MyRxRequest myRxRequest) {
                        assertTrue(Looper.myLooper() == Looper.getMainLooper());
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        assertTrue(Looper.myLooper() == currentLooper);
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        assertTrue(Looper.myLooper() == Looper.getMainLooper());
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onComplete() {
                    }

                    public void onFinally() {
                        assertTrue(Looper.myLooper() == Looper.getMainLooper());
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await();
    }

    public void testEnqueueListChained() throws Exception {
        final Looper currentLooper = Looper.myLooper();
        final int limit = 10;
        final CountDownLatch countDownLatch = new CountDownLatch(2);

        RxManager.Builder.initAppContext(getContext());
        RxManager rxManager = RxManager.Builder.sharedSingleThreadManager();
        final List<MyRxRequest> requests = new ArrayList<>();
        for (int i = 0; i < limit; ++i) {
            MyRxRequest request = new MyRxRequest(TestUtils.randInt(10, 100));
            requests.add(request);
            rxManager.append(request);
        }
        final MyRxRequest errorRequest = new MyRxRequest(-100);
        requests.add(errorRequest);
        rxManager.append(errorRequest);
        rxManager.enqueueList(new RxCallback<MyRxRequest>() {
            @Override
            public void onNext(@NonNull MyRxRequest myRxRequest) {
                assertTrue(Looper.myLooper() == Looper.getMainLooper());
                assertEquals(requests.indexOf(myRxRequest), 0);
                requests.remove(0);
            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                assertTrue(Looper.myLooper() == currentLooper);
                countDownLatch.countDown();
                assertTrue(countDownLatch.getCount() == 1);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                assertTrue(Looper.myLooper() == Looper.getMainLooper());
                Log.e("#########", "request list size: " + requests.size());
                assertEquals(requests.size(), 1);
                requests.remove(0);
            }

            @Override
            public void onComplete() {
            }

            public void onFinally() {
                assertTrue(Looper.myLooper() == Looper.getMainLooper());
                assertEquals(requests.size(), 0);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}
