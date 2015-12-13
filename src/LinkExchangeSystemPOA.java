
/**
* LinkExchangeSystemPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from LinkExchangeSystem.idl
* Saturday, December 12, 2015 2:44:34 PM CET
*/

public abstract class LinkExchangeSystemPOA extends org.omg.PortableServer.Servant
 implements LinkExchangeSystemOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("register", new java.lang.Integer (0));
    _methods.put ("addLink", new java.lang.Integer (1));
    _methods.put ("linkRemove", new java.lang.Integer (2));
    _methods.put ("publishLink", new java.lang.Integer (3));
    _methods.put ("getLinks", new java.lang.Integer (4));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // LinkExchangeSystem/register
       {
         String username = in.read_string ();
         org.omg.CORBA.IntHolder userID = new org.omg.CORBA.IntHolder ();
         this.register (username, userID);
         out = $rh.createReply();
         out.write_long (userID.value);
         break;
       }

       case 1:  // LinkExchangeSystem/addLink
       {
         int userID = in.read_long ();
         String link = in.read_string ();
         org.omg.CORBA.IntHolder linkID = new org.omg.CORBA.IntHolder ();
         this.addLink (userID, link, linkID);
         out = $rh.createReply();
         out.write_long (linkID.value);
         break;
       }

       case 2:  // LinkExchangeSystem/linkRemove
       {
         int userID = in.read_long ();
         int linkID = in.read_long ();
         boolean $result = false;
         $result = this.linkRemove (userID, linkID);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 3:  // LinkExchangeSystem/publishLink
       {
         int userID = in.read_long ();
         int linkID = in.read_long ();
         boolean $result = false;
         $result = this.publishLink (userID, linkID);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 4:  // LinkExchangeSystem/getLinks
       {
         int userID = in.read_long ();
         String $result[] = null;
         $result = this.getLinks (userID);
         out = $rh.createReply();
         stringTableHelper.write (out, $result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:LinkExchangeSystem:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public LinkExchangeSystem _this() 
  {
    return LinkExchangeSystemHelper.narrow(
    super._this_object());
  }

  public LinkExchangeSystem _this(org.omg.CORBA.ORB orb) 
  {
    return LinkExchangeSystemHelper.narrow(
    super._this_object(orb));
  }


} // class LinkExchangeSystemPOA
