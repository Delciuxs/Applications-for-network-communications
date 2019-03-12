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
    private static JScrollPane scrollHorario;
    private static JLabel lGrupo;
    private static JLabel lInscripcion;
    private static JTextField txtGrupo;
    private static JTable tabla;

    private static DatagramSocket studentInfo;
    private static BufferedReader br;
    private static String host;
    private static int port;
    private static String studentId;
    private static ArrayList<ArrayList<ArrayList<String>>> allSchedules;


    public static void printAllSchedules(){
        
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

    public static String getSelectedSchedule(int index){
        String message = "";
        ArrayList<ArrayList<String>> schedule = allSchedules.get(index);
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

        
        lInscripcion = new javax.swing.JLabel();
        lGrupo = new javax.swing.JLabel();
        txtGrupo = new javax.swing.JTextField();
        btnInscribir = new javax.swing.JButton();
        cmbGrupo = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);


        //Cosas para mostrar el horario
        String[] titulos = {"MATERIA", "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES"};
        DefaultTableModel modelo = new DefaultTableModel(null, titulos);
        String[] fila = {"POO","8.30-10.00","8.30-10.00","-","8.30-10.00","-"};
        modelo.addRow(fila);
        tabla = new JTable(modelo); 
        

        //DefaultTableModel modelo2 = (DefaultTableModel) tablaMostrar.getModel();
        //modelo2.setRowCount(0);
        

        /*
            DefaultTableModel modelo = (DefaultTableModel) tablaMostrar.getModel();
            modelo.setRowCount(0);

            for(i = 0; i < materias.length; i++) {
                System.out.println("Despliego materia del grupo: " + grupos[i].getId());
                Grupo g = Cliente.grupos[grupos[i].getId()];
                String nombreGrupo = g.getNombre();
                String[] filaA = {nombreGrupo, materias[i].getNombre(), profs[i], horas[i][0], horas[i][1], horas[i][2], horas[i][3], horas[i][4]};     
                modelo.addRow(filaA);
            }
        */

        //tabla.setPreferredScrollableViewportSize(new Dimension(500, 70));

        scrollHorario = new javax.swing.JScrollPane();
        scrollHorario.setViewportView(tabla);
        getContentPane().add(scrollHorario);
        scrollHorario.setBounds(70, 110, 420, 260);



        

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
        //String chosenValue = String.valueOf(cmbGrupo.getSelectedIndex());
        JOptionPane.showMessageDialog(null, getSelectedSchedule(cmbGrupo.getSelectedIndex()));
    }                                        

    private void btnInscribirActionPerformed(java.awt.event.ActionEvent evt) {                                             
        // TODO add your handling code here:
        String chosenValue = String.valueOf(cmbGrupo.getSelectedIndex());
        modifySchedule(chosenValue);
    }

    public InscripcionV(String host,int port,String studentId,ArrayList<ArrayList<ArrayList<String>>> allSchedules) {
        this.host = host;
        this.port = port;
        this.studentId = studentId;
        this.allSchedules = allSchedules;
        initComponents();
    }

    public static void main(String[] args) throws IOException{
    }
}