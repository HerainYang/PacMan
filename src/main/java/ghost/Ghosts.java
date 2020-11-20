package ghost;

public abstract class Ghosts {
    int X;
    int Y;
    int current_Matrix_X = 0;
    int current_Matrix_Y = 0;
    long speed;
    int next_Matrix_X = 2;
    int next_Matrix_Y = 4;
    //    int previousDirection;
    App windowsFrame;

    Message leftUp;
    Message rightUp;
    Message leftDown;
    Message rightDown;

    static final int SCATTERMODE = 0;
    static final int CHASEMODE = 1;

    static final int NORMAL = 0;
    static final int AMBUSHER = 1;
    static final int CHASER = 2;
    static final int IGNORANT = 3;
    static final int WHIM = 4;

    public int currentDirection;
    private int ghost_Type;

    public Ghosts(App windowsFrame, int origX, int origY, long speed) {
        this.windowsFrame = windowsFrame;
        currentDirection = windowsFrame.RIGHT;
        X = origX * 16;
        Y = origY * 16;

        leftUp = new Message(windowsFrame.mapLeft * 16, windowsFrame.mapUp * 16, 0, null);
        rightUp = new Message(windowsFrame.mapRight * 16, windowsFrame.mapUp * 16, 0, null);
        leftDown = new Message(windowsFrame.mapLeft * 16, windowsFrame.mapDown * 16, 0, null);
        rightDown = new Message(windowsFrame.mapRight * 16, windowsFrame.mapDown * 16, 0, null);
        this.speed = speed;
    }

    public void setGhost_Type(int ghost_Type) {
        this.ghost_Type = ghost_Type;
    }

    public int getGhost_Type() {
        return ghost_Type;
    }

    public void walk() {
        if (currentDirection == windowsFrame.UP) {
            Y -= speed;
        } else if (currentDirection == windowsFrame.DOWN) {
            Y += speed;
        } else if (currentDirection == windowsFrame.RIGHT) {
            X += speed;
        } else if (currentDirection == windowsFrame.LEFT) {
            X -= speed;
        }
    }

    public boolean accessible(int direction) {
        current_Matrix_X = X / 16;
        current_Matrix_Y = Y / 16;
        if (direction % 2 == 0) {
            int move = direction - 39;
            if(current_Matrix_Y + move < 0){
                System.out.println("out of bound: inaccessible");
                return false;
            }
            return windowsFrame.gameMaps[current_Matrix_Y + move][current_Matrix_X] == 0 || windowsFrame.gameMaps[current_Matrix_Y + move][current_Matrix_X] == 7 || windowsFrame.gameMaps[current_Matrix_Y + move][current_Matrix_X] == 8 || windowsFrame.gameMaps[current_Matrix_Y + move][current_Matrix_X] == 9;
        } else {
            int move = direction - 38;
            if(current_Matrix_X + move < 0){
                System.out.println("out of bound: inaccessible");
                return false;
            }
            return windowsFrame.gameMaps[current_Matrix_Y][current_Matrix_X + move] == 0 || windowsFrame.gameMaps[current_Matrix_Y][current_Matrix_X + move] == 7 || windowsFrame.gameMaps[current_Matrix_Y][current_Matrix_X + move] == 8 || windowsFrame.gameMaps[current_Matrix_Y + move][current_Matrix_X] == 9;
        }
    }

    public double countingDistance(Message nextPosition, Message targetPosition) {
        current_Matrix_X = X / 16;
        current_Matrix_Y = Y / 16;
        return (int) Math.sqrt(Math.pow((targetPosition.X >> 4) - nextPosition.X, 2) + Math.pow((targetPosition.Y >> 4) - nextPosition.Y, 2));
    }

    public Message predict(int nextWay) {
        current_Matrix_X = X / 16;
        current_Matrix_Y = Y / 16;
        if (nextWay % 2 == 0) {
            next_Matrix_X = current_Matrix_X;
            next_Matrix_Y = current_Matrix_Y + (nextWay - 39);
        } else {
            next_Matrix_Y = current_Matrix_Y;
            next_Matrix_X = current_Matrix_X + (nextWay - 38);
        }
        return new Message(next_Matrix_X, next_Matrix_Y, 0, null);
    }

    public int judgement(Message targetPosition) {
        int nextDirection = -1;
        double predictDistance;
        double distance = Integer.MAX_VALUE;

        if (currentDirection % 2 == 0) {
            if (accessible(currentDirection)) {
                predictDistance = countingDistance(predict(currentDirection), targetPosition);
                if (predictDistance < distance) {
                    nextDirection = currentDirection;
                    distance = predictDistance;
                }
            }
            if (accessible(windowsFrame.RIGHT)) {
                predictDistance = countingDistance(predict(windowsFrame.RIGHT), targetPosition);
                if (predictDistance < distance) {
                    nextDirection = windowsFrame.RIGHT;
                    distance = predictDistance;
                }
            }
            if (accessible(windowsFrame.LEFT)) {
                predictDistance = countingDistance(predict(windowsFrame.LEFT), targetPosition);
                if (predictDistance < distance) {
                    nextDirection = windowsFrame.LEFT;
                }
            }
        } else {
            if (accessible(currentDirection)) {
                predictDistance = countingDistance(predict(currentDirection), targetPosition);
                if (predictDistance < distance) {
                    nextDirection = currentDirection;
                    distance = predictDistance;
                }
            }
            if (accessible(windowsFrame.UP)) {
                predictDistance = countingDistance(predict(windowsFrame.UP), targetPosition);
                if (predictDistance < distance) {
                    nextDirection = windowsFrame.UP;
                    distance = predictDistance;
                }
            }
            if (accessible(windowsFrame.DOWN)) {
                predictDistance = countingDistance(predict(windowsFrame.DOWN), targetPosition);
                if (predictDistance < distance) {
                    nextDirection = windowsFrame.DOWN;
                }
            }
        }
        return nextDirection;
    }

    public boolean intersection() {
        if (currentDirection % 2 == 0) {
            return accessible(windowsFrame.RIGHT) || accessible(windowsFrame.LEFT);
        } else {
            return accessible(windowsFrame.UP) || accessible(windowsFrame.DOWN);
        }
    }

    public void turnBack(){
        if (currentDirection % 2 == 0){
            currentDirection += (39 - currentDirection) * 2;
        } else {
            currentDirection += (38 - currentDirection) * 2;
        }
    }

    /**
     * if(intersection)
     *   ->judgement
     * else if(no wall)
     *   ->go ahead
     * else if(is wall)
     *   ->turn back
     * @param mode
     * @param playerPosition
     */
    public Message run(int mode, Message playerPosition){
        Message target = findTarget(mode, playerPosition);
        if(X % 16 == 0 && Y % 16 == 0){
            if(intersection()){
                currentDirection = judgement(target);
                walk();
            } else if (accessible(currentDirection)) {
                walk();
            } else {
                //turn back
                System.out.println("turn back!");
                turnBack();
                walk();
            }
        } else {
            walk();
        }
        return new Message(X, Y, currentDirection, target);
    }


    private void randomDirection(int frame){
        int random = frame % 3;
        //vertical
        if(currentDirection % 2 == 0){
            do{
                if(random == 0){
                    currentDirection = windowsFrame.RIGHT;
                } else if (random == 1){
                    currentDirection = windowsFrame.LEFT;
                }
                random = (random + 1) % 3;
            } while (!accessible(currentDirection));
        } else {
            do{
                if(random == 0){
                    currentDirection = windowsFrame.UP;
                } else if (random == 1){
                    currentDirection = windowsFrame.DOWN;
                }
                random = (random + 1) % 3;
            } while (!accessible(currentDirection));
        }
    }
    /**
     *
     * @param frame use to generate random number
     * @return
     */
    public Message escape(int frame){
        Message target = new Message(X, Y, currentDirection, null);
        if(X % 16 == 0 && Y % 16 == 0){
            if(intersection()){
                randomDirection(frame);
                System.out.println(currentDirection);
                walk();
            } else if (accessible(currentDirection)) {
                walk();
            } else {
                //turn back
                System.out.println("turn back!");
                turnBack();
                walk();
            }
        } else {
            walk();
        }
        return new Message(X, Y, currentDirection, target);
    }

    abstract public Message findTarget(int mode, Message playerPosition);
}
