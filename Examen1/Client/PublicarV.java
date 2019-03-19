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
import java.text.SimpleDateFormat;



public class PublicarV extends JFrame implements ActionListener {
    JButton btnPublicar,btnRegresar,btnFoto;

    static String mensajeTotal;
    static ArrayList<Pair<String, String>> coments;
    JLabel lMensaje;
    JTextArea txtContenido;
    JTextArea txtFecha,txtTema;
    JPanel panelBotones,panelMensaje,panelAdentro,panelIndividual,panelContenido;
    JScrollPane scrollComentarios;

    //Variables necesarias para comentar
    static FunctionalityClient func;
    static String userName;
    static byte[]photo;
    static DatagramSocket cl;


    
    //Funcion para comentar
    public void createPost(){
        String content = txtContenido.getText();
        String nameTopic= txtTema.getText();
        String date = new SimpleDateFormat("dd-MM-yy").format(Calendar.getInstance().getTime());;
        //date = new SimpleDateFormat("dd-MM-yy").format(Calendar.getInstance().getTime());
        //date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        //JOptionPane.showMessageDialog(null, userName+" "+content+" "+nameTopic+" "+date);
        
        func.sendMessageServer("2", cl);
        func.createAndSendNewTopic(date, nameTopic, userName, content, photo, cl);
        
        JOptionPane.showMessageDialog(null, "Publicacion creada!");
        
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public void getPhoto(){
        photo = func.choosePhoto();
    }

    public void regresar(){
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public PublicarV(String userName, FunctionalityClient func, DatagramSocket cl){
        //Primero inicializamos las cosas para poder comentar
        this.userName = userName;
        this.func = func;
        this.cl = cl;

        Container container = getContentPane();
        //container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setLayout(new FlowLayout());

        //Panel para poner tu propio comentario
        panelContenido = new JPanel();
        panelContenido.setBorder(BorderFactory.createTitledBorder("Contenido"));     
        panelContenido.setPreferredSize(new Dimension(650, 300));
        //Text de la fecha
        //txtFecha = new JTextArea(5,50);
        //txtFecha.setText("Fecha");
        //txtFecha.setBounds(10,50,200,100);
        //Text del tema
        txtTema = new JTextArea(5,50);
        txtTema.setText("Tema");
        //txtTema.setBounds(10,50,200,100);
        //Text del contenido
        txtContenido=new JTextArea(5,50);
        txtContenido.setText("Contenido");
        //txtContenido.setBounds(10,50,400,100);
        //panelContenido.add(txtFecha);
        panelContenido.add(txtTema);
        panelContenido.add(txtContenido);

        //Boton para poner la foto
        btnFoto  = new JButton("Foto");
        panelContenido.add(btnFoto);
        container.add(panelContenido);

        //Panel para botones
        panelBotones = new JPanel();  
        panelBotones.setPreferredSize(new Dimension(650, 100));
        btnPublicar = new JButton("Publicar");
        panelBotones.add(btnPublicar);
        btnRegresar = new JButton("Regresar");
        panelBotones.add(btnRegresar);
        container.add(panelBotones);

        btnPublicar.addActionListener(this);
        btnRegresar.addActionListener(this);
        btnFoto.addActionListener(this);
    }
    //Funcion para establecer una accion a cada boton
    public void actionPerformed(ActionEvent e) {
        JButton b = (JButton) e.getSource();
        if(b == btnPublicar){
            createPost();
        }
        else if(b == btnRegresar){
            regresar();
        }
        else if(b == btnFoto){
            getPhoto();
        }
    }
}