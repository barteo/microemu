// Copyright (C) 1998, 1999 Chris Nokleberg
// Please see included LICENSE.TXT

package com.sixlegs.image.png;
import java.awt.image.*;
import java.io.IOException;

final class Chunk_PLTE extends Chunk {
  private int size;

  /* package */ int[] r_raw;
  /* package */ int[] g_raw;
  /* package */ int[] b_raw;

  /* package */ byte[] r;
  /* package */ byte[] g;
  /* package */ byte[] b;
  /* package */ byte[] a = null;

  Chunk_PLTE () { super(PLTE); }

  protected boolean multipleOK () { return false; }
  protected boolean beforeIDAT () { return true; }

  protected void readData () throws IOException {
    img.palette = this;
    if (img.getChunk(bKGD) != null) {
      throw new PngException("bKGD chunk must follow PLTE chunk");
    }
    if (!img.header.colorUsed) {
      throw new PngExceptionSoft("PLTE chunk found in grayscale image");
    }
    if (length % 3 != 0) {
      throw new PngException("PLTE chunk length indivisible by 3");
    }
    size = length / 3;

    /// look into this
    if (img.header.colorType == PngImage.COLOR_TYPE_PALETTE) {
      if (size > (2 << img.header.depth)) {
	throw new PngException("Too many palette entries");
      } else if (size > 256) {
	throw new PngExceptionSoft("Too many palette entries");
      }
    }

    r = new byte[size];
    g = new byte[size];
    b = new byte[size];

    int[][] raw  = new int[3][size];
    r_raw = raw[0];
    g_raw = raw[1];
    b_raw = raw[2];

    for (int i = 0; i < size; i++) {
      r_raw[i] = in_data.readUnsignedByte();
      g_raw[i] = in_data.readUnsignedByte();
      b_raw[i] = in_data.readUnsignedByte();
    }

    int[][] prop = new int[3][size];
    System.arraycopy(r_raw, 0, prop[0], 0, size);
    System.arraycopy(g_raw, 0, prop[1], 0, size);
    System.arraycopy(b_raw, 0, prop[2], 0, size);
    img.properties.put("palette", prop);
    img.properties.put("palette size", new Integer(size));
  }

  void calculate () {
    for (int i = 0; i < size; i++) {
      r[i] = (byte)img.gammaTable[r_raw[i]];
      g[i] = (byte)img.gammaTable[g_raw[i]];
      b[i] = (byte)img.gammaTable[b_raw[i]];
    }

    if (img.header.paletteUsed) {
      if (a != null) {
	img.header.model = 
	  new IndexColorModel(img.header.cmBits, size, r, g, b, a);
      } else {
	img.header.model = 
	  new IndexColorModel(img.header.cmBits, size, r, g, b);
      }
    }
  }

}
