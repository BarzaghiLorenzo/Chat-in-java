/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.server;

import java.io.IOException;
import javax.swing.JOptionPane;


public class ChatServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        String port = JOptionPane.showInputDialog("Enter the server port:");
        try{
        int p = Integer.parseInt(port);
        Server s = new Server(p);
        }catch(Throwable t){
            JOptionPane.showMessageDialog(null, "The port server must be a number!");
        }
    }
}
