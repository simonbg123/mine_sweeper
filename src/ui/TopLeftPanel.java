package ui;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

class TopLeftPanel extends JPanel {

    static final int OPTIONS_PANEL_WIDTH = 144;
    private static final int OPTIONS_PANEL_HEIGHT = 128;

    static final String CONTINUE_BUTTON_STRING = "Continue";
    static final String NEWGAME_BUTTON_STRING = "New Game";

    private boolean continueIsVisible = false;

    private JButton continueButton;
    private JButton newGameButton;

    TopLeftPanel(MainGUI mainGUI) {

        setLayout(null);
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        setBounds(5, 5, OPTIONS_PANEL_WIDTH, OPTIONS_PANEL_HEIGHT);


        newGameButton = new JButton(NEWGAME_BUTTON_STRING);
        newGameButton.setBounds(2, 2, OPTIONS_PANEL_WIDTH - 4, OPTIONS_PANEL_HEIGHT / 2 - 3 );
        newGameButton.addActionListener(mainGUI);

        continueButton = new JButton(CONTINUE_BUTTON_STRING);
        continueButton.setBounds(2, OPTIONS_PANEL_HEIGHT / 2, OPTIONS_PANEL_WIDTH - 4, OPTIONS_PANEL_HEIGHT / 2 - 3);
        continueButton.addActionListener(mainGUI);

        add(newGameButton);
        add(continueButton);

    }

    public void paintComponent(Graphics g) {
        continueButton.setEnabled(continueIsVisible);
    }

    void setContinueIsVisible(boolean continueIsVisible) {
        this.continueIsVisible = continueIsVisible;
    }
}