package com.example.vidyasetu;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
//import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Board extends AppCompatActivity {
    Toolbar toolbar;
    public RelativeLayout mainLayout;
    public DrawingView boardLayout;
    private final int GALLERY_REQ_CODE = 1000;

    public static Path path = new Path();
//    public static Paint paint = new Paint();
    ImageView imgGal;
    private boolean isScaled = false;

    private final Stack<Action> undoStack = new Stack<>();
    private final Stack<Action> redoStack = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Board");
        }

        mainLayout = findViewById(R.id.main);
        boardLayout = findViewById(R.id.drawingView);

        ImageButton draw = findViewById(R.id.btn1);
        ImageButton eraser = findViewById(R.id.btn2);
        ImageButton addTextBoxButton = findViewById(R.id.btn3);
        ImageButton imgBtn = findViewById(R.id.btn4);
        ImageButton undoBtn = findViewById(R.id.btnUndo);
        ImageButton redoBtn = findViewById(R.id.btnRedo);


        draw.setOnClickListener(this::pencil);
        eraser.setOnClickListener(this::eraser);
        addTextBoxButton.setOnClickListener(v -> addNewEditText());
        imgBtn.setOnClickListener(v -> openGallery());
        undoBtn.setOnClickListener(v -> undo());
        redoBtn.setOnClickListener(v -> redo());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.clear) {   // Clear the canvas
//            boardLayout.clearCanvas();
            if (boardLayout != null) {
                boardLayout.showClearCanvasDialog(Board.this);
            }
            return true;

        } else if (id == R.id.save) {   // Save the drawing
            saveDrawing();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveDrawing() {
        boardLayout.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(boardLayout.getDrawingCache());
        boardLayout.setDrawingCacheEnabled(false);

        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "VidyasetuDrawings");
        if (!directory.exists()) {
            directory.mkdirs(); // Create the folder if it doesn't exist
        }

        File file = new File(directory, "drawing_" + System.currentTimeMillis() + ".png");

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            // Make image visible in the gallery
            MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, new String[]{"image/png"}, null);

            Toast.makeText(this, "Drawing saved in Pictures/VidyasetuDrawings!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving drawing", Toast.LENGTH_SHORT).show();
        }
    }


    public void eraser(View view) {
        boardLayout.setEraseMode();
        undoStack.push(new Action("erase"));
    }

    public void pencil(View view) {
        boardLayout.setDrawingMode();
        undoStack.push(new Action("draw"));
    }
    @SuppressLint("ClickableViewAccessibility")
    private void addNewEditText() {
        // Create a new EditText
        EditText newEditText = new EditText(this);
        newEditText.setHint("Enter text here...");
        newEditText.setTextSize(18);
        newEditText.setHintTextColor(Color.parseColor("#000000"));
        newEditText.setTextColor(Color.parseColor("#000000"));
        newEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        newEditText.setPadding(10, 10, 10, 10);
        newEditText.setFocusable(true);
        newEditText.setBackground(null);
        newEditText.setFocusableInTouchMode(true);
        newEditText.setCursorVisible(false);

        // Set default position
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);  // Initial position you can set from here
        newEditText.setLayoutParams(params);


        // Enable dragging
        newEditText.setOnTouchListener(new View.OnTouchListener() {
            float dX =0, dY=0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        return false;

                    case MotionEvent.ACTION_MOVE:
                        v.setX(event.getRawX() + dX);
                        v.setY(event.getRawY() + dY);
                        return true;

//                    case MotionEvent.ACTION_UP:
//                        dX = v.getX() - event.getRawX();
//                        dY = v.getY() - event.getRawY();
//                        return false;

                    default:
                        return false;
                }
            }
        });


        newEditText.setOnLongClickListener(v -> {
            showDeleteDialog(newEditText);
            return true;
        });

//        newEditText.setOnLongClickListener(v -> {
//            mainLayout.removeView(newEditText); // Remove the EditText from the parent layout
//            return true; // Indicate the event is handled
//        });

        undoStack.push(new Action("addText", newEditText));
        redoStack.clear();

        // Add the EditText to the layout
        boardLayout.addView(newEditText);
        newEditText.bringToFront();
    }
    public void openGallery() {
        Intent iGallery = new Intent(Intent.ACTION_PICK);
        iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(iGallery, GALLERY_REQ_CODE);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onActivityResult(int requestCode, int resultcode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultcode, data);

        if (resultcode == RESULT_OK) {
            if (requestCode == GALLERY_REQ_CODE) {

                imgGal = new ImageView(this);

                int fheight = 400;
                int fwidth = 400;

                // Set default position
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(fwidth, fheight);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);  // Initial position you can set from here
                imgGal.setLayoutParams(params);
                imgGal.setImageURI(data.getData());
                imgGal.setScaleType(ImageView.ScaleType.FIT_CENTER);

                GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(@NonNull MotionEvent e) {
                        imageScale(); // Call the imageScale() method when double-tapped
                        return true;
                    }
                });

                imgGal.setOnTouchListener(new View.OnTouchListener() {
                    float dX, dY;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (gestureDetector.onTouchEvent(event))
                            return true; // Gesture detected, stop further handling

                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                dX = v.getX() - event.getRawX();
                                dY = v.getY() - event.getRawY();
                                return false;

                            case MotionEvent.ACTION_MOVE:
                                v.setX(event.getRawX() + dX);
                                v.setY(event.getRawY() + dY);
                                return true;

                            default:
                                return false;
                        }
                    }
                });

                imgGal.setClickable(true);
                imgGal.setLongClickable(true);

                imgGal.setOnLongClickListener(v -> {
                    showDeleteDialog(imgGal);
                    return true;
                });

                undoStack.push(new Action("addImage", imgGal));
                redoStack.clear();

                boardLayout.addView(imgGal);
                imgGal.bringToFront();
            }
        }
    }
    public void imageScale() {
        ObjectAnimator scaleX, scaleY;
        AnimatorSet animatorSet = new AnimatorSet();

        if (!isScaled) {
            // Scaling up animation
            scaleX = ObjectAnimator.ofFloat(imgGal, "scaleX", 1f, 2f);  // Scale X from 1 to 2
            scaleY = ObjectAnimator.ofFloat(imgGal, "scaleY", 1f, 2f);  // Scale Y from 1 to 2
        } else {
            // Scaling down animation
            scaleX = ObjectAnimator.ofFloat(imgGal, "scaleX", 2f, 1f);  // Scale X from 2 to 1
            scaleY = ObjectAnimator.ofFloat(imgGal, "scaleY", 2f, 1f);  // Scale Y from 2 to 1
        }

        // Play both animations together
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(1000);  // Set the duration of the animation

        // Start the animation
        animatorSet.start();

        isScaled = !isScaled;
    }
    public void undo() {
        if (!undoStack.isEmpty()) {
            Action action = undoStack.pop();
            redoStack.push(action);

            switch (action.type) {
                case "draw":
                    boardLayout.undo();
                    break;
                case "erase":
                    boardLayout.undo();
                    break;

                case "addText":
                case "addImage":
                    boardLayout.removeView((View) action.data);
                    break;

                default:
                    Toast.makeText(this, "Unknown action type", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            Toast.makeText(this, "Nothing to undo", Toast.LENGTH_SHORT).show();
        }
    }
    public void redo() {
        if (!redoStack.isEmpty()) {
            Action action = redoStack.pop();
            undoStack.push(action);

            switch (action.type) {
                case "draw":
                    boardLayout.redo();
                    break;
                case "erase":
                    boardLayout.redo();
                    break;

                case "addText":
                case "addImage":
                    // Re-add the last removed view (text or image)
                    boardLayout.addView((View) action.data);
                    break;

                default:
                    Toast.makeText(this, "Unknown action type", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Nothing to redo", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteDialog(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Content")
                .setIcon(R.drawable.delete_icon)
                .setMessage("Are you sure you want to delete this?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (view.getParent() != null) {
                        ((ViewGroup) view.getParent()).removeView(view);
                    }
                })
                .setNegativeButton("No", (dialog, which) ->
                        Toast.makeText(this, "Nothing deleted.", Toast.LENGTH_SHORT).show()
                )
                .show();
    }

//    private void undo() {
//        if (!undoStack.isEmpty()) {
//            Action lastAction = undoStack.pop();
//            redoStack.push(lastAction);
//            lastAction.undo(boardLayout);
//        }
//    }
//
//    private void redo() {
//        if (!redoStack.isEmpty()) {
//            Action lastAction = redoStack.pop();
//            undoStack.push(lastAction);
//            lastAction.redo(boardLayout);
//        }
//    }
//    private class AddTextAction extends Action {
//        private EditText editText;
//
//        public AddTextAction(EditText editText) {
//            this.editText = editText;
//        }
//
//        @Override
//        public void undo() {
//            boardLayout.removeView(editText);
//        }
//
//        @Override
//        public void redo() {
//            boardLayout.addView(editText);
//        }
//    }
}