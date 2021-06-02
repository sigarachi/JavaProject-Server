/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.servermaven;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author1 lalal
 * @author2 Toropchinov
 * 
 */
public class LoginHandler implements Runnable {
    private Server server;
    
    private Socket clientSocket = null;
    private String clientLogin;
    private String clientPassword;

    private PrintWriter messageToClient;
    
    private Connection conDatabase;
    
    public LoginHandler(Socket clientSocket, Server server, JInputMessage auth, Connection conDb){
        
        try {
            this.server = server;
            this.clientSocket = clientSocket;
            this.clientLogin = auth.login;
            this.clientPassword = auth.password;
            this.messageToClient = new PrintWriter(clientSocket.getOutputStream());
            this.conDatabase = conDb;
            
        } catch (IOException ex) {
            Logger.getLogger(LoginHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run(){
        try{
            //запрос в БД
            String csql = "SELECT userID, user_login FROM users WHERE user_login= '" +clientLogin+ "' "
                    + "AND user_password = '"+clientPassword+"'";    
            
            //Создали statement 
            Statement st = conDatabase.createStatement();
            st.executeQuery(csql);
            ResultSet rs = st.executeQuery(csql);
            
            //вернули userID и user_login
            while(rs.next()){
                int id = rs.getInt ("userID");
                String login = rs.getString("user_login");
            break;
            }

        }catch(Exception ex){
            ex.getStackTrace();
        }
        finally{
            this.close();
        }
    }
    
    
    
    private void close(){
        //Здесь допишем сообщение о резульате авторизации
        server.removeClientOnLogin(this);
    }
}
