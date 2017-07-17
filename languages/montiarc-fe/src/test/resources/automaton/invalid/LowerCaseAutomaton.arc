package automaton.invalid;

component LowerCaseAutomaton {
  port
    in Integer a;
  
  automaton lowercaseName {
  	variable int c;
  
  	state Start;
    initial Start;

    Start -> Start [c < 2];
  }
}
