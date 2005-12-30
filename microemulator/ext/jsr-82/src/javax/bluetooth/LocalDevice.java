package javax.bluetooth;

import javax.microedition.io.Connection;

public class LocalDevice
{

    public static LocalDevice getLocalDevice()
            throws BluetoothStateException
    {
        throw new RuntimeException("Not implemented");
    }
    
    
    public DiscoveryAgent getDiscoveryAgent()
    {
        throw new RuntimeException("Not implemented");
    }
    
    
    public String getFriendlyName()
    {
        throw new RuntimeException("Not implemented");
    }
    
    
    public DeviceClass getDeviceClass()
    {
        throw new RuntimeException("Not implemented");
    }
    
    
    public boolean setDiscoverable(int mode)
            throws BluetoothStateException
    {
        throw new RuntimeException("Not implemented");
    }
    
    
    public static String getProperty(String property)
    {
        throw new RuntimeException("Not implemented");
    }
    
    
    public int getDiscoverable()
    {
        throw new RuntimeException("Not implemented");
    }
    
    
    public String getBluetoothAddress()
    {
        throw new RuntimeException("Not implemented");
    }
    
    
    public ServiceRecord getRecord(Connection notifier)
    {
        throw new RuntimeException("Not implemented");
    }


    public void updateRecord(ServiceRecord srvRecord)
            throws ServiceRegistrationException
    {
        throw new RuntimeException("Not implemented");
    }
    
}
