package ghost;

public class Player {
    int X;
    int Y;
    long speed;
    int currentMatrixX = 0;
    int currentMatrixY = 0;
    int nextMatrixX = 2;
    int nextMatrixY = 4;
    int previousDirection;
    int currentDirection;
    int waitingDirection;
    boolean wrongClick = false;
    App windowsFrame;
    public Player(App windowsFrame, int origX, int origY, long speed){
        this.windowsFrame = windowsFrame;
        previousDirection = windowsFrame.RIGHT;
        currentDirection = windowsFrame.keyCode;
        X = origX * 16;
        Y = origY * 16;
        this.speed = speed;
    }
    public boolean isOpposite(){
        if(previousDirection % 2 == 0){
            return currentDirection % 2 == 0;
        } else {
            return currentDirection % 2 == 1;
        }
    }

    public void walk(int input){
        if(input == windowsFrame.UP){
            Y -= speed;
        } else if (input == windowsFrame.DOWN){
            Y += speed;
        } else if (input == windowsFrame.RIGHT){
            X += speed;
        } else if (input == windowsFrame.LEFT){
            X -= speed;
        }
    }

    public boolean isWall(int nextX, int nextY){
        return !(windowsFrame.gameMaps[nextY][nextX] == 0 || windowsFrame.gameMaps[nextY][nextX] == 7 || windowsFrame.gameMaps[nextY][nextX] == 8 || windowsFrame.gameMaps[nextY][nextX] == 9);
    }
    /**
     * what predict do:
     *   1.calculate current Grid
     *   2.calculate next Grid (this next grid is different to the one use in clear)
     * @param nextWay
     */
    public void predict(int nextWay){
        currentMatrixX = X / 16;
        currentMatrixY = Y / 16;
        if(nextWay % 2 == 0) {
            nextMatrixX = currentMatrixX;
            nextMatrixY = currentMatrixY + (nextWay - 39);
        } else {
            nextMatrixY = currentMatrixY;
            nextMatrixX = currentMatrixX + (nextWay - 38);
        }
    }

    int previousDirectNextMatrixX;
    int previousDirectNextMatrixY;
    public Message run() {
        if (windowsFrame.keyCode == windowsFrame.LEFT || windowsFrame.keyCode == windowsFrame.RIGHT || windowsFrame.keyCode == windowsFrame.UP || windowsFrame.keyCode == windowsFrame.DOWN) {
            currentDirection = windowsFrame.keyCode;
            wrongClick = false;

        }
        if(currentDirection != previousDirection){
            if(isOpposite() && waitingDirection == -1){
                walk(currentDirection);
                previousDirection = currentDirection;
            } else {
                waitingDirection = currentDirection;
                if(X % 16 == 0 && Y % 16 == 0){
                    predict(waitingDirection);
                    if(isWall(nextMatrixX, nextMatrixY)) {
                        if(previousDirection % 2 == 0) {
                            previousDirectNextMatrixX = currentMatrixX;
                            previousDirectNextMatrixY = currentMatrixY + (previousDirection - 39);
                        } else {
                            previousDirectNextMatrixY = currentMatrixY;
                            previousDirectNextMatrixX = currentMatrixX + (previousDirection - 38);
                        }
                        if(!isWall(previousDirectNextMatrixX, previousDirectNextMatrixY))
                            walk(previousDirection);
                    }
                    else {
                        previousDirection = waitingDirection;
                        walk(waitingDirection);
                        waitingDirection = -1;
                    }
                } else {
                    walk(previousDirection);
                }
                if(X % 16 == 0) {
                    currentMatrixX = X / 16;
                    nextMatrixX = currentMatrixX + (currentDirection - 38);
                }
                if(Y % 16 == 0) {
                    currentMatrixY = Y / 16;
                    nextMatrixY = currentMatrixY + (currentDirection - 39);
                }
            }
        } else {
            predict(previousDirection);
            if(!isWall(nextMatrixX, nextMatrixY) || X % 16 != 0 || Y % 16 !=0){
                walk(previousDirection);
            }
        }
        return new Message(X, Y, previousDirection, null);
    }
}

