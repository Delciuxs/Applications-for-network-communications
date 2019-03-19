import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.JFileChooser;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;   
import java.io.*;
import java.util.HashSet;
import java.util.*;
import java.io.*;
import javafx.util.*;



public class Login extends JFrame implements ActionListener {
    JButton BtnActualizar,BtnEntrar;
    static JList<String> file_list;
    static String serverAddress;
    static DefaultListModel<String> vector;
    static HashSet<String> directories = new HashSet<>();
    static HashSet<String> dates = new HashSet<>();
    static HashSet<String> chosenFiles = new HashSet<>();
    static HashSet<String> uploadFiles = new HashSet<>();
    JPanel panelBotones,panelContenido;
    static JTextField txtUser;
    JScrollPane scroll;

    //Declaraciones necesarias
    static String fechaPasada;
    static Login f;

    //Declaraciones globales necesarias
    static String host;
    static int port;
    static InetAddress address;
    static FunctionalityClient func;
    static String userName;
    static DatagramSocket cl;

    public static void crearVentana(){
        userName= txtUser.getText();
        Cliente2 p = new Cliente2(userName,host,port,address,func,cl);
        p.setTitle("Examen");
        p.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        p.setSize(700,500);
        p.setVisible(true);
        p.setLocationRelativeTo(null);
        f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
    }

    public Login(){

        Container container = getContentPane();
        //container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setLayout(new FlowLayout());

        //Panel para poner tu propio comentario
        panelContenido = new JPanel();
        panelContenido.setBorder(BorderFactory.createTitledBorder("Login"));     
        panelContenido.setPreferredSize(new Dimension(650, 400));
        //Text de la fecha
        txtUser = new JTextField(20);
        txtUser.setText("Usuario");
        panelContenido.add(txtUser);
        container.add(panelContenido);
        //Panel para botones
        panelBotones = new JPanel();  
        panelBotones.setPreferredSize(new Dimension(650, 100));
        BtnEntrar = new JButton("Entrar");
        panelBotones.add(BtnEntrar);
        container.add(panelBotones);

        BtnEntrar.addActionListener(this);
    }


    //Funcion para establecer una accion a cada boton
    public void actionPerformed(ActionEvent e) {
        JButton b = (JButton) e.getSource();
        if(b == BtnEntrar){
            crearVentana();
        }
    }
    public static void main(String s[]) throws IOException{
        //Esto solo muestra la ventana

        //Declaraciones iniciales en consola
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the server address: ");
        host = consoleInput.readLine();
        System.out.println("Enter the port: ");
        port = Integer.parseInt(consoleInput.readLine());
        address = null;
        try {
            address = InetAddress.getByName(host);
        } catch (UnknownHostException u) {
            System.out.println("Address is not valid");
            System.exit(1);
        }
        func = new FunctionalityClient(address, port);
        

        // System.out.println("Write your username: ");
        // userName = consoleInput.readLine();
        
        cl = new DatagramSocket();

        f = new Login();
        f.setTitle("Login");
        f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        f.setSize(700,500);
        f.setVisible(true);
        f.setLocationRelativeTo(null);

    }
}