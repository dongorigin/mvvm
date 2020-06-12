package cn.dong.mvvm

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cn.dong.mvvm.mark.MarkActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val max = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edit.filters = arrayOf(TextLengthFilter(max * 2))
        edit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                log("beforeTextChanged s=$s")
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                log("onTextChanged s=$s")
            }

            override fun afterTextChanged(s: Editable) {
                log("afterTextChanged s=$s")
                editCounter.text = "${s.textCount() / 2} / $max"
            }
        })
        edit.text = null

        markButton.setOnClickListener {
            startActivity(Intent(this, MarkActivity::class.java))
        }
    }
}

class DebugLengthFilter(max: Int) : InputFilter.LengthFilter(max) {
    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val filterSource = super.filter(source, start, end, dest, dstart, dend)
        Log.d(
            "main", "filter: source=$source, start=$start, end=$end, " +
                    "dest=$dest, dstart=$dstart, dend=$dend, filterSource=[$filterSource]"
        )
        return filterSource
    }
}

/** [max] 最大数量，英文和字符算 1 个数量，其他字符如中文算 2 个数量 */
class TextLengthFilter(private val max: Int) : InputFilter {
    /**
     * [dstart, dend) 是目标替换区间，当选择了部分文字时，这个区间才>0，否则两者相等都是输入位置。
     * 返回是最终输入的结果，比如返回 source 表示不改变原有输入
     */
    override fun filter(
        source: CharSequence, //新输入的文字
        start: Int, //开始位置（inclusive）
        end: Int, //结束位置（exclusive）
        dest: Spanned, //当前显示的内容
        dstart: Int, //输入目标开始位置
        dend: Int // 输入目标结束位置
    ): CharSequence? {
        log(
            "filter: source=$source, start=$start, end=$end, " +
                    "dest=$dest, dstart=$dstart, dend=$dend"
        )
        // 计算剩余数量
        // dstart = dend 时表示不替换原有字符
        // dstart < dend 时表示替换区间 [dstart, dend) 的字符，通常是光标选中的部分
        var destTextCount = 0 // 当前字符被替换后的数量
        for ((index, char) in dest.withIndex()) {
            if (index < dstart || index >= dend) {
                // 迭代文字剩余部分，剔除了被替换的区间
                destTextCount += char.textCount()
            }
        }
        val keep = max - destTextCount
        log("max=$max, destTextCount=$destTextCount, keep=$keep")
        if (keep <= 0) {
            return "" // 禁止输入
        } else {
            var sourceTextCount = 0
            for (index in start until end) {
                val char = source[index]
                sourceTextCount += char.textCount()
                if (sourceTextCount > keep) {
                    if (index == start) {
                        // 第一个字符就超过限制了，拒绝输入
                        return ""
                    } else {
                        // 当前字符超了，从前一个字符截取
                        var sourceEnd = index // (exclusive)
                        if (source[sourceEnd - 1].isHighSurrogate()) {
                            sourceEnd-- // 如果前一个字符是高位，需要再向前截取一位
                            if (sourceEnd == start) {
                                return ""
                            }
                        }
                        return source.subSequence(start, sourceEnd)
                    }
                }
            }
            return null // keep original
        }
    }
}

private fun log(message: String) {
    Log.i("TextCounter", message)
}


fun Char.textCount(): Int {
    return if (toInt() < 128) {
        1 // ASCII
    } else {
        2
    }
}

/**
 * 字数，英文算 1 个字数，中文算 2 个字数。
 * 注意业务上是英文算 0.5 个字数，中文算 1 个字数，为避免浮点数误差，乘 2 统一返回整数，由业务方根据需要转换（比如四舍五入）。
 * 计算的 UTF-16 数量，但 Emoji 字符一般会包含 2 个 UTF-16，特殊的 Emoji 还可能包含更多个（比如 Modifier sequences 和 ZWJ sequences）
 * 业界普遍使用 UTF-16 数量作为文字数量，比如 Google 翻译、Witter、微博，它们对 Emoji 算字数也是如此。
 */
fun CharSequence.textCount(): Int = sumBy { it.textCount() }
