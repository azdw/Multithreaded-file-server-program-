/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package multithreadedfileserverprogram;

import java.net.*;
import java.io.*;
import java.nio.file.*;

public class ServerForFileServer {

    static final int LISTENING_PORT = 7210;
    private String directoryName = "C:\\Users\\abd10\\Downloads\\Assignment 1 (1)\\files";
    private ServerSocket listener;
    private File directory;
    
    public ServerForFileServer(){
        try{
            listener = new ServerSocket(LISTENING_PORT);
            directory = new File(directoryName);
            System.out.println("Listening on port " + LISTENING_PORT);
            while (true) {
                Socket connection = listener.accept();
                System.out.println("New client connected from " + connection.getInetAddress().getHostName());
                Thread clientThread = new Thread(new ClientHandler(connection, directory));
                clientThread.start();
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        ServerForFileServer server = new ServerForFileServer();
    } 
}

class ClientHandler implements Runnable {
    private Socket connection;
    private File directory;

    public ClientHandler(Socket connection, File directory) {
        this.connection = connection;
        this.directory = directory;
    }

    public void run() {
        try (BufferedReader incoming = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             PrintWriter outgoing = new PrintWriter(connection.getOutputStream(), true)) {
            String command = incoming.readLine();
            if (command.equalsIgnoreCase("list")) {
                sendList(directory, outgoing);
            } else if (command.toLowerCase().startsWith("get")) {
                String fileName = command.substring(3).trim();
                sendFile(fileName, directory, outgoing, connection);
            } else {
                outgoing.println("ERROR unknown command");
                outgoing.flush();
            }
        } catch (IOException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    private static void sendList(File directory, PrintWriter outgoing) {
        try {
            File[] files = directory.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    outgoing.println(file.getName());
                }
            }
            outgoing.flush();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void sendFile(String fileName, File directory, PrintWriter outgoing, Socket connection) {
        try {
            File file = new File(directory, fileName);
            if (!file.exists()) {
                outgoing.println("ERROR file not found");
                outgoing.flush();
                return;
            }
            outgoing.println("OK");
            outgoing.flush();
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            OutputStream outgoingStream = new BufferedOutputStream(connection.getOutputStream());
            outgoingStream.write(fileBytes, 0, fileBytes.length);
            outgoingStream.flush();
            outgoingStream.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
