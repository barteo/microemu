package javax.bluetooth;

public interface DiscoveryListener
{
    public static final int INQUIRY_COMPLETED = 0x00;
    public static final int INQUIRY_TERMINATED = 0x05;
    public static final int INQUIRY_ERROR = 0x07;
    
    public static final int SERVICE_SEARCH_COMPLETED = 0x01;
    public static final int SERVICE_SEARCH_TERMINATED = 0x02;
    public static final int SERVICE_SEARCH_ERROR = 0x03;
    public static final int SERVICE_SEARCH_NO_RECORDS = 0x04;
    public static final int SERVICE_SEARCH_DEVICE_NOT_REACHABLE = 0x06;
    
    
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod);
    
    public void servicesDiscovered(int transID, ServiceRecord[] serviceRecord);
    
    public void serviceSearchCompleted(int transID, int respCode);
    
    public void inquiryCompleted(int discType);    
    
}
