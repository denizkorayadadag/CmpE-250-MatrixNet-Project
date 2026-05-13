import java.util.ArrayList;

/**
 * Host class representing a node in the network
 */
public class Host {
    public String name;
    public int securityLevel;
    public ArrayList<Backdoor> connections;
    public int discoveryId;
    public int minHops;
    public double minCost;

    /**
     * Constructor for Host
     */
    public Host(String name, int securityLevel) {
        this.name = name;
        this.securityLevel = securityLevel;
        this.connections = new ArrayList<>();
        this.discoveryId = 0;
        this.minHops = Integer.MAX_VALUE;
    }

}
