
package chat.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Vector;
import javax.swing.*;

public class Login extends JFrame{
    JTextField user,ip,port;
    JLabel utente,chiave,prot,porta;
    JButton connetti;
    Login()
    {
        setSize(280,210);
        setTitle("The Chat (LOG IN)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        JPanel pan = new JPanel();
        utente = new JLabel("Enter the username:");
        user = new JTextField(20);
        prot = new JLabel("Enter the Server IP:");
        ip = new JTextField("localhost",20);
        porta = new JLabel("Enter the port Server:");
        port = new JTextField("1000",20);
        connetti = new JButton("Connect");
        connetti.addActionListener(new GestoreBottone(this));
        this.getRootPane().setDefaultButton(connetti);
        pan.add(utente);
        pan.add(user);
        pan.add(prot);
        pan.add(ip);
        pan.add(porta);
        pan.add(port);
        pan.add(connetti);
        add(pan);
        setVisible(true);
    }
    
    boolean caratteriSpeciali(String s)
    {
        for(int i=0;i<s.length();i++)
            if(s.charAt(i) == '/') return false;
        return true;
    }
    
    class GestoreBottone implements ActionListener
    {
        JFrame frame;
        GestoreBottone(JFrame j)
        {
            frame = j;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            String nome = user.getText();
            int p;
            try{
            p = Integer.parseInt(port.getText());
            }catch(Throwable t){
                JOptionPane.showMessageDialog(null, "The port should be a number!");
                return;
            }
            String i = ip.getText();
            if(nome.isEmpty() || i.isEmpty())
            {
                JOptionPane.showMessageDialog(null, "Fields left empty!");
                return;
            }
            if(nome.length() < 4 || nome.length() > 15 || !caratteriSpeciali(nome))
            {
                JOptionPane.showMessageDialog(null, "The name must be ,between 4 and 15, and without '/'");
                return;
            }
            Socket s;
            ObjectOutputStream out;
            ObjectInputStream in;
            try{
                s = new Socket(i,p);
            }catch(Throwable t)
            {
                JOptionPane.showMessageDialog(null, "No response from the server!");
                return;
            }
            try{
            out = new ObjectOutputStream(s.getOutputStream());
            in = new ObjectInputStream(s.getInputStream());
            out.writeObject(nome);
            out.flush();
            int accesso = (Integer) in.readObject();
            if(accesso == 0)
            {
                JOptionPane.showMessageDialog(null,"Instance already opened!");
                return;
            }
            frame.setVisible(false);
            frame.dispose();
            Vector<String> vect = (Vector<String>) in.readObject();
            Client client = new Client(nome);
            client.conn = s;
            client.out = out;
            client.in = in;
            client.port = p;
            client.utenti = vect;
            new Thread(client).start();
            }catch(Throwable t)
            {
                JOptionPane.showMessageDialog(null, t);
            }
        }    
    }
}
