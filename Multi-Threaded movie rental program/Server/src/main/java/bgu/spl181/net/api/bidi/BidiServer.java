package bgu.spl181.net.api.bidi;

import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.srv.Bidi_Connections.Bidi_ConnectionHandlers.Bidi_BlockingConnectionHandler;
import bgu.spl181.net.srv.Bidi_Servers.Bidi_BaseServer;
import bgu.spl181.net.srv.Bidi_Servers.Bidi_Reactor;


import java.io.Closeable;
import java.util.function.Supplier;

public interface BidiServer <T> extends Closeable
{
//----------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------
    /**
     * The main loop of the server, Starts listening and handling new clients.
     */
    void serve ();
//----------------------------------------------------------------------------------------------------------------------
    /**
     *This function returns a new instance of a thread per client pattern server
     * @param port The port for the server socket
     * @param protocolFactory A factory that creats new MessagingProtocols
     * @param encoderDecoderFactory A factory that creats new MessageEncoderDecoder
     * @param <T> The Message Object for the protocol
     * @return A new Thread per client server
     */
    public static <T> BidiServer<T> threadPerClient (
            int port,
            Supplier<BidiMessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encoderDecoderFactory)
    {

        return new Bidi_BaseServer<T> (port, protocolFactory, encoderDecoderFactory)
        {
            @Override
            protected void execute (Bidi_BlockingConnectionHandler<T> handler)
                            { new Thread(handler).start(); }
        };
    }
//----------------------------------------------------------------------------------------------------------------------
    /**
     * This function returns a new instance of a reactor pattern server
     * @param nthreads Number of threads available for protocol processing
     * @param port The port for the server socket
     * @param protocolFactory A factory that creats new MessagingProtocols
     * @param encoderDecoderFactory A factory that creats new MessageEncoderDecoder
     * @param <T> The Message Object for the protocol
     * @return A new reactor server
     */
    public static <T> BidiServer<T> reactor (
            int nthreads,
            int port,
            Supplier<BidiMessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encoderDecoderFactory)
    
    { return new Bidi_Reactor<> (nthreads, port, protocolFactory, encoderDecoderFactory); }
//----------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------
}
