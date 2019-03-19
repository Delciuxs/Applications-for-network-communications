import java.io.Serializable;
import java.util.*;

public class Topic implements Serializable{
    String date, nameTopic, nameFile;
    byte []fileTopic;
    long sizeTopic;

    public Topic(String date, String nameTopic, String nameFile, byte []fileTopic, long sizeTopic){
        this.date = date;
        this.nameTopic = nameTopic;
        this.nameFile = nameFile;
        this.fileTopic = Arrays.copyOf(fileTopic, fileTopic.length);
        this.sizeTopic = sizeTopic;
    }
    public Topic(String date, String nameTopic){
        this.date = date;
        this.nameTopic = nameTopic;
    }

    public String getDateTopic(){
        return this.date;
    }
    public String getNameTopic(){
        return this.nameTopic;
    }
    public String getNameFile(){
        return this.nameFile;
    }
    public byte[] getfileTopic(){
        return this.fileTopic;
    }
    public long getSizeTopic(){
        return this.sizeTopic;
    }

    
}