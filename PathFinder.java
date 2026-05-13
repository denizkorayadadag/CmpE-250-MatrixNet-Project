
public class PathFinder {

    /**
     * Finds the optimal path between two hosts using Dijkstra's algorithm
     */
    public static RouteState findBestPath(Host startHost, Host endHost, int minBandwidth,
                                             int latencyMultiplier, int visitToken, MinHeap minHeap) {
        if (startHost == endHost) {
            return new RouteState(startHost, 0.0, 0.0, 0, null);
        }
        minHeap.resetHeap();
        RouteState initialState = new RouteState(startHost, 0.0, 0.0, 0, null);
        minHeap.insert(initialState);
        while (!minHeap.isEmpty()) {
            RouteState currentState = minHeap.extractMin();
            Host currentHost = currentState.currentHost;
            if (currentHost == endHost) {
                return currentState;
            }
            if (!shouldExpand(currentHost, currentState, visitToken)) {
                continue;
            }
            updateHostState(currentHost, currentState, visitToken);
            expandNeighbors(currentState, minHeap, minBandwidth, latencyMultiplier, visitToken);
        }

        return null;
    }

    /**
     * Determines if a state should be expanded
     */
    private static boolean shouldExpand(Host host, RouteState state, int visitToken) {
        if (host.discoveryId != visitToken) {
            return true;
        }
        return state.hopCount < host.minHops;
    }

    /**
     * Updates the host's best known state for this search
     */
    private static void updateHostState(Host host, RouteState state, int visitToken) {
        host.discoveryId = visitToken;
        host.minHops = state.hopCount;
        host.minCost = state.baseCost;
    }

    /**
     * Expands to all valid neighboring states
     */
    private static void expandNeighbors(RouteState currentState, MinHeap minHeap, int minBandwidth,
                                        int latencyMultiplier, int visitToken) {
        Host currentHost = currentState.currentHost;
        int nextSteps = currentState.hopCount + 1;
        double currentBaseCost = currentState.baseCost;
        double currentTotalCost = currentState.totalCost;
        for (int i = 0; i < currentHost.connections.size(); i++) {
            Backdoor connection = currentHost.connections.get(i);
            if (connection.isSealed  ||connection.bandWidth < minBandwidth ||currentHost.securityLevel < connection.firewallLevel)
                continue;
            Host neighbor = connection.getOther(currentHost);
            if (neighbor.discoveryId == visitToken && nextSteps >= neighbor.minHops) {
                continue;
            }

            if (latencyMultiplier == 0 && neighbor.discoveryId == visitToken) {
                double newBaseCost = currentBaseCost + connection.latency;
                if (newBaseCost > neighbor.minCost + Main.error) {
                    continue;
                }
            }
            double penalty = (double) latencyMultiplier * currentState.hopCount;
            double newBaseCost = currentBaseCost + connection.latency;
            double newTotalCost = currentTotalCost + connection.latency + penalty;
            RouteState newState = new RouteState(neighbor, newTotalCost, newBaseCost, nextSteps, currentState);
            minHeap.insert(newState);
        }
    }

}