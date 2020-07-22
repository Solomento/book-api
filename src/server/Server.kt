package server

import utils.Unpacker
import utils.dao.BookDAO
import java.io.*
import java.lang.IllegalStateException
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.net.URL
import java.nio.charset.Charset
import kotlin.random.Random

/**
 *  Самый обыкновенный сервер. Если от клиента приходит любой запрос,
 *  сервер отсылает ему ссылку на случайный архив с рассказом
 */
class Server {
    private lateinit var serverSocket: ServerSocket
    val count = BookDAO.getAll().size

    fun start(port: Int) {
        serverSocket = ServerSocket(port)
        println("Books count: $count")

        while (true)
            ClientHandler(serverSocket.accept()).start()
    }

    fun stop() {
        serverSocket.close()
    }

    /**
     *  Обработчик клиента. Если сервер встречает нового клиента, он
     *  отправляет его сюда
     */
    inner class ClientHandler(val clientSocket: Socket) : Thread() {
        lateinit var out: PrintWriter
        lateinit var `in`: BufferedReader

        override fun run() {
            println("Клиент подключился: " + clientSocket.inetAddress.hostAddress)

            out = PrintWriter(clientSocket.getOutputStream(), true)
            `in` = BufferedReader(InputStreamReader(clientSocket.getInputStream()))

            var inputLine: String
            while (true) {
                try {
                    inputLine = `in`.readLine()
                    println("Новый запрос: " + clientSocket.inetAddress.hostAddress)
                } catch (e: SocketException) {
                    println("Клиет отключился")
                    break
                } catch (e: IllegalStateException) {
                    println("Клиент отключислся")
                    break
                }
                if (inputLine == "bye")
                    break

                var response: String
                do {
                    var txtFile: File? = null

                    while (txtFile == null) {
                        txtFile = getFile()
                    }

                    val br = BufferedReader(FileReader(txtFile, Charset.forName("windows-1251")))

                    val sb = StringBuffer()

                    // Формирование текста без ссылок
                    var i = 0
                    val lines = br.readLines()
                    var count = lines.count()
                    val notesInd = lines.indexOf("notes")
                    if (notesInd > 0)
                        count = notesInd
                    for (line in lines) {
                        i++
                        if ((i in 1..11) || ((line.contains("http") || line.toLowerCase().contains("royallib")
                                            || line.contains(".com") || line.contains("notes")))
                        )
                            continue

                        sb.append("$line$")
                    }

                    response = sb.toString()
                } while (!response.isRussian())
                out.println(response)
            }

            `in`.close()
            out.close()
            clientSocket.close()
        }

    }

    fun getFile(): File? {
        val book = BookDAO.getById(Random.nextInt(1, count + 1))
        println("${book.author}: ${book.title}")
        val url = book.url

        val txtFile: File?
        try {
            txtFile = Unpacker.unpackArchive(URL(url), File("res"))
        } catch (e: IOException) {
            System.err.println(e.message)
            return null
        }
        return txtFile
    }

    fun String.isRussian(): Boolean {
        val iCount = this.chars().filter { it.toChar() == 'i' }.count()
        val iCyrCount = this.chars().filter { it.toChar() == 'і' }.count()
        val yCount = this.chars().filter { it.toChar() == 'ъ' }.count()
        val iDensity = iCount.toDouble() / this.length
        val iCyrDensity = iCyrCount.toDouble() / this.length
        val yDensity = yCount.toDouble() / this.length
        if (iDensity >= 1E-3 || yDensity >= 5E-3) {

        }
        println("i\t$iCount: $iDensity")
        println("i\t$iCyrCount: $iCyrDensity")
        println("ъ\t$yCount: $yDensity")

        return iDensity < 1E-3 && yDensity < 5E-3 && iCyrDensity < 7E-3
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val server = Server()
            server.start(6666)
        }
    }
}