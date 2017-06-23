public class Locator implements LocationListener {

        private LocationManager locationManager;

        //Variables globales para latitud y longitud
        String lati;
        String longi;

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0, 0, this);
            }

            Button orderButton = (Button)findViewById(R.id.btnVer);

            orderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent botonMapa = new Intent (MainActivity.this, Maps.class);
                    botonMapa.putExtra("latitud", lati);
                    botonMapa.putExtra("longitud", longi);
                    startActivity(botonMapa);
                }
            });
        }

        @Override
        public void onLocationChanged(Location location) {
            TextView latitud = (TextView)findViewById(R.id.lat);
            lati = Double.toString(location.getLatitude());
            latitud.setText(lati);

            TextView longitud = (TextView)findViewById(R.id.longi);
            longi = Double.toString(location.getLongitude());
            longitud.setText(longi);
        }

        @Override
        public void onProviderDisabled(String provider) {

            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            Toast.makeText(getBaseContext(), "Gps is turned off!! ",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {

            Toast.makeText(getBaseContext(), "Gps is turned on!! ",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

    }

}
