import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Network {
    private final HashTable network;
    private int visitCounter = 0;
    private MinHeap sharedHeap;
    private boolean pathFindingActive = false;

    /**
     * Initializes a new Network instance with a specified initial capacity for the
     * host hash table
     */
    public Network(int initialCapacity) {
        this.network = new HashTable(initialCapacity);
    }

    /**
     * Spawns a new host in the network if the ID is valid and unique
     */
    public String spawnHost(String hostId, int clearanceLevel) {
        if (network.get(hostId) != null)
            return "Some error occurred in spawn_host.";
        if (!hostId.matches("[A-Z0-9_]+"))
            return "Some error occurred in spawn_host.";

        network.put(new Host(hostId, clearanceLevel));
        return "Spawned host " + hostId + " with clearance level " + clearanceLevel + ".";
    }

    /**
     * Creates a backdoor connection between two hosts
     */
    public String linkBackdoor(String hostId1, String hostId2, int latency, int bandwidth, int firewallLevel) {
        if (hostId1.equals(hostId2))
            return "Some error occurred in link_backdoor.";
        Host host1 = network.get(hostId1);
        Host host2 = network.get(hostId2);
        if (host1 == null || host2 == null)
            return "Some error occurred in link_backdoor.";

        for (int i = 0; i < host1.connections.size(); i++) {
            Backdoor connection = host1.connections.get(i);
            if (connection.getOther(host1).name.equals(hostId2)) {
                return "Some error occurred in link_backdoor.";
            }
        }
        Backdoor newBackdoor = new Backdoor(host1, host2, latency, bandwidth, firewallLevel);
        host1.connections.add(newBackdoor);
        host2.connections.add(newBackdoor);
        return "Linked " + hostId1 + " <-> " + hostId2 + " with latency " + latency + "ms, bandwidth " + bandwidth
                + "Mbps, firewall " + firewallLevel + ".";
    }

    /**
     * Seals or unseals an existing backdoor connection
     */
    public String sealBackdoor(String hostId1, String hostId2) {
        Host host1 = network.get(hostId1);
        Host host2 = network.get(hostId2);
        if (host1 == null || host2 == null)
            return "Some error occurred in seal_backdoor.";

        Backdoor targetConnection = null;
        for (int i = 0; i < host1.connections.size(); i++) {
            Backdoor connection = host1.connections.get(i);
            if (connection.getOther(host1).name.equals(hostId2)) {
                targetConnection = connection;
                break;
            }
        }
        if (targetConnection == null)
            return "Some error occurred in seal_backdoor.";
        if (!targetConnection.isSealed) {
            targetConnection.isSealed = true;
            return "Backdoor " + hostId1 + " <-> " + hostId2 + " sealed.";
        } else {
            targetConnection.isSealed = false;
            return "Backdoor " + hostId1 + " <-> " + hostId2 + " unsealed.";
        }
    }

    /**
     * Finds the optimal path between two hosts given bandwidth and latency constraints
     */
    public String traceRoute(String startId, String endId, int minBandwidth, int latencyMultiplier) {
        Host startHost = network.get(startId);
        Host endHost = network.get(endId);
        if (startHost == null || endHost == null)
            return "Some error occurred in trace_route.";
        if (startHost == endHost)
            return "Optimal route " + startId + " -> " + endId + ": " + startId + " (Latency = 0ms)";
        visitCounter++;
        if (!pathFindingActive) {
            pathFindingActive = true;
            sharedHeap = new MinHeap();
        }

        RouteState bestPath = PathFinder.findBestPath(startHost, endHost, minBandwidth,
                latencyMultiplier, visitCounter, sharedHeap);

        if (bestPath == null) {
            return "No route found from " + startId + " to " + endId;
        }

        String pathString = bestPath.getPath();
        long totalLatency = (long) networkRound(bestPath.totalCost, 0);

        return "Optimal route " + startId + " -> " + endId + ": " + pathString +
                " (Latency = " + totalLatency + "ms)";
    }

    /**
     * Scans the network to count disconnected components
     */
    public String scanConnectivity() {
        int componentsCount = countComponents(null, null);
        if (componentsCount == 1 || componentsCount == 0)
            return "Network is fully connected.";
        return "Network has " + componentsCount + " disconnected components.";
    }


    /**
     * Simulates a breach by checking if a host is an articulation point or a
     * backdoor is a bridge
     */
    public String simulateBreach(String hostId1, String hostId2) {
        if (hostId2 == null) {
            Host targetHost = network.get(hostId1);
            if (targetHost == null)
                return "Some error occurred in simulate_breach.";
            int componentsBefore = countComponents(null,null);
            int componentsAfter = countComponents(targetHost,null);
            if (componentsAfter > componentsBefore) {
                return "Host " + hostId1 + " IS an articulation point.\nFailure results in " + componentsAfter
                        + " disconnected components.";
            } else {
                return "Host " + hostId1 + " is NOT an articulation point. Network remains the same.";
            }

        } else {

            Host host1 = network.get(hostId1);
            Host host2 = network.get(hostId2);
            if (host1 == null || host2 == null)
                return "Some error occurred in simulate_breach.";

            Backdoor targetLink = null;
            for (int i = 0; i < host1.connections.size(); i++) {
                Backdoor connection = host1.connections.get(i);
                if (connection.getOther(host1).name.equals(hostId2)) {
                    targetLink = connection;
                    break;
                }
            }
            if (targetLink == null || targetLink.isSealed)
                return "Some error occurred in simulate_breach.";

            int componentsBefore = countComponents(null,null);
            targetLink.isSealed = true;
            int componentsAfter = countComponents(null,null);
            targetLink.isSealed = false;

            if (componentsAfter > componentsBefore) {
                return "Backdoor " + hostId1 + " <-> " + hostId2 + " IS a bridge.\nFailure results in "
                        + componentsAfter
                        + " disconnected components.";
            } else {
                return "Backdoor " + hostId1 + " <-> " + hostId2 + " is NOT a bridge. Network remains the same.";
            }
        }
    }
    /**
     * Counts connected components in the network.
     */
    private int countComponents(Host skipHost, boolean[] cycleDetected) {
        visitCounter++;
        int components = 0;
        ArrayList<Host> allHosts = network.gather();
        if (skipHost != null) {
            skipHost.discoveryId = visitCounter;
        }
        for (Host startHost : allHosts) {
            if (startHost.discoveryId == visitCounter) {
                continue;
            }
            components++;
            Stack stack = new Stack();
            stack.push(new RouteState(startHost, 0, 0, 0, null));

            while (!stack.isEmpty()) {
                RouteState frame = stack.pop();
                Host currentHost = frame.currentHost;
                Host parent = (frame.previousState != null) ? frame.previousState.currentHost : null;

                if (currentHost.discoveryId == visitCounter) {
                    continue;
                }

                currentHost.discoveryId = visitCounter;

                for (int k = 0; k < currentHost.connections.size(); k++) {
                    Backdoor connection = currentHost.connections.get(k);
                    if (connection.isSealed)
                        continue;

                    Host neighbor = connection.getOther(currentHost);

                    if (neighbor == parent)
                        continue;

                    // Cycle detection logic
                    if (neighbor.discoveryId == visitCounter) {
                        if (cycleDetected != null) {
                            cycleDetected[0] = true;
                        }
                    } else {
                        stack.push(new RouteState(neighbor, 0, 0, 0, frame));
                    }
                }
            }
        }
        return components;
    }
    private double networkRound(double value, int decimalPlaces) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Generates a full report of network statistics
     */
    public String generateReport() {
        ArrayList<Host> allHosts = network.gather();
        int totalHosts = allHosts.size();
        int unsealedBackdoors = 0;
        double totalBandwidth = 0;
        double totalSecurity = 0;
        for (Host host : allHosts) {
            totalSecurity += host.securityLevel;
            for (Backdoor connection : host.connections) {
                if (connection.getOther(host).name.compareTo(host.name) > 0) {
                    if (!connection.isSealed) {
                        unsealedBackdoors++;
                        totalBandwidth += connection.bandWidth;
                    }
                }
            }
        }
        boolean[] cycleContainer = new boolean[1];
        int components = countComponents(null, cycleContainer);
        boolean hasCycles = cycleContainer[0];
        String status = (components <= 1 && totalHosts > 0) || (totalHosts <= 1)
                ? "Connected"
                : "Disconnected";

        // Calculate averages
        double avgBandwidth = unsealedBackdoors > 0 ? totalBandwidth / unsealedBackdoors : 0;
        double avgSecurity = totalHosts > 0 ? totalSecurity / totalHosts : 0;

        // Round to 1 decimal place
        avgBandwidth = networkRound(avgBandwidth, 1);
        avgSecurity = networkRound(avgSecurity, 1);

        String cycleStatus = hasCycles ? "Yes" : "No";

        StringBuilder report = new StringBuilder();
        report.append("--- Resistance Network Report ---\n");
        report.append("Total Hosts: ").append(totalHosts).append("\n");
        report.append("Total Unsealed Backdoors: ").append(unsealedBackdoors).append("\n");
        report.append("Network Connectivity: ").append(status).append("\n");
        report.append("Connected Components: ").append(components).append("\n");
        report.append("Contains Cycles: ").append(cycleStatus).append("\n");
        report.append("Average Bandwidth: ").append(avgBandwidth).append("Mbps\n");
        report.append("Average Clearance Level: ").append(avgSecurity);

        return report.toString();
    }
}