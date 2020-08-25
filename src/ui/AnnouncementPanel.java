package ui;

import domain.Board;
import domain.Game;
import interfaces.IBoard;
import interfaces.IGame;
import interfaces.IManager;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class AnnouncementPanel extends JPanel implements MouseListener {

    private static final int ANNOUNCEMENT_PANEL_WIDTH = MainGUI.PANEL_WIDTH - TopLeftPanel.OPTIONS_PANEL_WIDTH - 25;
    private static final int ANNOUNCEMENT_PANEL_HEIGHT = 128;

    static final String FLAG_BUTTON_ACTION = "Flag Button";

    static final String LEVEL_COMPLETED_STRING = "LEVEL COMPLETED!";
    static final String LEVELS_FINISHED_STRING = "CONGRATULATIONS, YOU WIN!!!";
    static final String GAME_LOST_STRING = "BOOM! GAME OVER. TRY AGAIN!";
    static final String NO_RESULT = "";

    private String gameStateString;
    private String gameResultString;

    private JButton flagButton;

    private JLabel gameStateLabel;
    private JLabel gameResultLabel;

    private IManager manager;
    private IBoard board;
    private IGame game;

    AnnouncementPanel(MainGUI mainGUI, IManager manager) {

        this.manager = manager;
        this.board = Board.getInstance();
        this.game = Game.getInstance();

        setLayout(null);
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        setBounds(5 + TopLeftPanel.OPTIONS_PANEL_WIDTH + 5, 5, ANNOUNCEMENT_PANEL_WIDTH, ANNOUNCEMENT_PANEL_HEIGHT );

        setGameStateString();
        setGameResultString(NO_RESULT);

        gameStateLabel = new JLabel();
        gameStateLabel.setFont(new Font("Monospaced", Font.BOLD, 23));
        gameStateLabel.setForeground(Color.BLACK);
        gameStateLabel.setVerticalAlignment(SwingConstants.CENTER);
        gameStateLabel.setBounds(35, 0, 250, ANNOUNCEMENT_PANEL_HEIGHT);
        add(gameStateLabel);

        gameResultLabel = new JLabel();
        gameResultLabel.setFont(new Font("Monospaced", Font.BOLD, 30));
        gameResultLabel.setForeground(Color.BLACK);
        gameResultLabel.setVerticalAlignment(SwingConstants.CENTER);
        gameResultLabel.setBounds(295, 0, ANNOUNCEMENT_PANEL_WIDTH - 330, ANNOUNCEMENT_PANEL_HEIGHT);
        add(gameResultLabel);

        flagButton = new JButton(new ImageIcon(getClass().getResource("/res/gray_flag.png")));
        // below works too
        // flagButton = new JButton(new ImageIcon(ClassLoader.getSystemResource("res/gray_flag.png")));
        flagButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        flagButton.setBounds(MainGUI.PANEL_WIDTH - TopLeftPanel.OPTIONS_PANEL_WIDTH - 125, 30, 64, 64);
        flagButton.setBackground(Color.lightGray);
        flagButton.setActionCommand(FLAG_BUTTON_ACTION);
        flagButton.addMouseListener(this);
        flagButton.addActionListener(mainGUI);
        add(flagButton);

    }

    public void paintComponent(Graphics g) {

        gameStateLabel.setText(gameStateString);
        gameResultLabel.setText(gameResultString);

        flagButton.setEnabled(game.getState() == IGame.State.PLAYING || game.getState() == Game.State.PLACING_FLAGS);

        g.setColor(new JButton().getBackground());
        g.fillRect(0, 0, ANNOUNCEMENT_PANEL_WIDTH, ANNOUNCEMENT_PANEL_HEIGHT);
        g.setColor(Color.BLUE);
        g.fillRect(6, 6, ANNOUNCEMENT_PANEL_WIDTH - 12, ANNOUNCEMENT_PANEL_HEIGHT - 12);
        g.setColor(new JButton().getBackground());
        g.fillRect(9, 9, ANNOUNCEMENT_PANEL_WIDTH - 18, ANNOUNCEMENT_PANEL_HEIGHT - 18);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (! flagButton.isEnabled()) return;
        flagButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createBevelBorder(BevelBorder.LOWERED), BorderFactory.createLineBorder(Color.DARK_GRAY, 1)));
        this.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (! flagButton.isEnabled()) return;
        flagButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createBevelBorder(BevelBorder.RAISED), BorderFactory.createLineBorder(Color.DARK_GRAY, 1)));
        this.repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (! flagButton.isEnabled()) return;
        flagButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createBevelBorder(BevelBorder.RAISED), BorderFactory.createLineBorder(Color.DARK_GRAY, 1)));
        this.repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (! flagButton.isEnabled()) return;
        flagButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        this.repaint();
    }

    public void setGameStateString(String gameStateString) {
        this.gameStateString = gameStateString;
    }

    public void setGameResultString(String gameResultString) {
        this.gameResultString = gameResultString;
    }

    void setGameStateString() {
        gameStateString = "<html>LEVEL &nbsp;&nbsp&nbsp;&nbsp&nbsp;: " + manager.getCurrentLevel() + "/" + manager.getWinLevel() + "<br>" +
                "BOMBS &nbsp;&nbsp&nbsp;&nbsp&nbsp;: " + manager.getnBombs() + "<br>" +
                "TILES LEFT : " + board.nTilesToUncover() + "</html>";
    }
}




