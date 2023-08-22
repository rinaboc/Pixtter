package core;

import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import entities.Box;
import entities.Line;
import entities.Player;
import input.InputManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import levels.LevelManager;
import utils.graphics.Renderable;
import utils.Constants.SCR;
import utils.simulation.Updateable;
import utils.graphics.CameraComponent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import utils.graphics.UIHandler;
import utils.math.Vec2D;


/**
 * Game class, handles everything.
 */
public class Main extends Application {

    private VBox root;
    private Scene scene;

    private Player player;
    private CameraComponent camera;
    private LevelManager levelManager;

    private final Vector<Renderable> renderComponents = new Vector<>();
    private final Vector<Updateable> updateComponents = new Vector<>();

    private final Vector<Box> colliderComponents = new Vector<>();

    private final ScheduledExecutorService appTasks = Executors.newScheduledThreadPool(2);

    @Override
    public void start(Stage primaryStage) {
        initWindow(primaryStage);
        initGame(primaryStage);
        primaryStage.show();
    }


    /**
     * Sets up game variables and parameters.
     * @param primaryStage
     */
    private void initGame(Stage primaryStage) {

        new InputManager(this); // input init

        // coordinate system for debugging purposes
        renderComponents.add(new Line(-1000, 0, 2000, 0, null));
        renderComponents.add(new Line(0, -1000, 0, 2000, null));
        for (int i = -SCR.WIDTH/16; i < SCR.WIDTH/16; i++) {
            renderComponents.add(new Line(i*8, -1000, i*8, 1000, null));
        }

        for (int i = -SCR.HEIGHT/16; i < SCR.HEIGHT/16; i++) {
            renderComponents.add(new Line(-1000, i*8, 1000, i*8, null));
        }

        // load levels
        this.levelManager = new LevelManager(this);

        // create player
        this.player = new Player(0, 0, 50, 24, this);
        this.renderComponents.add(this.player);

        // create camera and attach to player
        this.camera = new CameraComponent(this);
        this.camera.attachObject(player.getPhysicsComponent());
        this.camera.setLocalOffset(new Vec2D(0, 120d));

        // set up UI
        UIHandler uiHandler = new UIHandler();
        Label label = new Label("Hello UI.");
        label.setLayoutX(200d);
        label.setLayoutY(10d);
        uiHandler.addStaticUIComponent(label);

        this.camera.enableUI(uiHandler);
        this.camera.getUiHandler().addGraphicUIComponent(this.player.getStatusBar());
        root.getChildren().add(camera);

        // game loops init
        Thread startThreads = new Thread(() -> {
            appTasks.scheduleAtFixedRate(new Runnable() {

                @Override
                public void run() {
                    // update method
                    updateGame();
                }

            }, 100, 1000/SCR.UPS, TimeUnit.MILLISECONDS);

            appTasks.scheduleAtFixedRate(new Thread(new Runnable() {

                @Override
                public void run() {
                    // update method
                    renderGame();
                }

            }), 100, 1000/SCR.FPS, TimeUnit.MILLISECONDS);
        });
        startThreads.start();
    }


    /**
     * Sets up window.
     * @param primaryStage
     */
    private void initWindow(Stage primaryStage) {
        root = new VBox(5d);
        primaryStage.setTitle("Pixtter");

        scene = new Scene(root, SCR.WIDTH, SCR.HEIGHT);
//        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.setOnCloseRequest(event -> {
            appTasks.shutdown();
            System.exit(0);
        });
    }

    /**
     * Go through added components and update all of them.
     */
    private void updateGame() {
        for(Updateable object : updateComponents){
            object.update();
        }
    }

    /**
     * Render the game via the camera component.
     */
    private void renderGame() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                camera.renderScreen();
            }
        });
    }

    public Vector<Renderable> getRenderComponents(){
        return new Vector<>(renderComponents);
    }
    public void addRenderComponent(Renderable component){
        renderComponents.add(component);
    }

    /**
     * Searches for the given renderable compnent and deletes it if it's present in the renderComponents.
     * @param component
     */
    public void removeRenderComponent(Renderable component){
        if(!renderComponents.remove(component)){
            System.out.println("Couldn't remove render component.");
        }
    }

    public void addUpdateComponent(Updateable component){
        updateComponents.add(component);
    }

    public void removeUpdateComponent(Updateable component){
        if(!updateComponents.remove(component)){
            System.out.println("Couldn't remove update component.");
        }
    }

    public void addColliderComponent(Box collider){
        if(collider.isCollider() && !colliderComponents.contains(collider)){
            colliderComponents.add(collider);
        } else {
            System.out.println("Isn't a collider or container already contains the collider.");
        }
    }

    public void removeColliderComponent(Box collider){
        if(!colliderComponents.remove(collider)){
            System.out.println("Couldn't removed collider component.");
        }
    }

    public Vector<Box> getColliderComponents(){
        return new Vector<>(colliderComponents);
    }

    public Scene getScene() {
        return scene;
    }

    public CameraComponent getCamera() {
        return camera;
    }

    public Player getPlayer() {
        return player;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
