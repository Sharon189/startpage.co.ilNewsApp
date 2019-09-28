package News;

import java.io.*;
import java.net.*;
import java.nio.file.Paths;
import java.util.Scanner;

/*
Created by Sharon Zakay MAY,JUN-2019
*/

class AcceptClientRequest extends Thread {

    //Config parameters.
    private DatagramSocket ServerSoc;
    byte Receivebuff[] = new byte[1024];
    byte Sendbuff[] = new byte[1024];
    private DatagramPacket udpPacket;

    //Constructor : Receive UDP socket to create thread that handle one client.
    AcceptClientRequest(DatagramSocket mainServerDatagramSocket) {
        ServerSoc = mainServerDatagramSocket;
        udpPacket = new DatagramPacket(Receivebuff, Receivebuff.length);
        try {
            ServerSoc.receive(udpPacket);
        } catch (Exception ignored) {
        }
        start();
    }

    //Run the thread.
    public void run()
    {
        URL source;
        try {
            //Get the date from the website - it's a java script code.
            source = new URL("http://news.startpage.co.il/english/");
            BufferedReader urlIn = new BufferedReader(new InputStreamReader(source.openStream()));

            //indication for use "IndexOf" method.
            int indOfStartNewsLine,indOfEndNewsLine,indOfStartTimeString;

            //Config client connection details.
            InetAddress ClientHost = udpPacket.getAddress();
            int ClientPort = udpPacket.getPort();
            System.out.println(ClientHost);

            //Build the News String from the source data.
            StringBuilder News = new StringBuilder();

            //These parameters used for "substring" method.
            String NewsHeadLine,removeCharsFromNewsHeadLine,dateOfNews,timeOfNews;

            //After these string comes the specific data ( Date and time of the news and the news!).
            String startNewsString = "target='_blank'>",stringTimeOfNews="<ul class=\"menu\"><li>";
            int lenStartNewsString=startNewsString.length(),lenStringTimeOfNews = stringTimeOfNews.length(),startTimeOfnews;

            //Read the first line.
            NewsHeadLine = urlIn.readLine();

            //if the first line in't null get in and work.
            while ((NewsHeadLine = urlIn.readLine()) != null)
            {
                //Search for specific data to ensure the current date and time and News.
                indOfStartTimeString = NewsHeadLine.indexOf(stringTimeOfNews,5);
                indOfStartNewsLine = NewsHeadLine.indexOf(startNewsString,100);

                //If tou found above string get in and work.
                if (indOfStartNewsLine != -1 ){

                    //Sub string between two indexes (Begin and End).
                    dateOfNews = NewsHeadLine.substring(indOfStartTimeString+lenStringTimeOfNews,indOfStartTimeString+lenStringTimeOfNews+6);
                    startTimeOfnews = indOfStartTimeString+lenStringTimeOfNews+5+6;
                    timeOfNews = NewsHeadLine.substring(startTimeOfnews,startTimeOfnews+6);
                    indOfEndNewsLine = NewsHeadLine.indexOf("</a></span></li></ul>",indOfStartNewsLine+lenStartNewsString);
                    removeCharsFromNewsHeadLine = NewsHeadLine.substring(indOfStartNewsLine+lenStartNewsString,indOfEndNewsLine+4);

                    //Append the current data and read new line.
                    News.append("\n ").append(dateOfNews+" ").append(timeOfNews).append(" | ").append(removeCharsFromNewsHeadLine).append(" | ").append("\n");
                    NewsHeadLine = urlIn.readLine();

                    //update the condition.
                    indOfStartNewsLine = NewsHeadLine.indexOf(startNewsString,100);
                }
            }
            System.out.println("Sending Packet...");
            //Convert the News to string for sending.
            Sendbuff = News.toString().getBytes();
            System.out.println(News.toString());
            //Send the packet.
            DatagramPacket sendPacket = new DatagramPacket(Sendbuff, Sendbuff.length, ClientHost, ClientPort);
            ServerSoc.send(sendPacket);
            System.out.println("Packet Sent!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 }