package cn.dong.mvvm.mark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.dong.mvvm.R
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.activity_mark.*
import kotlinx.android.synthetic.main.item_mark.view.*

/**
 * 只承担 View 的职责
 * 1.根据数据渲染 UI
 * 2.将用户操作通知给 ViewModel
 *
 * @author zhaodong on 2020/03/22.
 */
class MarkActivity : AppCompatActivity() {

    private val viewModel: MarkViewModel by viewModels()

    private val allMarkAdapter = MarkAdapter()
    private val selectMarkAdapter = MarkAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark)

        setupAllMarkList()
        setupSelectMarkList()
        saveButton.setOnClickListener { viewModel.saveSelectMarks() }
    }

    private fun setupAllMarkList() {
        allMarkList.layoutManager = newMarkLayoutManager()
        allMarkList.addItemDecoration(newMarkItemDecoration())
        allMarkAdapter.onMarkClickListener = viewModel.onAllMarkClickListener
        allMarkList.adapter = allMarkAdapter

        viewModel.allMarksData.observe(this) {
            allMarkAdapter.submitList(it)
        }
    }

    private fun setupSelectMarkList() {
        selectMarkList.layoutManager = newMarkLayoutManager()
        selectMarkList.addItemDecoration(newMarkItemDecoration())
        selectMarkAdapter.onMarkClickListener = viewModel.onSelectMarkClickListener
        selectMarkList.adapter = selectMarkAdapter

        viewModel.selectMarksData.observe(this) { marks ->
            selectMarkAdapter.submitList(marks)

            val previewMarks = marks.joinToString(separator = "-") { it.name }
            selectMarksPreviewText.text = "preview: $previewMarks"
        }
    }

    private fun newMarkLayoutManager(): RecyclerView.LayoutManager {
        return FlexboxLayoutManager(this, FlexDirection.ROW)
    }

    private fun newMarkItemDecoration(): RecyclerView.ItemDecoration {
        return FlexboxItemDecoration(this).apply {
            setDrawable(getDrawable(R.drawable.mark_divider))
        }
    }
}

interface OnMarkClickListener {
    fun onMarkClick(position: Int)
}

private class MarkAdapter : ListAdapter<Mark, MarkViewHolder>(MarkDiffCallback()) {
    var onMarkClickListener: OnMarkClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_mark, parent, false)
        return MarkViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MarkViewHolder, position: Int) {
        val mark = getItem(position)
        holder.nameText.text = mark.name
        holder.itemView.isSelected = mark.selected
        holder.itemView.setOnClickListener {
            onMarkClickListener?.onMarkClick(holder.adapterPosition)
        }
    }
}

private class MarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val nameText: TextView = itemView.mark_name
}

private class MarkDiffCallback : DiffUtil.ItemCallback<Mark>() {

    override fun areItemsTheSame(oldItem: Mark, newItem: Mark): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Mark, newItem: Mark): Boolean {
        return oldItem.selected == newItem.selected
    }

}
