package app.marsel.cam_translate;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;

public class MainActivity extends AppCompatActivity {

    private CameraView cameraView;
    private ProgressBar progressBar;
    private TextView tvTranslatedText;
    private TextView tvLang;
    private CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = findViewById(R.id.cameraView);
        progressBar = findViewById(R.id.progressBar);
        tvTranslatedText = findViewById(R.id.tvTranslatedText);
        tvLang = findViewById(R.id.tvLang);
        cardView = findViewById(R.id.cardView);

        cameraView.setLifecycleOwner(this);

        cardView.setOnClickListener(view -> {
            cameraView.takePicture();
            progressBar.setVisibility(View.VISIBLE);
        });

        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(PictureResult result) {
                super.onPictureTaken(result);
                result.toBitmap(bitmap -> {
                    if (bitmap != null) {
                        extractTextFromImage(FirebaseVisionImage.fromBitmap(bitmap), text -> {
                            extractLanguageFromText(text, lang -> {
                                translateTextToEnglish(lang, text, translatedText -> {
                                    progressBar.setVisibility(View.GONE);
                                    tvTranslatedText.setText(translatedText);
                                    tvLang.setText(lang);
                                });
                            });
                        });
                    }
                });
            }
        });
    }

    private void translateTextToEnglish(String lang, String text, TranslationCallback callback) {
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(FirebaseTranslateLanguage.languageForLanguageCode(lang) != null ?
                        FirebaseTranslateLanguage.languageForLanguageCode(lang) :
                        FirebaseTranslateLanguage.ID)
                .setTargetLanguage(FirebaseTranslateLanguage.ID)
                .build();

        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        translator.downloadModelIfNeeded()
                .addOnSuccessListener(unused -> translator.translate(text)
                        .addOnSuccessListener(callback::onSuccess)
                        .addOnFailureListener(e -> {
                            callback.onSuccess("Failed to translate");
                            e.printStackTrace();
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this,
                            "Failed to download the translation model; please try again ...",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    private void extractLanguageFromText(String input, LanguageCallback callback) {
        FirebaseLanguageIdentification languageId = FirebaseNaturalLanguage.getInstance().getLanguageIdentification();

        languageId.identifyLanguage(input)
                .addOnSuccessListener(language -> {
                    Log.e("LANGUAGE", language);
                    callback.onSuccess(language);
                })
                .addOnFailureListener(e -> {
                    callback.onSuccess("en");
                    e.printStackTrace();
                });
    }

    private void extractTextFromImage(FirebaseVisionImage image, TextCallback callback) {
        FirebaseVisionTextRecognizer textDetector = FirebaseVision.getInstance().getCloudTextRecognizer();

        textDetector.processImage(image)
                .addOnSuccessListener(firebaseVisionText -> {
                    Log.e("TEXT", firebaseVisionText.getText());
                    callback.onSuccess(firebaseVisionText.getText());
                })
                .addOnFailureListener(e -> {
                    callback.onSuccess("No text found");
                    e.printStackTrace();
                });
    }

    interface TranslationCallback {
        void onSuccess(String translatedText);
    }

    interface LanguageCallback {
        void onSuccess(String language);
    }

    interface TextCallback {
        void onSuccess(String text);
    }
}
