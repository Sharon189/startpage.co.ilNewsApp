package News;

import java.net.*;

/*
Created by Sharon Zakay MAY,JUN-2019
*/

class NewsServer
{

    //Main program of the server: Have an option for programmers to chose the server port.
    //This main program create New UDP server on port 7777 and after it print that he is ready!.
    public static void main(String[] args) throws Exception
    {
        final int port = 7777;
        boolean serverIsActive=true;                    //Used for future option -TBD.
        DatagramSocket UDPServerSocket = new DatagramSocket(port);
        System.out.println("Server's socket is ready on port:"+port+"\n Waiting...");

        //Waiting for client for ever!
        while(serverIsActive)
        {
        AcceptClientRequest ThreadUDPSocket = new AcceptClientRequest(UDPServerSocket);
        }
    }
}