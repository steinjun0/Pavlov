package kr.osam.pavlov.UIComponent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import kr.osam.pavlov.R;

public class MapFragment extends Fragment
        implements
            OnMapReadyCallback,
            GoogleMap.OnPolylineClickListener,
            GoogleMap.OnPolygonClickListener {

    View rootView;
    MapView mapView;
    GoogleMap map;
    Polyline polyline;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.mapfragment, container, false);
        mapView = rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(this.getActivity());

    // Updates the location and zoom of the MapView



        if(((Gps_Meter_Activity)getActivity()).coord.size() >= 2)
        {
            Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .addAll(((Gps_Meter_Activity)getActivity()).coord));

            // Position the map's camera near Alice Springs in the center of Australia,
            // and set the zoom factor so most of Australia shows on the screen.
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(((Gps_Meter_Activity)getActivity()).coord.get(0), 4));
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(36.0, 127.35), 4));
        //googleMap.animateCamera(cameraUpdate);
        map = googleMap;

    }

    public void drawPolyline()
    {
        if(((Gps_Meter_Activity)getActivity()).coord.size() >= 2) {
            if(polyline != null) { polyline.remove(); }
            polyline = map.addPolyline(new PolylineOptions().clickable(true).addAll(((Gps_Meter_Activity) getActivity()).coord));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(((Gps_Meter_Activity) getActivity()).coord.get(1), 14);
            map.animateCamera(cameraUpdate);

            map.setOnPolylineClickListener(this);
            map.setOnPolygonClickListener(this);
        }
    }

    @Override
    public void onPolylineClick(Polyline polyline) {

    }
    @Override
    public void onPolygonClick(Polygon polygon) {

    }
}

