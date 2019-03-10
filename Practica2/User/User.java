import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.JFileChooser;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;   
import java.io.*;
import java.util.HashSet;
import org.xml.sax.Attributes;

public class User extends javax.swing.JFrame {

	private static javax.swing.JButton btnEntrar;
    private static javax.swing.JButton btnRegistrarse;
    private static javax.swing.JLabel lContra;
    private static javax.swing.JLabel lLogin;
    private static javax.swing.JLabel lUsuario;
    private static javax.swing.JTextField txtContra;
    private static javax.swing.JTextField txtUsuario;

    private static DatagramSocket studentInfo;
    private static BufferedReader br;
    private static String host;
    private static int port;
    private static User prim;

    public static void sendLoginOrRegistration(String host, int port ,Object student){
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

    public static Student receiveStatusLogin(){
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

    public static void modifySchedule(String host, int port, DatagramSocket studentInfo, String chosenSchedule){
        InetAddress address2 = null;
        try {
            address2 = InetAddress.getByName(host);
        } catch (UnknownHostException u) {
            System.out.println("Address is not valid");
            System.exit(1);
        }
        try {
            byte []b = chosenSchedule.getBytes();
            DatagramPacket modify = new DatagramPacket(b, b.length, address2, port);
            studentInfo.send(modify);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {

        lLogin = new javax.swing.JLabel();
        btnEntrar = new javax.swing.JButton();
        btnRegistrarse = new javax.swing.JButton();
        txtUsuario = new javax.swing.JTextField();
        txtContra = new javax.swing.JTextField();
        lUsuario = new javax.swing.JLabel();
        lContra = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Login");
        //setSize(new java.awt.Dimension(500, 500));
        getContentPane().setLayout(null);

        lLogin.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        lLogin.setText("Login");
        getContentPane().add(lLogin);
        lLogin.setBounds(230, 70, 90, 40);

        btnEntrar.setText("Entrar");
        btnEntrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startLogin(evt);
            }
        });
        getContentPane().add(btnEntrar);
        btnEntrar.setBounds(140, 270, 70, 23);

        btnRegistrarse.setText("Registrarse");
        btnRegistrarse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ventanaRegistrarse(evt);
            }
        });
        getContentPane().add(btnRegistrarse);
        btnRegistrarse.setBounds(310, 270, 110, 23);
        getContentPane().add(txtUsuario);
        txtUsuario.setBounds(260, 160, 100, 20);
        getContentPane().add(txtContra);
        txtContra.setBounds(260, 200, 100, 20);

        lUsuario.setText("Usuario");
        getContentPane().add(lUsuario);
        lUsuario.setBounds(170, 160, 50, 14);

        lContra.setText("Contrasena");
        getContentPane().add(lContra);
        lContra.setBounds(170, 200, 70, 14);

        pack();
    }

    public User() {
        initComponents();
    }

    public static void ventanaRegistrarse(java.awt.event.ActionEvent evt){
    	
    	RegisterV f = new RegisterV(host,port);
	    f.setTitle("Registro");
	    f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	    f.setSize(700,500);
	    f.setVisible(true);
	    f.setLocationRelativeTo(null);

    }

    public static void ventanaInicio(Student s){
        
        InicioV f = new InicioV(host,port,s);
        f.setTitle("Inicio");
        f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        f.setSize(700,500);
        f.setVisible(true);
        f.setLocationRelativeTo(null);

    }

    public static void startLogin(java.awt.event.ActionEvent evt){
    	try{
    		studentInfo = new DatagramSocket();
    	}catch(Exception e){

    	}
    	

    	String studentId = txtUsuario.getText();
    	String studentPassword = txtContra.getText();

    	//Send the students information to the server 
        Login studentLogin = new Login(studentId, studentPassword);
        sendLoginOrRegistration(host, port, studentLogin);
        //Receive the status of the login
        Student student = receiveStatusLogin();
        String statusLogin = student.getStatusLogin();
        if((statusLogin.equalsIgnoreCase("Login Correct"))){
            extractInfoStudent(student, studentId);
            ventanaInicio(student);
            /*
            if(student.getSchedule() == null){
                System.out.println("Choose a schedule");
                String chosenSchedule = "";
                modifySchedule(host, port, studentInfo, chosenSchedule);
                Student newStudent = receiveStatusLogin(studentInfo);
                extractInfoStudent(newStudent, studentId);
                
            }
            */
        }else{
            System.out.println(statusLogin);
        }
        studentInfo.close();
        prim.setVisible(false); //you can't see me!
        prim.dispose(); //Destroy the JFrame object
    }

    public static void main(String[] args) throws IOException{
    	java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            	prim = new User();
		        prim.setTitle("Practica 2");
		        prim.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		        prim.setSize(700,500);
		        prim.setVisible(true);
		        prim.setLocationRelativeTo(null);
                //new User().setVisible(true);
            }
        });

        
        br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the server address:");
        host = br.readLine();
        System.out.println("Enter the server port:");
        port = Integer.parseInt(br.readLine());

        System.out.println("Student Management System\n");
        /*
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
            Student student = receiveStatusLogin(studentInfo);
            String statusLogin = student.getStatusLogin();
            if((statusLogin.equalsIgnoreCase("Login Correct"))){
                extractInfoStudent(student, studentId);

                //
                if(student.getSchedule() == null){
                    System.out.println("Choose a schedule");
                    String chosenSchedule = br.readLine();
                    modifySchedule(host, port, studentInfo, chosenSchedule);
                    Student newStudent = receiveStatusLogin(studentInfo);
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
            
            Student student = receiveStatusLogin(studentInfo);
            String statusLogin = student.getStatusLogin();

            if(statusLogin.equalsIgnoreCase("Login Correct")){
                extractInfoStudent(student, id);
                System.out.println("Choose a schedule");
                String chosenSchedule = br.readLine();
                modifySchedule(host, port, studentInfo, chosenSchedule);
                Student newStudent = receiveStatusLogin(studentInfo);
                extractInfoStudent(newStudent, id);
            }else{
                System.out.println(statusLogin);
            }

        }
        */


    }
}