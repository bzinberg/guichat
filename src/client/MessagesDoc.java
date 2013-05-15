package client;

import java.awt.Color;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 * Represents a stylized display of the messages in a conversation. Can include
 * pending messages; they will be displayed underneath the non-pending ones.
 */
class MessagesDoc extends DefaultStyledDocument {
    private static final long serialVersionUID = -6948091405550281782L;
    // Font styles
    public Style regular;
    public Style bold;
    public Style grayItal;
    public Style boldGrayItal;

    // What is my name? (This matters when we decide which messages can be
    // unpended.)
    private final String myUsername;
    // What conversation is this for?
    private final String convName;

    /* Integers representing positions in the doc */
    // End of the part containing received (non-pending) messages
    private int endOfReceived;
    // End of the part containing pending messages
    private int endOfPending;

    /** Maps a uniqueID to a message */
    protected Map<Integer, IMMessage> pending;

    /**
     * Constructor for MessagesDoc
     * 
     * @param _myUsername
     *            Name we registered with the server
     * @param _convName
     *            Name of the conversation
     */
    public MessagesDoc(String _myUsername, String _convName) {
        myUsername = _myUsername;
        convName = _convName;

        Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(
                StyleContext.DEFAULT_STYLE);
        regular = this.addStyle("regular", defaultStyle);
        bold = this.addStyle("bold", regular);
        StyleConstants.setBold(bold, true);
        grayItal = this.addStyle("grayItal", regular);
        StyleConstants.setForeground(grayItal, Color.GRAY);
        StyleConstants.setItalic(grayItal, true);
        boldGrayItal = this.addStyle("boldGrayItal", grayItal);
        StyleConstants.setBold(boldGrayItal, true);

        endOfReceived = 0;
        endOfPending = 0;

        pending = new TreeMap<Integer, IMMessage>();
    }

    /**
     * Adds the given message to the doc.
     * 
     * @param m
     *            The message to add
     * @throws BadLocationException
     *             (if this ever happens then it is a bug)
     */
    public synchronized void addMessage(IMMessage m)
            throws BadLocationException {
        String byline = m.getUser() + ": ";
        String message = m.getMessage() + "\n";

        if (m.isPending()) {
            pending.put(m.getMessageID(), m);
            this.insertString(endOfPending, byline, boldGrayItal);
            endOfPending += byline.length();
            this.insertString(endOfPending, message, grayItal);
            endOfPending += message.length();
        } else {
            this.insertString(endOfReceived, byline, bold);
            endOfReceived += byline.length();
            endOfPending += byline.length();
            this.insertString(endOfReceived, message, regular);
            endOfReceived += message.length();
            endOfPending += message.length();
        }
    }

    /**
     * Unpend the message with the given ID, if it exists.
     * 
     * @param messageID
     *            ID of the message to unpend (see design doc)
     * @throws BadLocationException
     *             (hopefully never)
     */
    public synchronized void unpend(int messageID) throws BadLocationException {
        if (!pending.containsKey(messageID)) {
            throw new RuntimeException(
                    "Tried to unpend a message that doesn't exist");
        }

        IMMessage oldMessage = pending.get(messageID);
        pending.remove(messageID);
        IMMessage newMessage = new IMMessage(oldMessage.getUser(),
                oldMessage.getMessage(), convName, false, messageID);

        /* Rebuild the pending part of the message display */
        this.remove(endOfReceived, endOfPending - endOfReceived);
        endOfPending = endOfReceived;
        addMessage(newMessage);
        for (IMMessage m : pending.values()) {
            addMessage(m);
        }
    }

    /**
     * Registers a message as received from the server. Adds it to the display,
     * or unpends an existing message if appropriate.
     * 
     * @param message
     *            The IM message
     */
    public void receiveMessage(IMMessage message) {
        if (message.getUser().equals(myUsername)
                && pending.containsKey(message.getMessageID())) {
            try {
                unpend(message.getMessageID());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        } else {
            try {
                addMessage(message);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }
}