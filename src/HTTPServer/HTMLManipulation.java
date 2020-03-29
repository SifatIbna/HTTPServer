package HTTPServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class HTMLManipulation {

    private File file;
    private PrintWriter printWriter;
    private String path;
    private String finalString;

    private String start = "<html>\n<head><link rel='icon' href='data:,'></head>\n<body>";
    private String end = "\n</body>" + "</html>";

    private FileWalker filewalker;

    private ArrayList<String> directory;
    private ArrayList<String> files;

    private boolean exist;

    public HTMLManipulation(File file, String path, boolean exist) throws FileNotFoundException {
        this.file = file;
        // System.out.println(path);
        filewalker = new FileWalker(path);
        this.exist = exist;
        printWriter = new PrintWriter(this.file);
        this.directory = new ArrayList<>();
        this.files = new ArrayList<>();
    }

    public String setHTMLfile() throws FileNotFoundException {
        if (exist) {
            filewalker.Walker();
            this.directory = filewalker.getDir();
            this.files = filewalker.getFile();

            String fileString1 = "<ul>" + "Files :";
            String fileString2 = "\n</ul>";
            String middlePortion = "";

            // System.out.println(files.size());

            if (files.size() > 0) {
                for (String str : this.files) {

                    //System.out.println(str);
                    String[] strings = str.split("/");
                    String finalString = strings[strings.length-1];
                    middlePortion += "\n<li><a href='" + str + "' download='"+"'>" + str + "</a></li>";
                    //System.out.println(middlePortion);
                }

                // System.out.println(middlePortion);


                // printWriter.write(start + fileString1 + middlePortion + fileString2 + end);
            }
            // System.out.println(directory.size());
            String folders = "FOLDERS : ";
            String middleFolders = "";

            if (directory.size() > 0) {
                for (String s : this.directory) {
                    String s1 = s.replace("%20", " ");
                    middleFolders += "\n<li><a href='" + s1 + "'>" + s1 + "</a></li>";
                }
            }

            printWriter.write(start + fileString1 + middlePortion + folders + middleFolders + fileString2 + end);

            return start + fileString1 + middlePortion + folders + middleFolders + fileString2 + end;
        }

        return "File not Found";
    }
}

