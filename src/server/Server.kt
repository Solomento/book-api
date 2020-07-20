package server

import utils.dao.BookDAO
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
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
            out = PrintWriter(clientSocket.getOutputStream(), true)
            `in` = BufferedReader(InputStreamReader(clientSocket.getInputStream()))

            var inputLine: String
            while (true) {
                inputLine = `in`.readLine()
                if (inputLine == "bye")
                    break
                out.println(BookDAO.getById(Random.nextInt(1, count + 1)).url)
            }

            `in`.close()
            out.close()
            clientSocket.close()
        }

    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val server = Server()
            server.start(6666)
        }
    }
}