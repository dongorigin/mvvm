package cn.dong.mvvm

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cn.dong.mvvm.mark.MarkActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edit.filters = arrayOf(TextLengthFilter(10))

        markButton.setOnClickListener {
            startActivity(Intent(this, MarkActivity::class.java))
        }
    }
}

class TextLengthFilter(val max: Int) : InputFilter {
    /**
     * [dstart, dend) 是目标替换区间，当选择了部分文字时，这个区间才>0，否则两者相等都是输入位置。
     * 返回是最终输入的结果，比如返回 source 表示不改变原有输入
     */
    override fun filter(
        source: CharSequence, //新输入的文字
        start: Int, //开始位置
        end: Int, //结束位置
        dest: Spanned, //当前显示的内容
        dstart: Int, //输入目标开始位置
        dend: Int // 输入目标结束位置
    ): CharSequence {
        Log.d(
            "main", "filter: source=$source, start=$start, end=$end, " +
                    "dest=$dest, dstart=$dstart, dend=$dend"
        )

        return source
    }

}

// 英文和字符算 1 个数量，其他字符如中文算 2 个数量
fun textCount(source: CharSequence, start: Int, end: Int): Int {
    if (start < 0 || end >= source.length || start > end) {
        return source.length
    }
    var count = 0
    for (index in start until end) {
        val char = source[index]
        count += if (char.toInt() < 128) {
            1 // ASCII
        } else {
            2
        }
    }
    return count
}
