package com.onyx.android.sdk.utils;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.Reference;

/**
 * Created by suicheng on 2018/5/26.
 */

public class EventBusUtils {

    public static void ensureRegister(EventBus eventBus, Object subscriber) {
        if (eventBus == null || subscriber == null) {
            return;
        }
        if (!eventBus.isRegistered(subscriber)) {
            eventBus.register(subscriber);
        }
    }

    public static void ensureUnregister(EventBus eventBus, Object subscriber) {
        if (eventBus == null || subscriber == null) {
            return;
        }
        if (eventBus.isRegistered(subscriber)) {
            eventBus.unregister(subscriber);
        }
    }

    public static void safelyPostEvent(EventBus eventBus, Object event) {
        if (eventBus == null || event == null) {
            return;
        }
        eventBus.post(event);
    }

    public static void safelyPostEvent(Reference<EventBus> reference, Object event) {
        if (reference == null) {
            return;
        }
        safelyPostEvent(reference.get(), event);
    }
}
