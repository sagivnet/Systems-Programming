/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl181.net.api.bidi;

import java.io.Closeable;

/**
 *
 * @author bennyl
 */
public interface BidiConnectionHandler <T> extends Closeable{
    
    void send (T msg) ;
    boolean isConnected();
}