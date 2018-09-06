/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.exceptiones;

/**
 *
 * @author oswaldo
 */
public class NoExisteWSSunatException extends Exception {

    /**
     * Creates a new instance of <code>noWSSunat</code> without detail message.
     */
    String sws;
    
    public NoExisteWSSunatException() {
    }

    /**
     * Constructs an instance of <code>noWSSunat</code> with the specified
     * detail message.
     *
     * @param sws the detail message.
     */
    public NoExisteWSSunatException(String sws) {
        
        this.sws = sws;
    }

    public String getSws() {
        return sws;
    }
    
}
