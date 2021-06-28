// -*- C++ -*-
// $Id$

/**
 * Code generated by the The ACE ORB (TAO) IDL Compiler v2.2a_p19
 * TAO and the TAO IDL Compiler have been developed by:
 *       Center for Distributed Object Computing
 *       Washington University
 *       St. Louis, MO
 *       USA
 *       http://www.cs.wustl.edu/~schmidt/doc-center.html
 * and
 *       Distributed Object Computing Laboratory
 *       University of California at Irvine
 *       Irvine, CA
 *       USA
 * and
 *       Institute for Software Integrated Systems
 *       Vanderbilt University
 *       Nashville, TN
 *       USA
 *       http://www.isis.vanderbilt.edu/
 *
 * Information about TAO is available at:
 *     http://www.cs.wustl.edu/~schmidt/TAO.html
 **/

// TAO_IDL - Generated from
// be/be_codegen.cpp:376


#include "DDSRecorderMessageTypeSupportC.h"
#include "tao/CDR.h"
#include "ace/OS_NS_string.h"

#if !defined (__ACE_INLINE__)
#include "DDSRecorderMessageTypeSupportC.inl"
#endif /* !defined INLINE */

// TAO_IDL - Generated from
// be/be_visitor_interface/interface_cs.cpp:51

// Traits specializations for DDSRecorderMessage::MessageTypeSupport.

DDSRecorderMessage::MessageTypeSupport_ptr
TAO::Objref_Traits<DDSRecorderMessage::MessageTypeSupport>::duplicate (
    DDSRecorderMessage::MessageTypeSupport_ptr p)
{
  return DDSRecorderMessage::MessageTypeSupport::_duplicate (p);
}

void
TAO::Objref_Traits<DDSRecorderMessage::MessageTypeSupport>::release (
    DDSRecorderMessage::MessageTypeSupport_ptr p)
{
  ::CORBA::release (p);
}

DDSRecorderMessage::MessageTypeSupport_ptr
TAO::Objref_Traits<DDSRecorderMessage::MessageTypeSupport>::nil (void)
{
  return DDSRecorderMessage::MessageTypeSupport::_nil ();
}

::CORBA::Boolean
TAO::Objref_Traits<DDSRecorderMessage::MessageTypeSupport>::marshal (
    const DDSRecorderMessage::MessageTypeSupport_ptr p,
    TAO_OutputCDR & cdr)
{
  return ::CORBA::Object::marshal (p, cdr);
}

DDSRecorderMessage::MessageTypeSupport::MessageTypeSupport (void)
{}

DDSRecorderMessage::MessageTypeSupport::~MessageTypeSupport (void)
{
}

DDSRecorderMessage::MessageTypeSupport_ptr
DDSRecorderMessage::MessageTypeSupport::_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return MessageTypeSupport::_duplicate (
      dynamic_cast<MessageTypeSupport_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::MessageTypeSupport_ptr
DDSRecorderMessage::MessageTypeSupport::_unchecked_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return MessageTypeSupport::_duplicate (
      dynamic_cast<MessageTypeSupport_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::MessageTypeSupport_ptr
DDSRecorderMessage::MessageTypeSupport::_nil (void)
{
  return 0;
}

DDSRecorderMessage::MessageTypeSupport_ptr
DDSRecorderMessage::MessageTypeSupport::_duplicate (MessageTypeSupport_ptr obj)
{
  if (! ::CORBA::is_nil (obj))
    {
      obj->_add_ref ();
    }
  return obj;
}

void
DDSRecorderMessage::MessageTypeSupport::_tao_release (MessageTypeSupport_ptr obj)
{
  ::CORBA::release (obj);
}

::CORBA::Boolean
DDSRecorderMessage::MessageTypeSupport::_is_a (const char *value)
{
  if (
      ACE_OS::strcmp (
          value,
          "IDL:DDS/TypeSupport:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:OpenDDS/DCPS/TypeSupport:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDSRecorderMessage/MessageTypeSupport:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/LocalObject:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/Object:1.0"
        ) == 0
    )
    {
      return true; // success using local knowledge
    }
  else
    {
      return false;
    }
}

const char* DDSRecorderMessage::MessageTypeSupport::_interface_repository_id (void) const
{
  return "IDL:DDSRecorderMessage/MessageTypeSupport:1.0";
}

::CORBA::Boolean
DDSRecorderMessage::MessageTypeSupport::marshal (TAO_OutputCDR & /* cdr */)
{
  return false;
}

// TAO_IDL - Generated from
// be/be_visitor_interface/interface_cs.cpp:51

// Traits specializations for DDSRecorderMessage::MessageDataWriter.

DDSRecorderMessage::MessageDataWriter_ptr
TAO::Objref_Traits<DDSRecorderMessage::MessageDataWriter>::duplicate (
    DDSRecorderMessage::MessageDataWriter_ptr p)
{
  return DDSRecorderMessage::MessageDataWriter::_duplicate (p);
}

void
TAO::Objref_Traits<DDSRecorderMessage::MessageDataWriter>::release (
    DDSRecorderMessage::MessageDataWriter_ptr p)
{
  ::CORBA::release (p);
}

DDSRecorderMessage::MessageDataWriter_ptr
TAO::Objref_Traits<DDSRecorderMessage::MessageDataWriter>::nil (void)
{
  return DDSRecorderMessage::MessageDataWriter::_nil ();
}

::CORBA::Boolean
TAO::Objref_Traits<DDSRecorderMessage::MessageDataWriter>::marshal (
    const DDSRecorderMessage::MessageDataWriter_ptr p,
    TAO_OutputCDR & cdr)
{
  return ::CORBA::Object::marshal (p, cdr);
}

DDSRecorderMessage::MessageDataWriter::MessageDataWriter (void)
{}

DDSRecorderMessage::MessageDataWriter::~MessageDataWriter (void)
{
}

DDSRecorderMessage::MessageDataWriter_ptr
DDSRecorderMessage::MessageDataWriter::_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return MessageDataWriter::_duplicate (
      dynamic_cast<MessageDataWriter_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::MessageDataWriter_ptr
DDSRecorderMessage::MessageDataWriter::_unchecked_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return MessageDataWriter::_duplicate (
      dynamic_cast<MessageDataWriter_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::MessageDataWriter_ptr
DDSRecorderMessage::MessageDataWriter::_nil (void)
{
  return 0;
}

DDSRecorderMessage::MessageDataWriter_ptr
DDSRecorderMessage::MessageDataWriter::_duplicate (MessageDataWriter_ptr obj)
{
  if (! ::CORBA::is_nil (obj))
    {
      obj->_add_ref ();
    }
  return obj;
}

void
DDSRecorderMessage::MessageDataWriter::_tao_release (MessageDataWriter_ptr obj)
{
  ::CORBA::release (obj);
}

::CORBA::Boolean
DDSRecorderMessage::MessageDataWriter::_is_a (const char *value)
{
  if (
      ACE_OS::strcmp (
          value,
          "IDL:DDS/Entity:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDS/DataWriter:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDSRecorderMessage/MessageDataWriter:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/LocalObject:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/Object:1.0"
        ) == 0
    )
    {
      return true; // success using local knowledge
    }
  else
    {
      return false;
    }
}

const char* DDSRecorderMessage::MessageDataWriter::_interface_repository_id (void) const
{
  return "IDL:DDSRecorderMessage/MessageDataWriter:1.0";
}

::CORBA::Boolean
DDSRecorderMessage::MessageDataWriter::marshal (TAO_OutputCDR & /* cdr */)
{
  return false;
}

// TAO_IDL - Generated from
// be/be_visitor_interface/interface_cs.cpp:51

// Traits specializations for DDSRecorderMessage::MessageDataReader.

DDSRecorderMessage::MessageDataReader_ptr
TAO::Objref_Traits<DDSRecorderMessage::MessageDataReader>::duplicate (
    DDSRecorderMessage::MessageDataReader_ptr p)
{
  return DDSRecorderMessage::MessageDataReader::_duplicate (p);
}

void
TAO::Objref_Traits<DDSRecorderMessage::MessageDataReader>::release (
    DDSRecorderMessage::MessageDataReader_ptr p)
{
  ::CORBA::release (p);
}

DDSRecorderMessage::MessageDataReader_ptr
TAO::Objref_Traits<DDSRecorderMessage::MessageDataReader>::nil (void)
{
  return DDSRecorderMessage::MessageDataReader::_nil ();
}

::CORBA::Boolean
TAO::Objref_Traits<DDSRecorderMessage::MessageDataReader>::marshal (
    const DDSRecorderMessage::MessageDataReader_ptr p,
    TAO_OutputCDR & cdr)
{
  return ::CORBA::Object::marshal (p, cdr);
}

DDSRecorderMessage::MessageDataReader::MessageDataReader (void)
{}

DDSRecorderMessage::MessageDataReader::~MessageDataReader (void)
{
}

DDSRecorderMessage::MessageDataReader_ptr
DDSRecorderMessage::MessageDataReader::_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return MessageDataReader::_duplicate (
      dynamic_cast<MessageDataReader_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::MessageDataReader_ptr
DDSRecorderMessage::MessageDataReader::_unchecked_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return MessageDataReader::_duplicate (
      dynamic_cast<MessageDataReader_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::MessageDataReader_ptr
DDSRecorderMessage::MessageDataReader::_nil (void)
{
  return 0;
}

DDSRecorderMessage::MessageDataReader_ptr
DDSRecorderMessage::MessageDataReader::_duplicate (MessageDataReader_ptr obj)
{
  if (! ::CORBA::is_nil (obj))
    {
      obj->_add_ref ();
    }
  return obj;
}

void
DDSRecorderMessage::MessageDataReader::_tao_release (MessageDataReader_ptr obj)
{
  ::CORBA::release (obj);
}

::CORBA::Boolean
DDSRecorderMessage::MessageDataReader::_is_a (const char *value)
{
  if (
      ACE_OS::strcmp (
          value,
          "IDL:DDS/Entity:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDS/DataReader:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:OpenDDS/DCPS/DataReaderEx:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDSRecorderMessage/MessageDataReader:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/LocalObject:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/Object:1.0"
        ) == 0
    )
    {
      return true; // success using local knowledge
    }
  else
    {
      return false;
    }
}

const char* DDSRecorderMessage::MessageDataReader::_interface_repository_id (void) const
{
  return "IDL:DDSRecorderMessage/MessageDataReader:1.0";
}

::CORBA::Boolean
DDSRecorderMessage::MessageDataReader::marshal (TAO_OutputCDR & /* cdr */)
{
  return false;
}

// TAO_IDL - Generated from
// be/be_visitor_interface/interface_cs.cpp:51

// Traits specializations for DDSRecorderMessage::CommandTypeSupport.

DDSRecorderMessage::CommandTypeSupport_ptr
TAO::Objref_Traits<DDSRecorderMessage::CommandTypeSupport>::duplicate (
    DDSRecorderMessage::CommandTypeSupport_ptr p)
{
  return DDSRecorderMessage::CommandTypeSupport::_duplicate (p);
}

void
TAO::Objref_Traits<DDSRecorderMessage::CommandTypeSupport>::release (
    DDSRecorderMessage::CommandTypeSupport_ptr p)
{
  ::CORBA::release (p);
}

DDSRecorderMessage::CommandTypeSupport_ptr
TAO::Objref_Traits<DDSRecorderMessage::CommandTypeSupport>::nil (void)
{
  return DDSRecorderMessage::CommandTypeSupport::_nil ();
}

::CORBA::Boolean
TAO::Objref_Traits<DDSRecorderMessage::CommandTypeSupport>::marshal (
    const DDSRecorderMessage::CommandTypeSupport_ptr p,
    TAO_OutputCDR & cdr)
{
  return ::CORBA::Object::marshal (p, cdr);
}

DDSRecorderMessage::CommandTypeSupport::CommandTypeSupport (void)
{}

DDSRecorderMessage::CommandTypeSupport::~CommandTypeSupport (void)
{
}

DDSRecorderMessage::CommandTypeSupport_ptr
DDSRecorderMessage::CommandTypeSupport::_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return CommandTypeSupport::_duplicate (
      dynamic_cast<CommandTypeSupport_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::CommandTypeSupport_ptr
DDSRecorderMessage::CommandTypeSupport::_unchecked_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return CommandTypeSupport::_duplicate (
      dynamic_cast<CommandTypeSupport_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::CommandTypeSupport_ptr
DDSRecorderMessage::CommandTypeSupport::_nil (void)
{
  return 0;
}

DDSRecorderMessage::CommandTypeSupport_ptr
DDSRecorderMessage::CommandTypeSupport::_duplicate (CommandTypeSupport_ptr obj)
{
  if (! ::CORBA::is_nil (obj))
    {
      obj->_add_ref ();
    }
  return obj;
}

void
DDSRecorderMessage::CommandTypeSupport::_tao_release (CommandTypeSupport_ptr obj)
{
  ::CORBA::release (obj);
}

::CORBA::Boolean
DDSRecorderMessage::CommandTypeSupport::_is_a (const char *value)
{
  if (
      ACE_OS::strcmp (
          value,
          "IDL:DDS/TypeSupport:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:OpenDDS/DCPS/TypeSupport:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDSRecorderMessage/CommandTypeSupport:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/LocalObject:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/Object:1.0"
        ) == 0
    )
    {
      return true; // success using local knowledge
    }
  else
    {
      return false;
    }
}

const char* DDSRecorderMessage::CommandTypeSupport::_interface_repository_id (void) const
{
  return "IDL:DDSRecorderMessage/CommandTypeSupport:1.0";
}

::CORBA::Boolean
DDSRecorderMessage::CommandTypeSupport::marshal (TAO_OutputCDR & /* cdr */)
{
  return false;
}

// TAO_IDL - Generated from
// be/be_visitor_interface/interface_cs.cpp:51

// Traits specializations for DDSRecorderMessage::CommandDataWriter.

DDSRecorderMessage::CommandDataWriter_ptr
TAO::Objref_Traits<DDSRecorderMessage::CommandDataWriter>::duplicate (
    DDSRecorderMessage::CommandDataWriter_ptr p)
{
  return DDSRecorderMessage::CommandDataWriter::_duplicate (p);
}

void
TAO::Objref_Traits<DDSRecorderMessage::CommandDataWriter>::release (
    DDSRecorderMessage::CommandDataWriter_ptr p)
{
  ::CORBA::release (p);
}

DDSRecorderMessage::CommandDataWriter_ptr
TAO::Objref_Traits<DDSRecorderMessage::CommandDataWriter>::nil (void)
{
  return DDSRecorderMessage::CommandDataWriter::_nil ();
}

::CORBA::Boolean
TAO::Objref_Traits<DDSRecorderMessage::CommandDataWriter>::marshal (
    const DDSRecorderMessage::CommandDataWriter_ptr p,
    TAO_OutputCDR & cdr)
{
  return ::CORBA::Object::marshal (p, cdr);
}

DDSRecorderMessage::CommandDataWriter::CommandDataWriter (void)
{}

DDSRecorderMessage::CommandDataWriter::~CommandDataWriter (void)
{
}

DDSRecorderMessage::CommandDataWriter_ptr
DDSRecorderMessage::CommandDataWriter::_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return CommandDataWriter::_duplicate (
      dynamic_cast<CommandDataWriter_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::CommandDataWriter_ptr
DDSRecorderMessage::CommandDataWriter::_unchecked_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return CommandDataWriter::_duplicate (
      dynamic_cast<CommandDataWriter_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::CommandDataWriter_ptr
DDSRecorderMessage::CommandDataWriter::_nil (void)
{
  return 0;
}

DDSRecorderMessage::CommandDataWriter_ptr
DDSRecorderMessage::CommandDataWriter::_duplicate (CommandDataWriter_ptr obj)
{
  if (! ::CORBA::is_nil (obj))
    {
      obj->_add_ref ();
    }
  return obj;
}

void
DDSRecorderMessage::CommandDataWriter::_tao_release (CommandDataWriter_ptr obj)
{
  ::CORBA::release (obj);
}

::CORBA::Boolean
DDSRecorderMessage::CommandDataWriter::_is_a (const char *value)
{
  if (
      ACE_OS::strcmp (
          value,
          "IDL:DDS/Entity:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDS/DataWriter:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDSRecorderMessage/CommandDataWriter:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/LocalObject:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/Object:1.0"
        ) == 0
    )
    {
      return true; // success using local knowledge
    }
  else
    {
      return false;
    }
}

const char* DDSRecorderMessage::CommandDataWriter::_interface_repository_id (void) const
{
  return "IDL:DDSRecorderMessage/CommandDataWriter:1.0";
}

::CORBA::Boolean
DDSRecorderMessage::CommandDataWriter::marshal (TAO_OutputCDR & /* cdr */)
{
  return false;
}

// TAO_IDL - Generated from
// be/be_visitor_interface/interface_cs.cpp:51

// Traits specializations for DDSRecorderMessage::CommandDataReader.

DDSRecorderMessage::CommandDataReader_ptr
TAO::Objref_Traits<DDSRecorderMessage::CommandDataReader>::duplicate (
    DDSRecorderMessage::CommandDataReader_ptr p)
{
  return DDSRecorderMessage::CommandDataReader::_duplicate (p);
}

void
TAO::Objref_Traits<DDSRecorderMessage::CommandDataReader>::release (
    DDSRecorderMessage::CommandDataReader_ptr p)
{
  ::CORBA::release (p);
}

DDSRecorderMessage::CommandDataReader_ptr
TAO::Objref_Traits<DDSRecorderMessage::CommandDataReader>::nil (void)
{
  return DDSRecorderMessage::CommandDataReader::_nil ();
}

::CORBA::Boolean
TAO::Objref_Traits<DDSRecorderMessage::CommandDataReader>::marshal (
    const DDSRecorderMessage::CommandDataReader_ptr p,
    TAO_OutputCDR & cdr)
{
  return ::CORBA::Object::marshal (p, cdr);
}

DDSRecorderMessage::CommandDataReader::CommandDataReader (void)
{}

DDSRecorderMessage::CommandDataReader::~CommandDataReader (void)
{
}

DDSRecorderMessage::CommandDataReader_ptr
DDSRecorderMessage::CommandDataReader::_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return CommandDataReader::_duplicate (
      dynamic_cast<CommandDataReader_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::CommandDataReader_ptr
DDSRecorderMessage::CommandDataReader::_unchecked_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return CommandDataReader::_duplicate (
      dynamic_cast<CommandDataReader_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::CommandDataReader_ptr
DDSRecorderMessage::CommandDataReader::_nil (void)
{
  return 0;
}

DDSRecorderMessage::CommandDataReader_ptr
DDSRecorderMessage::CommandDataReader::_duplicate (CommandDataReader_ptr obj)
{
  if (! ::CORBA::is_nil (obj))
    {
      obj->_add_ref ();
    }
  return obj;
}

void
DDSRecorderMessage::CommandDataReader::_tao_release (CommandDataReader_ptr obj)
{
  ::CORBA::release (obj);
}

::CORBA::Boolean
DDSRecorderMessage::CommandDataReader::_is_a (const char *value)
{
  if (
      ACE_OS::strcmp (
          value,
          "IDL:DDS/Entity:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDS/DataReader:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:OpenDDS/DCPS/DataReaderEx:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDSRecorderMessage/CommandDataReader:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/LocalObject:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/Object:1.0"
        ) == 0
    )
    {
      return true; // success using local knowledge
    }
  else
    {
      return false;
    }
}

const char* DDSRecorderMessage::CommandDataReader::_interface_repository_id (void) const
{
  return "IDL:DDSRecorderMessage/CommandDataReader:1.0";
}

::CORBA::Boolean
DDSRecorderMessage::CommandDataReader::marshal (TAO_OutputCDR & /* cdr */)
{
  return false;
}

// TAO_IDL - Generated from
// be/be_visitor_interface/interface_cs.cpp:51

// Traits specializations for DDSRecorderMessage::CommandReplyTypeSupport.

DDSRecorderMessage::CommandReplyTypeSupport_ptr
TAO::Objref_Traits<DDSRecorderMessage::CommandReplyTypeSupport>::duplicate (
    DDSRecorderMessage::CommandReplyTypeSupport_ptr p)
{
  return DDSRecorderMessage::CommandReplyTypeSupport::_duplicate (p);
}

void
TAO::Objref_Traits<DDSRecorderMessage::CommandReplyTypeSupport>::release (
    DDSRecorderMessage::CommandReplyTypeSupport_ptr p)
{
  ::CORBA::release (p);
}

DDSRecorderMessage::CommandReplyTypeSupport_ptr
TAO::Objref_Traits<DDSRecorderMessage::CommandReplyTypeSupport>::nil (void)
{
  return DDSRecorderMessage::CommandReplyTypeSupport::_nil ();
}

::CORBA::Boolean
TAO::Objref_Traits<DDSRecorderMessage::CommandReplyTypeSupport>::marshal (
    const DDSRecorderMessage::CommandReplyTypeSupport_ptr p,
    TAO_OutputCDR & cdr)
{
  return ::CORBA::Object::marshal (p, cdr);
}

DDSRecorderMessage::CommandReplyTypeSupport::CommandReplyTypeSupport (void)
{}

DDSRecorderMessage::CommandReplyTypeSupport::~CommandReplyTypeSupport (void)
{
}

DDSRecorderMessage::CommandReplyTypeSupport_ptr
DDSRecorderMessage::CommandReplyTypeSupport::_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return CommandReplyTypeSupport::_duplicate (
      dynamic_cast<CommandReplyTypeSupport_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::CommandReplyTypeSupport_ptr
DDSRecorderMessage::CommandReplyTypeSupport::_unchecked_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return CommandReplyTypeSupport::_duplicate (
      dynamic_cast<CommandReplyTypeSupport_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::CommandReplyTypeSupport_ptr
DDSRecorderMessage::CommandReplyTypeSupport::_nil (void)
{
  return 0;
}

DDSRecorderMessage::CommandReplyTypeSupport_ptr
DDSRecorderMessage::CommandReplyTypeSupport::_duplicate (CommandReplyTypeSupport_ptr obj)
{
  if (! ::CORBA::is_nil (obj))
    {
      obj->_add_ref ();
    }
  return obj;
}

void
DDSRecorderMessage::CommandReplyTypeSupport::_tao_release (CommandReplyTypeSupport_ptr obj)
{
  ::CORBA::release (obj);
}

::CORBA::Boolean
DDSRecorderMessage::CommandReplyTypeSupport::_is_a (const char *value)
{
  if (
      ACE_OS::strcmp (
          value,
          "IDL:DDS/TypeSupport:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:OpenDDS/DCPS/TypeSupport:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDSRecorderMessage/CommandReplyTypeSupport:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/LocalObject:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/Object:1.0"
        ) == 0
    )
    {
      return true; // success using local knowledge
    }
  else
    {
      return false;
    }
}

const char* DDSRecorderMessage::CommandReplyTypeSupport::_interface_repository_id (void) const
{
  return "IDL:DDSRecorderMessage/CommandReplyTypeSupport:1.0";
}

::CORBA::Boolean
DDSRecorderMessage::CommandReplyTypeSupport::marshal (TAO_OutputCDR & /* cdr */)
{
  return false;
}

// TAO_IDL - Generated from
// be/be_visitor_interface/interface_cs.cpp:51

// Traits specializations for DDSRecorderMessage::CommandReplyDataWriter.

DDSRecorderMessage::CommandReplyDataWriter_ptr
TAO::Objref_Traits<DDSRecorderMessage::CommandReplyDataWriter>::duplicate (
    DDSRecorderMessage::CommandReplyDataWriter_ptr p)
{
  return DDSRecorderMessage::CommandReplyDataWriter::_duplicate (p);
}

void
TAO::Objref_Traits<DDSRecorderMessage::CommandReplyDataWriter>::release (
    DDSRecorderMessage::CommandReplyDataWriter_ptr p)
{
  ::CORBA::release (p);
}

DDSRecorderMessage::CommandReplyDataWriter_ptr
TAO::Objref_Traits<DDSRecorderMessage::CommandReplyDataWriter>::nil (void)
{
  return DDSRecorderMessage::CommandReplyDataWriter::_nil ();
}

::CORBA::Boolean
TAO::Objref_Traits<DDSRecorderMessage::CommandReplyDataWriter>::marshal (
    const DDSRecorderMessage::CommandReplyDataWriter_ptr p,
    TAO_OutputCDR & cdr)
{
  return ::CORBA::Object::marshal (p, cdr);
}

DDSRecorderMessage::CommandReplyDataWriter::CommandReplyDataWriter (void)
{}

DDSRecorderMessage::CommandReplyDataWriter::~CommandReplyDataWriter (void)
{
}

DDSRecorderMessage::CommandReplyDataWriter_ptr
DDSRecorderMessage::CommandReplyDataWriter::_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return CommandReplyDataWriter::_duplicate (
      dynamic_cast<CommandReplyDataWriter_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::CommandReplyDataWriter_ptr
DDSRecorderMessage::CommandReplyDataWriter::_unchecked_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return CommandReplyDataWriter::_duplicate (
      dynamic_cast<CommandReplyDataWriter_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::CommandReplyDataWriter_ptr
DDSRecorderMessage::CommandReplyDataWriter::_nil (void)
{
  return 0;
}

DDSRecorderMessage::CommandReplyDataWriter_ptr
DDSRecorderMessage::CommandReplyDataWriter::_duplicate (CommandReplyDataWriter_ptr obj)
{
  if (! ::CORBA::is_nil (obj))
    {
      obj->_add_ref ();
    }
  return obj;
}

void
DDSRecorderMessage::CommandReplyDataWriter::_tao_release (CommandReplyDataWriter_ptr obj)
{
  ::CORBA::release (obj);
}

::CORBA::Boolean
DDSRecorderMessage::CommandReplyDataWriter::_is_a (const char *value)
{
  if (
      ACE_OS::strcmp (
          value,
          "IDL:DDS/Entity:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDS/DataWriter:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDSRecorderMessage/CommandReplyDataWriter:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/LocalObject:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/Object:1.0"
        ) == 0
    )
    {
      return true; // success using local knowledge
    }
  else
    {
      return false;
    }
}

const char* DDSRecorderMessage::CommandReplyDataWriter::_interface_repository_id (void) const
{
  return "IDL:DDSRecorderMessage/CommandReplyDataWriter:1.0";
}

::CORBA::Boolean
DDSRecorderMessage::CommandReplyDataWriter::marshal (TAO_OutputCDR & /* cdr */)
{
  return false;
}

// TAO_IDL - Generated from
// be/be_visitor_interface/interface_cs.cpp:51

// Traits specializations for DDSRecorderMessage::CommandReplyDataReader.

DDSRecorderMessage::CommandReplyDataReader_ptr
TAO::Objref_Traits<DDSRecorderMessage::CommandReplyDataReader>::duplicate (
    DDSRecorderMessage::CommandReplyDataReader_ptr p)
{
  return DDSRecorderMessage::CommandReplyDataReader::_duplicate (p);
}

void
TAO::Objref_Traits<DDSRecorderMessage::CommandReplyDataReader>::release (
    DDSRecorderMessage::CommandReplyDataReader_ptr p)
{
  ::CORBA::release (p);
}

DDSRecorderMessage::CommandReplyDataReader_ptr
TAO::Objref_Traits<DDSRecorderMessage::CommandReplyDataReader>::nil (void)
{
  return DDSRecorderMessage::CommandReplyDataReader::_nil ();
}

::CORBA::Boolean
TAO::Objref_Traits<DDSRecorderMessage::CommandReplyDataReader>::marshal (
    const DDSRecorderMessage::CommandReplyDataReader_ptr p,
    TAO_OutputCDR & cdr)
{
  return ::CORBA::Object::marshal (p, cdr);
}

DDSRecorderMessage::CommandReplyDataReader::CommandReplyDataReader (void)
{}

DDSRecorderMessage::CommandReplyDataReader::~CommandReplyDataReader (void)
{
}

DDSRecorderMessage::CommandReplyDataReader_ptr
DDSRecorderMessage::CommandReplyDataReader::_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return CommandReplyDataReader::_duplicate (
      dynamic_cast<CommandReplyDataReader_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::CommandReplyDataReader_ptr
DDSRecorderMessage::CommandReplyDataReader::_unchecked_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return CommandReplyDataReader::_duplicate (
      dynamic_cast<CommandReplyDataReader_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::CommandReplyDataReader_ptr
DDSRecorderMessage::CommandReplyDataReader::_nil (void)
{
  return 0;
}

DDSRecorderMessage::CommandReplyDataReader_ptr
DDSRecorderMessage::CommandReplyDataReader::_duplicate (CommandReplyDataReader_ptr obj)
{
  if (! ::CORBA::is_nil (obj))
    {
      obj->_add_ref ();
    }
  return obj;
}

void
DDSRecorderMessage::CommandReplyDataReader::_tao_release (CommandReplyDataReader_ptr obj)
{
  ::CORBA::release (obj);
}

::CORBA::Boolean
DDSRecorderMessage::CommandReplyDataReader::_is_a (const char *value)
{
  if (
      ACE_OS::strcmp (
          value,
          "IDL:DDS/Entity:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDS/DataReader:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:OpenDDS/DCPS/DataReaderEx:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDSRecorderMessage/CommandReplyDataReader:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/LocalObject:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/Object:1.0"
        ) == 0
    )
    {
      return true; // success using local knowledge
    }
  else
    {
      return false;
    }
}

const char* DDSRecorderMessage::CommandReplyDataReader::_interface_repository_id (void) const
{
  return "IDL:DDSRecorderMessage/CommandReplyDataReader:1.0";
}

::CORBA::Boolean
DDSRecorderMessage::CommandReplyDataReader::marshal (TAO_OutputCDR & /* cdr */)
{
  return false;
}

// TAO_IDL - Generated from
// be/be_visitor_interface/interface_cs.cpp:51

// Traits specializations for DDSRecorderMessage::AcknowledgementTypeSupport.

DDSRecorderMessage::AcknowledgementTypeSupport_ptr
TAO::Objref_Traits<DDSRecorderMessage::AcknowledgementTypeSupport>::duplicate (
    DDSRecorderMessage::AcknowledgementTypeSupport_ptr p)
{
  return DDSRecorderMessage::AcknowledgementTypeSupport::_duplicate (p);
}

void
TAO::Objref_Traits<DDSRecorderMessage::AcknowledgementTypeSupport>::release (
    DDSRecorderMessage::AcknowledgementTypeSupport_ptr p)
{
  ::CORBA::release (p);
}

DDSRecorderMessage::AcknowledgementTypeSupport_ptr
TAO::Objref_Traits<DDSRecorderMessage::AcknowledgementTypeSupport>::nil (void)
{
  return DDSRecorderMessage::AcknowledgementTypeSupport::_nil ();
}

::CORBA::Boolean
TAO::Objref_Traits<DDSRecorderMessage::AcknowledgementTypeSupport>::marshal (
    const DDSRecorderMessage::AcknowledgementTypeSupport_ptr p,
    TAO_OutputCDR & cdr)
{
  return ::CORBA::Object::marshal (p, cdr);
}

DDSRecorderMessage::AcknowledgementTypeSupport::AcknowledgementTypeSupport (void)
{}

DDSRecorderMessage::AcknowledgementTypeSupport::~AcknowledgementTypeSupport (void)
{
}

DDSRecorderMessage::AcknowledgementTypeSupport_ptr
DDSRecorderMessage::AcknowledgementTypeSupport::_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return AcknowledgementTypeSupport::_duplicate (
      dynamic_cast<AcknowledgementTypeSupport_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::AcknowledgementTypeSupport_ptr
DDSRecorderMessage::AcknowledgementTypeSupport::_unchecked_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return AcknowledgementTypeSupport::_duplicate (
      dynamic_cast<AcknowledgementTypeSupport_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::AcknowledgementTypeSupport_ptr
DDSRecorderMessage::AcknowledgementTypeSupport::_nil (void)
{
  return 0;
}

DDSRecorderMessage::AcknowledgementTypeSupport_ptr
DDSRecorderMessage::AcknowledgementTypeSupport::_duplicate (AcknowledgementTypeSupport_ptr obj)
{
  if (! ::CORBA::is_nil (obj))
    {
      obj->_add_ref ();
    }
  return obj;
}

void
DDSRecorderMessage::AcknowledgementTypeSupport::_tao_release (AcknowledgementTypeSupport_ptr obj)
{
  ::CORBA::release (obj);
}

::CORBA::Boolean
DDSRecorderMessage::AcknowledgementTypeSupport::_is_a (const char *value)
{
  if (
      ACE_OS::strcmp (
          value,
          "IDL:DDS/TypeSupport:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:OpenDDS/DCPS/TypeSupport:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDSRecorderMessage/AcknowledgementTypeSupport:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/LocalObject:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/Object:1.0"
        ) == 0
    )
    {
      return true; // success using local knowledge
    }
  else
    {
      return false;
    }
}

const char* DDSRecorderMessage::AcknowledgementTypeSupport::_interface_repository_id (void) const
{
  return "IDL:DDSRecorderMessage/AcknowledgementTypeSupport:1.0";
}

::CORBA::Boolean
DDSRecorderMessage::AcknowledgementTypeSupport::marshal (TAO_OutputCDR & /* cdr */)
{
  return false;
}

// TAO_IDL - Generated from
// be/be_visitor_interface/interface_cs.cpp:51

// Traits specializations for DDSRecorderMessage::AcknowledgementDataWriter.

DDSRecorderMessage::AcknowledgementDataWriter_ptr
TAO::Objref_Traits<DDSRecorderMessage::AcknowledgementDataWriter>::duplicate (
    DDSRecorderMessage::AcknowledgementDataWriter_ptr p)
{
  return DDSRecorderMessage::AcknowledgementDataWriter::_duplicate (p);
}

void
TAO::Objref_Traits<DDSRecorderMessage::AcknowledgementDataWriter>::release (
    DDSRecorderMessage::AcknowledgementDataWriter_ptr p)
{
  ::CORBA::release (p);
}

DDSRecorderMessage::AcknowledgementDataWriter_ptr
TAO::Objref_Traits<DDSRecorderMessage::AcknowledgementDataWriter>::nil (void)
{
  return DDSRecorderMessage::AcknowledgementDataWriter::_nil ();
}

::CORBA::Boolean
TAO::Objref_Traits<DDSRecorderMessage::AcknowledgementDataWriter>::marshal (
    const DDSRecorderMessage::AcknowledgementDataWriter_ptr p,
    TAO_OutputCDR & cdr)
{
  return ::CORBA::Object::marshal (p, cdr);
}

DDSRecorderMessage::AcknowledgementDataWriter::AcknowledgementDataWriter (void)
{}

DDSRecorderMessage::AcknowledgementDataWriter::~AcknowledgementDataWriter (void)
{
}

DDSRecorderMessage::AcknowledgementDataWriter_ptr
DDSRecorderMessage::AcknowledgementDataWriter::_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return AcknowledgementDataWriter::_duplicate (
      dynamic_cast<AcknowledgementDataWriter_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::AcknowledgementDataWriter_ptr
DDSRecorderMessage::AcknowledgementDataWriter::_unchecked_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return AcknowledgementDataWriter::_duplicate (
      dynamic_cast<AcknowledgementDataWriter_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::AcknowledgementDataWriter_ptr
DDSRecorderMessage::AcknowledgementDataWriter::_nil (void)
{
  return 0;
}

DDSRecorderMessage::AcknowledgementDataWriter_ptr
DDSRecorderMessage::AcknowledgementDataWriter::_duplicate (AcknowledgementDataWriter_ptr obj)
{
  if (! ::CORBA::is_nil (obj))
    {
      obj->_add_ref ();
    }
  return obj;
}

void
DDSRecorderMessage::AcknowledgementDataWriter::_tao_release (AcknowledgementDataWriter_ptr obj)
{
  ::CORBA::release (obj);
}

::CORBA::Boolean
DDSRecorderMessage::AcknowledgementDataWriter::_is_a (const char *value)
{
  if (
      ACE_OS::strcmp (
          value,
          "IDL:DDS/Entity:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDS/DataWriter:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDSRecorderMessage/AcknowledgementDataWriter:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/LocalObject:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/Object:1.0"
        ) == 0
    )
    {
      return true; // success using local knowledge
    }
  else
    {
      return false;
    }
}

const char* DDSRecorderMessage::AcknowledgementDataWriter::_interface_repository_id (void) const
{
  return "IDL:DDSRecorderMessage/AcknowledgementDataWriter:1.0";
}

::CORBA::Boolean
DDSRecorderMessage::AcknowledgementDataWriter::marshal (TAO_OutputCDR & /* cdr */)
{
  return false;
}

// TAO_IDL - Generated from
// be/be_visitor_interface/interface_cs.cpp:51

// Traits specializations for DDSRecorderMessage::AcknowledgementDataReader.

DDSRecorderMessage::AcknowledgementDataReader_ptr
TAO::Objref_Traits<DDSRecorderMessage::AcknowledgementDataReader>::duplicate (
    DDSRecorderMessage::AcknowledgementDataReader_ptr p)
{
  return DDSRecorderMessage::AcknowledgementDataReader::_duplicate (p);
}

void
TAO::Objref_Traits<DDSRecorderMessage::AcknowledgementDataReader>::release (
    DDSRecorderMessage::AcknowledgementDataReader_ptr p)
{
  ::CORBA::release (p);
}

DDSRecorderMessage::AcknowledgementDataReader_ptr
TAO::Objref_Traits<DDSRecorderMessage::AcknowledgementDataReader>::nil (void)
{
  return DDSRecorderMessage::AcknowledgementDataReader::_nil ();
}

::CORBA::Boolean
TAO::Objref_Traits<DDSRecorderMessage::AcknowledgementDataReader>::marshal (
    const DDSRecorderMessage::AcknowledgementDataReader_ptr p,
    TAO_OutputCDR & cdr)
{
  return ::CORBA::Object::marshal (p, cdr);
}

DDSRecorderMessage::AcknowledgementDataReader::AcknowledgementDataReader (void)
{}

DDSRecorderMessage::AcknowledgementDataReader::~AcknowledgementDataReader (void)
{
}

DDSRecorderMessage::AcknowledgementDataReader_ptr
DDSRecorderMessage::AcknowledgementDataReader::_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return AcknowledgementDataReader::_duplicate (
      dynamic_cast<AcknowledgementDataReader_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::AcknowledgementDataReader_ptr
DDSRecorderMessage::AcknowledgementDataReader::_unchecked_narrow (
    ::CORBA::Object_ptr _tao_objref)
{
  return AcknowledgementDataReader::_duplicate (
      dynamic_cast<AcknowledgementDataReader_ptr> (_tao_objref)
    );
}

DDSRecorderMessage::AcknowledgementDataReader_ptr
DDSRecorderMessage::AcknowledgementDataReader::_nil (void)
{
  return 0;
}

DDSRecorderMessage::AcknowledgementDataReader_ptr
DDSRecorderMessage::AcknowledgementDataReader::_duplicate (AcknowledgementDataReader_ptr obj)
{
  if (! ::CORBA::is_nil (obj))
    {
      obj->_add_ref ();
    }
  return obj;
}

void
DDSRecorderMessage::AcknowledgementDataReader::_tao_release (AcknowledgementDataReader_ptr obj)
{
  ::CORBA::release (obj);
}

::CORBA::Boolean
DDSRecorderMessage::AcknowledgementDataReader::_is_a (const char *value)
{
  if (
      ACE_OS::strcmp (
          value,
          "IDL:DDS/Entity:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDS/DataReader:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:OpenDDS/DCPS/DataReaderEx:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:DDSRecorderMessage/AcknowledgementDataReader:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/LocalObject:1.0"
        ) == 0 ||
      ACE_OS::strcmp (
          value,
          "IDL:omg.org/CORBA/Object:1.0"
        ) == 0
    )
    {
      return true; // success using local knowledge
    }
  else
    {
      return false;
    }
}

const char* DDSRecorderMessage::AcknowledgementDataReader::_interface_repository_id (void) const
{
  return "IDL:DDSRecorderMessage/AcknowledgementDataReader:1.0";
}

::CORBA::Boolean
DDSRecorderMessage::AcknowledgementDataReader::marshal (TAO_OutputCDR & /* cdr */)
{
  return false;
}
