package com.example.udhaarpay;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class ReceiveMoneyActivity extends AppCompatActivity {

    private ImageView ivQrCode;
    private TextView tvUpiId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_money);

        ivQrCode = findViewById(R.id.ivQrCode);
        tvUpiId = findViewById(R.id.tvUpiId);

        // Sample UPI ID - in real app, this would be user's actual UPI ID
        String upiId = "user@udhaarpay";
        tvUpiId.setText("UPI ID: " + upiId);

        // Generate QR Code
        generateQRCode(upiId);
    }

    private void generateQRCode(String upiId) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(upiId, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? getResources().getColor(android.R.color.black) : getResources().getColor(android.R.color.white));
                }
            }
            ivQrCode.setImageBitmap(bmp);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
