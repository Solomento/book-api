import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.lang.IndexOutOfBoundsException
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

fun main() {
    var downloadPages: List<DownloadPage> = listOf()

    var doc: Document = Jsoup.connect("https://www.libtxt.ru/page/1/").get()

    var divElements: Elements = doc.getElementsByAttributeValue("class", "line")

    var url: String
    var bookName: String
    var author: String
    for (element in divElements) {
        try {
            var aElement: Element = element.child(0).child(1)
            url = aElement.attr("href")
            bookName = aElement.text()
            aElement = element.child(0).child(0)
            author = aElement.text()

        } catch (e: IndexOutOfBoundsException) {
            continue
        }
        val textUrl = movToText(url)
        val text = getPage(textUrl)
        println("$author. $bookName: $textUrl\n$text")

    }

//    println(doc.allElements)
}

fun movToText(url: String): String {
    var textUrl = url.substring(0, url.indexOf("ru") + 3) + "chitat/" + url.substring(url.indexOf("ru") + 3)
    return textUrl
}

fun getPage(url: String): String {
    var i = 1
    val builder = StringBuilder()
    var lines: String
    while (true) {
        val textDoc = Jsoup.connect(getPageUrl(url, i)).get()
        val shortStoryElements: Elements = textDoc.getElementsByAttributeValue("id", "news-id-21")
        try {
            lines = shortStoryElements[0].child(0).child(0).child(0).child(0).child(3).toString()
        } catch (e: IndexOutOfBoundsException) {
            break
        }
        lines = lines.substring(lines.indexOf('>') + 2)
        lines = lines.replace(" <br>", "")
        lines = lines.substring(0, lines.length - 6)
        builder.append(lines)
        i++
    }
    return builder.toString()
}

fun getPageUrl(url: String, page: Int): String {
    return url.substring(0, url.length - 5) + '/' + page + ".html"
}

class DownloadPage(val url: String, val author: String, val bookName: String, val story: String) {

}