package javax.bluetooth;

public class DiscoveryAgent
{
    public static final int NOT_DISCOVERABLE = 0x00;
    
    public static final int GIAC = 0x9E8B33;
    public static final int LIAC = 0x9E8B00;
    
    public static final int CACHED = 0x00;
    public static final int PREKNOWN = 0x01;

    
    public RemoteDevice[] retrieveDevices(int option)
    {
        throw new RuntimeException("Not implemented");
    }
    
    
    public boolean startInquiry(int accessCode, DiscoveryListener listener)
            throws BluetoothStateException
    {
        throw new RuntimeException("Not implemented");
    }
    
    
    public boolean cancelInquiry(DiscoveryListener listener)
    {
        throw new RuntimeException("Not implemented");
    }
    
    
    public int searchServices(int[] attrSet, UUID[] uuidSet, RemoteDevice btDev, DiscoveryListener listener)
            throws BluetoothStateException
    {
        throw new RuntimeException("Not implemented");
    }
    
    
    public boolean cancelServiceSearch(int transID)
    {
        throw new RuntimeException("Not implemented");
    }
    
    
    public java.lang.String selectService(UUID uuid, int security, boolean master)
            throws BluetoothStateException
    {
        throw new RuntimeException("Not implemented");
    }
    
}
