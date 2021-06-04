/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 
package com.mycompany.servermaven;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
 
/**
 *
 * @author1 lalal
 * @author2 Toropchinov
 *
 */
 
public class ChatHandler implements Runnable {
 
    private ArrayList<ClientHandler> clients;
    private int chatID;
    private int firstUserID;
    private String userMessage;
    private int secondUserID;
    private Socket client;
    private Connection conDatabase;
    
    private BufferedReader clientInputStream ;
    private PrintWriter clientOutputStream;
    
    private PrintWriter messageToClient;
 
    public ChatHandler(Socket clientSocket ,int chatID, ArrayList clients, int firstUserID, int secondUserID, String userMessage, Connection conDb) throws IOException {
        this.clients = clients;
        this.chatID = chatID;
        this.firstUserID = firstUserID;
        this.secondUserID = secondUserID;
        this.conDatabase = conDb;
        this.client = clientSocket;
        this.messageToClient = new PrintWriter(clientSocket.getOutputStream());
    }
 
    @Override
    public void run() {
        try {
            while (true) {
                boolean chatFounded = false;
                this.clientInputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
                this.clientOutputStream = new PrintWriter(client.getOutputStream());
                //Ищем чат по userid1 и userid2 в бд и возвращаем chatID
 
                //запрос в БД
                String csql = "SELECT chatID FROM chats WHERE firstUserID= '" + firstUserID + "' "
                        + "AND secondUserID = '" + secondUserID + "'";
 
                Statement st = conDatabase.createStatement();
                st.executeQuery(csql);
                ResultSet rs = st.executeQuery(csql);
 
                //вернули chatID
                while (rs.next()) {
                    int chatID = rs.getInt("chatID");
                    
                    JSONObject res = new JSONObject();
                    res.put("type", "recivedChat");
                    res.put("chatID", chatID);
                    res.put("chatName", secondUserID);
                    messageToClient.println(res);
                    messageToClient.flush();
                    break;
                }
                if (!chatFounded) {
                    this.createNew();
                }
 
                /* Это код ответа, он будет дописан после бд
              for(int i = 0; i < clients.size(); i++){
                  ClientHandler client = clients.get(i);
                  if(client.getUser().getUserID() == userID && client.getUser().isOnline()){
                      
                  }
              }
                 */
            }
        } catch (SQLException ex) {
            ex.getStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(ChatHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
    private void sendResponse() {
           this.clientOutputStream.println("sss");
           this.clientOutputStream.flush();
    }
 
    private void createNew() throws SQLException {
        //Пишем запрос к бд , где создаем новый объект чата и добавляем туда два userID
        //возвращаем chatID нашего нового чата
 
        //создаем новый чат (добавляем 2 пользователей в таблицу)
        String csql1 = "INSERT INTO chats (firstUserID, secondUserID) VALUES"
                + "('" + firstUserID + "','" + secondUserID + "')";
 
        Statement st = conDatabase.createStatement();
        st.executeUpdate(csql1);
 
        //возвращаем наш chatID
        String csql2 = "SELECT chatID FROM chats WHERE firstUserID= '" + firstUserID + "' "
                + "AND secondUserID = '" + secondUserID + "'";
 
        st.executeQuery(csql2);
        ResultSet rs = st.executeQuery(csql2);
 
        while (rs.next()) {
            int chatID = rs.getInt("chatID");
            JSONObject res = new JSONObject();
            res.put("type", "recivedChat");
            res.put("chatID", chatID);
            res.put("chatName", secondUserID);
            messageToClient.println(res);
            messageToClient.flush();
            break;
        }
    }
}