import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.JFileChooser;

public class ClientDropBox{
    
    public static void main(String[] args) throws IOException{
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the server address: ");
        String serverAddress = consoleInput.readLine();

        System.out.println("Enter an option: ");
        System.out.println("1) Download files from the server");
        System.out.println("2) Upload files to the server");

        String selectedOption = consoleInput.readLine();

        int port = 1234;
        Socket cl = new Socket(serverAddress, port);

        DataOutputStream outputSocket = new DataOutputStream(cl.getOutputStream());
        DataInputStream inputSocket = new DataInputStream(cl.getInputStream());

        if(selectedOption.equalsIgnoreCase("1")){//Client wants to download         
        
            outputSocket.writeUTF(selectedOption);
            outputSocket.flush();

            /*
                Printing all files or folders coming from the server
            */
            

            System.out.println("\nType name files o folders you want to download");
            System.out.println("Important: ");
            System.out.println("* Add extensions to files");
            System.out.println("* Add subfolders to files that requiered them, use / for dividing subfolders");
            System.out.println("* Divide each download request with ,\n");

            String selectedFiles = consoleInput.readLine();
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

        }else if(selectedOption.equalsIgnoreCase("2")){//Client wants to upload
            outputSocket.writeUTF(selectedOption);
            outputSocket.flush();

            /*
                Implement a drag and drop so that client can choose which files or folders want to uploads
                Keep the in an ArrayList<File> files = new ArrayList<File>();
                
            */
            //Example of folder/file path
            String stringSelectedFiles = "C:/Users/JuanJos\u00e9/Documents/SomeFolder,C:/Users/JuanJos\u00e9/Documents/SomeFile.txt";
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

        }
    }
}