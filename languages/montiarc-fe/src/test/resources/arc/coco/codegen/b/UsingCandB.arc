package b;

component UsingCandB {
    
    port
        in String sIn1,
        in String sIn2,
        out String sOut;
        
    component C;
    
    component B b1, b2;
    
    
    connect sIn1 -> b1.sIn;
    connect sIn2 -> b2.sIn;
    connect b1.sOut -> c.sIn1;
    connect b2.sOut -> c.sIn2;
    connect c.sOut -> sOut;
    
    
}