package utils.dataSource

import utils.dao.DAOException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException

abstract class DataSource @Throws(DataSourceException::class) constructor(
    private val user: String,
    private val password: String
) {
    protected var connection: Connection
    private val url: String = "jdbc:postgresql://localhost:5432/books"

    init {
        connection = setupConnection()
    }

    @Throws(DataSourceException::class)
    private fun setupConnection(): Connection {
        return try {
            Class.forName("org.postgresql.Driver")
            DriverManager.getConnection(url, user, password)
        } catch (e: SQLException) {
            throw DataSourceException("Error with database connection")
        } catch (e: ClassNotFoundException) {
            throw DataSourceException("org.postgresql.Driver is not available")
        }
    }

    @Throws(DAOException::class)
    fun getPreparedStatement(statement: String): PreparedStatement {
        return try {
            connection.prepareStatement(statement)
        } catch (e: SQLException) {
            throw DAOException("Error of SQL statement preparation")
        }
    }

    @Throws(DAOException::class)
    fun getPreparedStatement(sqlStatement: String, statement: Int): PreparedStatement {
        return try {
            connection.prepareStatement(sqlStatement, statement)
        } catch (e: SQLException) {
            throw DAOException("Error of SQL statement preparation")
        }
    }

    @Throws(DAOException::class)
    fun closePreparedStatement(preparedStatement: PreparedStatement?) {
        if (preparedStatement != null) {
            try {
                preparedStatement.close()
            } catch (e: SQLException) {
                throw DAOException("Error of SQL statement closing")
            }
        }
    }

}