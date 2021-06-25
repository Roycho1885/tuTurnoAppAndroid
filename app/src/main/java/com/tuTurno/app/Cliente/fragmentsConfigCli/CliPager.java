package com.tuTurno.app.Cliente.fragmentsConfigCli;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.tuTurno.app.SlidePagerAdapter;
import com.tuTurno.app.R;

public class CliPager extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.clipager, container, false);

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
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_baseline_attach_money_24));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_baseline_notifications_24));

        // add your fragments
        adapter.addFrag(new CliDatosPersonales());
        adapter.addFrag(new CliVerPago());
        adapter.addFrag(new CliNotificaciones());


        // set adapter on viewpager
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

    }
}
