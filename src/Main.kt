import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.File
import java.lang.IllegalStateException
import java.lang.IndexOutOfBoundsException
import java.net.URL

fun main() {


    var downloadPages: List<Book> = listOf()

    val doc: Document = Jsoup.connect("https://royallib.com/authors-a.html").get()
    val authorElements: Element = doc.getElementsByAttributeValue("class", "navi").get(0)

    for (element in authorElements.child(0).children()) {
        for (author in element.child(0).children()) {
            try {
                val authorName = author.text()
                var authorUrl = author.attr("href")
                if (authorName == "" || authorUrl == "")
                    throw IndexOutOfBoundsException()
                // Ссылки на страницы авторов найдены
                authorUrl = "https:" + authorUrl
                println("$authorName:")
                for (book in getBooksUrls(authorUrl)) {
                    println("${book.first}: ")

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

                println("---------------------------------------------------------------------------------------------")
            } catch (e: IndexOutOfBoundsException) {
                continue
            }
        }
    }
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