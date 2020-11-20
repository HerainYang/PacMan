package ghost;

public class Chaser extends Ghosts{
    public Chaser(App windowsFrame, int origX, int origY, long speed) {
        super(windowsFrame, origX, origY, speed);
        setGhost_Type(Ghost.CHASER);
    }

    @Override
    public Message findTarget(int mode, Message playerPosition) {
        current_Matrix_X = X/16;
        current_Matrix_Y = Y/16;

        if(mode == SCATTERMODE){
            return leftUp;
        } else {
            return playerPosition;
        }
    }
}
