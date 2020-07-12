package co.zw.sekai.zimmall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import co.zw.sekai.zimmall.HomeFragments.FragmentSettings;
import co.zw.sekai.zimmall.Models.Product;
import co.zw.sekai.zimmall.Models.UserWallet;
import co.zw.sekai.zimmall.Models.Users;
import co.zw.sekai.zimmall.Prevalent.Prevalent;
import co.zw.sekai.zimmall.ViewHolder.ProductViewHolder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class ActivityHome extends AppCompatActivity implements View.OnClickListener{

    //TODO Drawer
    DrawerLayout drawerLayout;
    ImageView imgMenu;
    TextView txtTitle, txtBalance;
    NavigationView navigationView;
    NavController navController;

    String userType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        if (bundle != null){
            userType = getIntent().getStringExtra("type");
        }else {
            userType = "User";
        }

        //TODO Nav Controller
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        imgMenu = (ImageView)findViewById(R.id.imageMenu);
        txtTitle = (TextView)findViewById(R.id.textViewTitle);
        navigationView = (NavigationView)findViewById(R.id.navigationView);
        imgMenu.setOnClickListener(this);
        navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                txtTitle.setText(destination.getLabel());
            }
        });

        //TODO Get UserData
        View headerView = navigationView.getHeaderView(0);
        TextView txtUsername = headerView.findViewById(R.id.textViewUsername);
        TextView txtUserphone = headerView.findViewById(R.id.textViewPhone);
        CircleImageView imgProfile = headerView.findViewById(R.id.imageProfile);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (userType.equals("Admin")){
                    Toast.makeText(ActivityHome.this, "Login As Regular User", Toast.LENGTH_SHORT).show();
                }else if (menuItem.getTitle().equals("LogOut") && userType == "User"){
                    Paper.book().destroy();
                    Intent intent = new Intent(ActivityHome.this, ActivityLogin.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }else{
                    navController.navigate(menuItem.getItemId());
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                return false;
            }
        });

        txtBalance = findViewById(R.id.textViewBalance);

        if (!userType.equals("Admin")){
            //Set Text
            txtUsername.setText(Prevalent.currentOnlineUser.getName());
            txtUserphone.setText(Prevalent.currentOnlineUser.getPhone());
            Picasso.get().load(Prevalent.currentOnlineUser.getImageUrl()).placeholder(R.drawable.profile_icon).into(imgProfile);

            //Balance
            final DatabaseReference balRef = FirebaseDatabase.getInstance().getReference()
                    .child("User Wallets").child(Prevalent.currentOnlineUser.getPhone());

            balRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserWallet userWallet  = dataSnapshot.getValue(UserWallet.class);
                    final String balance = String.format("%.2f", Float.parseFloat(userWallet.getBalance()));
                    txtBalance.setText("US$ " + balance);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

    @Override
    public void onClick(View v) {
        if (v == imgMenu){
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}
