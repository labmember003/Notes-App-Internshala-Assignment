package com.falcon.notesapp

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.falcon.notesapp.adapters.NoteAdapter
import com.falcon.notesapp.dao.NoteDatabase
import com.falcon.notesapp.dao.NoteEntity
import com.falcon.notesapp.databinding.FragmentMainBinding
import com.falcon.notesapp.models.NoteResponse
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    @Inject
    lateinit var noteDatabase: NoteDatabase

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: NoteAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        adapter = NoteAdapter(::onNoteClicked)
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    isEnabled = false
                    requireActivity().finish()
                }
            }
            )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        isNetworkAvailable(requireContext())
        TODO("NAVGRAPH MEI FIRST FRAGMENT KO RENAME KRKE WALKTHROUGH FRAGMENT KR DIYO KL")
        binding.notesList.adapter = adapter
        binding.notesList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.addNote.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_noteFragment)
        }
        displayData()
    }

    private fun displayData() {
        noteDatabase.noteDao().getNotes().observe(viewLifecycleOwner) {
            Log.i("testtesttest", it.size.toString())
            if (it.isEmpty()) {
                binding.listEmptyAnimation.isVisible = true
                binding.nothingToDisplayText.isVisible = true
            } else {
                binding.listEmptyAnimation.isVisible = false
                binding.nothingToDisplayText.isVisible = false
            }
            val convertedList = mapNoteEntityListToNoteResponseList(it)
            adapter.submitList(convertedList)
        }
    }

    private fun mapNoteEntityListToNoteResponseList(noteEntityList: List<NoteEntity>?): MutableList<NoteResponse> {
        val list: MutableList<NoteResponse> = emptyList<NoteResponse>().toMutableList()
        noteEntityList?.forEach {
            val noteResponse = NoteResponse(it.__v, it._id, it.createdAt, it.description, it.title, it.updatedAt, it.userId)
            list.add(noteResponse)
        }
        return list
    }

    private fun noteEntityToNoteResponse(noteEntity: NoteEntity): NoteResponse {
        return NoteResponse(noteEntity.__v, noteEntity._id, noteEntity.createdAt, noteEntity.description, noteEntity.title, noteEntity.updatedAt, noteEntity.userId)
    }

    private fun onNoteClicked(noteResponse: NoteResponse) {
        val bundle = Bundle()
        bundle.putString("note", Gson().toJson(noteResponse))
        findNavController().navigate(R.id.action_mainFragment_to_noteFragment, bundle)
    }

    private suspend fun synchronizeListData(data: List<NoteResponse>?) {
        data?.forEach {
            Log.i("nimbumirchdhnaiya", noteDatabase.noteDao().checkEntryExists(it._id).toString())
            Log.i("asdfghjkl", checkNoteExistenceInDB(it).toString())
            if (!checkNoteExistenceInDB(it)) {
                val note = NoteEntity(it.__v, it._id, it.createdAt, it.description, it.title, it.updatedAt, it.userId,
                    isSynced = true,
                    isDeleted = false
                )
                noteDatabase.noteDao().insertNote(note)
            }
        }
    }

    private fun checkNoteExistenceInDB(note: NoteResponse): Boolean {
        if (noteDatabase.noteDao().checkEntryExists(note._id) == 0) {
            return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showSnackBar(message: String?, activity: Activity?) {
        if (null != activity && null != message) {
            Snackbar.make(
                activity.findViewById(android.R.id.content),
                message, Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}