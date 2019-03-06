import java.io.*;
import java.util.*;
import javax.swing.JFileChooser;
import java.net.*;

public class User{

    public static void sendLoginOrRegistration(String host, int port ,Object student, DatagramSocket studentInfo){
        InetAddress address = null;
        try {
            address = InetAddress.getByName(host);
        } catch (UnknownHostException u) {
            System.out.println("Address is not valid");
            System.exit(1);
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(student);
            oos.flush();
            byte []byteStudentInfo = baos.toByteArray();
            DatagramPacket dataPacket = new DatagramPacket(byteStudentInfo, byteStudentInfo.length, address, port);
            studentInfo.send(dataPacket);
            oos.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Student recieveStatusLogin(DatagramSocket studentInfo){
        Student student = null;
        try {
            DatagramPacket studentInfoPacket = new DatagramPacket(new byte[65535], 65535);
            studentInfo.receive(studentInfoPacket);
            InetAddress destinationAddress = studentInfoPacket.getAddress();
            int port = studentInfoPacket.getPort();
            System.out.println("Datagram received from " + destinationAddress + " : " + port);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(studentInfoPacket.getData()));
            student = (Student)ois.readObject();
            ois.close();

            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return student;
        
    }

    public static byte[] choosePhoto(){
        byte []binaryPhoto = new byte[50000];
        byte []auxImage = null;
        try {
            File f = null;
            JFileChooser jf = new JFileChooser();
            if(jf.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                f = jf.getSelectedFile();
            }
            long sizeImage = f.length();
            String pathImage = f.getAbsolutePath();
            if(sizeImage > 50000){
                System.out.println("Image too heavy, needs to be smaller than 50k bytes ");
                System.exit(1);
            }
            DataInputStream dis = new DataInputStream(new FileInputStream(pathImage));
            int n = dis.read(binaryPhoto);
            auxImage = Arrays.copyOf(binaryPhoto, n);
            dis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return auxImage;
    }

    public static void downloadPhoto(Student student, String studentId){
        try {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(student.getBinaryPhoto()));
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(studentId + ".png"));
            byte aux[] = new byte[50000];
            int n = dis.read(aux);
            dos.write(aux, 0, n);
            dis.close();
            dos.close();    
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    public static void printAllSchedules(ArrayList<ArrayList<ArrayList<String>>> allSchedules){
        
        for(int s = 0; s < allSchedules.size(); s++){
            System.out.println("Schedule: " + s);
            for(int i = 0; i < allSchedules.get(0).size(); i++){
                for(int j = 0; j < allSchedules.get(0).get(0).size(); j++){
                    System.out.print(allSchedules.get(s).get(i).get(j) + ",");
                }
                System.out.println("");
            }
            System.out.println("----------");
        }
    }

    public static void printSchedule(ArrayList<ArrayList<String>> schedule){
        
        for(int i = 0; i < schedule.size(); i++){
            for(int j = 0; j < schedule.get(0).size(); j++){
                System.out.print(schedule.get(i).get(j) + ",");
            }
            System.out.println("");
        }
    }

    public static void printRankings(ArrayList<ArrayList<String>> rankings){
        
        for(int i = 0; i < rankings.size(); i++){
            for(int j = 0; j < rankings.get(0).size(); j++){
                System.out.print(rankings.get(i).get(j) + ",");
            }
            System.out.println("");
        }
    }

    public static void extractInfoStudent(Student student, String studentId){
        System.out.println("The student is registered");
        System.out.println("FullName: " + student.getFullName());
        System.out.println("Photo, saved");
        downloadPhoto(student, studentId);
        System.out.println("Inscription");
        if(student.getAllSchedules() == null){
            System.out.println("Your registration is complete");
        }else{
            System.out.println("Please choose a schedule");
            printAllSchedules(student.getAllSchedules());
        }
        if(student.getSchedule() == null){
            System.out.println("You dont have a schedule yet");
            System.out.println("You need to register a schedule");
        }else{
            System.out.println("This is your actual schedule");
            printSchedule(student.getSchedule());
        }
        if(student.getRankings() == null){
            System.out.println("You dont have rankings yet");
            System.out.println("You need to register a schedule");
        }else{
            printRankings(student.getRankings());
        }
    }

    public static void modifySchedule(String host, int port, DatagramSocket studentInfo, String choosedSchedule){
        InetAddress address2 = null;
        try {
            address2 = InetAddress.getByName(host);
        } catch (UnknownHostException u) {
            System.out.println("Address is not valid");
            System.exit(1);
        }
        try {
            byte []b = choosedSchedule.getBytes();
            DatagramPacket modify = new DatagramPacket(b, b.length, address2, port);
            studentInfo.send(modify);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException{
        DatagramSocket studentInfo = new DatagramSocket();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
       
        System.out.println("Enter the server address:");
        String host = br.readLine();
        System.out.println("Enter the server port:");
        int port = Integer.parseInt(br.readLine());

        System.out.println("Student Management System\n");
        System.out.println("If you want to log in press 1, if you want to register press 2");
        int option = Integer.parseInt(br.readLine());
        
        
        if(option == 1){ //User wants to log in
            System.out.println("Student Id: ");
            String studentId = br.readLine();

            System.out.println("Password: ");
            String studentPassword = br.readLine();

            //Send the students information to the server 
            Login studentLogin = new Login(studentId, studentPassword);
            sendLoginOrRegistration(host, port, studentLogin, studentInfo);
            //Receive the status of the login
            Student student = recieveStatusLogin(studentInfo);
            String statusLogin = student.getStatusLogin();
            if((statusLogin.equalsIgnoreCase("Login Correct"))){
                extractInfoStudent(student, studentId);

                //
                if(student.getSchedule() == null){
                    System.out.println("Choose a schedule");
                    String choosedSchedule = br.readLine();
                    modifySchedule(host, port, studentInfo, choosedSchedule);
                    Student newStudent = recieveStatusLogin(studentInfo);
                    extractInfoStudent(newStudent, studentId);
                    
                }
                //
            }else{
                System.out.println(statusLogin);
            }
            

        }
        else if(option == 2){ //User wants to register
            System.out.println("Enter your student id: ");
            String id = br.readLine();
            System.out.println("Enter your name: ");
            String name = br.readLine();
            System.out.println("Enter your parental surname: ");
            String parentalSurname = br.readLine();
            System.out.println("Enter your maternal surname: ");
            String maternalSurname = br.readLine();
            System.out.println("Enter your password: ");
            String password = br.readLine();
            System.out.println("Choose your profile picture: ");
            byte binaryPhoto[] = choosePhoto();


            Register studentRegister = new Register(id, name, parentalSurname, maternalSurname, password, binaryPhoto);
            sendLoginOrRegistration(host, port, studentRegister, studentInfo);  
            
            Student student = recieveStatusLogin(studentInfo);
            String statusLogin = student.getStatusLogin();

            if(statusLogin.equalsIgnoreCase("Login Correct")){
                extractInfoStudent(student, id);
                System.out.println("Choose a schedule");
                String choosedSchedule = br.readLine();
                modifySchedule(host, port, studentInfo, choosedSchedule);
                Student newStudent = recieveStatusLogin(studentInfo);
                extractInfoStudent(newStudent, id);
            }else{
                System.out.println(statusLogin);
            }

        }
        studentInfo.close();



    }
}