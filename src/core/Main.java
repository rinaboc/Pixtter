package core;

import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import entities.Line;
import entities.Player;
import input.InputManager;
import javafx.application.Application;
import javafx.stage.Stage;
import levels.LevelManager;
import utils.graphic.Renderable;
import utils.Constants.SCR;
import utils.Updateable;
import utils.CameraComponent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;


public class Main extends Application {

    private VBox root;
    private Scene scene;

    private Player player;
    private CameraComponent camera;
    private LevelManager levelManager;

    private final Vector<Renderable> renderComponents = new Vector<>();
    private final Vector<Updateable> updateComponents = new Vector<>();

    private final ScheduledExecutorService appTasks = Executors.newScheduledThreadPool(2);

    @Override
    public void start(Stage primaryStage) {
        initWindow(primaryStage);
        initGame(primaryStage);
        primaryStage.show();
    }


    private void initGame(Stage primaryStage) {

        new InputManager(this);
        player = new Player(0, 0, 50, 24);
        addUpdateComponent(player);

        renderComponents.add(new Line(-1000, 0, 2000, 0));
        renderComponents.add(new Line(0, -1000, 0, 2000));
        for (int i = -SCR.WIDTH/16; i < SCR.WIDTH/16; i++) {
            renderComponents.add(new Line(i*8, -1000, i*8, 1000));
        }

        for (int i = -SCR.HEIGHT/16; i < SCR.HEIGHT/16; i++) {
            renderComponents.add(new Line(-1000, i*8, 1000, i*8));
        }
        renderComponents.add(player);

        levelManager = new LevelManager(this);

        this.camera = new CameraComponent(this);
        this.camera.attachObject(player.getPhysicsComponent());
        root.getChildren().add(camera);


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

    private void updateGame() {
        for(Updateable object : updateComponents){
            object.update();
        }
    }

    private void renderGame() {
        camera.renderDebug();
    }

    public Vector<Renderable> getRenderComponents(){
        return new Vector<Renderable>(renderComponents);
    }
    public void addRenderComponent(Renderable component){
        renderComponents.add(component);
    }

    public void addUpdateComponent(Updateable component){
        updateComponents.add(component);
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
