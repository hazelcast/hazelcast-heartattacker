package com.hazelcast.simulator.protocol.configuration;

import com.hazelcast.simulator.protocol.connector.ServerConnector;
import com.hazelcast.simulator.protocol.core.ConnectionManager;
import com.hazelcast.simulator.protocol.core.ResponseFuture;
import com.hazelcast.simulator.protocol.core.SimulatorAddress;
import com.hazelcast.simulator.protocol.handler.ConnectionListenerHandler;
import com.hazelcast.simulator.protocol.handler.ConnectionValidationHandler;
import com.hazelcast.simulator.protocol.handler.ExceptionHandler;
import com.hazelcast.simulator.protocol.handler.MessageConsumeHandler;
import com.hazelcast.simulator.protocol.handler.MessageEncoder;
import com.hazelcast.simulator.protocol.handler.MessageTestConsumeHandler;
import com.hazelcast.simulator.protocol.handler.ResponseEncoder;
import com.hazelcast.simulator.protocol.handler.ResponseHandler;
import com.hazelcast.simulator.protocol.handler.SimulatorFrameDecoder;
import com.hazelcast.simulator.protocol.handler.SimulatorProtocolDecoder;
import com.hazelcast.simulator.protocol.processors.OperationProcessor;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;

import java.util.concurrent.ConcurrentMap;

/**
 * Bootstrap configuration for a {@link com.hazelcast.simulator.protocol.connector.WorkerConnector}.
 */
public class WorkerServerConfiguration extends AbstractServerConfiguration {

    private final SimulatorAddress localAddress;

    private final MessageConsumeHandler messageConsumeHandler;
    private final MessageTestConsumeHandler messageTestConsumeHandler;
    private final ConnectionManager connectionManager;

    public WorkerServerConfiguration(OperationProcessor processor, ConcurrentMap<String, ResponseFuture> futureMap,
                                     ConnectionManager connectionManager, SimulatorAddress localAddress, int port) {
        super(processor, futureMap, localAddress, port);
        this.localAddress = localAddress;

        this.messageConsumeHandler = new MessageConsumeHandler(localAddress, processor);
        this.messageTestConsumeHandler = new MessageTestConsumeHandler(localAddress);
        this.connectionManager = connectionManager;
    }

    @Override
    public ChannelGroup getChannelGroup() {
        connectionManager.waitForAtLeastOneChannel();
        return connectionManager.getChannels();
    }

    @Override
    public void configurePipeline(ChannelPipeline pipeline, ServerConnector serverConnector) {
        pipeline.addLast("connectionValidationHandler", new ConnectionValidationHandler());
        pipeline.addLast("connectionListenerHandler", new ConnectionListenerHandler(connectionManager));
        pipeline.addLast("responseEncoder", new ResponseEncoder(localAddress));
        pipeline.addLast("messageEncoder", new MessageEncoder(localAddress, localAddress.getParent()));
        pipeline.addLast("frameDecoder", new SimulatorFrameDecoder());
        pipeline.addLast("protocolDecoder", new SimulatorProtocolDecoder(localAddress));
        pipeline.addLast("messageConsumeHandler", messageConsumeHandler);
        pipeline.addLast("testProtocolDecoder", new SimulatorProtocolDecoder(localAddress.getChild(0)));
        pipeline.addLast("testMessageConsumeHandler", messageTestConsumeHandler);
        pipeline.addLast("responseHandler", new ResponseHandler(localAddress, localAddress.getParent(), getFutureMap(),
                getLocalAddressIndex()));
        pipeline.addLast("exceptionHandler", new ExceptionHandler(serverConnector));
    }

    public void addTest(int testIndex, OperationProcessor processor) {
        messageTestConsumeHandler.addTest(testIndex, processor);
    }

    public void removeTest(int testIndex) {
        messageTestConsumeHandler.removeTest(testIndex);
    }
}
