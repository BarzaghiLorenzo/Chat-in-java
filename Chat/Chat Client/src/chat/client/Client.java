package chat.client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Vector;
import javax.swing.*;

public class Client extends JFrame implements Runnable {

    int port;
    JTextArea area;
    JTextField text;
    JButton invia,privata;
    JScrollPane scroll,scroll2;
    Socket conn;
    JList list;
    ObjectOutputStream out;
    ObjectInputStream in;
    String user;
    String ip;
    DefaultListModel model;
    Vector<String> utenti;
    Vector<String> privatechat;
    Image img;
    
    Client(String n) {
        Toolkit t = Toolkit.getDefaultToolkit();
        //img = t.getImage("logo.png");
        user = n;
        privatechat = new Vector<String>();
        setSize(485, 445);
        setTitle("The Chat (" + user + ")");
        setLayout(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container c = getContentPane();
        Color col = new Color(147,147,147);
        c.setBackground(col);
        area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setBackground(new Color(210,210,210));
        JPanel p1 = new JPanel();
        p1.setBounds(5,60,350,20);
        p1.setBackground(Color.BLACK);
        JLabel lab = new JLabel("WELCOME TO THE CHAT (TCP/IP)");
        lab.setFont(new Font("Tahoma",Font.BOLD,10));
        lab.setForeground(Color.white);
        p1.add(lab);
        add(p1);
        scroll = new JScrollPane(area);
        scroll.setBorder(javax.swing.BorderFactory.createLineBorder(Color.black,2));
        scroll.setBounds(5, 80, 350, 280);
        JLabel llist = new JLabel("USER LIST");
        JPanel p = new JPanel();
        p.setBackground(Color.BLACK);
        p.setBounds(365,60,110,20);
        llist.setFont(new Font("Tahoma",Font.BOLD,10));
        llist.setForeground(Color.white);        
        p.add(llist);
        model = new DefaultListModel();
        list = new JList(model);
        col = new Color(210,210,210);
        list.setBackground(col);
        scroll2 = new JScrollPane(list);
        scroll2.setBounds(365,80,110,255);
        scroll2.setBorder(javax.swing.BorderFactory.createLineBorder(Color.black,2));
        privata = new JButton("PRIVATE");
        privata.setBounds(365,340,110,20);
        privata.addActionListener(new GestoreLista());
        privata.setFont(new Font("Tahoma",Font.BOLD,10));
        privata.setBackground(Color.black);
        privata.setForeground(Color.white);
        JPanel p2 = new JPanel();
        p2.setBounds(5,370,350,20);
        p2.setBackground(Color.black);
        JLabel l = new JLabel("WRITE HERE YOUR MESSAGE");
        l.setFont(new Font("Tahoma",Font.BOLD,10));
        l.setForeground(Color.white);
        p2.add(l);
        add(p2);
        text = new JTextField();
        text.setBackground(new Color(210,210,210));
        text.setBounds(5, 390, 350, 20);
        text.setBorder(javax.swing.BorderFactory.createLineBorder(Color.black,2));
        invia = new JButton("SEND");
        invia.setFont(new Font("Tahoma",Font.BOLD,11));
        invia.setBackground(Color.black);
        invia.setForeground(Color.white);
        invia.setBounds(365, 370, 110, 40);
        invia.addActionListener(new GestoreBottone());
        this.getRootPane().setDefaultButton(invia);
        add(text);
        add(scroll);
        add(invia);
        add(scroll2);
        add(privata);
        add(p);
        setVisible(true);
    }
   
    @Override
    public void paint(Graphics g)
    {
        this.paintComponents(g);
        //g.drawImage(img,0,30, 485, 54, this);
    }
    
    @Override
    public void run() {
        new PrivateServer(user,privatechat);
        try {
            area.append("Successful connession with " + conn.getInetAddress().getHostName() + "...\n");
            for(int i=0;i<utenti.size();i++)
            {
                String nome = getNome(utenti.get(i));
                model.addElement(nome);
            }
            sendMessage("is logged in!\n");
        } catch (Throwable t) {
            area.append("Connession failed!\n");
        }
        setVisible(true);
        while (true) {
            if(area.getLineCount()%50 == 0) 
            {
                try{
                area.replaceRange("", area.getLineStartOffset(0),area.getLineEndOffset(20));
                area.setCaretPosition(area.getDocument().getLength());
                }catch(Throwable t){}
            }
            try {
                String msg = (String) in.readObject();
                if(msg.charAt(0) == 'E')
                {
                    String c = msg.substring(1);
                    int i = Integer.parseInt(c);
                    model.remove(i);
                    utenti.remove(i);
                }
                else if(msg.charAt(0) == 'L')
                {
                    String c = msg.substring(1);
                    model.addElement(getNome(c));
                    utenti.add(c);
                }
                else{
                area.append(msg);
                area.setCaretPosition(area.getDocument().getLength());
                }
            } catch (Throwable ex) {
                try {
                    conn.close();
                    in.close();
                    out.close();
                    area.append("No response from the server!\n");
                    area.setCaretPosition(area.getDocument().getLength());
                    break;
                } catch (Throwable exs) {
                    JOptionPane.showMessageDialog(null, exs);
                }
            }
        }
    }

    void sendMessage(String msg) throws IOException {
        out.writeObject(msg);
        out.flush();
    }
    
    String getNome(String n)
    {
        String vet[] = n.split("/");
        return vet[0];
    }
    
    String getIP(String n)
    {
        String vet[] = n.split("/");
        return vet[1];
    }
    
    int isPrivate(String nome)
    {
        for(int i=0;i<privatechat.size();i++)
            if(privatechat.get(i).equals(nome)) return 0;
        return 1;
    }

    class GestoreBottone implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int numberCar = text.getText().length();
            if(numberCar > 100)
            {
                text.setText("");
                return;
            }
            if (out != null && numberCar > 0) {
                try {
                    String msg = text.getText() + "\n";
                    sendMessage(msg);
                    text.setText("");
                } catch (IOException ex) {
                    try {
                        conn.close();
                        in.close();
                        out.close();
                        area.append("No response from the server!\n");
                    } catch (Throwable exs) {
                        JOptionPane.showMessageDialog(null, exs);
                    }
                }
            }
        }
    }
    
    class GestoreLista implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            int index = list.getSelectedIndex();
            String n = (String) list.getSelectedValue();
            int ok = isPrivate(n);
            if(index != -1 && ok == 1 && !n.equalsIgnoreCase(user)){
            String ip = getIP(utenti.get(index));
            PrivateClient c = new PrivateClient(user,n,ip,privatechat);
            }
        }  
    }
    
    public static void main(String args[])
    {
        Client c = new Client("ciao");
    }
}
