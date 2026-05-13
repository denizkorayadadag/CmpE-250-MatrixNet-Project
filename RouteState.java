/**
 * Represents a state in the route finding algorithm
 * Stores the current location, costs, and the path taken so far
 */
public class RouteState {
    public Host currentHost;
    public double totalCost;
    public double baseCost;
    public int hopCount;
    public RouteState previousState;

    public RouteState(Host host, double total, double base, int hops, RouteState prev) {
        this.currentHost = host;
        this.totalCost = total;
        this.baseCost = base;
        this.hopCount = hops;
        this.previousState = prev;
    }

    /**
     * Reconstructs the full path string from start to this state
     */
    public String getPath() {
        StringBuilder sb = new StringBuilder();
        buildPath(this, sb);
        return sb.toString();
    }

    private void buildPath(RouteState state, StringBuilder sb) {
        if (state.previousState != null) {
            buildPath(state.previousState, sb);
            sb.append(" -> ");
        }
        sb.append(state.currentHost.name);
    }
}
