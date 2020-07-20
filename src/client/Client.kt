package client

import server.Server
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.*
import kotlin.test.assertEquals


class Client {
    private var clientSocket: Socket? = null
    private var out: PrintWriter? = null
    private var `in`: BufferedReader? = null
    fun startConnection(ip: String?, port: Int) {
        clientSocket = Socket(ip, port)
        out = PrintWriter(clientSocket!!.getOutputStream(), true)
        `in` = BufferedReader(InputStreamReader(clientSocket!!.getInputStream()))
        val sc = Scanner(System.`in`)

        while (sc.hasNextLine()) {
            out!!.println(sc.nextLine())
            println(`in`!!.readLine())
        }
    }

    fun sendMessage(msg: String?): String {
        out!!.println(msg)
        return `in`!!.readLine()
    }

    fun stopConnection() {
        `in`!!.close()
        out!!.close()
        clientSocket!!.close()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val client = Client()
            client.startConnection("127.0.0.1", 6666)
        }
    }
}