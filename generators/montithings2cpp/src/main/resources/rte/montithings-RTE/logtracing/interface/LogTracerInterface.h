/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once

#include <string>
#include "sole/sole.hpp"

class LogTracerInterface {
public:
    enum Request {
        LOG_ENTRIES, INTERNAL_DATA
    };

    virtual ~LogTracerInterface() = default;

    virtual void response(sole::uuid reqUuid, const std::string &content) = 0;

    virtual void request(std::string instanceName, Request request, time_t fromDatetime) = 0;

    virtual void request(std::string instanceName, Request request, time_t fromDatetime, sole::uuid traceUuid) = 0;

    virtual void addOnResponseCallback(std::function<void(sole::uuid reqUuid, std::string content)> callback) = 0;

    virtual void addOnRequestCallback(std::function<void(sole::uuid reqUuid, sole::uuid traceUuid, Request reqType,
                                                         long from_timestamp)> callback) = 0;
};
