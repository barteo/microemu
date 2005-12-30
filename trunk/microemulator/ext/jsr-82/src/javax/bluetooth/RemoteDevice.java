package javax.bluetooth;

import java.io.IOException;

import javax.microedition.io.Connection;

public class RemoteDevice
{
    
    protected RemoteDevice(String address)
    {        
        throw new RuntimeException("Not implemented");
    }
    
    
    public boolean isTrustedDevice()
    {        
        throw new RuntimeException("Not implemented");
    }
    
    
    public String getFriendlyName(boolean alwaysAsk)
            throws IOException
    {        
        throw new RuntimeException("Not implemented");
    }
    
    
    public final String getBluetoothAddress()
    {
        throw new RuntimeException("Not implemented");
    }
    
    
    public boolean equals(Object obj)
    {
        throw new RuntimeException("Not implemented");
    }
    
    
    public int hashCode()
    {
        throw new RuntimeException("Not implemented");
    }
    
    
    public static RemoteDevice getRemoteDevice(Connection conn)
            throws IOException
    {
        throw new RuntimeException("Not implemented");
    }
    
    
    public boolean authenticate()
            throws IOException
    {
        throw new RuntimeException("Not implemented");
    }
    

    public boolean authorize(Connection conn)
            throws IOException
    {
        throw new RuntimeException("Not implemented");
    }
    
    
    public boolean encrypt(Connection conn, boolean on)
            throws IOException
    {
        throw new RuntimeException("Not implemented");
    }

    
    public boolean isAuthenticated()
    {
        throw new RuntimeException("Not implemented");
    }

    
    public boolean isAuthorized(Connection conn)
            throws IOException
    {
        throw new RuntimeException("Not implemented");
    }

    
    public boolean isEncrypted()
    {
        throw new RuntimeException("Not implemented");
    }

}
