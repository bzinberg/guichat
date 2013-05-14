package client;

public interface MessageToServer {

    public abstract String getMessageText();

    public abstract boolean isCancelled();

    public abstract void cancel();

}