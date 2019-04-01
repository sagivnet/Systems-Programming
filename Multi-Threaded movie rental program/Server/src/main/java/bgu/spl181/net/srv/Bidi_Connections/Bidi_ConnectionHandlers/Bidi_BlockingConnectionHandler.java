package bgu.spl181.net.srv.Bidi_Connections.Bidi_ConnectionHandlers;

import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.bidi.BidiConnectionHandler;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Bidi_BlockingConnectionHandler<T> implements Runnable, BidiConnectionHandler<T>
{
    //------------------------------------------------------------------------------------------------------------------
    //                                         Bidi  Blocking Connection Handler
    //------------------------------------------------------------------------------------------------------------------
    /**                                                      Fields                                                   */
    //------------------------------------------------------------------------------------------------------------------
    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;
    //------------------------------------------------------------------------------------------------------------------
    /**                                                      Methods                                                  */
    //------------------------------------------------------------------------------------------------------------------
    public Bidi_BlockingConnectionHandler(Socket sock,
                                         MessageEncoderDecoder<T> reader,
                                         BidiMessagingProtocol<T> protocol)
    {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
    }
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void run()
    {
        //just for automatic closing
        try (Socket sock = this.sock)
        {
            int read;
            
            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());
            
            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0)
            {
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null)
                    protocol.process(nextMessage);
            }
        }
        catch (IOException ex)
        { ex.printStackTrace(); }
    }
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void close() //throws IOException
    {
        connected = false;
        try{sock.close();}
        catch (IOException ex)
        { ex.printStackTrace(); }
    }
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void send (T msg)
    {
        byte[] packet = encdec.encode (msg);
        try
        {
            out.write (packet);
            out.flush ();
        }
        catch (IOException e)
        {
            e.printStackTrace ();
            close ();
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean isConnected ()   { return connected = sock.isConnected (); }
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
}

