package cn.dong.mvvm.mark

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * 负责管理数据
 *
 * @author zhaodong on 2020/03/22.
 */
class MarkViewModel : ViewModel() {

    private val allMarks: MutableList<Mark> = mutableListOf()
    private val selectMarks: MutableList<Mark> = mutableListOf()

    val allMarksData = MutableLiveData<List<Mark>>()
    val selectMarksData = MutableLiveData<List<Mark>>()

    val onAllMarkClickListener = object : OnMarkClickListener {
        override fun onMarkClick(position: Int) {
            val mark = allMarks[position]
            mark.selected = !mark.selected
            if (mark.selected) {
                selectMarks.add(mark)
            } else {
                selectMarks.remove(mark)
            }
            notifyMarksUpdated()
        }
    }

    val onSelectMarkClickListener = object : OnMarkClickListener {
        override fun onMarkClick(position: Int) {
            val mark = selectMarks[position]
            mark.selected = false
            selectMarks.remove(mark)
            notifyMarksUpdated()
        }
    }

    init {
        testData()
    }

    private fun testData() {
        val testMarks = MutableList(10) { Mark(it.toString()) }
        allMarks.addAll(testMarks)
        notifyMarksUpdated()
    }

    fun saveSelectMarks() {
        // 需要保存数据时，唯一的数据源就在这里，保证了一致性，维护和理解都方便
        selectMarks
    }

    private fun notifyMarksUpdated() {
        // 深拷贝是为了 DiffUtils 能对比列表数据变化，也是为了保护数据源
        allMarksData.value = allMarks.deepCopy()
        selectMarksData.value = selectMarks.deepCopy()
    }

    private fun List<Mark>.deepCopy() : List<Mark> = map { it.copy() }
}
