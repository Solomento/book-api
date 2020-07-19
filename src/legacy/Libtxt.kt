package legacy

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.lang.IndexOutOfBoundsException
import java.lang.StringBuilder

class Libtxt {

    fun scan() {
        var booksCount = 0
        for (k in 1..2656) {

            val doc: Document = Jsoup.connect("https://www.libtxt.ru/page/$k/").get()
            val divElements: Elements = doc.getElementsByAttributeValue("class", "line")

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
                val text = getFullText(textUrl)
                if (text.length < 30000 && !text.contains("<a href")) {
                    println(++booksCount)
                    println("$author. $bookName: $textUrl\n$text")
                }
            }
        }
    }

    fun movToText(url: String): String {
        return url.substring(0, url.indexOf("ru") + 3) + "chitat/" + url.substring(url.indexOf("ru") + 3)
    }

    fun getFullText(url: String): String {
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
            lines = lines.replace(" <br>", "").replace("&nbsp;", "")
            lines = lines.substring(0, lines.length - 6)
            builder.append(lines)
            i++
        }
        return builder.toString()
    }

    fun getPageUrl(url: String, page: Int): String {
        return url.substring(0, url.length - 5) + '/' + page + ".html"
    }
}