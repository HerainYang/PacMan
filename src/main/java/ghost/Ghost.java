package ghost;

public class Ghost extends Ghosts{

    public Ghost(App windowsFrame, int origX, int origY, long speed) {
        super(windowsFrame, origX, origY, speed);
    }

    public Message findTarget(int mode, Message playerPosition){
        System.out.println("normal find target");
        current_Matrix_X = X/16;
        current_Matrix_Y = Y/16;

        if(mode == SCATTERMODE){
            int x_axis = current_Matrix_X - (windowsFrame.mapLeft + windowsFrame.mapRight) / 2;
            int y_axis = current_Matrix_Y - (windowsFrame.mapUp + windowsFrame.mapDown) / 2;
            if(x_axis <= 0 && y_axis <= 0){
                return leftUp;
            }
            if(x_axis > 0 && y_axis <= 0){
                return rightUp;
            }
            if(x_axis <= 0){
                return leftDown;
            }
            return rightDown;
        } else {
            return new Message(playerPosition.X, playerPosition.Y, 0, null);
        }
    }
}

