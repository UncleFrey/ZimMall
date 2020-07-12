package co.zw.sekai.zimmall.AdminActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import co.zw.sekai.zimmall.ActivityHome;
import co.zw.sekai.zimmall.ActivityWelcome;
import co.zw.sekai.zimmall.R;

public class ActivityAdminCategory extends AppCompatActivity implements View.OnClickListener{
    //Dec Views
    private ImageView imgPhone, imgComputer, imgPhoto
            ,imgMen, imgWomen, imgAccesory
            ,imgFurniture, imgKitchen, imgAppliances;

    private Button btnManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);
        //Bind Views
        imgPhone = (ImageView)findViewById(R.id.imageViewPhones);
        imgComputer = (ImageView)findViewById(R.id.imageViewComputer);
        imgPhoto  = (ImageView)findViewById(R.id.imageViewPhoto);
        imgMen = (ImageView)findViewById(R.id.imageViewMen);
        imgWomen = (ImageView)findViewById(R.id.imageViewWomen);
        imgAccesory = (ImageView)findViewById(R.id.imageViewAccessories);
        imgFurniture = (ImageView)findViewById(R.id.imageViewFurniture);
        imgKitchen = (ImageView)findViewById(R.id.imageViewKitchenWare);
        imgAppliances = (ImageView)findViewById(R.id.imageViewAppliances);
        btnManage = (Button)findViewById(R.id.buttonManage);

        //OnClick
        imgPhone.setOnClickListener(this);
        imgComputer.setOnClickListener(this);
        imgPhoto.setOnClickListener(this);
        imgMen.setOnClickListener(this);
        imgWomen.setOnClickListener(this);
        imgAccesory.setOnClickListener(this);
        imgFurniture.setOnClickListener(this);
        imgKitchen.setOnClickListener(this);
        imgAppliances.setOnClickListener(this);
        btnManage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == imgPhone){
            Intent intent = new Intent(ActivityAdminCategory.this, ActivityAdminNewProduct.class);
            intent.putExtra("category", "Phones and Accessories");
            intent.putExtra("heading", "Phones & Accesories");
            startActivity(intent);
        }

        if (v == imgComputer){
            Intent intent = new Intent(ActivityAdminCategory.this, ActivityAdminNewProduct.class);
            intent.putExtra("category", "Computers and Sound");
            intent.putExtra("heading", "Computers & Sound");
            startActivity(intent);
        }

        if (v == imgPhoto){
            Intent intent = new Intent(ActivityAdminCategory.this, ActivityAdminNewProduct.class);
            intent.putExtra("category", "Photos and Media");
            intent.putExtra("heading", "Photos & Media");
            startActivity(intent);
        }

        if (v == imgMen){
            Intent intent = new Intent(ActivityAdminCategory.this, ActivityAdminNewProduct.class);
            intent.putExtra("category", "Men Fashion");
            intent.putExtra("heading", "Men's Fashion");
            startActivity(intent);
        }

        if (v == imgWomen){
            Intent intent = new Intent(ActivityAdminCategory.this, ActivityAdminNewProduct.class);
            intent.putExtra("category", "Women Fashion");
            intent.putExtra("heading", "Women's Fashion");
            startActivity(intent);
        }

        if (v == imgAccesory){
            Intent intent = new Intent(ActivityAdminCategory.this, ActivityAdminNewProduct.class);
            intent.putExtra("category", "Accessories and Beauty");
            intent.putExtra("heading", "Accessories & Beauty");
            startActivity(intent);
        }

        if (v == imgFurniture){
            Intent intent = new Intent(ActivityAdminCategory.this, ActivityAdminNewProduct.class);
            intent.putExtra("category", "Furniture and Dressing");
            intent.putExtra("heading", "Furniture & Dressing");
            startActivity(intent);
        }

        if (v == imgKitchen){
            Intent intent = new Intent(ActivityAdminCategory.this, ActivityAdminNewProduct.class);
            intent.putExtra("category", "Kitchenware");
            intent.putExtra("heading", "KitchenWare");
            startActivity(intent);
        }

        if (v == imgAppliances){
            Intent intent = new Intent(ActivityAdminCategory.this, ActivityAdminNewProduct.class);
            intent.putExtra("category", "Appliances and Tools");
            intent.putExtra("heading", "Appliances & Tools");
            startActivity(intent);
        }

        if (v == btnManage){
            Intent intent  = new Intent(ActivityAdminCategory.this, ActivityHome.class);
            intent.putExtra("type", "Admin");
            startActivity(intent);
        }

    }
}
