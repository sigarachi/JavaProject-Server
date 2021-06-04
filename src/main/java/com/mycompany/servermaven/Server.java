/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.servermaven;

/**
 *
 * @author1 lalal
 * @author2 Toropchinov
 *
 */


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
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

    static final int PORT = 5905;
    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
    private ArrayList<LoginHandler> clientsOnLogin = new ArrayList<LoginHandler>();
    private Scanner clientInputStream ;

    public Server() {

        Socket clientSocket = null;

        ServerSocket serverSocket = null;

        String dbUser = "postgres";
        String dbPassword = "root";
        String dbUrl = "jdbc:postgresql://localhost:5432/iatemessenger";
        String drvName = "org.postgresql.Driver";
        Connection conDatabase = null;

        try {
            Class.forName(drvName);
            conDatabase = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port = " + PORT);

            while (true) {
                clientSocket = serverSocket.accept();
                clientInputStream = new Scanner(clientSocket.getInputStream());
                
                System.out.println("ClientInputStream 1" + clientInputStream.toString());
                
                if(clientInputStream.hasNext()){
                    
                    System.out.println("ClientInputStream 2" + clientInputStream.toString());
                    
                    ObjectMapper mapper = new ObjectMapper();
                    JInputMessage message = mapper.readValue(clientSocket.toString(), JInputMessage.class);
                    //System.out.println("message" + message.toString());
                    
                    if (message.type.equals("login")) {
                        //System.out.println("login");
                        LoginHandler login = new LoginHandler(clientSocket, this, message, conDatabase);
                        clientsOnLogin.add(login);
                        new Thread(login).start();
                    }

                    if (message.type.equals("message")) {
                        ChatHandler chat = new ChatHandler(message.chatID, clients, Integer.parseInt(message.firstUserID), Integer.parseInt(message.secondUserID), message.userMessage, conDatabase);

                        new Thread(chat).start();

                    }

                    if (message.type.equals("clientOnline")) {
                        ClientHandler client = new ClientHandler(clientSocket, this, message.firstUserID);
                        clients.add(client);

                        new Thread(client).start();
                    }
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
                if(clientSocket != null){
                    clientSocket.close();
                   
                }
                if(serverSocket != null){
                    System.out.println("Server has been stopped");
                    serverSocket.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void sendMessageToAllClients(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }

    protected void setDualFields() {

    }

    // удаляем клиента из коллекции при выходе из чата
    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public void removeClientOnLogin(LoginHandler login) {
        clientsOnLogin.remove(login);
    }

}
