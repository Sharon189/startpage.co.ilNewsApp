package News;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.awt.*;
import java.util.Calendar;

/*
Created by Sharon Zakay MAY,JUN-2019
*/

public class NewsClient extends Frame implements Runnable {

    //config main parameters.
    private TextArea lblNewsHeadline, userName;   //text field input of user and server news.
    private String NewsMsg;                       //new news msg.
    private Thread thread = null;
    private int ClientPortNumber;
    private boolean clientIsActive;               // option for the user to active and deactivate his client.
    private JButton Join, Leave, Clear;           //Buttons for user use.


    //Constructor
    private NewsClient() {

        super("News Client");                  //title of the frame.
        clientIsActive = false;                     //init clientIsActive=false mean client is not active at start.

        //Throw\notice a massage for user : Due to main source\javacript could be changed Some day the packets\news can be wrong strings.
        // tested of 14.6.19 by sharon zakay.
        JOptionPane.showMessageDialog(null, "Due to javascript code  from news.startpage.co.il/english Could be changed by date,\n" +
                "The news Could be with wrong strings sequences.");

    }

    //Setup the object that created.
    private void Setup(int ClientPort) {

        ClientPortNumber = ClientPort;

        //Init buttons first text:
        Join = new JButton("Join");
        Leave = new JButton("Leave");
        Clear = new JButton("Clear");

        //Init Jbuttons listener
        listener lis = new listener();
        Join.addActionListener(lis);
        Leave.addActionListener(lis);
        Clear.addActionListener(lis);


        //Add buttons to the super frame
        this.add(Join, BorderLayout.EAST);
        this.add(Leave, BorderLayout.WEST);
        this.add(Clear, BorderLayout.SOUTH);



        //Init TextArea first text:
        userName = new TextArea("Please, insert your name here", 1, 10);
        lblNewsHeadline = new TextArea("Retrieving News From news.startpage.co.il\n");//Label();

        //Add TextArea to the super frame
        lblNewsHeadline.setBackground(Color.cyan);
        this.add(userName, BorderLayout.NORTH);
        this.add(lblNewsHeadline, BorderLayout.CENTER);

        //Config size of the frame and Show to the user the main frame.
        setSize(700, 300);
        setVisible(true);

        //Create new thread start.
        thread = new Thread(this);
        thread.start();
    }

    //Main program option for programs to change the port of the client.
    //Create new News.NewsClient and config with the chosen port.
    public static void main(String[] args) {
        final int port = 7777;
        NewsClient ob = new NewsClient();
        ob.Setup(port);
    }


    //Run the thread
    public void run() {

        //Create new Calendar client for time of the packet.
        Calendar calendar = Calendar.getInstance();

        //Create new UDP Client
        DatagramSocket ClientSoc = null;
        try {
            ClientSoc = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        System.out.println("Waiting for user to join server...");

        //This while used for check if the user pressed on Join button afterwards clientIsActive going to true.
        while (true) {

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (clientIsActive) {

                try {
                    System.out.println("ClientPortNumber is:" + ClientPortNumber);
                    ClientSoc = new DatagramSocket();

                    String Command = "GET";

                    byte[] Sendbuff;
                    Sendbuff = Command.getBytes();

                    //Use the local IP address.
                    InetAddress ServerHost = InetAddress.getByName("localhost");
                    System.out.println("ServerHost is:" + ServerHost);

                    assert ClientSoc != null;

                    ClientSoc.send(new DatagramPacket(Sendbuff, Sendbuff.length, ServerHost, ClientPortNumber));

                    //Config the receiver size.
                    byte[] Receivebuff = new byte[4096];
                    //Receive new data.
                    DatagramPacket dp = new DatagramPacket(Receivebuff, Receivebuff.length);
                    ClientSoc.receive(dp);

                    //Build the string's client has got from the server.
                    NewsMsg = new String(dp.getData(), 0, dp.getLength());
                    System.out.println("Received new MSG:" + NewsMsg);

                    //Set the string on the client GUI (TextField).
                    lblNewsHeadline.setText("Current time : "+calendar.getTime() + " \n | " + NewsMsg + " | \n");

                    //Check news updates every 4 Seconds.
                    Thread.sleep(4000);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                assert ClientSoc != null;
                ClientSoc.close();
            }

        }
    }


    //Auto-generated method for every button or input.
    private class listener implements ActionListener {

   
        public void actionPerformed(ActionEvent e) {
            Object o = e.getSource();
            // if it is the Logout button

            if (o == Join) {
                clientIsActive =true;
            }
            // if it the who is in button
            if (o == Leave) {
                clientIsActive =false;

            }

            // ok it is coming from the JTextField
            if (o == Clear) {
                // just have to send the message
                //client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, tf.getText()));
                lblNewsHeadline.setText("");
                userName.setText("");

            }


        }
    }
}

