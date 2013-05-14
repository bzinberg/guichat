package client;

import javax.swing.SwingWorker;

class IncomingMessageWorker extends SwingWorker<Void, Void> {
    private String message;

    private ClientGUI clientGUI;
    
    public IncomingMessageWorker(String _message, ClientGUI _clientGUI) {
        message = _message;
        clientGUI = _clientGUI;
    }

    public Void doInBackground() {
        /* TODO I don't think anything needs to go here? */
        return null;
    }

    public void done() {
        String[] data = message.split("\t", 2);
        int messageType = Integer.parseInt(data[0]);
        String[] args;
        switch (messageType) {
        case 0:
            throw new BadServerMessageException(
                    "Received unexpected INIT_USERS_LIST message");
        case 1:
            args = data[1].split("\t", 4);
            if (args.length < 4) {
                throw new BadServerMessageException(
                        "Received malformed IM message from server: "
                                + message);
            }
            clientGUI.registerIM(args[0], args[1], args[2], args[3]);
            break;
        case 2:
            args = data[1].split("\t", 2);
            if (args.length < 2) {
                throw new BadServerMessageException(
                        "Received malformed ADDED_TO_CONV message from server: "
                                + message);
            }
            clientGUI.tryToAddUserToConv(args[0], args[1]);
            break;
        case 3:
            args = data[1].split("\t", 2);
            if (args.length < 2) {
                throw new BadServerMessageException(
                        "Received malformed ENTERED_CONV message from server: "
                                + message);
            }
            clientGUI.tryToEnterConv(args[0], args[1]);
            break;
        case 4:
            args = data[1].split("\t", 2);
            if (args.length < 2) {
                throw new BadServerMessageException(
                        "Received malformed REMOVED_FROM_CONV message from server: "
                                + message);
            }
            clientGUI.tryToRemoveUserFromConv(args[0], args[1]);
            break;
        case 5:
            args = new String[] { data[1] };
            clientGUI.handleConnectedMessage(args[0]);
            break;
        case 6:
            args = new String[] { data[1] };
            clientGUI.handleDisconnectedMessage(args[0]);
            break;
        case 7:
            args = data[1].split("\t", 2);
            if (args.length < 2) {
                throw new BadServerMessageException(
                        "Received malformed PARTICIPANTS message from server: "
                                + message);
            }
            clientGUI.handleParticipantsMessage(args[0], args[1]);
            break;
        case 8:
            args = new String[] { data[1] };
            clientGUI.handleErrorMessage(args[0]);
            break;
        default:
            throw new BadServerMessageException(
                    "Unrecognized message from server: " + message);
        }
    }

}