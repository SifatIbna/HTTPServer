import FileTransfer.FileServer;
import HTTPServer.HTTPServer;

import java.io.IOException;

public class Server {
    public static void main(String[] args) throws IOException {
        //new FileHandler();
        new HTTPServer();
        new FileServer();
    }
}
