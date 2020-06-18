package afkt.project.ui.activity;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import afkt.project.R;
import afkt.project.base.app.BaseToolbarActivity;
import butterknife.BindView;
import dev.utils.app.ResourceUtils;
import dev.utils.app.helper.ViewHelper;

/**
 * detail: LinearSnapHelper - RecyclerView
 * @author Ttt
 * <pre>
 *     LinearSnapHelper : 滑动多页居中显示, 类似 Gallery
 * </pre>
 */
public class LinearSnapActivity extends BaseToolbarActivity {

    // = View =
    @BindView(R.id.vid_bvr_recy)
    RecyclerView vid_bvr_recy;

    @Override
    public int getLayoutId() {
        return R.layout.base_view_recyclerview;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewGroup parent = (ViewGroup) vid_bvr_recy.getParent();
        // 根布局处理
        ViewHelper.get().setPadding(parent, 0);
    }

    @Override
    public void initValues() {
        super.initValues();
    }
}