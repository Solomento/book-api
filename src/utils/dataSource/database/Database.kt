package utils.dataSource.database

import utils.dao.DAOException
import utils.dataSource.DataSource
import utils.dataSource.DataSourceException
import java.sql.SQLException

class Database @Throws(DataSourceException::class) constructor(
    private val user: String,
    private val password: String
): DataSource(user, password) {

    private val CREATE_IF_NOT_EXISTS_USERS_TABLE = "CREATE TABLE IF NOT EXISTS USERS (" +
            "ID SERIAL NOT NULL PRIMARY KEY," +
            "LOGIN VARCHAR UNIQUE NOT NULL," +
            "PASSWORD VARCHAR NOT NULL," +
            "EMAIL VARCHAR NOT NULL" +
            ")"
    private val CREATE_IF_NOT_EXISTS_BOOKS_TABLE =
        "CREATE TABLE IF NOT EXISTS BOOKS (" +
                "ID SERIAL NOT NULL PRIMARY KEY," +
                "AUTHOR VARCHAR NOT NULL CHECK(LENGTH(AUTHOR)>0)," +
                "TITLE VARCHAR NOT NULL CHECK(LENGTH(TITLE)>0)," +
                "URL VARCHAR NOT NULL CHECK(LENGTH(URL)>0)" +
                ")"

    init {
        initBooksTable()
    }

    @Throws(DatabaseException::class)
    private fun initBooksTable() {
        val preparedStatement = getPreparedStatement(CREATE_IF_NOT_EXISTS_BOOKS_TABLE)
        try {
            preparedStatement.executeUpdate()
        } catch (e: SQLException) {
            throw DatabaseException("Error while creation of Books table", e)
        } catch (e: DAOException) {
            throw DatabaseException("Error while creation of Books table", e)
        } finally {
            closePreparedStatement(preparedStatement)
        }
    }
}