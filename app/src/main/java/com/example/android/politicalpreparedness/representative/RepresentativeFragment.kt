package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.util.*

class RepresentativeFragment : Fragment() {

    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var viewModel: RepresentativeViewModel
    private lateinit var viewModelFactory: RepresentativeViewModelFactory
    private lateinit var representativeAdapter: RepresentativeListAdapter

    companion object {
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100
        const val KEY_ADDRESS = "key_address"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_representative, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelFactory = RepresentativeViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(requireActivity(),viewModelFactory).get(RepresentativeViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        if (savedInstanceState != null) {
            binding.address = savedInstanceState.getParcelable(KEY_ADDRESS)
        } else {
            binding.address = Address("", "", "", "California", "")
        }
        viewModel.setAddress(binding.address)

        val states = resources.getStringArray(R.array.states)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, states)
        binding.state.adapter = adapter

        binding.buttonLocation.setOnClickListener {
            checkLocationPermissions(it)
        }

        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            viewModel.setAddress(binding.address)
            viewModel.getRepresentatives()
        }

        representativeAdapter = RepresentativeListAdapter()
        binding.representativesRecyclerView.adapter = representativeAdapter

        viewModel.representatives.observe(viewLifecycleOwner, { representatives ->
            representativeAdapter.submitList(representatives)
        })


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                Snackbar.make(requireView(), R.string.location_permission_denied, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun checkLocationPermissions(view: View): Boolean {
        return if (isLocationPermissionGranted()) {
            getLocation()
            true
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
            false
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener { location:Location? ->
            if(location != null) {
                val address = geoCodeLocation(location)
                if(address != null) {
                    binding.address = address
                    viewModel.setAddress(address)
                    val states = resources.getStringArray(R.array.states)
                    val selectedStateIndex = states.indexOf(address?.state)
                    binding.state.setSelection(selectedStateIndex)
                    viewModel.getRepresentatives()
                }
            }
        }.addOnFailureListener { e -> e.printStackTrace() }
    }

    private fun geoCodeLocation(location: Location): Address? {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
        return addresses.map { address ->
                        Address(address.thoroughfare ?: "", address.subThoroughfare ?: "", address.locality ?: "", address.adminArea ?: "", address.postalCode ?: "")
                    }.first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_ADDRESS, binding.address)
    }
}