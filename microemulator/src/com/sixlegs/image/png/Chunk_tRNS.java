// Copyright (C) 1998, 1999 Chris Nokleberg
// Please see included LICENSE.TXT

package com.sixlegs.image.png;
import java.io.IOException;
import java.awt.image.*;

final class Chunk_tRNS extends Chunk {
  /* package */ int rgb;
  /* package */ int rgb_low;
  /* package */ int r, g, b;

  Chunk_tRNS () { super(tRNS); }

  protected boolean multipleOK () { return false; }
  protected boolean beforeIDAT () { return true; }

  protected void readData () throws IOException {
    int d = img.header.outputDepth;
    switch (img.header.colorType) {
    case PngImage.COLOR_TYPE_GRAY:
      if (length != 2) badLength(2);
      r = g = b = in_data.readUnsignedShort();
      rgb = r | r << d | r << (d * 2);
      img.header.model = img.header.alphaModel;
      break;

    case PngImage.COLOR_TYPE_RGB:
      if (length != 6) badLength(6);
      int rh = r = in_data.readUnsignedShort();
      int gh = g = in_data.readUnsignedShort();
      int bh = b = in_data.readUnsignedShort();
      if (img.header.depth == 16) {
	rh = r >>> 8;
	gh = g >>> 8;
	bh = b >>> 8;
	rgb_low = (b & 0xFF) | (g & 0xFF) << 8 | (r & 0xFF) << 16;
      }
      rgb = bh | gh << d | rh << (d * 2);
      img.header.model = img.header.alphaModel;
      break;

    case PngImage.COLOR_TYPE_PALETTE:
      if (img.palette == null)
	throw new PngException("tRNS chunk must follow PLTE chunk");
      Chunk_PLTE p = img.palette;
      int size = p.r.length;
      if (length > size) badLength();
      p.a = new byte[size];
      int i = 0;
      while (i < length) {
	p.a[i++] = (byte)in_data.readUnsignedByte();
      }
      while (i < size) {
	p.a[i++] = (byte)0xFF;
      }
      break;

    default:
      throw new PngException("tRNS prohibited for color type " + img.header.colorType);
    }
  }

}
