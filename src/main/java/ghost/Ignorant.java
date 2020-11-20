package ghost;

public class Ignorant extends Ghosts{
    public Ignorant(App windowsFrame, int origX, int origY, long speed) {
        super(windowsFrame, origX, origY, speed);
        setGhost_Type(Ghost.IGNORANT);
    }

    @Override
    public Message findTarget(int mode, Message playerPosition) {
        System.out.println("ignorant find target");
        current_Matrix_X = X/16;
        current_Matrix_Y = Y/16;

        if(mode == SCATTERMODE){
            return leftDown;
        } else {
            int playerMatrixX = playerPosition.X / 16;
            int playerMatrixY = playerPosition.Y / 16;
            int distance = (int) Math.sqrt(Math.pow((current_Matrix_X - playerMatrixX), 2) + Math.pow((current_Matrix_Y - playerMatrixY), 2));
            if(distance > 8){
                return leftDown;
            } else {
                return playerPosition;
            }
        }
    }
}
