import android.util.LruCache
import java.io.File

interface ImageCache {
    fun getFile(url: String?): File?
    fun putFile(url: String?, file: File?)
}

class CacheLRU constructor(maxSize: Int = DEFAULT_CACHE_SIZE) :
    LruCache<String?, File?>(maxSize), ImageCache {
    override fun getFile(url: String?): File {
        return get(url)!!
    }

    override fun putFile(url: String?, file: File?) {

    }

    override fun sizeOf(key: String?, value: File?): Int {
        return value?.length()?.toInt() ?: 0
    }

    companion object {
        private val DEFAULT_CACHE_SIZE = (Runtime.getRuntime().maxMemory() / 1024).toInt() / 8
    }
}