package com.helloIgnite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.cluster.ClusterGroupEmptyException;
import org.apache.ignite.lang.IgniteRunnable;


public class HelloIgnite {
    /**
     * Ignite instance
     */
    private Ignite ignite;

    /**
     * Start Ignite with given configuration file path
     * @param configurationFilePath ignite configuration file path
     */
    public void startIgnite(String configurationFilePath){

        ignite = null;

        try{
            ignite = Ignition.start(configurationFilePath);
        }
        catch(IgniteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send message from this ignite node to all ignite nodes
     * in the cluster
     * @param message
     */
    public void broadCastToAllNodes(String message){
        if (ignite != null)
            ignite.compute().broadcast(new MessageSender(message));
        else
            System.out.println("Error occurred");
    }

    /**
     * Send message from this ignite node to only remote nodes
     * in the cluster
     * @param message
     */
    public void sendMessageToRemoteNodes(String message){

        if (ignite != null){
            try{
                ClusterGroup remoteNodes = ignite.cluster().forRemotes();
                ignite.compute(remoteNodes).broadcast(new MessageSender(message));
            }
            catch (ClusterGroupEmptyException e){
                System.out.println("There is no remote node(s). Start one!!");
            }

        }
        else
            System.out.println("Error occurred");

    }

    public static void main(String[] args) {
        /* To solve that:
              SEVERE: Failed to resolve default logging config file: config/java.util.logging.properties
        */
        System.setProperty("java.util.logging.config.file", "java.util.logging.properties");

        HelloIgnite helloIgnite = new HelloIgnite();
        helloIgnite.startIgnite("example-ignite.xml");
        String message_for_broadcast = "Hello All Ignite Nodes";
        String message_for_remoteNodes = "Hello Remote Ignite Nodes";
        helloIgnite.broadCastToAllNodes(message_for_broadcast);
        helloIgnite.sendMessageToRemoteNodes(message_for_remoteNodes);
    }



    private class MessageSender implements IgniteRunnable{
        private String message;

        public MessageSender(String message){
            this.message = message;
        }


        public void run() {
            System.out.println("This message will be sent all the nodes: " +
                    message);
        }
    }
}
