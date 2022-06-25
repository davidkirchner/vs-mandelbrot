import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.*;

public class Slave extends MandelServerImpl {
    public Slave() throws RemoteException {
        super();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Args: Server Servername");
            return;
        }
        try {
            Slave slave = new Slave();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind(args[0], slave);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
