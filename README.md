SlidingDeck View
================

Sliding-deck view for Android offer an intuitive and useful user interface widget. If you like to have a deck of views and don't want complicate your code. Here you have the solution. Lightweight and easy to deploy, Sliding-deck brings full feature widget. Your users will can perform swipe, quick review or stick the elements. 

If you want learn more about the library, you should take a look to the demo App and see all the capabilities and behaviours of the component.  

![](assets/demo.gif)

## Latest Version

[ ![Download](https://api.bintray.com/packages/txusballesteros/maven/SlidingDeck/images/download.svg) ](https://bintray.com/txusballesteros/maven/SlidingDeck/_latestVersion) ![](https://img.shields.io/badge/platform-android-green.svg) ![](https://img.shields.io/badge/Min%20SDK-14-green.svg) ![](https://img.shields.io/badge/Licence-Apache%20v2-green.svg)

## Features

### Gestures

* **Pull Down** to apply a vertical offset to the element, this action is perfect to allow a Quick Review action to the users.
* **Swipe Left to Right** to move the element horizontally, you can detect this action to create you self logic.
* **Swipe Top to Bottom** if you want to show all the visible items and keep theses sticked, and perform a **Swipe Top to Bottom** to pick all the items.

### Overdraw Improvements

If you're a hard Android developer and like that your App work fast and fine, you can enable the overdraw improvements proccess into the view. You only need enable the flag **enableOverdrawImprovement** on you xml layout file.

### Customization

If you want to change the default look and feel of the view, you can edit two attributes of the view, see below.

* **itemsMarginLeftRight**, if you want to change the corners padding of the deck elements.
* **itemsMarginTop**, if you want to change the vertical spacing between the deck elements.
 
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

I created this view as a little piece of the [Redbooth](https://redbooth.com/) App for [Android](https://play.google.com/store/apps/details?id=com.redbooth).

## License

Copyright Txus Ballesteros 2016 (@txusballesteros)

This file is part of some open source application.

Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

Contact: Txus Ballesteros txus.ballesteros@gmail.com