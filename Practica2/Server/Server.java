import java.io.*;
import java.util.*;
import javax.swing.JFileChooser;
import java.net.*;

public class Server{
    public static void sendLoginOrRegistration(String host, int port ,Object student, DatagramSocket s){
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
            s.send(dataPacket);
            oos.close();
            baos.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void printMap(Map mp) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            // it.remove(); // avoids a ConcurrentModificationException
        }
    }
    public static ArrayList<ArrayList<ArrayList<String>>> schedules(){
        ArrayList<ArrayList<ArrayList<String>>> allSchedules = new ArrayList<ArrayList<ArrayList<String>>>();
        ArrayList<ArrayList<String>> schedule  = new ArrayList<ArrayList<String>>();
        ArrayList<String> row = new ArrayList<String>();

        row.add("Subject");row.add("Monday");row.add("Tuesday");row.add("Wednesday");row.add("Thursday");row.add("Friday");
        schedule.add(new ArrayList<String>(row));
        row.clear();

        row.add("POO");row.add("8.30-10.00");row.add("8.30-10.00");row.add("-");row.add("8.30-10.00");row.add("-");
        schedule.add(new ArrayList<String>(row));
        row.clear();

        row.add("Algorithms");row.add("-");row.add("10.30-12.00");row.add("10.30-12.00");row.add("10.30-12.00");row.add("10.30-12.00");
        schedule.add(new ArrayList<String>(row));
        row.clear();
        
        row.add("DataBases");row.add("12.00-13.30");row.add("-");row.add("12.00-13.30");row.add("12.00-13.30");row.add("-");
        schedule.add(new ArrayList<String>(row));
        row.clear();

        allSchedules.add(new ArrayList<ArrayList<String>>(schedule));
        schedule.clear();

        row.add("Subject");row.add("Monday");row.add("Tuesday");row.add("Wednesday");row.add("Thursday");row.add("Friday");
        schedule.add(new ArrayList<String>(row));
        row.clear();

        row.add("Networks");row.add("8.30-10.00");row.add("-");row.add("8.30-10.00");row.add("8.30-10.00");row.add("-");
        schedule.add(new ArrayList<String>(row));
        row.clear();

        row.add("Physics");row.add("10.30-12.00");row.add("10.30-12.00");row.add("-");row.add("10.30-12.00");row.add("-");
        schedule.add(new ArrayList<String>(row));
        row.clear();
        
        row.add("DataMining");row.add("-");row.add("12.00-13.30");row.add("12.00-13.30");row.add("12.00-13.30");row.add("12.00-13.30");
        schedule.add(new ArrayList<String>(row));
        row.clear();

        allSchedules.add(new ArrayList<ArrayList<String>>(schedule));
        schedule.clear();

        return allSchedules;
    }
    public static void printAllSchedules(){
        ArrayList<ArrayList<ArrayList<String>>> allSchedules = schedules();

        for(int s = 0; s < allSchedules.size(); s++){
            for(int i = 0; i < allSchedules.get(0).size(); i++){
                for(int j = 0; j < allSchedules.get(0).get(0).size(); j++){
                    System.out.print(allSchedules.get(s).get(i).get(j) + ",");
                }
                System.out.println("");
            }
            System.out.println("----------");
        }
    }
    public static Student constructStudentInfo(String statusLogin, String idStudent, ArrayList<ArrayList<ArrayList<String>>> allSchedules){
 
        Student student = new Student(statusLogin);   

        if(statusLogin.equalsIgnoreCase("Login Correct")){
            String pathFileUsersData = "./UsersData/" + idStudent + ".txt";
            ArrayList<String> allData = new ArrayList<String>();
            try {
                String data = "";
                FileReader fr2 = new FileReader(pathFileUsersData);
                BufferedReader br2 = new BufferedReader(fr2);
                while((data = br2.readLine()) != null){
                    allData.add(data);
                }
                fr2.close();
                br2.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            student.setFullName(allData.get(0) + " " + allData.get(1) + " " + allData.get(2));
            
            try {
                byte []binaryPhoto = new byte[50000];
                byte []auxImage = null;
                DataInputStream dis = new DataInputStream(new FileInputStream("./UsersData/" + idStudent + ".png"));
                int n = dis.read(binaryPhoto);
                auxImage = Arrays.copyOf(binaryPhoto, n);
                dis.close();
                student.setBinaryPhoto(auxImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            
            String inscriptionStatus = allData.get(3);
            String scheduleNumber = allData.get(4);
            if(inscriptionStatus.equalsIgnoreCase("-1")){
                student.setAllSchedules(allSchedules);
            }
            if(!scheduleNumber.equalsIgnoreCase("-1")){
                student.setSchedule(allSchedules.get(Integer.parseInt(scheduleNumber)));
                ArrayList<ArrayList<String>> rankings = new ArrayList<ArrayList<String>>();
                ArrayList<String> ranks = new ArrayList<String>();
                if(scheduleNumber.equalsIgnoreCase("0")){
                    ranks.add("POO");ranks.add("Algorithms");ranks.add("DataBases");    
                    rankings.add(new ArrayList<String>(ranks));
                    ranks.clear();
                }else{
                    ranks.add("Networks");ranks.add("Physics");ranks.add("DataMining");    
                    rankings.add(new ArrayList<String>(ranks));
                    ranks.clear();
                }
                ranks.add("8");ranks.add("9");ranks.add("10");
                rankings.add(new ArrayList<String>(ranks));
                student.setRankings(new ArrayList<ArrayList<String>> (rankings));
            }
        }
        return student;
    }

    public static void modifySchedule(DatagramSocket s, DatagramPacket studentInfo, String idStudent){
        try {
            s.receive(studentInfo);
            String choosedSchedule = new String(studentInfo.getData(), 0, studentInfo.getLength());
            System.out.println("he choosed "+ choosedSchedule);

            String pathFileUsersData = "./UsersData/" + idStudent + ".txt";
            ArrayList<String> allData = new ArrayList<String>();
            try {
                String data = "";
                FileReader fr3 = new FileReader(pathFileUsersData);
                BufferedReader br3 = new BufferedReader(fr3);
                while((data = br3.readLine()) != null){
                    allData.add(data);
                }
                fr3.close();
                br3.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            allData.set(3, "1");
            allData.set(4, choosedSchedule);

            File lastInfoStudent = new File(pathFileUsersData);
            lastInfoStudent.delete();

            FileWriter fw3 = new FileWriter("./UsersData/" + idStudent + ".txt");
            BufferedWriter bw3 = new BufferedWriter(fw3);
            bw3.write(allData.get(0) + "\n");bw3.write(allData.get(1) + "\n");
            bw3.write(allData.get(2) + "\n");bw3.write(allData.get(3) + "\n");bw3.write(allData.get(4));
            bw3.close();
            fw3.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Map<String, String> userAuthentication = new HashMap<String, String>();
        ArrayList<ArrayList<ArrayList<String>>> allSchedules = schedules();
        // printAllSchedules();   

        try {
            System.out.println("Server Initialized");
            DatagramSocket s = new DatagramSocket(1234);
            String pathFileUsersAuthentication = "./registeredUsers.txt";
            try {
                String authentication = "";
                FileReader fr = new FileReader(pathFileUsersAuthentication);
                BufferedReader br = new BufferedReader(fr);
                while((authentication = br.readLine()) != null){
                    String inputLine[] = authentication.split("_");
                    String id = inputLine[0];
                    String password = inputLine[1];
                    userAuthentication.put(id, password);
                }
                fr.close();
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            while(true){
                System.out.println("\nWaiting...");
                DatagramPacket studentInfo = new DatagramPacket(new byte[65535], 65535);
                s.receive(studentInfo);
                InetAddress destinationAddress = studentInfo.getAddress();
                int port = studentInfo.getPort();
                System.out.println("Datagram received from " + destinationAddress + " : " + port);
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(studentInfo.getData()));
                Object o = ois.readObject();
                String statusLogin = "";
                if(o instanceof Login){//Petition for login
                    Login studentLogin = (Login)o;
                    String idStudent = studentLogin.getStudentId();
                    String passwordStudent = studentLogin.getStudentPassword();
                    if(userAuthentication.containsKey(idStudent)){
                        String valueAuthentication = userAuthentication.get(idStudent);
                        if(valueAuthentication.equalsIgnoreCase(passwordStudent)){
                            statusLogin = "Login Correct";
                        }else{
                            statusLogin = "Password Incorrect";
                        }
                    }else{
                        statusLogin = "User not registered";
                    }
                    System.out.println(statusLogin);
                    
                    Student student = constructStudentInfo(statusLogin, idStudent, allSchedules);
                    sendLoginOrRegistration(destinationAddress.getHostAddress(), port, student, s); 

                    //
                    if((student.getSchedule() == null) && statusLogin.equalsIgnoreCase("Login Correct")){
                        System.out.println("Sending a user without schedule, waiting for new schedule");
                        modifySchedule(s, studentInfo, idStudent);
                        Student newStudent = constructStudentInfo(statusLogin, idStudent, allSchedules);
                        sendLoginOrRegistration(destinationAddress.getHostAddress(), port, newStudent, s);
                    }
                    //
                    
                }
                else if(o instanceof Register){//Petition for registration
                    Register studentRegister = (Register)o;
                    String idRegister = studentRegister.getId();
                    String nameRegister = studentRegister.getName();
                    String parentalSurnameRegister = studentRegister.getPaternalSurname();
                    String maternalSurnameRegister = studentRegister.getMaternalSurname();
                    String passwordRegister = studentRegister.getPassword();
                    byte photo[] = studentRegister.getBinaryPhoto();

                    if(userAuthentication.containsKey(idRegister)){
                        statusLogin = "User already registered";
                    }else{
                        statusLogin = "Login Correct";
                    }

                    System.out.println(statusLogin);

                    if(statusLogin.equalsIgnoreCase("Login Correct")){
                        
                        FileWriter fw = new FileWriter("./UsersData/" + idRegister + ".txt");
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write(nameRegister + "\n");bw.write(parentalSurnameRegister + "\n");
                        bw.write(maternalSurnameRegister + "\n");bw.write("-1\n");bw.write("-1");
                        bw.close();
                        fw.close();
                        
                        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(photo));
                        DataOutputStream dos = new DataOutputStream(new FileOutputStream("./UsersData/" + idRegister + ".png"));
                        byte aux[] = new byte[50000];
                        int n = dis.read(aux);
                        dos.write(aux, 0, n);
                        dis.close();
                        dos.close();

                        FileWriter fw2 = new FileWriter("registeredUsers.txt", true);
                        BufferedWriter bw2 = new BufferedWriter(fw2);
                        bw2.write("\n" + idRegister + "_" + passwordRegister);
                        bw2.close();
                        fw2.close();
                        userAuthentication.put(idRegister, passwordRegister);

                        Student student = constructStudentInfo(statusLogin, idRegister, allSchedules);
                        sendLoginOrRegistration(destinationAddress.getHostAddress(), port, student, s); 

                        //
                        modifySchedule(s, studentInfo, idRegister);
                        Student newStudent = constructStudentInfo(statusLogin, idRegister, allSchedules);
                        sendLoginOrRegistration(destinationAddress.getHostAddress(), port, newStudent, s);
                        //
                       
                    }else{
                        Student student = constructStudentInfo(statusLogin, idRegister, allSchedules);
                        sendLoginOrRegistration(destinationAddress.getHostAddress(), port, student, s); 
                        System.out.println(statusLogin);
                    }
                }
                
                ois.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}