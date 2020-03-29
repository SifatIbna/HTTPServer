package HTTPServer;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FileWalker {

    private String path;
    private ArrayList<String> dir;
    private ArrayList<String> file;



    public FileWalker(String path){
        this.path = path;
        this.dir = new ArrayList<>();
        this.file = new ArrayList<>();
    }

    public void Walker() {

        //System.out.println(path);
        path = path.replace("%20"," ");
        File root = new File(path);
        File[] list = root.listFiles();
//        System.out.println(list.length  );
        if (list == null) return;

        for ( File f : list ) {
            //System.out.println(f);
          //  System.out.println(f.isDirectory());
            if ( f.isDirectory() ) {
               // walk( f.getAbsolutePath() );
                this.dir.add(f.getAbsolutePath());
               // System.out.println( "Dir:" + f.getAbsoluteFile() );
            }
            else {

                //System.out.println(finalname);
                this.file.add(f.getAbsolutePath());
             //  System.out.println( "File:" + f.getAbsoluteFile() );
            }
        }


    }

    public ArrayList<String> getDir(){
        return dir;
        //return null;
    }

    public ArrayList<String> getFile(){
        return file;

    }

    public static void main(String[] args) {
        new FileWalker("/home/sifat/Documents/Workspace/Netwoking/Sessional/Materials on Offline 1/Offline 1/root").Walker();
    }
}
