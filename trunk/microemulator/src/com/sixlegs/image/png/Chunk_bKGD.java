// Copyright (C) 1998, 1999 Chris Nokleberg
// Please see included LICENSE.TXT

package com.sixlegs.image.png;
import java.awt.Color;
import java.io.IOException;

final class Chunk_bKGD extends Chunk {
  Chunk_bKGD () { super(bKGD); }

  protected boolean multipleOK () { return false; }
  protected boolean beforeIDAT () { return true; }

  protected void readData () throws IOException {
    int index, r, g, b;
    switch (img.header.colorType) {
    case PngImage.COLOR_TYPE_PALETTE:
      if (length != 1) badLength(1);
      index = in_data.readUnsignedByte();
      if (img.palette == null)
	throw new PngException("hIST chunk must follow PLTE chunk");
      r = img.palette.r_raw[index];
      g = img.palette.g_raw[index];
      b = img.palette.b_raw[index];
      break;

    case PngImage.COLOR_TYPE_GRAY:
    case PngImage.COLOR_TYPE_GRAY_ALPHA:
      if (length != 2) badLength(2);
      if (img.header.depth == 16) {
	r = g = b = in_data.readUnsignedByte();
	in_data.readUnsignedByte();
      } else {
	r = g = b = in_data.readUnsignedShort();
      }
      break;

    default: // truecolor
      if (length != 6) badLength(6);
      if (img.header.depth == 16) {
	r = in_data.readUnsignedByte();
	in_data.readUnsignedByte();
	g = in_data.readUnsignedByte();
	in_data.readUnsignedByte();
	b = in_data.readUnsignedByte();
	in_data.readUnsignedByte();
      } else {
	r = in_data.readUnsignedShort();
	g = in_data.readUnsignedShort();
	b = in_data.readUnsignedShort();
      }
    }
    img.properties.put("background", new Color(r, g, b));
  }
}
