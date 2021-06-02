/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.servermaven;

import java.util.ArrayList;

/**
 *
 * @author lalal
 */
public class ChatHandler implements Runnable {
    private ArrayList<ClientHandler> clients;
    private int chatID;
    
    public ChatHandler(int chatID, ArrayList clients){
        this.clients = clients;
        this.chatID = chatID;
    }
    
    
    
    @Override
    public void run(){
        
    }
}
