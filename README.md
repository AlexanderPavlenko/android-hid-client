<div id="top"></div>

<!-- PROJECT SHIELDS -->
[![GPL-3.0 License][license-shield]][license-url]

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/AlexanderPavlenko/android-hid-client">
    <img src="images/app-icon-round.png" alt="App Icon" width="80" height="80">
  </a>

<h3 align="center">SSH HID Client</h3>
  <p align="center">
    Android app that allows you to use your phone as a wireless keyboard <br/>
    (adapter not included)
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->

## About The Project

This Android app allows your phone to connect via SSH to a gadget which presents itself as a keyboard.
Since this happens at a very low level, the connected device sees it exactly as a normal keyboard, meaning
you don't need to install any software on the connected computer! For this reason, it will even work if the
connected device is in its BIOS/UEFI. Even multimedia keys are supported, meaning you can send volume up/down
keys to the connected computer to control media.

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- GETTING STARTED -->

## Getting Started

### Prerequisites

* Any device running Android
* SSH access to a Linux device acting as an [USB HID gadget](https://github.com/AlexanderPavlenko/pi-keyboard)
* Soft Keyboard, for example [Unexpected Keyboard](https://github.com/Julow/Unexpected-Keyboard)

### Installation (Binary)<a name="installation-binary"> </a>

Obtain via [Obtanium](https://github.com/ImranR98/Obtainium?tab=readme-ov-file#installation)!

Deep link which GitHub refuses to render as clickable:
```
obtainium://add/https://github.com/AlexanderPavlenko/android-hid-client
```

Verification info:
- Package ID: `top.flvr.ssh_hid_client`
- SHA-256 of the signing certificate: `A9:38:D4:A9:02:5A:C1:4A:7B:89:CC:FF:7A:EB:A3:9D:C3:21:43:84:5D:C9:7B:68:4E:A4:01:92:81:2A:46:BC`

### Installation (Source)<a name="installation-source"></a>

Clone the repository.

   ```sh
   git clone --depth 1 https://github.com/AlexanderPavlenko/android-hid-client.git
   ```

import into [Android Studio](https://developer.android.com/studio), build the APK, then install it
on your Android device.

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- USAGE EXAMPLES -->

## Usage

To relay keys in real-time as soon as you press them, click on the keyboard icon in the menu bar. It should pull up your
keyboard. Now you can just start typing!

Typing in the "Manual Input" text box will send all the characters that you've typed into the box to
the connected device once you hit the "send" button.

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- ROADMAP -->

## Roadmap

- [X] Keyboard support
- [ ] Add some special key buttons to the UI (like multimedia keys)
- [X] Touchpad ~~support~~ removed
- [X] Ability to send string all at once
- [X] Add full settings page

See the [open issues][issues-url] for a full list of proposed features (and known issues).

<p align="right">(<a href="#top">back to top</a>)</p>


<!-- LICENSE -->

## License

Distributed under the GNU GPLv3 License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- ACKNOWLEDGMENTS -->

## Acknowledgments

* Original [USB HID Client](https://github.com/Arian04/android-hid-client)

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[contributors-shield]: https://img.shields.io/github/contributors/AlexanderPavlenko/android-hid-client.svg?style=for-the-badge

[contributors-url]: https://github.com/AlexanderPavlenko/android-hid-client/graphs/contributors

[forks-shield]: https://img.shields.io/github/forks/AlexanderPavlenko/android-hid-client.svg?style=for-the-badge

[forks-url]: https://github.com/AlexanderPavlenko/android-hid-client/network/members

[stars-shield]: https://img.shields.io/github/stars/AlexanderPavlenko/android-hid-client.svg?style=for-the-badge

[stars-url]: https://github.com/AlexanderPavlenko/android-hid-client/stargazers

[issues-shield]: https://img.shields.io/github/issues/AlexanderPavlenko/android-hid-client.svg?style=for-the-badge

[issues-url]: https://github.com/AlexanderPavlenko/android-hid-client/issues

[license-shield]: https://img.shields.io/github/license/AlexanderPavlenko/android-hid-client.svg?style=for-the-badge

[license-url]: https://github.com/AlexanderPavlenko/android-hid-client/blob/master/LICENSE.txt
