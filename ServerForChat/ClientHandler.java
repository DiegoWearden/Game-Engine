package org.example;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Random;

import static org.example.Server.clients;

class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final BufferedReader in;
    private final DataOutputStream out;
    private final Color color;
    private final int r;
    private final int g;
    private final int b;

    public ClientHandler(Socket clientSocket) throws IOException {
        Random rand = new Random();
        this.clientSocket = clientSocket;
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.out = new DataOutputStream(clientSocket.getOutputStream());
        r = rand.nextInt(256);
        g = rand.nextInt(256);
        b = rand.nextInt(256);
        color = new Color(r, g, b);
    }

    @Override
    public void run() {
        String data = null;
        try {
            while (true) {
                data = readLine();
                Server.broadcast(data, this);
            }
        } catch (SocketException e) {
            System.out.println("Client disconnected - IP Address: " + this.clientSocket.getInetAddress().getHostAddress());
        }catch (IOException e) {
            System.out.println("Error handling client.");
        } finally {
            try {
                if(data != null){
                    String username = data.split("> ", 2)[0];
                    Server.broadcastLeave(username.substring(1) + " has left the chat", this);
                }
                closeConnection();
                clients.remove(this);
            } catch (IOException e) {
                System.out.println("Error closing connection.");
            }
        }
    }

    private String readLine() throws IOException {
        StringBuilder data = new StringBuilder();
        char inData = 0;
        while(inData != '\n'){
                inData = (char) in.read();
            if(inData != '\n'){
                data.append(inData);
            }
        }
        return data.toString();
    }

    public void sendData(String data, ClientHandler client) throws IOException {
        for (int i = 0; i < data.length(); i++) {
            try {
                out.write(data.charAt(i));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Color color1 = client.getColor();
        out.writeBoolean(true);
        out.writeInt(color1.getRed());
        out.writeInt(color1.getGreen());
        out.writeInt(color1.getBlue());
    }
    public void sendData(String data) throws IOException {
        for (int i = 0; i < data.length(); i++) {
            try {
                out.write(data.charAt(i));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        out.writeBoolean(false);
        out.writeInt(0);
        out.writeInt(0);
        out.writeInt(0);
    }
    public Color getColor(){
        return color;
    }

    public void closeConnection() throws IOException {
        if (clientSocket != null) {
            clientSocket.close();
        }
    }
}