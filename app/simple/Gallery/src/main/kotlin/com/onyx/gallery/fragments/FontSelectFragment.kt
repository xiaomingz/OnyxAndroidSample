package com.onyx.gallery.fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.onyx.android.sdk.data.GPaginator
import com.onyx.android.sdk.utils.ResManager
import com.onyx.gallery.R
import com.onyx.gallery.databinding.FragmentEditMenuFontSelectBinding
import com.onyx.gallery.databinding.ItemTextStyleOptionChoiceBinding
import com.onyx.gallery.event.ui.FontChangeEvent
import com.onyx.gallery.models.ItemTextStyleOptionChoiceViewModel
import com.onyx.gallery.viewmodel.FontSelectViewModel
import com.onyx.gallery.views.DisableScrollLinearManager
import com.onyx.gallery.views.PageRecyclerView
import com.onyx.gallery.views.RecyclerViewBindingViewHolder

/**
 * Created by Leung on 2020/6/13
 */
class FontSelectFragment : BaseFragment<FragmentEditMenuFontSelectBinding, FontSelectViewModel>() {

    override fun getLayoutId(): Int = R.layout.fragment_edit_menu_font_select

    override fun onInitView(binding: FragmentEditMenuFontSelectBinding, contentView: View) {
        binding.pageView.run {
            adapter = FontSelectAdapter().apply { onTextStyleCheckedChange = getOnTextStyleCheckedChange() }
            layoutManager = DisableScrollLinearManager(ResManager.getAppContext())
            setOnPagingListener(getOnPagingListener())
        }
    }

    override fun onInitViewModel(context: Context, binding: FragmentEditMenuFontSelectBinding, rootView: View): FontSelectViewModel {
        val fontSelectViewModel = ViewModelProvider(requireActivity()).get(FontSelectViewModel::class.java)
        binding.viewModel = fontSelectViewModel.apply {
            fontAdapter = binding.pageView.adapter as FontSelectAdapter
            prevPage.observe(this@FontSelectFragment, Observer { binding.pageView.prevPage() })
            nextPage.observe(this@FontSelectFragment, Observer { binding.pageView.nextPage() })
            gPaginator = GPaginator().apply { binding.pageView.paginator = this }
            loadFontList()
        }
        binding.lifecycleOwner = this
        return fontSelectViewModel
    }

    private fun getOnPagingListener(): PageRecyclerView.OnPagingListener = object : PageRecyclerView.OnPagingListener() {
        override fun onPageChange(position: Int, itemCount: Int, pageSize: Int) = viewModel.updatePageIndicator()
    }

    private fun getOnTextStyleCheckedChange(): FontSelectAdapter.OnTextStyleCheckedChange = object : FontSelectAdapter.OnTextStyleCheckedChange {
        override fun onCheckedChange(position: Int, itemTextStyleOptionChoiceViewModel: ItemTextStyleOptionChoiceViewModel) {
            val fontInfo = itemTextStyleOptionChoiceViewModel.getFontInfo()
            postEvent(FontChangeEvent(fontInfo))
        }
    }

}

class FontSelectAdapter(val data: MutableList<ItemTextStyleOptionChoiceViewModel> = mutableListOf()) : PageRecyclerView.PageAdapter<RecyclerViewBindingViewHolder<ItemTextStyleOptionChoiceBinding>>() {

    interface OnTextStyleCheckedChange {
        fun onCheckedChange(position: Int, itemTextStyleOptionChoiceViewModel: ItemTextStyleOptionChoiceViewModel)
    }

    var onTextStyleCheckedChange: OnTextStyleCheckedChange? = null

    override fun getRowCount(): Int = ResManager.getInteger(R.integer.text_style_option_row)

    override fun getColumnCount(): Int = 1

    override fun getDataCount(): Int = data.size

    override fun onPageCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerViewBindingViewHolder<ItemTextStyleOptionChoiceBinding> {
        val layoutInflater = LayoutInflater.from(parent!!.context)
        val itemView = layoutInflater.inflate(R.layout.item_text_style_option_choice, parent, false)
        return RecyclerViewBindingViewHolder(itemView)
    }

    override fun onPageBindViewHolder(holder: RecyclerViewBindingViewHolder<ItemTextStyleOptionChoiceBinding>, position: Int) {
        holder.binding.viewModel = data[position]
        holder.binding.root.setOnClickListener {
            onTextStyleCheckedChange?.onCheckedChange(position, data[position])
            updateAdapterSelectedFont(position)
        }
        holder.binding.executePendingBindings()
    }

    private fun updateAdapterSelectedFont(position: Int) {
        for (i in data.indices) {
            val choiceViewModel = data[i]
            if (choiceViewModel.isChecked.get()) {
                choiceViewModel.isChecked.set(false)
                break
            }
        }
        val choiceViewModel = data[position]
        choiceViewModel.isChecked.set(true)
    }

}
