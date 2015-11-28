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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.redbooth.SlidingDeck;

public class MainActivity extends AppCompatActivity {
    private SlidingDeck slidingDeck;
    private SlidingDeckAdapter slidingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeSlidingDeck();
    }

    private void initializeSlidingDeck() {
        slidingAdapter = new SlidingDeckAdapter(this);
        slidingAdapter.add(new SlidingDeckModel("Txus Ballesteros", "http://lorempixel.com/100/100/nature/", "Attach and emoji keyboard is over content", "When emoji keyboard or attach panel is visible, it stay over all the content (for example chat messages). This happens in chat, task and conversations."));
        slidingAdapter.add(new SlidingDeckModel("Fran Sirvent", "http://lorempixel.com/100/100/sports/", "Add Deep Link to Chat Detail", "This task let to the Redbooth Calls application do the integration with the Redbooth App and allow the navigation to chat details."));
        slidingAdapter.add(new SlidingDeckModel("Joan Fuentes", "http://lorempixel.com/100/100/city/", "Make the release process automated", "Make the release process automated"));
        slidingAdapter.add(new SlidingDeckModel("Ruben Serrano", "http://lorempixel.com/100/100/food/", "Create a list of potential blog posts", "Let's be evil :smiling_imp: I've created a list. As I've added some proposals, I've changed my :hankey: emoji by the :+1: one, and I've passed the task to the next one. Do the same Joan Fuentes , until everyone has added at least one proposal :smiling_imp:"));
        slidingAdapter.add(new SlidingDeckModel("Bimal Ghundhu", "http://lorempixel.com/100/100/cats/", "Apply improvements to Notifications list activity", "Apply cache changes for task lists, task details to notifications"));
        slidingAdapter.add(new SlidingDeckModel("Ben Falk", "http://lorempixel.com/100/100/nightlife/", "Fix FilesDownloader bad naming in local repository", "Some technical debt we created in a PR. This is karma."));
        slidingAdapter.add(new SlidingDeckModel("Annie Du", "http://lorempixel.com/100/100/transport/", "Investigate whats happening when development crashes", "This is so we have further disucssion around dealing with collisions in the code and how we can test them with spoon or jenkins or something else."));
        slidingDeck = (SlidingDeck)findViewById(R.id.slidingDeck);
        slidingDeck.setAdapter(slidingAdapter);
        slidingDeck.setEmptyView(findViewById(R.id.emptyView));
        slidingDeck.setSwipeEventListener(new SlidingDeck.SwipeEventListener() {
            @Override
            public void onSwipe(SlidingDeck view) {
                SlidingDeckModel model = slidingAdapter.getItem(0);
                slidingAdapter.remove(slidingAdapter.getItem(0));
                slidingAdapter.insert(model, slidingAdapter.getCount());
                slidingAdapter.notifyDataSetChanged();
            }
        });
    }
}
