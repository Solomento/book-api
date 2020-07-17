import java.io.*
import java.net.URL
import java.nio.charset.Charset
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

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
    fun unpackArchive(url: URL, targetDir: File): File {
        if (!targetDir.exists()) {
            targetDir.mkdirs()
        }
        val `in`: InputStream = BufferedInputStream(url.openStream(), 1024)
        // make sure we get the actual file
        val zip = File.createTempFile("arc", ".zip", targetDir)
        val out: OutputStream = BufferedOutputStream(FileOutputStream(zip))
        copyInputStream(`in`, out)
        out.close()
        return unpackArchive(zip, targetDir)
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
        val UTF = Charset.forName("Windows-1251")
        val zipFile = ZipFile(theFile, UTF)
        val entries: Enumeration<*> = zipFile.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement() as ZipEntry
            val file = File(targetDir, File.separator + entry.name)
            if (!buildDirectory(file.parentFile)) {
                throw IOException("Could not create directory: " + file.parentFile)
            }
            if (!entry.isDirectory) {
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