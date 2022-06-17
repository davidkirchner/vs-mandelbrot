import java.rmi.*;

public class MandelClientMain {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Args: Clientname Server");
            return;
        }
        try {
            MandelServer server = (MandelServer) Naming.lookup("rmi://" + args[1] + "/Mandelbrot");
            MandelClientImpl client = new MandelClientImpl(args[0]);
            if (server.addClient(client)) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
