package com.onyx.gallery.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.gallery.bundle.EditBundle;
import com.onyx.gallery.common.BaseEditAction;
import com.onyx.gallery.event.result.GetFontsResultEvent;
import com.onyx.gallery.request.GetFontsRequest;

import org.jetbrains.annotations.NotNull;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by ming on 2016/11/18.
 */

public class GetFontsAction extends BaseEditAction {

    private String currentFont;

    public GetFontsAction(@NotNull EditBundle editBundle) {
        super(editBundle);
    }

    public GetFontsAction setCurrentFont(String currentFont) {
        this.currentFont = currentFont;
        return this;
    }

    @Override
    public void execute(final RxCallback callback) {
        EditBundle editBundle = getEditBundle();
        GetFontsRequest request = new GetFontsRequest(editBundle, currentFont);
        editBundle.enqueue(request, new RxCallback<GetFontsRequest>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                super.onSubscribe(d);
                RxCallback.onSubscribe(callback, d);
            }

            @Override
            public void onNext(@NonNull GetFontsRequest getFontsRequest) {
                RxCallback.onNext(callback, getFontsRequest);
                getEventBus().post(new GetFontsResultEvent()
                        .setChineseFontList(getFontsRequest.getChineseFontList())
                        .setEnglishFontList(getFontsRequest.getEnglishFontList())
                        .setCustomizeFontList(getFontsRequest.getCustomizeFonts())
                        .setOnyxSystemFontExist(getFontsRequest.isOnyxSystemFontExist())
                );
            }

            @Override
            public void onError(@NonNull Throwable e) {
                super.onError(e);
                RxCallback.onError(callback, e);
                getEventBus().post(new GetFontsResultEvent(e));
            }

            @Override
            public void onComplete() {
                super.onComplete();
                RxCallback.onComplete(callback);
            }

            @Override
            public void onFinally() {
                super.onFinally();
                RxCallback.onFinally(callback);
            }
        });
    }
}
