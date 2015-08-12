package com.gdg.nanochat.model;

/**
 * @author Santiago Carrillo
 */
public class ChatMessage
{

    private String name;

    private String message;

    /**
     * Empty constructor necessary for Firebase's deserializer
     */
    public ChatMessage()
    {
    }

    public ChatMessage( String name, String message )
    {

        this.name = name;
        this.message = message;
    }

    public String getName()
    {
        return name;
    }

    public String getMessage()
    {
        return message;
    }

}
