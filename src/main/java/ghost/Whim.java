package ghost;

public class Whim extends Ghosts{

    public Message chaser_Position;

    public Whim(App windowsFrame, int origX, int origY, long speed) {
        super(windowsFrame, origX, origY, speed);
        setGhost_Type(Ghost.WHIM);
    }

    /**
     * four situation:
     *   1, first quadrant < <
     *   2, second quadrant > <
     *   3, third quadrant < >
     *   4, fourth quadrant > >
     * @param targetMatrixX
     * @param targetMatrixY
     * @param ghostMatrixX
     * @param ghostMatrixY
     * @return
     */
    public Message doubleVector(int targetMatrixX, int targetMatrixY, int ghostMatrixX, int ghostMatrixY){
        int newTargetMatrixX = targetMatrixX * 2 - ghostMatrixX;
        int newTargetMatrixY = targetMatrixY * 2 - ghostMatrixY;
        newTargetMatrixX = Math.min(windowsFrame.mapRight, newTargetMatrixX);
        newTargetMatrixX = Math.max(windowsFrame.mapLeft, newTargetMatrixX);
        newTargetMatrixY = Math.min(windowsFrame.mapDown, newTargetMatrixY);
        newTargetMatrixY = Math.max(windowsFrame.mapUp, newTargetMatrixY);
        return new Message(newTargetMatrixX * 16, newTargetMatrixY * 16, 0, null);
    }

    public void passChaserPosition(Message chaser_Position){
        this.chaser_Position = chaser_Position;
    }

    @Override
    public Message findTarget(int mode, Message playerPosition) {
        if(mode == SCATTERMODE){
            return rightDown;
        } else {
            if(chaser_Position == null){
                return playerPosition;
            }
            int chaser_Matrix_X = chaser_Position.X / 16;
            int chaser_Matrix_Y = chaser_Position.Y / 16;
            int playerMatrixX = playerPosition.X / 16;
            int playerMatrixY = playerPosition.Y / 16;
            int targetMatrixX;
            int targetMatrixY;
            if(playerPosition.direction % 2 == 0){
                targetMatrixX = playerMatrixX;
                targetMatrixY = playerMatrixY + (playerPosition.direction - 39) * 2;
            } else {
                targetMatrixX = playerMatrixX + (playerPosition.direction - 38) * 2;
                targetMatrixY = playerMatrixY;
            }
            return doubleVector(targetMatrixX, targetMatrixY, chaser_Matrix_X, chaser_Matrix_Y);
        }
    }
}
