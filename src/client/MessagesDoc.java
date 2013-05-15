package client;

import java.awt.Color;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

class MessagesDoc extends DefaultStyledDocument {
    private static final long serialVersionUID = -6948091405550281782L;
    public Style regular;
    public Style bold;
    public Style grayItal;
    public Style boldGrayItal;

    private final String myUsername;
    private final String convName;

    /* Integers representing positions in the doc */
    private int endOfReceived;
    private int endOfPending;

    /** Maps a uniqueID to a message */
    protected Map<Integer, IMMessage> pending;

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

    public synchronized void addMessage(IMMessage m)
            throws BadLocationException {
        String byline = m.getUser() + ": ";
        String message = m.getMessage() + "\n";

        if (m.isPending()) {
            pending.put(m.getMessageId(), m);
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

    public synchronized void unpend(int messageId) throws BadLocationException,
            BadServerMessageException {
        if (!pending.containsKey(messageId)) {
            throw new BadServerMessageException(
                    "Tried to unpend a message that doesn't exist");
        }

        IMMessage oldMessage = pending.get(messageId);
        pending.remove(messageId);
        IMMessage newMessage = new IMMessage(oldMessage.getUser(),
                oldMessage.getMessage(), convName, false, messageId);

        /* Rebuild the pending part of the message display */
        this.remove(endOfReceived, endOfPending - endOfReceived);
        endOfPending = endOfReceived;
        addMessage(newMessage);
        for (IMMessage m : pending.values()) {
            addMessage(m);
        }
    }

    public void receiveMessage(IMMessage message) {
        if (message.getUser().equals(myUsername)
                && pending.containsKey(message.getMessageId())) {
            try {
                unpend(message.getMessageId());
            } catch (BadLocationException e) {
                e.printStackTrace();
            } catch (BadServerMessageException e) {
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