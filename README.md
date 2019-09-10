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
| <img src="https://user-images.githubusercontent.com/1988156/64579200-fdfc6600-d3bc-11e9-8cab-84d64db38e92.png" width="300px" /> | <img src="https://user-images.githubusercontent.com/1988156/64579203-fe94fc80-d3bc-11e9-9f4a-abef0c1341c1.png" width="300px" />  | <img src="https://user-images.githubusercontent.com/1988156/64579204-fe94fc80-d3bc-11e9-9d68-d3310ba3f701.png" width="300px" />  | <img src="https://user-images.githubusercontent.com/1988156/64579202-fdfc6600-d3bc-11e9-8721-988788d725ed.png" width="400px" />|
</details>

## Features
- convert currency from one to another
- support decimal point
- long press on `'x'` will clear all input
- swap button to switch between base and target currency quickly
- auto currency rate update every 30 minutes
- pick from 168 currencies
- filter currency by currency name or code in currency picker

## Tech Explanation
### Programming Language
This project is written in Kotlin.

### Architecture
MVVM is used in this app. The view layer is made reactive... 👷‍♂️

## Adaptive Icon
Adaptive icon is created using Sketch. The sketch file can be found in `logo.sketch` file in the root of this project.

## Coding Style
`.editorconfig` is used in this project to make sure that the spacing and indentations are standardized, the `editorconfig` is obtained from [ktlint project](https://github.com/shyiko/ktlint/blob/master/.editorconfig).
