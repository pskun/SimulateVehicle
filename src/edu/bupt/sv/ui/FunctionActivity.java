package edu.bupt.sv.ui;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;



import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupMenu;

public class FunctionActivity extends Activity  implements OnMapReadyCallback{
	
	static final LatLng NKUT = new LatLng(23.979548, 120.696745);
    private GoogleMap map;
    PopupMenu popup = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.function_page);
        
        ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        
        final  Button moreMenu = (Button)this.findViewById(R.id.buttonservice);
        moreMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View moreMenu){
                   PopupMenu popup = new PopupMenu(FunctionActivity.this, moreMenu);
                    popup.getMenuInflater()
                        .inflate(R.layout.service_menu, popup.getMenu());
 
                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                   
                            return true;
                        }
                    });
                    popup.show(); //showing popup menu 
            }
        });

    }

	@Override
	public void onMapReady(GoogleMap map) {
      
	  
	  Marker nkut = map.addMarker(new MarkerOptions().position(NKUT).title("南開科技大學").snippet("數位生活創意系"));

      // Move the camera instantly to NKUT with a zoom of 16.
      map.moveCamera(CameraUpdateFactory.newLatLngZoom(NKUT, 16));
	}
}
