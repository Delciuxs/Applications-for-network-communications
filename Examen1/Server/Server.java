import java.util.*;
import java.net.*;
import java.io.*;
import javafx.util.*;

public class Server{
       
    public static void main(String[] args) {
        try {
            DatagramSocket s = new DatagramSocket(1234);
            while(true){
                System.out.println("\nWaiting for a client");
                FunctionalityServer func = new FunctionalityServer();
                String selectedOption = func.receiveMessageClient(s);
                
                //Petition for reading a post
                if(selectedOption.equalsIgnoreCase("1")){
                    System.out.println("User want to read a post");
                    String postRequested = func.receiveMessageClient(s);
                    func.sendPostClient(s, postRequested);
                }
                //Petition for creating a post
                else if(selectedOption.equalsIgnoreCase("2")){
                    System.out.println("User want to create a post");
                    Topic receivedTopic = func.receiveTopicObject(s);
                    func.saveTopic(receivedTopic);
                }
                //Petition for adding a comentary
                else if(selectedOption.equalsIgnoreCase("3")){
                    System.out.println("User want add coment to a post");
                    Topic receivedTopic = func.receiveTopicObject(s);
                    func.saveTopic(receivedTopic);
                }
                //Petition to get every directory in the given route
                else if(selectedOption.equalsIgnoreCase("4")){
                    System.out.println("User wants to get the directories");
                    String directoryRoute = func.receiveMessageClient(s);
                    //Funcion comprimida
                    func.sendDirectoriesClient(s,directoryRoute);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}