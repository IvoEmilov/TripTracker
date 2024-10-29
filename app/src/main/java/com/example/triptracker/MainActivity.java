package com.example.triptracker;

import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;
import static com.mapbox.maps.plugin.animation.CameraAnimationsUtils.getCamera;
import static com.mapbox.navigation.base.extensions.RouteOptionsExtensions.applyDefaultNavigationOptions;


import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.directions.v5.models.VoiceInstructions;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
//import com.mapbox.maps.ImageHolder;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.Bearing;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.bindgen.Expected;
import com.mapbox.maps.EdgeInsets;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentConstants;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.generated.LocationComponentSettings;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.base.route.NavigationRoute;
import com.mapbox.navigation.base.route.NavigationRouterCallback;
import com.mapbox.navigation.base.route.RouterFailure;
import com.mapbox.navigation.base.route.RouterOrigin;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.directions.session.RoutesObserver;
import com.mapbox.navigation.core.directions.session.RoutesUpdatedResult;
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp;
import com.mapbox.navigation.core.trip.session.LocationMatcherResult;
import com.mapbox.navigation.core.trip.session.LocationObserver;
import com.mapbox.navigation.core.trip.session.VoiceInstructionsObserver;
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer;
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView;
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineError;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources;
import com.mapbox.navigation.ui.maps.route.line.model.RouteSetValue;
import com.google.android.material.button.MaterialButton;
import com.mapbox.navigation.ui.voice.api.MapboxSpeechApi;
import com.mapbox.navigation.ui.voice.api.MapboxVoiceInstructionsPlayer;
import com.mapbox.navigation.ui.voice.model.SpeechAnnouncement;
import com.mapbox.navigation.ui.voice.model.SpeechError;
import com.mapbox.navigation.ui.voice.model.SpeechValue;
import com.mapbox.navigation.ui.voice.model.SpeechVolume;
import com.mapbox.navigation.ui.voice.view.MapboxSoundButton;
import com.mapbox.search.autocomplete.PlaceAutocomplete;
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion;
import com.mapbox.search.ui.adapter.autocomplete.PlaceAutocompleteUiAdapter;
import com.mapbox.search.ui.view.CommonSearchViewConfiguration;
import com.mapbox.search.ui.view.SearchResultsView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function1;


public class MainActivity extends AppCompatActivity {
    private final int REQUEST_ENABLE_BT = 1;
    private final int REQUEST_ENABLE_LOC = 2;

    static TextView tvConnectedDevices, tvHeader, tvBtToggle, tvLocToggle, tvData1, tvData2;
    private TextInputEditText txtSearch;
    static TextView tvInstantConsumption, tvAvgConsumption, tvAvgSpeed, tvTimeDuration, tvDistance;
    static Button btnScanDevices, btnPreviewMode;
    static ImageView imgProfile, BtGradient, LocGradient, imgAppLogo;
    static CardView btnBtToggle, btnLocToggle;
    static Spinner data1Spinner, data2Spinner;
    static ArrayList<Trip> trips = new ArrayList<>();

    static LinearLayout loFooter, loData, loCalculations, btnNavigation, btnTrips;
    static RelativeLayout loBtLocSettings, loRootView, loMap;
    static NestedScrollView devicesSV, tripsSV;
    Intent enableBtIntent;

    static RecyclerView rvBTPairs, rvScanDevices, rvBTConnected, rvTrips;
    private RecyclerView.LayoutManager layoutManager, BTScannedLM, BTConnectedLM, tripsLM;
    //static Adapter adapter;
    static AdapterTrips adapterTrips;

    static AdapterBTPairs BTAdapter, BTAdapterScanned, BTAdapterConnected;
    BluetoothAdapter bluetoothAdapter;

    private UpdateUIThread UIThread;
    private Database db;
    static ProgressBar progressBar;

    MapView mapView;
    FloatingActionButton btnFocusLocation;
    MaterialButton btnSetRoute;
    MapboxSoundButton btnSound;
    private PlaceAutocomplete placeAutocomplete;
    private SearchResultsView searchResultsView;
    private PlaceAutocompleteUiAdapter placeAutocompleteUiAdapter;
    private boolean ignoreNextQueryUpdate = Boolean.FALSE;
    private final NavigationLocationProvider navigationLocationProvider = new NavigationLocationProvider();
    private MapboxRouteLineView routeLineView;
    private MapboxRouteLineApi routeLineApi;
    private final LocationObserver locationObserver = new LocationObserver() {
        @Override
        public void onNewRawLocation(@NonNull Location location) {

        }

        @Override
        public void onNewLocationMatcherResult(@NonNull LocationMatcherResult locationMatcherResult) {
            Location location = locationMatcherResult.getEnhancedLocation();
            navigationLocationProvider.changePosition(location, locationMatcherResult.getKeyPoints(), null, null);
            if (focusLocationFlag) {
                updateCamera(Point.fromLngLat(location.getLongitude(), location.getLatitude()), (double) location.getBearing());
            }
        }
    };
    private final RoutesObserver routesObserver = new RoutesObserver() {
        @Override
        public void onRoutesChanged(@NonNull RoutesUpdatedResult routesUpdatedResult) {
            routeLineApi.setNavigationRoutes(routesUpdatedResult.getNavigationRoutes(), new MapboxNavigationConsumer<Expected<RouteLineError, RouteSetValue>>() {
                @Override
                public void accept(Expected<RouteLineError, RouteSetValue> routeLineErrorRouteSetValueExpected) {
                    Style style = mapView.getMapboxMap().getStyle();
                    if (style != null) {
                        routeLineView.renderRouteDrawData(style, routeLineErrorRouteSetValueExpected);
                    }
                }
            });
        }
    };
    boolean focusLocationFlag = true;
    private MapboxNavigation mapboxNavigation;
    private void updateCamera(Point point, Double bearing) {
        MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();
        CameraOptions cameraOptions = new CameraOptions.Builder().center(point).zoom(17.5).bearing(bearing).pitch(45.0)
                .padding(new EdgeInsets(1000.0, 0.0, 0.0, 0.0)).build();

        getCamera(mapView).easeTo(cameraOptions, animationOptions);
    }
    private final OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            focusLocationFlag = false;
            getGestures(mapView).removeOnMoveListener(this);
            btnFocusLocation.show();
        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {

        }
    };
    //MapBox ActivityResultLauncher
    private final ActivityResultLauncher<String> activityResultLauncherMap = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                Toast.makeText(MainActivity.this, "Permission granted! Restart this app", Toast.LENGTH_SHORT).show();
            }
        }
    });

    private MapboxSpeechApi speechApi;
    private MapboxVoiceInstructionsPlayer mapboxVoiceInstructionsPlayer;

    private MapboxNavigationConsumer<Expected<SpeechError, SpeechValue>> speechCallback = new MapboxNavigationConsumer<Expected<SpeechError, SpeechValue>>() {
        @Override
        public void accept(Expected<SpeechError, SpeechValue> speechErrorSpeechValueExpected) {
            speechErrorSpeechValueExpected.fold(new Expected.Transformer<SpeechError, Unit>() {
                @NonNull
                @Override
                public Unit invoke(@NonNull SpeechError input) {
                    mapboxVoiceInstructionsPlayer.play(input.getFallback(), voiceInstructionsPlayerCallback);
                    return Unit.INSTANCE;
                }
            }, new Expected.Transformer<SpeechValue, Unit>() {
                @NonNull
                @Override
                public Unit invoke(@NonNull SpeechValue input) {
                    mapboxVoiceInstructionsPlayer.play(input.getAnnouncement(), voiceInstructionsPlayerCallback);
                    return Unit.INSTANCE;
                }
            });
        }
    };

    private MapboxNavigationConsumer<SpeechAnnouncement> voiceInstructionsPlayerCallback = new MapboxNavigationConsumer<SpeechAnnouncement>() {
        @Override
        public void accept(SpeechAnnouncement speechAnnouncement) {
            speechApi.clean(speechAnnouncement);
        }
    };

    VoiceInstructionsObserver voiceInstructionsObserver = new VoiceInstructionsObserver() {
        @Override
        public void onNewVoiceInstructions(@NonNull VoiceInstructions voiceInstructions) {
            speechApi.generate(voiceInstructions, speechCallback);
        }
    };

    private boolean voiceInstructionsMutedFlag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loBtLocSettings = (RelativeLayout) findViewById(R.id.loBtLocSettings);
        loRootView = (RelativeLayout) findViewById(R.id.loRootView);
        loMap = (RelativeLayout) findViewById(R.id.loMap);
        loFooter = (LinearLayout) findViewById(R.id.loFooter);
        loData = (LinearLayout) findViewById(R.id.loData);
        loCalculations = (LinearLayout) findViewById(R.id.loCalculations);
        devicesSV = (NestedScrollView) findViewById(R.id.devicesSV);
        tripsSV = (NestedScrollView) findViewById(R.id.tripsSV);
        //Named as buttons due to OnClickListeners
        btnNavigation = (LinearLayout) findViewById(R.id.btnNavigation);
        btnTrips = (LinearLayout) findViewById(R.id.btnTrips);

        tvHeader = (TextView) findViewById(R.id.tvHeader);
        tvConnectedDevices = (TextView) findViewById(R.id.tvConnectedDevices);
        tvBtToggle = (TextView) findViewById(R.id.tvBtToggle);
        tvLocToggle = (TextView) findViewById(R.id.tvLocToggle);
        tvData1 = (TextView) findViewById(R.id.tvData1);
        tvData2 = (TextView) findViewById(R.id.tvData2);

        tvInstantConsumption = (TextView) findViewById(R.id.tvInstantConsumption);
        tvAvgConsumption = (TextView) findViewById(R.id.tvAvgConsumption);
        tvAvgSpeed = (TextView) findViewById(R.id.tvAvgSpeed);
        tvTimeDuration = (TextView) findViewById(R.id.tvTimeDuration);
        tvDistance = (TextView) findViewById(R.id.tvDistance);

        btnScanDevices = (Button) findViewById(R.id.btnScanDevices);
        btnPreviewMode = (Button) findViewById(R.id.btnPreviewMode);

        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        BtGradient = (ImageView) findViewById(R.id.BtGradient);
        LocGradient = (ImageView) findViewById(R.id.LocGradient);
        imgAppLogo = (ImageView) findViewById(R.id.imgAppLogo);

        btnBtToggle = (CardView) findViewById(R.id.btnBtToggle);
        btnLocToggle = (CardView) findViewById(R.id.btnLocToggle);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

        btnFocusLocation = findViewById(R.id.btnFocusLocation);
        btnFocusLocation.hide();
        btnSetRoute = findViewById(R.id.btnSetRoute);
        btnSound = findViewById(R.id.btnSound);

        //Create 2 data spinners, that will define OBD Commands to transmit.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dataCommands, android.R.layout.simple_spinner_item);
        data1Spinner = (Spinner) findViewById(R.id.data1Spinner);
        data2Spinner = (Spinner) findViewById(R.id.data2Spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        data1Spinner.setAdapter(adapter);
        data2Spinner.setAdapter(adapter);

        db = new Database();
        db.initDatabase(new CallbackDB() {
            @Override
            public void onInit() {
                db.getUserTrips(new CallbackDB() {
                    @Override
                    public void onInit() {

                    }
                    @Override
                    public void onSuccessTrips(ArrayList<Trip> TripsDB) {
                        trips.clear();
                        trips.addAll(TripsDB);
                        sortTrips();
                    }
                });
            }
            @Override
            public void onSuccessTrips(ArrayList<Trip> TripsDB) {
            }
        });

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        UIThread = new UpdateUIThread(MainActivity.this,bluetoothAdapter);
        UpdateUIThread.setApplicationContext(this);
        UIThread.start();

        displayLocationSettingsRequest(MainActivity.this, Boolean.TRUE);
        enableServices();

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        btnScanDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doDiscovery();
                btnScanDevices.setText("Scanning...");
                BTAdapterScanned.emptyList();
                BTAdapterScanned.notifyDataSetChanged();
            }
        });

        btnPreviewMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIThread.startPreviewMode();
            }
        });

        imgAppLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIThread.stopPreviewMode();
            }
        });
        btnNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIThread.NavigationView();
            }
        });
        btnTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIThread.TripsView();
            }
        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                }
            });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:

                if (resultCode == RESULT_OK) {
                    // Bluetooth is on
                    showToast("Bluetooth enabled successfully.");
                } else {
                    showToast("Failed to connect to bluetooth");
                }

                break;
            case REQUEST_ENABLE_LOC:
                if (resultCode == RESULT_OK) {
                    // Location is on
                    showToast("Location enabled successfully.");
                    UIThread.locUiUpdate();
                } else {
                    showToast("Failed to enable location.");
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("Permission Granted");
            } else {
                showToast("Permission Denied");
            }
        }
    }

    public void initMap(){
        mapView = findViewById(R.id.mapView);
        MapboxRouteLineOptions options = new MapboxRouteLineOptions.Builder(this).withRouteLineResources(new RouteLineResources.Builder().build())
                .withRouteLineBelowLayerId(LocationComponentConstants.LOCATION_INDICATOR_LAYER).build();
        routeLineView = new MapboxRouteLineView(options);
        routeLineApi = new MapboxRouteLineApi(options);

        speechApi = new MapboxSpeechApi(MainActivity.this, getString(R.string.mapbox_access_token), "EN");
        mapboxVoiceInstructionsPlayer = new MapboxVoiceInstructionsPlayer(MainActivity.this, "EN");

        NavigationOptions navigationOptions = new NavigationOptions.Builder(this).accessToken(getString(R.string.mapbox_access_token)).build();
        MapboxNavigationApp.setup(navigationOptions);
        mapboxNavigation = new MapboxNavigation(navigationOptions);

        mapboxNavigation.registerRoutesObserver(routesObserver);
        mapboxNavigation.registerLocationObserver(locationObserver);
        mapboxNavigation.registerVoiceInstructionsObserver(voiceInstructionsObserver);

        btnSound.unmute();
        btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voiceInstructionsMutedFlag = !voiceInstructionsMutedFlag;
                if (voiceInstructionsMutedFlag) {
                    btnSound.muteAndExtend(1500L);
                    mapboxVoiceInstructionsPlayer.volume(new SpeechVolume(0f));
                } else {
                    btnSound.unmuteAndExtend(1500L);
                    mapboxVoiceInstructionsPlayer.volume(new SpeechVolume(1f));
                }
            }
        });

        placeAutocomplete = PlaceAutocomplete.create(getString(R.string.mapbox_access_token));
        txtSearch = findViewById(R.id.txtSearch);

        searchResultsView = findViewById(R.id.search_results_view);
        searchResultsView.initialize(new SearchResultsView.Configuration(new CommonSearchViewConfiguration()));

        placeAutocompleteUiAdapter = new PlaceAutocompleteUiAdapter(searchResultsView, placeAutocomplete, LocationEngineProvider.getBestLocationEngine(MainActivity.this));

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (ignoreNextQueryUpdate) {
                    ignoreNextQueryUpdate = false;
                } else {
                    placeAutocompleteUiAdapter.search(charSequence.toString(), new Continuation<Unit>() {
                        @NonNull
                        @Override
                        public CoroutineContext getContext() {
                            return EmptyCoroutineContext.INSTANCE;
                        }

                        @Override
                        public void resumeWith(@NonNull Object o) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    searchResultsView.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                activityResultLauncherMap.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncherMap.launch(android.Manifest.permission.ACCESS_FINE_LOCATION);
            activityResultLauncherMap.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
        } else {
            mapboxNavigation.startTripSession();
        }

        btnFocusLocation.hide();
        LocationComponentPlugin locationComponentPlugin = getLocationComponent(mapView);
        getGestures(mapView).addOnMoveListener(onMoveListener);

        mapView.getMapboxMap().loadStyleUri(Style.LIGHT, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(17.5).build());
                locationComponentPlugin.setEnabled(true);
                locationComponentPlugin.setLocationProvider(navigationLocationProvider);
                getGestures(mapView).addOnMoveListener(onMoveListener);
                locationComponentPlugin.updateSettings(new Function1<LocationComponentSettings, Unit>() {
                    @Override
                    public Unit invoke(LocationComponentSettings locationComponentSettings) {
                        locationComponentSettings.setEnabled(true);
                        locationComponentSettings.setPulsingEnabled(true);
                        return null;
                    }
                });
                //Point point = Point.fromLngLat(23.353902, 42.657138);


                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_loc);
                AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
                PointAnnotationManager pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);

//                PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions().withTextAnchor(TextAnchor.CENTER).withIconImage(bitmap)
//                        .withPoint(point);
//                pointAnnotationManager.create(pointAnnotationOptions);

//                btnSetRoute.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        fetchRoute(point);
//                        btnSetRoute.setVisibility(View.GONE);
//                    }
//                });
                btnFocusLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        focusLocationFlag = true;
                        //fetchRoute(point);
                        getGestures(mapView).addOnMoveListener(onMoveListener);
                        btnFocusLocation.hide();
                    }
                });

                placeAutocompleteUiAdapter.addSearchListener(new PlaceAutocompleteUiAdapter.SearchListener() {
                    @Override
                    public void onSuggestionsShown(@NonNull List<PlaceAutocompleteSuggestion> list) {

                    }

                    @Override
                    public void onSuggestionSelected(@NonNull PlaceAutocompleteSuggestion placeAutocompleteSuggestion) {
                        ignoreNextQueryUpdate = true;
                        focusLocationFlag = false;
                        txtSearch.setText(placeAutocompleteSuggestion.getName());
                        searchResultsView.setVisibility(View.GONE);

                        pointAnnotationManager.deleteAll();
                        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions().withTextAnchor(TextAnchor.CENTER).withIconImage(bitmap)
                                .withPoint(placeAutocompleteSuggestion.getCoordinate());
                        pointAnnotationManager.create(pointAnnotationOptions);
                        updateCamera(placeAutocompleteSuggestion.getCoordinate(), 0.0);

                        btnSetRoute.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                fetchRoute(placeAutocompleteSuggestion.getCoordinate());
                            }
                        });
                    }

                    @Override
                    public void onPopulateQueryClick(@NonNull PlaceAutocompleteSuggestion placeAutocompleteSuggestion) {
                        //queryEditText.setText(placeAutocompleteSuggestion.getName());
                    }

                    @Override
                    public void onError(@NonNull Exception e) {

                    }
                });
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void fetchRoute(Point point) {
        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(MainActivity.this);
        locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                Location location = result.getLastLocation();
                RouteOptions.Builder builder = RouteOptions.builder();
                Point origin = Point.fromLngLat(Objects.requireNonNull(location).getLongitude(), location.getLatitude());
                builder.coordinatesList(Arrays.asList(origin, point));
                builder.alternatives(false);
                builder.profile(DirectionsCriteria.PROFILE_DRIVING);
                builder.bearingsList(Arrays.asList(Bearing.builder().angle(location.getBearing()).degrees(45.0).build(), null));
                applyDefaultNavigationOptions(builder);

                mapboxNavigation.requestRoutes(builder.build(), new NavigationRouterCallback() {
                    @Override
                    public void onRoutesReady(@NonNull List<NavigationRoute> list, @NonNull RouterOrigin routerOrigin) {
                        mapboxNavigation.setNavigationRoutes(list);
                        btnFocusLocation.performClick();
                    }

                    @Override
                    public void onFailure(@NonNull List<RouterFailure> list, @NonNull RouteOptions routeOptions) {
                        Toast.makeText(MainActivity.this, "Route request failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCanceled(@NonNull RouteOptions routeOptions, @NonNull RouterOrigin routerOrigin) {

                    }
                });
            }

            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }


    private void displayLocationSettingsRequest(Context context, boolean initFlag) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            String TAG = "Location";
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        if(initFlag == Boolean.FALSE){
                            Log.d(TAG, "All location settings are satisfied.");
                            showToast("Location is already ON.");
                        }
                        UIThread.locUiUpdate();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.d(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            if(initFlag == Boolean.FALSE){
                                status.startResolutionForResult(MainActivity.this, REQUEST_ENABLE_LOC);
                            }
                        } catch (IntentSender.SendIntentException e) {
                            Log.d(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });

    }

    private void enableServices() {
        btnBtToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bluetoothAdapter == null) {

                } else {
                    if (!bluetoothAdapter.isEnabled()) {

                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 1);
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }

                        activityResultLauncher.launch(enableBtIntent);

                    } else {
                        Toast.makeText(getApplicationContext(), "Bluetooth is already enabled", Toast.LENGTH_LONG).show();

                    }

                }
            }
        });
        btnLocToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayLocationSettingsRequest(MainActivity.this, Boolean.FALSE);
            }
        });

    }

    ArrayList<BluetoothDevice> getConnectedDevices() {
        if (bluetoothAdapter.isEnabled()) {
            //tvConnectedDevices.setText("Conected Devices");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return new ArrayList<BluetoothDevice>();
            }
            BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
            List<BluetoothDevice> devices = manager.getConnectedDevices(BluetoothProfile.GATT_SERVER);
            return new ArrayList<BluetoothDevice>(devices);
//            for (BluetoothDevice device : devices) {
//                //tvConnectedDevices.append("\nDevice: " + device.getName() + ", " + device);
//                BTAdapterConnected.add(device);
//            }
//            BTAdapterConnected.notifyDataSetChanged();
        } else {
            //bluetooth is off so can't get paired devices
            showToast("Turn ON Bluetooth to get paired devices");
        }
        return new ArrayList<BluetoothDevice>();
    }

    ArrayList<BluetoothDevice> getPairedDevices() {
        if (bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return new ArrayList<BluetoothDevice>();
            }
            return new ArrayList<>(bluetoothAdapter.getBondedDevices());

        } else {
            //bluetooth is off so can't get paired devices
            showToast("Turn ON Bluetooth to get paired devices");
        }
        return new ArrayList<BluetoothDevice>();
    }


    void loadRecyclerView(List<BluetoothDevice> pairedDevices, List<BluetoothDevice> connectedDevices) {
        rvBTConnected = findViewById(R.id.rvBTConnected);
        rvBTConnected.setNestedScrollingEnabled(false);
        BTAdapterConnected = new AdapterBTPairs(this, connectedDevices, R.layout.items_btpairs, MainActivity.this);
        BTConnectedLM = new LinearLayoutManager(this);
        rvBTConnected.setLayoutManager(BTConnectedLM);
        ItemTouchHelper.Callback BTConnectedithCallback = new RvItemTouchHelper(BTAdapterConnected, Boolean.FALSE, Boolean.FALSE);
        ItemTouchHelper BTConnecteditemTouchHelper = new ItemTouchHelper(BTConnectedithCallback);
        BTAdapterConnected.setItemTouchHelper(BTConnecteditemTouchHelper);
        BTConnecteditemTouchHelper.attachToRecyclerView(rvBTConnected);
        rvBTConnected.setAdapter(BTAdapterConnected);

        rvBTPairs = findViewById(R.id.rvBTPairs);
        rvBTPairs.setNestedScrollingEnabled(false);
        layoutManager = new LinearLayoutManager(this);
        BTAdapter = new AdapterBTPairs(this, pairedDevices, R.layout.items_btpairs, MainActivity.this);
        rvBTPairs.setLayoutManager(layoutManager);
        ItemTouchHelper.Callback ithCallback = new RvItemTouchHelper(BTAdapter, Boolean.FALSE, Boolean.FALSE);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(ithCallback);
        BTAdapter.setItemTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(rvBTPairs);
        rvBTPairs.setAdapter(BTAdapter);

        rvScanDevices = findViewById(R.id.rvScanDevices);
        rvScanDevices.setNestedScrollingEnabled(false);
        BTAdapterScanned = new AdapterBTPairs(this, new ArrayList<BluetoothDevice>(), R.layout.items_btpairs, MainActivity.this);
        BTScannedLM = new LinearLayoutManager(this);
        rvScanDevices.setLayoutManager(BTScannedLM);
        ItemTouchHelper.Callback BTScannedithCallback = new RvItemTouchHelper(BTAdapterScanned, Boolean.FALSE, Boolean.FALSE);
        ItemTouchHelper BTScanneditemTouchHelper = new ItemTouchHelper(BTScannedithCallback);
        BTAdapterScanned.setItemTouchHelper(BTScanneditemTouchHelper);
        BTScanneditemTouchHelper.attachToRecyclerView(rvScanDevices);
        rvScanDevices.setAdapter(BTAdapterScanned);

        rvTrips = findViewById(R.id.rvTrips);
        rvTrips.setNestedScrollingEnabled(false);
        adapterTrips = new AdapterTrips(this, trips, R.layout.items_trips, MainActivity.this);//TODO: Add trips after DB load.
        //tripsLM = new LinearLayoutManager(this);
        tripsLM = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rvTrips.setLayoutManager(tripsLM);
        ItemTouchHelper.Callback TripsIthCallback = new RvItemTouchHelper(adapterTrips, Boolean.FALSE, Boolean.TRUE);
        ItemTouchHelper tripsItemTouchHelper = new ItemTouchHelper(TripsIthCallback);
        adapterTrips.setItemTouchHelper(tripsItemTouchHelper);
        tripsItemTouchHelper.attachToRecyclerView(rvTrips);
        rvTrips.setAdapter(adapterTrips);
    }

    String[] getResourcesArray(){
        return getResources().getStringArray(R.array.dataCommands);
    }


    private void showToast(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
    }

    public static void sortTrips(){
        Collections.sort(trips, new Comparator<Trip>() {
            @Override
            public int compare(Trip o1, Trip o2) {
                if( o1.getPosition() > o2.getPosition()) return -1;
                else return 1;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapboxNavigation.onDestroy();
        mapboxNavigation.unregisterRoutesObserver(routesObserver);
        mapboxNavigation.unregisterLocationObserver(locationObserver);
        mapboxNavigation.unregisterVoiceInstructionsObserver(voiceInstructionsObserver);

        // Make sure we're not doing discovery anymore
        if (bluetoothAdapter != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // If it's already paired, skip it, because it's been listed already
                if (device != null && device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    BTAdapterScanned.add(device);
                    BTAdapterScanned.notifyDataSetChanged();
                }
                // When discovery is finished, change the Activity title
                //tvHeader.setText("Discovery Finished with found device");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                  btnScanDevices.setText("Scan for new Devices");
            }
        }
    };

    private void doDiscovery() {


        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        //setTitle(R.string.scanning);

        // Turn on sub-title for new devices
        //findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        bluetoothAdapter.startDiscovery();
    }
}