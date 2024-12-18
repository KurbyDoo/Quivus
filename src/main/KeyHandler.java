package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

public class KeyHandler implements KeyListener {
    private final HashMap<Integer, Boolean> pressed = new HashMap<>();

    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Stores pressed keys in a boolean hashmap
     * <p>
     * @param e the key event that occurred
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        pressed.put(code, true);

    }

    /**
     * Removes pressed keys from the boolean hashmap
     * <p>
     * @param e the key event that occurred
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        pressed.put(code, false);
    }

    /**
     * Returns whether a current key is pressed
     * <p>
     * @param code is a valid key code
     * @return whether the key was pressed or not
     */
    public boolean checkPressed(int code) {
        return pressed.getOrDefault(code, false);
    }
}
