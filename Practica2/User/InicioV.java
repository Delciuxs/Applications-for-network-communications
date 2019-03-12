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
    private static JLabel lFoto;

    private static DatagramSocket studentInfo;
    private static BufferedReader br;
    private static String host;
    private static int port;
    private static String studentId;
    private static boolean bandera;
    private static ArrayList<ArrayList<ArrayList<String>>> horarios;
    private static Student studentPrincipal;
    private static boolean esNuevo;

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

    public static String getScheduleMesssage(ArrayList<ArrayList<String>> schedule){
        String message = "";
        for(int i = 0; i < schedule.size(); i++){
            for(int j = 0; j < schedule.get(0).size(); j++){
                System.out.print(schedule.get(i).get(j) + ",");
                message+=(schedule.get(i).get(j) + ",");
            }
            System.out.println("");
            message+="\n";
        }
        return message;
    }

    public static String getRankingsMessage(ArrayList<ArrayList<String>> rankings){
        String message = "";
        for(int i = 0; i < rankings.size(); i++){
            for(int j = 0; j < rankings.get(0).size(); j++){
                System.out.print(rankings.get(i).get(j) + ",");
                message+=(rankings.get(i).get(j) + ",");
            }
            System.out.println("");
        }
        return message;
    }
    /*
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
    }*/



    private void initComponents() {

        lBienvenido = new javax.swing.JLabel();
        lFoto = new javax.swing.JLabel();
        btnInscripcion = new javax.swing.JButton();
        btnHorario = new javax.swing.JButton();
        btnCalificaciones = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        lBienvenido.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        lBienvenido.setText("Bienvenido!");
        getContentPane().add(lBienvenido);
        lBienvenido.setBounds(58, 62, 137, 29);

        //Ponemos una foto
        ImageIcon img = new ImageIcon(studentId+".png");
        lFoto.setIcon(img);
        getContentPane().add(lFoto);
        lFoto.setBounds(300, 32, 150, 150);

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

        
        if(esNuevo){
            esNuevo=false;
            //Mandamos a elegir horario
            InscripcionV f = new InscripcionV(host,port,studentId,horarios);
            f.setTitle("Inscripcion");
            f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            f.setSize(700,500);
            f.setVisible(true);
            f.setLocationRelativeTo(null);
        }
        else{
            try{
                studentInfo = new DatagramSocket();
            }catch(Exception e){

            }

            sendLoginOrRegistration(host, port, studentId);
            //Receive the status of the login
            studentPrincipal = receiveStatusLogin();
            studentInfo.close();
            if(studentPrincipal.getSchedule() != null){
            JOptionPane.showMessageDialog(null, "Inscripcion completa");
            }
            else{
                //Mandamos a elegir horario
                InscripcionV f = new InscripcionV(host,port,studentId,horarios);
                f.setTitle("Inscripcion");
                f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                f.setSize(700,500);
                f.setVisible(true);
                f.setLocationRelativeTo(null);
            }
        }
        
       
    }                                              

    private void btnHorarioActionPerformed(java.awt.event.ActionEvent evt) {
        if(esNuevo){
            JOptionPane.showMessageDialog(null, "No tienes horario. Termina tu Inscripcion");
        }
        else{
            //Si queremos ver nuestro horario primero debemos jalar a nuestro estudiante si es que hubo modificacion
            try{
                studentInfo = new DatagramSocket();
            }catch(Exception e){

            }

            sendLoginOrRegistration(host, port, studentId);
            //Receive the status of the login
            studentPrincipal = receiveStatusLogin();
            studentInfo.close();
            if(studentPrincipal.getSchedule() == null){
                JOptionPane.showMessageDialog(null, "No tienes horario. Termina tu Inscripcion");
            }
            else{
                JOptionPane.showMessageDialog(null, getScheduleMesssage(studentPrincipal.getSchedule()));
                //Mandamos a elegir horario
                // InicioV f = new InicioV(host,port);
                // f.setTitle("Inicio");
                // f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                // f.setSize(700,500);
                // f.setVisible(true);
                // f.setLocationRelativeTo(null);
            }
        }
        
    }                                          

    private void btnCalificacionesActionPerformed(java.awt.event.ActionEvent evt) {
        if(esNuevo){
            JOptionPane.showMessageDialog(null, "No tienes calficaciones. Termina tu Inscripcion");
        }
        else{
            try{
                studentInfo = new DatagramSocket();
            }catch(Exception e){

            }
            sendLoginOrRegistration(host, port, studentId);
            //Receive the status of the login
            studentPrincipal = receiveStatusLogin();
            studentInfo.close();                                            
            if(studentPrincipal.getSchedule() == null){
                JOptionPane.showMessageDialog(null, "No tienes calficaciones. Termina tu Inscripcion");
            }
            else{
                JOptionPane.showMessageDialog(null, getRankingsMessage(studentPrincipal.getRankings()));
                //Mandamos a elegir horario
                // InicioV f = new InicioV(host,port);
                // f.setTitle("Inicio");
                // f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                // f.setSize(700,500);
                // f.setVisible(true);
                // f.setLocationRelativeTo(null);
            }
        }
        
    }

    public InicioV(String host,int port,String studentId,ArrayList<ArrayList<ArrayList<String>>> allSchedules,boolean esNuevo) {
        this.host = host;
        this.port = port;
        this.studentId = studentId;
        this.bandera=true;
        this.horarios = allSchedules;
        this.esNuevo = esNuevo;
        initComponents();
    }

    public static void main(String[] args) throws IOException{
    }
}