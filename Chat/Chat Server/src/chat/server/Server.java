/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.server;

import java.awt.Container;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.Vector;


public class Server extends JFrame implements Runnable {

    ServerSocket server;
    Socket conn;
    ObjectOutputStream out;
    ObjectInputStream in;
    JTextArea area;
    JScrollPane scroll;
    Vector<ServerThread> clients;
    Vector<String> utenti;

    Server(int port) {
        setSize(300, 300);
        setTitle("The Chat (SERVER)");
        setLayout(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container c = getContentPane();
        area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        scroll = new JScrollPane(area);
        scroll.setBounds(0, 0, 295, 270);
        add(scroll);
        setVisible(true);
        utenti = new Vector<String>();
        out = null;
        in = null;
        conn = null;
        try{
        clients = new Vector<ServerThread>();
        server = new ServerSocket(port);
        area.append("Open chat session on port " + port + "\n");
        new Thread(this).start();
        }catch(Throwable t){
            JOptionPane.showMessageDialog(null, t);
            setVisible(false);
            dispose();
        }
    }

    int findUser(String nome, String ip) {
        String ipp = ip.substring(1);
        for (int i = 0; i < utenti.size(); i++) {
            String v[] = utenti.get(i).split("/");
            if (v[0].equalsIgnoreCase(nome) || v[1].equals(ipp)) {
                return 0;
            }
        }
        return 1;
    }

    @Override
    public void run() {
        while (true) {
            try {
                conn = server.accept();
                out = new ObjectOutputStream(conn.getOutputStream());
                out.flush();
                in = new ObjectInputStream(conn.getInputStream());
                try{
                String us = (String) in.readObject();
                int re = findUser(us,""+conn.getInetAddress());
                out.writeObject(re);
                out.flush();
                if(re == 1)
                {
                    String invia = us+conn.getInetAddress();
                    sendMessage("L"+invia);
                    utenti.add(invia);
                    out.writeObject(utenti);
                    out.flush();
                    clients.add(new ServerThread(this,us,invia));
                }  
            }catch(Throwable t){}
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
    }

    void sendMessage(String msg) throws IOException {
        if(msg.charAt(0) == 'E')
        {
            area.append(msg.substring(1) + " si è disconesso!\n");
            area.setCaretPosition(area.getDocument().getLength());
        }
        if(msg.charAt(0) == 'L')
        {
            area.append(msg.substring(1) + " si è connesso!\n");
            area.setCaretPosition(area.getDocument().getLength());
        }
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).out.writeObject(msg);
            clients.get(i).out.flush();
        }
    }
}
