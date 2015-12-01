SlidingDeck View
================

SlidingDeck View for Android is a complex deck of view, easy to deploy and lightweight. For more info, take a look to the demo App to see all the capabilities and behaviours of the component.  

![](assets/demo.gif)

## Latest Version

[ ![Download](https://api.bintray.com/packages/txusballesteros/maven/SlidingDeck/images/download.svg) ](https://bintray.com/txusballesteros/maven/SlidingDeck/_latestVersion) ![](https://img.shields.io/badge/platform-android-green.svg) ![](https://img.shields.io/badge/Min%20SDK-14-green.svg) ![](https://img.shields.io/badge/Licence-Apache%20v2-green.svg)

## How to use

### 1.- Configuring your project dependencies

Add the library dependency to your build.gradle file.

```groovy
dependencies {
    ...
    compile 'com.redbooth:SlidingDeck:1.0.0'
}
```

### 2.- Adding and Customizing the View

Add the view to your xml layout file.

```xml
<com.redbooth.SlidingDeck
        android:id="@+id/slidingDeck"
        android:layout_width="match_parent"
        android:layout_height="wrap_content' />
```

### 3.- Setting the View Adapter

Set the data adapter to the view.

```java
    final SlidingDeck slidingDeck = (SlidingDeck)findViewById(R.id.slidingDeck);
    slidingDeck.setAdapter(slidingAdapter);
```

## Motivation of the Project

I created this view as a little piece of the [Redbooth](https://redbooth.com/) App for Android.

## License

Copyright Txus Ballesteros 2015 (@txusballesteros)

This file is part of some open source application.

Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

Contact: Txus Ballesteros <txus.ballesteros@gmail.com>