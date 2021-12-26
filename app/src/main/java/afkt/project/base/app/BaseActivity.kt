package afkt.project.base.app

import afkt.project.ui.activity.Utils2
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import dev.base.expand.mvp.MVP
import dev.utils.app.assist.floating.IFloatingActivity

/**
 * detail: Base ViewBinding 基类
 * @author Ttt
 */
abstract class BaseActivity<VB : ViewBinding> :
    BaseMVPActivity<MVP.Presenter<out MVP.IView, out MVP.IModel>, VB>(),
    IFloatingActivity {

    override fun createPresenter(): MVP.Presenter<out MVP.IView, out MVP.IModel> {
        return MVP.Presenter(MVP.ViewImpl())
    }

    // =====================
    // = IFloatingActivity =
    // =====================

    override fun getAttachActivity(): Activity {
        return this
    }

    override fun getMapFloatingKey(): String {
        return this.toString()
    }

    override fun getMapFloatingView(): View {
        return Utils2.instance.createFloatingView(this)
    }

    override fun getMapFloatingViewLayoutParams(): ViewGroup.LayoutParams {
        return Utils2.instance.createLayoutParams(this)
    }

    // =

    override fun onResume() {
        super.onResume()
        // 添加悬浮窗 View
        Utils2.instance.addFloatingView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 移除悬浮窗 View
        Utils2.instance.removeFloatingView(this)
    }
}