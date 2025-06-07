package com.example.vidyasetu;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Stack;

import android.graphics.Path;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


public class DrawingView extends RelativeLayout {
    public static Stack<Path> pathList = new Stack<>();

    public ViewGroup.LayoutParams params;
    private Paint paint;
    private Path path;
    private boolean eraseMode = false;
    private Bitmap bitmap;
    private Canvas canvas;

    private ArrayList<Action> actionList = new ArrayList<>();
    private Stack<Action> undoStack = new Stack<>();
    private Stack<Action> redoStack = new Stack<>();




    public DrawingView(Context context) {
        super(context);
        init(context);
    }

    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(4f);

        params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        path = new Path();
    }

    public void setDrawingMode() {
        eraseMode = false;
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
    }

    public void setEraseMode() {
        eraseMode = true;
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(40);
    }

//    public void undo() {
//        if (!actionList.isEmpty()) {
//            Action lastAction = actionList.remove(actionList.size() - 1);
//            undoStack.push(lastAction);
//            invalidate();
//        }
//    }
//
//    public void redo() {
//        if (!undoStack.isEmpty()) {
//            Action redoAction = undoStack.pop();
//            actionList.add(redoAction);
//            redoStack.push(redoAction);
//            invalidate();
//        }
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        float x = event.getX();
//        float y = event.getY();
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                path = new Path();
//                path.moveTo(x, y);
//                Action newAction = new Action(new Path(path), new Paint(paint));
//                actionList.add(newAction);
//                invalidate();
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                path.lineTo(x, y);
//                actionList.get(actionList.size() - 1).setPath(new Path(path));
//                invalidate();
//                break;
//
//            case MotionEvent.ACTION_UP:
//                redoStack.clear();
//                break;
//        }
//        return true;
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        for (Action action : actionList) {
//            canvas.drawPath(action.getPath(), action.getPaint());
//        }
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path = new Path();
                path.moveTo(x, y);
                redoStack.clear();  // Clear redo on new action
                break;

            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                Action newAction = new Action(new Path(path), new Paint(paint));
                actionList.add(newAction);
                undoStack.push(newAction);// Store last action for undo
                path.reset();
                invalidate();
                break;
        }
        return true;
    }

    public void undo() {
        if (!actionList.isEmpty()) {
            Action lastAction = actionList.remove(actionList.size() - 1);
            redoStack.push(lastAction);  // Move to redo stack
            invalidate();
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            Action redoAction = redoStack.pop();
            actionList.add(redoAction);
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Action action : actionList) {
            canvas.drawPath(action.getPath(), action.getPaint());
        }
    }


    public void clearCanvas() {
        actionList.clear();
        redoStack.clear();
        path.reset();
        removeAllViews();
        invalidate();
    }

    public void showClearCanvasDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Clear Canvas")
                .setMessage("Are you sure you want to clear the canvas? This will remove all drawings, text, and images.")
                .setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearCanvas();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void restorePaths(Stack<Path> paths) {
        pathList.clear();
        pathList.addAll(paths);
        invalidate();
    }

//    public void setActionListener(ActionListener listener){
//        this.actionListener = listener;
//
//    }

}


