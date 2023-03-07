package managers;

import ui.UiObject;

import java.util.ArrayList;

import gui.Renderer;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

public class UiManager implements Renderer {

    private ArrayList<UiObject> objects;

    public UiManager() {
        objects = new ArrayList<UiObject>();
    }

    @Override
    public void render(Graphics g) {
        for (UiObject o : objects) {
            o.render(g);
        }
    }

    @Override
    public void update(MouseEvent e) {};

    // ----------------------------------- Mouse -----------------------------------

    public void onMouseMove(MouseEvent e) {
        for (UiObject o : objects) {
            o.onMouseMove(e);
        }
    }
    
    public void onMouseRelease(MouseEvent e) {
        for (UiObject o : objects) {
            o.onMouseRelease(e);
        }
    }

    // ----------------------------------- Utils -----------------------------------

    public void addObject(UiObject o) {
        objects.add(o);
    }

    public void removeObject(UiObject o) {
        objects.remove(o);
    }

    public void clear() {
        objects.clear();
    }
    
    public void setObjects(ArrayList<UiObject> objects) {
        this.objects = objects;
    }

    public ArrayList<UiObject> getObjects() {
        return objects;
    }
}
