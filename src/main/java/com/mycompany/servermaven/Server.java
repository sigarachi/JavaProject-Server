/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.servermaven;
 
/**
 *
 * @author1 Dmitriev
 * @author2 Toropchinov
 * 
 */
 
 
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.runtime.ScriptObject;
 
public class Server extends ScriptObject {
 
    static final int PORT = 5600;
    
    private ArrayList<LoginHandler> clientsOnLogin = new ArrayList<LoginHandler>();
    private BufferedReader clientInputStream ;
 
    public Server() {
 
        Socket clientSocket = null;
 
        ServerSocket serverSocket = null;
 
        String dbUser = "postgres";
        String dbPassword = "root";
        String dbUrl = "jdbc:postgresql://localhost:5432/postgres";
        String drvName = "org.postgresql.Driver";
        Connection conDatabase = null;
 
        try {
            Class.forName(drvName);
            conDatabase = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port = " + PORT);
            clientSocket = serverSocket.accept();
            clientInputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while (true) {
                
                
                
                String input = clientInputStream.readLine();
 
                ObjectMapper mapper = new ObjectMapper();
                JInputMessage message = mapper.readValue(input, JInputMessage.class);
                //System.out.println("message" + message.toString());
 
                if (message.type.equals("login")) {
                    System.out.println("login");
                    LoginHandler login = new LoginHandler(clientSocket, this, message, conDatabase);
                    clientsOnLogin.add(login);
                    new Thread(login).start();
                }
 
                if (message.type.equals("message")) {
                    ChatHandler chat = new ChatHandler(clientSocket ,message.chatID, Integer.parseInt(message.firstUserID), Integer.parseInt(message.secondUserID), message.userMessage, conDatabase);
                    new Thread(chat).start();
                }
                
                if(message.type.equals("getChat")){
                    System.out.println("getChat");
                    ChatHandler chat = new ChatHandler(clientSocket, Integer.parseInt(message.firstUserID), message.secondUserID,  conDatabase);
                    new Thread(chat).start();
                }
                
                if(message.type.equals("getMessages")){
                    System.out.println("getMessages");
                    MessageHandler mes = new MessageHandler(clientSocket, message.chatID, Integer.parseInt(message.firstUserID), 0, " ", conDatabase);
                    new Thread(mes).start();
                }
                if(message.type.equals("recivedMessage")){
                    System.out.println("recivedMessage");
                    MessageHandler mes = new MessageHandler(clientSocket, message.chatID, Integer.parseInt(message.firstUserID), Integer.parseInt(message.secondUserID), message.userMessage, conDatabase);
                    new Thread(mes).start();
                }
 
                
 
                
 
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                // закрываем подключение
                
                if(serverSocket != null){
                    System.out.println("Server has been stopped");
                    serverSocket.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
 
    
 
    
    // удаляем клиента из коллекции при выходе из чата
    
 
    public void removeClientOnLogin(LoginHandler login) {
        clientsOnLogin.remove(login);
    }
 
}