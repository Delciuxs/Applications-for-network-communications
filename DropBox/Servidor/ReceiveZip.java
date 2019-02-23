import java.io.*;
import java.util.*;
import java.net.*;

public class ReceiveZip{
    private DataInputStream inputSocket;

    public ReceiveZip(DataInputStream inputSocket){
        this.inputSocket = inputSocket;
    }

    public void receive() throws IOException{
        String nameZip = inputSocket.readUTF();
        long sizeZip = inputSocket.readLong();
        System.out.println("\nThe size of the file is : " + sizeZip + " bytes");
        DataOutputStream outputF = new DataOutputStream(new FileOutputStream(nameZip));
        long received = 0;
        int n = 0, percentage = 0;
        byte []b = new byte[2000];
        while(received < sizeZip){
            n = inputSocket.read(b);
            outputF.write(b, 0, n);
            outputF.flush();
            received += n;
            percentage = (int)((received*100)/ sizeZip);
            System.out.print("\rReceived the " + percentage + "%");
        }
        System.out.println("\nThe file " + nameZip + " has been succesfully received");
        inputSocket.close();
        outputF.close();
    }
}