// Copyright (C) 1998, 1999 Chris Nokleberg
// Please see included LICENSE.TXT

package com.sixlegs.image.png;
import java.io.*;

final class BitMover8RGB extends BitMover {
  int fill (int[] pixels, InputStream str, int off, int len) throws IOException {
    for (int n = 0; n < len; n++) {
      int r = str.read();
      int g = str.read();
      int b = str.read();
//    float[] answer = cspace.toRGB(new float[]{(float)r / 255, (float)g / 255, (float)b / 255});
//    r = (int)(answer[0] * 255);
//    g = (int)(answer[1] * 255);
//    b = (int)(answer[2] * 255);
      int val = gammaTable[r] << 16 | gammaTable[g] << 8 | gammaTable[b];
      pixels[off++] = ((r << 16 | g << 8 | b) == trans ? 0 : 0xFF000000) | val;
    }
    return off;
  }
}
