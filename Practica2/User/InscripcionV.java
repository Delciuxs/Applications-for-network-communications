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

public class InscripcionV extends javax.swing.JFrame {

    private static JButton btnInscribir;
    private static JComboBox<String> cmbGrupo;
    private static JScrollPane jScrollPane1;
    private static JLabel lGrupo;
    private static JLabel lInscripcion;
    private static JTextField txtGrupo;

    private static DatagramSocket studentInfo;
    private static BufferedReader br;
    private static String host;
    private static int port;
    private static Student s;


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

    public static void modifySchedule(String chosenSchedule){
        try{
            studentInfo = new DatagramSocket();
        }catch(Exception e){

        }
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
        JOptionPane.showMessageDialog(null, "Inscripcion realizada con exito");
        studentInfo.close();
    }

    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        lInscripcion = new javax.swing.JLabel();
        lGrupo = new javax.swing.JLabel();
        txtGrupo = new javax.swing.JTextField();
        btnInscribir = new javax.swing.JButton();
        cmbGrupo = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);
        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(70, 110, 420, 260);

        lInscripcion.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lInscripcion.setText("Inscripción");
        getContentPane().add(lInscripcion);
        lInscripcion.setBounds(70, 20, 190, 30);

        lGrupo.setText("Grupo:");
        getContentPane().add(lGrupo);
        lGrupo.setBounds(70, 400, 50, 14);
        getContentPane().add(txtGrupo);
        txtGrupo.setBounds(130, 400, 120, 20);

        btnInscribir.setText("Inscribir");
        btnInscribir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInscribirActionPerformed(evt);
            }
        });
        getContentPane().add(btnInscribir);
        btnInscribir.setBounds(300, 400, 120, 23);

        cmbGrupo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2"}));
        cmbGrupo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbGrupoActionPerformed(evt);
            }
        });
        getContentPane().add(cmbGrupo);
        cmbGrupo.setBounds(70, 70, 120, 20);

        pack();
    }

    private void cmbGrupoActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // TODO add your handling code here:
    }                                        

    private void btnInscribirActionPerformed(java.awt.event.ActionEvent evt) {                                             
        // TODO add your handling code here:
        String chosenValue = String.valueOf(cmbGrupo.getSelectedIndex());
        modifySchedule(chosenValue);
    }

    public InscripcionV(String host,int port,Student s) {
        this.host = host;
        this.port = port;
        this.s = s;
        initComponents();
    }

    public static void main(String[] args) throws IOException{
    }
}