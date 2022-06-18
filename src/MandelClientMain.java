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
            long startTime, endTime;
            if (server.addClient(client)) {
                // Client sends image's width, height and detail to server. This
                // is necessary to set the size of int[][] bild, which contains
                // the color information of the mandelbrot image and for the
                // calculation of RGB Values.
                server.setDetail(MandelClientImpl.WIDTH, MandelClientImpl.HEIGHT, MandelClientImpl.DETAIL);
                while (true) {
                    startTime = System.currentTimeMillis();
                    try {
                        // When client clicks on a point, mouselistener() will
                        // calculate the value of top, left and zoom therefore
                        // client needs to send these new values to server.
                        server.setImageProperties(MandelClientImpl.top, MandelClientImpl.left, MandelClientImpl.zoom);
                        MandelClientImpl.mouseClick = false;
                        // A for-loop but using IntStream (Java 8)
                        // for(int i = 0; i < MandelClientImpl.HEIGHT ; i += 10){}
                        // Client divides the entire Mandelbrot image into multiple
                        // smaller images (yStart, yStop, xStart, xStop).
                        // Client sends the smaller image as a task to server.
                        // Server uses ThreadPool with a fixed number of threads,
                        // When all threads are busy, new task will be queued.
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
                    // Because number of tasks is larger than number of threads
                    // a mechanism is needed to block client from updating the Mandelbrot
                    // image.
                    while (server.isFinish())
                        ;
                    endTime = System.currentTimeMillis();
                    System.out.printf("Iteration: %d. Finish calculating in (ms): %d\r", iter, endTime - startTime);
                    client.setRGB(server.returnColor());
                    iter++;
                    // Unless user clicks a point with a mouse, the same method
                    // can be used to block client sending unecessary tasks to server.
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
