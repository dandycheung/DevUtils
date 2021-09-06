package afkt.project.ui.adapter.multitype

import afkt.project.R
import afkt.project.databinding.AdapterConcatClassifyBinding
import afkt.project.model.bean.ClassifyBeanItem1
import android.view.LayoutInflater
import android.view.ViewGroup
import com.drakeet.multitype.ItemViewBinder
import dev.base.adapter.DevBaseViewBindingVH
import dev.base.adapter.newBindingViewHolder
import dev.utils.app.ResourceUtils
import dev.utils.app.helper.quick.QuickHelper

/**
 * detail: 一级分类 Adapter
 * @author Ttt
 */
class Classify1ItemViewBinder : ItemViewBinder<ClassifyBeanItem1, DevBaseViewBindingVH<AdapterConcatClassifyBinding>>() {

    override fun onCreateViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): DevBaseViewBindingVH<AdapterConcatClassifyBinding> {
        return newBindingViewHolder(parent, R.layout.adapter_concat_classify)
    }

    override fun onBindViewHolder(
        holder: DevBaseViewBindingVH<AdapterConcatClassifyBinding>,
        item: ClassifyBeanItem1
    ) {
        val itemObj = item.obj

        QuickHelper.get(holder.binding.vidAccTitleTv)
            .setText(itemObj.name)
            .setBackgroundColor(itemObj.background)
            .setPaddingLeft(ResourceUtils.getDimensionInt(R.dimen.un_dp_20))
    }
}