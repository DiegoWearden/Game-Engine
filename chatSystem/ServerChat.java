package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerChat {

    private static Socket socket;
    private static String username;
    private static ChatWindow chatWindow;
    private static boolean colored;
    public static void main(String[] args) {
        chatWindow = new ChatWindow();
        chatWindow.buildFrame();
        try{
            socket = new Socket("162.204.2.105", 57923);
        }catch (IOException e){
            chatWindow.printData("Could not connect to server", false);
        }
        chatWindow.printData("Connected to server: " + socket.getInetAddress().getHostAddress(), false);
        Thread receive = new Thread(() -> {
            while (true){
                String receivedData = receiveData();
                chatWindow.printData(receivedData, colored);
            }
        });
        receive.start();
    }

    private static String receiveData() {
        StringBuilder data = new StringBuilder();
        DataInputStream in;
        try {
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Error in creating input stream");
        }
        char inData = 0;
        while(inData != '\n'){
            try {
                inData = (char) in.read();
            } catch (IOException e) {
                throw new RuntimeException("Error in receiving data");
            }
            if(inData != '\n'){
                data.append(inData);
            }
        }
        try {
            colored = in.readBoolean();
            int r = in.readInt();
            int g = in.readInt();
            int b = in.readInt();
            chatWindow.setUsernameColor(r, g, b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data.toString();
    }

    public static void sendData(String text) {
        DataOutputStream out;
        try {
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        text = "<" + username + "> " + text + "\n";
        for (int i = 0; i < text.length(); i++) {
            try {
                out.write(text.charAt(i));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void setUsername(String username) {
        ServerChat.username = username;
        chatWindow.setUsernamePrompt(username);
    }
}
