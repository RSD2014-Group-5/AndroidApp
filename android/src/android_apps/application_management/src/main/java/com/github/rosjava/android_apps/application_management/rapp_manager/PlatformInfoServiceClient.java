package com.github.rosjava.android_apps.application_management.rapp_manager;

import android.util.Log;

import org.ros.exception.ServiceNotFoundException;
import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.master.client.TopicSystemState;
import org.ros.master.client.SystemState;
import org.ros.master.client.MasterStateClient;
import org.ros.namespace.GraphName;
import org.ros.namespace.NameResolver;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

import rocon_app_manager_msgs.Icon;
import rocon_app_manager_msgs.PlatformInfo;
import rocon_app_manager_msgs.GetPlatformInfo;
import rocon_app_manager_msgs.GetPlatformInfoRequest;
import rocon_app_manager_msgs.GetPlatformInfoResponse;

/**
 * Communicates with the robot app manager and determines various facets of
 * the platform information. Actually does a bit more than platform info.
 *
 * - Determines the robot app manager's namespace.
 * - Retrieves details from the PlatformInfo service.
 */
public class PlatformInfoServiceClient extends AbstractNodeMain {
    private String namespace; // this is the namespace under which all rapp manager services reside.
    private String robotUniqueName; // unique robot name, simply the above with stripped '/''s.
    private ServiceResponseListener<GetPlatformInfoResponse> platformInfoListener;
    private PlatformInfo platformInfo;
    private ConnectedNode connectedNode;
    private String errorMessage = "";

    /**
     * Configures the service client.
     *
     * @param namespace : namespace for the app manager's services
     */
    public PlatformInfoServiceClient(String namespace) {
        this.namespace = namespace;
        this._createListeners();
    }

    public PlatformInfoServiceClient() { this._createListeners(); }

    private void _createListeners() {
        this.platformInfoListener = new ServiceResponseListener<GetPlatformInfoResponse>() {
            @Override
            public void onSuccess(GetPlatformInfoResponse message) {
                Log.i("ApplicationManagement", "platform info retrieved successfully");
                platformInfo = message.getPlatformInfo();
            }

            @Override
            public void onFailure(RemoteException e) {
                Log.e("ApplicationManagement", "failed to get platform information!");
            }
        };
    }

    /**
     * Utility function to block until platform info's callback gets processed.
     *
     * @throws ServiceNotFoundException : when it times out waiting for the service.
     */
    public void waitForResponse() throws ServiceNotFoundException {
        int count = 0;
        while ( platformInfo == null ) {
            if ( errorMessage != "" ) {  // errorMessage gets set by an exception in the run method
                throw new ServiceNotFoundException(errorMessage);
            }
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                throw new RosRuntimeException(e);
            }
            if ( count == 20 ) {  // timeout.
                throw new ServiceNotFoundException("timed out waiting for a platform_info service response");
            }
            count = count + 1;
        }
    }

    public PlatformInfo getPlatformInfo() {
        return platformInfo;
    }

    public String getRobotAppManagerNamespace() {
        return this.namespace;
    }

    /**
     * Robot unique name is simply the unadorned app manager namespace (i.e. no '/').
     * @return
     */
    public String getRobotUniqueName() {
        return this.robotUniqueName;
    }

    public String getRobotType() {
        return this.platformInfo.getRobot();
    }

    public Icon getRobotIcon() {
        return this.platformInfo.getIcon();
    }

    /**
     * This gets executed by the nodeMainExecutor. Note that any exception handling here will set an error
     * message that can be picked up when calling the waitForResponse() method.
     *
     * @param connectedNode
     */
    @Override
    public void onStart(final ConnectedNode connectedNode) {
        if (this.connectedNode != null) {
            errorMessage = "service client instances may only ever be executed once";
            Log.e("ApplicationManagement", errorMessage + ".");
            return;
        }
        this.connectedNode = connectedNode;

        // Find the rapp manager namespace
        int count = 0;
        MasterStateClient masterClient = new MasterStateClient(this.connectedNode, this.connectedNode.getMasterUri());
        while ( this.namespace == null ) {
            SystemState systemState = masterClient.getSystemState();
            for (TopicSystemState topic : systemState.getTopics()) {
                String name = topic.getTopicName();
                GraphName graph_name = GraphName.of(name);
                if ( graph_name.getBasename().toString().equals("app_list") ) {
                    this.namespace = graph_name.getParent().toString();
                    this.robotUniqueName = graph_name.getParent().toRelative().toString();
                    Log.i("ApplicationManagement", "found the namespace for the robot app manager [" + this.namespace + "]");
                    break;
                }
            }
            try {
                Thread.sleep(200);
            } catch (Exception e) {
                errorMessage = "interrupted while looking for the robot app manager.";
                Log.w("ApplicationManagement", errorMessage);
                return;
            }
            if ( count == 10 ) {  // timeout - 2s.
                errorMessage = "Timed out waiting for the robot app manager to appear.";
                Log.w("ApplicationManagement", errorMessage);
                return;
            }
            count = count + 1;
        }

        // Find the platform information
        NameResolver resolver = this.connectedNode.getResolver().newChild(this.namespace);
        String serviceName = resolver.resolve("platform_info").toString();
        ServiceClient<GetPlatformInfoRequest, GetPlatformInfoResponse> client;
        try {
            client = connectedNode.newServiceClient(serviceName,
                    GetPlatformInfo._TYPE);
            Log.d("ApplicationManagement", "service client created [" + serviceName + "]");
        } catch (ServiceNotFoundException e) {
            errorMessage = "Service not found [" + serviceName + "]";
            Log.w("ApplicationManagement", errorMessage);
            return;
        } catch (RosRuntimeException e) {
            errorMessage = "Couldn't connect to the platform_info service [is ROS_IP set?][" + e.getMessage() + "]";
            Log.e("ApplicationManagement", errorMessage);
            return;
        }
        final GetPlatformInfoRequest request = client.newMessage();
        client.call(request, platformInfoListener);
        Log.d("ApplicationManagement", "service call done [" + serviceName + "]");
    }

    /**
     * This is unused, but abstract, so have to override.
     * @return
     */
    @Override
    public GraphName getDefaultNodeName() {
        return null;
    }
}
