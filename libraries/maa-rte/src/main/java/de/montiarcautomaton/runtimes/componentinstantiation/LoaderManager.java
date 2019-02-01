package de.montiarcautomaton.runtimes.componentinstantiation;

import java.util.HashMap;
import java.util.Map;


/**
 * Stores all ComponentLoaders, so they can be stopped whenever a component gets replaced.
 */
public class LoaderManager {

  Map<String, ILoader> registeredLoaders = new HashMap<>();

  /**
   * Register loader to the loadermanager
   * @param instanceName
   * @param fsLoader
   */
  public void registerLoader(String instanceName, ILoader fsLoader){
    registeredLoaders.put(instanceName, fsLoader);
  }

  /**
   * Stop and unregister loader from the loadermanager
   * @param instanceName
   */
  public void unregisterLoader(String instanceName){
	  ILoader loader = registeredLoaders.get(instanceName);
    if (loader != null){
      loader.stop();
      registeredLoaders.remove(instanceName);
    }
    else{
      System.out.println("Could not unregister loader since no loader for "
              + instanceName + " is registered");
    }
  }

  public Map<String, ILoader> getRegisteredLoaders() {
    return registeredLoaders;
  }
}
