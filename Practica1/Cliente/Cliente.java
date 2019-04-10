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

    public static void anadirElemento(String file_name,int isDirectory){
        vector.addElement(file_name);
        if(isDirectory == 1){
            directories.add(file_name);
        }
    }

    // Funcion abrir carpetas del servidor en el cliente
    public static void navigate(int indice,String last_folder){
        try {

            cl = new Socket(serverAddress, 1234);
            outputSocket = new DataOutputStream(cl.getOutputStream());
            inputSocket = new DataInputStream(cl.getInputStream());
            System.out.println("Creamos todo bien");

            outputSocket.writeUTF("3");
            outputSocket.flush();

            //Enviamos el indice en donde se encuentra la carpeta dentro del arreglo de Files[]
            outputSocket.writeInt(indice);
            outputSocket.flush();

            int n = inputSocket.readInt();
            //Leemos n archivos
            for(int i = 0; i < n; i++) {
                int isDirectory = inputSocket.readInt();//1 carpeta y 0 archivo
                String file_name = inputSocket.readUTF();

                anadirElemento(last_folder+file_name,isDirectory);
                System.out.println("Recibimos al archivo " + last_folder +file_name);
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
    //Muestra los archivos a descargar
    void mostrarSeleccionados(){
        vector.clear();
        for(String c : chosenFiles){
            vector.addElement(c);
        }
    }
    //Muestra losa archivosa subir
    void mostrarArchivosSubir(){
        vector.clear();
        for(String c : uploadFiles){
            vector.addElement(c);
        }
    }
    //Inicia la subida de archivos
    void iniciarSubida(){
        try{
            cl = new Socket(serverAddress, 1234);
            outputSocket = new DataOutputStream(cl.getOutputStream());
            inputSocket = new DataInputStream(cl.getInputStream());

            outputSocket.writeUTF("2");
            outputSocket.flush();

            String auxSelected = "";
            String mensaje = "Nuestros archivos a subir son\n";
            System.out.println(mensaje);
            for(String c : uploadFiles){
                System.out.println("Archivo -> " + c);
                mensaje+= ("Archivo -> " + c+"\n");
                auxSelected += (c+",");
            }

            JOptionPane.showMessageDialog(null, mensaje);

            String stringSelectedFiles = auxSelected.substring(0,auxSelected.length()-1);
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
                        System.out.println("ESTA EN EL MAP DE DIRECTORIO");
                        vector.clear();
                        navigate(index,file_name+"/");
                    }
                    else{
                        //Esto es para SUBIR archivos
                        if(uploadFiles.contains(file_name)){
                            //Lo quitamos
                            uploadFiles.remove(file_name);
                            System.out.println("Quitamoss a "+ file_name);
                            JOptionPane.showMessageDialog(null, "Quitamos a "+ file_name+" de nuestros archivos a subir");
                        }
                        else{
                            //Esto es para descargar archivos
                            if(chosenFiles.contains(file_name)){
                                //Lo quitamos
                                chosenFiles.remove(file_name);
                                System.out.println("Quitamoss a "+ file_name);
                                JOptionPane.showMessageDialog(null, "Quitamos a "+ file_name+" de nuestros archivos a descargar");
                            }
                            else{
                                //Lo agregamos
                                chosenFiles.add(file_name);
                                System.out.println("Agregamos a "+ file_name +" a nuestros archivos");
                                JOptionPane.showMessageDialog(null, "Agregamos a "+ file_name +" a nuestros archivos a descargar");
                            }
                        }
                    }
                    System.out.println("Nuestros archivos seleccionados son");
                    for(String c : chosenFiles){
                        System.out.println("Archivo -> " + c);
                    }
                }
                //Si damos un click derecho
                if(SwingUtilities.isRightMouseButton(e)){
                    int index = file_list.locationToIndex(e.getPoint());//Regresa la posicion de la Lista
                    String file_name = vector.getElementAt(index);
                    //Si es una carpeta
                    if (directories.contains(file_name)) {
                        //Esto es para descargar carpetas
                        if(chosenFiles.contains(file_name)){
                            //Lo quitamos
                            chosenFiles.remove(file_name);
                            System.out.println("Quitamos la carpeta "+ file_name);
                            JOptionPane.showMessageDialog(null, "Quitamos a "+ file_name+" de nuestras carpetas seleccionadas");
                        }
                        else{
                            //Lo agregamos
                            chosenFiles.add(file_name);
                            System.out.println("Agregamos a "+ file_name +" que es una carpeta");
                            JOptionPane.showMessageDialog(null, "Agregamos a "+ file_name +" a nuestras carpetas");
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
        scroll.setMinimumSize(new Dimension(100, 200));
        container.add(scroll);
        panelBotones = new JPanel();
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.X_AXIS));
        BtnActualizar = new JButton("Carpeta inicial");
        panelBotones.add(BtnActualizar);
        BtnDescargar = new JButton("Descargar");
        panelBotones.add(BtnDescargar);
        BtnSeleccionados = new JButton("Archivos a descargar");

        panelBotones.add(BtnSeleccionados);
        BtnDragDrop = new JButton("Drag and drop");
        panelBotones.add(BtnDragDrop);
        BtnArchivosSubir = new JButton("Archivos a subir");
        panelBotones.add(BtnArchivosSubir);
        BtnSubir = new JButton("Subir");
        panelBotones.add(BtnSubir);

        container.add(panelBotones);

        BtnActualizar.addActionListener(this);
        BtnDescargar.addActionListener(this);
        BtnSeleccionados.addActionListener(this);
        BtnDragDrop.addActionListener(this);
        BtnArchivosSubir.addActionListener(this);
        BtnSubir.addActionListener(this);
    }
    //Funcion para establecer una accion a cada boton
    public void actionPerformed(ActionEvent e) {
        JButton b = (JButton) e.getSource();
        
        if(b == BtnActualizar) {
            vector.clear();
            navigate(-1,"");
        }
        else if(b == BtnDescargar){
            iniciarDescarga();
        }
        else if(b == BtnSeleccionados){
            mostrarSeleccionados();
        }else if(b == BtnDragDrop){
            crearVentana();
        }
        else if(b == BtnArchivosSubir){
            mostrarArchivosSubir();
        }else if(b == BtnSubir){
            iniciarSubida();
        }
    }
    //Funcion para crear el drag and drop
    public void crearVentana(){
        JFrame frame = new JFrame("Cosas");
        final JTextArea text = new JTextArea();
        frame.getContentPane().add(new JScrollPane(text),BorderLayout.CENTER);
        
        //El text indica a que se le hara
        new FileDrop( System.out, text, /*dragBorder,*/ new FileDrop.Listener()
        {   public void filesDropped(File[] files )
            {   for( int i = 0; i < files.length; i++ )
                {   try
                    {   
                        //Agregamos archivo
                        if(!uploadFiles.contains(files[i].getCanonicalPath())){
                            uploadFiles.add(files[i].getCanonicalPath());
                            text.append( files[i].getCanonicalPath() + "\n" );
                        }
                    }   // end try
                    catch(IOException e ) {}
                }   // end for: through each dropped file
            }   // end filesDropped
        }); // end FileDrop.Listener

        frame.setBounds( 100, 100, 300, 400 );
        frame.setDefaultCloseOperation( frame.HIDE_ON_CLOSE );
        frame.setVisible(true);
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