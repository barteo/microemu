// Copyright (C) 1998, 1999 Chris Nokleberg
// Please see included LICENSE.TXT

package com.sixlegs.image.png;
import java.io.*;

final class BitMover8P extends BitMover {
  int fill (int[] pixels, InputStream str, int off, int len) throws IOException {
    for (int n = 0; n < len; n++) {
      int x = str.read();
      pixels[off++] = x;
      if (x < 0 || x > 255) {
	System.err.println("bad palette entry: " + x);
      }
    }
    return off;
  }
}
