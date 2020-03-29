package HTTPServer;


import FileTransfer.ServiceClient;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class HTTPServer {

    static final int PORT1 = 5555;
    static final int PORT2 = 25444;


    public static String readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } finally {
            if (fileIn != null)
                fileIn.close();
        }

        return String.valueOf(fileData);
    }

    public static void main(String[] args) throws IOException {

        ServerSocket serverConnect1 = new ServerSocket(5555);
        ServerSocket serverSocket2 = new ServerSocket(25444);

        System.out.println("Server started.\nListening for connections on port : " + PORT1 +" ...\n");

        File file = new File("index.html");


        FileInputStream fis = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append('\n');
        }

        String content = sb.toString();
        // System.out.println(content);

        while (true) {

            try {
                Socket s1 = serverConnect1.accept();
                System.out.println("Accepted connection : " + s1);

                Thread t = new Thread(new RequestHandler(s1, file, content));
                t.start();

            } catch (Exception e) {
                System.err.println("Error in connection attempt.");
            }
        }
    }
}
