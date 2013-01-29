import java.net.*;
import java.io.*;

public class ChatBotProtocol {
    private static final int WAITING = 0;
    private static final int ANSWER = 1;
    private int state = WAITING;
    private int count = -1;

    private String[] answers = { "Hallo", "Ich bin ein ChatBot", "Du nervst", "BlaBla" };

    public String processInput(String theInput) {
        String theOutput = null;

        if (state == WAITING) {
            theOutput = "Ist da jemand?";
            state = ANSWER;
        } else if (state == ANSWER) {
        	count = ++count % answers.length;
        	theOutput = answers[count];
        	
        }
        return theOutput;
    }
}