package FileTransfer;

import java.io.*;
import java.net.Socket;

public class FileClient implements Runnable {

    private static Socket sock;
    private static String fileName;
    private static BufferedReader stdin;
    private static PrintStream os;
    private Socket socket;
    private Thread thread;
    private int PORT;

    public FileClient(){
        //this.sock = socket;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {

      while(true) {
           try {
               sock = new Socket("localhost", 5555);
               stdin = new BufferedReader(new InputStreamReader(System.in));
           } catch (Exception e) {
               System.err.println("Cannot connect to the server, try again later.");
               System.exit(1);
           }

           try {
               os = new PrintStream(sock.getOutputStream());
           } catch (IOException e) {
               e.printStackTrace();
           }

           try {
               System.out.print("Enter file name: ");
               fileName = stdin.readLine();
                File file = new File(fileName);
                if(file.exists()) {
                    os.println("UPLOAD " + fileName);
                    sendFile();
                }

                else
                {
                    os.println("UPLOAD "+fileName+" false");
                    sendFile();
                }
               //sock.close();
               //continue;

           } catch (Exception e) {
               System.err.println("not valid input");
           }
      }

    }

    public static void main(String[] args) throws IOException {
        new FileClient();
    }


    public static void sendFile() throws IOException {
        try {

            File myFile = new File(fileName);
            byte[] mybytearray = new byte[(int) myFile.length()];
            if(!myFile.exists()) {
                System.out.println("File does not exist..");
                return;
            }


            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            //bis.read(mybytearray, 0, mybytearray.length);

            DataInputStream dis = new DataInputStream(bis);
            //dis.readFully(mybytearray, 0, mybytearray.length);

            OutputStream os = sock.getOutputStream();

            //Sending file name and file size to the server
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(myFile.getName());
            dos.writeLong(mybytearray.length);

            int count=0;
            byte[] buffer = new byte[8421];

            while ((count=dis.read(buffer))>0){
                System.out.println(count);
                dos.write(buffer,0,count);
            }

           // dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();
            System.out.println("File "+fileName+" sent to Server.");

        } catch (Exception e) {
            System.err.println("Exception: "+e);
        }
    }

    public static void receiveFile(String fileName) {
        try {
            int bytesRead;
            InputStream in = sock.getInputStream();

            DataInputStream clientData = new DataInputStream(in);

            fileName = clientData.readUTF();
            OutputStream output = new FileOutputStream(fileName);
            long size = clientData.readLong();
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }

            output.close();
            in.close();

            System.out.println("File "+fileName+" received from Server.");
        } catch (IOException ex) {
            System.out.println("Exception: "+ex);
        }

    }


}
