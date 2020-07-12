package co.zw.sekai.zimmall;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ActivitySecurityQuestions extends AppCompatActivity implements View.OnClickListener {
    private RadioGroup radios;
    private RadioButton radioQst1, radioQst2, radioQst3;

    private EditText editAns1, editAns2, editAns3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_questions);

        radios = findViewById(R.id.radiosGroup);
    }

    @Override
    public void onClick(View v) {

    }
}
