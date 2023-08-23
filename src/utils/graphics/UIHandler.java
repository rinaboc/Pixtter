package utils.graphics;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import utils.simulation.Updateable;

import java.util.Vector;

public class UIHandler extends Group implements Renderable, Updateable {

    // custom uiComponents which aren't Node types (rely on draw)
    Vector<UIComponent> uiComponents = new Vector<>();

    public UIHandler(){

    }

    @Override
    public void update() {
        for(Updateable component : uiComponents){
            component.update();
        }
    }

    @Override
    public void render(GraphicsContext g) {
        for(Renderable component: uiComponents){
            component.render(g);
        }
    }

    @Override
    public void setOffset(double x, double y) {
        for(Renderable r: uiComponents){
            r.setOffset(x, y);
        }
    }

    /**
     * Adds a Node type component to its group.
     * @param component
     */
    public void addStaticUIComponent(Node component){
        this.getChildren().add(component);
    }

    /**
     * Custom ui components are handled separately. They are in the update as well as the render loop.
     * @param component custom UIComponent that relies on graphics
     */
    public void addGraphicUIComponent(UIComponent component){
        uiComponents.add(component);
    }
}
