package com.tuTurno.app.Admin.fragmentsConfig;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuTurno.app.SlidePagerAdapter;
import com.tuTurno.app.R;

import java.util.Objects;

import models.cliente;

public class AdminPager extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.adminpager, container, false);


        new SlidePagerAdapter(getChildFragmentManager());

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager viewPager = view.findViewById(R.id.viewpager);
        TabLayout tabLayout = view.findViewById(R.id.tablayout);

        //tabLayout.setupWithViewPager(viewPager);

        SlidePagerAdapter adapter = new SlidePagerAdapter(getChildFragmentManager());
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_perm_identity_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_baseline_lock_24));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_baseline_check_24));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_baseline_close_24));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_baseline_attach_money_24));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_baseline_monetization_on_24));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_baseline_local_atm_24));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_baseline_account_balance_wallet_24));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_baseline_notifications_24));


        // add your fragments
        adapter.addFrag(new AdminDatosClientes());
        adapter.addFrag(new AdminCodigo());
        adapter.addFrag(new AdminAgregarTur());
        adapter.addFrag(new AdminElimTur());
        adapter.addFrag(new AdminCuotas());
        adapter.addFrag(new AdminVerCuotas());
        adapter.addFrag(new AdminModificarCuota());
        adapter.addFrag(new AdminIngresosyGastos());
        adapter.addFrag(new AdminNotificaciones());

        // set adapter on viewpager
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }
}
