import java.util.*;
import java.io.*;
import java.net.*;
import javafx.util.*;

public class FunctionalityServer{
    InetAddress address;
    int port;

    public FunctionalityServer(){
        this.address = null;
        this.port = 0;
    }
    public FunctionalityServer(InetAddress address, int port){
        this.address = address;
        this.port = port;
    }

    public ArrayList<String> getLocalDirectories(String path){
        File dirPath = new File(path);
        File[] localFiles = dirPath.listFiles();
        ArrayList<String> directories = new ArrayList<>();
        String info = "";
        for (File f : localFiles) {
            if (f.isDirectory()) { 
                info = "" + f.getName();
                directories.add(f.getName());
                //Hay que poner un mejor nombre para las carpetas
                //System.out.println("Dir: " + f.getParentFile()+"s"+f.getName()); 
            }
        }
        //System.out.println("Informacion enviada al cliente: Request atendido.");
        return directories;
    }
    
    public void sendDirectoriesClient(DatagramSocket s,String directoryRoute){
        try {
            // Aqui llamaria a una funcion que retorna un arraylist de string de los elementos
            ArrayList<String> directorios = getLocalDirectories(directoryRoute);
            // Primero mandamos la cantidad de directorios a recibir
            sendMessageClient(String.valueOf(directorios.size()),s);
            for (int i =0; i < directorios.size() ;i++ ) {
                sendMessageClient(directorios.get(i),s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    public void sendMessageClient(String message, DatagramSocket s){
        try {
            byte []m = message.getBytes();
            DatagramPacket p = new DatagramPacket(m, m.length, this.address, this.port);
            s.send(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String receiveMessageClient(DatagramSocket s){
        String selectedOption = "";
        try {
            DatagramPacket p = new DatagramPacket(new byte[65535], 65535);
            s.receive(p);
            this.address = p.getAddress();
            this.port = p.getPort();
            System.out.println("Message received from " + address + " : " + port);
            selectedOption = new String(p.getData(), 0, p.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return selectedOption;
    }
    public Topic receiveTopicObject(DatagramSocket cl){
        Topic receivedTopic = null;
        try {
            DatagramPacket dp = new DatagramPacket(new byte[65535], 65535);
            cl.receive(dp);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(dp.getData()));
            receivedTopic = (Topic)ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return receivedTopic;
    }
    public void sendTopicObjectClient(DatagramSocket s, File zipGenerated, String []dividedRequest){
        try {
            DataInputStream inputFile = new DataInputStream(new FileInputStream(zipGenerated.getAbsolutePath()));
            byte []data = new byte[200000];
            int n = inputFile.read(data);
            byte []dataFile = Arrays.copyOf(data, n);
            inputFile.close();

            String dateTopic = dividedRequest[dividedRequest.length - 2];
            String nameTopic = dividedRequest[dividedRequest.length - 1];
            Topic t = new Topic(dateTopic, nameTopic, zipGenerated.getName(), dataFile, zipGenerated.length());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(t);
            oos.flush();
            oos.close();
            byte []temp = baos.toByteArray();
            baos.close();
            DatagramPacket dp = new DatagramPacket(temp, temp.length, this.address, this.port);
            s.send(dp);
                    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void saveTopic(Topic topic){
        try {
            String dateTopic = topic.getDateTopic();
            String pathToSave = "./Posts/" + dateTopic;
            
            File folderDate = new File(pathToSave);

            if(!folderDate.exists()){
                File newFolderDate = new File(pathToSave);
                newFolderDate.mkdir();
            }
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(pathToSave + "/" + topic.getNameFile()));    
            byte []aux = new byte[200000];
            int nRead;
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(topic.getfileTopic()));
            nRead = dis.read(aux);
            dos.write(aux, 0, nRead);
            dos.flush();
            dis.close();
            dos.close();

            File zipObtained = new File(pathToSave + "/" + topic.getNameFile());
            UnzipFile unZip = new UnzipFile();
            unZip.extract(zipObtained, new File(pathToSave));
            zipObtained.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    public File createTopicZip(String postRequested, String nameZip){
        File zipGenerated = null;
        try {
            ArrayList<File> files = new ArrayList<File>();
            files.add(new File(postRequested));   

            ZipMultiFileAndDir zipFiles = new ZipMultiFileAndDir();
            zipGenerated = new File(nameZip);
            zipFiles.ZipThisFiles(files, nameZip);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return zipGenerated;
    }
    public void sendPostClient(DatagramSocket s, String postRequested){
        String [] dividedRequest = postRequested.split("/");
        String nameZip = "All.zip";
        File zipGenerated = createTopicZip(postRequested, nameZip);
        sendTopicObjectClient(s, zipGenerated, dividedRequest);
        zipGenerated.delete();
    }    
}