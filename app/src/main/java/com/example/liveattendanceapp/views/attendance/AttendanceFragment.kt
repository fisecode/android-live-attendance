package com.example.liveattendanceapp.views.attendance

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.example.liveattendanceapp.R
import com.example.liveattendanceapp.databinding.FragmentAttendanceBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.example.liveattendanceapp.dialog.MyDialog
import java.lang.StringBuilder


class AttendanceFragment : Fragment(), OnMapReadyCallback {

    companion object{
        private const val REQUEST_CODE_MAP_PERMISSIONS = 1000
    }

    private val mapPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private var mapAttendance: SupportMapFragment? = null
    private var map: GoogleMap? = null
    private var binding: FragmentAttendanceBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAttendanceBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMaps()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_CODE_MAP_PERMISSIONS -> {
                var isHasPermissions = false
                val permissionNotGranted = StringBuilder()

                for (i in permissions.indices){
                    isHasPermissions = grantResults[i] == PackageManager.PERMISSION_GRANTED

                    if (!isHasPermissions){
                        permissionNotGranted.append("${permissions[i]}\n")
                    }
                }
                if (isHasPermissions){
                    setupMaps()
                }else{
                    val message = permissionNotGranted.toString() + "\n" + getString(R.string.not_granted)
                    MyDialog.dynamicDialog(context, getString(R.string.required_permission), message)
                }
            }
        }
    }

    private fun setupMaps() {
        mapAttendance = childFragmentManager.findFragmentById(R.id.map_attendance) as SupportMapFragment
        mapAttendance?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if (checkPermission()){
            val sydney = LatLng(-6.289085, 106.757644)
            map?.moveCamera(CameraUpdateFactory.newLatLng(sydney))
            map?.animateCamera(CameraUpdateFactory.zoomTo(20f))

            goToCurrentLocation()
        }else{
            setRequestPermission()
        }

    }

    private fun setRequestPermission() {
        requestPermissions(mapPermissions, REQUEST_CODE_MAP_PERMISSIONS)
    }

    private fun goToCurrentLocation() {

    }

    private fun checkPermission(): Boolean {
        var isHasPermission = false
        context?.let{
            for (permission in mapPermissions){
                isHasPermission = ActivityCompat.checkSelfPermission(it, permission) == PackageManager.PERMISSION_GRANTED
            }
        }
        return  isHasPermission
    }
}