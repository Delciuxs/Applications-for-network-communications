import java.util.*;
import java.io.Serializable;

public class Login implements Serializable{
    String studentId;
    String studentPassword;

    public Login(String studentId, String studentPassword){
        this.studentId = studentId;
        this.studentPassword = studentPassword;
    }

    public String getStudentId(){
        return this.studentId;
    }
    public String getStudentPassword(){
        return this.studentPassword;
    }

}