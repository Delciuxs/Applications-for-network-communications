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



public class Cliente2 extends JFrame implements ActionListener {
    JButton BtnActualizar,BtnCrear,BtnBuscar;
    static JList<String> file_list;
    static String serverAddress;
    static DefaultListModel<String> vector;
    static HashSet<String> directories = new HashSet<>();
    static HashSet<String> dates = new HashSet<>();
    static HashSet<String> chosenFiles = new HashSet<>();
    static HashSet<String> uploadFiles = new HashSet<>();
    JPanel panelBotones,panelBuscar;
    JTextArea txtBuscar;
    JScrollPane scroll;

    //Declaraciones necesarias
    static String fechaPasada;

    //Declaraciones globales necesarias
    static String host;
    static int port;
    static InetAddress address;
    static FunctionalityClient func;
    static String userName;
    static DatagramSocket cl;

    public static void addElement(String file_name){
        vector.addElement(file_name);
        if(!dates.contains(file_name))
            directories.add(file_name);
    }

    public static void searchTopic(String topicName){
        //Iremos por todas las fechas a buscar nuestro topic

        if(dates.contains(topicName)){
            vector.clear();
            updatePosts(topicName);
        }
        else{
            String carpetaEncontrada="-1";
            for(String fecha : dates){
                //Mandamos un request para saber las carpetas dentro de esa fecha
                try {
                    func.sendMessageServer("4", cl);
                    String directoryRequest = "./Posts/"+fecha+"/";
                    func.sendMessageServer(directoryRequest,cl);
                    //Recibimos un ArrayList con los elementos
                    ArrayList<String> directorios = func.receiveDirectories(cl);
                    for (int i =0;i < directorios.size();i++ ) {
                        if(topicName.equals(directorios.get(i))){
                            carpetaEncontrada = fecha;
                        }
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }

            //Despues de que hayamos buscado ahora tenemos que ver si se la encontramos o nel
            //Si la encontramos entonces abrimos ese post
            if(!carpetaEncontrada.equals("-1")){
                fechaPasada=carpetaEncontrada;
                openPost(topicName);
                //JOptionPane.showMessageDialog(null,"El tema fue encontrado!");
            }
            else{
                JOptionPane.showMessageDialog(null,"El tema/carpeta no existe!");
            }
        }
    }

    // Funcion abrir carpetas del servidor en el Cliente2
    public static void updatePosts(String carpeta){
        try {
            func.sendMessageServer("4", cl);
            String directoryRequest = "./Posts/"+carpeta+"/";
            fechaPasada=carpeta;
            func.sendMessageServer(directoryRequest,cl);
            //Recibimos un ArrayList con los elementos
            ArrayList<String> directorios = func.receiveDirectories(cl);
            System.out.println("Carpetas");
            for (int i =0;i < directorios.size();i++ ) {
                addElement(directorios.get(i));
                System.out.println(directorios.get(i));
            }
            System.out.println("Nueva carpeta abierta: Request recibido.");

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void setDates(){
        try {
            func.sendMessageServer("4", cl);
            String directoryRequest = "./Posts";
            func.sendMessageServer(directoryRequest,cl);
            //Recibimos un ArrayList con los elementos
            ArrayList<String> directorios = func.receiveDirectories(cl);
            System.out.println("Fechas");
            for (int i =0;i < directorios.size();i++ ) {
                dates.add(directorios.get(i));
                addElement(directorios.get(i));
                System.out.println(directorios.get(i));
            }
            //System.out.println("Nueva carpeta abierta: Request recibido.");
            fechaPasada="";

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    //Aqui recibiremos el contenido del post que esta guardado LOCALMENTE
    //Nos moveremos sobre nuestra carpeta de QueryTopic. Antes de movernos deberiamos de ya haber descargado el zip que
    // nos mando el servidor con la informacion
    public static void openPost(String topicName){
        //"./Posts/"+fechaPasada+"/"+topicName+"/";
        String postRequested = "./Posts/"+fechaPasada+"/"+topicName+"/"; 
        System.out.println("Mi post a descargar es:" +postRequested );
        //Asi se debe de abrir el post
        //Decimos que queremos abrir un post
        func.sendMessageServer("1", cl);
        //Le mandamos la direccion del post
        
        func.sendMessageServer(postRequested, cl);
        //Recibimos al Topic del post
        Topic receivedTopic = func.receiveTopicObject(cl);
        //Lo salvamos en nuestra carpeta
        func.saveTopic(receivedTopic);
        
        //Leemos y desplegamos el post
        System.out.println("\nReading the post: ");
        func.infoPost(receivedTopic);

        //Aqui abriremos una ventana a la cual le pasaremos la info del comentario y nuestra info
        PostV p = new PostV(topicName,userName,func,cl,receivedTopic);
        p.setTitle("Post-"+topicName);
        p.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        p.setSize(700,850);
        p.setVisible(true);
        p.setLocationRelativeTo(null);
    }

    public static void ventanaCrear(){
        PublicarV p = new PublicarV(userName,func,cl);
        p.setTitle("Crear");
        p.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        p.setSize(700,400);
        p.setVisible(true);
        p.setLocationRelativeTo(null);
    }

    public Cliente2(String userName,String host,int port,InetAddress address,FunctionalityClient func,DatagramSocket cl) {
        //recibimos todo
        this.userName=userName;
        this.host=host;
        this.port=port;
        this.address = address;
        this.func=func;
        this.cl=cl;


        Container container = getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        file_list = new JList<String>();
        file_list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = file_list.locationToIndex(e.getPoint());//Regresa la posicion de la Lista
                    String file_name = vector.getElementAt(index);
                    //Si le damos doble click a un directorio nos muestra sus archivos

                    //Si la seleccionada es una fecha entonces vamos a ver sus carpetas
                    if(dates.contains(file_name)){
                        vector.clear();
                        updatePosts(file_name);
                    }
                    else{
                        if (directories.contains(file_name)) {
                            //Aqui abririamos el post porque agarramos el nombre de la carpeta
                            openPost(file_name);
                            vector.clear();
                            setDates();
                            System.out.println("ESTA EN EL MAP DE DIRECTORIO");
                        }
                    }
                }
            }
        };

        file_list.addMouseListener(mouseListener);
        vector = new DefaultListModel<>();
        file_list.setModel(vector); 

        //Puro diseno
        scroll = new JScrollPane(file_list);
        scroll.setMinimumSize(new Dimension(200, 200));
        container.add(scroll);

        //Panel para buscar por tema
        panelBuscar = new JPanel();
        //panelBuscar.setLayout(new BoxLayout(panelBuscar, BoxLayout.X_AXIS));
        panelBuscar.setBorder(BorderFactory.createTitledBorder("Buscar"));     
        panelBuscar.setPreferredSize(new Dimension(300, 300));
        txtBuscar = new JTextArea(5,50);
        txtBuscar.setText("Tema");
        panelBuscar.add(txtBuscar);
        container.add(panelBuscar);

        //Panel para los botones
        panelBotones = new JPanel();
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.X_AXIS));
        BtnActualizar = new JButton("Actualizar");
        panelBotones.add(BtnActualizar);
        BtnCrear = new JButton("Crear");
        panelBotones.add(BtnCrear);
        BtnBuscar = new JButton("Buscar");
        panelBotones.add(BtnBuscar);
        container.add(panelBotones);

        BtnActualizar.addActionListener(this);
        BtnCrear.addActionListener(this);
        BtnBuscar.addActionListener(this);

        setDates();
    }
    //Funcion para establecer una accion a cada boton
    public void actionPerformed(ActionEvent e) {
        JButton b = (JButton) e.getSource();
        if(b == BtnActualizar) {
            vector.clear();
            setDates();
            //updatePosts();
        }
        else if(b == BtnCrear){
            ventanaCrear();
        }
        else if(b == BtnBuscar){
            String buscar = txtBuscar.getText();
            txtBuscar.setText("Tema");
            searchTopic(buscar);
        }
    }
}