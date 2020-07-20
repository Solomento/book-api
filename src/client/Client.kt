package client

import server.Server
import utils.Unpacker
import java.io.*
import java.net.Socket
import java.net.URL
import java.nio.charset.Charset
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
            val response = sendMessage(sc.nextLine()) ?: break
            val txtFile: File?
            try {
                txtFile = Unpacker.unpackArchive(URL(response), File("res"))
            } catch (e: IOException) {
                System.err.println(e.message)
                continue
            }
            if (txtFile == null) {
                System.err.println("Файл не найден")
                continue
            }
            val br = BufferedReader(FileReader(txtFile, Charset.forName("windows-1251")))
            for (line in br.lines()) {
                println(line)
            }
        }
    }

    fun sendMessage(msg: String?): String? {
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
            client.startConnection("188.18.67.79", 6666)
        }
    }
}