import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class TesterPanel extends JPanel implements Runnable {

    Thread gameThread;
    Graphics graphics;
    Image image;
    Image backgroundImage;

    public final static int TESTER_WIDTH = 791;
    public final static int TESTER_HEIGHT = 731;
    public final Dimension TESTER_DIMENSIONS = new Dimension(TESTER_WIDTH, TESTER_HEIGHT);

    public final int FRAMERATE = 1;

    public boolean easyMode;
    public boolean flashMode;

    private Scanner sc;

    Set<GreekLifeStructure> greekLifeStructures;

    TesterPanel() throws IOException {
        this.setFocusable(true);
        this.setBackground(Color.BLACK);
        this.setPreferredSize(new Dimension(TESTER_DIMENSIONS));

        Scanner scFile = new Scanner(new File("src\\UWFratSorDatabase.txt"));
        greekLifeStructures = new HashSet<>();
        
        sc = new Scanner(System.in);
        System.out.println("This is a game to test your knowledge of the Greek Life\nhouses at the University of Washington. You will be\nshown a house (yellow) and you must guess the name of the house.\nIf you guess correctly, the house will turn blue (fraternity) or\nred (sorority). If you guess incorrectly, the house will\nturn black. You will have three attempts to guess the\nhouse. If you guess incorrectly three times, the correct\nanswer will be displayed and the house will turn black.\n\nWould you like to play in easy mode? (y/n/flash)");
        String response = sc.nextLine();
        if(response.toLowerCase().equals("y")) {
            easyMode = true;
        } else if(response.toLowerCase().equals("flash")) {
            easyMode = true;
            flashMode = true;
        } else {
            easyMode = false;
        }

        while(scFile.hasNextLine()) {
            String line = scFile.nextLine();
            greekLifeStructures.add(new GreekLifeStructure(line));
        }
        backgroundImage = ImageIO.read(new File("src\\UWFratSorDatabase.png"));
        Thread thread = new Thread(this);
        thread.start();
    }

    public void paint(Graphics g) {
        image = createImage(getWidth(), getHeight());
        graphics = image.getGraphics();
        graphics.drawImage(backgroundImage, 0,0, this);
        if(flashMode) {
            if(easyMode) {
                easyMode = false;
            } else {
                easyMode = true;
            }
        }
        for(GreekLifeStructure greekLifeStructure : greekLifeStructures) {
            if(!greekLifeStructure.hasAnotherAttempt){
                graphics.setColor(Color.BLACK);
            } else if(greekLifeStructure.isActive) { //currently is being displayed
                graphics.setColor(Color.YELLOW);
            } else if(greekLifeStructure.gender == 0) { //fraternity
                if(greekLifeStructure.isCorrect) {
                    graphics.setColor(Color.BLUE);
                } else {
                    graphics.setColor(Color.CYAN);
                }
            } else { //sorority
                if(greekLifeStructure.isCorrect) {
                    graphics.setColor(Color.RED);
                } else {
                    graphics.setColor(Color.PINK);
                }
            }
            graphics.fillRect(greekLifeStructure.x, greekLifeStructure.y, greekLifeStructure.width, greekLifeStructure.height);
            if(easyMode || greekLifeStructure.isCorrect) {
                graphics.setFont(new Font("Arial", Font.PLAIN, 12));
                if(!greekLifeStructure.hasAnotherAttempt){
                    if(greekLifeStructure.gender == 0) {
                        graphics.setColor(Color.CYAN);
                    } else {
                        graphics.setColor(Color.RED);
                    }
                } else {
                    graphics.setColor(Color.BLACK);
                }
                graphics.drawString(greekLifeStructure.displayName, greekLifeStructure.x, greekLifeStructure.y + 12);
            }
        }
        g.drawImage(image, 0, 0, this);
    }
    public GreekLifeStructure nextHouse() {
        boolean found = false;
        int size = greekLifeStructures.size();
        int numCorrect = 0;
        for(GreekLifeStructure greekLifeStructure : greekLifeStructures) {
            if(greekLifeStructure.isCorrect) {
                numCorrect++;
            }
        }
        
        if(numCorrect == size) {
            for(GreekLifeStructure greekLifeStructure : greekLifeStructures) {
                if(!greekLifeStructure.hasAnotherAttempt) {
                    greekLifeStructure.hasAnotherAttempt = true;
                    greekLifeStructure.isCorrect = false;
                    numCorrect--;
                }
            }
            if(numCorrect == size) {
                System.out.println();
                System.out.println("All houses have been guessed correctly");
                System.out.println("Play again? (y/n)");
                String response = sc.nextLine();
                if(response.toLowerCase().equals("n")) {
                    System.exit(0);
                } else {
                    for(GreekLifeStructure greekLifeStructure : greekLifeStructures) {
                        greekLifeStructure.hasAnotherAttempt = true;
                        greekLifeStructure.isCorrect = false;
                    }
                }
            } else {
                System.out.println();
                System.out.println("You didn't guess all the houses by your second try.");
                System.out.println("Would you like try the ones you missed again? (y/n)");
                String response = sc.nextLine();
                if(response.toLowerCase().equals("n")) {
                    System.exit(0);
                }
            }
        }

        while(!found) {
            int item = new Random().nextInt(size);
            int i = 0;
            for(GreekLifeStructure greekLifeStructure : greekLifeStructures) {
                if(i == item) {
                    if(!greekLifeStructure.isCorrect) {
                        greekLifeStructure.isActive = true;
                        return greekLifeStructure;
                    }
                }
                item--;
            }
        }

        return null;
    }
    public void question(GreekLifeStructure greekLifeStructure) {
        System.out.println("What is the name of this house?");
        boolean correct = false;
        int attempts = 0;
        while(!correct) {
            String answer = sc.nextLine();
            if(greekLifeStructure.names.contains(answer)) {
                greekLifeStructure.isCorrect = true;
                correct = true;
            } else if(greekLifeStructure.hasAnotherAttempt) {
                System.out.println("Try again");
                attempts++;
                if(attempts == 2) {
                    greekLifeStructure.hasAnotherAttempt = false;
                }
            } else {
                System.out.println("The correct answer is " + greekLifeStructure.displayName);
                greekLifeStructure.isCorrect = true;
                correct = true;
            }
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerFrame = 1000000000 / FRAMERATE;
        double frameCounter = 0;
        
        GreekLifeStructure questionHouse = null;
        while(true) {
            long now = System.nanoTime();
            double deltaTime = (now - lastTime);

            frameCounter += deltaTime / nsPerFrame;
            
            lastTime = now;
            if(frameCounter >= 1) {
                System.out.println();
                System.out.println("--------------------");
                if(flashMode) {
                    sc.nextLine();
                } else {
                    questionHouse = nextHouse();
                }
                repaint();
                if(!flashMode) {
                    question(questionHouse);
                    questionHouse.isActive = false;
                }
                frameCounter--;
            
            }
        }
    }
}
