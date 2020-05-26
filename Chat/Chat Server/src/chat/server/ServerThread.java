/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.server;

import java.net.*;
import java.io.*;
import java.util.Vector;
import javax.swing.JTextArea;


public class ServerThread extends Thread{

    Socket conn;
    ObjectOutputStream out;
    ObjectInputStream in; 
    Server serv;
    String user;
    JTextArea area;
    Vector<ServerThread> clients;
    String index;
    ServerThread(Server a,String user,String s)
    {
        conn = a.conn;
        out = a.out;
        in = a.in;
        serv = a;
        area = a.area;
        this.user = user;
        clients = a.clients;
        index = s;
        start();
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                String msg = (String) in.readObject();
                serv.sendMessage("#" + user + " : " + msg);
            } catch (Throwable t) {
                 try{
                 conn.close();
                 in.close();
                 out.close();
                 clients.remove(this);
                 int i = serv.utenti.indexOf(index);
                 serv.utenti.remove(i);
                 serv.sendMessage("E"+i);
                 serv.sendMessage("#" + user + " : is logged out!\n");
                 area.setCaretPosition(area.getDocument().getLength());
                 break;
                 }catch(Throwable ta){}
            }
        }
    }
}
