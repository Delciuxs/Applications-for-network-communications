import java.io.*;
import java.util.*;
import java.net.*;

public class SentZip{
    private DataOutputStream outputSocket;
    private String nameZip;

    public SentZip(DataOutputStream outputSocket, String nameZip){
        this.outputSocket = outputSocket;
        this.nameZip = nameZip;
    }

    public void sent() throws IOException{
        File zipFile = new File(nameZip);
        long sizeZip = zipFile.length();
        String pathZip = zipFile.getAbsolutePath();
        System.out.println("\nSendind the file: " + nameZip + " with " + sizeZip + " bytes\n");
        
        DataInputStream inputF = new DataInputStream(new FileInputStream(pathZip));
        
        outputSocket.writeUTF(nameZip);
        outputSocket.flush();
        outputSocket.writeLong(sizeZip);
        outputSocket.flush();

        long sent = 0;
        int n = 0, percentage = 0;
        byte []b = new byte[2000];
        while(sent < sizeZip){
            n = inputF.read(b);
            outputSocket.write(b, 0, n);
            outputSocket.flush();
            sent += n;
            percentage = (int)((sent*100)/ sizeZip);
            System.out.print("\rSent " + percentage + "%");
        }
        inputF.close();
        outputSocket.close();
        System.out.println("\nThe zip " + nameZip + " has already been sent");
    }
}