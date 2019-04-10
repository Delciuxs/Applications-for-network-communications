import java.io.*;
import java.util.*;
import java.net.*;

public class Servidor{

    private static String addressFolderToFiles = System.getProperty("user.dir") + "/Archivos/";
    private static String serverPath = "./Archivos/";
    private static File[] localFiles;

    public static void updateFiles(Socket cl, DataInputStream dis, String path, DataOutputStream dos) throws IOException {
        File dirPath = new File(path);
        
        if(!dirPath.exists()) {
            dirPath.mkdir();
        }

        localFiles = dirPath.listFiles();

        //Escribimos el tamano
        dos.writeInt(localFiles.length);
        dos.flush();

        String info = "";
        int tipo=0;//Sera un 1 si es directorio y un 0 si es archivo
        for (File f : localFiles) {
            if (f.isDirectory()) { 
                tipo = 1;
                info = "" + f.getName();
                //Hay que poner un mejor nombre para las carpetas
                System.out.println("Dir: " + f.getParentFile()+f.getName()); 
            }
            else { 
                tipo = 0;
                info = f.getName(); //+ "  -------  " + f.length() + " bytes";
                System.out.println("File: " + f.getAbsoluteFile()); 
            }
            dos.writeInt(tipo);
            dos.flush();
            dos.writeUTF(info);
            dos.flush();   
        }
        dos.close();
        System.out.println("Informacion enviada al cliente: Request atendido."); 
    }


    public static void main(String[] args) {
        try {
            ServerSocket s = new ServerSocket(1234);
            while(true){
                System.out.println("\nWaiting for a client");
                Socket cl = s.accept();
                System.out.println("Client connected from: " + cl.getInetAddress() + " : " + cl.getPort() + "\n");

                DataOutputStream outputSocket = new DataOutputStream(cl.getOutputStream());
                DataInputStream inputSocket = new DataInputStream(cl.getInputStream());
                
                String selectedOption = inputSocket.readUTF();
                //Descargar
                if(selectedOption.equalsIgnoreCase("1")){//client wants to download
                    
                    String stringSelectedFiles = inputSocket.readUTF();//Recibimos cadena con archivos a descargar
                    //Separamos esa cadena
                    String [] selectedFiles = stringSelectedFiles.split(",");
                    //Creamos una arraylist de archivos
                    ArrayList<File> files = new ArrayList<File>();
                    //Por cada nombre de archivo de la cadena, lo metemos al arraylist
                    for(String selectedFile: selectedFiles){
                        files.add(new File(addressFolderToFiles + selectedFile));
                    }

                    //NO TOCAR
                    ZipMultiFileAndDir zipFiles = new ZipMultiFileAndDir();
                    String nameZip = "All.zip";
                    File zipGenerated = new File(nameZip);
                    zipFiles.ZipThisFiles(files, nameZip);

                    SentZip sz = new SentZip(outputSocket, nameZip);
                    sz.sent();
                    inputSocket.close();
                    cl.close();
                    zipGenerated.delete();

                }//Subir archivo
                else if(selectedOption.equalsIgnoreCase("2")){//client wants to upload
                    System.out.println("Client wants to upload");
                    ReceiveZip rz = new ReceiveZip(inputSocket);
                    rz.receive();
                    outputSocket.close();
                    cl.close();

                    String addressFolderDownloads = System.getProperty("user.dir") + "/Archivos";
                    File zipObtained = new File("All2.zip");

                    UnzipFile unZip = new UnzipFile();
                    unZip.extract(zipObtained, new File(addressFolderDownloads));
                    zipObtained.delete();
                }//Este es para mostrar las carpetas, como el que tenia en el main del servidor normal
                else if(selectedOption.equalsIgnoreCase("3")){

                    //ESTE YA ES MI CODIGO

                    //Leemos el indice del directorio
                    int indexDirectory = inputSocket.readInt();
                    //Si es diferente a menos uno significa que iremos a esa carpeta
                    if(indexDirectory != -1){
                        String newPath = "" + localFiles[indexDirectory].getAbsoluteFile();
                        System.out.println("Ahora iremos a "+ newPath);
                        updateFiles(cl, inputSocket, newPath,outputSocket);
                    }//Si es igual a -1 solo regresamos a la carpeta inicial
                    else
                        updateFiles(cl, inputSocket, serverPath,outputSocket);
                }
                inputSocket.close();
                cl.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}