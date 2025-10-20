package com.example.lab06;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.util.Log;
import android.view.MotionEvent;

import androidx.fragment.app.FragmentActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HouseView extends View {

    private Paint paintRoomOutline;
    private Paint paintDoor;
    private Paint paintText;
    private Paint paintTextVertical;
    private static final String TAG = "MansionView";

    private List<Room> rooms = new ArrayList<>();
    private List<Door> doors = new ArrayList<>();
    private Map<Integer, RoomDescription> roomDataMap = new HashMap<>();

    // Variables para centrar el dibujo y ajustar los toques
    private float translateX = 0;
    private float translateY = 0;

    public HouseView(Context context) {
        super(context);
        init();
        loadCoordinates(context);
        loadRoomData(context);
    }

    public HouseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        loadCoordinates(context);
        loadRoomData(context);
    }

    public HouseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        loadCoordinates(context);
        loadRoomData(context);
    }

    private void init() {
        paintRoomOutline = new Paint();
        paintRoomOutline.setColor(Color.DKGRAY);
        paintRoomOutline.setStyle(Paint.Style.STROKE);
        paintRoomOutline.setStrokeWidth(5);

        paintDoor = new Paint();
        paintDoor.setColor(Color.YELLOW);
        paintDoor.setStrokeWidth(10);

        paintText = new Paint();
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(30);
        paintText.setAntiAlias(true);

        paintTextVertical = new Paint();
        paintTextVertical.setColor(Color.BLACK);
        paintTextVertical.setTextSize(30);
        paintTextVertical.setAntiAlias(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Ajustar el toque al desplazamiento del canvas
            float touchX = event.getX() - translateX;
            float touchY = event.getY() - translateY;

            Log.d(TAG, "Touch detected at: (" + touchX + ", " + touchY + ")");

            for (Room room : rooms) {
                if (isTouchInsideRoom(touchX, touchY, room)) {
                    Log.d(TAG, "Touched inside: " + room.getName());
                    handleRoomTouch(room);
                    return true;
                }
            }

            Log.d(TAG, "No room touched.");
        }
        return super.onTouchEvent(event);
    }

    /**
     * Verifica si el toque está dentro de un cuarto específico.
     */
    private boolean isTouchInsideRoom(float touchX, float touchY, Room room) {
        float x1 = room.getX1();
        float y1 = room.getY1();
        float x2 = room.getX2();
        float y2 = room.getY2();

        // Eje Y corregido (crece hacia abajo)
        boolean inside = (touchX >= x1 && touchX <= x2 && touchY >= y1 && touchY <= y2);
        Log.d(TAG, "Checking room " + room.getName() + ": (" + x1 + ", " + y1 + ") to (" + x2 + ", " + y2 + ") - Inside: " + inside);
        return inside;
    }

    /**
     * Maneja el evento de toque en un cuarto específico.
     */
    private void handleRoomTouch(Room room) {
        String name = room.getName();
        String roomNumberStr = name.replaceAll("[^0-9]", "");

        if (!roomNumberStr.isEmpty()) {
            try {
                int roomNumber = Integer.parseInt(roomNumberStr);
                RoomDescription roomInfo = roomDataMap.get(roomNumber);
                if (roomInfo != null) {
                    showRoomDetails(roomInfo);
                } else {
                    Log.d(TAG, "Información no encontrada para Cuarto " + roomNumber);
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error al convertir el número de cuarto", e);
            }
        } else {
            Log.d(TAG, "Número de cuarto no encontrado en el nombre: " + name);
        }
    }

    /**
     * Muestra los detalles del cuarto seleccionado en un fragmento.
     */
    private void showRoomDetails(RoomDescription roomInfo) {
        DetailRoomFragment detailRoomFragment = DetailRoomFragment.newInstance(
                roomInfo.getTitle(), roomInfo.getImageUrl(), roomInfo.getDescription()
        );
        FragmentActivity activity = (FragmentActivity) getContext();
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, detailRoomFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);

        // Calcula los límites del dibujo
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;

        for (Room room : rooms) {
            minX = Math.min(minX, room.getX1());
            minY = Math.min(minY, room.getY1());
            maxX = Math.max(maxX, room.getX2());
            maxY = Math.max(maxY, room.getY2());
        }

        float drawingWidth = maxX - minX;
        float drawingHeight = maxY - minY;

        // Calcula el centro del canvas y del dibujo
        float canvasCenterX = canvas.getWidth() / 2f;
        float canvasCenterY = canvas.getHeight() / 2f;
        float drawingCenterX = (minX + maxX) / 2f;
        float drawingCenterY = (minY + maxY) / 2f;

        // Centrar el dibujo
        translateX = canvasCenterX - drawingCenterX;
        translateY = canvasCenterY - drawingCenterY;
        canvas.translate(translateX, translateY);

        // Dibuja los cuartos
        for (Room room : rooms) {
            canvas.drawRect(room.getX1(), room.getY1(), room.getX2(), room.getY2(), paintRoomOutline);

            float textX = (room.getX1() + room.getX2()) / 2;
            float textY = (room.getY1() + room.getY2()) / 2;

            canvas.save();
            canvas.translate(textX, textY);
            canvas.rotate(-90);
            canvas.drawText(room.getName(), -paintTextVertical.measureText(room.getName()) / 2, 0, paintTextVertical);
            canvas.restore();
        }

        // Dibuja las puertas (solo visual)
        for (Door door : doors) {
            canvas.drawLine(door.getX1(), door.getY1(), door.getX2(), door.getY2(), paintDoor);
        }
    }

    /**
     * Carga los datos de los cuartos desde el archivo "cuartos.txt" en assets.
     */
    public void loadRoomData(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("cuartos.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            int roomNumber = -1;
            String title = null;
            String description = null;
            String imageUrl = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Cuarto:")) {
                    roomNumber = Integer.parseInt(line.split(":")[1].trim());
                } else if (line.startsWith("Título:")) {
                    title = line.split(":")[1].trim();
                } else if (line.startsWith("Descripción:")) {
                    description = line.substring(line.indexOf(":") + 1).trim();
                } else if (line.startsWith("URL de la imagen:")) {
                    imageUrl = line.split(":")[1].trim();

                    int imageResId = context.getResources().getIdentifier(imageUrl, "drawable", context.getPackageName());

                    if (roomNumber != -1 && title != null && description != null && imageResId != 0) {
                        roomDataMap.put(roomNumber, new RoomDescription(title, description, imageResId));
                        roomNumber = -1;
                        title = null;
                        description = null;
                        imageUrl = null;
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al cargar datos de cuartos", e);
        }
    }

    /**
     * Carga las coordenadas de los cuartos y puertas desde el archivo "coordenadas.txt" en assets.
     */
    private void loadCoordinates(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("coordenadas.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(" ");
                if (parts[0].equalsIgnoreCase("Cuarto")) {
                    if (parts.length < 6) continue;
                    String name = parts[1];
                    float x1 = Float.parseFloat(parts[2]);
                    float y1 = Float.parseFloat(parts[3]);
                    float x2 = Float.parseFloat(parts[4]);
                    float y2 = Float.parseFloat(parts[5]);
                    rooms.add(new Room(name, x1, y1, x2, y2));
                } else if (parts[0].equalsIgnoreCase("Puerta")) {
                    if (parts.length < 5) continue;
                    float x1 = Float.parseFloat(parts[1]);
                    float y1 = Float.parseFloat(parts[2]);
                    float x2 = Float.parseFloat(parts[3]);
                    float y2 = Float.parseFloat(parts[4]);
                    doors.add(new Door(x1, y1, x2, y2));
                }
            }
            reader.close();
        } catch (IOException e) {
            Log.e(TAG, "Error al cargar coordenadas", e);
        }
    }
}
