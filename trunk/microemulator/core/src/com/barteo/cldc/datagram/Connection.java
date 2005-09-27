package com.barteo.cldc.datagram;

import java.io.IOException;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;


public class Connection implements DatagramConnection
{
  static final String NOT_IMPLEMENTED = "Not implemented";

      
	public void close()
			throws IOException
  {
    throw new IOException(NOT_IMPLEMENTED);
  }

  
  public int getMaximumLength()
			throws IOException
  {
    throw new IOException(NOT_IMPLEMENTED);
  }

  
	public int getNominalLength()
      throws IOException
  {
    throw new IOException(NOT_IMPLEMENTED);
  }
      
  
	public void send(Datagram dgram)
      throws IOException
  {
    throw new IOException(NOT_IMPLEMENTED);
  }

      
	public void receive(Datagram dgram)
      throws IOException
  {
    throw new IOException(NOT_IMPLEMENTED);
  }
  
  
	public Datagram newDatagram(int size)
      throws IOException
  {
    throw new IOException(NOT_IMPLEMENTED);
  }
      

	public Datagram newDatagram(int size, String addr)
      throws IOException
  {
    throw new IOException(NOT_IMPLEMENTED);
  }
      

	public Datagram newDatagram(byte[] buf, int size)
      throws IOException
  {
    throw new IOException(NOT_IMPLEMENTED);
  }
      
	
	public Datagram newDatagram(byte[] buf, int size, String addr)
      throws IOException
  {
    throw new IOException(NOT_IMPLEMENTED);
  }
        
}
