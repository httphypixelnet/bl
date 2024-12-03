package xyz.lincolnsmp.main.api;

import com.zaxxer.hikari.HikariDataSource;
import xyz.lincolnsmp.main.MainPlugin;

import java.util.Objects;

public class External {

    private String jdbc;
    private HikariDataSource hikari;
    private MainPlugin plugin;
    private String jdbcUrl;

    public External(MainPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isConnected() {
        return this.hikari != null;
    }

    public void connect() {

        String jdbcDriver;
        final String mySQL = "MySQL";
        final String mySQLDriver = "com.mysql.cj.jdbc.Driver";
        final String USERNAME = plugin.getConfig().getString("db-username");
        final String PASSWORD = plugin.getConfig().getString("db-password");
        jdbcUrl = !Objects.equals(plugin.getConfig().getString("jdbc-url"), "") ? plugin.getConfig().getString("jdbc-url") : "jdbc:mysql://" + plugin.getConfig().getString("db-url") + "/" + plugin.getConfig().getString("db-name");
        plugin.getLogger().warning(jdbcUrl);
        this.jdbc = mySQL;
        jdbcDriver = mySQLDriver;

        if (!isConnected()) {
            hikari = new HikariDataSource();
            hikari.setDriverClassName(jdbcDriver);
            hikari.setJdbcUrl(this.getJdbcUrl());
            hikari.addDataSourceProperty("user", USERNAME);
            hikari.addDataSourceProperty("password", PASSWORD);
            System.out.println(this.jdbc + " Connection has been established!");
        }
    }

    public void disconnect() {

        if (isConnected()) {

            this.hikari.close();
            System.out.println(this.jdbc + " Connection has been closed!");

        }
    }

    private String getJdbcUrl() {
        return this.jdbcUrl;
    }

    public HikariDataSource getHikari() {
        return this.hikari;
    }
}
