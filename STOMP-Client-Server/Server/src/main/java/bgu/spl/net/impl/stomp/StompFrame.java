package bgu.spl.net.impl.stomp;

import java.util.HashMap;
import java.util.Map;

public abstract class StompFrame {

    protected String command;
    protected String body;
    protected Map<String, String> headers = new HashMap<>();

    // Default constructor
    public StompFrame() {}

    public String toString(){
        StringBuilder output = new StringBuilder();
        output.append(command).append('\n');
        for (String header : headers.keySet()){
            output.append(header).append(":").append(headers.get(header)).append('\n');
        }
        if(body.length() == 0) {
            output.append('\n').append(body);
        }
        else{
            output.append('\n').append(body).append('\n');
        }

        return output.toString();
    }

    public String getCommand(){
        return command;
    }

    public String getHeader(String headerName){
        return headers.get(headerName);
    }

    public String getBody(){
        return body;
    }

    public void addHeader(String description, String value){
        headers.put(description,value);
    }

}