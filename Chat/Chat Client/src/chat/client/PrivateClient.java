package chat.client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.util.Vector;
import javax.swing.*;


public class PrivateClient extends JFrame implements Runnable {

    Socket conn;
    ObjectOutputStream out;
    ObjectInputStream in;
    JTextArea area;
    JTextField text;
    JScrollPane scroll;
    JButton invia;
    String nome, avv ,ip;
    boolean go, avvia;
    Vector<String> privatechat;

    PrivateClient(String nome, String n, String ip, Vector<String> chat) {
        privatechat = chat;
        avv = n;
        this.nome = nome;
        this.ip = ip;
        go = true;
        avvia = false;
        setSize(300, 275);
        setTitle(n);
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
        while (go) {
            try {
                String msg = (String) in.readObject();
                area.append(msg);
                area.setCaretPosition(area.getDocument().getLength());
            } catch (Throwable t) {
                try {
                    privatechat.remove(avv);
                    conn.close();
                    in.close();
                    out.close();
                } catch (Throwable ta) {
                    JOptionPane.showMessageDialog(null, ta);
                }
                setVisible(false);
                dispose();
                break;
            }
        }
    }
    
    private void apriConnessione() {
        try{
        conn = new Socket(ip, 9999);
        out = new ObjectOutputStream(conn.getOutputStream());
        out.flush();
        in = new ObjectInputStream(conn.getInputStream());
        privatechat.add(avv);
        out.writeObject(nome);
        out.flush();
        }catch(Throwable t)
        {
            JOptionPane.showMessageDialog(null, t);
        }
    }

    class GestoreBottone implements ActionListener {

        PrivateClient fin;

        GestoreBottone(PrivateClient f) {
            fin = f;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!avvia) {
                apriConnessione();
                new Thread(fin).start();
                avvia = true;
            }
            int numberCar = text.getText().length();
            if(numberCar > 100)
            {
                text.setText("");
                return;
            }
            if (out != null && numberCar > 0) {
                try {
                    String msg = "#" + nome + " : " + text.getText() + "\n";
                    sendMessage(msg);
                    area.append(msg);
                    area.setCaretPosition(area.getDocument().getLength());
                    text.setText("");
                } catch (IOException ex) {
                    try {
                        privatechat.remove(avv);
                        conn.close();
                        in.close();
                        out.close();
                    } catch (Throwable exs) {
                        JOptionPane.showMessageDialog(null, exs);
                    }
                    go = false;
                    fin.setVisible(false);
                    fin.dispose();
                }
            }
        }
    }

    void sendMessage(String msg) throws IOException {
        out.writeObject(msg);
        out.flush();
    }

    class FrameListener extends WindowAdapter {

        JFrame fin;
        FrameListener(JFrame f)
        {
            fin = f;
        }
        @Override
        public void windowClosing(WindowEvent e) {
            try {
                conn.close();
                out.close();
                in.close();
                privatechat.remove(avv);
            } catch (Throwable t) {
                JOptionPane.showMessageDialog(null, t);
            }
            go = false;
            fin.setVisible(false);
            fin.dispose();
        }
    }
    public static void main(String args[])
    {
        PrivateClient c = new PrivateClient(null,null,null,null);
    }
}
