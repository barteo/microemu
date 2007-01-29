/*
 *  MicroEmulator
 *  Copyright (C) 2006 Bartek Teodorczyk <barteo@barteo.net>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

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
