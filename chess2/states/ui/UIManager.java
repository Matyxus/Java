package chess2.states.ui;
import java.util.ArrayList;
import chess2.main.Handler;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

public class UIManager {

    private Handler handler;
    private ArrayList<UIObject> objects;

    public UIManager(Handler handler) {
        this.handler = handler;
        objects = new ArrayList<UIObject>();
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setObjects(ArrayList<UIObject> objects) {
        this.objects = objects;
    }

    public ArrayList<UIObject> getObjects() {
        return objects;
    }


    public void tick(){
        for (UIObject o : objects) {
            o.tick();
        }
    }

    public void render(Graphics g){
        for (UIObject o : objects) {
            o.render(g);
        }
    }

    public void onMouseMove(MouseEvent e){
        for (UIObject o : objects) {
            o.onMouseMove(e);
        }
    }
    

    public void onMouseRelease(MouseEvent e){
        for (UIObject o : objects) {
            o.onMouseRelease(e);
        }
    }

    public void addObject(UIObject o){
        objects.add(o);

    }

    public void removeObject(UIObject o){
        objects.remove(o);
    }

    public void clean(){
        objects.clear();
    }

}
