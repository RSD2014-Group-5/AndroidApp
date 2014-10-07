package com.github.rosjava.android_apps.rsdapp.frobit;

/**
 * Created by kasper on 10/6/14.
 */

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;


import std_msgs.*;

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
        subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
            @Override
            public void onNewMessage(std_msgs.String message) {
                //log.info("I heard: \"" + message.getData() + "\"");
                log.info(message.getData() + "\"");
            }
        });
    }


}
