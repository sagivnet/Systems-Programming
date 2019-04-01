package bgu.spl181.net.srv.Bidi_Servers;

import bgu.spl181.net.api.Connections;
import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.BidiServer;
import bgu.spl181.net.srv.Bidi_Connections.Bidi_ConnectionHandlers.Bidi_BlockingConnectionHandler;
import bgu.spl181.net.srv.Bidi_Connections.Bidi_Connections;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

public abstract class Bidi_BaseServer<T> implements BidiServer<T>
{
    //------------------------------------------------------------------------------------------------------------------
    //                                                  Bidi Base Server
    //------------------------------------------------------------------------------------------------------------------
    /**                                                      Fields                                                   */
    //------------------------------------------------------------------------------------------------------------------
    private final int port;
    private final Supplier<BidiMessagingProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
    private ServerSocket sock;
    private Connections connections;
    private Integer idCounter;
    //------------------------------------------------------------------------------------------------------------------
    /**                                                      Methods                                                  */
    //------------------------------------------------------------------------------------------------------------------
    public Bidi_BaseServer(int port,
                          Supplier<BidiMessagingProtocol<T>> protocolFactory,
                          Supplier<MessageEncoderDecoder<T>> encdecFactory)
    {
        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
        this.sock = null;
        connections = new Bidi_Connections ();
        idCounter = 0;
    }
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void serve()
    {
        try (ServerSocket serverSock = new ServerSocket(port))
        {
            System.out.println("Server started");
            this.sock = serverSock; //just to be able to close
            
            while (!Thread.currentThread().isInterrupted())
            {
                Socket clientSock = serverSock.accept();
                idCounter++;
                
                
                BidiMessagingProtocol<T> protocol = protocolFactory.get();
                protocol.start (idCounter, connections);
                
                Bidi_BlockingConnectionHandler<T> handler = new Bidi_BlockingConnectionHandler<> (clientSock, encdecFactory.get(), protocol);
                
                //  sign new client at connections
                connections.connect (idCounter, handler);
                
                //  run connection handler task
                execute(handler);
            }
        }
        catch (IOException ex) { System.out.println ("Error: Listening on port: "+port+ " has failed!");}
        
        System.out.println("server closed!!!");
    }
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void close() throws IOException
    {
        if (sock != null)
            sock.close();
    }
    //------------------------------------------------------------------------------------------------------------------
    protected abstract void execute(Bidi_BlockingConnectionHandler<T> handler);
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
}
