package montithings.services.iot_manager.server.data;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.*;

public class ConnectionString {
  byte[] rawMsgPayload;
  String connectionString;
  String instanceName;
  String componentInterface;

  public ConnectionString(byte[] rawMsgPayload) {
    this.rawMsgPayload = rawMsgPayload;
    String strJson = new String(rawMsgPayload, StandardCharsets.UTF_8);
    JsonObject json = JsonParser.parseString(strJson).getAsJsonObject();
    this.connectionString = json.get("connectionString").getAsString();
    this.instanceName = json.get("instanceName").getAsString();
    this.componentInterface = json.get("interface").getAsString();
  }

  public byte[] getRawMsgPayload() {
    return rawMsgPayload;
  }

  public String getInstanceName() {
    return instanceName;
  }

  public String getConnectionString() {
    return connectionString;
  }

  public byte[] getConnectionStringAsByte() {
    return connectionString.getBytes(StandardCharsets.UTF_8);
  }

  public String getComponentInterface() {
    return componentInterface;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ConnectionString)) {
      return false;
    }
    if (obj == this) {
      return true;
    }

    ConnectionString rhs = (ConnectionString) obj;
    return Arrays.equals(rawMsgPayload, rhs.rawMsgPayload);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(rawMsgPayload).toHashCode();
  }
}
