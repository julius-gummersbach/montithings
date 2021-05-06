/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once
#include "MqttClient.h"
#include "MqttUser.h"
#include "Port.h"
#include "Utils.h"
#include <iostream>
#include <mosquitto.h>
#include <string>
#include <thread>
#include <utility>

template <typename T> class MqttPort : public Port<T>, public MqttUser
{
protected:
  /**
   * Fully qualified name of the port (with slashes instead of dots)
   */
  std::string fullyQualifiedName;

  /**
   * True if this is a sensor / actuator port
   */
  bool isSensorActuator = false;

  /**
   * Subscribed MQTT topics of other ports
   */
  std::set<std::string> subscriptions;

public:
  explicit MqttPort (std::string name, bool shouldSubscribe = true,
                     MqttClient *client = MqttClient::instance ());
  ~MqttPort () = default;

  /**
   * Subscribe to the messages of the port with the provided name
   * \param portFqn fully qualified name of the port to subscribe to
   */
  void subscribe (std::string portFqn);

  /**
   * Set the name of the local sensor / actuator to connect to
   * \param sensorName topic name without "/sensorActuator/" prefix
   */
  void setSensorActuatorName (std::string sensorName);

  /* ============================================================ */
  /* ======================== Port Methods ====================== */
  /* ============================================================ */
  void getExternalMessages () override;
  void sendToExternal (tl::optional<T> nextVal) override;

  /* ============================================================ */
  /* ======================= MQTT Callbacks ===================== */
  /* ============================================================ */

  /**
   * Callback that is called when a message is received from the broker.
   *
   * \see https://mosquitto.org/api/files/mosquitto-h.html#mosquitto_message_callback_set
   * \param mosquitto the mosquitto instance making the callback.
   * \param obj the user data provided in mosquitto_new
   * \param message the message data. This variable and associated memory will
   *                be freed by the library after the callback completes. The
   *                client should make copies of any of the data it requires.
   */
  void onMessage (mosquitto *mosquitto, void *obj,
                  const struct mosquitto_message *message) override;
};

template <typename T>
MqttPort<T>::MqttPort (std::string name, bool shouldSubscribe, MqttClient *client)
    : fullyQualifiedName (std::move (name))
{
  MqttClient::instance ()->addUser (this);

  std::string connectorTopic = "/connectors/" + replaceDotsBySlashes (fullyQualifiedName);
  std::string manualInjectTopic = "/portsInject/" + replaceDotsBySlashes (fullyQualifiedName);
  if (shouldSubscribe)
    {
      MqttClient::instance ()->subscribe (connectorTopic);
      subscriptions.emplace (connectorTopic);
      MqttClient::instance ()->subscribe (manualInjectTopic);
      subscriptions.emplace (manualInjectTopic);
    }
}

template <typename T>
void
MqttPort<T>::subscribe (std::string portFqn)
{
  std::string topic = "/ports/" + replaceDotsBySlashes (portFqn);
  MqttClient::instance ()->subscribe (topic);
  subscriptions.emplace (topic);
}

template <typename T>
void
MqttPort<T>::setSensorActuatorName (std::string sensorName)
{
  LOG (DEBUG) << "Set sensor actuator name";
  MqttClient::localInstance ()->addUser (this);
  std::string topic = "/sensorActuator/" + replaceDotsBySlashes (sensorName);
  MqttClient::localInstance ()->subscribe (topic);
  subscriptions.emplace (topic);
  isSensorActuator = true;
  LOG (DEBUG) << "Subscribe to sensor '" << topic << "'";
}

template <typename T>
void
MqttPort<T>::onMessage (mosquitto *mosquitto, void *obj, const struct mosquitto_message *message)
{
  std::string topic = std::string ((char *)message->topic);
  std::string payload = std::string ((char *)message->payload, message->payloadlen);

  for (std::string subscription : subscriptions)
    {
      // only process message messages from ports we are subscribed to
      if (topic.find (subscription) != std::string::npos)
        {
          // check if this message informs us about new data
          if (topic.find ("/ports/") != std::string::npos
              || topic.find ("/portsInject/") != std::string::npos
              || (isSensorActuator && topic.find ("/sensorActuator/") != std::string::npos))
            {
              try
                {
                  T result = jsonToData<T> (payload);
                  this->setNextValue (result);
                }
              catch (...)
                {
                  std::string portName = replaceDotsBySlashes(fullyQualifiedName);
                  LOG (WARNING) << "Port '" << portName << "' could not parse incoming message '"
                                << payload << "'";
                }
            }

          // check if this message informs us about a connector
          if (topic.find ("/connectors/") != std::string::npos)
            {
              subscribe (payload);
            }
        }
    }
}

template <typename T>
void
MqttPort<T>::getExternalMessages ()
{
  // nothing to do here. Message are received via onMessage() callback
}

template <typename T>
void
MqttPort<T>::sendToExternal (tl::optional<T> nextVal)
{
  std::string prefix = "/ports/";
  if (isSensorActuator)
    {
      prefix = "/sensorActuator/";
    }

  if (nextVal)
    {
      std::string payload = dataToJson (nextVal);
      std::string topic = prefix + replaceDotsBySlashes (fullyQualifiedName);
      if (isSensorActuator)
        {
          MqttClient::localInstance ()->publish (topic, payload);
        }
      else
        {
          MqttClient::instance ()->publish (topic, payload);
        }
    }
}
