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
    private int firstUserID;
    private String userMessage;
    
    public ChatHandler(int chatID, ArrayList clients, int firstUserID, int secondUserID ,String userMessage){
        this.clients = clients;
        this.chatID = chatID;
        this.firstUserID = firstUserID;
    }
    
  
   @Override
   public void run(){
       try{
           while(true){
               boolean chatFounded = false;
               //Ищем чат по chatID в бд и создаем новое сообщение с данным id пользователя и текстом
               
               
               if(!chatFounded){
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
       }catch(Exception ex){
           ex.getStackTrace();
       }
   }
   
   private void sendResponse(){
       
   }
   
   private void createNew(){
       //Пишем запрос к бд , где создаем новый объект чата и добавляем туда два userID
       
   }
    
    
   
}
