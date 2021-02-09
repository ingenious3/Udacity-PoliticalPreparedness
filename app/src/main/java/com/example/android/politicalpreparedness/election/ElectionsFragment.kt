package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter.ElectionListener

class ElectionsFragment: Fragment() {

    private lateinit var viewModel: ElectionsViewModel
    private lateinit var viewModelFactory: ElectionsViewModelFactory
    private lateinit var binding: FragmentElectionBinding

    private lateinit var upcomingElectionsAdapter: ElectionListAdapter
    private lateinit var savedElectionsAdapter: ElectionListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_election, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelFactory = ElectionsViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(ElectionsViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.getSavedElections()
        viewModel.refreshElectionsList()
        viewModel.getUpcomingElections()

        upcomingElectionsAdapter = ElectionListAdapter(ElectionListener {
            findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it)
            )
        })
        binding.upcomingElectionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.upcomingElectionsRecyclerView.adapter = upcomingElectionsAdapter

        savedElectionsAdapter = ElectionListAdapter(ElectionListener {
            findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it))
        })
        binding.savedElectionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.savedElectionsRecyclerView.adapter = savedElectionsAdapter


        viewModel.upcomingElections.observe(viewLifecycleOwner, { upcomingElections ->
            if (upcomingElections != null) {
                upcomingElectionsAdapter.submitList(upcomingElections)
            }
        })

        viewModel.savedElections.observe(viewLifecycleOwner, { elections ->
            if (elections != null) {
                savedElectionsAdapter.submitList(elections)
            }
        })

        viewModel.navigateToVoterInfo.observe(viewLifecycleOwner, { election ->
            if (election != null) {
                findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election))
                viewModel.onElectionSelected()
            }
        })
    }
}