package com.komar_olga.cloud;

import org.sqlite.SQLiteException;

import java.sql.*;

public class JDBCConnect implements AuthService {
    private static Connection connection;
    private static Statement stmt;
    private String login;
    private String pass;
    private String nick;
    private int id;
    private String actionPoint = "_auth true";




    public JDBCConnect(String login, String pass, int index) {
        try {
            start();
            if (index == 1) {
                nick = getNickByLoginPass(login, pass);
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS clients_cloud(\n" +
                        " id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "login TEXT UNIQUE,\n" +
                        "password TEXT,\n" +
                        "nick TEXT,\n" +
                        "free INTEGER \n" +
                        ");");
                try {
                    stmt.executeUpdate(String.format("INSERT INTO clients_cloud (login,password,nick,free) VALUES ('%s','%s', '%s',1)", login, pass, nick));
                    System.out.println("Подключился " + nick);
                    actionPoint = "_auth true";
                } catch (SQLiteException e) {
                    actionPoint = "_auth false";
                    //todo отправить сообщение, что такой пользователь существует
                    //e.printStackTrace();
                }
            } else {
                try (ResultSet rs = stmt.executeQuery("SELECT nick,id FROM clients_cloud WHERE login='" + login + "' AND password='" + pass + "';")) {

                    while (rs.next()) {
                        nick = rs.getString("nick");
                        id=rs.getInt("id");
                        System.out.println("Авторизовался " + nick+"["+id+"]");
                    }

                    if (nick != null) {
                        actionPoint = "_auth true";
                    } else {
                        actionPoint = "_auth false";
                    }

                } catch (SQLException e) {
                    System.out.println("Подключился 2 " + nick);
                    //todo исправить
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getActionPoint() {
        return actionPoint;
    }
    public int getId() {
        return id;
    }

    public String getNick() {
        return nick;
    }


    @Override
    public void start() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:clientsSQ.db");
            stmt = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Сервер аутентификации запущен");
    }

    @Override
    public String getNickByLoginPass(String login, String pass) throws SQLException {
        nick = "Client_" + login;
        return nick;
    }

    @Override
    public void stop() {
        System.out.println("Сервер аутентификации остановлен");
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
