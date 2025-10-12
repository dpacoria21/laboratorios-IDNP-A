package com.example.lab4_fragments.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.lab4_fragments.HomeActivity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import com.example.lab4_fragments.Building;
import com.example.lab4_fragments.BuildingRepository;
import com.example.lab4_fragments.LoginActivity;
import com.example.lab4_fragments.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;

import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final float PROXIMITY_THRESHOLD_METERS = 50.0f;
    private static final String TAG = "HomeFragment";
    private static final int NOTIFICATION_ID_BASE = 1000;
    private static final String CHANNEL_ID = "proximity_channel";
    private static final String CHANNEL_NAME = "Notificaciones de Proximidad";
    private static final String CHANNEL_DESC = "Recibe notificaciones cuando te acerques a una edificación específica.";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_ZOOM_LEVEL = "zoom_level";
    private static final float ZOOM_THRESHOLD = 17.0f;

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Set<Integer> notifiedBuildings = new HashSet<>();

    private GoogleMap mMap;
    private Button viewUbiButton;
    private Button logoutButton;
    private BuildingRepository buildingRepository;

    private LatLng lastCameraPosition = null;
    private float lastZoom = 12.0f;
    private List<Marker> allMarkers = new ArrayList<>();
    private Map<Integer, Bitmap> markerWithLabelCache = new HashMap<>();
    private Map<Integer, Bitmap> markerWithoutLabelCache = new HashMap<>();
    private FusedLocationProviderClient fusedLocationClient;


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewUbiButton = view.findViewById(R.id.viewUbi);
        logoutButton = view.findViewById(R.id.logoutButton);

        buildingRepository = new BuildingRepository(getContext());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        viewUbiButton.setOnClickListener(v -> {
            if (mMap != null) {
                centerMapOnCurrentLocation();
            }
        });

        logoutButton.setOnClickListener(v -> logout());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        }

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Log.d(TAG, "Permiso de notificación concedido");
                    } else {
                        Log.e(TAG, "Permiso de notificación denegado");
                    }
                }
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (savedInstanceState != null) {
            lastCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
            lastZoom = savedInstanceState.getFloat(KEY_ZOOM_LEVEL, 12.0f);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    handleNewLocation(location);
                }
            }
        };

        if (savedInstanceState != null) {
            ArrayList<Integer> notified = savedInstanceState.getIntegerArrayList("notifiedBuildings");
            if (notified != null) {
                notifiedBuildings.addAll(notified);
            }
        }

        createNotificationChannel();
        startLocationUpdates();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESC);

            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d(TAG, "Canal de notificación creado");
            } else {
                Log.e(TAG, "No se pudo obtener NotificationManager");
            }
        }
    }


    private void handleNewLocation(Location userLocation) {
        List<Building> buildingList = buildingRepository.getBuildingList();
        for (int i = 0; i < buildingList.size(); i++) {
            Building building = buildingList.get(i);
            float[] results = new float[1];
            Location.distanceBetween(
                    userLocation.getLatitude(),
                    userLocation.getLongitude(),
                    building.getLatitude(),
                    building.getLongitude(),
                    results
            );
            float distanceInMeters = results[0];

            if (distanceInMeters <= PROXIMITY_THRESHOLD_METERS && !notifiedBuildings.contains(i)) {
                String logMessage = "El usuario se ha acercado a la edificación: " + building.getTitle() + " (" + distanceInMeters + " metros)";
                Log.d(TAG, logMessage);
                notifiedBuildings.add(i);

                sendProximityNotification(i, building, distanceInMeters);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void sendProximityNotification(int buildingId, Building building, float distance) {
        if (!isAdded()) {
            Log.w(TAG, "Fragment no está añadido. No se puede enviar la notificación.");
            return;
        }

        Intent intent = new Intent(requireContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("buildingId", buildingId); // Añadir el ID de la edificación
        PendingIntent pendingIntent = PendingIntent.getActivity(requireContext(), buildingId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Te has acercado a una edificación")
                .setContentText(building.getTitle() + " está a " + String.format("%.2f", distance) + " metros de ti.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        int notificationId = NOTIFICATION_ID_BASE + buildingId;
        notificationManager.notify(notificationId, builder.build());
        Log.d(TAG, "Notificación enviada para la edificación: " + building.getTitle());
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void centerMapOnCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                            } else {
                                Toast.makeText(getContext(), "No se pudo obtener la ubicación actual.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Permiso de ubicación denegado.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);

        List<Building> buildingList = buildingRepository.getBuildingList();
        for (int i = 0; i < buildingList.size(); i++) {
            Building building = buildingList.get(i);
            LatLng position = new LatLng(building.getLatitude(), building.getLongitude());

            Bitmap customMarker = createCustomMarkerWithoutLabel(i);

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .icon(BitmapDescriptorFactory.fromBitmap(customMarker)) // Usar el ícono sin etiqueta
                    .snippet(String.valueOf(i)) // Usamos el índice como referencia
            );
            if (marker != null) {
                marker.setTag(i);
                allMarkers.add(marker); // Almacenar el marcador
            }
        }

        if (lastCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastCameraPosition, lastZoom));
        } else {
            // Mover la cámara a Arequipa si no hay datos guardados
            LatLng arequipa = new LatLng(-16.409047, -71.537451);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(arequipa, 12));
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        // Configurar el listener para cambios en el zoom del mapa
        mMap.setOnCameraIdleListener(() -> {
            float currentZoom = mMap.getCameraPosition().zoom;
            updateMarkerIcons(currentZoom);

            // Actualizar las variables con la posición y zoom actuales
            lastCameraPosition = mMap.getCameraPosition().target;
            lastZoom = currentZoom;
        });
    }

    /**
     * Actualiza los íconos de los marcadores según el nivel de zoom.
     *
     * @param zoomLevel Nivel de zoom actual del mapa.
     */
    private void updateMarkerIcons(float zoomLevel) {
        for (Marker marker : allMarkers) {
            if (marker.getTag() instanceof Integer) {
                int buildingId = (Integer) marker.getTag();
                Building building = buildingRepository.getBuildingList().get(buildingId);
                if (zoomLevel >= ZOOM_THRESHOLD) {
                    // Usar ícono con etiqueta
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(createCustomMarkerWithLabel(buildingId, building.getTitle())));
                } else {
                    // Usar ícono sin etiqueta
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(createCustomMarkerWithoutLabel(buildingId)));
                }
            }
        }
    }

    /**
     * Crea un marcador personalizado sin etiqueta.
     *
     * @param buildingId ID de la edificación.
     * @return Bitmap del marcador sin etiqueta.
     */
    private Bitmap createCustomMarkerWithoutLabel(int buildingId) {
        if (markerWithoutLabelCache.containsKey(buildingId)) {
            return markerWithoutLabelCache.get(buildingId);
        }

        // Inflar el layout personalizado sin etiqueta
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View markerView = inflater.inflate(R.layout.custom_marker_without_label, null);

        // Medir y dibujar la vista para crear el Bitmap
        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        markerView.layout(0, 0, markerView.getMeasuredWidth(), markerView.getMeasuredHeight());
        markerView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(markerView.getMeasuredWidth(), markerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerView.draw(canvas);

        // Almacenar el Bitmap en el caché
        markerWithoutLabelCache.put(buildingId, bitmap);

        return bitmap;
    }

    /**
     * Crea un marcador personalizado con etiqueta de texto.
     *
     * @param buildingId ID de la edificación.
     * @param title      Nombre de la edificación.
     * @return Bitmap del marcador con etiqueta.
     */
    private Bitmap createCustomMarkerWithLabel(int buildingId, String title) {
        if (markerWithLabelCache.containsKey(buildingId)) {
            return markerWithLabelCache.get(buildingId);
        }

        // Inflar el layout personalizado con etiqueta
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View markerView = inflater.inflate(R.layout.custom_marker_with_label, null);

        // Establecer el título de la edificación
        TextView titleTextView = markerView.findViewById(R.id.marker_title);
        titleTextView.setText(title);

        // Medir y dibujar la vista para crear el Bitmap
        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        markerView.layout(0, 0, markerView.getMeasuredWidth(), markerView.getMeasuredHeight());
        markerView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(markerView.getMeasuredWidth(), markerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerView.draw(canvas);

        // Almacenar el Bitmap en el caché
        markerWithLabelCache.put(buildingId, bitmap);

        return bitmap;
    }

    /**
     * Maneja el clic en un marcador del mapa.
     *
     * @param marker Marcador que fue clickeado.
     * @return True si el evento fue consumido, false de lo contrario.
     */
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        // Obtener el índice de la edificación desde el tag del marcador
        Object tag = marker.getTag();
        if (tag instanceof Integer) {
            int buildingId = (Integer) tag;
            navigateToDetailFragment(buildingId);
            return true;
        }
        return false;
    }

    /**
     * Navega al DetailFragment correspondiente a la edificación seleccionada.
     *
     * @param buildingId ID de la edificación.
     */
    private void navigateToDetailFragment(int buildingId) {
        DetailFragment detailFragment = DetailFragment.newInstance(buildingId);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Maneja el evento de clic en el botón "Salir".
     */
    private void logout() {
        // Eliminar el usuario logueado de SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("loggedInUser");
        editor.apply();

        // Navegar a la actividad de Login
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    /**
     * Maneja la respuesta a la solicitud de permisos.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    if (mMap != null) {
                        mMap.setMyLocationEnabled(true);
                    }
                }
            } else {
                // Permiso denegado, manejar adecuadamente
                Toast.makeText(getContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Sobrescribir onSaveInstanceState para guardar la posición y el zoom del mapa.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Guardar los IDs de edificaciones ya notificadas
        outState.putIntegerArrayList("notifiedBuildings", new ArrayList<>(notifiedBuildings));

        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition().target);
            outState.putFloat(KEY_ZOOM_LEVEL, mMap.getCameraPosition().zoom);
        }
    }
}
