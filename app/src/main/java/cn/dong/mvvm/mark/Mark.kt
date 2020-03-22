package cn.dong.mvvm.mark

/**
 * @author zhaodong on 2020/03/22.
 */
data class Mark(
        val name: String,
        var selected: Boolean = false
) {

    override fun equals(other: Any?): Boolean {
        return if (other is Mark) {
            other.name == name
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
