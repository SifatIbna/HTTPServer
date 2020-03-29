package HTTPServer;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class filedown {

    public static void download(String address, String localFileName) {
        OutputStream out = null;
        URLConnection conn = null;
        InputStream in = null;

        try {
            URL url = new URL(address);
            out = new BufferedOutputStream(new FileOutputStream(localFileName));
            conn = url.openConnection();
            in = conn.getInputStream();
            byte[] buffer = new byte[1024];

            int numRead;
            long numWritten = 0;

            while ((numRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, numRead);
                numWritten += numRead;
            }

            System.out.println(localFileName + "\t" + numWritten);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }
            catch (IOException ioe) {
            }
        }
    }

    public static void download(String address) throws MalformedURLException {
        int lastSlashIndex = address.lastIndexOf('/');
        if (lastSlashIndex >= 0 &&
                lastSlashIndex < address.length() - 1) {
            download(address, (new URL(address)).getFile());
        }
        else {
            System.err.println("Could not figure out local file name for "+address);
        }
    }

    public static void main(String[] args) throws MalformedURLException {
        String s = "/home/sifat/Documents/Workspace/Netwoking/Sessional/Assignment_HTTPFileServer.pdf";
        download(s);
    }
}
