import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartServer extends JFrame {
    private JTextField portNumber;
    private JPanel panel1;
    private JLabel port;
    private JLabel title;
    private JButton startServerButton;
    public StartServer(){
        portNumber.setText("8818");
        title.setFont(new Font("arial", Font.PLAIN, 30));
        title.setSize(new Dimension(150,30));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panel1);
        this.pack();
        panel1.setBackground(new Color(216,239,237));
        this.setVisible(true);

        startServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int port = new Integer(portNumber.getText());
                Server server=new Server( port);
                server.start();

            }
        });
    }

    public static void main(String args[]){
        StartServer Window=new StartServer();
         Window.setSize(new Dimension(350,350));
        Window.setVisible(true);

    }

}
