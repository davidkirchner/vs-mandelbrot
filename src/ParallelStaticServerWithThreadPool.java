//Docs: https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ThreadPoolExecutor.html
import java.net.ServerSocket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class Counter {
    private int counter;

    public synchronized int increment() {
        counter++;
        return counter;
    }

    public synchronized int reset() {
        counter = 0;
        return counter;
    }

    public synchronized int getCounter() {
        return counter;
    }
}

class Task implements Runnable {
    private TCPSocket socket;
    private Counter counter;

    public Task(TCPSocket socket, Counter counter) {
        this.socket = socket;
        this.counter = counter;
    }

    public void run() {
        try (TCPSocket s = socket) {
            while (true) {
                String request = socket.receiveLine();
                int result;
                if (request != null) {
                    if (request.equals("increment")) {
                        result = counter.increment();
                    } else if (request.equals("reset")) {
                        result = counter.reset();
                    } else {
                        result = counter.getCounter();
                    }
                    socket.sendLine("" + result);
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

public class ParallelStaticServerWithThreadPool {
    public static void main(String[] args) {
        Counter counter = new Counter();
        ThreadPoolExecutor pool = new ThreadPoolExecutor(3, 3, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        try (ServerSocket serverSocket = new ServerSocket(1250)) {
            while (true) {
                try {
                    TCPSocket tcpSocket = new TCPSocket(serverSocket.accept());
                    Task task = new Task(tcpSocket, counter);
                    pool.execute(task);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
