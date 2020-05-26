package chat.client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;
import javax.swing.*;


public class PrivateServerGUI extends JFrame implements Runnable{

    Socket conn;
    ObjectOutputStream out;
    ObjectInputStream in;
    JTextArea area;
    JTextField text;
    JScrollPane scroll;
    JButton invia;
    String user,avv;
    boolean go;
    Vector<String> chat;
    
    PrivateServerGUI(String u,String a,Vector<String>ch)
    {
        chat = ch;
        user = u;
        go = true;
        avv = a;
        setSize(300, 275);
        setTitle(a);
        setResizable(false);
        setLayout(null);
        addWindowListener(new FrameListener(this));
        Container c = getContentPane();
        Color col2 = new Color(147,147,147);
        c.setBackground(col2);
        area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        Color col = new Color(210,210,210);
        area.setBackground(col);
        scroll = new JScrollPane(area);
        scroll.setBorder(javax.swing.BorderFactory.createLineBorder(Color.black,2));
        scroll.setBounds(0, 0, 295, 200);
        text = new JTextField();
        text.setBounds(2, 220, 200, 20);
        text.setBackground(new Color(210,210,210));
        text.setBorder(javax.swing.BorderFactory.createLineBorder(Color.black,2));
        JPanel pan = new JPanel();
        pan.setBounds(2,205,200,15);
        pan.setBackground(Color.black);
        JLabel l = new JLabel("WRITE HERE YOUR MESSAGE");
        l.setForeground(Color.white);
        l.setFont(new Font("Tahoma",Font.BOLD,9));
        pan.add(l);
        add(pan);
        invia = new JButton("SEND");
        invia.setBounds(210, 205, 80, 35);
        invia.addActionListener(new GestoreBottone(this));
        invia.setFont(new Font("Tahoma",Font.BOLD,10));
        invia.setBackground(Color.BLACK);
        invia.setForeground(Color.white);
        this.rootPane.setDefaultButton(invia);
        add(scroll);
        add(text);
        add(invia);
        setVisible(true);
    }
    
    @Override
    public void run() {
        chat.add(avv);
        while(go)
        {
            try {
                String msg = (String) in.readObject();
                area.append(msg);
                area.setCaretPosition(area.getDocument().getLength());
            } catch (Throwable t) {
                 try{
                 chat.remove(avv);
                 conn.close();
                 in.close();
                 out.close();
                 setVisible(false);
                 dispose();
                 break;
                 }catch(Throwable ta)
                 {
                     JOptionPane.showMessageDialog(null, ta);
                 }
            }
        }
    }
    
    class GestoreBottone implements ActionListener {

        JFrame fin;
        GestoreBottone(JFrame f)
        {
            fin = f;
        }
        
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
                    String msg = "#" + user + " : " + text.getText() + "\n";
                    sendMessage(msg);
                    area.append(msg);
                    area.setCaretPosition(area.getDocument().getLength());
                    text.setText("");
                } catch (IOException ex) {
                    try {
                        chat.remove(avv);
                        conn.close();
                        in.close();
                        out.close();
                        fin.setVisible(false);
                        fin.dispose();
                    } catch (Throwable exs) {
                        JOptionPane.showMessageDialog(null, exs);
                    }
                }
            }
        }
    }
    
    void sendMessage(String msg) throws IOException {
        out.writeObject(msg);
        out.flush();
    }
    
    class FrameListener extends WindowAdapter
    {
        JFrame fin;
        FrameListener(JFrame f)
        {
            fin = f;
        }
        
        @Override
        public void windowClosing(WindowEvent e)
        {
            try{
            conn.close();
            out.close();
            in.close();
            chat.remove(avv);
            }catch(Throwable t)
            {
                JOptionPane.showMessageDialog(null, t);
            }
            go = false;
            fin.setVisible(false);
            fin.dispose();
        }
    }
}
