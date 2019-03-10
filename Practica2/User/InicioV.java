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

public class InicioV extends javax.swing.JFrame {

	private static JButton btnCalificaciones;
    private static JButton btnHorario;
    private static JButton btnInscripcion;
    private static JLabel lBienvenido;

    private static DatagramSocket studentInfo;
    private static BufferedReader br;
    private static String host;
    private static int port;
    private static Student s;

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

    public static Student receiveStatusLogin(DatagramSocket studentInfo){
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

        lBienvenido = new javax.swing.JLabel();
        btnInscripcion = new javax.swing.JButton();
        btnHorario = new javax.swing.JButton();
        btnCalificaciones = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        lBienvenido.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        lBienvenido.setText("Bienvenido!");
        getContentPane().add(lBienvenido);
        lBienvenido.setBounds(58, 62, 137, 29);

        btnInscripcion.setText("Inscripcion");
        btnInscripcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInscripcionActionPerformed(evt);
            }
        });
        getContentPane().add(btnInscripcion);
        btnInscripcion.setBounds(30, 190, 130, 23);

        btnHorario.setText("Horario");
        btnHorario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHorarioActionPerformed(evt);
            }
        });
        getContentPane().add(btnHorario);
        btnHorario.setBounds(180, 190, 130, 23);

        btnCalificaciones.setText("Calficaciones");
        btnCalificaciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalificacionesActionPerformed(evt);
            }
        });
        getContentPane().add(btnCalificaciones);
        btnCalificaciones.setBounds(340, 190, 130, 23);

        pack();
    }

    private void btnInscripcionActionPerformed(java.awt.event.ActionEvent evt) {                                               
        // TODO add your handling code here:
        // setVisible(false); //you can't see me!
        // dispose(); //Destroy the JFrame object
        if(s.getSchedule() != null){
            JOptionPane.showMessageDialog(null, "Inscripcion completa");
        }
        else{
            //Mandamos a elegir horario
            // InicioV f = new InicioV(host,port);
            // f.setTitle("Inicio");
            // f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            // f.setSize(700,500);
            // f.setVisible(true);
            // f.setLocationRelativeTo(null);
        }
       
    }                                              

    private void btnHorarioActionPerformed(java.awt.event.ActionEvent evt) {                                           
        if(s.getSchedule() == null){
            JOptionPane.showMessageDialog(null, "No tienes horario. Termina tu Inscripcion");
        }
        else{
            //Mandamos a elegir horario
            // InicioV f = new InicioV(host,port);
            // f.setTitle("Inicio");
            // f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            // f.setSize(700,500);
            // f.setVisible(true);
            // f.setLocationRelativeTo(null);
        }
    }                                          

    private void btnCalificacionesActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        if(s.getSchedule() == null){
            JOptionPane.showMessageDialog(null, "No tienes horario. Termina tu Inscripcion");
        }
        else{
            //Mandamos a elegir horario
            // InicioV f = new InicioV(host,port);
            // f.setTitle("Inicio");
            // f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            // f.setSize(700,500);
            // f.setVisible(true);
            // f.setLocationRelativeTo(null);
        }
    }

    public InicioV(String host,int port,Student s) {
        this.host = host;
        this.port = port;
        this.s = s;
        initComponents();
    }

    public static void main(String[] args) throws IOException{
    }
}