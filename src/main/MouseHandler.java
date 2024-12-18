package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

public class MouseHandler extends MouseAdapter {
    private int x, y;
    private final HashMap<Integer, Boolean> pressed = new HashMap<Integer, Boolean>();

    public Point getPoint(MouseEvent e) {
        return e.getPoint();
    }

    public boolean leftPressed() {
        return pressed.getOrDefault(MouseEvent.BUTTON1, false);
    }

    public boolean middlePressed() {
        return pressed.getOrDefault(MouseEvent.BUTTON2, false);
    }

    public boolean rightPressed() {
        return pressed.getOrDefault(MouseEvent.BUTTON3, false);
    }
    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {pressed.put(e.getButton(), true);}

    @Override
    public void mouseReleased(MouseEvent e) {pressed.put(e.getButton(), false);}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }

    public int getX() {return x;}
    public int getY() {return y;}

}
