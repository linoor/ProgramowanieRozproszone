
/**
* LinkExchangeSystemHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from LinkExchangeSystem.idl
* sobota, 12 grudnia 2015 19:37:10 CET
*/

public final class LinkExchangeSystemHolder implements org.omg.CORBA.portable.Streamable
{
  public LinkExchangeSystem value = null;

  public LinkExchangeSystemHolder ()
  {
  }

  public LinkExchangeSystemHolder (LinkExchangeSystem initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = LinkExchangeSystemHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    LinkExchangeSystemHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return LinkExchangeSystemHelper.type ();
  }

}
