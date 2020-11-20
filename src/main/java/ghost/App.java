package ghost;
import processing.core.PApplet;
import processing.event.KeyEvent;
public class App extends PApplet {
    public static final int WIDTH = 448;
    public static final int HEIGHT = 576;
    public int[][] gameMaps = new int[36][28];
    public int mapLeft, mapDown, mapRight, mapUp;
    public GameParser gameParser;
    public App(){
        gameParser = new GameParser(this);
    }

    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    @Override
    public void setup() {
        gameParser.resourcesBonding();
        gameParser.runStartFrame();
        gameParser.bondingFont();
        gameParser.drawStartMap();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == ' '){
            if(gameParser.debugMode){
                gameParser.clearLastFrame();
                gameParser.debugMode = false;
            } else {
                gameParser.debugMode = true;
            }
        }
    }

    @Override
    public void draw() {
        gameParser.runEachFrame();
    }

    public static void main(String[] args) {
        PApplet.main("ghost.App");
    }
}
