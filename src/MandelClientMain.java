import java.rmi.Naming;

public class MandelClientMain {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Args: Server Servername [opt.:] destinationLeft destinationTop destinationZoom animationSteps");
            return;
        }
        try {
            MandelServer server = (MandelServer) Naming.lookup("rmi://" + args[0] + "/" + args[1]);
            MandelClientImpl client = new MandelClientImpl(server);
            client.sendTasks(server);

            if(args.length == 6){
                client.setZoomDestination(server, Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]), Integer.parseInt(args[5]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
