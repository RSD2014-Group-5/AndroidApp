package edu.rsd2014.mobilersd.nodes;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import std_msgs.*;
import std_msgs.String;

/**
 * Created by kasper on 10/6/14.
 */
public class Listener extends AbstractNodeMain
{

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("frobitapp/listener_node");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        final Log log = connectedNode.getLog();
        Subscriber<std_msgs.String> subscriber = connectedNode.newSubscriber("frobitapp/listener", std_msgs.String._TYPE);
        subscriber.addMessageListener(new MessageListener<String>() {
            @Override
            public void onNewMessage(std_msgs.String message) {
                //log.info("I heard: \"" + message.getData() + "\"");
                log.info(message.getData() + "\"");
            }
        });
    }


}