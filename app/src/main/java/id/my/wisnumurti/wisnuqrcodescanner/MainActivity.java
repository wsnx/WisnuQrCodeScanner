package id.my.wisnumurti.wisnuqrcodescanner;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ActionProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonScan;
    private TextView textViewNama, textViewKelas, textViewNim;

    private IntentIntegrator qrScan;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonScan = (Button) findViewById(R.id.buttonScan);
        textViewNama = (TextView) findViewById(R.id.textViewNama);
        textViewKelas = (TextView) findViewById(R.id.textViewKelas);
        textViewNim = (TextView) findViewById(R.id.textViewNIM);
        qrScan = new IntentIntegrator(this);
        buttonScan.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Hasil SCAN tidak ada", Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject obj = new JSONObject(result.getContents());
                    textViewNama.setText(obj.getString("nama"));
                    textViewKelas.setText(obj.getString("kelas"));
                    textViewNim.setText(obj.getString("nim"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(),
                            Toast.LENGTH_LONG).show();
                }
            }

            if (Patterns.WEB_URL.matcher(result.getContents()).matches()) {
                Intent visitUrl = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getContents()));
                startActivity(visitUrl);
            } else {
                try {
                    Uri geoIntent = Uri.parse(result.getContents() + "?z=11");
                    Intent visitGeo = new Intent(Intent.ACTION_VIEW, geoIntent);
                    visitGeo.setPackage("com.google.android.apps.maps");
                    startActivity(visitGeo);
                } catch (ActivityNotFoundException e) {
                    Log.d("lokasi tidak di temukan", result.getContents());
                }
            }

            if (Patterns.PHONE.matcher(result.getContents()).matches()) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + result.getContents()));
                startActivity(intent);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
            } else {
                try {
                    Intent PhoneIntent = new Intent();
                    PhoneIntent.setAction("android.intent.action.DIAL");
                    PhoneIntent.setData(Uri.parse(result.getContents()));
                    startActivity(PhoneIntent);

                } catch (Exception e) {
                    Log.d("Panggilan gagal", result.getContents());
                }
            }
            if (Patterns.EMAIL_ADDRESS.matcher(result.getContents()).matches()) {
                Intent visitmEmail = new Intent(Intent.EXTRA_EMAIL, Uri.parse(result.getContents()));
                visitmEmail.setType("text/plain");
                visitmEmail.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                visitmEmail.putExtra(Intent.EXTRA_TEXT, "Body");
                startActivity(visitmEmail);
            }else{
                try {
                    Intent SendEmail = new Intent(Intent.ACTION_SENDTO, Uri.parse(result.getContents()));
                    startActivity(SendEmail);
                } catch (ActivityNotFoundException e) {
                    Log.d("Email tidak terkirim ", result.getContents());
                }
            }

            if  (Patterns.EMAIL_ADDRESS.matcher(result.getContents()).matches())
            {
                String mailAddress = String.valueOf(result.getContents());
                Intent KirimEmail = new Intent(Intent.ACTION_SENDTO);
                KirimEmail.setType("message/rfc822");
                KirimEmail.setData(Uri.parse("mailto:"+ mailAddress));
                KirimEmail.putExtra(Intent.EXTRA_SUBJECT, "Tugas QR Scanner UAS Wisnu");
                KirimEmail.putExtra(Intent.EXTRA_TEXT, "Selamat anda Telah sukses mengirim Email");
                KirimEmail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(Intent.createChooser(KirimEmail,"send To email"));
                    finish();
                }
                catch( Exception e){
                    Log.d("Email tidak terkirim ", result.getContents());
                }
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
    @Override
    public void onClick(View view) {
        qrScan.initiateScan();
    }
}
