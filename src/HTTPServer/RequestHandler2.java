package HTTPServer;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class RequestHandler2 implements Runnable {


    private Socket socket;
    private File file;
    private String content;
    //private ServletResponse response;

    public RequestHandler2(Socket socket,File file,String content){
        this.socket = socket;
        this.file = file;
        this.content = content;
    }

    @Override
    public void run() {

        BufferedReader in = null;
        PrintWriter pr = null;
        String input = null;

        OutputStream outputStream;
        InputStream inputStream;

        BufferedInputStream bufferedInputStream;
        BufferedOutputStream bufferedOutputStream = null;

        try {

            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            bufferedInputStream = new BufferedInputStream(inputStream);
            bufferedOutputStream = new BufferedOutputStream(outputStream);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pr = new PrintWriter(socket.getOutputStream());

            //input = in.readLine();

            byte[] by = null;
            while (bufferedInputStream.available() <= 0) ;

            if (bufferedInputStream.available() > 0) {
                by = new byte[bufferedInputStream.available()];
                bufferedInputStream.read(by);
            }

            input = new String(by);

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
                //System.out.println(root);

                if (!root.exists()) {

                    try {
                        content = new HTMLManipulation(file, str[1], false).setHTMLfile();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        bufferedOutputStream.write("HTTP/1.1 404 error\r\n".getBytes());
                        bufferedOutputStream.write("Server: Java HTTP Server: 1.0\r\n".getBytes());
                        bufferedOutputStream.write(("Data: " + new Date() + "\r\n").getBytes());
                        bufferedOutputStream.write("Content-Type: text/html\r\n".getBytes());
                        bufferedOutputStream.write(("Content-Length: " + content.length() + "\r\n").getBytes());
                        bufferedOutputStream.write("\r\n".getBytes());
                        bufferedOutputStream.write(content.getBytes());
                        bufferedOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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


                    try {

                        bufferedOutputStream.write("HTTP/1.1 200 OK\r\n".getBytes());
                        bufferedOutputStream.write("Server: Java HTTP Server: 1.0\r\n".getBytes());
                        bufferedOutputStream.write(("Date: " + new Date() + "\r\n").getBytes());
                        bufferedOutputStream.write("Content-Type: text/html\r\n".getBytes());
                        bufferedOutputStream.write(("Content-Length: " + content.length() + "\r\n").getBytes());
                        bufferedOutputStream.write("\r\n".getBytes());
                        bufferedOutputStream.write(content.getBytes());
                        bufferedOutputStream.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        download(root, bufferedOutputStream);
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
//
//                }


                }
            }

            else if (input.startsWith("UPLOAD")) {
                System.out.println("File Receiving...");
                receiveFile();
            }
        }

    }
    public void download(File root,BufferedOutputStream bufferedOutputStream) throws IOException {

        //output stream
        //getB
        PrintWriter pr = new PrintWriter(socket.getOutputStream());
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
            //  pr.write(content.getBytes());
            pr.flush();

            int count=0;
            // byte[] buffer = new byte[8192];
//            while ((count = inputStream.read(mybytearray)) > 0)
//            {
//                System.out.println(count);
//                dos.write(mybytearray, 0, count);
//            }

            //dos.write(mybytearray, 0, (int) f.length());
            //inputStream.close();
            //dos.flush();
            BufferedOutputStream dos = new BufferedOutputStream(socket.getOutputStream());
            inputStream = new FileInputStream(f);
            //inputStream.read(mybytearray);

            //int count=0;

            while ((count = inputStream.read(mybytearray)) >= 0)
            {
                System.out.println(count);
                dos.write(mybytearray, 0, count);
            }

            inputStream.close();
            dos.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedInputStream bin = new BufferedInputStream(inputStream);
        DataInputStream din = new DataInputStream(bin);

        OutputStream os = null;
        try {
            os =  socket.getOutputStream();
            //System.out.println(os);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Sending file name and file size to the server

        // int count = 0;
        // byte[] buffer = new byte[81920000];

        System.out.println("here1");

        System.out.println("here2");


//                        while(true){
//                            try {
//                                if (!(din.available() > 0)) break;
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            out.print(din.readLine());
//                            out.print("\n");
//                        }

        // response.setContentType("application/force-download");
        // response.setContentLength((int)f.length());
        // response.setHeader("Content-Transfer-Encoding", "binary");
        // response.setHeader("Content-Disposition","attachment; filename=\"" + "xxx\"");//fileName);

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
