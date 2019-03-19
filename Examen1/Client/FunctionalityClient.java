import java.util.*;
import java.io.*;
import java.net.*;
import javafx.util.*;
import javax.swing.JFileChooser;

public class FunctionalityClient{
    InetAddress address;
    int port;

    public FunctionalityClient(){
        this.address = null;
        this.port = 0;
    }

    public FunctionalityClient(InetAddress address, int port){
        this.address = address;
        this.port = port;
    }

    public void sendMessageServer(String message, DatagramSocket cl){
        try {
            byte []m = message.getBytes();
            DatagramPacket p = new DatagramPacket(m, m.length, this.address, this.port);
            cl.send(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String receiveMessageServer(DatagramSocket s){
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
    
    public ArrayList<String> receiveDirectories(DatagramSocket cl){
        ArrayList<String> receivedDirectories = new ArrayList<>();
        try {
            int cantidad = Integer.parseInt(receiveMessageServer(cl));
            for (int i =0;  i < cantidad; i++ ) {
                String aux = receiveMessageServer(cl);
                receivedDirectories.add(aux);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return receivedDirectories;
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

    public void sendTopicObjectServer(DatagramSocket cl, File zipGenerated, Topic topicToSend){
        try {
            DataInputStream inputFile = new DataInputStream(new FileInputStream(zipGenerated.getAbsolutePath()));
            byte []data = new byte[200000];
            int n = inputFile.read(data);
            byte []dataFile = Arrays.copyOf(data, n);
            inputFile.close();

            String dateTopic = topicToSend.getDateTopic();
            String nameTopic = topicToSend.getNameTopic();
            Topic t = new Topic(dateTopic, nameTopic, zipGenerated.getName(), dataFile, zipGenerated.length());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(t);
            oos.flush();
            oos.close();
            byte []temp = baos.toByteArray();
            baos.close();
            DatagramPacket dp = new DatagramPacket(temp, temp.length, this.address, this.port);
            cl.send(dp);
                    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void saveTopic(Topic topic){
        try {
            String pathToSave = "./QueryTopic/" + topic.getNameFile();
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(pathToSave));    
            byte []aux = new byte[200000];
            int nRead;
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(topic.getfileTopic()));
            nRead = dis.read(aux);
            dos.write(aux, 0, nRead);
            dos.flush();
            dis.close();
            dos.close();

            File zipObtained = new File(pathToSave);
            UnzipFile unZip = new UnzipFile();
            unZip.extract(zipObtained, new File("./QueryTopic/"));
            zipObtained.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    public void infoPost(Topic receivedTopic){
        String topicName = receivedTopic.getNameTopic();
        ReadTopic topic = new ReadTopic(topicName);
        String contentTopic = topic.getContent();
        String imgPath = topic.getImage();
        String userWhoCreateTopic = topic.getUser();
        ArrayList<Pair<String, String>> coments = topic.getComents();

        System.out.println(topicName);
        System.out.println("Posted by: " + userWhoCreateTopic);
        System.out.println("Content: ");
        System.out.println(contentTopic);
        System.out.println("Image: " + imgPath);
        System.out.println("Coments: ");
        for(int i = 0; i < coments.size(); i++){
            System.out.println(coments.get(i).getKey() + ":");
            System.out.println(coments.get(i).getValue());
        }
    }
    public void addNewComent(Topic receivedTopic, String newComent, String user){
        String topicName = receivedTopic.getNameTopic();
        ReadTopic topic = new ReadTopic(topicName);
        topic.addComent(user, newComent);
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
    
    public void sendPostServer(Topic topicToSend, DatagramSocket cl){
        try {
            String postRequested = "./QueryTopic/" + topicToSend.getNameTopic();
            String nameZip = "All.zip";
            File zipGenerated = createTopicZip(postRequested, nameZip);
            this.sendTopicObjectServer(cl, zipGenerated, topicToSend);
            zipGenerated.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] choosePhoto(){
        
        byte []binaryPhoto = new byte[50000];
        byte []auxImage = null;
        try {
            File f = null;
            JFileChooser jf = new JFileChooser();
            if(jf.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                f = jf.getSelectedFile();
            }
            long sizeImage = f.length();
            String pathImage = f.getAbsolutePath();
            if(sizeImage > 500000){
                System.out.println("Image too heavy, needs to be smaller than 500k bytes ");
                System.exit(1);
            }
            DataInputStream dis = new DataInputStream(new FileInputStream(pathImage));
            int n = dis.read(binaryPhoto);
            auxImage = Arrays.copyOf(binaryPhoto, n);
            dis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return auxImage;
    }

    public void deleteFolderUtil(File f){
        if(!f.isDirectory()){
            f.delete();
            return;
        }
        else{
            File []fs = f.listFiles();
            for(File s : fs){
                deleteFolderUtil(s);
            }
            f.delete();
        }
    }


    public void deleteFolder(String pathFolder){
        File f = new File(pathFolder);
        this.deleteFolderUtil(f);
    }
 
    public void createAndSendNewTopic(String date, String nameTopic, String userName, String content, byte []photo, DatagramSocket cl){
        try {
            File newFolder = new File("./QueryTopic/" + nameTopic);
            newFolder.mkdir();              
            File newComentFolder = new File("./QueryTopic/" + nameTopic + "/Coments");
            newComentFolder.mkdir();

            FileWriter fw = new FileWriter("./QueryTopic/" + nameTopic + "/user.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(userName);
            bw.close(); fw.close();

            fw = new FileWriter("./QueryTopic/" + nameTopic + "/content.txt");
            bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close(); fw.close();

            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(photo));
            DataOutputStream dos = new DataOutputStream(new FileOutputStream("./QueryTopic/" + nameTopic + "/img.png"));
            byte aux[] = new byte[500500];
            int n = dis.read(aux);
            dos.write(aux, 0, n);
            dis.close();
            dos.close();

            Topic newTopic = new Topic(date, nameTopic);
            sendPostServer(newTopic, cl);
            this.deleteFolder("./QueryTopic/" + nameTopic);
            

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
    }

    

    
}