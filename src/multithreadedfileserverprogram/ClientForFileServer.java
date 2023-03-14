package multithreadedfileserverprogram;

import java.net.*;
import java.io.*;

public class ClientForFileServer {
    private String ipAddress;
    private int port;
    private BufferedReader input = null; // reading data from keyboard

    public ClientForFileServer(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
        input = new BufferedReader(new InputStreamReader(System.in));
    }

    public void run() {
        try {
            String line = "";
            System.out.println("Connected");
    
            while (!line.equals("quit")) {
                Socket socket = new Socket(ipAddress, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    
                System.out.println("Enter command (list/get <file name>/quit)");
                line = input.readLine();
                out.println(line);
                out.flush();
    
                if (line.startsWith("list")) {
                    String response = "";
                    while (!response.equals("__LIST_COMPLETE__")) {
                        response = in.readLine();
                        if (response == null) {
                            break;
                        } else {
                            System.out.println(response);
                        }
                    }
                } else if (line.toLowerCase().startsWith("get")) {
                    String response = in.readLine();
                    if (response == null) {
                        break;
                    } else if (response.startsWith("OK")) {
                        byte[] buffer = new byte[4096];
                        InputStream fileIn = socket.getInputStream();
                        FileOutputStream fileOut = new FileOutputStream(new File(line.substring(4).trim()));
    
                        int bytesRead;
                        while ((bytesRead = fileIn.read(buffer)) != -1) {
                            fileOut.write(buffer, 0, bytesRead);
                        }
    
                        fileOut.close();
                        fileIn.close();
                        System.out.println("File downloaded successfully.");
                    } else {
                        System.out.println(response);
                    }
                } else {
                    System.out.println(in.readLine());
                }
    
                out.close();
                in.close();
                socket.close();
            }
    
            // close the input
            input.close();
        } catch (IOException i) {
            System.out.println(i);
        }
    }
    

    public static void main(String args[]) {
        ClientForFileServer ClientForFileServer = new ClientForFileServer("localhost", 7210);
        ClientForFileServer.run();
    }
}
