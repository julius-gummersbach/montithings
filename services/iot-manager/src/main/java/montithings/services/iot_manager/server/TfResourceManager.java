package montithings.services.iot_manager.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.io.output.StringBuilderWriter;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import montithings.services.iot_manager.server.azurecloud.AzureCredentials;
import montithings.services.iot_manager.server.data.DeploymentInfo;
import montithings.services.iot_manager.server.data.Distribution;
import montithings.services.iot_manager.server.data.TerraformInfo;
import montithings.services.iot_manager.server.dto.ApplyTerraformDTO;
import montithings.services.iot_manager.server.dto.ApplyTerraformResDTO;
import montithings.services.iot_manager.server.exception.DeploymentException;

/**
 * Responsible for applying and destroying terraform resources via
 * terraform-deployer service
 */
public class TfResourceManager {
  // Ensure singleton instance for state management
  private static final TfResourceManager tfResourceManager = new TfResourceManager();

  // Token to authenticate against terraform-deployer service
  private final String token = "03c11e6e-41fc-4862-a37a-6dbc46a834b9";
  // Duration after which HTTP request timeout
  private final Duration timeout = Duration.ofMinutes(15);
  // Path to provider.tf freemarker template
  private final String providerFtl = "templates/azureCloudProviderTf.ftl";
  // Path to base.tf freemarker template
  private final String baseFtl = "templates/azureCloudBaseTf.ftl";

  // Url to terraform-deployer service
  private String terraformDeployerUrl;
  // Credentials to authenticate against Azure cloud
  private AzureCredentials credentials;
  // Storage account name where terraform-deployer uploads tfState
  // Must be unique within Azure
  private String storageAccountName;
  // List of all component resources from DeploymentInfo
  private Set<TerraformInfo> componentResources = new HashSet<TerraformInfo>();
  // List of resources that were applied
  private Set<TerraformInfo> appliedResources = new HashSet<TerraformInfo>();
  // List of resources that will be applied with next "terraform apply"
  private Set<TerraformInfo> toApplyResources = new HashSet<TerraformInfo>();
  // Environment variables to be set in container instances
  private Map<String, String> envvars = new HashMap<>();

  private TfResourceManager() {
  }

  public static TfResourceManager getInstance() {
    return tfResourceManager;
  }

  public void setCredentials(AzureCredentials credentials) {
    this.credentials = credentials;
  }

  public void setTerraformDeployerUrl(String terraformDeployerUrl) {
    this.terraformDeployerUrl = terraformDeployerUrl;
  }

  public void setStorageAccountName(String storageAccountName) {
    this.storageAccountName = storageAccountName;
  }

  public Map<String, String> getEnvvars() {
    return envvars;
  }

  /**
   * Adds all component resources from deployment info
   * 
   * @param deploymentInfo
   */
  public void setComponentResources(DeploymentInfo deploymentInfo) {
    this.componentResources.clear();

    for (TerraformInfo tfInfo : deploymentInfo.getTerraformInfos()) {
      this.componentResources.add(tfInfo);
    }
  }

  /**
   * Provisions terraform resources
   * 
   * @param distribution
   * @param customResources
   * @throws DeploymentException
   */
  public void apply(Distribution distribution,
      DeploymentInfo deploymentInfo, TerraformInfo... customResources) throws DeploymentException {
    prepareApply(distribution, deploymentInfo, customResources);
    apply();
  }

  /**
   * Destroys all terraform resources
   * 
   * @throws DeploymentException
   */
  public void destroyAll() throws DeploymentException {
    prepareDestroyAll();
    apply();
  }

  /**
   * Destroys component terraform resources
   * 
   * @throws DeploymentException
   */
  public void destroyComponentResources() throws DeploymentException {
    prepareDestroyComponentResources();
    apply();
  }

  /**
   * Destroys toDestroyResources
   * 
   * @param toDestroyResources
   * @throws DeploymentException
   */
  public void destroy(TerraformInfo... toDestroyResources) throws DeploymentException {
    prepareDestroy(toDestroyResources);
    apply();
  }

  /**
   * Identifies all resources required for the upcoming apply
   * 
   * @param distribution
   * @param customResources
   */
  private void prepareApply(Distribution distribution, DeploymentInfo deploymentInfo,
      TerraformInfo... customResources) {
    // Clean toApplyResources
    toApplyResources.clear();

    // Provider.tf always applied
    toApplyResources.add(getProviderTf());

    // Base.tf always applied
    toApplyResources.add(getBaseTf());

    // Always add appliedResources
    toApplyResources.addAll(appliedResources);

    // Add component resources for distribution
    toApplyResources.addAll(getToApplyComponentResources(distribution, deploymentInfo));

    // Add custom resources
    for (TerraformInfo tfInfo : customResources) {
      toApplyResources.add(tfInfo);
    }
  }

  /**
   * Marks all resources to be destroyed
   */
  private void prepareDestroyAll() {
    // Clean toApplyResources
    toApplyResources.clear();

    // Destroy all without provider declaration fails with "At least 1 'features'
    // blocks are required."
    TerraformInfo providerTf = getProviderTf();
    if (appliedResources.contains(providerTf)) {
      toApplyResources.add(providerTf);
    }
  }

  /**
   * Identifies all resources required for the upcoming apply by dropping
   * componentResources
   * 
   * @param toDestroyResources
   */
  private void prepareDestroyComponentResources() {
    // Clean toApplyResources
    toApplyResources.clear();

    // Provider.tf always applied
    TerraformInfo providerTf = getProviderTf();
    if (appliedResources.contains(providerTf)) {
      toApplyResources.add(providerTf);
    }

    // Base.tf always applied
    TerraformInfo baseTf = getBaseTf();
    if (appliedResources.contains(baseTf)) {
      toApplyResources.add(baseTf);
    }

    // Always add appliedResources
    toApplyResources.addAll(appliedResources);

    // Drop componentResources resources from toApplyResources
    toApplyResources.removeAll(componentResources);
  }

  /**
   * Identifies all resources required for the upcoming apply by dropping
   * toDestroyResources
   * 
   * @param toDestroyResources
   */
  private void prepareDestroy(TerraformInfo... toDestroyResources) {
    // Clean toApplyResources
    toApplyResources.clear();

    // Provider.tf always applied
    TerraformInfo providerTf = getProviderTf();
    if (appliedResources.contains(providerTf)) {
      toApplyResources.add(providerTf);
    }

    // Base.tf always applied
    TerraformInfo baseTf = getBaseTf();
    if (appliedResources.contains(baseTf)) {
      toApplyResources.add(baseTf);
    }

    // Always add appliedResources
    toApplyResources.addAll(appliedResources);

    // Drop toDestroy resources from toApplyResources
    for (TerraformInfo toDestroyResource : toDestroyResources) {
      toApplyResources.remove(toDestroyResource);
    }
  }

  /**
   * Executes terraform apply via terraform-deployer service using the resources
   * identified during prepareApply
   * 
   * @return
   * @throws DeploymentException
   */
  private void apply() throws DeploymentException {
    // 1. Ensure all required fields are set via UI
    preCheckApply();

    // 2. Apply only, if resources changed
    if (appliedResources.equals(toApplyResources)) {
      return;
    }

    // 3. Construct POST body from toApplyResources
    ApplyTerraformDTO body = new ApplyTerraformDTO(credentials, new ArrayList<>(toApplyResources), storageAccountName);

    // 4. Apply terraform via "terraform-deployer" service
    ApplyTerraformResDTO res = execApplyTerraform(body);

    // 5. Update applied resources
    appliedResources.addAll(toApplyResources);

    // 6. Upsert environment variables
    upsertEnvVars(res.getEnvvars());
  }

  /**
   * Check that terraform-deployer service target url as well as credentials are
   * set
   * 
   * @throws DeploymentException
   */
  private void preCheckApply() throws DeploymentException {
    if (terraformDeployerUrl == null) {
      throw new DeploymentException("Terraform Deployer URL not set");
    }

    if (credentials == null) {
      throw new DeploymentException("Azure credentials not set");
    }

    if (storageAccountName == null) {
      throw new DeploymentException("Storage account name not set");
    }
  }

  /**
   * Adds new env vars to this.envvars. If an env var with key already exists, it
   * overrides with new value
   * 
   * @param newEnvVars
   */
  private void upsertEnvVars(Map<String, String> newEnvVars) {
    for (Entry<String, String> e : newEnvVars.entrySet()) {
      this.envvars.put(e.getKey(), e.getValue());
    }
  }

  /**
   * Returns a list of component resources for each component in distribution
   * 
   * @param distribution
   * @return
   */
  private Set<TerraformInfo> getToApplyComponentResources(Distribution distribution, DeploymentInfo deploymentInfo) {
    Set<TerraformInfo> toApplyComponentResources = new HashSet<TerraformInfo>();

    for (String[] componentNames : distribution.getDistributionMap().values()) {
      for (String componentName : componentNames) {
        for (TerraformInfo tfInfo : componentResources) {
          String componentType = deploymentInfo.getInstanceInfo(componentName).getComponentType();
          if (tfInfo.getFilename().equals(componentType)) {
            toApplyComponentResources.add(tfInfo);
          }
        }
      }
    }

    return toApplyComponentResources;
  }

  /**
   * Returns provider.tf with all providers. This file is required for apply and
   * destroy
   * 
   * @return
   */
  private TerraformInfo getProviderTf() {
    GeneratorSetup setup = new GeneratorSetup();
    setup.setTracing(false);
    GeneratorEngine engine = new GeneratorEngine(setup);
    StringBuilderWriter providerTf = new StringBuilderWriter();
    engine.generateNoA(this.providerFtl, providerTf);
    String filecontent = Base64.getEncoder().encodeToString(providerTf.toString().getBytes());
    return new TerraformInfo("provider.tf", filecontent);
  }

  /**
   * 
   * /**
   * Returns base.tf with all base cloud resources such as resource group or
   * storage account
   * 
   * @return
   */
  private TerraformInfo getBaseTf() {
    GeneratorSetup setup = new GeneratorSetup();
    setup.setTracing(false);
    GeneratorEngine engine = new GeneratorEngine(setup);
    StringBuilderWriter baseTf = new StringBuilderWriter();
    engine.generateNoA(this.baseFtl, baseTf, storageAccountName);
    String filecontent = Base64.getEncoder().encodeToString(baseTf.toString().getBytes());
    return new TerraformInfo("base.tf", filecontent);
  }

  /**
   * Applies a set of Terraform files via "terraform-deployer" Service
   * 
   * @param body
   * @return
   * @throws DeploymentException
   */
  private ApplyTerraformResDTO execApplyTerraform(ApplyTerraformDTO body) throws DeploymentException {
    HttpURLConnection connection = null;

    try {
      // 1. Open connection
      System.out.println("Apply terraform. Url: " + this.terraformDeployerUrl + "/apply");
      URL url = new URL(this.terraformDeployerUrl + "/apply");
      connection = (HttpURLConnection) url.openConnection();

      // 2. Prepare request
      System.out.println("Apply terraform. Prepare request. Body: " + body.toJson());
      byte[] postData = body.toJson().getBytes();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      connection.setRequestProperty("Accept", "application/json");
      connection.setRequestProperty("X-Token", token);
      connection.setFixedLengthStreamingMode(postData.length);
      connection.setReadTimeout((int) timeout.toMillis());
      connection.setConnectTimeout((int) timeout.toMillis());
      connection.setUseCaches(false);
      connection.setDoOutput(true);

      // 3. Send request
      System.out.println("Apply terraform. Send request");
      DataOutputStream wr = new DataOutputStream(
          connection.getOutputStream());
      wr.write(postData);
      wr.close();

      // 4. Parse response
      if (connection.getResponseCode() != 201) {
        String errorMessage = "Terraform apply failed with status code " + connection.getResponseCode()
            + " and error message: " + getResponseStr(connection);
        throw new DeploymentException(errorMessage);
      }

      // 5. Parse response
      System.out.println("Parse response");
      return getResponse(connection);
    } catch (IOException e) {
      e.printStackTrace();
      throw new DeploymentException(e.getMessage());
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  /**
   * Parses HTTP response to ApplyTerraformResDTO
   * 
   * @param connection
   * @return
   * @throws IOException
   */
  private ApplyTerraformResDTO getResponse(HttpURLConnection connection) throws IOException {
    String jsonString = getResponseStr(connection);
    return new ObjectMapper().readValue(jsonString, ApplyTerraformResDTO.class);
  }

  /**
   * Parses HTTP response to String
   * 
   * @param connection
   * @return
   * @throws IOException
   */
  private String getResponseStr(HttpURLConnection connection) throws IOException {
    InputStream is = connection.getInputStream();
    try (BufferedReader rd = new BufferedReader(new InputStreamReader(is))) {
      StringBuffer response = new StringBuffer();
      String line;
      while ((line = rd.readLine()) != null) {
        response.append(line);
        response.append('\r');
      }
      return response.toString();
    }
  }
}
