package com.onyx.android.sdk.data;

import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by zhuzeng on 11/6/15.
 */
public class ReaderTextStyle {

    public enum Alignment {
        ALIGNMENT_NONE,
        ALIGNMENT_LEFT,
        ALIGNMENT_RIGHT,
        ALIGNMENT_JUSTIFY
    }

    public static class Percentage {
        private int percent;

        public Percentage() {
        }

        public Percentage(int percent) {
            this.percent = percent;
        }

        public static Percentage create(int percent) {
            return new Percentage(percent);
        }

        public int getPercent() {
            return percent;
        }

        public void setPercent(int percent) {
            this.percent = percent;
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && o instanceof Percentage) {
                return this.getPercent() == ((Percentage) o).getPercent();
            }
            return false;

        }
    }

    public static class CharacterIndent {
        private int indent;

        public CharacterIndent() {
        }

        public CharacterIndent(int indent) {
            this.indent = indent;
        }

        public static CharacterIndent create(int indent) {
            return new CharacterIndent(indent);
        }

        public int getIndent() {
            return indent;
        }

        public void setIndent(int indent) {
            this.indent = indent;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            CharacterIndent that = (CharacterIndent) o;

            return indent == that.indent;
        }

        @Override
        public int hashCode() {
            return indent;
        }
    }

    /**
     * Density-independent Pixels
     */
    public static class DPUnit {
        private int value;

        public DPUnit() {
        }

        public DPUnit(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public static DPUnit create(int value) {
            return new DPUnit(value);
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && o instanceof DPUnit) {
                return this.getValue() == ((DPUnit) o).getValue();
            }
            return false;
        }
    }

    public static class SPUnit {
        private float value;

        public SPUnit() {
        }

        public SPUnit(float value) {
            this.value = value;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }

        public static SPUnit create(float value) {
            return new SPUnit(value);
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && o instanceof SPUnit) {
                return this.getValue() == ((SPUnit) o).getValue();
            }
            return false;
        }

        public SPUnit increaseSPUnit(SPUnit step) {
            value  = Math.min(value + step.getValue(), MAX_FONT_SIZE.getValue());
            return this;
        }

        public SPUnit decreaseSPUnit(SPUnit step) {
            value  = Math.max(value - step.getValue(), MIN_FONT_SIZE.getValue());
            return this;
        }
    }

    public static class PageMargin {
        private Percentage topMargin;
        private Percentage leftMargin;
        private Percentage rightMargin;
        private Percentage bottomMargin;

        public PageMargin() {
        }

        public PageMargin(Percentage leftMargin, Percentage bottomMargin, Percentage rightMargin, Percentage topMargin) {
            this.bottomMargin = bottomMargin;
            this.leftMargin = leftMargin;
            this.rightMargin = rightMargin;
            this.topMargin = topMargin;
        }

        public static PageMargin copy(PageMargin pageMargin) {
            return new PageMargin(Percentage.create(pageMargin.getLeftMargin().getPercent()),
                    Percentage.create(pageMargin.getBottomMargin().getPercent()),
                    Percentage.create(pageMargin.getRightMargin().getPercent()),
                    Percentage.create(pageMargin.getTopMargin().getPercent()));
        }

        public void increasePageMargin(PageMargin pageMargin) {
            topMargin.setPercent(topMargin.getPercent() + pageMargin.getTopMargin().getPercent());
            leftMargin.setPercent(leftMargin.getPercent() + pageMargin.getLeftMargin().getPercent());
            rightMargin.setPercent(rightMargin.getPercent() + pageMargin.getRightMargin().getPercent());
            bottomMargin.setPercent(bottomMargin.getPercent() + pageMargin.getBottomMargin().getPercent());
        }

        public void decreasePageMargin(PageMargin pageMargin) {
            topMargin.setPercent(topMargin.getPercent() - pageMargin.getTopMargin().getPercent());
            leftMargin.setPercent(leftMargin.getPercent() - pageMargin.getLeftMargin().getPercent());
            rightMargin.setPercent(rightMargin.getPercent() - pageMargin.getRightMargin().getPercent());
            bottomMargin.setPercent(bottomMargin.getPercent() - pageMargin.getBottomMargin().getPercent());
        }

        public Percentage getBottomMargin() {
            return bottomMargin;
        }

        public void setBottomMargin(Percentage bottomMargin) {
            this.bottomMargin = bottomMargin;
        }

        public Percentage getLeftMargin() {
            return leftMargin;
        }

        public void setLeftMargin(Percentage leftMargin) {
            this.leftMargin = leftMargin;
        }

        public Percentage getRightMargin() {
            return rightMargin;
        }

        public void setRightMargin(Percentage rightMargin) {
            this.rightMargin = rightMargin;
        }

        public Percentage getTopMargin() {
            return topMargin;
        }

        public void setTopMargin(Percentage topMargin) {
            this.topMargin = topMargin;
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && o instanceof PageMargin) {
                PageMargin value = (PageMargin) o;
                return this.getBottomMargin().equals(value.getBottomMargin()) &&
                        this.getLeftMargin().equals(value.getLeftMargin()) &&
                        this.getTopMargin().equals(value.getTopMargin()) &&
                        this.getRightMargin().equals(value.getRightMargin());
            }
            return false;
        }
    }

    static public Alignment DEFAULT_ALIGNMENT = Alignment.ALIGNMENT_JUSTIFY;
    static public CharacterIndent DEFAULT_CHARACTER_INDENT = new CharacterIndent(2);

    static public Percentage LINE_SPACING_STEP = new Percentage(10);
    static public Percentage SMALL_LINE_SPACING = new Percentage(100);
    static public Percentage NORMAL_LINE_SPACING = new Percentage(150);
    static public Percentage LARGE_LINE_SPACING = new Percentage(200);
    static public Percentage DEFAULT_LINE_SPACING = NORMAL_LINE_SPACING;

    static public Percentage PARAGRAPH_SPACING_STEP = new Percentage(10);
    static public Percentage SMALL_PARAGRAPH_SPACING = new Percentage(0);
    static public Percentage NORMAL_PARAGRAPH_SPACING = new Percentage(100);
    static public Percentage LARGE_PARAGRAPH_SPACING = new Percentage(100);
    static public Percentage DEFAULT_PARAGRAPH_SPACING = NORMAL_PARAGRAPH_SPACING;

    static private int MARGIN_STEP = 1;
    static private int SMALL_MARGIN = 1;
    static private int NORMAL_MARGIN = 10;
    static private int LARGE_MARGIN = 20;

    static public PageMargin PAGE_MARGIN_STEP = new PageMargin(Percentage.create(MARGIN_STEP), Percentage.create(MARGIN_STEP), Percentage.create(MARGIN_STEP), Percentage.create(MARGIN_STEP));
    static public PageMargin SMALL_PAGE_MARGIN = new PageMargin(Percentage.create(SMALL_MARGIN), Percentage.create(SMALL_MARGIN), Percentage.create(SMALL_MARGIN), Percentage.create(SMALL_MARGIN));
    static public PageMargin NORMAL_PAGE_MARGIN = new PageMargin(Percentage.create(NORMAL_MARGIN), Percentage.create(NORMAL_MARGIN), Percentage.create(NORMAL_MARGIN), Percentage.create(NORMAL_MARGIN));
    static public PageMargin LARGE_PAGE_MARGIN = new PageMargin(Percentage.create(LARGE_MARGIN), Percentage.create(LARGE_MARGIN), Percentage.create(LARGE_MARGIN), Percentage.create(LARGE_MARGIN));
    static public PageMargin DEFAULT_PAGE_MARGIN = new PageMargin(Percentage.create(NORMAL_MARGIN), Percentage.create(NORMAL_MARGIN), Percentage.create(NORMAL_MARGIN), Percentage.create(NORMAL_MARGIN));;

    static public SPUnit[] DEFAULT_FONT_SIZE_LIST = {};
    static public SPUnit DEFAULT_FONT_SIZE = SPUnit.create(40.0f);
    static public SPUnit MAX_FONT_SIZE = SPUnit.create(96.0f);
    static public SPUnit MIN_FONT_SIZE = SPUnit.create(10.0f);
    static public SPUnit FONT_SIZE_STEP = SPUnit.create(4.0f);

    static public final int PAGING_MODE_SINGLE_COLUMN = 0;
    static public final int PAGING_MODE_TWO_COLUMN = 1;

    static public final int PAGING_MODE_DEFAULT = PAGING_MODE_SINGLE_COLUMN;

    private String fontFace = null;
    private SPUnit fontSize = DEFAULT_FONT_SIZE;
    private Alignment alignment = DEFAULT_ALIGNMENT;
    private CharacterIndent indent = DEFAULT_CHARACTER_INDENT;
    private Percentage lineSpacing = DEFAULT_LINE_SPACING;
    private Percentage paragraphSpacing = DEFAULT_PARAGRAPH_SPACING;
    private PageMargin pageMargin = DEFAULT_PAGE_MARGIN;
    private int pagingMode = PAGING_MODE_DEFAULT;

    public static float limitFontSize(float newSize) {
        final float minSize = MIN_FONT_SIZE.getValue();
        final float maxSize = MAX_FONT_SIZE.getValue();
        if (newSize < minSize) {
            newSize = minSize;
        } else if (newSize > maxSize) {
            newSize = maxSize;
        }
        return newSize;
    }

    public static void setDefaultFontSizes(Float[] fontSizes) {
        if (fontSizes == null || fontSizes.length <=0) {
            return;
        }
        DEFAULT_FONT_SIZE_LIST = new SPUnit[fontSizes.length];
        for (int i = 0; i < fontSizes.length; i++) {
            DEFAULT_FONT_SIZE_LIST[i] = SPUnit.create(fontSizes[i]);
        }
    }

    public static SPUnit getFontSizeByIndex(int index) {
        if (DEFAULT_FONT_SIZE_LIST.length > index) {
            return DEFAULT_FONT_SIZE_LIST[index];
        }
        return DEFAULT_FONT_SIZE;
    }

    public static Percentage getLineSpacingByIndex(int index) {
        int step = index * LINE_SPACING_STEP.getPercent() + SMALL_LINE_SPACING.getPercent();
        Percentage lineSpacing;
        if (step < SMALL_LINE_SPACING.getPercent()) {
            lineSpacing = SMALL_LINE_SPACING;
        } else if (step > LARGE_LINE_SPACING.getPercent()) {
            lineSpacing = LARGE_LINE_SPACING;
        } else {
            lineSpacing = new Percentage(step);
        }
        return lineSpacing;
    }

    public static Percentage getParagraphSpacingByIndex(int index) {
        int step = index * PARAGRAPH_SPACING_STEP.getPercent();
        Percentage paragraphSpacing;
        if (step < SMALL_PARAGRAPH_SPACING.getPercent()) {
            paragraphSpacing = SMALL_PARAGRAPH_SPACING;
        } else if (step > LARGE_PARAGRAPH_SPACING.getPercent()) {
            paragraphSpacing = LARGE_PARAGRAPH_SPACING;
        } else {
            paragraphSpacing = new Percentage(step);
        }
        return paragraphSpacing;
    }

    public static PageMargin getPageMarginByIndex(int index) {
        PageMargin pageMargin;
        if (index < SMALL_MARGIN) {
            pageMargin = SMALL_PAGE_MARGIN;
        } else if (index > LARGE_MARGIN) {
            pageMargin = LARGE_PAGE_MARGIN;
        } else {
            pageMargin = new PageMargin(Percentage.create(index), Percentage.create(index), Percentage.create(index), Percentage.create(index));
        }
        return pageMargin;
    }

    public ReaderTextStyle() {
    }

    public static ReaderTextStyle defaultStyle() {
        return new ReaderTextStyle();
    }

    public static ReaderTextStyle create(String fontface, SPUnit fontSize, Percentage lineSpacing,
                                         Percentage paragraphSpacing,
                                         CharacterIndent indent,
                                         Percentage leftMargin, Percentage topMargin,
                                         Percentage rightMargin, Percentage bottomMargin,
                                         int pagingMode) {
        ReaderTextStyle style = new ReaderTextStyle();
        style.fontFace = fontface;
        style.fontSize = fontSize;
        style.lineSpacing = lineSpacing;
        style.paragraphSpacing = paragraphSpacing;
        style.indent = indent;
        style.pageMargin.setLeftMargin(leftMargin);
        style.pageMargin.setTopMargin(topMargin);
        style.pageMargin.setRightMargin(rightMargin);
        style.pageMargin.setBottomMargin(bottomMargin);
        style.pagingMode = pagingMode;
        return style;
    }

    public static ReaderTextStyle copy(ReaderTextStyle style) {
        if (style == null) {
            return null;
        }
        ReaderTextStyle copy = new ReaderTextStyle();
        copy.fontFace = style.fontFace;
        copy.fontSize = SPUnit.create(style.fontSize.getValue());
        copy.alignment = style.alignment;
        copy.indent = CharacterIndent.create(style.indent.getIndent());
        copy.lineSpacing = Percentage.create(style.lineSpacing.getPercent());
        copy.paragraphSpacing = Percentage.create(style.paragraphSpacing.getPercent());
        copy.pageMargin = PageMargin.copy(style.pageMargin);
        copy.pagingMode = style.pagingMode;
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReaderTextStyle textStyle = (ReaderTextStyle) o;

        if (pagingMode != textStyle.pagingMode) {
            return false;
        }
        if (!StringUtils.safelyEquals(fontFace, textStyle.fontFace)) {
            return false;
        }
        if (fontSize != null ? !fontSize.equals(textStyle.fontSize) : textStyle.fontSize != null) {
            return false;
        }
        if (alignment != textStyle.alignment) {
            return false;
        }
        if (indent != null ? !indent.equals(textStyle.indent) : textStyle.indent != null) {
            return false;
        }
        if (lineSpacing != null ? !lineSpacing.equals(textStyle.lineSpacing) : textStyle.lineSpacing != null) {
            return false;
        }
        if (paragraphSpacing != null ? !paragraphSpacing.equals(textStyle.paragraphSpacing) : textStyle.paragraphSpacing != null) {
            return false;
        }
        return pageMargin != null ? pageMargin.equals(textStyle.pageMargin) : textStyle.pageMargin == null;
    }

    @Override
    public int hashCode() {
        int result = fontFace != null ? fontFace.hashCode() : 0;
        result = 31 * result + (fontSize != null ? fontSize.hashCode() : 0);
        result = 31 * result + (alignment != null ? alignment.hashCode() : 0);
        result = 31 * result + (indent != null ? indent.hashCode() : 0);
        result = 31 * result + (lineSpacing != null ? lineSpacing.hashCode() : 0);
        result = 31 * result + (paragraphSpacing != null ? paragraphSpacing.hashCode() : 0);
        result = 31 * result + (pageMargin != null ? pageMargin.hashCode() : 0);
        result = 31 * result + pagingMode;
        return result;
    }

    public String getFontFace() {
        return fontFace;
    }

    public void setFontFace(String fontFace) {
        this.fontFace = fontFace;
    }

    public SPUnit getFontSize() {
        return fontSize;
    }

    public void setFontSize(SPUnit fontSize) {
        this.fontSize = fontSize;
    }

    public void increaseFontSize() {
        int i = 0;
        for (; i < DEFAULT_FONT_SIZE_LIST.length - 1; i++) {
            if (DEFAULT_FONT_SIZE_LIST[i].getValue() > fontSize.getValue()) {
                break;
            }
        }
        fontSize = DEFAULT_FONT_SIZE_LIST[i];
    }

    public void decreaseFontSize() {
        int i = DEFAULT_FONT_SIZE_LIST.length - 1;
        for (; i > 0; i--) {
            if (DEFAULT_FONT_SIZE_LIST[i].getValue() < fontSize.getValue()) {
                break;
            }
        }
        fontSize = DEFAULT_FONT_SIZE_LIST[i];
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    public CharacterIndent getIndent() {
        return indent;
    }

    public void setIndent(CharacterIndent indent) {
        this.indent = indent;
    }

    public Percentage getLineSpacing() {
        return lineSpacing;
    }

    public void setLineSpacing(Percentage lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public Percentage getParagraphSpacing() {
        return paragraphSpacing;
    }

    public void setParagraphSpacing(Percentage paragraphSpacing) {
        this.paragraphSpacing = paragraphSpacing;
    }

    public PageMargin getPageMargin() {
        return pageMargin;
    }

    public void setPageMargin(PageMargin pageMargin) {
        this.pageMargin = pageMargin;
    }

    public int getPagingMode() {
        return pagingMode;
    }

    public void setPagingMode(int pagingMode) {
        this.pagingMode = pagingMode;
    }
}
