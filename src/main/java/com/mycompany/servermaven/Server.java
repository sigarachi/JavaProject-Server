/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.servermaven;

/**
 *
 * @author lalal
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import jdk.nashorn.internal.runtime.ScriptObject;



/**
 *
 * @author lalal
 */
public class Server extends ScriptObject {
    static final int PORT = 5000;
    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
    
    
    public Server(){
        Socket clientSocket = null;
        
        ServerSocket serverSocket = null;
        
        
        
        
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port = " + PORT);
            
            while(true){
                clientSocket = serverSocket.accept();
                final boolean dualFields;
               
                
                ObjectMapper mapper = new ObjectMapper();
                JInputMessage message = mapper.readValue(clientSocket.toString(), JInputMessage.class);
                
                if(message.type == "login"){
                    LoginHandler login = new LoginHandler(clientSocket, this, message);
                    new Thread(login).start();
                }
                
                
                ClientHandler client = new ClientHandler(clientSocket, this);
                clients.add(client);
                
                new Thread(client).start();
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            try {
                // закрываем подключение
                clientSocket.close();
                System.out.println("Server has been stopped");
                serverSocket.close();
            }
            catch (IOException ex) {
              ex.printStackTrace();
            }
        }
    }
    
    public void sendMessageToAllClients(String msg) {
        for (ClientHandler o : clients) {
          o.sendMsg(msg);
        }
 
    }
    
    protected void setDualFields(){
        
    }
 
  // удаляем клиента из коллекции при выходе из чата
    public void removeClient(ClientHandler client) {
      clients.remove(client);
    }
    
    
}
