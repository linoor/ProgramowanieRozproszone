
/**
* stringTableHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from LinkExchangeSystem.idl
* Saturday, December 12, 2015 2:20:33 PM CET
*/

public final class stringTableHolder implements org.omg.CORBA.portable.Streamable
{
  public String value[] = null;

  public stringTableHolder ()
  {
  }

  public stringTableHolder (String[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = stringTableHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    stringTableHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return stringTableHelper.type ();
  }

}
