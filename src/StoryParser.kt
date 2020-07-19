import StoryParser.TextColors.ANSI_RED
import StoryParser.TextColors.ANSI_RESET
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.File
import java.lang.Exception
import java.net.URL
import java.time.LocalDateTime
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object StoryParser {

    fun start() {

        val timeStart = System.currentTimeMillis()
        var downloadPages: List<Book> = listOf()

        val executorService: ExecutorService = Executors.newFixedThreadPool(10)

        val firstLetters = listOf("a", "b", "v", "g", "d", "e", "zh", "z", "i", "j", "k", "l", "m",
            "n", "o", "p", "r", "s", "t", "u", "f", "x", "c", "ch", "sh", "shh", "eh", "yu", "ya")

        var bookCounter = 0
        // Пробегаем по каждой букве алфавита
        for (letter in firstLetters) {
            val doc: Document = Jsoup.connect("https://royallib.com/authors-$letter.html").get()
            val authorElements: Element = doc.getElementsByAttributeValue("class", "navi").get(0)

            for (element in authorElements.child(0).children()) {
                // Идём по всем столбцам
                for (column in 0..3) {
                    val authors: Elements
                    try {
                        // Выбираем автора
                        authors = element.child(column).children()
                    } catch (e: IndexOutOfBoundsException) {
                         continue
                    }
                    for (author in authors) {
                        // Достаём его имя и ссылку на его страницу
                        val authorName: String
                        var authorUrl: String
                        var authorPrinted = false
                        try {
                            authorName = author.text()
                            authorUrl = author.attr("href")
                            if (authorName == "" || authorUrl == "")
                                throw IndexOutOfBoundsException()
                        } catch (e: IndexOutOfBoundsException) {
                            continue
                        }

                        val booksUrls: List<Pair<String, String>>
                        try {
                            // Ссылки на страницы авторов найдены. Записываем в консоль и собираем список доступных книг
                            authorUrl = "https:$authorUrl"
                            booksUrls = getBooksUrls(authorUrl)
                        } catch (e: java.lang.IndexOutOfBoundsException) {
                             continue
                        }

                        for (book in booksUrls) {

                            val zipUrl: URL
                            // Достаю ссылки на zip архивы со страниц
                            try {
                                val zipDoc: Document = Jsoup.connect(book.second).get()
                                val zipElement: Element = zipDoc.select("a:contains(Скачать в формате TXT)").first()
                                zipUrl = URL("https:" + zipElement.attr("href"))
                                // Узнаём размер файла, чтобы выбрать только рассказы
                                val size = zipElement.nextSibling().toString().filter { it.isDigit() }.toInt()
                                if (size <= 32) {
                                    if (!authorPrinted) {
                                        printAuthor(authorName)
                                        authorPrinted = true
                                    }
                                    println("${++bookCounter}: ${book.first}")
                                }
                                // Скачиваем архив и распаковываем текс
                                executorService.submit { Utils.unpackArchive(zipUrl, File("res")) }
                            } catch (e: Exception) {
                                continue
                            }
                        }

                        if (authorPrinted)
                            println("---------------------------------------------------------------------------------------------")
                    }
                }
            }
        }
        val timeFinish = System.currentTimeMillis()
        val executionTime = timeFinish - timeStart
        println(ANSI_RED + "Execution time: " + ANSI_RESET + "${executionTime / 1000 / 60 / 60} hours ${executionTime / 1000 / 60} minutes ${(executionTime / 1000) % 60} seconds")
    }

    /**
     * По заданному url страницы автора возвращает ссылки на все книги, которые содеражатся на этой странице.
     * При этом даже те страницы, с которых невозможно скачать книги.
     */
    private fun getBooksUrls(url: String): List<Pair<String, String>> {
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

    fun printAuthor(author: String) {
        println(ANSI_RED + "[${LocalDateTime.now()}]" + ANSI_RESET + "\t$author:")
    }


    /**
     *  Устаревший метод для считывания текста из онлайн читалки
     */
    fun getText(url: String): String {
        val txtDoc: Document = Jsoup.connect(url).get()
        val contentDiv: Element = txtDoc.getElementsByAttributeValue("id", "contentDiv")[0]
        println(contentDiv.allElements)
        var str = contentDiv.children().text()
        return str
    }

    /**
     *  Цвета текста в консоли
     */
    object TextColors {
        const val ANSI_RED = "\u001B[31m"
        const val ANSI_RESET = "\u001B[0m"
    }

}