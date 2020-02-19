package com.jwcrain.sandcastle.consistenthash;

import com.jwcrain.sandcastle.hashring.HashRingImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/*
    A consistent hashing simulator
    We could generify this if we really wanted
 */
public class Cluster {
    private HashRingImpl<String> serverHashRing = new HashRingImpl<>(); /* Store our servers */
    private HashRingImpl<String> pairHashRing = new HashRingImpl<>(); /* Store our pairs */
    private HashMap<String, Server<String, String>> servers = new HashMap<>(); /* Server Name -> Server */
    private Server<String, String> defaultServer;

    public Cluster(String serverName) {
        defaultServer = addServer(serverName);
    }

    /* Adding a server may trigger reallocation of keys */
    public Server<String, String> addServer(String serverName) {
        Server<String, String> server = new Server<>(serverName);
        double degree = serverHashRing.put(server.getName());
        servers.put(serverName, server);

        //System.out.printf("Adding server %s\n", serverName);
        
        if (servers.size() > 1) {
            String clockwiseServerName = serverHashRing.clockwise(degree).orElse(defaultServer.getName());
            Server<String, String> clockwiseServer = servers.get(clockwiseServerName);

            double clockwiseDegree = serverHashRing
                    .getDegree(clockwiseServer.getName())
                    .orElseThrow(IllegalStateException::new);
            
            reallocateKeys(clockwiseServer, server, clockwiseDegree, degree, false);
        }
        
        return server;
    }

    /* Removing a server will trigger reallocation of keys */
    public void removeServer(String serverName) {
        if (servers.size() == 1) {
            throw new IllegalStateException("Cannot remove the last server from our cluster");
        }

        //System.out.printf("Removing server %s\n", serverName);

        Server<String, String> server = servers.get(serverName);

        double degree = serverHashRing
                .getDegree(server.getName())
                .orElseThrow(IllegalStateException::new);

        String clockwiseServerName = serverHashRing.clockwise(degree).orElse(defaultServer.getName());
        Server<String, String> clockwiseServer = servers.get(clockwiseServerName);

        double clockwiseDegree = serverHashRing
                .getDegree(clockwiseServer.getName())
                .orElseThrow(IllegalStateException::new);

        reallocateKeys(server, clockwiseServer, degree, clockwiseDegree, true);

        servers.remove(serverName);
        serverHashRing.remove(serverName);
    }

    /* Reallocate keys in between two degrees in our HashRing */
    private void reallocateKeys(Server<String, String> source, Server<String, String> dest, double sourceDegree, double destDegree, boolean all) {
        ArrayList<String> removed = new ArrayList<>();
        //System.out.printf("Reallocating keys if needed from %s(%f) to %s(%f)\n", source, sourceDegree, dest, destDegree);

        for (Map.Entry<String, String> entry : source.entrySet()) {
            //System.out.printf("Getting entryDegree for entry %s\n", entry);
            double entryDegree = pairHashRing
                    .getDegree(entry.getKey())
                    .orElseThrow(IllegalStateException::new);

            if (all || entryDegree <= destDegree || entryDegree >= sourceDegree) {
                //System.out.printf("Found a key that needs to be reallocated %s, entry(%f), source(%f), dest(%f), moving from %s to %s\n", entry.toString(), entryDegree, sourceDegree, destDegree, source, dest);
                dest.put(entry.getKey(), entry.getValue());
                removed.add(entry.getKey());
            }
        }

        for (String key : removed) {
            source.remove(key);
        }
    }

    public void put(String key, String value) {
        pairHashRing.put(key);
        Server<String, String> server = getClosestServerForPair(key);
        server.put(key, value);
        //System.out.printf("Put %s=%s in server %s\n", key, value, server);
    }

    public Optional<String> get(String key) {
        Server<String, String> server = getClosestServerForPair(key);
        //System.out.printf("Got %s from server %s\n", key, server);
        return server.get(key);
    }

    private Server<String, String> getClosestServerForPair(String key) {
        double degree = pairHashRing.getDegree(key).orElseThrow(IllegalStateException::new);
        String serverName = serverHashRing.clockwise(degree).orElse(defaultServer.getName());
        //System.out.printf("Trying to get closest server for key %s(%f), returning server name %s(%f)\n", key, degree, serverName, serverHashRing.getDegree(serverName).orElse(-1d));
        return servers.get(serverName);
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "serverHashRing=" + serverHashRing +
                ", pairHashRing=" + pairHashRing +
                ", servers=" + servers +
                ", defaultServer=" + defaultServer +
                '}';
    }
}
