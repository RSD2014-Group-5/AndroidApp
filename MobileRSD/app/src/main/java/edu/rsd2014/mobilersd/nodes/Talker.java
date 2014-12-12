package edu.rsd2014.mobilersd.nodes;

/**
 * Created by kasper on 10/6/14.
 */

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import std_msgs.*;

/**
 * A simple {@link org.ros.node.topic.Publisher} {@link org.ros.node.NodeMain}.
 *
 * @author damonkohler@google.com (Damon Kohler)
 */
public class Talker extends AbstractNodeMain {

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("frobitapp/talker_node");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final Publisher<std_msgs.String> publisher =
                //connectedNode.newPublisher("frobitapp/talker", std_msgs.String._TYPE);
                connectedNode.newPublisher("frobitapp/deadman", std_msgs.String._TYPE);
        // This CancellableLoop will be canceled automatically when the node shuts
        // down.
        connectedNode.executeCancellableLoop(new CancellableLoop() {
            private int sequenceNumber;

            @Override
            protected void setup() {
                sequenceNumber = 0;
            }

            @Override
            protected void loop() throws InterruptedException {
                std_msgs.String str = publisher.newMessage();
                str.setData("Hello world! " + sequenceNumber);
                publisher.publish(str);
                sequenceNumber++;
                Thread.sleep(1000);
            }
        });
    }
}
