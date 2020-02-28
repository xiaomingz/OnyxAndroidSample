package com.onyx.android.sdk.utils;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : lxw
 *     time   : 2018/12/11 19:14
 *     desc   :
 * </pre>
 */
public class EventBusHolder {

    private EventBus eventBus;
    private List<WeakReference<Object>> subscriberList = new ArrayList<>();

    public EventBusHolder() {
    }

    public EventBusHolder(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public EventBus getEventBus() {
        if (eventBus == null) {
            eventBus = new EventBus();
        }
        return eventBus;
    }

    public void post(Object event) {
        getEventBus().post(event);
    }

    public void register(Object subscriber) {
        if (getEventBus().isRegistered(subscriber)) {
            return;
        }
        getEventBus().register(subscriber);
        addSubscriber(subscriber);
    }

    private void addSubscriber(Object subscriber) {
        if (getEventBus().isRegistered(subscriber)) {
            subscriberList.add(new WeakReference<>(subscriber));
        }
    }

    private void removeSubscriber(Object subscriber) {
        if (getEventBus().isRegistered(subscriber)) {
            return;
        }
        for (WeakReference<Object> reference : subscriberList) {
            if (reference != null && reference.get() != null && reference.get().equals(subscriber)) {
                subscriberList.remove(reference);
                break;
            }
        }
    }

    public void unregister(Object subscriber) {
        if (!getEventBus().isRegistered(subscriber)) {
            return;
        }
        getEventBus().unregister(subscriber);
        removeSubscriber(subscriber);
    }

    public int dumpEventBus(final String tag) {
        if (subscriberList.size() > 0) {
            Log.e(tag, "EventBus unregister class counting: " + subscriberList.size());
            for (WeakReference<Object> reference : subscriberList) {
                if (reference == null || reference.get() == null) {
                    continue;
                }
                Log.e(tag, "EventBus unregister class: " + reference.get().getClass().getSimpleName());
            }
        }else {
            Log.e(tag, "EventBus all unregister.");
        }
        return subscriberList.size();
    }

}
