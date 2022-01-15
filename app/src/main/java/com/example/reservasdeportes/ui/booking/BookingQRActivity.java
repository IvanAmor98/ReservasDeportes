package com.example.reservasdeportes.ui.booking;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.reservasdeportes.R;
import com.example.reservasdeportes.databinding.BookingQrActivityBinding;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class BookingQRActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BookingQrActivityBinding binding = BookingQrActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageView qrImage = binding.qrImage;

        BookingDTO bookingDTO = getIntent().getParcelableExtra("bookingDTO");

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = Math.min(width, height);
        smallerDimension = smallerDimension * 3 / 4;

        QRGEncoder qrgEncoder = new QRGEncoder(bookingDTO.getId(), null, QRGContents.Type.TEXT, smallerDimension);

        try {
            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            qrImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}