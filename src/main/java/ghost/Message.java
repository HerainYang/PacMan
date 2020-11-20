package ghost;

import java.util.Objects;

public class Message {
    int X;
    int Y;
    int direction;
    Message target;
    int ghostTypes;

    public Message(int X, int Y, int direction, Message target){
        this.X = X;
        this.Y = Y;
        this.direction = direction;
        this.target = target;
    }

    public static void copy(Message m1, Message m2){
        m1.X = m2.X;
        m1.Y = m2.Y;
        m1.direction = m2.direction;
        m1.target = m2.target;
        m1.ghostTypes = m2.ghostTypes;
    }
}

