package types;

component GenericIfProvider<T> {
    
    port
        out MyGenericImpl<T> implOut,
        out GenericInterface<T> ifOut;

}