/* XMLElement.java                                                 NanoXML/Lite
 *
 * $Revision$
 * $Date$
 * $Name$
 *
 * This file is part of NanoXML 2 Lite.
 * Copyright (C) 2001 Marc De Scheemaecker, All Rights Reserved.
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from the
 * use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software in
 *     a product, an acknowledgment in the product documentation would be
 *     appreciated but is not required.
 *
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 *
 *  3. This notice may not be removed or altered from any source distribution.
 */


package nanoxml;


import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


/**
 * XMLElement is a representation of an XML object. The object is able to parse
 * XML code.
 *
 * @see nanoxml.XMLParseException
 *
 * @author Marc De Scheemaecker
 * @version $Name$, $Revision$
 */
public class XMLElement
{

    /**
     * Serialization serial version ID.
     */
    static final long serialVersionUID = 6685035139346394777L;
   
   
    /**
     * Major version of NanoXML.
     */
    public static final int NANOXML_MAJOR_VERSION = 2;


    /**
     * Minor version of NanoXML.
     */
    public static final int NANOXML_MINOR_VERSION = 0;

   
    /**
     * The attributes given to the object.
     */
    private Hashtable attributes;
   
   
    /**
     * Subobjects of the object. The subobjects are of class XMLElement
     * themselves.
     */
    private Vector children;
   
   
    /**
     * The class of the object (the name indicated in the tag).
     */
    private String name;
   

    /**
     * The #PCDATA content of the object. If there is no such content, this
     * field is null.
     */
    private String contents;
   
   
    /**
     * Conversion table for &amp;...; entities.
     */
    private Hashtable entities;


    /**
     * The line number where the element starts.
     */
    private int lineNr;
   
    
    /**
     * If the case of the element and attribute names are case insensitive.
     */
    private boolean ignoreCase;
    
    
    /**
     * If the leading and trailing whitespace of #PCDATA sections have to be
     * ignored.
     */
    private boolean ignoreWhitespace;
    
    
    /**
     * Character read too much.
     */
    private char charReadTooMuch;
    
    
    /**
     * The reader.
     */
    private Reader reader;
    
    
    /**
     * The parsing line nr.
     */
    private int parserLineNr;
    
    
    /**
     * Creates a new XML element. The following settings are used:
     * <DL><DT>Conversion table</DT>
     *     <DD>Minimal XML conversions: <CODE>&amp;amp; &amp;lt; &amp;gt;
     *         &amp;apos; &amp;quot;</CODE></DD>
     *     <DT>Skip whitespace in contents</DT>
     *     <DD><CODE>false</CODE></DD>
     *     <DT>Ignore Case</DT>
     *     <DD><CODE>true</CODE></DD>
     * </DL>
     *
     * @see nanoxml.XMLElement#XMLElement(java.util.Hashtable)
     * @see nanoxml.XMLElement#XMLElement(boolean)
     * @see nanoxml.XMLElement#XMLElement(java.util.Hashtable,boolean)
     */
    public XMLElement()
    {
        this(new Hashtable(), false, true, true);
    }


    /**
     * Creates a new XML element. The following settings are used:
     * <DL><DT>Conversion table</DT>
     *     <DD><I>entities</I> combined with the minimal XML
     *         conversions: <CODE>&amp;amp; &amp;lt; &amp;gt;
     *         &amp;apos; &amp;quot;</CODE></DD>
     *     <DT>Skip whitespace in contents</DT>
     *     <DD><CODE>false</CODE></DD>
     *     <DT>Ignore Case</DT>
     *     <DD><CODE>true</CODE></DD>
     * </DL>
     *
     * @see nanoxml.XMLElement#XMLElement()
     * @see nanoxml.XMLElement#XMLElement(boolean)
     * @see nanoxml.XMLElement#XMLElement(java.util.Hashtable,boolean)
     */
    public XMLElement(Hashtable entities)
    {
        this(entities, false, true, true);
    }
    
 
    /**
     * Creates a new XML element. The following settings are used:
     * <DL><DT>Conversion table</DT>
     *     <DD>Minimal XML conversions: <CODE>&amp;amp; &amp;lt; &amp;gt;
     *         &amp;apos; &amp;quot;</CODE></DD>
     *     <DT>Skip whitespace in contents</DT>
     *     <DD><I>skipLeadingWhitespace</I></DD>
     *     <DT>Ignore Case</DT>
     *     <DD><CODE>true</CODE></DD>
     * </DL>
     *
     * @see nanoxml.XMLElement#XMLElement()
     * @see nanoxml.XMLElement#XMLElement(java.util.Hashtable)
     * @see nanoxml.XMLElement#XMLElement(java.util.Hashtable,boolean)
     */
    public XMLElement(boolean skipLeadingWhitespace)
    {
        this(new Hashtable(), skipLeadingWhitespace, true, true);
    }


    /**
     * Creates a new XML element. The following settings are used:
     * <DL><DT>Conversion table</DT>
     *     <DD><I>entities</I> combined with the minimal XML
     *         conversions: <CODE>&amp;amp; &amp;lt; &amp;gt;
     *         &amp;apos; &amp;quot;</CODE></DD>
     *     <DT>Skip whitespace in contents</DT>
     *     <DD><I>skipLeadingWhitespace</I></DD>
     *     <DT>Ignore Case</DT>
     *     <DD><CODE>true</CODE></DD>
     * </DL>
     *
     * @see nanoxml.XMLElement#XMLElement()
     * @see nanoxml.XMLElement#XMLElement(boolean)
     * @see nanoxml.XMLElement#XMLElement(java.util.Hashtable)
     */
    public XMLElement(Hashtable entities,
                      boolean   skipLeadingWhitespace)
    {
        this(entities, skipLeadingWhitespace, true, true);
    }


    /**
     * Creates a new XML element. The following settings are used:
     * <DL><DT>Conversion table</DT>
     *     <DD><I>entities</I>, eventually combined with the minimal XML
     *         conversions: <CODE>&amp;amp; &amp;lt; &amp;gt;
     *         &amp;apos; &amp;quot;</CODE>
     *         (depending on <I>fillBasicConversionTable</I>)</DD>
     *     <DT>Skip whitespace in contents</DT>
     *     <DD><I>skipLeadingWhitespace</I></DD>
     *     <DT>Ignore Case</DT>
     *     <DD><I>ignoreCase</I></DD>
     * </DL>
     * <P>
     * This constructor should <I>only</I> be called from XMLElement itself
     * to create child elements.
     *
     * @see nanoxml.XMLElement#XMLElement()
     * @see nanoxml.XMLElement#XMLElement(boolean)
     * @see nanoxml.XMLElement#XMLElement(java.util.Hashtable)
     * @see nanoxml.XMLElement#XMLElement(java.util.Hashtable,boolean)
     */
    public XMLElement(Hashtable entities,
                      boolean   skipLeadingWhitespace,
                      boolean   ignoreCase)
    {
        this(entities, skipLeadingWhitespace, true, ignoreCase);
    }


    /**
     * Creates a new XML element. The following settings are used:
     * <DL><DT>Conversion table</DT>
     *     <DD><I>entities</I>, eventually combined with the minimal XML
     *         conversions: <CODE>&amp;amp; &amp;lt; &amp;gt;
     *         &amp;apos; &amp;quot;</CODE>
     *         (depending on <I>fillBasicConversionTable</I>)</DD>
     *     <DT>Skip whitespace in contents</DT>
     *     <DD><I>skipLeadingWhitespace</I></DD>
     *     <DT>Ignore Case</DT>
     *     <DD><I>ignoreCase</I></DD>
     * </DL>
     * <P>
     * This constructor should <I>only</I> be called from XMLElement itself
     * to create child elements.
     *
     * @see nanoxml.XMLElement#XMLElement()
     * @see nanoxml.XMLElement#XMLElement(boolean)
     * @see nanoxml.XMLElement#XMLElement(java.util.Hashtable)
     * @see nanoxml.XMLElement#XMLElement(java.util.Hashtable,boolean)
     */
    protected XMLElement(Hashtable entities,
                         boolean   skipLeadingWhitespace,
                         boolean   fillBasicConversionTable,
                         boolean   ignoreCase)
    {
        this.ignoreWhitespace = skipLeadingWhitespace;
        this.ignoreCase = ignoreCase;
        this.name = null;
        this.contents = "";
        this.attributes = new Hashtable();
        this.children = new Vector();
        this.entities = entities;
        this.lineNr = 0;
    
        Enumeration enum = this.entities.keys();
        
        while (enum.hasMoreElements()) {
            Object key = enum.nextElement();
            Object value = this.entities.get(key);
            
            if (value instanceof String) {
                value = ((String) value).toCharArray();
                this.entities.put(key, value);
            }
        }
        
        if (fillBasicConversionTable) {
            this.entities.put("amp", new char[] { '&' });
            this.entities.put("quot", new char[] { '"' });
            this.entities.put("apos", new char[] { '\'' });
            this.entities.put("lt", new char[] { '<' });
            this.entities.put("gt", new char[] { '>' });
        }
    }
    
    
    /**
     * Cleans up the object when it's destroyed.
     */
    protected void finalize()
        throws Throwable
    {
        this.name = null;
        this.contents = null;
        this.attributes.clear();
        this.attributes = null;
        this.children.clear();
        this.children = null;
        this.entities.clear();
        this.entities = null;
        super.finalize();
    }


    /**
     * Adds a subobject.
     */
    public void addChild(XMLElement child)
    {
        this.children.addElement(child);
    }


    /**
     * Adds a property.
     */
    public void addProperty(String key,
                            Object value)
    {
        if (this.ignoreCase) {
            key = key.toUpperCase();
        }
        
        this.attributes.put(key, value.toString());
    }


    /**
     * Adds a property.
     */
    public void addProperty(String key,
                            int    value)
    {
        if (this.ignoreCase) {
            key = key.toUpperCase();
        }
        
        this.attributes.put(key, Integer.toString(value));
    }


    /**
     * Adds a property.
     */
    public void addProperty(String key,
                            double value)
    {
        if (this.ignoreCase) {
            key = key.toUpperCase();
        }
        
        this.attributes.put(key, Double.toString(value));
    }


    /**
     * Returns the number of subobjects of the object.
     */
    public int countChildren()
    {
        return this.children.size();
    }


    /**
     * Enumerates the attribute names.
     */
    public Enumeration enumeratePropertyNames()
    {
        return this.attributes.keys();
    }


    /**
     * Enumerates the subobjects of the object.
     */
    public Enumeration enumerateChildren()
    {
        return this.children.elements();
    }


    /**
     * Returns the subobjects of the object.
     */
    public Vector getChildren()
    {
        return this.children;
    }


    /**
     * Returns the #PCDATA content of the object. If there is no such content,
     * <CODE>null</CODE> is returned.
     *
     * @deprecated Use getContent instead.
     */
    public String getContents()
    {
        return this.getContent();
    }


    /**
     * Returns the #PCDATA content of the object. If there is no such content,
     * <CODE>null</CODE> is returned.
     */
    public String getContent()
    {
        return this.contents;
    }


    /**
     * Returns the line nr on which the element is found.
     */
    public int getLineNr()
    {
        return this.lineNr;
    }


    /**
     * Returns a property by looking up a key in a hashtable.
     * If the property doesn't exist, the value corresponding to defaultValue
     * is returned.
     */
    public int getIntProperty(String    key,
                              Hashtable valueSet,
                              String    defaultValue)
    {
        if (this.ignoreCase) {
            key = key.toUpperCase();
        }
        
        Object val = this.attributes.get(key);
        Integer result;

        if (val == null) {
            val = defaultValue;
        }

        try {
            result = (Integer) valueSet.get(val);
        } catch (ClassCastException e) {
            throw this.invalidValueSet(key);
        }

        if (result == null) {
            throw this.invalidValue(key, (String) val);
        }

        return result.intValue();
    }


    /**
     * Returns a property of the object. If there is no such property, this
     * method returns <CODE>null</CODE>.
     */
    public String getProperty(String key)
    {
        if (this.ignoreCase) {
            key = key.toUpperCase();
        }
        
        return (String) this.attributes.get(key);
    }


    /**
     * Returns a property of the object.
     * If the property doesn't exist, <I>defaultValue</I> is returned.
     */
    public String getProperty(String key,
                              String defaultValue)
    {
        if (this.ignoreCase) {
            key = key.toUpperCase();
        }
        
        String value = (String) this.attributes.get(key);
        
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }


    /**
     * Returns an integer property of the object.
     * If the property doesn't exist, <I>defaultValue</I> is returned.
     */
    public int getProperty(String key,
                           int    defaultValue)
    {
        if (this.ignoreCase) {
            key = key.toUpperCase();
        }
        
        String val = (String) this.attributes.get(key);
       
        if (val == null) {
            return defaultValue;
        } else {
            try {
                return Integer.parseInt(val);
            } catch (NumberFormatException e) {
                throw this.invalidValue(key, val);
            }
        }
    }


    /**
     * Returns a floating point property of the object.
     * If the property doesn't exist, <I>defaultValue</I> is returned.
     */
    public double getProperty(String key,
                              double defaultValue)
    {
        if (this.ignoreCase) {
            key = key.toUpperCase();
        }
        
        String val = (String) this.attributes.get(key);
    
        if (val == null) {
            return defaultValue;
        } else {
            try {
                return Double.valueOf(val).doubleValue();
            } catch (NumberFormatException e) {
                throw this.invalidValue(key, val);
            }
        }
    }


    /**
     * Returns a boolean property of the object. If the property is missing,
     * <I>defaultValue</I> is returned.
     */
    public boolean getProperty(String  key,
                               String  trueValue,
                               String  falseValue,
                               boolean defaultValue)
    {
        if (this.ignoreCase) {
            key = key.toUpperCase();
        }
        
        Object val = this.attributes.get(key);
        
        if (val == null) {
            return defaultValue;
        } else if (val.equals(trueValue)) {
            return true;
        } else if (val.equals(falseValue)) {
            return false;
        } else {
            throw this.invalidValue(key, (String) val);
        }
    }
   
   
    /**
     * Returns a property by looking up a key in the hashtable <I>valueSet</I>
     * If the property doesn't exist, the value corresponding to
     * <I>defaultValue</I>  is returned.
     */
    public Object getProperty(String    key,
                              Hashtable valueSet,
                              String    defaultValue)
    {
        if (this.ignoreCase) {
            key = key.toUpperCase();
        }
        
        Object val = this.attributes.get(key);
        
        if (val == null) {
            val = defaultValue;
        }
        
        Object result = valueSet.get(val);
        
        if (result == null) {
            throw this.invalidValue(key, (String) val);
        }
        
        return result;
    }
   
                                       
    /**
     * Returns a property by looking up a key in the hashtable <I>valueSet</I>.
     * If the property doesn't exist, the value corresponding to
     * <I>defaultValue</I>  is returned.
     */
    public String getStringProperty(String    key,
                                    Hashtable valueSet,
                                    String    defaultValue)
    {
        if (this.ignoreCase) {
            key = key.toUpperCase();
        }
        
        Object val = this.attributes.get(key);
        String result;
        
        if (val == null) {
            val = defaultValue;
        }
        
        try {
            result = (String) valueSet.get(val);
        } catch (ClassCastException e) {
            throw this.invalidValueSet(key);
        }
        
        if (result == null) {
            throw this.invalidValue(key, (String) val);
        }
        
        return result;
    }
   
                                       
    /**
     * Returns a property by looking up a key in the hashtable <I>valueSet</I>.
     * If the value is not defined in the hashtable, the value is considered to
     * be an integer.
     * If the property doesn't exist, the value corresponding to
     * <I>defaultValue</I> is returned.
     */
    public int getSpecialIntProperty(String    key,
                                     Hashtable valueSet,
                                     String    defaultValue)
    {
        if (this.ignoreCase) {
            key = key.toUpperCase();
        }
        
        String val = (String) this.attributes.get(key);
        Integer result;
        
        if (val == null) {
            val = defaultValue;
        }
        
        try {
            result = (Integer) valueSet.get(val);
        } catch (ClassCastException e) {
            throw this.invalidValueSet(key);
        }
        
        if (result == null) {
            try {
                return Integer.parseInt(val);
            } catch (NumberFormatException e) {
                throw this.invalidValue(key, val);
            }
        }
        
        return result.intValue();
    }
    
                                        
    /**
     * Returns a property by looking up a key in the hashtable <I>valueSet</I>.
     * If the value is not defined in the hashtable, the value is considered to
     * be a floating point number.
     * If the property doesn't exist, the value corresponding to
     * <I>defaultValue</I> is returned.
     */
    public double getSpecialDoubleProperty(String    key,
                                           Hashtable valueSet,
                                           String    defaultValue)
    {
        if (this.ignoreCase) {
            key = key.toUpperCase();
        }
        
        String val = (String) this.attributes.get(key);
        Double result;
        
        if (val == null) {
            val = defaultValue;
        }
        
        try {
            result = (Double) valueSet.get(val);
        } catch (ClassCastException e) {
            throw this.invalidValueSet(key);
        }
        
        if (result == null) {
            try {
                result = Double.valueOf(val);
            } catch (NumberFormatException e) {
                throw this.invalidValue(key, val);
            }
        }
        
        return result.doubleValue();
    }
    
                                        
    /**
     * Returns the class (i.e. the name indicated in the element) of the
     * object.
     */
    public String getName()
    {
        return this.name;
    }


    /**
     * Returns the class (i.e. the name indicated in the element) of the
     * object.
     *
     * @deprecated Use getName instead.
     */
    public String getTagName()
    {
        return this.getName();
    }


    /**
     * Reads an XML definition from a java.io.Reader and parses it.
     *
     * @exception java.io.IOException
     *    if an error occured while reading the input
     * @exception nanoxml.XMLParseException
     *    if an error occured while parsing the read data
     */
    public void parseFromReader(Reader reader)
        throws IOException, XMLParseException
    {
        this.parseFromReader(reader, /*startingLineNr*/ 1);
    }
    
 
    /**
     * Reads an XML definition from a java.io.Reader and parses it.
     *
     * @exception java.io.IOException
     *    if an error occured while reading the input
     * @exception nanoxml.XMLParseException
     *    if an error occured while parsing the read data
     */
    public void parseFromReader(Reader reader,
                                int    startingLineNr)
        throws IOException, XMLParseException
    {
        this.charReadTooMuch = '\0';
        this.reader = reader;
        this.parserLineNr = startingLineNr;
        
        for (;;) {
            char ch = this.scanWhitespace();
            
            if (ch != '<') {
                throw this.expectedInput("<");
            }
            
            ch = this.readChar();
            
            if (ch == '!') {
                this.skipSpecialTag(0);
            } else {
                this.unreadChar(ch);
                this.scanElement(this);
                return;
            }
        } 
    }
  
  
    /**
     * Parses an XML definition.
     *
     * @exception nanoxml.XMLParseException
     *    if an error occured while parsing the string
     */
    public void parseString(String string)
        throws XMLParseException
    {
        try {
            this.parseFromReader(new StringReader(string),
                                 /*startingLineNr*/ 1);
        } catch (IOException e) {
            // Java exception handling suxx
        }
    }
    
    
    /**
     * Parses an XML definition starting at <I>offset</I>.
     *
     * @exception nanoxml.XMLParseException
     *    if an error occured while parsing the string
     */
    public void parseString(String string,
                            int    offset)
        throws XMLParseException
    {
        this.parseString(string.substring(offset));
    }
    
    
    /**
     * Parses an XML definition starting at <I>offset</I>.
     *
     * @exception nanoxml.XMLParseException
     *    if an error occured while parsing the string
     */
    public void parseString(String string,
                            int    offset,
                            int    end)
        throws XMLParseException
    {
        this.parseString(string.substring(offset, end));
    }
    
    
    /**
     * Parses an XML definition starting at <I>offset</I>.
     *
     * @exception nanoxml.XMLParseException
     *    if an error occured while parsing the string
     */
    public void parseString(String string,
                            int    offset,
                            int    end,
                            int    startingLineNr)
        throws XMLParseException
    {
        string = string.substring(offset, end);
        try {
            this.parseFromReader(new StringReader(string),
                                        startingLineNr);
        } catch (IOException e) {
            // Java exception handling suxx
        }
    } 
    
    
    /**
     * Parses an XML definition starting at <I>offset</I>.
     *
     * @exception nanoxml.XMLParseException
     *    if an error occured while parsing the array
     */
    public void parseCharArray(char[] input,
                               int    offset,
                               int    end)
        throws XMLParseException
    {
        this.parseCharArray(input, offset, end, /*startingLineNr*/ 1);
    }
    
    
    /**
     * Parses an XML definition starting at <I>offset</I>.
     *
     * @exception nanoxml.XMLParseException
     *    if an error occured while parsing the array
     */
    public void parseCharArray(char[] input,
                               int    offset,
                               int    end,
                               int    startingLineNr)
        throws XMLParseException
    {
        try {
            Reader reader = new CharArrayReader(input, offset, end);
            this.parseFromReader(reader, startingLineNr);
        } catch (IOException e) {
            // Java exception handling suxx
        }
    }


    /**
     * Removes a child object. If the object is not a child, nothing happens.
     */
    public void removeChild(XMLElement child)
    {
        this.children.removeElement(child);
    }


    /**
     * Removes an attribute.
     */
    public void removeProperty(String name)
    {
        if (this.ignoreCase) {
            name = name.toUpperCase();
        }
        
        this.attributes.remove(name);
    }


    /**
     * Removes an attribute.
     *
     * @deprecated Use removeProperty instead.
     */
    public void removeChild(String name)
    {
        this.removeProperty(name);
    }


    /**
     * Creates a new XML element.
     */
    protected XMLElement createAnotherElement()
    {
        return new XMLElement(this.entities,
                              this.ignoreWhitespace,
                              false,
                              this.ignoreCase);
    }


    /**
     * Changes the content string.
     *
     * @param content The new content string.
     */
    public void setContent(String content)
    {
        this.contents = content;
    }


    /**
     * Changes the element name.
     *
     * @param name The new name.
     *
     * @deprecated Use setName instead.
     */
    public void setTagName(String name)
    {
        this.setName(name);
    }


    /**
     * Changes the element name.
     *
     * @param name The new name.
     */
    public void setName(String name)
    {
        this.name = name;
    }


    /**
     * Writes the XML element to a string.
     */
    public String toString()
    {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(out);
            this.write(writer);
            writer.flush();
            return new String(out.toByteArray());
        } catch (IOException e) {
            // Java exception handling suxx
            return super.toString();
        }
    }


    /**
     * Writes the XML element to a writer.
     */
    public void write(Writer writer)
        throws IOException
    {
        if (this.name == null) {
            this.writeEncoded(writer, this.contents);
            return;
        }

        writer.write('<');
        writer.write(this.name);

        if (! this.attributes.isEmpty()) {
            Enumeration enum = this.attributes.keys();

            while (enum.hasMoreElements()) {
                writer.write(' ');
                String key = (String) enum.nextElement();
                String value = (String) this.attributes.get(key);
                writer.write(key);
                writer.write('='); writer.write('"');
                this.writeEncoded(writer, value);
                writer.write('"');
            }
        }

        if ((this.contents != null) && (this.contents.length() > 0)) {
            writer.write('>');
            this.writeEncoded(writer, this.contents);
            writer.write('<'); writer.write('/');
            writer.write(this.name);
            writer.write('>');
        } else if (this.children.isEmpty()) {
            writer.write('/'); writer.write('>');
        } else {
            writer.write('>');
            Enumeration enum = this.enumerateChildren();
            
            while (enum.hasMoreElements()) {
                XMLElement child = (XMLElement) enum.nextElement();
                child.write(writer);
            }
            
            writer.write('<'); writer.write('/');
            writer.write(this.name);
            writer.write('>');
        }
    }


    /**
     * Writes a string encoded to a writer.
     */
    protected void writeEncoded(Writer writer,
                                String str)
        throws IOException
    {
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            
            switch (ch) {
                case '<':
                    writer.write('&'); writer.write('l'); writer.write('t');
                    writer.write(';');
                    break;
                    
                case '>':
                    writer.write('&'); writer.write('g'); writer.write('t');
                    writer.write(';');
                    break;
                    
                case '&':
                    writer.write('&'); writer.write('a'); writer.write('m');
                    writer.write('p'); writer.write(';');
                    break;
                    
                case '"':
                    writer.write('&'); writer.write('q'); writer.write('u');
                    writer.write('o'); writer.write('t'); writer.write(';');
                    break;
                    
                case '\'':
                    writer.write('&'); writer.write('a'); writer.write('p');
                    writer.write('o'); writer.write('s'); writer.write(';');
                    break;

                default:
                    int unicode = (int) ch;
                    
                    if ((unicode < 32) || (unicode > 126)) {
                        writer.write('&'); writer.write('#');
                        writer.write('x');
                        writer.write(Integer.toString(unicode, 16));
                        writer.write(';');
                    } else {
                        writer.write(ch);
                    }
            }
        }
    }
    
    
    /**
     * Scans an identifier.
     */
    protected void scanIdentifier(StringBuffer identifier)
        throws IOException
    {
        for (;;) {
            char ch = this.readChar();
            
            if (((ch < 'A') || (ch > 'Z')) && ((ch < 'a') || (ch > 'z'))
                    && ((ch < '0') || (ch > '9')) && (ch != '_') && (ch != '.')
                    && (ch != ':') && (ch != '-') && (ch <= '\u007E')) {
                this.unreadChar(ch);
                return;
            }
        
            identifier.append(ch);
        }
    }
    
    
    /**
     * Scans whitespace.
     */
    protected char scanWhitespace()
        throws IOException
    {
        for (;;) {
            char ch = this.readChar();
            
            switch (ch) {
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                    break;
                    
                default:
                    return ch;
            }
        }
    }
    
    
    /**
     * Scans whitespace.
     */
    protected char scanWhitespace(StringBuffer buf)
        throws IOException
    {
        for (;;) {
            char ch = this.readChar();
            
            switch (ch) {
                case ' ':
                case '\t':
                case '\n':
                    buf.append(ch);
                    
                case '\r':
                    break;
                    
                default:
                    return ch;
            }
        }
    }
    
    
    /**
     * Scans a string.
     */
    protected void scanString(StringBuffer string)
        throws IOException
    {
        char delimiter = this.readChar();
        
        if ((delimiter != '\'') && (delimiter != '"')) {
            throw this.expectedInput("' or \"");
        }
        
        for (;;) {
            char ch = this.readChar();
            
            if (ch == delimiter) {
                return;
            } else if (ch == '&') {
                this.resolveEntity(string);
            } else {
                string.append(ch);
            }
        }
    }
    
    
    /**
     * Scans a #PCDATA element. CDATA sections and entities are resolved.
     * The next &lt; char is skipped.
     */
    protected void scanPCData(StringBuffer data)
        throws IOException
    {
        for (;;) {
            char ch = this.readChar();
            
            if (ch == '<') {
                ch = this.readChar();
                
                if (ch == '!') {
                    this.checkCDATA(data);
                } else {
                    this.unreadChar(ch);
                    return;
                }
            } else if (ch == '&') {
                this.resolveEntity(data);
            } else {
                data.append(ch);
            }
        }
    }
    
    
    /**
     * Scans a special tag and if the tag is a CDATA section, append its 
     * content to buf.
     */
    protected boolean checkCDATA(StringBuffer buf)
        throws IOException
    {
        char ch = this.readChar();
        if (ch != '[') {
            this.unreadChar(ch);
            this.skipSpecialTag(0);
            return false;
        } else if (! this.checkLiteral("CDATA[")) {
            this.skipSpecialTag(1); // one [ has already been read
            return false;
        } else {
            int delimiterCharsSkipped = 0;
            while (delimiterCharsSkipped < 3) {
                ch = this.readChar();
                switch (ch) {
                    case ']':
                        if (delimiterCharsSkipped < 2) {
                            ++(delimiterCharsSkipped);
                        } else {
                            buf.append(']');
                            buf.append(']');
                            delimiterCharsSkipped = 0;
                        }
                        
                        break;
                        
                    case '>':
                        if (delimiterCharsSkipped < 2) {
                            for (int i = 0; i < delimiterCharsSkipped; ++i) {
                                buf.append(']');
                            }
                            
                            delimiterCharsSkipped = 0;
                            buf.append('>');
                        } else {
                            delimiterCharsSkipped = 3;
                        }
                        
                        break;
                        
                    default:
                        for (int i = 0; i < delimiterCharsSkipped; ++i) {
                            buf.append(']');
                        }
                            
                        buf.append(ch);
                        delimiterCharsSkipped = 0;
                }
            }
            
            return true;
        }
    }
    
    
    /**
     * Skips a comment.
     */
    protected void skipComment()
        throws IOException
    {
        int dashesToRead = 2;
        
        while (dashesToRead > 0) {
            char ch = this.readChar();
            
            if (ch == '-') {
                --(dashesToRead);
            } else {
                dashesToRead = 2;
            }
        }
        
        if (this.readChar() != '>') {
            throw this.expectedInput(">");
        }
    }
    
    
    /**
     * Skips a special tag or comment.
     */
    protected void skipSpecialTag(int bracketLevel)
        throws IOException
    {
        int tagLevel = 1; // <
        char stringDelimiter = '\0';
        
        if (bracketLevel == 0) {
            char ch = this.readChar();
            
            if (ch == '[') {
                ++(bracketLevel);
            } else if (ch == '-') {
                ch = this.readChar();
                
                if (ch == '[') {
                    ++(bracketLevel);
                } else if (ch == ']') {
                    --(bracketLevel);
                } else if (ch == '-') {
                    this.skipComment();
                    return;
                }
            }
        }
        
        while (tagLevel > 0) {
            char ch = this.readChar();
            
            if (stringDelimiter == '\0') {
                if ((ch == '"') || (ch == '\'')) {
                    stringDelimiter = ch;
                } else if (bracketLevel <= 0) {
                    if (ch == '<') {
                        ++(tagLevel);
                    } else if (ch == '>') {
                        --(tagLevel);
                    }
                }
                
                if (ch == '[') {
                    ++(bracketLevel);
                } else if (ch == ']') {
                    --(bracketLevel);
                }
            } else {
                if (ch == stringDelimiter) {
                    stringDelimiter = '\0';
                }
            }
        }
    }
    
    
    /**
     * Scans the data for literal text.
     */
    protected boolean checkLiteral(String literal)
        throws IOException
    {
        for (int i = 0; i < literal.length(); i++) {
            if (this.readChar() != literal.charAt(i)) {
                return false;
            }
        }
        
        return true;
    }
    
    
    /**
     * Reads a character from a reader.
     */
    protected char readChar()
        throws IOException
    {
        if (this.charReadTooMuch != '\0') {
            char ch = this.charReadTooMuch;
            this.charReadTooMuch = '\0';
            return ch;
        } else {
            int i = this.reader.read();
            
            if (i < 0) {
                throw this.unexpectedEndOfData();
            } else if (i == 10) {
                ++(this.parserLineNr);
                return '\n';
            } else {
                return (char) i;
            }
        }
    }


    /**
     * Scans an XML element.
     */
    protected void scanElement(XMLElement elt)
        throws IOException
    {
        StringBuffer buf = new StringBuffer();
        this.scanIdentifier(buf);
        String name = buf.toString();
        elt.setName(name);
        char ch = this.scanWhitespace();
        
        while ((ch != '>') && (ch != '/')) {
            buf.setLength(0);
            this.unreadChar(ch);
            this.scanIdentifier(buf);
            String key = buf.toString();
            ch = this.scanWhitespace();
            
            if (ch != '=') {
                throw this.expectedInput("=");
            }
            
            this.unreadChar(this.scanWhitespace());
            buf.setLength(0);
            this.scanString(buf);
            elt.addProperty(key, buf);
            ch = this.scanWhitespace();
        }
        
        if (ch == '/') {
            ch = this.readChar();
            
            if (ch != '>') {
                throw this.expectedInput(">");
            }
            
            return;
        }
        
        buf.setLength(0);
        ch = this.scanWhitespace(buf);
        
        if (ch != '<') {
            this.unreadChar(ch);
            this.scanPCData(buf);
        } else {
            for (;;) {
                ch = this.readChar();
                
                if (ch == '!') {
                    if (this.checkCDATA(buf)) {
                        this.scanPCData(buf);
                        break;
                    } else {
                        ch = this.scanWhitespace(buf);
                        if (ch != '<') {
                            this.unreadChar(ch);
                            this.scanPCData(buf);
                            break;
                        }
                    }
                } else {
                    buf.setLength(0);
                    break;
                }
            }
        }
        
        if (buf.length() == 0) {
            while (ch != '/') {
                this.unreadChar(ch);
                XMLElement child = new XMLElement(this.entities,
                                                    this.ignoreWhitespace,
                                                    false, this.ignoreCase);
                this.scanElement(child);
                elt.addChild(child);
                ch = this.scanWhitespace();
                if (ch != '<') {
                    throw this.expectedInput("<");
                }
                ch = this.readChar();
                while (ch == '!') {
                    ch = this.readChar();
                    if (ch != '-') {
                        throw this.expectedInput("Comment or Element");
                    }
                    ch = this.readChar();
                    if (ch != '-') {
                        throw this.expectedInput("Comment or Element");
                    }
                    this.skipComment();
                    ch = this.scanWhitespace();
                    if (ch != '<') {
                        throw this.expectedInput("<");
                    }
                    ch = this.readChar();
                }
            }
            
            this.unreadChar(ch);
        } else {
            if (this.ignoreWhitespace) {
                elt.setContent(buf.toString().trim());
            } else {
                elt.setContent(buf.toString());
            }
        }

        ch = this.readChar();
        
        if (ch != '/') {
            throw this.expectedInput("/");
        }
        
        this.unreadChar(this.scanWhitespace());
        
        if (! this.checkLiteral(name)) {
            throw this.expectedInput(name);
        }
        
        if (this.scanWhitespace() != '>') {
            throw this.expectedInput(">");
        }
    }
    
    
    /**
     * Resolves an entity.
     */
    protected void resolveEntity(StringBuffer buf)
        throws IOException
    {
        char ch = '\0';
        StringBuffer keyBuf = new StringBuffer();
        
        for (;;) {
            ch = this.readChar();
            
            if (ch == ';') {
                break;
            }
        
            keyBuf.append(ch);
        }

        String key = keyBuf.toString();
        
        if (key.charAt(0) == '#') {
            try {
                if (key.charAt(1) == 'x') {
                    ch = (char) Integer.parseInt(key.substring(2), 16);
                } else {
                    ch = (char) Integer.parseInt(key.substring(1), 10);
                }
            } catch (NumberFormatException e) {
                throw this.unknownEntity(key);
            }
            
            buf.append(ch);
        } else {
            char[] value = (char[]) this.entities.get(key);
            
            if (value == null) {
                throw this.unknownEntity(key);
            }
            
            buf.append(value);
        }
    }
    
        
    /**
     * Adds a character to the read-back buffer.
     */
    protected void unreadChar(char ch)
    {
        this.charReadTooMuch = ch;
    }
    
    
    /**
     * Creates a parse exception for when an invalid valueset is given to
     * a method.
     */
    protected XMLParseException invalidValueSet(String key)
    {
        String msg = "Invalid value set (key = \"" + key + "\")";
        return new XMLParseException(this.getName(), this.parserLineNr, msg);
    }


    /**
     * Creates a parse exception for when an invalid value is given to a
     * method.
     */
    protected XMLParseException invalidValue(String key,
                                             String value)
    {
        String msg = "Attribute \"" + key + "\" does not contain a valid "
                   + "value (\"" + value + "\")";
        return new XMLParseException(this.getName(), this.parserLineNr, msg);
    }


    /**
     * The end of the data input has been reached.
     */
    protected XMLParseException unexpectedEndOfData()
    {
        String msg = "Unexpected end of data reached";
        return new XMLParseException(this.getName(), this.parserLineNr, msg);
    }


    /**
     * A syntax error occured.
     */
    protected XMLParseException syntaxError(String context)
    {
        String msg = "Syntax error while parsing " + context;
        return new XMLParseException(this.getName(), this.parserLineNr, msg);
    }


    /**
     * A character has been expected.
     */
    protected XMLParseException expectedInput(String charSet)
    {
        String msg = "Expected: " + charSet;
        return new XMLParseException(this.getName(), this.parserLineNr, msg);
    }


    /**
     * A value is missing for an attribute.
     */
    protected XMLParseException valueMissingForAttribute(String key)
    {
        String msg = "Value missing for attribute with key \"" + key + "\"";
        return new XMLParseException(this.getName(), this.parserLineNr, msg);
    }


    /**
     * Invalid entity.
     */
    protected XMLParseException unknownEntity(String key)
    {
        String msg = "Unknown or invalid entity: &" + key + ";";
        return new XMLParseException(this.getName(), this.parserLineNr, msg);
    }

}
