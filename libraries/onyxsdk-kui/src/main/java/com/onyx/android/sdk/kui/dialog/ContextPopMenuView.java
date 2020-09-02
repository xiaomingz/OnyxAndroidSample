package com.onyx.android.sdk.kui.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.onyx.android.sdk.kui.R;
import com.onyx.android.sdk.kui.databinding.PopMenuBinding;
import com.onyx.android.sdk.kui.rxbinding.RxView;
import com.onyx.android.sdk.kui.view.DisableScrollLinearManager;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by anypwx on 2018/5/11 10:40.
 */
public class ContextPopMenuView extends BasePopWindow {
    private PopMenuBinding binding;
    private View target;
    private TextView textView;
    private LayoutInflater layoutInflater;
    private AdapterView.OnItemClickListener itemListener;
    private MenuAdapter menuAdapter;
    private List<String> menuData;
    private int menuLayoutId = -1;
    private List<Integer> itemId = null;
    private int offsetX, offsetY;
    private int extraWidth;
    private Menu menu = null;
    private String mostLargeText = "";
    private String title = "";
    private int itemTextSize = -1;
    private int dimensStringSpace = 2;

    private ContextPopMenuView(Context context) {
        super(context);
        layoutInflater = LayoutInflater.from(context);
        menuData = new ArrayList<>();
        dimensStringSpace = context.getResources().getInteger(R.integer.pop_width_include_space_dimens);
    }

    private void notifyDataSetChanged() {
        if (null != menuAdapter) {
            menuAdapter.notifyDataSetChanged();
        }
    }

    private boolean isValidMenu() {
        return menuLayoutId != -1;
    }

    private boolean hasMenuData() {
        return menuData != null && menuData.size() > 0;
    }

    private void initView() {
        if (!isValidMenu()) {
            if (!hasMenuData()) {
                return;
            }
            Paint paint = new Paint();
            for (String title : menuData) {
                getMostLargeText(title, paint);
            }
        } else {
            menu = initMenu(layoutInflater.getContext());
        }
        initItemLayout();
        binding = PopMenuBinding.bind(layoutInflater.inflate(R.layout.pop_menu, null));
        initPopView(binding.getRoot());
        initTitle();
        menuAdapter = new MenuAdapter();
        menuAdapter.setOnItemClick(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != itemListener) {
                    if (isValidMenu()) {
                        itemListener.onItemClick(parent, view, position, menu.getItem(position).getItemId());
                    } else {
                        itemListener.onItemClick(parent, view, position, id);
                    }
                }
                notifyDataSetChanged();
                dismissPopView();
            }
        });
        binding.listviewContainer.setLayoutManager(new DisableScrollLinearManager(binding.listviewContainer.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.listviewContainer.setAdapter(menuAdapter);
    }

    private void initItemLayout() {
        View itemTextView = layoutInflater.inflate(R.layout.pop_simple_list, null);
        textView = itemTextView.findViewById(android.R.id.text1);
    }

    private float measureTextViewSize() {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(spec, spec);
        TextPaint textPaint = textView.getPaint();
        float textPaintWidth = textPaint.measureText(mostLargeText);
        return textPaintWidth + textView.getPaddingLeft() + textView.getPaddingRight()
                + binding.listviewContainer.getPaddingLeft()
                + binding.listviewContainer.getPaddingRight()
                + StringUtils.calculateSpaceNumForString(mostLargeText) * dimensStringSpace;
    }

    private void initPopView(View view) {
        setContentView(view);
        setWidth((int) measureTextViewSize() + extraWidth);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(view.getResources().getColor(R.color.transparent)));
        setFocusable(true);
        setOutsideTouchable(true);
    }

    private void initTitle() {
        if (StringUtils.isNullOrEmpty(title)) {
            binding.titleLayout.setVisibility(View.GONE);
        } else {
            ((TextView)binding.titleLayout.findViewById(R.id.textView_title)).setText(title);
            binding.titleLayout.findViewById(R.id.button_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissPopView();
                }
            });
        }
    }

    @SuppressLint("RestrictedApi")
    private Menu initMenu(Context context) {
        Menu menu = new MenuBuilder(context);
        MenuInflater menuInflater = new MenuInflater(context);
        menuInflater.inflate(menuLayoutId, menu);
        getMenuTitle(menu);
        return menu;
    }

    private void getMenuTitle(Menu menu) {
        removeItemById(menu);
        Paint paint = new Paint();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            getMostLargeText(item, paint);
            menuData.add(item.getTitle().toString());
        }
    }

    private void getMostLargeText(MenuItem item, Paint paint) {
        getMostLargeText(item.getTitle().toString(), paint);
    }

    private void getMostLargeText(String text, Paint paint) {
        if (paint.measureText(text) > paint.measureText(mostLargeText)) {
            mostLargeText = text;
        }
    }

    private void removeItemById(Menu menu) {
        if (itemId == null) {
            return;
        }
        for (int id : itemId) {
            menu.removeItem(id);
        }
    }

    public void showPopView() {
        if (target == null) {
            return;
        }
        Point p = new Point();
        p.x = offsetX;
        p.y = offsetY;
        showDynamicPositionPopView(target, p);
    }

    private class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
        private AdapterView.OnItemClickListener onItemClickListener;

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.textView.setText(menuData.get(position));
            holder.divider.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            if (onItemClickListener != null) {
                RxView.onClick(holder.itemView, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(null, v, holder.getAdapterPosition(), 0);
                    }
                });
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(layoutInflater.inflate(R.layout.pop_simple_list, parent, false));
        }

        @Override
        public int getItemCount() {
            return menuData == null ? 0 : menuData.size();
        }

        public void setOnItemClick(AdapterView.OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            View divider;
            TextView textView;
            public ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
                if (itemTextSize != -1) {
                    textView.setTextSize(itemTextSize);
                }
                divider = itemView.findViewById(R.id.view_divider);
            }
        }
    }

    public static class Builder {
        private ContextPopMenuView popView;

        public Builder(Context context) {
            popView = new ContextPopMenuView(context);
        }

        public Builder setTarget(View target) {
            popView.target = target;
            return this;
        }

        public Builder setItemListener(AdapterView.OnItemClickListener itemListener) {
            popView.itemListener = itemListener;
            return this;
        }

        public Builder setMenuLayoutId(int menuLayoutId) {
            popView.menuLayoutId = menuLayoutId;
            return this;
        }

        public Builder setRemoveItemId(int itemId) {
            ArrayList<Integer> itemIdList = new ArrayList<>();
            itemIdList.add(itemId);
            setRemoveItemId(itemIdList);
            return this;
        }

        public Builder setRemoveItemId(@Nullable int[] itemId) {
            if (itemId == null) {
                popView.itemId = new ArrayList<>();
            } else {
                popView.itemId = new ArrayList<>(Arrays.asList(toObject(itemId)));
            }
            return this;
        }

        private Integer[] toObject(final int[] array) {
            if (array == null) {
                return null;
            } else if (array.length == 0) {
                return new Integer[0];
            }
            final Integer[] result = new Integer[array.length];
            for (int i = 0; i < array.length; i++) {
                result[i] = Integer.valueOf(array[i]);
            }
            return result;
        }

        public Builder setRemoveItemId(Collection<Integer> itemId) {
            if (itemId == null) {
                popView.itemId = new ArrayList<>();
            } else {
                popView.itemId = new ArrayList<>(itemId);
            }
            return this;
        }

        public Builder setOffsetXY(int offsetX, int offsetY) {
            popView.offsetX = offsetX;
            popView.offsetY = offsetY;
            return this;
        }

        public Builder setExtraWidth(int extraWidth) {
            popView.extraWidth = extraWidth;
            return this;
        }

        public Builder setItemTextSize(int itemTextSize) {
            popView.itemTextSize = itemTextSize;
            return this;
        }

        public Builder setMenuData(List<String> menuData) {
            popView.menuData = menuData;
            return this;
        }

        public Builder setTitle(String title) {
            popView.title = title;
            return this;
        }

        public ContextPopMenuView create() {
            popView.initView();
            return popView;
        }
    }
}
