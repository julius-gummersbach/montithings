/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#pragma once
#include <iostream>

class Message
{
  public:
  virtual std::string toString () = 0;
};
