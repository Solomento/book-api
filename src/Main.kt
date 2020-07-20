import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import utils.dataSource.database.Database
import java.io.File
import java.lang.Exception
import java.net.URL
import java.time.LocalDateTime
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

fun main() {
    StoryParser.start()
}