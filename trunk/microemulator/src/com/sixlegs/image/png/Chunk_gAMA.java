// Copyright (C) 1998, 1999 Chris Nokleberg
// Please see included LICENSE.TXT

package com.sixlegs.image.png;
import java.io.IOException;

final class Chunk_gAMA extends Chunk {
  Chunk_gAMA () { super(gAMA); }

  protected boolean multipleOK () { return false; }
  protected boolean beforeIDAT () { return true; }

  protected void readData () throws IOException {
    if (img.palette != null) 
      throw new PngException("gAMA chunk must precede PLTE chunk");
    if (length != 4) badLength(4);
    long gamma = in_data.readUnsignedInt();

    if (img.getChunk(sRGB) == null)
      img.properties.put("gamma", new Long(gamma));
  }

}
