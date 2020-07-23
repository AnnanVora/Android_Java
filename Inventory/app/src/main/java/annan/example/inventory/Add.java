package annan.example.inventory;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Add extends AppCompatActivity {

    private static final String ITEM_NAME = "ItemName";
    private static final String ITEM_QUANTITY = "ItemQuantity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(ITEM_NAME, ((EditText) findViewById(R.id.add_name)).getText().toString());
                values.put(ITEM_QUANTITY, ((EditText) findViewById(R.id.add_quantity)).getText().toString());
                getContentResolver().insert(ItemsContract.CONTENT_URI, values);
            }
        });
    }
}
