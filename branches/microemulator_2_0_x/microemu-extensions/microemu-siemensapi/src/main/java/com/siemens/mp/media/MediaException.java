// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) debugmode 
// Source File Name:   MediaException.java
// Class Version:      45.3

package com.siemens.mp.media;


// flag ACC_SUPER is set
public class MediaException extends Exception
{
    // Constants:          21
    // Interfaces:         0
    // Fields:             0
    // Methods:            2
    // Class Attributes:   1


    // Decompiling method: <init>  Signature: ()V
    // Max stack: 1, #locals: 1, #params: 1
    // Code length: 5 bytes, Code offset: 327
    // Local Variable Table found: 1 entries
    // Line Number Table found: 2 entries
    // Parameter  0 added: Name this Type Lcom/siemens/mp/media/MediaException; At 0 5 Range 0 4 Init 0 fixed
    // RetValue   1 added: Name <returnValue> Type V At 0 5 Range 0 4 Init 0 fixed
    public MediaException()
    {
        /* super(); */
        /* return; */
    }

    // Decompiling method: <init>  Signature: (Ljava/lang/String;)V
    // Max stack: 2, #locals: 2, #params: 2
    // Code length: 6 bytes, Code offset: 392
    // Local Variable Table found: 2 entries
    // Line Number Table found: 2 entries
    // Parameter  0 added: Name this Type Lcom/siemens/mp/media/MediaException; At 0 6 Range 0 5 Init 0 fixed
    // Parameter  1 added: Name reason Type Ljava/lang/String; At 0 6 Range 0 5 Init 0 fixed
    // RetValue   2 added: Name <returnValue> Type V At 0 6 Range 0 5 Init 0 fixed
    public MediaException(String reason)
    {
        super(reason);
        /* return; */
    }
}
