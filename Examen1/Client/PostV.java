import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.JFileChooser;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;   
import java.io.*;
import java.util.HashSet;
import java.util.*;
import java.io.*;
import javafx.util.*;



public class PostV extends JFrame implements ActionListener {
    JButton BtnComentar,btnRegresar;

    static String mensajeTotal;
    static ArrayList<Pair<String, String>> coments;
    static String imgPath;
    //ArrayList<JTextArea> txtComentarios;
    JLabel lMensaje;
    JTextArea txtMensaje,txtComentar;
    JPanel panelBotones,panelMensaje,panelAdentro,panelIndividual,panelComentarios,panelComentar,panelFoto;
    JScrollPane scrollComentarios;

    //Variables necesarias para comentar
    static FunctionalityClient func;
    static String userName;
    static DatagramSocket cl;
    static Topic topic;


    //Aqui recibiremos el contenido del post que esta guardado LOCALMENTE
    //Nos moveremos sobre nuestra carpeta de QueryTopic. Antes de movernos deberiamos de ya haber descargado el zip que
    // nos mando el servidor con la informacion
    public static void openPost(String topicName){
        ReadTopic leerTopic = new ReadTopic(topicName);
        String contentTopic = leerTopic.getContent();
        imgPath = leerTopic.getImage();
        String userWhoCreateTopic = leerTopic.getUser();
        coments = leerTopic.getComents();
        mensajeTotal=contentTopic;

        System.out.println(topicName);
        //mensajeTotal+=(topicName+"\n");
        System.out.println("Posted by: " + userWhoCreateTopic);
        //mensajeTotal+=("Posted by: " + userWhoCreateTopic+"\n");
        System.out.println("Content: ");
        //mensajeTotal+=("Content: "+"\n");
        System.out.println(contentTopic);
        //mensajeTotal+=(contentTopic+"\n");
        System.out.println("Image: " + imgPath);
        //mensajeTotal+=("Image: " + imgPath+"\n");
        System.out.println("Coments: ");
        //mensajeTotal+=("Coments: "+"\n");
        for(int i = 0; i < coments.size(); i++){
            System.out.println(coments.get(i).getKey() + ":");
            //mensajeTotal+=(coments.get(i).getKey() + ":"+"\n");
            System.out.println(coments.get(i).getValue());
            //mensajeTotal+=(coments.get(i).getValue()+"\n");
        }
        //Aqui abriremos una ventana a la cual le pasaremos la info del comentario y nuestra info
        
    }
    //Funcion para comentar
    public void comentPost(){
        String comentario = txtComentar.getText();
        func.sendMessageServer("3", cl);
        func.addNewComent(topic, comentario, userName);
        func.sendPostServer(topic, cl);
        JOptionPane.showMessageDialog(null, "Comentario anadido!");
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public void regresar(){
        
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public PostV(String topicName, String userName, FunctionalityClient func, DatagramSocket cl,Topic topic){
        //Primero inicializamos las cosas para poder comentar
        this.userName = userName;
        this.func = func;
        this.cl = cl;
        this.topic = topic;
        openPost(topicName);
        //Mostramos la foto
        
        
        Container container = getContentPane();
        //container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setLayout(new FlowLayout());

        // Este es el panel para mostrar el contenido del post
        panelMensaje = new JPanel();
        panelMensaje.setBorder(BorderFactory.createTitledBorder("Contenido"));     
        panelMensaje.setPreferredSize(new Dimension(650, 100));
        txtMensaje=new JTextArea();
        txtMensaje.setBounds(10,50,300,100);
        txtMensaje.setText(mensajeTotal);
        panelMensaje.add(txtMensaje);
        container.add(panelMensaje);

        //FOTO
        panelFoto = new JPanel();
        panelFoto.setBorder(BorderFactory.createTitledBorder("Foto"));     
        panelFoto.setPreferredSize(new Dimension(650, 150));
        ImageIcon icon = new ImageIcon(imgPath);
        Image image = icon.getImage(); // transform it 
        Image newimg = image.getScaledInstance(100, 100,  java.awt.Image.SCALE_DEFAULT); // scale it the smooth way  
        icon = new ImageIcon(newimg);  // transform it back
        JLabel label = new JLabel(icon);
        panelFoto.add(label);
        container.add(panelFoto);
        

        //Panel para poner los paneles individuales de cada comentario
        panelAdentro = new JPanel();     
        panelAdentro.setPreferredSize(new Dimension(600, (coments.size()*100)+50));
        for(int i =0; i < coments.size(); i++){
            panelIndividual = new JPanel();
            panelIndividual.setBorder(BorderFactory.createTitledBorder(coments.get(i).getKey()));     
            panelIndividual.setPreferredSize(new Dimension(500, 100));
            JTextArea txtAux = new JTextArea();
            txtAux.setBounds(10,50,300,100);
            txtAux.setText(coments.get(i).getValue());
            //txtComentarios.add(txtAux);
            panelIndividual.add(txtAux);
            panelAdentro.add(panelIndividual);
        }
        //Scroll para ver los comentarios
        scrollComentarios = new JScrollPane(panelAdentro);
        scrollComentarios.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollComentarios.setBounds(15, 30, 630, 300);
        //Panel total donde estara el scroll
        panelComentarios = new JPanel(null);
        panelComentarios.setBorder(BorderFactory.createTitledBorder("Comentarios"));
        panelComentarios.setPreferredSize(new Dimension(650, 350));
        panelComentarios.add(scrollComentarios);
        container.add(panelComentarios);

        //Panel para poner tu propio comentario
        panelComentar = new JPanel();
        panelComentar.setBorder(BorderFactory.createTitledBorder("Introduce tu comentario"));     
        panelComentar.setPreferredSize(new Dimension(650, 150));
        txtComentar=new JTextArea(5,50);
        txtComentar.setText("Holanna");
        //txtComentar.setBounds(10,50,400,100);
        panelComentar.add(txtComentar);
        container.add(panelComentar);


        //Panel para botones
        panelBotones = new JPanel();  
        panelBotones.setPreferredSize(new Dimension(650, 100));
        BtnComentar = new JButton("Comentar");
        panelBotones.add(BtnComentar);
        btnRegresar = new JButton("Regresar");
        panelBotones.add(btnRegresar);
        container.add(panelBotones);

        BtnComentar.addActionListener(this);
        btnRegresar.addActionListener(this);

        
        //JOptionPane.showMessageDialog(null, label);
    }
    //Funcion para establecer una accion a cada boton
    public void actionPerformed(ActionEvent e) {
        JButton b = (JButton) e.getSource();
        if(b == BtnComentar){
            comentPost();
        }
        else if(b == btnRegresar){
            regresar();
        }
    }
}