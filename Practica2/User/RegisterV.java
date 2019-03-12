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

public class RegisterV extends javax.swing.JFrame {

	private static JButton btnRegistrarse;
    private static JButton btnFoto;
    private static JLabel lBoleta;
    private static JLabel lContra;
    private static JLabel lMat;
    private static JLabel lNombre;
    private static JLabel lPat;
    private static JLabel lRegistro;
    private static JTextField txtBoleta;
    private static JTextField txtContra;
    private static JTextField txtMat;
    private static JTextField txtNombre;
    private static JTextField txtPat;

    private static DatagramSocket studentInfo;
    private static BufferedReader br;
    private static String host;
    private static int port;
    private static byte binaryPhoto[];

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



    public static byte[] choosePhoto(java.awt.event.ActionEvent evt){
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

        lRegistro = new javax.swing.JLabel();
        lNombre = new javax.swing.JLabel();
        lPat = new javax.swing.JLabel();
        lBoleta = new javax.swing.JLabel();
        lMat = new javax.swing.JLabel();
        lContra = new javax.swing.JLabel();
        btnRegistrarse = new javax.swing.JButton();
        btnFoto = new javax.swing.JButton();
        txtBoleta = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        txtPat = new javax.swing.JTextField();
        txtMat = new javax.swing.JTextField();
        txtContra = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        lRegistro.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        lRegistro.setText("Registro");
        getContentPane().add(lRegistro);
        lRegistro.setBounds(219, 32, 97, 29);

        lNombre.setText("Nombre:");
        getContentPane().add(lNombre);
        lNombre.setBounds(160, 135, 60, 14);

        lPat.setText("Apellido paterno:");
        getContentPane().add(lPat);
        lPat.setBounds(119, 170, 100, 14);

        lBoleta.setText("Boleta:");
        getContentPane().add(lBoleta);
        lBoleta.setBounds(167, 100, 50, 14);

        lMat.setText("Apellido materno:");
        getContentPane().add(lMat);
        lMat.setBounds(117, 202, 100, 14);

        lContra.setText("Contrasena:");
        getContentPane().add(lContra);
        lContra.setBounds(141, 237, 70, 14);

        btnRegistrarse.setText("Registrarse");
        btnRegistrarse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startRegister(evt);
            }
        });
        getContentPane().add(btnRegistrarse);
        btnRegistrarse.setBounds(229, 271, 120, 23);


        btnFoto.setText("Foto");
        btnFoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                binaryPhoto = choosePhoto(evt);
            }
        });
        getContentPane().add(btnFoto);
        btnFoto.setBounds(349, 221, 120, 23);



        getContentPane().add(txtBoleta);
        txtBoleta.setBounds(229, 94, 100, 20);
        getContentPane().add(txtNombre);
        txtNombre.setBounds(229, 132, 100, 20);
        getContentPane().add(txtPat);
        txtPat.setBounds(229, 164, 100, 20);
        getContentPane().add(txtMat);
        txtMat.setBounds(229, 196, 100, 20);
        getContentPane().add(txtContra);
        txtContra.setBounds(229, 234, 100, 20);

        pack();
    }

    public RegisterV(String host,int port) {
        this.host = host;
        this.port = port;
        initComponents();
    }

    public static void startRegister(java.awt.event.ActionEvent evt){
    	
    	try{
            studentInfo = new DatagramSocket();
        }catch(Exception e){

        }

        String id = txtBoleta.getText();
        System.out.println(id);
        String name = txtNombre.getText();
        System.out.println(name);
        String parentalSurname = txtPat.getText();
        System.out.println(parentalSurname);
        String maternalSurname = txtMat.getText();
        System.out.println(maternalSurname);
        String password = txtContra.getText();
        System.out.println(password);
        //byte binaryPhoto[] = choosePhoto();


    	Register studentRegister = new Register(id, name, parentalSurname, maternalSurname, password, binaryPhoto);
        sendLoginOrRegistration(host, port, studentRegister);  
        
        Student student = receiveStatusLogin();
        String statusLogin = student.getStatusLogin();

        if(statusLogin.equalsIgnoreCase("Login Correct")){
            extractInfoStudent(student, id);
            System.out.println("Todo Bien");
            JOptionPane.showMessageDialog(null, "Registro exitoso");
        }else{
            JOptionPane.showMessageDialog(null, statusLogin);
            System.out.println(statusLogin);
        }

        studentInfo.close();

    }

    public static void main(String[] args) throws IOException{

    }
}