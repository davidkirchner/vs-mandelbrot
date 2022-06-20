import java.rmi.Naming;

public class MandelClientMain {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Args: Clientname Server Servername");
            return;
        }
        try {
            MandelServer server = (MandelServer) Naming.lookup("rmi://" + args[0] + "/" + args[1]);
            MandelClientImpl client = new MandelClientImpl(server);
            client.sendTasks(server);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
