package com.barteo.cldc.datagram;

import java.io.IOException;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;


public class Connection implements DatagramConnection
{

      
	public void close()
			throws IOException
  {
    throw new IOException();
  }

  
  public int getMaximumLength()
			throws IOException
  {
    throw new IOException();
  }

  
	public int getNominalLength()
      throws IOException
  {
    throw new IOException();
  }
      
  
	public void send(Datagram dgram)
      throws IOException
  {
    throw new IOException();
  }

      
	public void receive(Datagram dgram)
      throws IOException
  {
    throw new IOException();
  }
  
  
	public Datagram newDatagram(int size)
      throws IOException
  {
    throw new IOException();
  }
      

	public Datagram newDatagram(int size, String addr)
      throws IOException
  {
    throw new IOException();
  }
      

	public Datagram newDatagram(byte[] buf, int size)
      throws IOException
  {
    throw new IOException();
  }
      
	
	public Datagram newDatagram(byte[] buf, int size, String addr)
      throws IOException
  {
    throw new IOException();
  }
        
}
