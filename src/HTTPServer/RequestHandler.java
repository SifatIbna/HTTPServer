package HTTPServer;

import com.sun.security.jgss.GSSUtil;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class RequestHandler implements Runnable {

    private Socket socket;
    private File file;
    private String content;
    //private ServletResponse response;
    private BufferedReader in = null;
    private PrintWriter pr = null;
    private String input = null;

    public RequestHandler(Socket socket,File file,String content){
        this.socket = socket;
        this.file = file;
        this.content = content;
    }

    @Override
    public void run() {



        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pr = new PrintWriter(socket.getOutputStream());
            input = in.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (input == null) {

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }
        if (input.length() > 0) {

            if (input.startsWith("GET")) {

                String[] str = input.split(" ");
                str[1] = str[1].replace("%20", " ");
                File root = new File(str[1]);

                if (!root.exists()) {

                    try {
                        content = new HTMLManipulation(file, str[1], false).setHTMLfile();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    pr.write("HTTP/1.1 404 error\r\n");
                    pr.write("Server: Java HTTP Server: 1.0\r\n");
                    pr.write("Data: " + new Date() + "\r\n");
                    pr.write("Content-Type: text/html\r\n");
                    pr.write("Content-Length: " + content.length() + "\r\n");
                    pr.write("\r\n");
                    pr.write(content);
                    pr.flush();

                    System.out.println("Directory not found");
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //String type = input+"GET";
                else if (root.isDirectory()) {
                    try {
                        content = new HTMLManipulation(file, str[1], true).setHTMLfile();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                    pr.write("HTTP/1.1 200 OK\r\n");
                    pr.write("Server: Java HTTP Server: 1.0\r\n");
                    pr.write("Date: " + new Date() + "\r\n");
                    pr.write("Content-Type: text/html\r\n");
                    pr.write("Content-Length: " + content.length() + "\r\n");
                    pr.write("\r\n");
                    pr.write(content);
                    pr.flush();

                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                else {
                    download(root,pr);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            } else if (input.startsWith("UPLOAD")) {
                String [] check = input.split(" ");
                String finalCheck = check[check.length-1];

                if (finalCheck.equals("false")){
                    System.out.println("file doesn't exist");
                    return;
                }
                System.out.println("File Receiving...");
                receiveFile();
            }
        }
    }


    public void download(File root,PrintWriter pr){


        System.out.println("File found");
        String[] fileName = root.getAbsolutePath().split("/");
        String finalname = fileName[fileName.length - 1];

        pr.write("HTTP/1.1 200 OK\r\n");
        pr.write("Server: Java HTTP Server: 1.0\r\n");
        pr.write("Content-Type: text/plain\r\n");
        pr.write("Content-Transfer-Encoding: byte\r\n");
        pr.write(("Content-Disposition: attachment; filename='" + finalname + "'"));

        File f = new File(String.valueOf(root));
        byte[] mybytearray = new byte[8162];
        FileInputStream inputStream = null;
        try {
            pr.write(("Content-Length: " + root.length() + "\r\n"));
            pr.write("\r\n");
            pr.flush();

            int count=0;

            BufferedOutputStream dos = new BufferedOutputStream(socket.getOutputStream());
            inputStream = new FileInputStream(f);


            while ((count = inputStream.read(mybytearray)) >= 0)
            {
                System.out.println(count);
                dos.write(mybytearray, 0, count);
            }

            inputStream.close();
            dos.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        }


    public void receiveFile() {
        try {

            int bytesRead;

            DataInputStream clientData = new DataInputStream(socket.getInputStream());

            String fileName = clientData.readUTF();
            OutputStream output = new FileOutputStream(fileName);
            long size = clientData.readLong();
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }

            System.out.println("File "+fileName+" received from client.");
            //clientData.close();
        } catch (IOException ex) {
            System.err.println("Client error. Connection closed.");
        }
    }

}
