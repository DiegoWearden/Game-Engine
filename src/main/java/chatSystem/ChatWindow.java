package chatSystem;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class ChatWindow {
    private JTextField chatInput;
    private JTextPane chatDisplay;
    private boolean isUsernameEntered = false;
    private String enteredUsername;
    private Color randomColor;
    String username;
    String message;

    public ChatWindow(){
    }

    public void buildFrame() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(100, 100, 450, 300);

        Container container = frame.getContentPane();
        container.setLayout(new BorderLayout(0, 0));

        chatDisplay = new JTextPane();
        chatDisplay.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatDisplay);
        container.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        container.add(inputPanel, BorderLayout.SOUTH);
        inputPanel.setLayout(new BorderLayout(0, 0));

        chatInput = new JTextField();
        chatInput.setText("Enter username: ");
        inputPanel.add(chatInput, BorderLayout.CENTER);
        if(!isUsernameEntered) {
            DocumentFilter filter = new DocumentFilter() {
                @Override
                public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                    String prompt = "Enter username: ";
                    int lengthOfPrompt = prompt.length();

                    if (offset < lengthOfPrompt) return;
                    super.remove(fb, offset, length);
                }
            };
            ((AbstractDocument) chatInput.getDocument()).setDocumentFilter(filter);
        }

        chatInput.addActionListener(e -> {
            String text;
            while (!isUsernameEntered) {
                text = chatInput.getText();
                String[] segments = text.split(": ", 2); // to get the username after ': '

                if (segments.length == 2) {
                    enteredUsername = segments[1];            // take everything after ': '
                    ServerChat.setUsername(enteredUsername.trim()); // Remove leading/trailing spaces
                    isUsernameEntered = true;
                } else {
                    return;
                }
                DocumentFilter filter = new DocumentFilter();
                ((AbstractDocument) chatInput.getDocument()).setDocumentFilter(filter);
                return;
            }
            text = chatInput.getText();
            String[] segments = text.split(": ", 2);
            String typedString;
            if (segments.length == 2) {
                typedString = segments[1];
                ServerChat.setUsername(segments[0]);
            } else {
                return;
            }
            ServerChat.sendData(typedString);
        });

        frame.setVisible(true);
    }

    public synchronized void printData(String text, boolean colored){
        // Spilt the username and message via "> " delimiter
        if(colored){
            String[] textParts = text.split("> ", 2);
            if (textParts.length < 2) {
                return;
            }

            // Define the username and message
            username = textParts[0] + "> ";
            message = textParts[1];
        }

        // Determine the document and existing style
        Document doc = chatDisplay.getStyledDocument();
        MutableAttributeSet standard = new SimpleAttributeSet();
        StyleConstants.setFontFamily(standard, "SansSerif");

        try {
            // Generate a random color and use it for the username
            if (colored){
                StyleConstants.setForeground(standard, randomColor);
                doc.insertString(doc.getLength(), username, standard);

                // Insert the message in normal black color
                StyleConstants.setForeground(standard, Color.BLACK);
                doc.insertString(doc.getLength(), message + "\n", standard);
            }else{
                StyleConstants.setForeground(standard, Color.BLACK);
                doc.insertString(doc.getLength(), text + "\n", standard);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        // Scroll to the end
        chatDisplay.setCaretPosition(chatDisplay.getDocument().getLength());
    }

    public void setUsernameColor(int r, int g, int b){
        randomColor = new Color(r, g, b);
    }
    public void setUsernamePrompt(String username){
        chatInput.setText(username + ": ");
    }
}
