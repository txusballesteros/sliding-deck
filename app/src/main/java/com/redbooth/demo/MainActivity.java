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
        slidingAdapter.add(new SlidingDeckModel("Emilia Clarke",
                                                "http://www.hollywoodreporter.com/sites/default/files/custom/Kimberly/thr_emilia_clarke.jpg",
                                                "Emilia Clarke is an English actress. She is best known for her role as Daenerys Targaryen in the HBO series Game of Thrones, for which she received two Emmy Award nominations for Outstanding Supporting Actress in a Drama Series in 2013 and 2015."));
        slidingAdapter.add(new SlidingDeckModel("Kit Harington",
                                                "http://www.ew.com/sites/default/files/styles/tout_image_612x380/public/i/2013/09/24/kit-harington-jon-hamm_612x380_0.jpg",
                                                "Christopher Catesby Harington, better known as Kit Harington, is an English actor. He rose to fame playing the role of Jon Snow in the television series Game of Thrones."));
        slidingAdapter.add(new SlidingDeckModel("Peter Dinklage",
                                                "http://media1.popsugar-assets.com/files/2015/01/05/110/n/1922398/2ce86fc8b47262e1_thumb_temp_image881416727523.xxlarge.jpg",
                                                "Peter Hayden Dinklage is an American actor. Since his breakout role in The Station Agent, he has appeared in numerous films and voiced Ghost in the video game, Destiny."));
        slidingAdapter.add(new SlidingDeckModel("Lena Headey",
                                                "http://img2.wikia.nocookie.net/__cb20150623161517/disney/images/5/5d/Lena_headey_.jpg",
                                                "Lena Headey is an English actress. After being scouted at age 17, Headey worked steadily as an actress in small and supporting roles in films throughout the 1990s, before finding fame for her lead"));
        slidingAdapter.add(new SlidingDeckModel("Maisie Williams",
                                                "http://www.ew.com/sites/default/files/styles/tout_image_612x380/public/i/2015/06/16/maisie-williams.jpg?itok=6lBByS7D",
                                                "Maisie Williams is an English actress. She is known for her role as Arya Stark in the HBO television series Game of Thrones, which earned her the 2012 Portal Awards for Best Supporting Actress "));
        slidingAdapter.add(new SlidingDeckModel("Sophie Turner",
                                                "http://static.independent.co.uk/s3fs-public/thumbnails/image/2015/02/15/10/Sophie-Turner-v2.jpg",
                                                "Sophie Turner is an English actress. Turner is best known for her role as Sansa Stark on the HBO fantasy television series Game of Thrones, which earned her a Young Artist Award nomination for Best Supporting Young Actress in a TV Series. "));
        slidingAdapter.add(new SlidingDeckModel("Natalie Dormer",
                                                "http://media1.popsugar-assets.com/files/2014/11/19/077/n/1922398/17fe79cd7add5e92_thumb_temp_image10906271416444282/i/Natalie-Dormer-Interview-Mockingjay-Part-1.jpg",
                                                "Natalie Dormer is an English actress. She is best known for her roles as Anne Boleyn on the Showtime series The Tudors, as Margaery Tyrell on the HBO series Game of Thrones, Irene Adler and Jamie Moriarty"));
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
