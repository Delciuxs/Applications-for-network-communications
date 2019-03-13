import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.JFileChooser;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;   
import java.io.*;
import java.util.HashSet;



public class Cliente extends JFrame implements ActionListener {
    JButton BtnActualizar,BtnDescargar,BtnSeleccionados,BtnDragDrop,BtnArchivosSubir,BtnSubir;
    static JList<String> file_list;
    static String serverAddress;
    static DefaultListModel<String> vector;
    static HashSet<String> directories = new HashSet<>();
    static HashSet<String> chosenFiles = new HashSet<>();
    static HashSet<String> uploadFiles = new HashSet<>();
    JPanel panelBotones;
    JScrollPane scroll;

    //Declaraciones necesarias
    static Socket cl;
    static DataOutputStream outputSocket;
    static DataInputStream inputSocket;

    public static void addElement(String file_name,int isDirectory){
        if(isDirectory == 1){
            vector.addElement(file_name);
            directories.add(file_name);
        }
    }

    // Funcion abrir carpetas del servidor en el cliente
    public static void updatePosts(){
        try {

            cl = new Socket(serverAddress, 1234);
            outputSocket = new DataOutputStream(cl.getOutputStream());
            inputSocket = new DataInputStream(cl.getInputStream());
            System.out.println("Creamos todo bien");

            outputSocket.writeUTF("3");
            outputSocket.flush();

            //Enviamos el indice en donde se encuentra la carpeta dentro del arreglo de Files[]
            outputSocket.writeInt(-1);
            outputSocket.flush();

            int n = inputSocket.readInt();
            //Leemos n archivos
            for(int i = 0; i < n; i++) {
                int isDirectory = inputSocket.readInt();//1 carpeta y 0 archivo
                String file_name = inputSocket.readUTF();

                addElement(file_name,isDirectory);
                System.out.println("Recibimos al archivo " + file_name);
                //Aqui se crea el elemento 
                if(isDirectory == 1)
                    System.out.println("Es un directorio ");
                else
                    System.out.println("Es un archivo ");
            }
            inputSocket.close();
            outputSocket.close();
            cl.close();
            System.out.println("Nueva carpeta abierta: Request recibido.");

        }catch(Exception e) {
            e.printStackTrace();
        }
    }




    //FUNCION PARA LEER VALORES QUE LE MANDA POR CADA PUBLICACION
    public static void openPost(int index){
        try {

            cl = new Socket(serverAddress, 1234);
            outputSocket = new DataOutputStream(cl.getOutputStream());
            inputSocket = new DataInputStream(cl.getInputStream());
            
            //Enviamos la opcion elegida, en este caso es la de ver publicacion.
            outputSocket.writeUTF("3");
            outputSocket.flush();

            //Enviamos el indice en donde se encuentra la carpeta dentro del arreglo de Files[]
            outputSocket.writeInt(index);
            outputSocket.flush();

            //Leemos el contenido
            String content = inputSocket.readUTF();
            //Leemos la ruta de la imagen
            String imagePath = inputSocket.readUTF();
            //Leemos la cantidad de comentarios
            int n = inputSocket.readInt();

            //Leemos n comentarios
            for(int i = 0; i < n; i++) {
                String user_c = inputSocket.readUTF();
                String comment = inputSocket.readUTF();
            
            }
            //Despues de que tengamos todos creamos una ventana y mostramos la publicacion.

            inputSocket.close();
            outputSocket.close();
            cl.close();

        }catch(Exception e) {
            e.printStackTrace();
        }
    }


    // Funcion descargar
    public static void iniciarDescarga(){
        try {

            cl = new Socket(serverAddress, 1234);
            outputSocket = new DataOutputStream(cl.getOutputStream());
            inputSocket = new DataInputStream(cl.getInputStream());

            outputSocket.writeUTF("1");
            outputSocket.flush();

            String auxSelected = "";
            String mensaje = "Nuestros archivos a descargar son\n";
            System.out.println(mensaje);
            for(String c : chosenFiles){
                System.out.println("Archivo -> " + c);
                mensaje+= ("Archivo -> " + c+"\n");
                auxSelected += (c+",");
            }
            
            JOptionPane.showMessageDialog(null, mensaje);
            String selectedFiles = auxSelected.substring(0,auxSelected.length()-1);

            outputSocket.writeUTF(selectedFiles);
            outputSocket.flush();

            ReceiveZip rz = new ReceiveZip(inputSocket);
            rz.receive();
            outputSocket.close();
            cl.close();

            String addressFolderDownloads = System.getProperty("user.dir") + "/Downloads";
            File zipObtained = new File("All.zip");

            UnzipFile unZip = new UnzipFile();
            unZip.extract(zipObtained, new File(addressFolderDownloads));
            zipObtained.delete();
            chosenFiles.clear();

            
            //inputSocket.close();
            System.out.println("Descargado");
            JOptionPane.showMessageDialog(null, "Descarga exitosa");

        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    //Inicia la subida de archivos
    void uploadPost(){
        try{
            cl = new Socket(serverAddress, 1234);
            outputSocket = new DataOutputStream(cl.getOutputStream());
            inputSocket = new DataInputStream(cl.getInputStream());

            //Opcion de subir post
            outputSocket.writeUTF("2");
            outputSocket.flush();

            //Valores que debera mandar para crear el post
            String content = "";
            String user = "";
            String imagePath = "";
            String subject="";


            String [] selectedFiles = stringSelectedFiles.split(",");
            ArrayList<File> files = new ArrayList<File>();

            for(String selectedFile: selectedFiles){
                files.add(new File(selectedFile));
            }

            ZipMultiFileAndDir zipFiles = new ZipMultiFileAndDir();
            String nameZip = "All2.zip";
            File zipGenerated = new File(nameZip);
            zipFiles.ZipThisFiles(files, nameZip);

            SentZip sz = new SentZip(outputSocket, nameZip);
            sz.sent();
            inputSocket.close();
            cl.close();
            zipGenerated.delete();

            System.out.println("Subiendo");
            JOptionPane.showMessageDialog(null, "Subida exitosa");
            uploadFiles.clear();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Cliente() {
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
                    if (directories.contains(file_name)) {



                        //Aqui abririamos el post
                    	openPost(index);


                        System.out.println("ESTA EN EL MAP DE DIRECTORIO");
                    }
                    
                }
            }
        };

        file_list.addMouseListener(mouseListener);
        vector = new DefaultListModel<>();
        file_list.setModel(vector); 

        //Puro diseno
        scroll = new JScrollPane(file_list);
        scroll.setMinimumSize(new Dimension(100, 200));
        container.add(scroll);
        panelBotones = new JPanel();
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.X_AXIS));
        BtnActualizar = new JButton("Actualizar");
        panelBotones.add(BtnActualizar);
        BtnDescargar = new JButton("Descargar");
        panelBotones.add(BtnDescargar);
        BtnSubir = new JButton("Subir");
        panelBotones.add(BtnSubir);

        container.add(panelBotones);

        BtnActualizar.addActionListener(this);
        BtnDescargar.addActionListener(this);
        BtnSubir.addActionListener(this);
    }
    //Funcion para establecer una accion a cada boton
    public void actionPerformed(ActionEvent e) {
        JButton b = (JButton) e.getSource();
        
        if(b == BtnActualizar) {
            vector.clear();
            updatePosts();
        }
        else if(b == BtnDescargar){
            iniciarDescarga();
        }
        else if(b == BtnSubir){
            uploadPost();
        }
    }
    public static void main(String s[]) throws IOException{
        //Esto solo muestra la ventana

        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the server address: ");
        serverAddress = consoleInput.readLine();


        Cliente f = new Cliente();
        f.setTitle("Practica 1");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(700,500);
        f.setVisible(true);
        f.setLocationRelativeTo(null);


    }
}