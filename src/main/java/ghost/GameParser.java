package ghost;

import org.json.simple.parser.ParseException;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class GameParser {
    private final String[] maps = new String[36];
    private int remain = 0;
    private long lives = 3;
    private long gameSpeed;
    private long frightenedLength;
    private boolean frightenedModeOn = false;
    private long[] ghostMode;
    private int ghostModeLength;
    public int frame = 0;
    private boolean haveChaser = false;
    private boolean haveWhim = false;

    private PImage horizontal;
    private PImage downLeft;
    private PImage downRight;
    private PImage upLeft;
    private PImage upRight;
    private PImage vertical;
    private PImage fruit;
    private PImage wakaState_Close;
    private PImage wakaState_Down;
    private PImage wakaState_Left;
    private PImage wakaState_Right;
    private PImage wakaState_Up;
    private PImage ghostImage;
    private PImage ambusherImage;
    private PImage chaserImage;
    private PImage ignorantImage;
    private PImage whimImage;
    private PImage superFruit;
    private PImage frightenedImage;
    private PImage sodaCan;

    private PFont char_Font;

    private Message playerPrePosition;
    private ArrayList<Message> ghostPrePosition;

    private Player player;
    private Message playerStartPoint;

    private ArrayList<Ghosts> ghosts;
    private ArrayList<Message> ghostsStartPoint;

    private int currentState = 0;
    private int stateStartTime = 0;

    public boolean debugMode = false;
    public int frightenedModeStartTime = 0;

    Message chaserPosition = null;

    public App gameWindow;

    public GameParser(App gameWindow){
        this.gameWindow = gameWindow;
    }

    public void resourcesBonding(){
        horizontal = gameWindow.loadImage(Objects.requireNonNull(this.getClass().getClassLoader().getResource("horizontal.png")).getPath());
        downLeft = gameWindow.loadImage(Objects.requireNonNull(this.getClass().getClassLoader().getResource("downLeft.png")).getPath());
        downRight = gameWindow.loadImage(Objects.requireNonNull(this.getClass().getClassLoader().getResource("downRight.png")).getPath());
        upLeft = gameWindow.loadImage(Objects.requireNonNull(this.getClass().getClassLoader().getResource("upLeft.png")).getPath());
        upRight = gameWindow.loadImage(Objects.requireNonNull(this.getClass().getClassLoader().getResource("upRight.png")).getPath());
        vertical = gameWindow.loadImage(Objects.requireNonNull(this.getClass().getClassLoader().getResource("vertical.png")).getPath());
        fruit = gameWindow.loadImage(Objects.requireNonNull(this.getClass().getClassLoader().getResource("fruit.png")).getPath());
        wakaState_Close = gameWindow.loadImage(Objects.requireNonNull(this.getClass().getClassLoader().getResource("playerClosed.png")).getPath());
        wakaState_Down = gameWindow.loadImage(Objects.requireNonNull(this.getClass().getClassLoader().getResource("playerDown.png")).getPath());
        wakaState_Left = gameWindow.loadImage(Objects.requireNonNull(this.getClass().getClassLoader().getResource("playerLeft.png")).getPath());
        wakaState_Right = gameWindow.loadImage(Objects.requireNonNull(this.getClass().getClassLoader().getResource("playerRight.png")).getPath());
        wakaState_Up = gameWindow.loadImage(Objects.requireNonNull(this.getClass().getClassLoader().getResource("playerUp.png")).getPath());
        ghostImage = gameWindow.loadImage(Objects.requireNonNull(this.getClass().getClassLoader().getResource("ghost.png")).getPath());
        ambusherImage = gameWindow.loadImage(Objects.requireNonNull(this.getClass().getClassLoader().getResource("ambusher.png")).getPath());
        chaserImage = gameWindow.loadImage(Objects.requireNonNull(this.getClass().getClassLoader().getResource("chaser.png")).getPath());
        ignorantImage = gameWindow.loadImage(Objects.requireNonNull(this.getClass().getClassLoader().getResource("ignorant.png")).getPath());
        whimImage = gameWindow.loadImage(Objects.requireNonNull(this.getClass().getClassLoader().getResource("whim.png")).getPath());
        frightenedImage = gameWindow.loadImage(Objects.requireNonNull(this.getClass().getClassLoader().getResource("frightened.png")).getPath());
        superFruit = gameWindow.loadImage(Objects.requireNonNull(this.getClass().getClassLoader().getResource("superFruit.png")).getPath());
        sodaCan = gameWindow.loadImage(Objects.requireNonNull(this.getClass().getClassLoader().getResource("sodacan.png")).getPath());
    }

    public void bondingFont(){
        char_Font = gameWindow.createFont("src/main/resources/PressStart2P-Regular.ttf", 32);
        gameWindow.textFont(char_Font);
    }

    private void gameInit(){
        debugMode = false;
        frightenedModeOn = false;
        frame = 0;
        gameWindow.keyCode = gameWindow.RIGHT;
        currentState = 0;
        stateStartTime = 0;

        for(int i = 0; i < lives; i++){
            renderLives(i * 30, 34 * 16);
        }

        if(playerStartPoint == null) {
            System.out.println("Map error: player no found");
        }
        player = new Player(gameWindow, playerStartPoint.X, playerStartPoint.Y, gameSpeed);
        for(Message eachGhost : ghostsStartPoint){
            if(eachGhost.ghostTypes == Ghosts.AMBUSHER){
                System.out.println("add ambusher");
                ghosts.add(new Ambusher(gameWindow, eachGhost.X, eachGhost.Y,gameSpeed));
                ghostPrePosition.add(new Message(eachGhost.X, eachGhost.Y, 0, null));
            } else if (eachGhost.ghostTypes == Ghosts.NORMAL){
                ghosts.add(new Ghost(gameWindow, eachGhost.X, eachGhost.Y,gameSpeed));
                ghostPrePosition.add(new Message(eachGhost.X, eachGhost.Y, 0, null));
            } else if (eachGhost.ghostTypes == Ghosts.CHASER){
                ghosts.add(new Chaser(gameWindow, eachGhost.X, eachGhost.Y,gameSpeed));
                ghostPrePosition.add(new Message(eachGhost.X, eachGhost.Y, 0, null));
            } else if (eachGhost.ghostTypes == Ghosts.IGNORANT){
                ghosts.add(new Ignorant(gameWindow, eachGhost.X, eachGhost.Y,gameSpeed));
                ghostPrePosition.add(new Message(eachGhost.X, eachGhost.Y, 0, null));
            } else if (eachGhost.ghostTypes == Ghosts.WHIM){
                ghosts.add(new Whim(gameWindow, eachGhost.X, eachGhost.Y,gameSpeed));
                ghostPrePosition.add(new Message(eachGhost.X, eachGhost.Y, 0, null));
            }
        }
    }

    public void mapRender(boolean firstTime){
        boolean firstLineBorder = true;
        int rowStart = 0;
        int rowEnd = 0;
        int colStart = 0;
        int colEnd = 0;
        for(int i = 0; i < 36; i++){
            boolean allRowZero = true;
            for(int j = 0; j < 28; j++){

                if(firstTime && maps[i].charAt(j) == 'p'){
                    //player location
                    if(allRowZero && gameWindow.gameMaps[i][j] != 0){
                        allRowZero = false;
                        colStart = j;
                    }
                    gameWindow.gameMaps[i][j] = 0;
                    playerStartPoint = new Message(j, i, 0, null);
                } else if (firstTime && maps[i].charAt(j) == 'g'){
                    //ghost location
                    if(allRowZero && gameWindow.gameMaps[i][j] != 0){
                        allRowZero = false;
                        colStart = j;
                    }
                    gameWindow.gameMaps[i][j] = 0;
                    Message newMsg = new Message(j, i, 0, null);
                    newMsg.ghostTypes = Ghosts.NORMAL;
                    ghostsStartPoint.add(newMsg);
                } else if(firstTime && maps[i].charAt(j) == 'a'){
                    //ghost location
                    if(allRowZero && gameWindow.gameMaps[i][j] != 0){
                        allRowZero = false;
                        colStart = j;
                    }
                    gameWindow.gameMaps[i][j] = 0;
                    Message newMsg = new Message(j, i, 0, null);
                    newMsg.ghostTypes = Ghosts.AMBUSHER;
                    ghostsStartPoint.add(newMsg);
                } else if(firstTime && maps[i].charAt(j) == 'c'){
                    //ghost location
                    if(allRowZero && gameWindow.gameMaps[i][j] != 0){
                        allRowZero = false;
                        colStart = j;
                    }
                    gameWindow.gameMaps[i][j] = 0;
                    Message newMsg = new Message(j, i, 0, null);
                    newMsg.ghostTypes = Ghosts.CHASER;
                    haveChaser = true;
                    ghostsStartPoint.add(0, newMsg);
                } else if(firstTime && maps[i].charAt(j) == 'i'){
                    //ghost location
                    if(allRowZero && gameWindow.gameMaps[i][j] != 0){
                        allRowZero = false;
                        colStart = j;
                    }
                    gameWindow.gameMaps[i][j] = 0;
                    Message newMsg = new Message(j, i, 0, null);
                    newMsg.ghostTypes = Ghosts.IGNORANT;
                    ghostsStartPoint.add(newMsg);
                } else if(firstTime && maps[i].charAt(j) == 'w'){
                    //ghost location
                    if(allRowZero && gameWindow.gameMaps[i][j] != 0){
                        allRowZero = false;
                        colStart = j;
                    }
                    gameWindow.gameMaps[i][j] = 0;
                    Message newMsg = new Message(j, i, 0, null);
                    newMsg.ghostTypes = Ghosts.WHIM;
                    haveWhim = true;
                    ghostsStartPoint.add(newMsg);
                } else {
                    if(firstTime) {
                        gameWindow.gameMaps[i][j] = Integer.parseInt(String.valueOf(maps[i].charAt(j)));
                    }
                    if(allRowZero && gameWindow.gameMaps[i][j] != 0){
                        if(firstLineBorder)
                            rowStart = i;
                        allRowZero = false;
                        colStart = j;
                    }
                    if(firstLineBorder && !allRowZero && gameWindow.gameMaps[i][j] != 0){
                        colEnd = j;
                    }
                }
            }
            if(!allRowZero){
                if(firstLineBorder){
                    firstLineBorder = false;
                }
                rowEnd = i;
            }
        }
        if(!haveChaser && haveWhim){
            System.out.println("There is a whim but no chaser in the game");
            gameWindow.exit();
        }
        gameWindow.mapLeft = colStart;
        gameWindow.mapRight = colEnd;
        gameWindow.mapUp = rowStart;
        gameWindow.mapDown = rowEnd;
    }

    public void mapElementRenderSwitch(int x, int y, boolean firstTime){
        switch (gameWindow.gameMaps[y][x]){
            case 0:{
                case0(x * 16, y * 16);
                break;
            }
            case 1:{
                case1(x * 16, y * 16);
                break;
            }
            case 2:{
                case2(x * 16, y * 16);
                break;
            }
            case 3:{
                case3(x * 16, y * 16);
                break;
            }
            case 4:{
                case4(x * 16, y * 16);
                break;
            }
            case 5:{
                case5(x * 16, y * 16);
                break;
            }
            case 6:{
                case6(x * 16, y * 16);
                break;
            }
            case 7:{
                if(firstTime)
                    remain++;
                case7(x * 16, y * 16);
                break;
            }
            case 8:{
                if(firstTime)
                    remain++;
                case8(x * 16, y * 16);
                break;
            }
            case 9:{
                case9(x * 16, y * 16);
                break;
            }
        }
    }

    private void case0(int x, int y){
        gameWindow.fill(0, 0, 0);
        gameWindow.stroke(0, 0, 0);
        gameWindow.rect(x, y, 16, 16);
    }

    private void case1(int x, int y){
        gameWindow.image(horizontal, x, y);
    }

    private void case2(int x, int y){
        gameWindow.image(vertical, x, y);
    }

    private void case3(int x, int y){
        gameWindow.image(upLeft, x, y);
    }

    private void case4(int x, int y){
        gameWindow.image(upRight, x, y);
    }

    private void case5(int x, int y){
        gameWindow.image(downLeft, x, y);
    }

    private void case6(int x, int y){
        gameWindow.image(downRight, x, y);
    }

    private void case7(int x, int y){
        gameWindow.image(fruit, x, y);
    }

    private void case8(int x, int y){ gameWindow.image(superFruit, x, y);}

    private void case9(int x, int y){ gameWindow.image(sodaCan, x, y);}

    private void renderLives(int x, int y){gameWindow.image(wakaState_Right, x, y);}


    public void playerRender(Message position){
        if(frame % 16 < 8){
            gameWindow.image(wakaState_Close, position.X - 4, position.Y - 5);
        } else {
            switch (position.direction){
                case PApplet.RIGHT:{
                    gameWindow.image(wakaState_Right, position.X - 4, position.Y - 5);
                    break;
                }
                case PApplet.LEFT:{
                    gameWindow.image(wakaState_Left, position.X - 4, position.Y - 5);
                    break;
                }
                case PApplet.DOWN:{
                    gameWindow.image(wakaState_Down, position.X - 5, position.Y - 4);
                    break;
                }
                case PApplet.UP:{
                    gameWindow.image(wakaState_Up, position.X - 5, position.Y - 4);
                    break;
                }
            }
        }
        int currentMatrixX = position.X / 16;
        int currentMatrixY = position.Y / 16;
        if(position.X % 16 == 0 && position.Y % 16 == 0 && gameWindow.gameMaps[currentMatrixY][currentMatrixX] == 7) {
            gameWindow.gameMaps[currentMatrixY][currentMatrixX] = 0;
            remain--;
            System.out.println(remain);
        }
        if(position.X % 16 == 0 && position.Y % 16 == 0 && gameWindow.gameMaps[currentMatrixY][currentMatrixX] == 8) {
            gameWindow.gameMaps[currentMatrixY][currentMatrixX] = 0;
            frightenedModeOn = true;
            frightenedModeStartTime = frame / 60;
            System.out.println("frightened mode start at "+ frightenedModeStartTime);
            remain--;
            System.out.println(remain);
        }
        if(position.X % 16 == 0 && position.Y % 16 == 0 && gameWindow.gameMaps[currentMatrixY][currentMatrixX] == 9) { // eat soda can
            gameWindow.gameMaps[currentMatrixY][currentMatrixX] = 0;
        }
    }

    private void ghostRender(Message position, int ghost_Type){
        if(frightenedModeOn){
            gameWindow.image(frightenedImage, position.X - 6, position.Y - 6);
        } else {
            if(ghost_Type == Ghosts.AMBUSHER){
                gameWindow.image(ambusherImage, position.X - 6, position.Y - 6);
            } else if(ghost_Type == Ghosts.CHASER){
                gameWindow.image(chaserImage, position.X - 6, position.Y - 6);
            } else if(ghost_Type == Ghosts.IGNORANT){
                gameWindow.image(ignorantImage, position.X - 6, position.Y - 6);
            } else if(ghost_Type == Ghosts.WHIM){
                gameWindow.image(whimImage, position.X - 6, position.Y - 6);
            } else {
                gameWindow.image(ghostImage, position.X - 6, position.Y - 6);
            }
        }
    }

    public void clear(int x, int y){
        int currentMatrixX = x / 16;
        int currentMatrixY = y / 16;

        int nextGirdX = currentMatrixX;
        int nextGirdY = currentMatrixY;

        if(gameWindow.keyCode % 2 == 0)
            nextGirdY++;
        else
            nextGirdX++;


        if(gameWindow.gameMaps[currentMatrixY][currentMatrixX] == 7){
            gameWindow.fill(0, 0, 0);
            gameWindow.stroke(0, 0, 0);
            gameWindow.rect(x - 5, y - 5, 26, 26);
            case7(currentMatrixX * 16, currentMatrixY * 16);
        } else if(gameWindow.gameMaps[currentMatrixY][currentMatrixX] == 8){
            gameWindow.fill(0, 0, 0);
            gameWindow.stroke(0, 0, 0);
            gameWindow.rect(x - 5, y - 5, 26, 26);
            case8(currentMatrixX * 16, currentMatrixY * 16);
        } else if(gameWindow.gameMaps[currentMatrixY][currentMatrixX] == 9){
            gameWindow.fill(0, 0, 0);
            gameWindow.stroke(0, 0, 0);
            gameWindow.rect(x - 5, y - 5, 26, 26);
            case9(currentMatrixX * 16, currentMatrixY * 16);
        } else {
            gameWindow.fill(0, 0, 0);
            gameWindow.stroke(0, 0, 0);
            gameWindow.rect(x - 5, y - 5, 26, 26);
        }

        if(gameWindow.gameMaps[nextGirdY][nextGirdX] == 7){
            gameWindow.fill(0, 0, 0);
            gameWindow.stroke(0, 0, 0);
            gameWindow.rect(x - 5, y - 5, 26, 26);
            case7(nextGirdX * 16, nextGirdY * 16);
        } else if(gameWindow.gameMaps[nextGirdY][nextGirdX] == 8){
            gameWindow.fill(0, 0, 0);
            gameWindow.stroke(0, 0, 0);
            gameWindow.rect(x - 5, y - 5, 26, 26);
            case8(nextGirdX * 16, nextGirdY * 16);
        }else if(gameWindow.gameMaps[nextGirdY][nextGirdX] == 9){
            gameWindow.fill(0, 0, 0);
            gameWindow.stroke(0, 0, 0);
            gameWindow.rect(x - 5, y - 5, 26, 26);
            case9(nextGirdX * 16, nextGirdY * 16);
        } else if(gameWindow.gameMaps[nextGirdY][nextGirdX] == 0) {
            gameWindow.fill(0, 0, 0);
            gameWindow.stroke(0, 0, 0);
            gameWindow.rect(x - 5, y - 5, 26, 26);
        }
    }

    private void ghostClear(int x, int y, int direction){
        int currentMatrixX = x / 16;
        int currentMatrixY = y / 16;

        int nextGirdX = currentMatrixX;
        int nextGirdY = currentMatrixY;

        if(direction % 2 == 0)
            nextGirdY++;
        else
            nextGirdX++;
        if(gameWindow.gameMaps[currentMatrixY][currentMatrixX] == 7){
            gameWindow.fill(0, 0, 0);
            gameWindow.stroke(0, 0, 0);
            gameWindow.rect(x - 6, y - 6, 28, 28);
            case7(currentMatrixX * 16, currentMatrixY * 16);

        } else if(gameWindow.gameMaps[currentMatrixX][currentMatrixY] == 8){
            gameWindow.fill(0, 0, 0);
            gameWindow.stroke(0, 0, 0);
            gameWindow.rect(x - 6, y - 6, 28, 28);
            case8(nextGirdX * 16, nextGirdY * 16);
        } else if(gameWindow.gameMaps[currentMatrixX][currentMatrixY] == 9){
            gameWindow.fill(0, 0, 0);
            gameWindow.stroke(0, 0, 0);
            gameWindow.rect(x - 6, y - 6, 28, 28);
            case9(nextGirdX * 16, nextGirdY * 16);
        } else {
            gameWindow.fill(0, 0, 0);
            gameWindow.stroke(0, 0, 0);
            gameWindow.rect(x - 6, y - 6, 28, 28);
        }

        if(gameWindow.gameMaps[nextGirdY][nextGirdX] == 7){
            gameWindow.fill(0, 0, 0);
            gameWindow.stroke(0, 0, 0);
            gameWindow.rect(x - 6, y - 6, 28, 28);
            case7(nextGirdX * 16, nextGirdY * 16);
        } else if(gameWindow.gameMaps[nextGirdY][nextGirdX] == 8){
            gameWindow.fill(0, 0, 0);
            gameWindow.stroke(0, 0, 0);
            gameWindow.rect(x - 6, y - 6, 28, 28);
            case8(nextGirdX * 16, nextGirdY * 16);
        } else if(gameWindow.gameMaps[nextGirdY][nextGirdX] == 9){
            gameWindow.fill(0, 0, 0);
            gameWindow.stroke(0, 0, 0);
            gameWindow.rect(x - 6, y - 6, 28, 28);
            case9(nextGirdX * 16, nextGirdY * 16);
        } else if(gameWindow.gameMaps[nextGirdY][nextGirdX] == 0) {
            gameWindow.fill(0, 0, 0);
            gameWindow.stroke(0, 0, 0);
            gameWindow.rect(x - 6, y - 6, 28, 28);
        }
    }




    public void clearLastFrame(){
        if(debugMode){
            gameWindow.background(0, 0, 0);
            mapRender(false);
            for(int i = 0; i < 36; i++) {
                for (int j = 0; j < 28; j++) {
                    mapElementRenderSwitch(j, i, false);
                }
            }
        } else {
            clear(playerPrePosition.X, playerPrePosition.Y);
            for(Message eachGhost : ghostPrePosition){
                ghostClear(eachGhost.X, eachGhost.Y, eachGhost.direction);
            }
        }
    }

    private void updateGhostMode(){
        if(frame % 60 == 0){
            if(frightenedModeOn){
                if(frame / 60  > frightenedModeStartTime + frightenedLength){
                    frightenedModeOn = false;
                    stateStartTime += frightenedLength;
                }
            }
            if(frame / 60 > stateStartTime + ghostMode[currentState]){
                currentState++;
                currentState = currentState % ghostModeLength;
                stateStartTime = frame / 60;
                System.out.println("Mode change!");
            }
        }
    }

    private void drawDebugLine(Message ghostMessage){
        gameWindow.stroke(255, 255, 255);
        gameWindow.line(ghostMessage.X + 8, ghostMessage.Y + 8, ghostMessage.target.X + 8, ghostMessage.target.Y + 8);
    }

    public void refreshData(){
        ghostsStartPoint = new ArrayList<>();
        String mapPath = null;
        ghosts = new ArrayList<>();
        ghostPrePosition = new ArrayList<>();
        remain = 0;

        try {
            ConfigReader configReader = new ConfigReader();
            gameSpeed = configReader.getSpeed();
            ghostMode = configReader.getGhostMode();
            lives = configReader.getLives();
            frightenedLength = configReader.getFrightenedLength();
            mapPath = configReader.getMapFile();
            ghostModeLength = 0;
            for(long time: ghostMode){
                ghostModeLength += time;
            }
        } catch (IOException | ParseException e){
            e.printStackTrace();
            System.exit(0);
        }

        File map = new File(mapPath);
        try {
            Scanner scanner = new Scanner(map);
            for(int i = 0; i < 36; i++){
                maps[i] = scanner.nextLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void runStartFrame(){
        gameWindow.background(0,0,0);
        refreshData();
    }

    public void drawStartMap(){
        gameWindow.frameRate(60);
        mapRender(true);
        for(int i = 0; i < 36; i++) {
            for (int j = 0; j < 28; j++) {
                mapElementRenderSwitch(j, i, true);
            }
        }
        gameInit();
        playerPrePosition = new Message(playerStartPoint.X, playerStartPoint.Y, 0, null);
    }

    private boolean catchTarget(Message ghostPosition, Message playerPosition){
        if((ghostPosition.X <= playerPosition.X && playerPosition.X < ghostPosition.X + 16) && (ghostPosition.Y <= playerPosition.Y && playerPosition.Y < ghostPosition.Y + 16)){
            System.out.println("error 1");
            System.out.println("ghost: X: " + ghostPosition.X + ", Y: "+ghostPosition.Y+", and player: Y: "+playerPosition.X+", Y: "+ playerPosition.Y);
            return true;
        }
        if((playerPosition.X <= ghostPosition.X && ghostPosition.X < playerPosition.X + 16) && (playerPosition.Y <= ghostPosition.Y && ghostPosition.Y < playerPosition.Y + 16)){
            System.out.println("error 2");
            System.out.println("ghost: X: " + ghostPosition.X + ", Y: "+ghostPosition.Y+", and player: Y: "+playerPosition.X+", Y: "+ playerPosition.Y);
            return true;
        }
        return false;
    }

    private int startFrame;
    private boolean startSleep = false;
    public boolean sleep(int seconds){
        if(!startSleep){
            startSleep = true;
            startFrame = frame;
            System.out.println("Start counting");
        } else {
            if(frame % 60 == 0){
                System.out.println(frame / 60 + "s");
            }
            if(frame > startFrame + seconds * 60){
                startSleep = false;
                return true;
            }
            return false;
        }
        return false;
    }

    public void runNewGame(){
        if(sleep(10)){
            runStartFrame();
        }
    }

    public void runEachFrame(){
        frame++;
        if(remain == 0){
            gameWindow.background(0, 0, 0);
            gameWindow.fill(255, 255, 255);
            gameWindow.text("YOU WIN", 90, 300);
            runNewGame();
        } else if (lives == 0){
            System.out.println(frame);
            gameWindow.background(0, 0, 0);
            gameWindow.fill(255, 255, 255);
            gameWindow.text("GAME OVER", 80, 300);
            runNewGame();
        } else {
            clearLastFrame();
            updateGhostMode();
            playerPrePosition = player.run();
            for(int i = 0; i < ghosts.size(); i++){
                Message ghostPosition;
                int ghost_Type = ghosts.get(i).getGhost_Type();

                if(ghost_Type == Ghosts.WHIM){ // if ghost is type whim, we have to pass the position of chaser
                    Whim whim = (Whim) ghosts.get(i);
                    whim.passChaserPosition(chaserPosition);
                }

                if(frightenedModeOn){// different behaviour when in frightened mode and normal two modes
                    ghostPosition = ghosts.get(i).escape(frame);
                } else {
                    if (currentState % 2 == 0){
                        ghostPosition = ghosts.get(i).run(Ghost.SCATTERMODE, playerPrePosition);
                    } else{
                        ghostPosition = ghosts.get(i).run(Ghost.CHASEMODE, playerPrePosition);
                    }
                }

                if(i == 0 && ghost_Type == Ghosts.CHASER){
                    chaserPosition = ghostPosition;
                } else if (i == 0){
                    chaserPosition = null;
                }

                if(debugMode) // in debug mode, need to draw line
                    drawDebugLine(ghostPosition);
                Message.copy(ghostPrePosition.get(i), ghostPosition);
                if(catchTarget(ghostPosition, playerPrePosition)){
                    if(frightenedModeOn){
                        ghosts.remove(i);
                        ghostPrePosition.remove(i);
                        i--;
                        continue;

                    } else {
                        lives--;
                        if(lives > 0){
                            gameWindow.background(0, 0, 0);
                            ghosts.clear();
                            ghostPrePosition.clear();
                            mapRender(false);
                            for(int k = 0; k < 36; k++) {
                                for (int j = 0; j < 28; j++) {
                                    mapElementRenderSwitch(j, k, false);
                                }
                            }
                            gameInit();
                        }
                    }
                }
                ghostRender(ghostPrePosition.get(i), ghost_Type);
            }


            playerRender(playerPrePosition);
        }
    }
}
