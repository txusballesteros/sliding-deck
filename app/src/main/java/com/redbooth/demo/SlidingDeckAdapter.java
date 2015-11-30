/*
 * Copyright Txus Ballesteros 2015 (@txusballesteros)
 *
 * This file is part of some open source application.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Contact: Txus Ballesteros <txus.ballesteros@gmail.com>
 */
package com.redbooth.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.redbooth.SlidingDeck;
import com.squareup.picasso.Picasso;

public class SlidingDeckAdapter extends ArrayAdapter<SlidingDeckModel> {
    public SlidingDeckAdapter(Context context) {
        super(context, R.layout.sliding_item);
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sliding_item, parent, false);
        }
        SlidingDeckModel item = getItem(position);
        view.setTag(item);
        ((TextView)view.findViewById(R.id.description)).setText(item.getDescription());
        ((TextView)view.findViewById(R.id.name)).setText(item.getName());
        ImageView avatar = (ImageView)view.findViewById(R.id.avatar);
        Picasso.with(parent.getContext())
                .load(item.getAvatarUri())
                .placeholder(R.drawable.ic_launcher_48dp)
                .transform(new RoundedTransform())
                .into(avatar);
        final View completeView = view.findViewById(R.id.completeCommand);
        completeView.setTag(view);
        completeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SlidingDeck slidingDeck = (SlidingDeck)parent;
                slidingDeck.swipeItem((View)view.getTag(), new SlidingDeck.SwipeEventListener() {
                    @Override
                    public void onSwipe(SlidingDeck parent, View item) {
                        final SlidingDeckModel slidingDeckModel = (SlidingDeckModel)item.getTag();
                        remove(slidingDeckModel);
                        notifyDataSetChanged();
                    }
                });
            }
        });
        return view;
    }
}
