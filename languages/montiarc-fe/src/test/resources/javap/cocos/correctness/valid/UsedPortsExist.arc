package javap.cocos.correctness.valid;

component UsedPortsExist(String s) {
  port
    in double distance,
    out String hulu;
    
  init EmptyStringInitializer {
    hulu = "";
  } 

  compute {    
    hulu = s;
  }
}