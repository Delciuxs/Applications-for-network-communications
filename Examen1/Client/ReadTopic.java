import java.io.*;
import java.util.*;
import javafx.util.*;

public class ReadTopic{
    
    String nameTopic;
    String pathToTopics;

    public ReadTopic(String nameTopic){
        this.nameTopic = nameTopic;
        this.pathToTopics = "./QueryTopic/";
    }

    public String getContent(){
        String contentLine = "";
        try {
            FileReader fr = new FileReader(this.pathToTopics + nameTopic + "/content.txt");
            BufferedReader br = new BufferedReader(fr);
            contentLine = br.readLine();
            br.close();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentLine;
    }

    public String getImage(){
        return (this.pathToTopics + nameTopic + "/img.png");
    }

    public String getUser(){
        String user = "";
        try {
            FileReader fr = new FileReader(this.pathToTopics + nameTopic + "/user.txt");
            BufferedReader br = new BufferedReader(fr);
            user = br.readLine();
            br.close();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public  ArrayList<Pair<String, String>> getComents(){
        File comentsFolder = new File(this.pathToTopics + nameTopic + "/Coments/");
        File []comentsFiles = comentsFolder.listFiles();
        Arrays.sort(comentsFiles, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.compare(f1.lastModified(), f2.lastModified());
            }
        });

        ArrayList<Pair<String, String>> coments = new ArrayList<Pair<String, String>>();

        for(File f : comentsFiles){
            String userComent = f.getName();
            String user = "";
            for(int i = 0; i < userComent.length(); i++){
                if(userComent.charAt(i) == '.') break;
                user += userComent.charAt(i);
            }
            String comentLine = "";
            try {
                FileReader fr = new FileReader(f.getAbsolutePath());
                BufferedReader br = new BufferedReader(fr);
                comentLine = br.readLine();
                br.close();
                fr.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            coments.add(new Pair<>(user, comentLine));
        }
        return coments;
    }

    public void addComent(String user, String coment){
        try {
            FileWriter fw = new FileWriter(this.pathToTopics + nameTopic + "/Coments/" + user + ".txt");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(coment);
            bw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}