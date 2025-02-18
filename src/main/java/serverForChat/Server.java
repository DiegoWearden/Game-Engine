package serverForChat;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {

    public static final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private static volatile boolean exit = false;
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            File dir = new File("server info");
            if (!dir.exists()){
                dir.mkdir();
                PrintStream output = new PrintStream("server info/server_info.txt");
                output.println("port = 57923");
                output.close();
            }
            Scanner serverScanner = new Scanner(new File("server info/server_info.txt"));
            int port = getPort(serverScanner);
            ServerSocket server = new ServerSocket(port);
            System.out.println("Opened connection on port " + port);
            System.out.println("Server started");

            Thread shutdown = new Thread(() -> {
                System.out.println("Enter \"stop\" to stop the server");
                if(scanner.nextLine().equalsIgnoreCase("stop")){
                    exit = true;
                    try {
                        server.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            });
            shutdown.start();

            while (!exit) {
                try {
                    Socket clientSocket = server.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clients.add(clientHandler);
                    new Thread(clientHandler).start();
                    System.out.println("Client connected - IP Address: " + clientSocket.getInetAddress().getHostAddress());
                } catch (Exception e) {
                    if(!exit){
                        System.out.println("Error accepting client.");
                    }
                }
            }

        }catch (SocketException e){
            System.out.println("Could not connect to port");
        }catch (IOException e){
            System.out.println("Error in handling client");
        }

    }

    private static int getPort(Scanner serverScanner) {
        return Integer.parseInt(getLastWordInLine(serverScanner.nextLine()));
    }

    public static String getLastWordInLine(String line) {
        Scanner scanner = new Scanner(line);
        scanner.useDelimiter("= ");
        String result = null;

        while (scanner.hasNext()) {
            result = scanner.next();
        }

        scanner.close();
        return result;
    }

    public static void broadcast(String data, ClientHandler clientHandler){
        System.out.println(data);
        for (ClientHandler client : clients) {
                try {
                    client.sendData(data + "\n", clientHandler);
                } catch (IOException e) {
                    throw new RuntimeException("Error in sending data");
                }
        }
    }

    public static void broadcastLeave(String data, ClientHandler leavingClient){
        System.out.println(data);
        for (ClientHandler client : clients) {
            if(!client.equals(leavingClient)){
                try {
                    client.sendData(data + "\n");
                } catch (IOException e) {
                    throw new RuntimeException("Error in sending data");
                }
            }
        }
    }
}
