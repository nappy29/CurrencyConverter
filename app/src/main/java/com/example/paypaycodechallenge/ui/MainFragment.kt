package com.example.paypaycodechallenge.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.paypaycodechallenge.R
import com.example.paypaycodechallenge.R.layout.main_fragment
import com.example.paypaycodechallenge.data.model.Currency
import com.example.paypaycodechallenge.databinding.MainFragmentBinding
import com.example.paypaycodechallenge.ui.adapter.CurrencyAdapter
import dagger.hilt.android.AndroidEntryPoint
import androidx.recyclerview.widget.SimpleItemAnimator




@AndroidEntryPoint
class MainFragment: Fragment() {

    private lateinit var binding: MainFragmentBinding
    private val viewModel: MainViewModel by viewModels()
    var currencyList: MutableList<Currency> = ArrayList()
    var newCurrencyList: MutableList<Currency> = ArrayList()
    private lateinit var currencyAdapter: CurrencyAdapter
    private lateinit var currentCurrency: Currency


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, main_fragment, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currencyAdapter = CurrencyAdapter(requireContext())
        binding.recyclerView.adapter = currencyAdapter
        binding.recyclerView.setHasFixedSize(true)
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(p0: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {

                runWithSearchQuery(p0)
                return false
            }
        })

        binding.reloadBtn.setOnClickListener {
            binding.progressCircular.visibility = View.VISIBLE
            binding.reloadBtn.visibility = View.GONE
            viewModel.getRates()
        }

        observeData()

    }

    private fun runWithSearchQuery(p0: String?){

        newCurrencyList.clear()
        if(!p0.isNullOrEmpty()){

            var base_usd_val = (1.0/currentCurrency.value) * p0.toDouble()
            currencyList.forEach {
                var currency: Currency

                if(it.name != currentCurrency.name){
                    currency = Currency(0,it.name, it.value * base_usd_val)

                }else{
                    currency = Currency(0,it.name, p0.toDouble() )
                }

                newCurrencyList.add(currency)
            }
        }else{
            newCurrencyList.addAll(currencyList)
        }

        currencyAdapter.setDataList(newCurrencyList)
    }

    private fun observeData() {

        viewModel.localRespone.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()){
                binding.reloadBtn.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                binding.progressCircular.visibility = View.GONE

                currencyList.clear()
                currencyList.addAll(it)

                if(!::currentCurrency.isInitialized)
                    populateSpinner(currencyList)

                var searchText = binding.searchView.query.toString()

                if(!searchText.isNullOrEmpty()){
                    runWithSearchQuery(searchText)
                }else{
                    currencyAdapter.setDataList(currencyList)
                }
            }else{
                binding.reloadBtn.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                binding.progressCircular.visibility = View.GONE
                Toast.makeText(requireContext(), "No data, make sure you have an active internet connection", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun populateSpinner(currencyList: List<Currency>){
        binding.spinner?.adapter = activity?.applicationContext?.let { ArrayAdapter(it, R.layout.support_simple_spinner_dropdown_item, currencyList) }

        binding.spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val type = parent?.getItemAtPosition(position)
                currentCurrency = type as Currency

                var searchText = binding.searchView.query.toString()

                if(!searchText.isNullOrEmpty()){
                    runWithSearchQuery(searchText)
                }else{
                    currencyAdapter.setDataList(currencyList)
                }

            }
        }
    }

}