package utils.dao

import Book
import utils.dataSource.DataSource
import utils.dataSource.database.Database
import java.sql.SQLException
import java.sql.Statement

object BookDAO {
    private val SELECT_ALL = "SELECT * FROM SPACE_MARINES"
    private val SELECT_BY_ID: String = SELECT_ALL + " WHERE ID = ?"
    private val INSERT = "INSERT INTO BOOKS " +
            "(AUTHOR, TITLE, URL) VALUES (?, ?, ?)"

    val dataSource = Database("postgres", "123")


    @Throws(DAOException::class)
    fun getAll(): Map<Int, Book> {
        val books: HashMap<Int, Book> = hashMapOf()
        val preparedStatement = dataSource.getPreparedStatement(SELECT_ALL)

        try {
            val resultSet = preparedStatement.executeQuery()
            while (resultSet.next()) {
                val id = resultSet.getInt("ID")
                val author = resultSet.getString("AUTHOR")
                val title = resultSet.getString("TITLE")
                val url = resultSet.getString("URL")

                val book = Book(id, author, title, url)
                books[id] = book
            }
        } catch (e: SQLException) {
            throw DAOException("Произошла ошибка при получении всего списка книг из базы данных")
        } finally {
            dataSource.closePreparedStatement(preparedStatement)
        }
        return books
    }

    @Throws(DAOException::class)
    fun getById(id: Int): Book {
        val preparedStatement = dataSource.getPreparedStatement(SELECT_BY_ID)
        val book: Book

        try {
            val resultSet = preparedStatement.executeQuery()
            if (resultSet.next()) {
                val author = resultSet.getString("AUTHOR")
                val title = resultSet.getString("TITLE")
                val url = resultSet.getString("URL")

                book = Book(id, author, title, url)
            } else {
                throw SQLException()
            }
        }catch (e: SQLException) {
            throw DAOException("Произошла ошибка при получении книги по полю ID из базы данных")
        } finally {
            dataSource.closePreparedStatement(preparedStatement)
        }
        return book
    }

    @Throws(DAOException::class)
    fun insert(book: Book): Book {
        val preparedStatement = dataSource.getPreparedStatement(INSERT, Statement.RETURN_GENERATED_KEYS);

        try {
            preparedStatement.setString(1, book.author)
            preparedStatement.setString(2, book.title)
            preparedStatement.setString(3, book.url)

            preparedStatement.execute()

            val generatedKeys = preparedStatement.generatedKeys

            if (generatedKeys.next()) {
                book.id = generatedKeys.getInt(1)
            }
        } catch (e: SQLException) {
            throw DAOException("Произошла ошибка при передаче элемента в базу данных")
        } finally {
            dataSource.closePreparedStatement(preparedStatement)
        }
        return book
    }
}