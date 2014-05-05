package com.hazelcast.stabilizer.common;

import com.hazelcast.logging.ILogger;
import com.hazelcast.stabilizer.Utils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static java.lang.String.format;

public class AgentsFile {

    private final static ILogger log = com.hazelcast.logging.Logger.getLogger(AgentsFile.class);

    public static void save(File agentsFile, List<AgentAddress> addresses) {
        StringBuffer text = new StringBuffer();
        for (AgentAddress agentAddress : addresses) {
            text.append(agentAddress.publicAddress)
                    .append(',')
                    .append(agentAddress.privateAddress)
                    .append('\n');
        }
        Utils.writeText(text.toString(), agentsFile);
    }

    public static List<AgentAddress> load(File agentFile) {
        String content = Utils.fileAsText(agentFile);

        String[] addresses = content.split("\n");
        int lineNumber = 1;
        List<AgentAddress> pairs = new LinkedList<AgentAddress>();
        for (String line : addresses) {
            String[] chunks = line.trim().split(",");
            AgentAddress pair = new AgentAddress();
            switch (chunks.length) {
                case 1:
                    pair.publicAddress = chunks[0];
                    pair.privateAddress = chunks[0];
                    break;
                case 2:
                    pair.publicAddress = chunks[0];
                    pair.privateAddress = chunks[1];
                    break;
                default:
                    log.severe(format("Line %s of file %s is invalid, it should contain 1 or 2 addresses separated by a comma, " +
                            "but contains %s", lineNumber, agentFile, chunks.length));
                    System.exit(1);
                    break;
            }
            pairs.add(pair);
        }
        return pairs;
    }
}
