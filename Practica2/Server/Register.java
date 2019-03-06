import java.util.*;
import java.io.Serializable;

public class Register implements Serializable{
    String id, name, paternalSurname, maternalSurname, password;
    byte []photo;
    
    public Register(String id, String name, String paternalSurname, String maternalSurname, String password, byte []photo){
        this.id = id;
        this.name = name;
        this.paternalSurname = paternalSurname;
        this.maternalSurname = maternalSurname;
        this.password = password;
        this.photo = Arrays.copyOf(photo, photo.length);
    }

    public String getId(){
        return this.id;
    } 
    public String getName(){
        return this.name;
    } 
    public String getPaternalSurname(){
        return this.paternalSurname;
    } 
    public String getMaternalSurname(){
        return this.maternalSurname;
    }
    public String getPassword(){
        return this.password;
    } 
    public byte[] getBinaryPhoto(){
        return this.photo;
    }
}