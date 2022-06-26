import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class MandelServerMain {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Args: servername <IP slave1> <IP slave2> ...");
            return;
        }
        ArrayList<String> slaveIPs = new ArrayList<String>();
        for(int i=1; i<args.length; i++){
            slaveIPs.add(args[i]);
        }

        try {
            MandelServerImpl server = new MandelServerImpl(slaveIPs);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind(args[0], server);
            System.out.println("Server is ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
