package com.example.vidyasetu;

import android.graphics.Paint;
import android.graphics.Path;

public class Action {
    String type;
    Object data;//This is for text view and image view
    private Path path;
    private Paint paint;

    public Action(String type, Object data){
        this.type = type;
//        if (data instanceof Stack) {
//            this.data = ((Stack<Path>) data).clone();  // Clone the stack to prevent modifying the original
//        } else {
        this.data = data;
//        }
    }
    public Action(String type){
        this.type = type;
    }

    public Action(Path path, Paint paint) {
        this.path = path;
        this.paint = new Paint(paint);
    }

    public Path getPath() {
        return path;
    }

//    public void setPath(Path path) {
//        this.path = path;
//    }

    public Paint getPaint() {
        return paint;
    }


}



