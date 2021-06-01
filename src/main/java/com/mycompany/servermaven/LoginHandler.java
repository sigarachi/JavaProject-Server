/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.servermaven;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lalal
 */
public class LoginHandler implements Runnable {
    private Server server;
    
    private Socket clientSocket = null;
    private String clientLogin;
    private String clientPassword;
    
    private PrintWriter messageToClient;
    
    
    
    public LoginHandler(Socket clientSocket, Server server, JInputMessage auth){
        
        try {
            this.server = server;
            this.clientSocket = clientSocket;
            this.clientLogin = auth.login;
            this.clientPassword = auth.password;
            this.messageToClient = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(LoginHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run(){
        try{
            //Добавить код для бд 
            while(true){
                //Ищем запись в бд 
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
        server.removeClient(this);
    }
}
