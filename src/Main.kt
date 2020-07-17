import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.File
import java.net.URL
import java.time.LocalDateTime

const val ANSI_RED = "\u001B[31m"
const val ANSI_RESET = "\u001B[0m"

fun main() {

    var timeStart = System.currentTimeMillis()
    var downloadPages: List<Book> = listOf()
    val firstLetters = listOf("a", "b", "v", "g", "d", "e", "zh", "z", "i", "j", "k", "l", "m",
        "n", "o", "p", "r", "s", "t", "u", "f", "x", "c", "ch", "sh", "shh", "y", "eh", "yu", "ya")

    for (letter in firstLetters) {
        val doc: Document = Jsoup.connect("https://royallib.com/authors-$letter.html").get()
        val authorElements: Element = doc.getElementsByAttributeValue("class", "navi").get(0)

        for (element in authorElements.child(0).children()) {
            for (author in element.child(0).children()) {
                val authorName: String
                var authorUrl: String
                try {
                    authorName = author.text()
                    authorUrl = author.attr("href")
                    if (authorName == "" || authorUrl == "")
                        throw IndexOutOfBoundsException()
                } catch (e: IndexOutOfBoundsException) {
                    continue
                }
                try {
                    // Ссылки на страницы авторов найдены
                    authorUrl = "https:" + authorUrl
                    println(ANSI_RED + "[${LocalDateTime.now()}]" + ANSI_RESET + "\t$authorName:")
                    for (book in getBooksUrls(authorUrl)) {
                        println(book.first)

                        val zipUrl: URL
                        // Достаю ссылки на zip архивы со страниц
                        try {
                            val zipDoc: Document = Jsoup.connect(book.second).get()
                            val zipElement: Element = zipDoc.select("a:contains(Скачать в формате TXT)").first()
                            zipUrl = URL("https:" + zipElement.attr("href"))
                        } catch (e: IllegalStateException) {
                            continue
                        }
                        Utils.unpackArchive(zipUrl, File("res"))
                    }
                } catch (e: java.lang.IndexOutOfBoundsException) {
                    // continue
                } finally {
                    println("---------------------------------------------------------------------------------------------")
                }
            }
        }
    }
    val timeFinish = System.currentTimeMillis()
    val executionTime = timeFinish - timeStart
    println(ANSI_RED + "Execution time: " + ANSI_RESET + "${executionTime / 1000 / 60 / 60} hours ${executionTime / 1000 / 60} minutes ${executionTime / 1000} seconds")
}

fun getBooksUrls(url: String): List<Pair<String, String>> {
    val booksUrls = mutableListOf<Pair<String, String>>()

    val authorDoc: Document = Jsoup.connect(url).get()
    val nameElements: Elements = authorDoc.getElementsByAttributeValue("title", "Скачать книгу")

    var url: String
    var bookName: String
    for (i in 0 until nameElements.size) {
        url = "https:" + nameElements[i].attr("href")
        bookName = nameElements[i].text()
        booksUrls.add(Pair(bookName, url))
    }
    return booksUrls
}




fun getText(url: String): String {
    val txtDoc: Document = Jsoup.connect(url).get()
    val contentDiv: Element = txtDoc.getElementsByAttributeValue("id", "contentDiv")[0]
    println(contentDiv.allElements)
    var str = contentDiv.children().text()
    return str
}


class Book(val url: String, val author: String, val bookName: String, val story: String) {

}