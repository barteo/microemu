// Copyright (C) 1998, 1999 Chris Nokleberg
// Please see included LICENSE.TXT

package com.sixlegs.image.png;

class PngException extends java.io.IOException {
  String msg = null;
  PngException () { }
  PngException (String s) {
    msg = s;
  }
  public String toString () {
    return msg;
  }
}

