import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.stream.IntStream;

public class MandelClientMain {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Args: Clientname Server Servername");
            return;
        }
        try {
            MandelServer server = (MandelServer) Naming.lookup("rmi://" + args[1] + "/" + args[2]);
            MandelClientImpl client = new MandelClientImpl(args[0]);
            int iter = 0;
            if (server.addClient(client)) {
                server.setDetail(MandelClientImpl.WIDTH, MandelClientImpl.HEIGHT, MandelClientImpl.DETAIL);
                while (true) {
                    try {
                        server.setImageProperties(MandelClientImpl.top, MandelClientImpl.left, MandelClientImpl.zoom);
                        MandelClientImpl.mouseClick = false;
                        IntStream.iterate(0, i -> i + 10).limit(MandelClientImpl.HEIGHT / 10).parallel()
                                .forEach((i) -> {
                                    try {
                                        int j = i + 10;
                                        server.calculateRGB(i, j, 0, MandelClientImpl.WIDTH);
                                    } catch (RemoteException re) {
                                        re.printStackTrace();
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    while (server.isFinish())
                        ;
                    System.out.printf("Iteration: %d\r", iter);
                    client.setRGB(server.returnColor());
                    iter++;
                    while (!MandelClientImpl.mouseClick)
                        ;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
