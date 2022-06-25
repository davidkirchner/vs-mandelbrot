import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class MandelClientMain {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Server agrs: IP/Hostname Servername");
            return;
        }
        try {
            MandelServer server = (MandelServer) Naming.lookup("rmi://" + args[0] + "/" + args[1]);
            MandelClientImpl client = new MandelClientImpl(server);
            client.sendTasks(server);
        } catch (RemoteException re) {
            System.out.println("Registry " + args[0] + " could not be found.");
            re.printStackTrace();
        } catch (NotBoundException nbe) {
            System.out.println("Name " + args[1] + " is not currently bound");
            nbe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
