/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import javax.swing.JOptionPane;


public class PrivateServer implements Runnable {

    ServerSocket server;
    Socket conn;
    ObjectOutputStream out;
    ObjectInputStream in;
    String nome;
    Vector<String> chat;

    PrivateServer(String nome, Vector<String> c) {
        chat = c;
        this.nome = nome;
        try {
            server = new ServerSocket(9999);
            new Thread(this).start();
        } catch (Throwable t) {
            JOptionPane.showMessageDialog(null, t);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                conn = server.accept();
                out = new ObjectOutputStream(conn.getOutputStream());
                out.flush();
                in = new ObjectInputStream(conn.getInputStream());
                String n = (String) in.readObject();
                PrivateServerGUI ch = new PrivateServerGUI(nome, n, chat);
                ch.conn = conn;
                ch.out = out;
                ch.in = in;
                new Thread(ch).start();
            } catch (Throwable t) {
                JOptionPane.showMessageDialog(null, t);
            }
        }
    }
}
