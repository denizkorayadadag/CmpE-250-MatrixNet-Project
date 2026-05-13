/**
 * Backdoor class representing an edge between two hosts
 */
public class Backdoor {
    public Host hostOne;
    public Host hostTwo;
    public int latency;
    public boolean isSealed;
    public int bandWidth;
    public int firewallLevel;

    /**
     * Constructor
     */
    public Backdoor(Host hostOne, Host hostTwo, int latency, int bandWidth, int firewallLevel) {
        this.hostOne = hostOne;
        this.hostTwo = hostTwo;
        this.latency = latency;
        this.bandWidth = bandWidth;
        this.firewallLevel = firewallLevel;
        this.isSealed = false;
    }

    /**
     * Returns the other host in the connection
     */
    public Host getOther(Host me) {
        if (me == hostOne)
            return hostTwo;
        return hostOne;
    }
}