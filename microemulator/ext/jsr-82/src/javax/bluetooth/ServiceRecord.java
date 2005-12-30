package javax.bluetooth;

import java.io.IOException;

public interface ServiceRecord
{
    
    public static final int NOAUTHENTICATE_NOENCRYPT = 0x00;
    public static final int AUTHENTICATE_NOENCRYPT = 0x01;
    public static final int AUTHENTICATE_ENCRYPT = 0x02;
    
    
    public DataElement getAttributeValue(int attrID);

    public RemoteDevice getHostDevice();
    
    public int[] getAttributeIDs();
    
    public boolean populateRecord(int[] attrIDs) throws IOException;
    
    public String getConnectionURL(int requiredSecurity, boolean mustBeMaster);
    
    public void setDeviceServiceClasses(int classes);
    
    public boolean setAttributeValue(int attrID, DataElement attrValue);
    
}
