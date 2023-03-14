/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package multithreadedfileserverprogram;

/**
 *
 * @author abd10
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientForFileServer {
    
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", ServerForFileServer.LISTENING_PORT);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            // Send "LIST" command to server and print the list of files
            output.println("LIST");
            String response = input.readLine();
            System.out.println(response);

            // Send "GET <filename>" command to server and save the file
            output.println("GET example 2.txt");
            response = input.readLine();
            if (response.startsWith("OK")) {
                // Read the contents of the file from the input stream
                StringBuilder fileContents = new StringBuilder();
                String line;
                while ((line = input.readLine()) != null) {
                    fileContents.append(line);
                    fileContents.append("\n");
                }
                // Save the file to disk
                // ...
            } else {
                System.out.println(response);
            }

            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
}

