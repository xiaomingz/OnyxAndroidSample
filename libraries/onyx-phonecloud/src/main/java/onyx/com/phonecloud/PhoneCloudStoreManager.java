package onyx.com.phonecloud;

import android.content.Context;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.rx.RxManager;
import com.onyx.android.sdk.rx.RxRequest;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class PhoneCloudStoreManager {

    private static Context appContext;
    private EventBus eventBus;
    private RxManager rxManager;

    private static final PhoneCloudStoreManager ourInstance = new PhoneCloudStoreManager();

    public static PhoneCloudStoreManager getInstance() {
        return ourInstance;
    }

    public static void initAppContext(Context context) {
        appContext = context.getApplicationContext();
    }

    public EventBus getEventBus() {
        if (eventBus == null) {
            eventBus = new EventBus();
        }
        return eventBus;
    }

    public RxManager getRxManager() {
        if (rxManager == null) {
            rxManager = RxManager.Builder.singleThreadManager();
        }
        return rxManager;
    }

    public <T extends RxRequest> void enqueue(final T request, final RxCallback<T> callback) {
        getRxManager().enqueue(request, callback);
    }

    public <T extends RxRequest> void submit(List<T> requests, RxCallback<T> callback) {
        getRxManager().concat(requests, callback);
    }

    public void post(Object event) {
        getEventBus().post(event);
    }


    public static Context getAppContext() {
        return appContext;
    }
}
