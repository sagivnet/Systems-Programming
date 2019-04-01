package bgu.spl181.net.srv.Bidi_Servers;

import bgu.spl181.net.api.Connections;
import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.BidiServer;
import bgu.spl181.net.srv.Bidi_Connections.Bidi_ConnectionHandlers.Bidi_NonBlockingConnectionHandler;
import bgu.spl181.net.srv.Bidi_Connections.Bidi_Connections;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;


public class Bidi_Reactor<T> implements BidiServer<T>
{
    //------------------------------------------------------------------------------------------------------------------
    //                                                    Bidi Reactor
    //------------------------------------------------------------------------------------------------------------------
    /**                                                      Fields                                                   */
    //------------------------------------------------------------------------------------------------------------------
    private final int port;
    private final Supplier<BidiMessagingProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> readerFactory;
    private final Bidi_ActorThreadPool pool;
    private Selector selector;
    private Thread selectorThread;
    private final ConcurrentLinkedQueue<Runnable> selectorTasks = new ConcurrentLinkedQueue<>();
    private Connections connections;
    private Integer idCounter;
    //------------------------------------------------------------------------------------------------------------------
    /**                                                      Methods                                                  */
    //------------------------------------------------------------------------------------------------------------------
    public Bidi_Reactor(int numThreads,
                       int port,
                       Supplier<BidiMessagingProtocol<T>> protocolFactory,
                       Supplier<MessageEncoderDecoder<T>> readerFactory)
    {
        this.pool = new Bidi_ActorThreadPool(numThreads);
        this.port = port;
        this.protocolFactory = protocolFactory;
        this.readerFactory = readerFactory;
        this.connections = new Bidi_Connections ();
        this.idCounter = 0;
    }
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void serve()
    {
        selectorThread = Thread.currentThread();
        
        try (
                Selector selector = Selector.open();
                ServerSocketChannel serverSock = ServerSocketChannel.open()
        )
        {
            this.selector = selector; //just to be able to close
            
            serverSock.bind(new InetSocketAddress (port));
            serverSock.configureBlocking(false);
            serverSock.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server started");
            
            while (!Thread.currentThread().isInterrupted())
            {
                selector.select();
                runSelectionThreadTasks();
                
                for (SelectionKey key : selector.selectedKeys())
                {
                    if (!key.isValid())
                        continue;
                    
                    else if (key.isAcceptable())
                        handleAccept(serverSock, selector);
                    
                    else
                        handleReadWrite(key);
                }
                
                //  clear the selected keys set so that we can know about new events
                selector.selectedKeys().clear();
            }
            
        }
        catch (ClosedSelectorException ex)
        {/**do nothing - server was requested to be closed*/}
        
        catch (IOException ex)
        {
            //this is an error
            ex.printStackTrace();
        }
        
        System.out.println("server closed!!!");
        pool.shutdown();
    }
    //------------------------------------------------------------------------------------------------------------------
    /*package*/
    public void updateInterestedOps (SocketChannel chan, int ops)
    {
        final SelectionKey key = chan.keyFor(selector);
        
        if (Thread.currentThread() == selectorThread)
            key.interestOps(ops);
        
        else
        {
            selectorTasks.add(() ->  key.interestOps(ops) );
            selector.wakeup();
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    private void handleAccept(ServerSocketChannel serverChan, Selector selector) throws IOException
    {
        idCounter++;
        SocketChannel clientChan = serverChan.accept();
        clientChan.configureBlocking(false);
    
        BidiMessagingProtocol<T> protocol = protocolFactory.get();
        protocol.start (idCounter, connections);
        
        final Bidi_NonBlockingConnectionHandler handler = new Bidi_NonBlockingConnectionHandler
                (
                        readerFactory.get(),
                        protocol,
                        
                        clientChan,
                        this
                );
    
        connections.connect (idCounter, handler);
        clientChan.register(selector, SelectionKey.OP_READ, handler);
    }
    //------------------------------------------------------------------------------------------------------------------
    private void handleReadWrite(SelectionKey key)
    {
        Bidi_NonBlockingConnectionHandler handler = (Bidi_NonBlockingConnectionHandler) key.attachment();
        
        if (key.isReadable())
        {
            Runnable task = handler.continueRead();
            
            if (task != null)
                pool.submit(handler, task);
        }
        
        if (key.isValid() && key.isWritable())
            handler.continueWrite();
    }
    //------------------------------------------------------------------------------------------------------------------
    private void runSelectionThreadTasks()
    {
        while (!selectorTasks.isEmpty())
            selectorTasks.remove().run();
    }
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void close() throws IOException
    { selector.close(); }
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
}
