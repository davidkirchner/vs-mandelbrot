import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MandelServerMain {
    public static void main(String[] args) {
        try {
            MandelServerImpl server = new MandelServerImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind(args[0], server);
            System.out.println("Server is ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
