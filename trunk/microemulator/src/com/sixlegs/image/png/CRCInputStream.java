// Copyright (C) 1998, 1999 Chris Nokleberg
// Please see included LICENSE.TXT

package com.sixlegs.image.png;
import java.io.*;
import java.util.zip.*;

final class CRCInputStream extends FilterInputStream {
  private CRC32 crc = new CRC32();
  private int byteCount = 0;

  public CRCInputStream (InputStream in) {
    super(in);
  }

  public long getValue () {
    return (long)crc.getValue();
  }

  public void reset () {
    byteCount = 0;
    crc.reset();
  }

  public int count () {
    return byteCount;
  }

  public int read () throws IOException {
    int x = in.read();
    crc.update(x);
    byteCount++;
    return x;
  }

  public int read (byte[] b, int off, int len) throws IOException {
    int x = in.read(b, off, len);
    crc.update(b, off, x);
    byteCount += x;
    return x;
  }

  private byte[] byteArray = new byte[0];
  public long skip (long n) throws IOException {
    // what if n > Integer.MAX_VALUE ?
    if (byteArray.length < n) byteArray = new byte[(int)n];
    return read(byteArray, 0, (int)n);
  }
}
  
