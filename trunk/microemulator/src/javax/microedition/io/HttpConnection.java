/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@it.pl>
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
 
package javax.microedition.io;

import java.io.IOException;


public interface HttpConnection extends Connection
{

	public static final String HEAD = "HEAD";
	public static final String GET = "GET";
	public static final String POST = "POST";
	
	public static final int HTTP_OK = 200;
	public static final int HTTP_CREATED = 201;
	public static final int HTTP_ACCEPTED = 202;
	public static final int HTTP_NOT_AUTHORITATIVE = 203;
	public static final int HTTP_NO_CONTENT = 204;
	public static final int HTTP_RESET = 205;
	public static final int HTTP_PARTIAL = 206;
	
	public static final int HTTP_MULT_CHOICE = 300;
	public static final int HTTP_MOVED_PERM = 301;
	public static final int HTTP_MOVED_TEMP = 302;
	public static final int HTTP_SEE_OTHER = 303;
	public static final int HTTP_NOT_MODIFIED = 304;
	public static final int HTTP_USE_PROXY = 305;
	public static final int HTTP_TEMP_REDIRECT = 307;
	
	public static final int HTTP_BAD_REQUEST = 400;
	public static final int HTTP_UNAUTHORIZED = 401;
	public static final int HTTP_PAYMENT_REQUIRED = 402;
	public static final int HTTP_FORBIDDEN = 403;
	public static final int HTTP_NOT_FOUND = 404;
	public static final int HTTP_BAD_METHOD = 405;
	public static final int HTTP_NOT_ACCEPTABLE = 406;
	public static final int HTTP_PROXY_AUTH = 407;
	public static final int HTTP_CLIENT_TIMEOUT = 408;
	public static final int HTTP_CONFLICT = 409;
	public static final int HTTP_GONE = 410;
	public static final int HTTP_LENGTH_REQUIRED = 411;
	public static final int HTTP_PRECON_FAILED = 412;
	public static final int HTTP_ENTITY_TOO_LARGE = 413;
	public static final int HTTP_REQ_TOO_LONG = 414;
	public static final int HTTP_UNSUPPORTED_TYPE = 415;
	public static final int HTTP_UNSUPPORTED_RANGE = 416;
	public static final int HTTP_EXPECT_FAILED = 417;
	
	public static final int HTTP_INTERNAL_ERROR = 500;
	public static final int HTTP_NOT_IMPLEMENTED = 501;
	public static final int HTTP_BAD_GATEWAY = 502;
	public static final int HTTP_UNAVAILABLE = 503;
	public static final int HTTP_GATEWAY_TIMEOUT = 504;
	public static final int HTTP_VERSION = 505;
	
	
	public String getURL();
	
	public String getProtocol();
	
	public String getHost();
	
	public String getFile();
	
	public String getRef();
	
	public String getQuery();
	
	public int getPort();
	
	public String getRequestMethod();
	
	public void setRequestMethod(String method)
      throws IOException;
      
	public String getRequestProperty(String key);
	
	public void setRequestProperty(String key, String value)
      throws IOException;
  
  public int getResponseCode()
      throws IOException;
      
	public String getResponseMessage()
      throws IOException;
      
	public long getExpiration()
      throws IOException;
      
	public long getDate()
      throws IOException;
      
  public long getLastModified()
      throws IOException;
      
  public String getHeaderField(String name)
      throws IOException;
      
  public int getHeaderFieldInt(String name, int def)
      throws IOException;
      
  public long getHeaderFieldDate(String name, long def)
      throws IOException;
	
	public String getHeaderField(int n)
      throws IOException;
      
  public String getHeaderFieldKey(int n)
      throws IOException;
      	
}
