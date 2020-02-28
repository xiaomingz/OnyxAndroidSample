package com.onyx.android.sdk.data;

import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by zhuzeng on 2/15/16.
 */
public class PageConstants {

    /**
     * layout section.
     */
    public static final String SINGLE_PAGE = "singlePage";
    public static final String SINGLE_PAGE_NAVIGATION_LIST = "singlePageNavigationList";
    public static final String DUAL_PAGE_LTR = "dualPage";
    public static final String DUAL_PAGE_RTL = "dualPage_RTL";
    public static final String CONTINUOUS_PAGE = "continuousPage";
    public static final String IMAGE_REFLOW_PAGE = "imageReflowPage";
    public static final String TEXT_REFLOW_PAGE = "textReflowPage";

    /**
     * scale
     */
    public static final int SCALE_INVALID = 0;
    public static final int SCALE_TO_PAGE = -1;
    public static final int SCALE_TO_WIDTH = -2;
    public static final int SCALE_TO_HEIGHT = -3;
    public static final int SCALE_TO_PAGE_CONTENT = -4;
    public static final int SCALE_TO_WIDTH_CONTENT = -5;
    public static final int ZOOM_TO_SCAN_REFLOW = -6;
    public static final int ZOOM_TO_REFLOW = -7;
    public static final int ZOOM_TO_COMICE = -8;
    public static final int ZOOM_TO_PAPER = -9;
    public static final float MAX_SCALE = 16;
    public static final float DEFAULT_ACTUAL_SCALE = 1.0f;

    public static double DEFAULT_AUTO_CROP_VALUE = 0.01;
    public static int DEFAULT_PARAGRAPH_INDENT = 2;
    public static int DEFAULT_LINE_SPACING = 110;
    public static int DEFAULT_CROP_STEP = 2;
    public static int DEFAULT_CROP_MARGIN = 0;

    public static boolean isSpecialScale(int scale) {
        if (scale < SCALE_INVALID && scale >= SCALE_TO_WIDTH_CONTENT) {
            return true;
        }
        return false;
    }

    public static boolean isScaleToPage(int specialScale) {
        return specialScale == PageConstants.SCALE_TO_PAGE;
    }

    public static boolean isScaleToWidth(int specialScale) {
        return specialScale == PageConstants.SCALE_TO_WIDTH;
    }

    public static boolean isScaleToHeight(int specialScale) {
        return specialScale == PageConstants.SCALE_TO_HEIGHT;
    }

    public static boolean isScaleToPageContent(int specialScale) {
        return specialScale == PageConstants.SCALE_TO_PAGE_CONTENT;
    }

    public static boolean isWidthCrop(int specialScale) {
        return specialScale == PageConstants.SCALE_TO_WIDTH_CONTENT;
    }

    public static boolean isCropPage(int specialScale) {
        switch (specialScale) {
            case PageConstants.SCALE_TO_WIDTH_CONTENT:
            case PageConstants.SCALE_TO_PAGE_CONTENT:
                return true;
            default:
                return false;
        }
    }

    public static boolean isDualPageLayout(String layoutType) {
        if (StringUtils.isNullOrEmpty(layoutType)) {
            return false;
        }
        switch (layoutType) {
            case DUAL_PAGE_LTR:
            case DUAL_PAGE_RTL:
                return true;
            default:
                return false;
        }
    }

    public static boolean isDualPageRTL(String layoutType) {
        return StringUtils.safelyEquals(layoutType, DUAL_PAGE_RTL);
    }

    public static boolean isDualPageLTR(String layoutType) {
        return StringUtils.safelyEquals(layoutType, DUAL_PAGE_LTR);
    }

    public static boolean isSinglePage(String layoutType) {
        return StringUtils.safelyEquals(layoutType, SINGLE_PAGE);
    }

    public static boolean isImageReflow(String layoutType) {
        return StringUtils.safelyEquals(layoutType, IMAGE_REFLOW_PAGE);
    }

    public static boolean isContinuousPage(String layoutType) {
        return StringUtils.safelyEquals(layoutType, CONTINUOUS_PAGE);
    }
}
