// Copyright (C) 1998, 1999 Chris Nokleberg
// Please see included LICENSE.TXT

package com.sixlegs.image.png;
import java.io.*;

abstract class BitMover {
  int trans = -1;
  int transgray = -1;
  int translow = -1;
  int[] gammaTable;

  abstract int fill (int[] pixels, InputStream str, int off, int len) throws IOException;

  static BitMover getBitMover (PngImage img) throws PngException {
    StringBuffer clsname = new StringBuffer("com.sixlegs.image.png.BitMover");
    clsname.append(img.header.depth);
    if (img.header.paletteUsed) {
      clsname.append('P');
    } else {
      clsname.append(img.header.colorUsed ? "RGB" : "G");
    }
    if (img.header.alphaUsed) clsname.append('A');
    try {
      BitMover b = (BitMover)Class.forName(clsname.toString()).newInstance();
      b.gammaTable = img.gammaTable;
      if (img.header.colorType == PngImage.COLOR_TYPE_GRAY || 
	  img.header.colorType == PngImage.COLOR_TYPE_RGB) {
	Chunk_tRNS trans = (Chunk_tRNS)img.getChunk(Chunk.tRNS);
	if (trans != null) {
	  b.trans = trans.rgb;
	  b.translow = trans.rgb_low;
	  b.transgray = trans.r;
	}
      }
      return b;
    } catch (Exception e) {
      System.err.println("ERROR: " + e);
      throw new PngException("Error loading " + clsname);
    }
  }
}

