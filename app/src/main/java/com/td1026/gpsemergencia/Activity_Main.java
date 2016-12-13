package com.td1026.gpsemergencia;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.td1026.gpsemergencia.BaseDados.BDbackup;
import com.td1026.gpsemergencia.BaseDados.SQLPostgre;
import com.td1026.gpsemergencia.BaseDados.bd_Ocurrencia;
import com.td1026.gpsemergencia.BaseDados.bd_Ocurrencia_Helper;
import com.td1026.gpsemergencia.BaseDados.bd_Trajecto;
import com.td1026.gpsemergencia.MetodosAuxiliares.EscritaLeituraFicheiros;
import com.td1026.gpsemergencia.MetodosAuxiliares.Logs;
import com.td1026.gpsemergencia.MetodosAuxiliares.Permissoes;

import java.sql.SQLException;
import java.util.List;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

public class Activity_Main extends AppCompatActivity {

    //--------------------------------------------------------------------------------------
    //------------------------------------Variaveis-----------------------------------------
    //--------------------------------------------------------------------------------------
    LocationListener locationlistener;
    //--------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------
    //---------------------------------Metodos da Classe------------------------------------
    //--------------------------------------------------------------------------------------
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Activity_Main t = this;
        Button ma_bt_Iniciar = (Button) findViewById(R.id.ma_bt_Iniciar);
        Button ma_bt_ActualizarManualmente = (Button) findViewById(R.id.ma_bt_ActualizarManualmente);
        Button ma_bt_VerDados = (Button) findViewById(R.id.ma_bt_VerDados);
        Button ma_bt_Finalizar = (Button) findViewById(R.id.ma_bt_Finalizar);
        Button ma_bt_Historico = (Button) findViewById(R.id.ma_bt_Historico);
        //----Defenir Click de Botoes
        ma_bt_Iniciar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(t);
                if(Activity_ServicePrincipal.SERVICERUNNING) {
                    builder
                            .setTitle(getString(R.string.lb_DescricaodaOcurrencia))
                            .setMessage(R.string.lb_JaExisteUmaOcurrenciaaDecorrer)
                            .setIcon(android.R.drawable.ic_delete)
                            .setNeutralButton(getString(R.string.lb_OK), null)
                            .show();
                }
                else{
                    Intent ser = new Intent(t, Activity_ServicePrincipal.class);
                    startService(ser);
                    builder
                            .setTitle(getString(R.string.lb_DescricaodaOcurrencia))
                            .setMessage(getString(R.string.lb_IniciodaOcurrencia))
                            .setIcon(android.R.drawable.ic_media_play)
                            .setNeutralButton(getString(R.string.lb_OK), null)
                            .show();
                }
            }
        });
        ma_bt_ActualizarManualmente.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(t);
                    if (!Activity_ServicePrincipal.SERVICERUNNING) {
                        builder
                                .setTitle(getString(R.string.lb_DescricaodaOcurrencia))
                                .setMessage(R.string.lb_NaoExisteUmaOcurrenciaaDecorrer)
                                .setIcon(android.R.drawable.ic_media_pause)
                                .setNeutralButton(getString(R.string.lb_OK), null)
                                .show();
                    } else {
                        if(Activity_ServicePrincipal.DADOS.proximoEstado()) {
                            builder
                                    .setTitle(getString(R.string.lb_DescricaodaOcurrencia))
                                    .setMessage(Activity_ServicePrincipal.DADOS.getEstadoString())
                                    .setIcon(android.R.drawable.ic_delete)
                                    .setNeutralButton(getString(R.string.lb_OK), null)
                                    .show();
                        }else{
                            builder
                                    .setTitle(getString(R.string.lb_DescricaodaOcurrencia))
                                    .setMessage(Activity_ServicePrincipal.DADOS.getEstadoString())
                                    .setIcon(android.R.drawable.ic_delete)
                                    .setNeutralButton(getString(R.string.lb_OK), null)
                                    .show();
                            Activity_ServicePrincipal.GUARDAR = true;
                            Intent ser = new Intent(t, Activity_ServicePrincipal.class);
                            stopService(ser);
                        }
                    }
                }catch (Exception e ){
                    Logs.erro(t.getLocalClassName(),e.getMessage());
                }
            }
        });
        ma_bt_VerDados.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(t);
                if(Activity_ServicePrincipal.SERVICERUNNING) {
                    builder
                            .setTitle(getString(R.string.lb_DescricaodaOcurrencia))
                            .setMessage(Activity_ServicePrincipal.DADOS.toString())
                            .setIcon(android.R.drawable.ic_popup_disk_full)
                            .setNeutralButton(getString(R.string.lb_OK), null)
                            .show();
                }
                else{
                    builder
                            .setTitle(getString(R.string.lb_DescricaodaOcurrencia))
                            .setMessage(getString(R.string.lb_NaoEstaNumaOcurrencia))
                            .setIcon(android.R.drawable.ic_delete)
                            .setNeutralButton(getString(R.string.lb_OK), null)
                            .show();
                }
            }
        });
        ma_bt_Finalizar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(t);
                if(!Activity_ServicePrincipal.SERVICERUNNING) {
                    builder
                            .setTitle(getString(R.string.lb_DescricaodaOcurrencia))
                            .setMessage(R.string.lb_NaoExisteUmaOcurrenciaaDecorrer)
                            .setIcon(android.R.drawable.ic_media_pause)
                            .setNeutralButton(getString(R.string.lb_OK), null)
                            .show();
                }
                else{
                    Intent ser = new Intent(t, Activity_ServicePrincipal.class);
                    stopService(ser);
                    builder
                            .setTitle(getString(R.string.lb_DescricaodaOcurrencia))
                            .setMessage(getString(R.string.lb_FimdaOcurrencia))
                            .setIcon(android.R.drawable.ic_delete)
                            .setNeutralButton(getString(R.string.lb_OK), null)
                            .show();
                }
            }
        });
        ma_bt_Historico.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(t, Activity_Historico.class);
                startActivity(i);
            }
        });
        //-----Pedir Permisao para utilizar GPS---------------------
        if (!Permissoes.canAccessLocation(this)) {
            requestPermissions(Permissoes.LOCATION_PERMS, Permissoes.LOCATION_REQUEST);
        }
        //-----Se GPS Nao ligado, Pedir para ligar---------------------
        LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1);
        }
        //-----Pedir Permisao para utilizar Armazenamento---------------------
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
        }

        //-----Receber dados de GPS---------------------
        LocationManager locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationlistener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //Desenhar coordenadas no Ecra
                TextView ma_lb_Latitude = (TextView) findViewById(R.id.ma_lb_Latitude);
                TextView ma_lb_Longitude = (TextView) findViewById(R.id.ma_lb_Longitude);
                ma_lb_Latitude.setText(getString(R.string.lb_Lat) + ":  " + location.getLatitude());
                ma_lb_Longitude.setText(getString(R.string.lb_Lon) + ":  " + location.getLongitude());
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            public void onProviderEnabled(String provider) {
            }
            public void onProviderDisabled(String provider) {
            }
        };
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 20, locationlistener);
        }
    }
    //--------------------------------------------------------------------------------------

    //--------------------------------------------------------------------------------------
    //-------------------------------------------Metodos o Menu-----------------------------
    //--------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity__main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_configuracoesgerais) {
            Intent i = new Intent(this, Activity_ConfiguracoesGerais.class);
            startActivity(i);
        }
        if (id == R.id.menu_configuracoeshospitais) {
            Intent i = new Intent(this, Activity_Hospitais.class);
            startActivity(i);
        }
        if (id == R.id.menu_guardar) {
            SQLPostgre.gravarOcorrenciasnaoenviadas(this);
            //SQLPostgre.teste();
        }
        return super.onOptionsItemSelected(item);
    }
    //--------------------------------------------------------------------------------------
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Deixar de receber dados de gps
        if (locationlistener != null) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                locationManager.removeUpdates(locationlistener);
            }
        }
    }

    //--------------------------------------------------------------------------------------
    //-------------------------------------------Outros Metodos-----------------------------
    // -------------------------------------------------------------------------------------

    //--------------------------------------------------------------------------------------
}
