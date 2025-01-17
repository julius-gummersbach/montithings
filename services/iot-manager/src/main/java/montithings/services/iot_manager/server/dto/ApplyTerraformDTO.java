package montithings.services.iot_manager.server.dto;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import montithings.services.iot_manager.server.azurecloud.AzureCredentials;
import montithings.services.iot_manager.server.data.TerraformInfo;

public class ApplyTerraformDTO {
  private List<TerraformInfo> files;
  private AzureCredentials credentials;
  private String storageAccountName;

  public ApplyTerraformDTO(AzureCredentials credentials, List<TerraformInfo> files, String storageAccountName) {
    this.files = files;
    this.credentials = credentials;
    this.storageAccountName = storageAccountName;
  }

  public List<TerraformInfo> getFiles() {
    return this.files;
  }

  public AzureCredentials getCredentials() {
    return credentials;
  }

  public String getStorageAccountName() {
    return storageAccountName;
  }

  public String toJson() throws JsonProcessingException {
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    return ow.writeValueAsString(this);
  }

}
