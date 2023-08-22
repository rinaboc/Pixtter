package utils.graphics;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import utils.simulation.Updateable;

import java.util.Vector;

public class UIHandler extends Group implements Renderable, Updateable {

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

    public void addStaticUIComponent(Node component){
        this.getChildren().add(component);
    }

    public void addGraphicUIComponent(UIComponent component){
        uiComponents.add(component);
    }
}
