package net.plasmere.streamline.utils.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.utils.TextUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BridgerDataSource {
    public static String doQuery(String from, String... args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();

            HikariConfig config = new HikariConfig();
            HikariDataSource ds;

            SingleSet<String, String> set = StreamLine.msbConfig.getQueryDatabaseSet(from);

            config.setJdbcUrl(StreamLine.msbConfig.getQueryLink(from));
            config.setUsername(set.key);
            config.setPassword(set.value);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("allowMultiQueries", "true");
            ds = new HikariDataSource(config);

            Connection connection = ds.getConnection();

            String query = StreamLine.msbConfig.getQuery(from);

            query = TextUtils.replaceArgs(query, args);

            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();

            String toReturn = "";

            while (resultSet.next()) {
                toReturn = String.valueOf(resultSet.getObject(1));
            }

            return toReturn;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return StreamLine.msbConfig.getValuesError();
        }
    }

    public static void doExecution(String from, String... args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();

            HikariConfig config = new HikariConfig();
            HikariDataSource ds;

            SingleSet<String, String> set = StreamLine.msbConfig.getExecutionDatabaseSet(from);

            config.setJdbcUrl(StreamLine.msbConfig.getExecutionLink(from));
            config.setUsername(set.key);
            config.setPassword(set.value);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("allowMultiQueries", "true");
            ds = new HikariDataSource(config);

            Connection connection = ds.getConnection();

            String execution = StreamLine.msbConfig.getExecution(from);

            execution = TextUtils.replaceArgs(execution, args);

            PreparedStatement statement = connection.prepareStatement(execution);

            statement.execute();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
