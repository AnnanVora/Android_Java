package annan.example.flickrbrowser;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PhotoDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        activateToolbar(true);

        Intent intent = getIntent();
        Photo photo = (Photo) intent.getSerializableExtra(PHOTO_TRANSFER);
        if (photo != null) {
            TextView photoTitle = findViewById(R.id.photoTitle);
            Resources resources = getResources();
            String text = resources.getString(R.string.photo_title_text, photo.getTitle());
            photoTitle.setText(text);
//             FIXME : Title text -> more efficient way

            TextView photoTags = findViewById(R.id.photoTags);
//             FIXME : Tags text

            TextView photoAuthor = findViewById(R.id.photoAuthor);
//             FIXME : Author text

            ImageView photoImage = findViewById(R.id.photoImage);
            Picasso.get().load(photo.getLink())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(photoImage);
        }
    }
}