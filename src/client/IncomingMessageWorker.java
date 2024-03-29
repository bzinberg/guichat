package client;

import javax.swing.SwingWorker;

import network.NetworkConstants;

/**
 * Worker class which handles a given message on the Swing event dispatch
 * thread.
 */
class IncomingMessageWorker extends SwingWorker<Void, Void> {
    private String message;

    private ClientGUI clientGUI;

    /**
     * Constructor for IncomingMessageWorker.
     * 
     * @param _message
     *            Text of the given message
     * @param _clientGUI
     *            Client GUI for this session
     */
    public IncomingMessageWorker(String _message, ClientGUI _clientGUI) {
        message = _message;
        clientGUI = _clientGUI;
    }

    @Override
    public Void doInBackground() {
        /* Do nothing -- everything happens on the Swing event dispatch thread */
        return null;
    }

    /**
     * Handle the given incoming message, updating data fields and modifying GUI
     * elements as necessary. This is thread-safe because GUI elements are only
     * ever modified from the Swing event dispatch thread. Sets the status bar of
     * the GUI if a bad message is encountered.
     */
    @Override
    public void done() {
        // Separate out the message type from the content
        String[] data = message.split("\t", 2);
        String[] args;
        String messageType = data[0];
        if (messageType.equals(NetworkConstants.INIT_USERS_LIST))
            clientGUI
                    .setStatusText("Received unexpected INIT_USERS_LIST message");
        else if (messageType.equals(NetworkConstants.IM)) {
            args = data[1].split("\t", 4);
            if (args.length < 4) {
                clientGUI
                        .setStatusText("Received malformed IM message from server: "
                                + message);
            }
            clientGUI.registerIM(args[0], args[1], args[2], args[3]);
        } else if (messageType.equals(NetworkConstants.ADDED_TO_CONV)) {
            args = data[1].split("\t", 2);
            if (args.length < 2) {
                clientGUI
                        .setStatusText("Received malformed ADDED_TO_CONV message from server: "
                                + message);
            }
            clientGUI.tryToAddUserToConv(args[0], args[1]);
        } else if (messageType.equals(NetworkConstants.ENTERED_CONV)) {
            args = data[1].split("\t", 2);
            if (args.length < 2) {
                clientGUI
                        .setStatusText("Received malformed ENTERED_CONV message from server: "
                                + message);
            }
            clientGUI.tryToEnterConv(args[0], args[1]);
        } else if (messageType.equals(NetworkConstants.REMOVED_FROM_CONV)) {
            args = data[1].split("\t", 2);
            if (args.length < 2) {
                clientGUI
                        .setStatusText("Received malformed REMOVED_FROM_CONV message from server: "
                                + message);
            }
            clientGUI.tryToRemoveUserFromConv(args[0], args[1]);
        } else if (messageType.equals(NetworkConstants.CONNECTED)) {
            clientGUI.handleConnectedMessage(data[1]);
        } else if (messageType.equals(NetworkConstants.DISCONNECTED)) {
            clientGUI.handleDisconnectedMessage(data[1]);
        } else if (messageType.equals(NetworkConstants.PARTICIPANTS)) {
            args = data[1].split("\t", 2);
            if (args.length < 2) {
                clientGUI
                        .setStatusText("Received malformed PARTICIPANTS message from server: "
                                + message);
            }
            clientGUI.handleParticipantsMessage(args[0], args[1]);
        } else if (messageType.equals(NetworkConstants.ERROR)) {
            clientGUI.handleErrorMessage(data[1]);
        } else {
            clientGUI.setStatusText("Unrecognized message from server: "
                    + message);
        }
    }
}