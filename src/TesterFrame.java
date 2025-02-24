import java.io.IOException;

import javax.swing.JFrame;

public class TesterFrame extends JFrame {

    TesterPanel panel;

    public TesterFrame() throws IOException {
        panel = new TesterPanel();

        this.add(panel);
        this.setTitle("Tester");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        
        this.setVisible(true);
    }
    
}
