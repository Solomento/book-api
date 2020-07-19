import java.io.*
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.Files
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import kotlin.math.max
import kotlin.math.min

object Utils {
    /**
     * Unpack an archive from a URL
     *
     * @param url
     * @param targetDir
     * @return the file to the url
     * @throws IOException
     */
    @Throws(IOException::class)
    fun unpackArchive(url: URL, targetDir: File) {
        if (!targetDir.exists()) {
            targetDir.mkdirs()
        }
        val `in` = BufferedInputStream(url.openStream(), 1024)
        // make sure we get the actual file
        val zip = File.createTempFile("tmp", ".zip", targetDir)
        val out = BufferedOutputStream(FileOutputStream(zip))
        copyInputStream(`in`, out)
        out.flush()
        out.close()
        unpackArchive(zip, targetDir)
        zip.deleteRecursively()
    }

    /**
     * Unpack a zip file
     *
     * @param theFile
     * @param targetDir
     * @return the file
     * @throws IOException
     */
    @Throws(IOException::class)
    fun unpackArchive(theFile: File, targetDir: File): File {
        if (!theFile.exists()) {
            throw IOException(theFile.absolutePath + " does not exist")
        }
        if (!buildDirectory(targetDir)) {
            throw IOException("Could not create directory: $targetDir")
        }
        val WIN = Charset.forName("CP866")
        val zipFile = ZipFile(theFile, WIN)
        val entries: Enumeration<*> = zipFile.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement() as ZipEntry
            val file = File(targetDir, File.separator + entry.name.substring(0, min(entry.name.length, 120)) + ".txt")
            if (!buildDirectory(file.parentFile)) {
                throw IOException("Could not create directory: " + file.parentFile)
            }
            if (!entry.isDirectory) {
                if (entry.name.contains(".txt"))
                    copyInputStream(zipFile.getInputStream(entry), BufferedOutputStream(FileOutputStream(file)))
            } else {
                if (!buildDirectory(file)) {
                    throw IOException("Could not create directory: $file")
                }
            }
        }
        zipFile.close()
        return theFile
    }

    @Throws(IOException::class)
    fun copyInputStream(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var len = `in`.read(buffer)
        while (len >= 0) {
            out.write(buffer, 0, len)
            len = `in`.read(buffer)
        }
        `in`.close()
        out.close()
    }

    fun buildDirectory(file: File): Boolean {
        return file.exists() || file.mkdirs()
    }
}