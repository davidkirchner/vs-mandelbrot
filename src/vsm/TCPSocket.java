package vsm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPSocket implements AutoCloseable {
    private Socket socket;
    private DataOutputStream doStream;
    private DataInputStream diStream;

    public TCPSocket(String serverAddress, int serverPort) throws UnknownHostException, IOException {
        socket = new Socket(serverAddress, serverPort);
        initializeStreams();
    }

    public TCPSocket(Socket socket) throws IOException {
        this.socket = socket;
        initializeStreams();
    }

    public void writeInts(OutputStream out, int[] ints) throws IOException {
        doStream.writeInt(ints.length);
        for (int e : ints)
            doStream.writeInt(e);
        doStream.flush();
    }

    public int[] readInts(InputStream in) throws IOException {
        int[] ints = new int[diStream.readInt()];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = diStream.readInt();
        }
        return ints;
    }

    public void close() throws IOException {
        socket.close();
    }

    private void initializeStreams() throws IOException {
        doStream = new DataOutputStream(socket.getOutputStream());
        diStream = new DataInputStream(socket.getInputStream());
    }
}

// Source: https://stackoverflow.com/a/35538456
