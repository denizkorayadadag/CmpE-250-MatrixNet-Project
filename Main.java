import java.io.*;
import java.util.Locale;

/**
 * Main class.
 * Initializes the Network system and handles Input Reading and Output writing.
 */
public class Main {
    static Network system = new Network(22483);
    public static final double error = 1e-9;

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        if (args.length != 2) {
            System.err.println("Usage: java Main <input_file> <output_file>");
            System.exit(1);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(args[0]));
                BufferedWriter writer = new BufferedWriter(new FileWriter(args[1]))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty())
                    processCommand(line, writer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processCommand(String command, BufferedWriter writer) throws IOException {
        String[] parts = command.split("\\s+");
        String operation = parts[0];
        String result = "";

        try {
            switch (operation) {
                case "spawn_host":
                    result = system.spawnHost(parts[1], Integer.parseInt(parts[2]));
                    break;
                case "link_backdoor":
                    result = system.linkBackdoor(parts[1], parts[2], Integer.parseInt(parts[3]),
                            Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
                    break;
                case "seal_backdoor":
                    result = system.sealBackdoor(parts[1], parts[2]);
                    break;
                case "trace_route":
                    result = system.traceRoute(parts[1], parts[2], Integer.parseInt(parts[3]),
                            Integer.parseInt(parts[4]));
                    break;
                case "scan_connectivity":
                    result = system.scanConnectivity();
                    break;
                case "simulate_breach":
                    if (parts.length == 2) {
                        result = system.simulateBreach(parts[1], null);
                    } else {
                        result = system.simulateBreach(parts[1], parts[2]);
                    }
                    break;
                case "oracle_report":
                    result = system.generateReport();
                    break;
                default:
                    break;
            }
            if (!result.isEmpty()) {
                writer.write(result);
                writer.newLine();
            }
        } catch (Exception e) {

        }
    }
}