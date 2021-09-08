// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server;

import java.net.URL;
import java.util.HashMap;
import org.eclipse.paho.client.mqttv3.MqttClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import montithings.services.iot_manager.server.exception.DeploymentException;
import montithings.services.iot_manager.server.genesis.GenesisDeployTargetProvider;
import montithings.services.iot_manager.server.k8s.K8sDeployTargetProvider;
import montithings.services.iot_manager.server.util.ThrowingFunction;

public class DeployTargetProviderParser {
  
  public static IDeployTargetProvider parse(JsonElement json) throws DeploymentException {
    try {
      if (json.isJsonArray()) {
        // Pool provider
        return parsePoolProvider(json.getAsJsonArray());
      }
      else {
        // otherwise it is a typed provider
        
        // register types
        HashMap<String, ThrowingFunction<JsonObject, IDeployTargetProvider, DeploymentException>> constructors = new HashMap<>();
        constructors.put("BASIC", DeployTargetProviderParser::parseBasicProvider);
        constructors.put("KUBERNETES", DeployTargetProviderParser::parseKubernetesProvider);
        constructors.put("GENESIS", DeployTargetProviderParser::parseGenesisProvider);
        
        JsonObject jo = json.getAsJsonObject();
        String type = jo.get("type").getAsString();
        
        ThrowingFunction<JsonObject, IDeployTargetProvider, DeploymentException> constructor = constructors.get(type);
        if (constructor == null) {
          throw new DeploymentException("Unknown iot_manager target type: " + type);
        }
        
        return constructor.apply(jo);
      }
    }
    catch (Exception e) {
      throw new DeploymentException("Could not parse iot_manager provider from json.", e);
    }
  }
  
  public static PoolDeployTargetProvider parsePoolProvider(JsonArray jarr) throws DeploymentException {
    PoolDeployTargetProvider pool = new PoolDeployTargetProvider();
    // parse nested providers and add them to pool
    for (JsonElement je : jarr) {
      pool.addProvider(parse(je));
    }
    return pool;
  }
  
  public static DefaultDeployTargetProvider parseBasicProvider(JsonObject json) throws DeploymentException {
    try {
      String mqttAddress = json.get("mqttAddress").getAsString();
      String mqttURI = "tcp://" + mqttAddress;
      if (mqttURI.contains(":")) {
        // if no port is given, add default port
        mqttURI += ":1883";
      }
      long providerID = json.get("id").getAsLong();
      
      MqttClient client = new MqttClient(mqttURI, MqttClient.generateClientId());
      return new DefaultDeployTargetProvider(providerID, client);
    }
    catch (Exception e) {
      throw new DeploymentException(e);
    }
  }
  
  public static K8sDeployTargetProvider parseKubernetesProvider(JsonObject json) throws DeploymentException {
    try {
      String endpointURL = json.get("endpoint").getAsString();
      String token = json.get("token").getAsString();
      long providerID = json.get("id").getAsLong();
      
      return new K8sDeployTargetProvider(providerID, endpointURL, token);
    }
    catch (Exception e) {
      throw new DeploymentException(e);
    }
  }
  
  public static GenesisDeployTargetProvider parseGenesisProvider(JsonObject json) throws DeploymentException {
    try {
      String endpointURL = json.get("endpoint").getAsString();
      long providerID = json.get("id").getAsLong();
      URL url = new URL(endpointURL);
      return new GenesisDeployTargetProvider(providerID, url);
    }
    catch (Exception e) {
      throw new DeploymentException(e);
    }
  }
  
}
