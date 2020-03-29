package FileTransfer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {

        private static ServerSocket serverSocket;
        private static Socket clientSocket = null;

        public FileServer() {

            try {
                serverSocket = new ServerSocket(25444);
                System.out.println("Heyy!! Server started.");
            } catch (Exception e) {
                System.err.println("Port already in use.");
                System.exit(1);
            }


            while (true) {
                try {
                    clientSocket = serverSocket.accept();
                    System.out.println("Accepted connection : " + clientSocket);

                    Thread t = new Thread(new ServiceClient(clientSocket));

                    t.start();
                    //System.out.println("BLA BLA");

                } catch (Exception e) {
                    System.err.println("Error in connection attempt.");
                }
            }
        }


        public static void main(String[] args) throws IOException {
            new FileServer();
        }
    }

