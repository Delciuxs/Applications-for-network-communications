import java.io.*;
import java.util.*;
import java.net.*;

public class ServerDropBox{

    private static String addressFolderToFiles = System.getProperty("user.dir") + "/Files/";
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

                if(selectedOption.equalsIgnoreCase("1")){//client wants to download
                    

                    /*
                        Sending name files and folders to the client
                    */

                    String stringSelectedFiles = inputSocket.readUTF();
                    String [] selectedFiles = stringSelectedFiles.split(",");
                    ArrayList<File> files = new ArrayList<File>();

                    for(String selectedFile: selectedFiles){
                        files.add(new File(addressFolderToFiles + selectedFile));
                    }
            
                    ZipMultiFileAndDir zipFiles = new ZipMultiFileAndDir();
                    String nameZip = "All.zip";
                    File zipGenerated = new File(nameZip);
                    zipFiles.ZipThisFiles(files, nameZip);

                    SentZip sz = new SentZip(outputSocket, nameZip);
                    sz.sent();
                    inputSocket.close();
                    cl.close();
                    zipGenerated.delete();

                }else if(selectedOption.equalsIgnoreCase("2")){//client wants to upload
                    System.out.println("Client wants to upload");
                    ReceiveZip rz = new ReceiveZip(inputSocket);
                    rz.receive();
                    outputSocket.close();
                    cl.close();

                    String addressFolderDownloads = System.getProperty("user.dir") + "/Files/";
                    File zipObtained = new File("All2.zip");

                    UnzipFile unZip = new UnzipFile();
                    unZip.extract(zipObtained, new File(addressFolderDownloads));
                    zipObtained.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}