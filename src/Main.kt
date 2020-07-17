import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.lang.IndexOutOfBoundsException

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
                    println("${book.first}: ${book.second}")
                }

                println("---------------------------------")
            } catch (e: IndexOutOfBoundsException) {
                continue
            }
        }
    }
}

fun getBooksUrls(url: String): List<Pair<String, String>> {
    var booksUrls = mutableListOf<Pair<String, String>>()

    val authorDoc: Document = Jsoup.connect(url).get()
    val nameElements: Elements = authorDoc.getElementsByAttributeValue("title", "Скачать книгу")
    val urlElements: Elements = authorDoc.getElementsByAttributeValue("title", "Читать книгу On-line")

    var url: String
    var bookName: String
    for (i in 0 until urlElements.size) {
        url = "https:" + urlElements[i].attr("href")
        bookName = nameElements[i].text()
        booksUrls.add(Pair(bookName, url))
    }
    return booksUrls
}



class Book(val url: String, val author: String, val bookName: String, val story: String) {

}