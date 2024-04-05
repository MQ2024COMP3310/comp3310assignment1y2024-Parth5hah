package assignment1;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;


public class Prompt extends Rectangle{
    private static int height = 70;
    private static int width = 630;

    protected int xpos;
    protected int ypos;

    protected String promptText = "";

    public Prompt(){
        promptText = "";
        xpos = 0;
        ypos = 0;
    }

    public Prompt(int x, int y){
        promptText = "";
        xpos = x;
        ypos = y;
    }
    
    void paint(Graphics g){
        g.setColor(Color.BLACK);
        g.fillRect(xpos, ypos, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(xpos, ypos, width, height);

        g.setColor(Color.RED);
        Font f = new Font("Arial", Font.PLAIN, 20);
        FontMetrics metrics = g.getFontMetrics(f);
        int drawXPos = xpos + ((width - metrics.stringWidth(promptText))/2);
        int drawYPos = ypos + ((height + metrics.getHeight())/2 - 10);

        g.setFont(f); 
        g.drawString(promptText, drawXPos, drawYPos);
                
    }

    public void reset(){
        promptText = "";
    }

    public void addLetter(char c){
        promptText = promptText + c;
    }

    public void removeLastLetter(){
        if(promptText != null && promptText.length() > 0){
            promptText = promptText.substring(0,promptText.length()-1);
        }else{
            promptText = "";
        }
    }

    public String getPromptText(){
        return promptText;
    }
}
