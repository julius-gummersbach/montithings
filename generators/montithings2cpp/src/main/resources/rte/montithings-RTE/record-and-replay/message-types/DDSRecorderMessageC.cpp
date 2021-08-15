// (c) https://github.com/MontiCore/monticore

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


#include "DDSRecorderMessageC.h"
#include "tao/CDR.h"

#if !defined (__ACE_INLINE__)
#include "DDSRecorderMessageC.inl"
#endif /* !defined INLINE */

// TAO_IDL - Generated from
// be/be_visitor_enum/cdr_op_cs.cpp:37


TAO_BEGIN_VERSIONED_NAMESPACE_DECL

::CORBA::Boolean operator<< (TAO_OutputCDR &strm, DDSRecorderMessage::MessageType _tao_enumerator)
{
  return strm << static_cast< ::CORBA::ULong> (_tao_enumerator);
}

::CORBA::Boolean operator>> (TAO_InputCDR &strm, DDSRecorderMessage::MessageType & _tao_enumerator)
{
  ::CORBA::ULong _tao_temp = 0;
  ::CORBA::Boolean const _tao_success = strm >> _tao_temp;
  
  if (_tao_success)
    {
      _tao_enumerator = static_cast<DDSRecorderMessage::MessageType> (_tao_temp);
    }
  
  return _tao_success;
}

TAO_END_VERSIONED_NAMESPACE_DECL



// TAO_IDL - Generated from
// be/be_visitor_structure/cdr_op_cs.cpp:52

TAO_BEGIN_VERSIONED_NAMESPACE_DECL

::CORBA::Boolean operator<< (
    TAO_OutputCDR &strm,
    const DDSRecorderMessage::Message &_tao_aggregate)
{
  return
    (strm << _tao_aggregate.instance_name.in ()) &&
    (strm << _tao_aggregate.id) &&
    (strm << _tao_aggregate.type) &&
    (strm << _tao_aggregate.timestamp) &&
    (strm << _tao_aggregate.serialized_vector_clock.in ()) &&
    (strm << _tao_aggregate.topic.in ()) &&
    (strm << _tao_aggregate.msg_id) &&
    (strm << _tao_aggregate.msg_content.in ()) &&
    (strm << _tao_aggregate.message_delays.in ());
}

::CORBA::Boolean operator>> (
    TAO_InputCDR &strm,
    DDSRecorderMessage::Message &_tao_aggregate)
{
  return
    (strm >> _tao_aggregate.instance_name.out ()) &&
    (strm >> _tao_aggregate.id) &&
    (strm >> _tao_aggregate.type) &&
    (strm >> _tao_aggregate.timestamp) &&
    (strm >> _tao_aggregate.serialized_vector_clock.out ()) &&
    (strm >> _tao_aggregate.topic.out ()) &&
    (strm >> _tao_aggregate.msg_id) &&
    (strm >> _tao_aggregate.msg_content.out ()) &&
    (strm >> _tao_aggregate.message_delays.out ());
}

TAO_END_VERSIONED_NAMESPACE_DECL



// TAO_IDL - Generated from
// be/be_visitor_enum/cdr_op_cs.cpp:37


TAO_BEGIN_VERSIONED_NAMESPACE_DECL

::CORBA::Boolean operator<< (TAO_OutputCDR &strm, DDSRecorderMessage::CommandType _tao_enumerator)
{
  return strm << static_cast< ::CORBA::ULong> (_tao_enumerator);
}

::CORBA::Boolean operator>> (TAO_InputCDR &strm, DDSRecorderMessage::CommandType & _tao_enumerator)
{
  ::CORBA::ULong _tao_temp = 0;
  ::CORBA::Boolean const _tao_success = strm >> _tao_temp;
  
  if (_tao_success)
    {
      _tao_enumerator = static_cast<DDSRecorderMessage::CommandType> (_tao_temp);
    }
  
  return _tao_success;
}

TAO_END_VERSIONED_NAMESPACE_DECL



// TAO_IDL - Generated from
// be/be_visitor_structure/cdr_op_cs.cpp:52

TAO_BEGIN_VERSIONED_NAMESPACE_DECL

::CORBA::Boolean operator<< (
    TAO_OutputCDR &strm,
    const DDSRecorderMessage::Command &_tao_aggregate)
{
  return
    (strm << _tao_aggregate.instance_name.in ()) &&
    (strm << _tao_aggregate.cmd);
}

::CORBA::Boolean operator>> (
    TAO_InputCDR &strm,
    DDSRecorderMessage::Command &_tao_aggregate)
{
  return
    (strm >> _tao_aggregate.instance_name.out ()) &&
    (strm >> _tao_aggregate.cmd);
}

TAO_END_VERSIONED_NAMESPACE_DECL



// TAO_IDL - Generated from
// be/be_visitor_structure/cdr_op_cs.cpp:52

TAO_BEGIN_VERSIONED_NAMESPACE_DECL

::CORBA::Boolean operator<< (
    TAO_OutputCDR &strm,
    const DDSRecorderMessage::CommandReply &_tao_aggregate)
{
  return
    (strm << _tao_aggregate.instance_name.in ()) &&
    (strm << _tao_aggregate.command_id) &&
    (strm << _tao_aggregate.content.in ());
}

::CORBA::Boolean operator>> (
    TAO_InputCDR &strm,
    DDSRecorderMessage::CommandReply &_tao_aggregate)
{
  return
    (strm >> _tao_aggregate.instance_name.out ()) &&
    (strm >> _tao_aggregate.command_id) &&
    (strm >> _tao_aggregate.content.out ());
}

TAO_END_VERSIONED_NAMESPACE_DECL



// TAO_IDL - Generated from
// be/be_visitor_structure/cdr_op_cs.cpp:52

TAO_BEGIN_VERSIONED_NAMESPACE_DECL

::CORBA::Boolean operator<< (
    TAO_OutputCDR &strm,
    const DDSRecorderMessage::Acknowledgement &_tao_aggregate)
{
  return
    (strm << _tao_aggregate.sending_instance.in ()) &&
    (strm << _tao_aggregate.receiving_instance.in ()) &&
    (strm << _tao_aggregate.port_name.in ()) &&
    (strm << _tao_aggregate.acked_id) &&
    (strm << _tao_aggregate.serialized_vector_clock.in ());
}

::CORBA::Boolean operator>> (
    TAO_InputCDR &strm,
    DDSRecorderMessage::Acknowledgement &_tao_aggregate)
{
  return
    (strm >> _tao_aggregate.sending_instance.out ()) &&
    (strm >> _tao_aggregate.receiving_instance.out ()) &&
    (strm >> _tao_aggregate.port_name.out ()) &&
    (strm >> _tao_aggregate.acked_id) &&
    (strm >> _tao_aggregate.serialized_vector_clock.out ());
}

TAO_END_VERSIONED_NAMESPACE_DECL


