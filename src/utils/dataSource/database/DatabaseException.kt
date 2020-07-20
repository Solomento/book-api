package utils.dataSource.database

import utils.dataSource.DataSourceException

class DatabaseException(message: String?, cause: Throwable?) :
    DataSourceException(message, cause)