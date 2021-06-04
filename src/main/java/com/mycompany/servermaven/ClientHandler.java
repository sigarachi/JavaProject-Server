/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.servermaven;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author lalal
 */
public class ClientHandler implements Runnable {

    private Server server;

    private PrintWriter messageOut;
    private Scanner messageIn;

    private User user;

    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    private Socket clientSocket = null;

    private static int clients_count = 0;
    
    private PrintWriter messageToClient;

    public ClientHandler(Socket socket, Server server, String userID) {
        try {
            clients_count++;
            Object obj = userID;

            this.server = server;
            this.clientSocket = socket;
            this.messageOut = new PrintWriter(socket.getOutputStream());
            this.messageIn = new Scanner(socket.getInputStream());
            this.user = new User(Integer.parseInt(userID));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (messageIn.hasNext()) {
                    String clientMessage = messageIn.nextLine();
                    if (clientMessage.equalsIgnoreCase("##session##end##")) {
                        break;
                    }
                    System.out.println(clientMessage);

                    server.sendMessageToAllClients(clientMessage);

                }
                Thread.sleep(100);
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            this.close();
        }

    }

    public void sendMsg(String msg) {
        try {

            messageOut.println(msg);
            messageOut.flush();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public User getUser() {
        return this.user;
    }

    public void close() {
        server.removeClient(this);
        clients_count--;
        server.sendMessageToAllClients("Clients = " + clients_count);

    }
}
