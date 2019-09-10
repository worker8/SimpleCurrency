# SimpleCurrency

<img src="https://github.com/worker8/SimpleCurrency/blob/master/app/src/main/ic_logo-web.png?raw=true" width="200px"/>

Simple Currency is an application that handles currency conversion from 168 countries. 

## Demo Gif
Here's a quick demo of how it works:

<details>
<summary>
click to show demo gif
</summary>

<img src="https://user-images.githubusercontent.com/1988156/64579051-6dbe2100-d3bc-11e9-9dac-49f067dc4674.gif" width="300px"/>
</details>

## Demo Screenshots
Here are some screenshots:

<details>
<summary>
click to show screenshots
</summary>

| HomeScreen | CurrencyScreen 1  | CurrencyScreen 2 | Landscape |
| - | - | - | - |
| <img src="https://user-images.githubusercontent.com/1988156/64579200-fdfc6600-d3bc-11e9-8cab-84d64db38e92.png" width="300px" /> | <img src="https://user-images.githubusercontent.com/1988156/64591574-1b452a80-d3e5-11e9-8f81-fa4cbff5aece.png" width="300px" />  | <img src="https://user-images.githubusercontent.com/1988156/64591575-1b452a80-d3e5-11e9-9140-c546466f4f47.png" width="300px" />  | <img src="https://user-images.githubusercontent.com/1988156/64579202-fdfc6600-d3bc-11e9-8721-988788d725ed.png" width="400px" />|
</details>

## Features
- convert currency from one to another
- support decimal point
- long press on `'x'` will clear all input
- swap button to switch between the base and target currency quickly
- automatic currency rate updates every 30 minutes
- pick from 168 currencies
- filter currency by currency name or code in currency picker
- comma seperation for big numbers

## How to Setup
The API used for obtaining the latest currency data is [CurrencyLayer.com](https://currencylayer.com). A free account can be made easily and it will provide an API key. You need to fill up the API key in `api_keys.properties` file at the root of this project. Instructions:

1. make a file named `api_keys.properties` in the root of the project
2. add this line in the file: `CURRENCY_LAYER_ACCESS_TOKEN=<fill in API key obtained from Currency Layer>`

This is how the file looks like:

```shell
༼つ◕_◕༽つ RootOfSimpleCurrency (master)$ cat api_keys.properties
CURRENCY_LAYER_ACCESS_TOKEN=d0bbf06c7xxxxxxxxxxxx47d2e56ed6f

```

## Tech Explanation
The following dependencies are used in this project:
- RxJava
- RxBinding
- Dagger2
- Retrofit
- Moshi
- Room
- Android Arch Lifecycle
- Android Arch ViewModel 
- WorkManager
- Material Design Library
- Mockk
- JUnit
- etc..

### Programming Language
This project is written in Kotlin.

### Architecture
MVVM is used in this app. The view layer is made reactive and passed into the `ViewModel` as the input. External sources that are needed (such as database access, network calls, shared preference access) will be passed into `ViewModel` as `Repo`. This way `ViewModel` doesn't have access to Android-related code so that it can be unit tested.

Room Persistence library is used to access SQLite easily. This is used to store the currency data. `USD` is used as the base currency, so all the currency stored in the database is referenced against `USD`. Let's say we wanted to find out `Japanese Yen (JPY)` vs. `Pound Sterling (GBP)`, simple math calculation will be done.

In every periodic interval (currently set at every 30 minutes), the `WorkManager` will fire up a Retrofit call to get the latest currency rate. The obtained json will be deserialized by Moshi and write into the database.

### Unidirectional data flow & Immutable data
The project follows the unidirectional data flow rule to better structure the code.

Here's a brief diagram of the main activity architecture.
<img src="https://user-images.githubusercontent.com/1988156/64588332-18ded280-d3dd-11e9-9fc5-75947217c329.png" width="600px"/>

1. RxView - The flow begins from the views. Every user view interactions are reactive and fed into the `ViewModel`.
2. Repo - Any external data access that is not from the user interactions, such as network calls, shared preference, database access, etc... will all go through the `Repo` class that is passed into `ViewModel`.
3. ScreenState - The reactive signals from `Views` will be processed inside `ViewModel` by some business logic. After that, it will produce the next `ScreenState` by using `.copy()` from Kotlin to ensure immutability.
4. View Update - Finally, all the views will listen to the `ScreenState` and update itself accordingly.

This architecture is quite similar to [MVI](https://www.raywenderlich.com/817602-mvi-architecture-for-android-tutorial-getting-started). The difference is that it doesn't use reducer or model the input as 'intent'.


### Unit Test
`ViewModel` doesn't have access to Android related code. Therefore, it can be tested by JUnit `test`, without using `androidTest`. Example Unit Test can be found [MainViewModelTest.kt](([MainViewModelTest.kt](https://github.com/worker8/SimpleCurrency/blob/master/app/src/test/java/com/worker8/simplecurrency/MainViewModelTest.kt))).

The general idea of testing an Activity can be described by this diagram:

<img src="https://user-images.githubusercontent.com/1988156/64589559-57c25780-d3e0-11e9-9726-95c5b654a4ca.png" width="600px"/>

The `RxViews` signals are replaced by fake inputs from the test. Reactive RxBinding signals are be replaced by RxJava's `Subjects` , reactive database access `Flowable` are replaced by RxJava's `Processors` and normal method calls are mocked by `mockk`. This way, user interactions can be controlled by us. After that, we can make `Assertion` on the `ScreenState` to check if it behaves correctly.

Here's an example of testing a simple conversion ([MainViewModelTest.kt#L112](https://github.com/worker8/SimpleCurrency/blob/4f320e6d9cec77d78849f70d1f1ad1fa9b2dbdd8/app/src/test/java/com/worker8/simplecurrency/MainViewModelTest.kt#L112)):

```kotlin
    fun testSimpleConversion() {
        // 1. arrange
        val fakeRate = 2.0
        viewModel.onCreate()
        val screenStateTestObserver = viewModel.screenState.test()

        // 2. act
        populateDbIfFirstTime.onNext(true)
        getLatestSelectedRateFlowable.offer(fakeRate)

        onNumpad1Click.onNext('1')
        onNumpad0Click.onNext('0')
        onNumpad0Click.onNext('0')

        // 3. assert
        verify(exactly = 1) { repo.setupPeriodicUpdate() }
        screenStateTestObserver.assertNoErrors()

        screenStateTestObserver.lastValue.apply {
            Assert.assertEquals("200", outputNumberString)
        }
    }
```

#### Description
1. Arrange - we first setup the necessary objects

2. Act - next, we make some actions.

While we run the following:

```kotlin
        // 2. act
        populateDbIfFirstTime.onNext(true)
        getLatestSelectedRateFlowable.offer(fakeRate)

        onNumpad1Click.onNext('1')
        onNumpad0Click.onNext('0')
        onNumpad0Click.onNext('0')
```

It is actually doing these:

1. `populateDbIfFirstTime.onNext(true)` - seeded the db
2. `getLatestSelectedRateFlowable.offer(fakeRate)` - taken the latest conversion rate from db
3. `onNumpad1Click.onNext('1')`, `onNumpad1Click.onNext('0')`, `onNumpad1Click.onNext('0')` - click on `1`, `0`, `0` (100) 

3. Assert - finally, we check on the output:
```kotlin
        screenStateTestObserver.lastValue.apply {
            Assert.assertEquals("200", outputNumberString)
        }
```
Since the fake conversaion rate is set to `2.0`, the output should be `200` when input is `100`.

## Adaptive Icon
An adaptive icon is created using Sketch. The sketch file can be found in [`logo.sketch`](https://github.com/worker8/SimpleCurrency/blob/master/logo.sketch) file at the root of this project.

## Coding Style
`.editorconfig` is used in this project to make sure that the spacing and indentations are standardized, the `editorconfig` is obtained from [ktlint project](https://github.com/shyiko/ktlint/blob/master/.editorconfig).
