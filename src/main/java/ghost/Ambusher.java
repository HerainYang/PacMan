package ghost;

public class Ambusher extends Ghosts{
    public Ambusher(App windowsFrame, int origX, int origY, long speed) {
        super(windowsFrame, origX, origY, speed);
        setGhost_Type(Ghost.AMBUSHER);
    }

    @Override
    public Message findTarget(int mode, Message playerPosition) {
        System.out.println("ambusher find target");
        current_Matrix_X = X/16;
        current_Matrix_Y = Y/16;

        if(mode == SCATTERMODE){
            return rightUp;
        } else {
            int playerMatrixX = playerPosition.X / 16;
            int playerMatrixY = playerPosition.Y / 16;
            if(playerPosition.direction % 2 == 0){
                int newPositionY = playerMatrixY + (playerPosition.direction - 39) * 4;
                if (newPositionY > windowsFrame.mapDown) {
                    newPositionY = windowsFrame.mapDown;
                }
                if (newPositionY < windowsFrame.mapUp) {
                    newPositionY = windowsFrame.mapUp;
                }
                return new Message(playerPosition.X, newPositionY * 16, 0, null);
            } else {
                int newPositionX = playerMatrixX + (playerPosition.direction - 38) * 4;
                if (newPositionX < windowsFrame.mapLeft) {
                    newPositionX = windowsFrame.mapLeft;
                }
                if (newPositionX > windowsFrame.mapRight) {
                    newPositionX = windowsFrame.mapRight;
                }
                return new Message(newPositionX * 16, playerPosition.Y, 0, null);
            }
        }
    }
}
