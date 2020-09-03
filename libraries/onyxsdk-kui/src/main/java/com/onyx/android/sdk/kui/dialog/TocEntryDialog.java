package com.onyx.android.sdk.kui.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.onyxsdk_kui.R;
import com.example.onyxsdk_kui.databinding.DialogTocBinding;
import com.onyx.android.sdk.kui.data.TocEntry;
import com.onyx.android.sdk.kui.rxbinding.RxView;
import com.onyx.android.sdk.kui.utils.DashLineItemDivider;
import com.onyx.android.sdk.kui.view.PageRecyclerView;
import com.onyx.android.sdk.kui.view.TocTreeRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.ResManager;

import java.util.ArrayList;

import androidx.databinding.DataBindingUtil;

/**
 * Created by suicheng on 2017/4/29.
 */
public class TocEntryDialog<T> extends OnyxAlertDialogSupport {

    private String title;
    private TocEntry<T> tocEntry;

    protected DialogTocBinding binding;

    private TocTreeRecyclerView treeRecyclerView;
    private int rowCount;

    private TocTreeRecyclerView.Callback itemActionCallBack;
    private View.OnClickListener createActionListener;
    private TextView textViewBottomPageSize;
    private TextView textViewbottomTotal;

    public TocEntryDialog() {
    }

    public TocEntryDialog(TocEntry<T> tocEntry, String title) {
        this.tocEntry = tocEntry;
        this.title = title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setParams(new Params().setEnableTittle(true)
                .setCustomContentLayoutResID(R.layout.dialog_toc)
                .setCustomLayoutHeight(getCustomLayoutHeight())
                .setEnableCloseButtonTopRight(true)
                .setEnableFunctionPanel(false)
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageView) {
                        initView(customView);
                    }
                })
                .setCustomLayoutBackgroundResId(R.drawable.dialog_rectangle_background)
        );
        super.onCreate(savedInstanceState);
    }

    protected void initView(View customView) {
        rowCount = ResManager.getInteger(R.integer.dialog_toc_tree_recycler_view_row);
        binding = DataBindingUtil.bind(customView);
        binding.createLayout.getLayoutParams().width = (int) (getDefaultWidth(getParams().isUsePercentageWidth()) *
                ResManager.getFloatValue(R.dimen.dialog_toc_create_btn_width_percent));
        RxView.onClick(binding.createLayout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateClickAction(v);
            }
        });
        initTitleBarView(customView);
        initTreeView(binding);
    }

    private void initTitleBarView(View parentView) {
        textViewbottomTotal = parentView.findViewById(R.id.bottom_total_indicator);
        textViewBottomPageSize = parentView.findViewById(R.id.bottom_page_size_indicator);
        TextView dialogTittleView = parentView.getRootView().findViewById(R.id.textView_title);
        dialogTittleView.setText(title);
        RxView.onClick(parentView.getRootView().findViewById(R.id.button_close), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initTreeView(DialogTocBinding binding) {
        ArrayList<TocTreeRecyclerView.TocTreeNode> rootNodes = buildTreeNodesFromLibraryList(tocEntry);
        DashLineItemDivider decoration = new DashLineItemDivider(ResManager.getColor(R.color.dash_line_color));
        treeRecyclerView = binding.contentPageView;
        treeRecyclerView.addItemDecoration(decoration);
        treeRecyclerView.setItemDecorationHeight(decoration.getDividerHeight());
        treeRecyclerView.setDefaultPageKeyBinding();
        treeRecyclerView.bindTree(rootNodes, new TocTreeRecyclerView.Callback() {
            @Override
            public void onTreeNodeClicked(TocTreeRecyclerView.TocTreeNode node) {
                if (node.getTag() == null) {
                    return;
                }
                onItemClickAction(node);
            }

            @Override
            public void onItemCountChanged(int position, int itemCount) {
                onPageChanged();
                onItemCountChangedAction(position, itemCount);
            }
        }, rowCount);

        treeRecyclerView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                onPageChanged();
            }
        });
        updatePageIndicatorView();
    }

    private void updatePageIndicatorView() {
        int currentPage = treeRecyclerView.getPaginator().getCurrentPage() + 1;
        int totalPage = treeRecyclerView.getPaginator().pages();
        int total = treeRecyclerView.getPaginator().getSize();
        if (totalPage == 0) {
            totalPage = 1;
        }
        String totalStr = getResources().getString(R.string.total_format, total);
        String pageSizeStr = getResources().getString(R.string.page_indicator, currentPage, totalPage);
        textViewbottomTotal.setText(totalStr);
        textViewBottomPageSize.setText(pageSizeStr);
    }

    private void onPageChanged() {
        updatePageIndicatorView();
    }

    private void onItemClickAction(TocTreeRecyclerView.TocTreeNode node) {
        if (itemActionCallBack != null) {
            itemActionCallBack.onTreeNodeClicked(node);
        }
    }

    private void onItemCountChangedAction(int position, int itemCount) {
        if (itemActionCallBack != null) {
            itemActionCallBack.onItemCountChanged(position, itemCount);
        }
    }

    private void onCreateClickAction(View view) {
        if (createActionListener != null) {
            createActionListener.onClick(view);
        }
    }

    private ArrayList<TocTreeRecyclerView.TocTreeNode> buildTreeNodesFromLibraryList(TocEntry<T> tocEntry) {
        ArrayList<TocTreeRecyclerView.TocTreeNode> nodes = new ArrayList<>();
        for (TocEntry<T> child : tocEntry.children) {
            nodes.add(buildTreeNode(null, child));
        }
        return nodes;
    }

    private TocTreeRecyclerView.TocTreeNode buildTreeNode(TocTreeRecyclerView.TocTreeNode parent, TocEntry<T> entry) {
        String desc = CollectionUtils.isNullOrEmpty(entry.children) ? "" : String.valueOf(CollectionUtils.getSize(entry.children));
        TocTreeRecyclerView.TocTreeNode node = new TocTreeRecyclerView.TocTreeNode(parent, entry.getTitle(),
                desc, entry.item);
        if (!CollectionUtils.isNullOrEmpty(entry.children)) {
            for (TocEntry<T> child : entry.children) {
                node.addChild(buildTreeNode(node, child));
            }
        }
        return node;
    }

    private int getCustomLayoutHeight() {
        return ResManager.getDimensionPixelSize(R.dimen.dialog_toc_content_height);
    }

    public TocEntryDialog<T> setItemActionCallBack(TocTreeRecyclerView.Callback itemActionCallBack) {
        this.itemActionCallBack = itemActionCallBack;
        return this;
    }

    public TocEntryDialog<T> setCreateActionListener(View.OnClickListener listener) {
        this.createActionListener = listener;
        return this;
    }
}
