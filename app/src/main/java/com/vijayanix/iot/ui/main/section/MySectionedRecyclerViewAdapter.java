package com.vijayanix.iot.ui.main.section;


import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class MySectionedRecyclerViewAdapter<T extends MySection> extends SectionedRecyclerViewAdapter {


	public String addSection(T section) {

		section.setAdapter(this);

		return super.addSection(section);

	}

}