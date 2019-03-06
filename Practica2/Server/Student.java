import java.util.*;
import java.io.Serializable;

public class Student implements Serializable{
    String statusLogin ,fullName;
    byte []photo;
    ArrayList<ArrayList<ArrayList<String>>> allSchedules;
    ArrayList<ArrayList<String>> schedule;
    ArrayList<ArrayList<String>> rankings;

    public Student(String statusLogin, String fullName, byte []photo, ArrayList<ArrayList<ArrayList<String>>> allSchedules, ArrayList<ArrayList<String>> schedule, ArrayList<ArrayList<String>> rankings){
        this.statusLogin = statusLogin;
        this.fullName = fullName;
        this.photo = Arrays.copyOf(photo, photo.length);
        this.allSchedules = new ArrayList<ArrayList<ArrayList<String>>>(allSchedules);
        this.schedule = new ArrayList<ArrayList<String>>(schedule);
        this.rankings = new ArrayList<ArrayList<String>>(rankings);
    }
    public Student(String statusLogin){
        this.statusLogin = statusLogin;
    }
    public String getStatusLogin(){
        return this.statusLogin;
    }
    public String getFullName(){
        return this.fullName;
    }
    public byte[] getBinaryPhoto(){
        return this.photo;
    }
    public ArrayList<ArrayList<ArrayList<String>>> getAllSchedules(){
        return this.allSchedules;
    }
    public ArrayList<ArrayList<String>> getSchedule(){
        return this.schedule;
    }
    public ArrayList<ArrayList<String>> getRankings(){
        return this.rankings;
    }

    public void setStatusLogin(String statusLogin){
        this.statusLogin = statusLogin;
    }
    public void setFullName(String fullName){
        this.fullName = fullName;
    }
    public void setBinaryPhoto(byte []photo){
        this.photo = photo;
    }
    public void setAllSchedules(ArrayList<ArrayList<ArrayList<String>>> allSchedules){
        this.allSchedules = new ArrayList<ArrayList<ArrayList<String>>>(allSchedules);;
    }
    public void setSchedule(ArrayList<ArrayList<String>> schedule){
        this.schedule = new ArrayList<ArrayList<String>>(schedule);
    }
    public void setRankings(ArrayList<ArrayList<String>> rankings){
        this.rankings = new ArrayList<ArrayList<String>>(rankings);
    }

}